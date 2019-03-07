package edu.baylor;

import edu.baylor.schema.Labels;
import org.neo4j.graphdb.*;
import org.neo4j.graphdb.traversal.*;
import org.neo4j.logging.Log;
import org.roaringbitmap.longlong.Roaring64NavigableMap;

import java.util.Iterator;

public class CCRunnable implements Runnable {

    private static final int TRANSACTION_LIMIT = 1000;
    private GraphDatabaseService db;
    private Log log;
    private Long time;
    private Long interval;

    public CCRunnable(GraphDatabaseService db, Log log, Long time, Long interval) {
        this.db = db;
        this.log = log;
        this.time = time;
        this.interval = interval;
    }

    @Override
    public void run() {
        int ccID = 1;
        long currentTime = System.currentTimeMillis()/1000;

        Roaring64NavigableMap nextPatients = new Roaring64NavigableMap();
        Roaring64NavigableMap infectedPatients = new Roaring64NavigableMap();

        // Step 1: Get the initial set of infected patients
        try(Transaction tx = db.beginTx()) {
            ResourceIterator<Node> infected =  db.findNodes(Labels.Infected);
            while (infected.hasNext()) {
                Node patient = infected.next();
                infectedPatients.add(patient.getId());
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
                endTime = time + (interval * ++counter);
                TimeSlicedExpander expander = new TimeSlicedExpander(time, interval * counter);

                TraversalDescription td = db.traversalDescription()
                        .breadthFirst()
                        .expand(expander)
                        .evaluator(Evaluators.excludeStartPosition())
                        .uniqueness(Uniqueness.NODE_GLOBAL);

                String ccProperty = "cc" + counter + "Id";
                iterator = infectedPatients.iterator();
                boolean commit = false;
                while (iterator.hasNext()) {

                    // Commit to Neo4j and Start a new Transaction
                    if (commit) {
                        tx.success();
                        tx.close();
                        tx = db.beginTx();
                        commit =false;
                    }

                    nodeId = iterator.next();
                    Node patient = db.getNodeById(nodeId);

                    if (!patient.hasProperty(ccProperty)) {
                        int currentCCid = ccID;
                        ccID++;
                        patient.setProperty(ccProperty, currentCCid);
                        for (Path p : td.traverse(patient)) {
                            p.endNode().setProperty(ccProperty, currentCCid);
                            nextPatients.add(p.endNode().getId());
                            if (changeCounter++ % TRANSACTION_LIMIT == 0) {
                                commit = true;
                            }
                        }
                    }
                }
                infectedPatients.or(nextPatients);
                nextPatients.clear();
                //endTime = time + (interval * ++counter);
                ccID = 1;
            } while (endTime < currentTime);
            tx.success();
        } catch ( Exception e ) {
            tx.failure();
        } finally {
            tx.close();
        }
    }
}