package co.jfgreen.quadtree.benchmark.model;

import co.jfgreen.quadtree.Point2D;

import java.util.Random;

public class Agent implements Point2D {

    private static final float SPEED = 2;
    private final float width;
    private final float height;
    private final int id;
    private final Random random;
    private float x;
    private float y;
    private float direction;

    public Agent(int id, float worldWidth, float worldHeight) {
        this.random = new Random();
        this.width = worldWidth;
        this.height = worldHeight;
        this.x = random.nextFloat() * worldWidth;
        this.y = random.nextFloat() * worldHeight;
        this.direction = random.nextFloat() * (float) Math.PI * 2;
        this.id = id;
    }

    @Override
    public float getX() {
        return this.x;
    }

    @Override
    public float getY() {
        return this.y;
    }

    public void move() {
        this.x += Math.cos(direction) * SPEED;
        this.y += Math.sin(direction) * SPEED;
        if (this.x > width) {
            this.x = width;
            this.direction += Math.PI;
        }
        if (this.y > height) {
            this.y = height;
            this.direction += Math.PI;
        }
        if (this.x < 0) {
            this.x = 0;
            this.direction += Math.PI;
        }
        if (this.y < 0) {
            this.y = 0;
            this.direction += Math.PI;
        }
        direction += random.nextGaussian() * 0.1;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Agent)) return false;
        if (!super.equals(o)) return false;

        Agent otherAgent = (Agent) o;

        return id == otherAgent.id;
    }

    @Override
    public int hashCode() {
        return id;
    }
}
