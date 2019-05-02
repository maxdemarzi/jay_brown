package edu.baylor;

import com.github.benmanes.caffeine.cache.Caffeine;
import com.github.benmanes.caffeine.cache.LoadingCache;
import edu.baylor.results.StringResult;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.logging.Log;
import org.neo4j.procedure.*;
import org.roaringbitmap.longlong.Roaring64NavigableMap;

import java.util.ArrayList;
import java.util.concurrent.TimeUnit;
import java.util.stream.Stream;

import static edu.baylor.schema.Properties.TIME;

public class Procedures {

    // This field declares that we need a GraphDatabaseService
    // as context when any procedure in this class is invoked
    @Context
    public GraphDatabaseService db;

    // This gives us a log instance that outputs messages to the
    // standard log, normally found under `data/log/console.log`
    @Context
    public Log log;

    public static final String runname = "edu.baylor.cc";

    // Cache INPUT/OUTPUT times
    static LoadingCache<Long, Long> times = Caffeine.newBuilder()
            .build(Procedures::getTimes);

    private static Long getTimes(Long relationshipId) {
        return ((Number) dbapi.getRelationshipById(relationshipId).getProperty(TIME, Long.MAX_VALUE)).longValue();
    }

    static Roaring64NavigableMap seenRels = new Roaring64NavigableMap();

    private static GraphDatabaseService dbapi;

    @Procedure(name = "edu.baylor.cc", mode = Mode.WRITE)
    @Description("CALL edu.baylor.cc(from, to, end) - find connected components between timestamps")
    public Stream<StringResult> cc(@Name("time") Number time, @Name("interval") Number interval, @Name("endtime") Number endtime) throws InterruptedException {
        if (dbapi == null) {
            dbapi = db;
        }
        // Clean up seen Rels Cache per query
        Procedures.seenRels.clear();
        Procedures.seenRels.runOptimize();

        long start = System.nanoTime();
        ArrayList<String> stringsToPrint = new ArrayList<>();

        Thread t1 = new Thread(new CCRunnable(db, log, time.longValue(), interval.longValue(), endtime.longValue(), stringsToPrint));
        t1.start();
        t1.join();

        StringBuilder outputString = new StringBuilder();
        for (String subString : stringsToPrint) {
            outputString.append(subString);
        }

        return Stream.of(new StringResult(outputString.toString()));

    }

}
