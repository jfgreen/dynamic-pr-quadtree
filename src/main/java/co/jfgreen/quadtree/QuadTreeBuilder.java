package co.jfgreen.quadtree;

public class QuadTreeBuilder<T extends Point2D> {
    private float x;
    private float y;
    private float width;
    private float height;
    private int maxBucketSize = 4;
    private int maxDepth = 10;

    public QuadTreeBuilder (float x, float y, float width, float height) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
    }

    public QuadTreeBuilder<T> maxBucketSize(int maxBucketSize) {
        this.maxBucketSize = maxBucketSize;
        return this;
    }

    public QuadTreeBuilder<T> maxDepth(int maxDepth) {
        this.maxDepth = maxDepth;
        return this;
    }

    public QuadTree<T> build() {
        return new QuadTree<T>(x, y, width, height, maxBucketSize, maxDepth);
    }
}
