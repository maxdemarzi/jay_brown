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

    public TimeSlicedExpander(Long start, Long end, Log log) {
        this.start = start;
        this.end = end;
        this.log = log;
    }

    @Override
    public Iterable<Relationship> expand(Path path, BranchState branchState) {

        List<Relationship> rels = new ArrayList<>();

        for (Relationship r : path.endNode().getRelationships()) {
            // We looked at this relationship already
            if(Procedures.seenRels.contains(r.getId())) { continue; }

            // We already infected this person
            if (Procedures.infected.contains(r.getEndNodeId())) { continue; }

            long time = Procedures.times.get(r.getId());
            if (time <= end) {
                rels.add(r);
            }

            // Regardless of if the relationship is over or under the time, I should not look at it again.
            Procedures.seenRels.add(r.getId());
        }
        return rels;

    }

    @Override
    public PathExpander reverse() {
        // Doesn't matter, do the same thing.
        return this;
    }
}