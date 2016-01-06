package co.jfgreen.quadtree;

public class Circle implements Shape {

    public final float x;
    public final float y;
    public final float radius;

    public Circle(float x, float y, float radius) {
        this.x = x;
        this.y = y;
        this.radius = radius;
    }


    @Override
    public boolean contains(float x, float y) {
        float dx = Math.abs(this.x - x);
        float dy = Math.abs(this.y - y);
        float distanceFromCentre = (float) Math.sqrt((dx*dx) + (dy*dy));
        return distanceFromCentre < radius;
    }


    @Override
    public boolean intersects(BoundingBox box) {

        float midpointDistanceX = Math.abs(x - box.midX);
        float midpointDistanceY = Math.abs(y - box.midY);

        if(midpointDistanceX > (box.getWidth()/2 + radius)) return false;
        if(midpointDistanceY > (box.getHeight()/2 + radius)) return false;

        if(midpointDistanceX <= (box.getWidth()/2)) return true;
        if(midpointDistanceY <= (box.getHeight()/2)) return true;

        float cornerDistanceSquared = (float) (Math.pow((midpointDistanceX - box.getWidth()/2),2) +
                                               Math.pow((midpointDistanceY - box.getHeight()/2),2));

        return cornerDistanceSquared <= Math.pow(radius,2);
    }

    @Override
    public boolean contains(BoundingBox box) {
        return
                contains(box.startX, box.startY) &&
                contains(box.endX, box.startY) &&
                contains(box.startX, box.endY) &&
                contains(box.endX, box.endY);
    }
}
