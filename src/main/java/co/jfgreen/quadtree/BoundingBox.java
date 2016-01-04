package co.jfgreen.quadtree;

public class BoundingBox {

    public final float startX;
    public final float startY;
    public final float endX;
    public final float endY;
    private final float midX;
    private final float midY;

    public BoundingBox(float startX, float startY, float endX, float endY) {
        validateDimension(startX, endX);
        validateDimension(startY, endY);
        this.startX = startX;
        this.startY = startY;
        this.endX = endX;
        this.endY = endY;
        this.midX = startX + getWidth()/2;
        this.midY = startY + getHeight()/2;
    }

    private void validateDimension(float start, float end) {
        if (start >= end) {
            throw new IllegalArgumentException("Start of dimension must be after end.");
        }
    }

    public float getWidth() {
        return endX - startX;
    }

    public float getHeight() {
        return endY - startY;
    }

    public boolean contains(float x, float y) {
        return
                x >= startX &&
                y >= startY &&
                x <= endX &&
                y <= endY;
    }

    public BoundingBox getTopLeftQuad() {
        return new BoundingBox(startX, startY,  midX, midY);
    }

    public BoundingBox getTopRightQuad() {
        return new BoundingBox(Math.nextUp(midX), startY, endX, midY);
    }

    public BoundingBox getBottomLeftQuad() {
        return new BoundingBox(startX, Math.nextUp(midY), midX, endY);
    }

    public BoundingBox getBottomRightQuad() {
        return new BoundingBox(Math.nextUp(midX), Math.nextUp(midY), endX, endY);
    }

    public String toString() {
        return String.format("From:(%f, %f), To:(%f, %f)", startX, startY, endX, endY);
    }

    public boolean intersects(BoundingBox other) {
        return !(
                   other.startX > endX
                || other.endX < startX
                || other.startY > endY
                || other.endY < startY
        );
    }
}
