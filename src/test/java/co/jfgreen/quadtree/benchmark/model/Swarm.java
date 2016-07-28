package co.jfgreen.quadtree.benchmark.model;

import co.jfgreen.quadtree.benchmark.model.Agent;

import java.util.Collection;

import static java.util.stream.Collectors.toList;
import static java.util.stream.IntStream.range;

public class Swarm {

    //TODO Make Agent an interface and generify this.
    private final Collection<Agent> agents;
    private float width;
    private float height;

    public Swarm(float width, float height, int agentCount) {
        this.width = width;
        this.height = height;
        agents = range(0, agentCount).mapToObj(n -> new Agent(n, width, height)).collect(toList());
    }

    public void tick() {
        agents.forEach(Agent::move);
    }

    public Collection<Agent> getAgents() {
        return agents;
    }

    public float getWidth() {
        return width;
    }

    public float getHeight() {
        return height;
    }
}
