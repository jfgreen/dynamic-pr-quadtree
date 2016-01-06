package co.jfgreen.quadtree;

public interface Shape {
    boolean contains(float x, float y);

    boolean intersects(BoundingBox box);

    boolean contains(BoundingBox box);
}
