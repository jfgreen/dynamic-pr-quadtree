package co.jfgreen.quadtree.benchmark;

import co.jfgreen.quadtree.QuadTree;
import co.jfgreen.quadtree.benchmark.model.Agent;
import co.jfgreen.quadtree.benchmark.model.Swarm;
import org.openjdk.jmh.annotations.*;

import java.util.concurrent.TimeUnit;

import static co.jfgreen.quadtree.benchmark.SwarmConfig.*;

@BenchmarkMode(Mode.Throughput)
@OutputTimeUnit(TimeUnit.SECONDS)
@State(Scope.Benchmark)
public class QuadTreeBenchmark {

    private Swarm swarm;

    private final static int BUCKET_SIZE = 100;
    private final static int MAX_DEPTH = 5;

    private QuadTree<Agent> quadtree;

    @Setup
    public void setup() {
        swarm = new Swarm(WORLD_SIZE, WORLD_SIZE, AGENT_COUNT);
        quadtree = new QuadTree<>(0,0, swarm.getWidth(), swarm.getHeight(), BUCKET_SIZE, MAX_DEPTH);
        swarm.getAgents().forEach(quadtree::add);
    }

    @Benchmark
    public void testPointRadiusQuery() {
        localityCheckEachAgent(CIRCLE_RADIUS);
        swarm.tick();
        quadtree.update();
    }

    public void localityCheckEachAgent(float radius) {
        swarm.getAgents().forEach(agent -> quadtree.queryByPointRadius(agent.getX(), agent.getY(), radius));
    }


}

