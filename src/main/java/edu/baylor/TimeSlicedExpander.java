package edu.baylor;

import edu.baylor.schema.RelationshipTypes;
import org.neo4j.graphdb.*;
import org.neo4j.graphdb.traversal.BranchState;

import java.util.ArrayList;
import java.util.List;

public class TimeSlicedExpander implements PathExpander {

    private Long start;
    private Long end;

    public TimeSlicedExpander(Long start, Long end) {
        this.start = start;
        this.end = end;
    }

    @Override
    public Iterable<Relationship> expand(Path path, BranchState branchState) {
        //When did I get infected
        long infectedTime = start;
        if (path.length() > 0) {
            infectedTime = Procedures.times.get(path.lastRelationship().getId());
        }

        // Traverse any relationships AFTER I got infected and before the end of the time interval
        List<Relationship> rels = new ArrayList<>();
        for (Relationship r : path.endNode().getRelationships(Direction.OUTGOING, RelationshipTypes.INPUT, RelationshipTypes.OUTPUT)) {
            if(Procedures.seenRels.contains(r.getId())) { continue; }
            long time = Procedures.times.get(r.getId());
            if (time >= infectedTime && time < end) {
                rels.add(r);
                Procedures.seenRels.add(r.getId());
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