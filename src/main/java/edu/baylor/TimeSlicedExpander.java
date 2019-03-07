package edu.baylor;

import edu.baylor.schema.RelationshipTypes;
import org.neo4j.graphdb.*;
import org.neo4j.graphdb.traversal.BranchState;

import java.util.ArrayList;
import java.util.List;

import static edu.baylor.schema.Properties.TIME;

public class TimeSlicedExpander implements PathExpander {

    private Long start;
    private Long end;

    public TimeSlicedExpander(Long start, Long interval) {
        this.start = start;
        this.end = start + interval;
    }

    @Override
    public Iterable<Relationship> expand(Path path, BranchState branchState) {
        List<Relationship> rels = new ArrayList<>();
        for (Relationship r : path.endNode().getRelationships(Direction.OUTGOING, RelationshipTypes.INPUT, RelationshipTypes.OUTPUT)) {
            long time = (long)r.getProperty(TIME);
            if (time >= start && time < end) {
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