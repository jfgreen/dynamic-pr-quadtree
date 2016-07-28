package co.jfgreen.quadtree.benchmark;

import co.jfgreen.quadtree.QuadTree;
import co.jfgreen.quadtree.benchmark.model.Agent;
import co.jfgreen.quadtree.benchmark.model.Swarm;


import static co.jfgreen.quadtree.benchmark.SwarmConfig.*;

public class ProfileSimulation {

    private Swarm swarm;

    private final static int BUCKET_SIZE = 100;
    private final static int MAX_DEPTH = 5;

    private final static int ITERATIONS = 100000;

    private QuadTree<Agent> quadtree;

    public ProfileSimulation() {
        swarm = new Swarm(WORLD_SIZE, WORLD_SIZE, AGENT_COUNT);
        quadtree = new QuadTree<>(0,0, swarm.getWidth(), swarm.getHeight(), BUCKET_SIZE, MAX_DEPTH);
        swarm.getAgents().forEach(quadtree::add);
    }

    public void go() {
        for(int i = 0; i < ITERATIONS; i++) {
            localityCheckEachAgent(CIRCLE_RADIUS);
            swarm.tick();
            quadtree.update();
        }
    }

    public void localityCheckEachAgent(float radius) {
        swarm.getAgents().forEach(agent -> quadtree.queryByPointRadius(agent.getX(), agent.getY(), radius));
    }

    public static void main(String[] args) {
        (new ProfileSimulation()).go();
    }

}

