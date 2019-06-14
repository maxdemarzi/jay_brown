package edu.baylor.schema;

import org.neo4j.graphdb.Label;

public enum Labels implements Label {
    Infected,
    TYPE_1, // Patient
    TYPE_2 // Event
}