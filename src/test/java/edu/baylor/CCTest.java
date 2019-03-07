package edu.baylor;

import org.junit.jupiter.api.*;
import org.neo4j.driver.v1.*;
import org.neo4j.harness.ServerControls;
import org.neo4j.harness.TestServerBuilders;

import static org.assertj.core.api.Assertions.assertThat;
import static org.neo4j.driver.v1.Values.parameters;

public class CCTest {
    private static ServerControls neo4j;

    @BeforeAll
    static void startNeo4j() {
        neo4j = TestServerBuilders.newInProcessBuilder()
                .withProcedure(Procedures.class)
                .withFixture(MODEL_STATEMENT)
                .newServer();
    }

    @AfterAll
    static void stopNeo4j() {
        neo4j.close();
    }

    @Test
    void shouldReturnPaths() {
        // In a try-block, to make sure we close the driver after the test
        try( Driver driver = GraphDatabase.driver( neo4j.boltURI() , Config.build().withoutEncryption().toConfig() ) )
        {

            // Given I've started Neo4j with the procedure
            //       which my 'neo4j' rule above does.
            Session session = driver.session();

            // When I use the procedure with January 1st 2017 for time, and a yearly interval
            StatementResult result = session.run( "CALL edu.baylor.cc($time, $interval)",
                    parameters( "time", 1483228800, "interval", 31536000 ) );

            // Then I should get what I expect
            assertThat(result.single().get("value").asString()).isEqualTo("Connected Components calculated in 0 seconds");
        }
    }

    private static final String MODEL_STATEMENT =
            "CREATE (i1:Infected { name:'infected 1' })" +
            "CREATE (i2:Infected { name:'infected 2' })" +
            "CREATE (i3:Infected { name:'infected 3' })" +
            "CREATE (p1:TYPE_1 { name:'patient 1' })" +
            "CREATE (p2:TYPE_1 { name:'patient 2' })" +
            "CREATE (p3:TYPE_1 { name:'patient 3' })" +
            "CREATE (p4:TYPE_1 { name:'patient 4' })" +
            "CREATE (p5:TYPE_1 { name:'patient 5' })" +
            "CREATE (p6:TYPE_1 { name:'patient 6' })" +
            "CREATE (p7:TYPE_1 { name:'patient 7' })" +
            "CREATE (p8:TYPE_1 { name:'patient 8' })" +
            "CREATE (p9:TYPE_1 { name:'patient 9' })" +
            "CREATE (p10:TYPE_1 { name:'patient 10' })" +

            "CREATE (e1:TYPE_2 { name:'event 1' })" +
            "CREATE (e2:TYPE_2 { name:'event 2' })" +
            "CREATE (e3:TYPE_2 { name:'event 3' })" +
            "CREATE (e4:TYPE_2 { name:'event 4' })" +
            "CREATE (e5:TYPE_2 { name:'event 5' })" +

            "CREATE (i1)-[:INPUT {time:1488585600}]->(e1)" +
            "CREATE (i2)-[:INPUT {time:1493942400}]->(e2)" +
            "CREATE (i3)-[:INPUT {time:1499472000}]->(e3)" +

            "CREATE (e1)-[:OUTPUT {time:1520985600}]->(p1)" +
            "CREATE (e2)-[:OUTPUT {time:1526256000}]->(p2)" +
            "CREATE (e2)-[:OUTPUT {time:1526256000}]->(p3)" +
            "CREATE (e3)-[:OUTPUT {time:1531526400}]->(p4)" +
            "CREATE (e3)-[:OUTPUT {time:1531526400}]->(p5)" +

            "CREATE (p1)-[:INPUT {time:1536883200}]->(e4)" +
            "CREATE (p2)-[:INPUT {time:1536883200}]->(e4)" +
            "CREATE (p3)-[:INPUT {time:1542153600}]->(e5)" +
            "CREATE (p4)-[:INPUT {time:1542153600}]->(e5)" +
            "CREATE (p5)-[:INPUT {time:1542153600}]->(e5)" +

            "CREATE (e4)-[:OUTPUT {time:1568419200}]->(p6)" +
            "CREATE (e4)-[:OUTPUT {time:1568419200}]->(p7)" +
            "CREATE (e5)-[:OUTPUT {time:1557792000}]->(p8)" +
            "CREATE (e5)-[:OUTPUT {time:1557792000}]->(p9)" +
            "CREATE (e5)-[:OUTPUT {time:1557792000}]->(p10)"
            ;
}
