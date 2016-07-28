package co.jfgreen.quadtree.benchmark;

import co.jfgreen.quadtree.benchmark.model.Agent;
import co.jfgreen.quadtree.benchmark.model.Swarm;
import org.openjdk.jmh.annotations.*;

import java.util.Collection;
import java.util.concurrent.TimeUnit;

import static co.jfgreen.quadtree.benchmark.SwarmConfig.*;
import static java.lang.Math.abs;
import static java.lang.Math.pow;
import static java.lang.Math.sqrt;
import static java.util.stream.Collectors.toList;

@BenchmarkMode(Mode.Throughput)
@OutputTimeUnit(TimeUnit.SECONDS)
@State(Scope.Benchmark)
public class NaiveBenchmark {

    private Swarm swarm;

    @Setup
    public void setup() {
        swarm = new Swarm(WORLD_SIZE, WORLD_SIZE, AGENT_COUNT);
    }

    @Benchmark
    public void testQuadtreePointRadiusQuery() {
        localityCheckEachAgent(CIRCLE_RADIUS);
        swarm.tick();
    }

    private void localityCheckEachAgent(float radius) {
        swarm.getAgents().forEach(a -> getAgentsWithinRadius(a.getX(), a.getY(), radius));
    }

    private Collection<Agent> getAgentsWithinRadius(float x, float y, float radius) {
        return swarm.getAgents().parallelStream()
                .filter(a -> distanceBetween(a.getX(), a.getY(), x, y) <= radius).collect(toList());
    }

    private float distanceBetween(float x1, float y1, float x2, float y2) {
        return (float) sqrt(pow(abs(x1-x2), 2) + pow(abs(y1-y2), 2));
    }


}

