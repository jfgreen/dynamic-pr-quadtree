package co.jfgreen.quadtree;

public class BoundingBox {

    public final float x;
    public final float y;
    public final float width;
    public final float height;

    public BoundingBox(float x, float y, float width, float height) {
        validateDimension(width);
        validateDimension(height);
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
    }

    private void validateDimension(float dimension) {
        if (dimension <= 0) {
            throw new IllegalArgumentException("Dimensions must be a positive number.");
        }
    }

    public boolean contains(float px, float py) {
        //It's convention that the lower and left boundaries are closed, while the upper and right boundaries are open.
        return
            px > x &&
            py >= y &&
            px <= x + width &&
            py < y + height;
    }

    public BoundingBox getTopLeftQuad() {
        return new BoundingBox(x, y, width/2, height/2);
    }

    public BoundingBox getTopRightQuad() {
        return new BoundingBox(x + width/2, y, width/2, height/2);
    }

    public BoundingBox getBottomLeftQuad() {
        return new BoundingBox(x, y + height/2, width/2, height/2);
    }

    public BoundingBox getBottomRightQuad() {
        return new BoundingBox(x + width/2 , y + height/2, width/2, height/2);
    }

    public String toString() {
        return String.format("Position:(%f, %f), Size:(%f, %f)", x, y, width, height);
    }
}
