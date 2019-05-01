# Jay Brown
Procedure for Jay Brown

Instructions
------------ 

This project uses maven, to build a jar-file with the procedure in this
project, simply package the project with maven:

    mvn clean package

This will produce a jar-file, `target/procedures-1.0-SNAPSHOT.jar`,
that can be copied to the `plugin` directory of your Neo4j instance.

    cp target/procedures-1.0-SNAPSHOT.jar neo4j-enterprise-3.5.3/plugins/.
    

Restart your Neo4j Server. Your new Stored Procedures are available:

    CALL edu.baylor.cc($time, $interval, $end)
    CALL edu.baylor.cc(1262304000, 2592000, 1267488000)
    
Then check:

    MATCH (n) 
    RETURN n.ccId, count(*) 
    ORDER BY n.ccId
    

For example given the following graph:

    CREATE (i1:Infected:TYPE_1 { name:'infected 1' })
    CREATE (i2:Infected:TYPE_1 { name:'infected 2' })
    CREATE (i3:Infected:TYPE_1 { name:'infected 3' })
    CREATE (p1:TYPE_1 { name:'patient 1' })
    CREATE (p2:TYPE_1 { name:'patient 2' })
    CREATE (p3:TYPE_1 { name:'patient 3' })
    CREATE (p4:TYPE_1 { name:'patient 4' })
    CREATE (p5:TYPE_1 { name:'patient 5' })
    CREATE (p6:TYPE_1 { name:'patient 6' })
    CREATE (p7:TYPE_1 { name:'patient 7' })
    CREATE (p8:TYPE_1 { name:'patient 8' })
    CREATE (p9:TYPE_1 { name:'patient 9' })
    CREATE (p10:TYPE_1 { name:'patient 10' })    
    CREATE (e1:TYPE_2 { name:'event 1' })
    CREATE (e2:TYPE_2 { name:'event 2' })
    CREATE (e3:TYPE_2 { name:'event 3' })
    CREATE (e4:TYPE_2 { name:'event 4' })
    CREATE (e5:TYPE_2 { name:'event 5' })   
    CREATE (i1)-[:INPUT {time:1488585600}]->(e1)
    CREATE (i2)-[:INPUT {time:1493942400}]->(e2)
    CREATE (i3)-[:INPUT {time:1499472000}]->(e3)
    CREATE (e1)-[:OUTPUT {time:1520985600}]->(p1)
    CREATE (e2)-[:OUTPUT {time:1526256000}]->(p2)
    CREATE (e2)-[:OUTPUT {time:1526256000}]->(p3)
    CREATE (e3)-[:OUTPUT {time:1531526400}]->(p4)
    CREATE (e3)-[:OUTPUT {time:1531526400}]->(p5)
    CREATE (p1)-[:INPUT {time:1536883200}]->(e4)
    CREATE (p2)-[:INPUT {time:1536883200}]->(e4)
    CREATE (p3)-[:INPUT {time:1542153600}]->(e5)
    CREATE (p4)-[:INPUT {time:1542153600}]->(e5)
    CREATE (p5)-[:INPUT {time:1542153600}]->(e5)
    CREATE (e4)-[:OUTPUT {time:1568419200}]->(p6)
    CREATE (e4)-[:OUTPUT {time:1568419200}]->(p7)
    CREATE (e5)-[:OUTPUT {time:1557792000}]->(p8)
    CREATE (e5)-[:OUTPUT {time:1557792000}]->(p9)
    CREATE (e5)-[:OUTPUT {time:1557792000}]->(p10);
    
When you call the procedure with these parameters:
 
    // 1483228800 is 1/1/2017
    // 31536000 seconds in a year
    CALL edu.baylor.cc(1483228800, 31536000)
    
You get the result you were expecting.       

To get the following queries to run faster I suggest adding these indexes after running the procedure:

    CREATE INDEX ON :TYPE_1(cc1Id);
    CREATE INDEX ON :TYPE_1(cc2Id);
    CREATE INDEX ON :TYPE_1(cc3Id);
    
Queries:

    MATCH (n:TYPE_1) 
    WHERE EXISTS(n.cc1Id) 
    RETURN distinct(n.cc1Id) as partition, count(*) as size_of_partition 
    ORDER by size_of_partition DESC    
    
    MATCH (n:TYPE_1) 
    WHERE EXISTS(n.cc2Id) 
    RETURN distinct(n.cc2Id) as partition, count(*) as size_of_partition 
    ORDER by size_of_partition DESC    
    
    MATCH (n:TYPE_1) 
    WHERE EXISTS(n.cc3Id) 
    RETURN distinct(n.cc3Id) as partition, count(*) as size_of_partition 
    ORDER by size_of_partition DESC    
