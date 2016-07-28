package co.jfgreen.quadtree.benchmark;

import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;
import org.openjdk.jmh.runner.options.TimeValue;


public class SwarmBenchmarks {

    public static void main(String... args) throws RunnerException {
        Options runOptions = new OptionsBuilder().
                include(NaiveBenchmark.class.getName()).
                include(QuadTreeBenchmark.class.getName())
                .forks(1)
                .threads(1)
                .warmupTime(TimeValue.seconds(5))
                .warmupIterations(3)
                .measurementTime(TimeValue.seconds(5))
                .measurementIterations(5)
                //.addProfiler(GCProfiler.class)
                //.addProfiler(StackProfiler.class)
                .build();
        Runner runner = new Runner(runOptions);
        runner.run();
    }

}
