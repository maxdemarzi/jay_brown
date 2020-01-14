package edu.baylor;

import edu.baylor.schema.Labels;
import edu.baylor.schema.RelationshipTypes;
import org.neo4j.graphdb.*;
import org.neo4j.logging.Log;
import org.roaringbitmap.longlong.Roaring64NavigableMap;

import static edu.baylor.schema.Properties.NAME;
import static edu.baylor.schema.Properties.TIME;
import static edu.baylor.schema.Properties.RECORD_NUM;
import static edu.baylor.schema.Properties.EVENT_ID;

import java.util.*;
import java.util.stream.Collectors;

public class CCRunnable implements Runnable {

    private GraphDatabaseService db;
    private Log log;
    private Long time;
    private Long finalEndTime;
    private ArrayList<String> stringsToPrint;
    private String depth_property;
    private Integer track_depth = 0;
    private Long maxDepth;
    int infected = 0;
    int initialPatients;
    private Node patient;

    public CCRunnable(GraphDatabaseService db, Log log, Long time, Long finalEndTime, ArrayList<String> stringsToPrint,
                      Long maxDepth, String depth_property)  {
        this.db = db;
        this.log = log;
        this.time = time;
        this.finalEndTime = finalEndTime;
        this.stringsToPrint = stringsToPrint;
        this.maxDepth = maxDepth;
        this.depth_property = depth_property;
    }

    @Override
    public void run() {
        Roaring64NavigableMap currentPatients = new Roaring64NavigableMap();
        Roaring64NavigableMap nextPatients = new Roaring64NavigableMap();

        // Step 1: Get the initial set of infected patients
        try(Transaction tx = db.beginTx()) {
            ResourceIterator<Node> infectedNodes =  db.findNodes(Labels.Infected6);
            while (infectedNodes.hasNext()) {
                Node patient = infectedNodes.next();
                long infectedTime = getTimeOfCreation(patient);
                // Skip any infected nodes beyond our final end time
                if (infectedTime > finalEndTime) { continue; }
                patient.setProperty(depth_property, track_depth);
                currentPatients.add(patient.getId());
                infected++;
            }
            initialPatients = currentPatients.getIntCardinality();
            tx.success();
        } catch ( Exception e ) {
            this.stringsToPrint.add("There was an exception in step 1: " + e.getMessage()+
                    " tracked depth: "+track_depth);
            log.error("Caught exception in first step. Please investigate: "
                    + e
                    + Arrays.asList(e.getStackTrace())
                    .stream()
                    .map(Objects::toString)
                    .collect(Collectors.joining("\n"))
            );
        }
        long nodeId;

        // Step 2: traverse our initial set
        Transaction tx = db.beginTx();

        try {
            // Iterate over current list of nodes
            Iterator<Long> current_iterator = currentPatients.iterator();

            while (track_depth <= maxDepth) {
                if (!current_iterator.hasNext()) {
                    currentPatients.clear();
                    currentPatients.or(nextPatients);
                    current_iterator = currentPatients.iterator();
                    nextPatients.clear();

                    // No more nodes to iterate over, full graph is traversed.
                    if (!current_iterator.hasNext()) {
                        break;
                    }
                    track_depth++;
                    log.error("new track depth: "+track_depth);

                }

                nodeId = current_iterator.next();
                patient = db.getNodeById(nodeId);

                // Add 1 step neighbors to next list if they've never been seen before
                for (Relationship r : patient.getRelationships()) {
                    long time = Procedures.times.get(r.getId());
                    if (time <= finalEndTime) {
                        if (r.hasProperty(depth_property)) {continue;}
                        r.setProperty(depth_property, track_depth + 1);

                        Node start_node = r.getStartNode();
                        if (!start_node.hasProperty(depth_property)) {
                            start_node.setProperty(depth_property, track_depth + 1);
                            nextPatients.add(start_node.getId());
                            if (start_node.hasLabel(Labels.PATIENT)) {
                                infected++;
                            }
                            continue;
                        }

                        Node end_node = r.getEndNode();
                        if (!end_node.hasProperty(depth_property)) {
                            end_node.setProperty(depth_property, track_depth + 1);
                            nextPatients.add(end_node.getId());
                            if (end_node.hasLabel(Labels.PATIENT)) {
                                infected++;
                            }
                        }
                    }
                }

            }

            stringsToPrint.add("/" + " : " + "From: " + time + " Until: " + finalEndTime
                    + " Infected:  at start " + initialPatients
                    +  " All Infected: " + infected +
                    " Ending depth: "+track_depth+";\n");

            tx.success();

        } catch ( Exception e ) {
            tx.failure();
            this.stringsToPrint.add("There was an exception: " + e.getMessage()+
                    " tracked depth: "+track_depth);
            log.error("Caught exception while methodX. Please investigate: "
                    + e
                    + Arrays.asList(e.getStackTrace())
                    .stream()
                    .map(Objects::toString)
                    .collect(Collectors.joining("\n"))
            );
            //log.error(e.printStackTrace());
        } finally {
            if (patient.hasProperty(NAME)) {
                stringsToPrint.add("/ Finally statement " + " : " + "From: " + time + " Until: " + finalEndTime
                        + " Infected:  at start " + initialPatients
                        + " All Infected: " + infected +
                        " Ending depth: " + track_depth +
                        " Ending patient node: " + patient.getProperty(NAME) + ";\n");
            }
            else {
                stringsToPrint.add("/ Finally statement " + " : " + "From: " + time + " Until: " + finalEndTime
                        + " Infected:  at start " + initialPatients
                        + " All Infected: " + infected +
                        " Ending depth: " + track_depth +
                        " Ending event node: " + patient.getProperty(EVENT_ID) + ";\n");
            }
            log.error("Finally method reached. Stack trace: "
                    + Arrays.asList(Thread.currentThread().getStackTrace())
                    .stream()
                    .map(Objects::toString)
                    .collect(Collectors.joining("\n")));
            tx.close();
        }
    }

    //Get node creation time from incoming edges
    private long getTimeOfCreation(Node patient) {
        long currCreationTime = Long.MAX_VALUE;
        for (Relationship r : patient.getRelationships(RelationshipTypes.OUTPUT)) {
            long retrievedTime = ((Number) r.getProperty(TIME)).longValue();
            currCreationTime = Math.min(retrievedTime, currCreationTime);
        }

        for (Relationship r : patient.getRelationships(RelationshipTypes.CARRIER)) {
            long parent_time = ((Number) r.getStartNode().getProperty(RECORD_NUM)).longValue();
            if (parent_time < currCreationTime) {
                currCreationTime = parent_time;
            }
        }
        return currCreationTime;
    }
}