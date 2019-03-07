package edu.baylor;

import edu.baylor.results.StringResult;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.logging.Log;
import org.neo4j.procedure.*;

import java.util.concurrent.TimeUnit;
import java.util.stream.Stream;

public class Procedures {

    // This field declares that we need a GraphDatabaseService
    // as context when any procedure in this class is invoked
    @Context
    public GraphDatabaseService db;

    // This gives us a log instance that outputs messages to the
    // standard log, normally found under `data/log/console.log`
    @Context
    public Log log;

    @Procedure(name = "edu.baylor.cc", mode = Mode.WRITE)
    @Description("CALL edu.baylor.cc(from, to) - find connected components between timestamps")
    public Stream<StringResult> cc(@Name("time") Number time, @Name("interval") Number interval) throws InterruptedException {
        long start = System.nanoTime();

        Thread t1 = new Thread(new CCRunnable(db, log, time.longValue(), interval.longValue()));
        t1.start();
        t1.join();

        return Stream.of(new StringResult("Connected Components calculated in " + TimeUnit.NANOSECONDS.toSeconds(System.nanoTime() - start) + " seconds"));

    }

}
