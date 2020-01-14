package edu.baylor;

import edu.baylor.schema.Labels;
import edu.baylor.schema.RelationshipTypes;
import org.neo4j.graphdb.*;
import org.neo4j.graphdb.traversal.BranchState;
import org.neo4j.logging.Log;

import static edu.baylor.schema.Properties.NAME;

import java.util.*;

public class TimeSlicedExpander implements PathExpander {

    private Long start;
    private Long end;
    private Log log;
    private Long depth;
    private String depth_property;

    public TimeSlicedExpander(Long start, Long end, Log log, String depth_property) {
        this.start = start;
        this.end = end;
        this.log = log;
        this.depth_property = depth_property;
    }

    @Override
    public Iterable<Relationship> expand(Path path, BranchState branchState) {

        List<Relationship> rels = new ArrayList<>();
        for (Relationship r : path.endNode().getRelationships()) {

            long time = Procedures.times.get(r.getId());
            if (time <= end) {
                if (r.hasProperty(depth_property)) {continue;}
                rels.add(r);
            }
        }
        return rels;

    }

    @Override
    public PathExpander reverse() {
        // Doesn't matter, do the same thing.
        return this;
    }
}