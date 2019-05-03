package edu.baylor;

import org.junit.jupiter.api.*;
import org.neo4j.driver.v1.*;
import org.neo4j.harness.ServerControls;
import org.neo4j.harness.TestServerBuilders;

import static org.assertj.core.api.Assertions.assertThat;
import static org.neo4j.driver.v1.Values.parameters;

public class UnitTest {
    private ServerControls neo4j;

    @BeforeEach
    void startNeo4j() {
        neo4j = TestServerBuilders.newInProcessBuilder()
                .withProcedure(Procedures.class)
                .withFixture(MODEL_STATEMENT)
                .newServer();
    }

    @AfterEach
    void stopNeo4j() {
        neo4j.close();
    }

    private static final String MODEL_STATEMENT =
            "CREATE (c1:CARRIER { city:'city 1' })" +
                    "CREATE (c2:CARRIER { city:'city 2' })" +
                    "CREATE (c3:CARRIER { city:'city 3' })" +

                    "CREATE (i1:Infected { patientId:'infected 1' })" +
                    "CREATE (i2:Infected { patientId:'infected 2' })" +
                    "CREATE (i3:Infected { patientId:'infected 3' })" +
                    "CREATE (p1:PATIENT { patientId:'patient 1' })" +
                    "CREATE (p2:PATIENT { patientId:'patient 2' })" +
                    "CREATE (p3:PATIENT { patientId:'patient 3' })" +
                    "CREATE (p4:PATIENT { patientId:'patient 4' })" +
                    "CREATE (p5:PATIENT { patientId:'patient 5' })" +
                    "CREATE (p6:PATIENT { patientId:'patient 6' })" +
                    "CREATE (p7:PATIENT { patientId:'patient 7' })" +
                    "CREATE (p8:PATIENT { patientId:'patient 8' })" +
                    "CREATE (p9:PATIENT { patientId:'patient 9' })" +
                    "CREATE (p10:PATIENT { patientId:'patient 10' })" +
                    "CREATE (p11:PATIENT { patientId:'patient 11' })" +
                    "CREATE (p12:PATIENT { patientId:'patient 12' })" +
                    "CREATE (p13:PATIENT { patientId:'patient 13' })" +
                    "CREATE (p14:PATIENT { patientId:'patient 14' })" +
                    "CREATE (p15:PATIENT { patientId:'patient 15' })" +
                    "CREATE (p16:PATIENT { patientId:'patient 16' })" +
                    "CREATE (p17:PATIENT { patientId:'patient 17' })" +
                    "CREATE (p18:PATIENT { patientId:'patient 18' })" +
                    "CREATE (p19:PATIENT { patientId:'patient 19' })" +
                    "CREATE (p20:PATIENT { patientId:'patient 20' })" +

                    "CREATE (p21:PATIENT { patientId:'patient 21' })" +
                    "CREATE (p22:PATIENT { patientId:'patient 22' })" +
                    "CREATE (p23:PATIENT { patientId:'patient 23' })" +
                    "CREATE (p24:PATIENT { patientId:'patient 24' })" +

                    "CREATE (e1:INFECTION { infectionID:'event 1' })" +
                    "CREATE (e2:INFECTION { infectionID:'event 2' })" +
                    "CREATE (e3:INFECTION { infectionID:'event 3' })" +
                    "CREATE (e4:INFECTION { infectionID:'event 4' })" +
                    "CREATE (e5:INFECTION { infectionID:'event 5' })" +
                    "CREATE (e6:INFECTION { infectionID:'event 6' })" +
                    "CREATE (e7:INFECTION { infectionID:'event 7' })" +
                    "CREATE (e8:INFECTION { infectionID:'event 8' })" +
                    "CREATE (e9:INFECTION { infectionID:'event 9' })" +
                    "CREATE (e10:INFECTION { infectionID:'event 10' })" +

                    "CREATE (e11:INFECTION { infectionID:'event 11' })" +
                    "CREATE (e12:INFECTION { infectionID:'event 12' })" +
                    "CREATE (e13:INFECTION { infectionID:'event 13' })" +
                    "CREATE (e14:INFECTION { infectionID:'event 14' })" +
                    "CREATE (e15:INFECTION { infectionID:'event 15' })" +

                    "CREATE (b1)-[:OUTPUT {time:1262304000}]->(i1)" +
                    "CREATE (b2)-[:OUTPUT {time:1262304000}]->(i2)" +
                    "CREATE (b3)-[:OUTPUT {time:1262304000}]->(i3)" +

                    "CREATE (i1)-[:INPUT {time:1262304000}]->(e1)" +
                    "CREATE (i2)-[:INPUT {time:1262304000}]->(e2)" +
                    "CREATE (i3)-[:INPUT {time:1262304000}]->(e3)" +

                    "CREATE (e1)-[:OUTPUT {time:1262304000}]->(p1)" +
                    "CREATE (e2)-[:OUTPUT {time:1262304000}]->(p2)" +
                    "CREATE (e2)-[:OUTPUT {time:1262304000}]->(p3)" +
                    "CREATE (e3)-[:OUTPUT {time:1262304000}]->(p4)" +
                    "CREATE (e3)-[:OUTPUT {time:1262304000}]->(p5)" +

