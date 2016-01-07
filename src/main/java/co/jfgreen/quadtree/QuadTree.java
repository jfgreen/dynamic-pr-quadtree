package co.jfgreen.quadtree;

import java.util.*;
import java.util.stream.Collectors;

public class QuadTree<T extends Point2D> {

    private final QuadNode<T> root;

    private QuadTree(float x, float y, float width, float height, int maxBucketSize, int maxDepth) {
        root = new QuadNode<>(new BoundingBox(x, y, x + width, y + height), maxBucketSize, maxDepth);
    }

    public void add(T point) {
        QuadNode<T> destination = root.findLeafEnclosing(point).orElseThrow(() -> new RuntimeException(
                "No suitable node for point " + point + "when adding to tree."));
        destination.addPoint(point);
        destination.refine();
    }

    public ImmutableQuadNode<T> getState() {
        return root.getState();
    }

    public void update() {
        Set<QuadNode<T>> parentsOfVacatedNodes = new HashSet<>();
        Set<QuadNode<T>> populatedNodes = new HashSet<>();
        root.leaves().forEach(leaf -> leaf.getPointsOutsideBounds().forEach(p -> {
            //TODO: Optimise by searching from root in certain cases.
            QuadNode<T> newHome = leaf.getAncestorEnclosing(p)
                    .orElseThrow(() -> new RuntimeException("No suitable ancestor for point " + p))
                    .findLeafEnclosing(p)
                    .orElseThrow(() -> new RuntimeException("No suitable home for point " + p));
            leaf.removePoint(p);
            newHome.addPoint(p);
            leaf.parent.ifPresent(parentsOfVacatedNodes::add);
            populatedNodes.add(newHome);
        }));
        populatedNodes.forEach(QuadNode::refine);
        parentsOfVacatedNodes.forEach(QuadNode::coarsen);
    }

    public Collection<T> queryByBoundingBox(float x, float y, float width, float height) {
        return root.queryByShape(new BoundingBox(x, y, x+width, y+height)).collect(Collectors.toList());
    }

    public Collection<T> queryByPointRadius(float x, float y, float radius) {
        return root.queryByShape(new Circle(x, y, radius)).collect(Collectors.toList());
    }

    public static class Builder {

        private float x;
        private float y;
        private float width;
        private float height;
        private int maxBucketSize = 4;
        private int maxDepth = 10;

        public Builder (float x, float y, float width, float height) {
            this.x = x;
            this.y = y;
            this.width = width;
            this.height = height;
        }

        public Builder maxBucketSize(int maxBucketSize) {
            this.maxBucketSize = maxBucketSize;
            return this;
        }

        public Builder maxDepth(int maxDepth) {
            this.maxDepth = maxDepth;
            return this;
        }

        public QuadTree build() {
            return new QuadTree(x, y, width, height, maxBucketSize, maxDepth);
        }
    }


}
