package edu.baylor.schema;

import org.neo4j.graphdb.RelationshipType;

public enum RelationshipTypes implements RelationshipType {
    CARRIER,
    INPUT,
    OUTPUT
}