                    "CREATE (p1)-[:INPUT {time:1267487000}]->(e4)" +
                    "CREATE (p2)-[:INPUT {time:1267487000}]->(e4)" +
                    "CREATE (p3)-[:INPUT {time:1267487000}]->(e5)" +
                    "CREATE (p4)-[:INPUT {time:1267487000}]->(e5)" +
                    "CREATE (p5)-[:INPUT {time:1267487000}]->(e5)" +

                    "CREATE (e4)-[:OUTPUT {time:1267487000}]->(p6)" +
                    "CREATE (e4)-[:OUTPUT {time:1267487000}]->(p7)" +
                    "CREATE (e5)-[:OUTPUT {time:1267487000}]->(p8)" +
                    "CREATE (e5)-[:OUTPUT {time:1267487000}]->(p9)" +
                    "CREATE (e5)-[:OUTPUT {time:1267487000}]->(p10)" +


                    "CREATE (p11)-[:INPUT {time:1262304000}]->(e6)" +
                    "CREATE (p12)-[:INPUT {time:1262304000}]->(e7)" +
                    "CREATE (p13)-[:INPUT {time:1262304000}]->(e8)" +

                    "CREATE (e6)-[:OUTPUT {time:1262304000}]->(p11)" +
                    "CREATE (e7)-[:OUTPUT {time:1262304000}]->(p14)" +
                    "CREATE (e7)-[:OUTPUT {time:1262304000}]->(p15)" +
                    "CREATE (e8)-[:OUTPUT {time:1262304000}]->(p16)" +
                    "CREATE (e8)-[:OUTPUT {time:1262304000}]->(p17)" +

                    "CREATE (p17)-[:INPUT {time:1267487000}]->(e8)" +
                    "CREATE (p18)-[:INPUT {time:1267487000}]->(e9)" +
                    "CREATE (p19)-[:INPUT {time:1267487000}]->(e9)" +
                    "CREATE (p20)-[:INPUT {time:1267487000}]->(e9)" +
                    "CREATE (p15)-[:INPUT {time:1267487000}]->(e10)" +

                    "CREATE (e9)-[:OUTPUT {time:1267487000}]->(p16)" +
                    "CREATE (e9)-[:OUTPUT {time:1267487000}]->(p17)" +
                    "CREATE (e8)-[:OUTPUT {time:1267487000}]->(p18)" +
                    "CREATE (e10)-[:OUTPUT {time:1267487000}]->(p19)" +
                    "CREATE (e10)-[:OUTPUT {time:1267487000}]->(p20)"+

                    //p21 interacts with p22 in T1-> p22 is not infected at all
                    "CREATE (p21)-[:INPUT {time:1262304000}]->(e11)" +
                    "CREATE (e11)-[:OUTPUT {time:1262304000}]->(p22)" +

                    //p21 is infected in T2-> p21 infected in T2
                    "CREATE (i1)-[:INPUT {time:1267487000}]->(e12)" +
                    "CREATE (e12)-[:OUTPUT {time:1267487000}]->(p21)" +

                    //p23 is infected in late T1-> p23 infected in T1
                    "CREATE (i1)-[:INPUT {time:1262304001}]->(e13)" +
                    "CREATE (e13)-[:OUTPUT {time:1262304001}]->(p23)" +

                    //p23 interacts with p24 in early T1-> should not contaminate p24 in T1
                    "CREATE (p23)-[:INPUT {time:1262303999}]->(e14)" +
                    "CREATE (e14)-[:OUTPUT {time:1262303999}]->(p24)" +

                    //p23 interacts with p24 again in T2-> now p24 is infected in T2
                    "CREATE (p23)-[:INPUT {time:1267487000}]->(e15)" +
                    "CREATE (e15)-[:OUTPUT {time:1267487000}]->(p24)";


    @Test
    void shouldReturnPaths() {
        // In a try-block, to make sure we close the driver after the test
        try (Driver driver = GraphDatabase.driver(neo4j.boltURI(), Config.build().withoutEncryption().toConfig())) {

            // Given I've started Neo4j with the procedure
            //       which my 'neo4j' rule above does.
            Session session = driver.session();

            // When I use the procedure with January 1st 2010 for time, and a monthly interval with the end time 2 months later
            StatementResult result = session.run("CALL " + Procedures.runname + "($time, $interval, $end)",
                    parameters("time", 1262304000, "interval", 2592000, "end", 1264896000));

            // Then I should get what I expect
            assertThat(result.single().get("value").asString().contains("Until period 1264896000 Num infected 9"));
        }

    }

    @Test
    void shouldReturnPathsTwo() {
        // In a try-block, to make sure we close the driver after the test
        try (Driver driver = GraphDatabase.driver(neo4j.boltURI(), Config.build().withoutEncryption().toConfig())) {

            // Given I've started Neo4j with the procedure
            //       which my 'neo4j' rule above does.
            Session session = driver.session();

            // When I use the procedure with January 1st 2010 for time, and a monthly interval with the end time 2 months later
            StatementResult result = session.run("CALL " + Procedures.runname + "($time, $interval, $end)",
                    parameters("time", 1262304000, "interval", 2592000, "end", 1267488000));

            // Then I should get what I expect
            assertThat(result.single().get("value").asString().contains("Until period 1267488000 Num infected 16"));
        }
    }

}