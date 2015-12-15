package co.jfgreen.quadtree;

public class BoundingBox {

    public final float x;
    public final float y;
    public final float width;
    public final float height;

    public BoundingBox(float x, float y, float width, float height) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
    }

    public boolean contains(float px, float py) {
        return
            px >= x &&
            py >= y &&
            px <= x + width &&
            py <= y + height;
    }

    public BoundingBox getTopLeftQuadrant() {
        return new BoundingBox(x, y, width/2, height/2);
    }

    public BoundingBox getTopRightQuadrant() {
        return new BoundingBox(x + width/2, y, width/2, height/2);
    }

    public BoundingBox getBottomLeftQuadrant() {
        return new BoundingBox(x, y + height/2, width/2, height/2);
    }

    public BoundingBox getBottomRightQuadrant() {
        return new BoundingBox(x + width/2 , y + height/2, width/2, height/2);
    }

    public String toString() {
        return String.format("Position:(%f, %f), Size:(%f, %f)", x, y, width, height);
    }
}
