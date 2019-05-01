package edu.baylor;

import edu.baylor.schema.Labels;
import edu.baylor.schema.RelationshipTypes;
import org.neo4j.graphdb.*;
import org.neo4j.graphdb.traversal.*;
import org.neo4j.logging.Log;
import org.roaringbitmap.longlong.Roaring64NavigableMap;

import java.util.*;

import static edu.baylor.schema.Properties.TIME;

public class CCRunnable implements Runnable {

    private static final int TRANSACTION_LIMIT = 1000;
    private GraphDatabaseService db;
    private Log log;
    private Long time;
    private Long interval;
    private Long finalEndTime;
    private ArrayList<String> stringsToPrint;

    public CCRunnable(GraphDatabaseService db, Log log, Long time, Long interval, Long finalEndTime, ArrayList<String> stringsToPrint)  {
        this.db = db;
        this.log = log;
        this.time = time;
        this.interval = interval;
        this.finalEndTime = finalEndTime;
        this.stringsToPrint = stringsToPrint;
    }

    @Override
    public void run() {
        int ccID = 1;
        String ccProperty = "ccId";
        // Integer division
        int intervals = 1 + (int)((finalEndTime - time) / interval);

        Roaring64NavigableMap nextPatients = new Roaring64NavigableMap();
        Roaring64NavigableMap[] infectedPatients = new Roaring64NavigableMap[intervals];
        for (int i = 0; i < intervals; i++) {
            infectedPatients[i] = new Roaring64NavigableMap();
        }

        // Step 1: Get the initial set of infected patients
        try(Transaction tx = db.beginTx()) {
            ResourceIterator<Node> infected =  db.findNodes(Labels.Infected);
            while (infected.hasNext()) {
                Node patient = infected.next();
                long infectedTime = getTimeOfCreation(patient);
                // Skip any infected nodes beyond our finalendtime
                if (infectedTime > finalEndTime) { continue; };
                int slot = (int)((infectedTime - time) / interval);
                infectedPatients[slot].add(patient.getId());
            }
            tx.success();
        }

        int changeCounter = 1;
        Iterator<Long> iterator;
        long nodeId;
        Transaction tx = db.beginTx();
        try {
            int counter = 0;
            long endTime;
            do {
                endTime = time + (interval * (1 + counter));
                TimeSlicedExpander expander = new TimeSlicedExpander(time, endTime);

                TraversalDescription td = db.traversalDescription()
                        .breadthFirst()
                        .expand(expander)
                        .evaluator(Evaluators.excludeStartPosition())
                        .uniqueness(Uniqueness.NODE_GLOBAL);


                iterator = infectedPatients[counter].iterator();
                boolean commit = false;
                while (iterator.hasNext()) {

                    // Commit to Neo4j and Start a new Transaction
                    if (commit) {
                        tx.success();
                        tx.close();
                        tx = db.beginTx();
                        commit = false;
                    }

                    nodeId = iterator.next();
                    Node patient = db.getNodeById(nodeId);

                    if (!patient.hasProperty(ccProperty)) {
                        patient.setProperty(ccProperty, ccID);
                    }

                    for (Path p : td.traverse(patient)) {
                        if (p.endNode().hasLabel(Labels.PATIENT) && !p.endNode().hasProperty(ccProperty)) {
                            p.endNode().setProperty(ccProperty, ccID);
                            nextPatients.add(p.endNode().getId());
                            if (changeCounter++ % TRANSACTION_LIMIT == 0) {
                                commit = true;
                            }
                        }
                    }
                }
                //infectedPatients[counter].or(nextPatients);
                stringsToPrint.add("/" + " : " + "Until period " + endTime
                        + " Num infected " + infectedPatients[counter].getLongCardinality() + " Newly infected " + nextPatients.getLongCardinality() + ";\n");
                // Add known infected plus newly infected patients to known infected patients at next time interval
                if (counter < intervals) {
                    infectedPatients[counter + 1].or(infectedPatients[counter]);
                    infectedPatients[counter + 1].or(nextPatients);
                    nextPatients.clear();
                }
                ccID++;
                counter++;
            } while (endTime < finalEndTime);
            tx.success();
        } catch ( Exception e ) {
            tx.failure();
        } finally {
            tx.close();
        }
    }

    private long getTimeOfCreation(Node patient) {
        long currCreationTime = Long.MAX_VALUE;
        for (Relationship r : patient.getRelationships(RelationshipTypes.OUTPUT, RelationshipTypes.CARRIER)) {
            long retrievedTime = ((Number) r.getProperty(TIME)).longValue();
            currCreationTime = Math.min(retrievedTime, currCreationTime);
        }
        return currCreationTime;
    }
}