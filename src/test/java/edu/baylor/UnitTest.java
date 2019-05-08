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
            "CREATE (c1:CARRIER { city:'city 1', record_num:1 })" +
                    "CREATE (c2:CARRIER { city:'city 2', record_num:2})" +
                    "CREATE (c3:CARRIER { city:'city 3', record_num:3})" +

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

                    "CREATE (p25:PATIENT { patientId:'patient 25' })" +
                    "CREATE (p26:PATIENT { patientId:'patient 26' })" +
                    "CREATE (p27:PATIENT { patientId:'patient 27' })" +

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

                    "CREATE (e16:INFECTION { infectionId:'event 16' })" +
                    "CREATE (e17:INFECTION { infectionId:'event 17' })" +
                    "CREATE (e18:INFECTION { infectionId:'event 18' })" +

                    "CREATE (b1)-[:OUTPUT {num:5}]->(i1)" +
                    "CREATE (b2)-[:OUTPUT {num:5}]->(i2)" +
                    "CREATE (b3)-[:OUTPUT {num:5}]->(i3)" +

                    "CREATE (i1)-[:INPUT {num:5}]->(e1)" +
                    "CREATE (i2)-[:INPUT {num:5}]->(e2)" +
                    "CREATE (i3)-[:INPUT {num:5}]->(e3)" +

                    "CREATE (e1)-[:OUTPUT {num:5}]->(p1)" +
                    "CREATE (e2)-[:OUTPUT {num:5}]->(p2)" +
                    "CREATE (e2)-[:OUTPUT {num:5}]->(p3)" +
                    "CREATE (e3)-[:OUTPUT {num:5}]->(p4)" +
                    "CREATE (e3)-[:OUTPUT {num:5}]->(p5)" +

                    "CREATE (p1)-[:INPUT {num:10}]->(e4)" +
                    "CREATE (p2)-[:INPUT {num:10}]->(e4)" +
                    "CREATE (p3)-[:INPUT {num:10}]->(e5)" +
                    "CREATE (p4)-[:INPUT {num:10}]->(e5)" +
                    "CREATE (p5)-[:INPUT {num:10}]->(e5)" +

                    "CREATE (e4)-[:OUTPUT {num:10}]->(p6)" +
                    "CREATE (e4)-[:OUTPUT {num:10}]->(p7)" +
                    "CREATE (e5)-[:OUTPUT {num:10}]->(p8)" +
                    "CREATE (e5)-[:OUTPUT {num:10}]->(p9)" +
                    "CREATE (e5)-[:OUTPUT {num:10}]->(p10)" +


                    "CREATE (p11)-[:INPUT {num:5}]->(e6)" +
                    "CREATE (p12)-[:INPUT {num:5}]->(e7)" +
                    "CREATE (p13)-[:INPUT {num:5}]->(e8)" +

                    "CREATE (e6)-[:OUTPUT {num:5}]->(p11)" +
                    "CREATE (e7)-[:OUTPUT {num:5}]->(p14)" +
                    "CREATE (e7)-[:OUTPUT {num:5}]->(p15)" +
                    "CREATE (e8)-[:OUTPUT {num:5}]->(p16)" +
                    "CREATE (e8)-[:OUTPUT {num:5}]->(p17)" +

                    "CREATE (p17)-[:INPUT {num:10}]->(e8)" +
                    "CREATE (p18)-[:INPUT {num:10}]->(e9)" +
                    "CREATE (p19)-[:INPUT {num:10}]->(e9)" +
                    "CREATE (p20)-[:INPUT {num:10}]->(e9)" +
                    "CREATE (p15)-[:INPUT {num:10}]->(e10)" +

                    "CREATE (e9)-[:OUTPUT {num:10}]->(p16)" +
                    "CREATE (e9)-[:OUTPUT {num:10}]->(p17)" +
                    "CREATE (e8)-[:OUTPUT {num:10}]->(p18)" +
                    "CREATE (e10)-[:OUTPUT {num:10}]->(p19)" +
                    "CREATE (e10)-[:OUTPUT {num:10}]->(p20)"+


                    //p21 interacts with p22 in T1-> p22 is not infected at all
                    "CREATE (p21)-[:INPUT {num:5}]->(e11)" +
                    "CREATE (e11)-[:OUTPUT {num:5}]->(p22)" +

                    //p21 is infected in T2-> p21 infected in T2
                    "CREATE (i1)-[:INPUT {num:10}]->(e12)" +
                    "CREATE (e12)-[:OUTPUT {num:10}]->(p21)" +

                    //p23 is infected in late T1-> p23 infected in T1
                    "CREATE (i1)-[:INPUT {num:6}]->(e13)" +
                    "CREATE (e13)-[:OUTPUT {num:6}]->(p23)" +

                    //p23 interacts with p24 in early T1-> should not contaminate p24 in T1
                    "CREATE (p23)-[:INPUT {num:5}]->(e14)" +
                    "CREATE (e14)-[:OUTPUT {num:5}]->(p24)" +

                    //p23 interacts with p24 again in T2-> now p24 is infected in T2
                    "CREATE (p23)-[:INPUT {num:10}]->(e15)" +
                    "CREATE (e15)-[:OUTPUT {num:10}]->(p24)"+


                    //num simultaneous infection of p25, p26, p27 in T1
                    "CREATE (i2)-[:INPUT {num:5}]->(e16)" +
                    "CREATE (e16)-[:OUTPUT {num:5}]->(p25)"+

                    "CREATE (p25)-[:INPUT {num:5}]->(e17)" +
                    "CREATE (e17)-[:OUTPUT {num:5}]->(p26)"+

                    "CREATE (p26)-[:INPUT {num:5}]->(e18)" +
                    "CREATE (e18)-[:OUTPUT {num:5}]->(p27)";


    @Test
    void shouldReturnPaths() {
        // In a try-block, to make sure we close the driver after the test
        try (Driver driver = GraphDatabase.driver(neo4j.boltURI(), Config.build().withoutEncryption().toConfig())) {

            // Given I've started Neo4j with the procedure
            //       which my 'neo4j' rule above does.
            Session session = driver.session();

            // When I use the procedure with January 1st 2010 for num, and a monthly interval with the end num 2 months later
            StatementResult result = session.run("CALL " + Procedures.runname + "($time, $interval, $end)",
                    parameters("time", 0, "interval", 6, "end", 6));

            // Then I should get what I expect
            assertThat(result.single().get("value").asString().contains("Until period 6 Num infected 12"));
        }

    }

    @Test
    void shouldReturnPathsTwo() {
        // In a try-block, to make sure we close the driver after the test
        try (Driver driver = GraphDatabase.driver(neo4j.boltURI(), Config.build().withoutEncryption().toConfig())) {

            // Given I've started Neo4j with the procedure
            //       which my 'neo4j' rule above does.
            Session session = driver.session();

            // When I use the procedure with January 1st 2010 for num, and a monthly interval with the end num 2 months later
            StatementResult result = session.run("CALL " + Procedures.runname + "($time, $interval, $end)",
                    parameters("time", 0, "interval", 2, "end", 12));

            // Then I should get what I expect
            assertThat(result.single().get("value").asString().contains("Until period 12 Num infected 19"));
        }
    }

}