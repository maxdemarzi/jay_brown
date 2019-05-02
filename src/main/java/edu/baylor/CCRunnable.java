package edu.baylor;

import edu.baylor.schema.Labels;
import edu.baylor.schema.RelationshipTypes;
import org.neo4j.graphdb.*;
import org.neo4j.graphdb.traversal.*;
import org.neo4j.logging.Log;
import org.roaringbitmap.longlong.LongConsumer;
import org.roaringbitmap.longlong.Roaring64NavigableMap;

import java.util.*;

import static edu.baylor.schema.Properties.NAME;
import static edu.baylor.schema.Properties.TIME;

public class CCRunnable implements Runnable {

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
        // Integer division
        int intervals = (int)(Math.ceil((finalEndTime - time) / interval));

        Roaring64NavigableMap infected = new Roaring64NavigableMap();
        Roaring64NavigableMap nextPatients = new Roaring64NavigableMap();
        Roaring64NavigableMap[] infectedPatients = new Roaring64NavigableMap[intervals];
        for (int i = 0; i < (intervals); i++) {
            infectedPatients[i] = new Roaring64NavigableMap();
        }

        // Step 1: Get the initial set of infected patients
        try(Transaction tx = db.beginTx()) {
            ResourceIterator<Node> infectedNodes =  db.findNodes(Labels.Infected);
            while (infectedNodes.hasNext()) {
                Node patient = infectedNodes.next();
                long infectedTime = getTimeOfCreation(patient);
                // Skip any infected nodes beyond our finalendtime
                if (infectedTime > finalEndTime) { continue; };
                int slot = (int)(Math.ceil((infectedTime - time) / interval));
                infectedPatients[slot].add(patient.getId());
            }
            tx.success();
        }

        Iterator<Long> iterator;
        long nodeId;

        Transaction tx = db.beginTx();
        try {
            long endTime;
            // Start - interval and add the interval every time in the loop.
            time = time - interval;
            for (int counter = 0; counter < intervals; counter++ ) {
                time = time + interval;
                endTime = time + interval;
                TimeSlicedExpander expander = new TimeSlicedExpander(time, endTime);

                TraversalDescription td = db.traversalDescription()
                        .breadthFirst()
                        .expand(expander)
                        .evaluator(Evaluators.excludeStartPosition())
                        .uniqueness(Uniqueness.NODE_GLOBAL);


                // Add already infected patients to seen
                infected.or(infectedPatients[counter]);


                iterator = infectedPatients[counter].iterator();
                while (iterator.hasNext()) {
                    nodeId = iterator.next();
                    Node patient = db.getNodeById(nodeId);

                    for (Path p : td.traverse(patient)) {
                        if (p.endNode().hasLabel(Labels.PATIENT)) {
                            nextPatients.add(p.endNode().getId());
                        }
                    }
                }

                infected.or(nextPatients);
                nextPatients.andNot(infectedPatients[counter]);

                stringsToPrint.add("/" + " : " + "From: " + time + " Until: " + endTime
                        + " Infected:  at start " + infectedPatients[counter].getLongCardinality() + " newly infected " + nextPatients.getLongCardinality() + " All Infected: " + infected.getLongCardinality() +  ";\n");

                // Add known infected plus newly infected patients to known infected patients at next time interval
                if (counter + 1 < intervals) {
                    infectedPatients[counter + 1].or(infectedPatients[counter]);
                    infectedPatients[counter + 1].or(nextPatients);
                    nextPatients.clear();
                }
            }
            tx.success();
        } catch ( Exception e ) {
            tx.failure();
            this.stringsToPrint.add("There was an exception: " + e.getMessage());
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