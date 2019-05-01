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

    CREATE (c1:CARRIER { city:'city 1' })
    CREATE (c2:CARRIER { city:'city 2' })
    CREATE (c3:CARRIER { city:'city 3' })
    
    CREATE (i1:Infected { patientId:'infected 1' })
    CREATE (i2:Infected { patientId:'infected 2' })
    CREATE (i3:Infected { patientId:'infected 3' })
    CREATE (p1:PATIENT { patientId:'patient 1' })
    CREATE (p2:PATIENT { patientId:'patient 2' })
    CREATE (p3:PATIENT { patientId:'patient 3' })
    CREATE (p4:PATIENT { patientId:'patient 4' })
    CREATE (p5:PATIENT { patientId:'patient 5' })
    CREATE (p6:PATIENT { patientId:'patient 6' })
    CREATE (p7:PATIENT { patientId:'patient 7' })
    CREATE (p8:PATIENT { patientId:'patient 8' })
    CREATE (p9:PATIENT { patientId:'patient 9' })
    CREATE (p10:PATIENT { patientId:'patient 10' })
    CREATE (p11:PATIENT { patientId:'patient 11' })
    CREATE (p12:PATIENT { patientId:'patient 12' })
    CREATE (p13:PATIENT { patientId:'patient 13' })
    CREATE (p14:PATIENT { patientId:'patient 14' })
    CREATE (p15:PATIENT { patientId:'patient 15' })
    CREATE (p16:PATIENT { patientId:'patient 16' })
    CREATE (p17:PATIENT { patientId:'patient 17' })
    CREATE (p18:PATIENT { patientId:'patient 18' })
    CREATE (p19:PATIENT { patientId:'patient 19' })
    CREATE (p20:PATIENT { patientId:'patient 20' })
    
    CREATE (e1:INFECTION { infectionID:'event 1' })
    CREATE (e2:INFECTION { infectionID:'event 2' })
    CREATE (e3:INFECTION { infectionID:'event 3' })
    CREATE (e4:INFECTION { infectionID:'event 4' })
    CREATE (e5:INFECTION { infectionID:'event 5' })
    CREATE (e6:INFECTION { infectionID:'event 6' })
    CREATE (e7:INFECTION { infectionID:'event 7' })
    CREATE (e8:INFECTION { infectionID:'event 8' })
    CREATE (e9:INFECTION { infectionID:'event 9' })
    CREATE (e10:INFECTION { infectionID:'event 10' })
    
    CREATE (b1)-[:OUTPUT {time:1262304000}]->(i1)
    CREATE (b2)-[:OUTPUT {time:1262304000}]->(i2)
    CREATE (b3)-[:OUTPUT {time:1262304000}]->(i3)
    
    CREATE (i1)-[:INPUT {time:1262304000}]->(e1)
    CREATE (i2)-[:INPUT {time:1262304000}]->(e2)
    CREATE (i3)-[:INPUT {time:1262304000}]->(e3)
    
    CREATE (e1)-[:OUTPUT {time:1262304000}]->(p1)
    CREATE (e2)-[:OUTPUT {time:1262304000}]->(p2)
    CREATE (e2)-[:OUTPUT {time:1262304000}]->(p3)
    CREATE (e3)-[:OUTPUT {time:1262304000}]->(p4)
    CREATE (e3)-[:OUTPUT {time:1262304000}]->(p5)
    
    CREATE (p1)-[:INPUT {time:1267487000}]->(e4)
    CREATE (p2)-[:INPUT {time:1267487000}]->(e4)
    CREATE (p3)-[:INPUT {time:1267487000}]->(e5)
    CREATE (p4)-[:INPUT {time:1267487000}]->(e5)
    CREATE (p5)-[:INPUT {time:1267487000}]->(e5)
    
    CREATE (e4)-[:OUTPUT {time:1267487000}]->(p6)
    CREATE (e4)-[:OUTPUT {time:1267487000}]->(p7)
    CREATE (e5)-[:OUTPUT {time:1267487000}]->(p8)
    CREATE (e5)-[:OUTPUT {time:1267487000}]->(p9)
    CREATE (e5)-[:OUTPUT {time:1267487000}]->(p10)
    
    
    CREATE (p11)-[:INPUT {time:1262304000}]->(e6)
    CREATE (p12)-[:INPUT {time:1262304000}]->(e7)
    CREATE (p13)-[:INPUT {time:1262304000}]->(e8)
    
    CREATE (e6)-[:OUTPUT {time:1262304000}]->(p11)
    CREATE (e7)-[:OUTPUT {time:1262304000}]->(p14)
    CREATE (e7)-[:OUTPUT {time:1262304000}]->(p15)
    CREATE (e8)-[:OUTPUT {time:1262304000}]->(p16)
    CREATE (e8)-[:OUTPUT {time:1262304000}]->(p17)
    
    CREATE (p17)-[:INPUT {time:1267487000}]->(e8)
    CREATE (p18)-[:INPUT {time:1267487000}]->(e9)
    CREATE (p19)-[:INPUT {time:1267487000}]->(e9)
    CREATE (p20)-[:INPUT {time:1267487000}]->(e9)
    CREATE (p15)-[:INPUT {time:1267487000}]->(e10)
    
    CREATE (e9)-[:OUTPUT {time:1267487000}]->(p16)
    CREATE (e9)-[:OUTPUT {time:1267487000}]->(p17)
    CREATE (e8)-[:OUTPUT {time:1267487000}]->(p18)
    CREATE (e10)-[:OUTPUT {time:1267487000}]->(p19)
    CREATE (e10)-[:OUTPUT {time:1267487000}]->(p20)

Calling:

    CALL edu.baylor.cc(1262304000, 2592000, 1267488000)
    
Should yield:

    "/ : Until period 1264896000 Num infected 3 Newly infected 8; / : Until period 1267488000 Num infected 11 Newly infected 7; "    
    
and then :
    
    MATCH (n) 
    RETURN n.ccId, count(*) 
    ORDER BY n.ccId

Should yield: 

    ╒════════╤══════════╕
    │"n.ccId"│"count(*)"│
    ╞════════╪══════════╡
    │1       │11        │
    ├────────┼──────────┤
    │2       │7         │
    ├────────┼──────────┤
    │null    │21        │
    └────────┴──────────┘