package co.jfgreen.quadtree;

import java.util.*;
import java.util.stream.Collectors;

public class QuadTree<T extends Point2D> {

    private final QuadNode<T> root;

    public static int DEFAULT_MAX_BUCKET_SIZE = 4;
    public static int DEFAULT_MAX_DEPTH = 10;

    public QuadTree(float x, float y, float width, float height) {
       this(x, y, width, height, DEFAULT_MAX_BUCKET_SIZE, DEFAULT_MAX_DEPTH);
    }

    public QuadTree(float x, float y, float width, float height, int maxBucketSize, int maxDepth) {
        BoundingBox box = new BoundingBox(x, y, x + width, y + height);
        root = new QuadNode<>(box, maxBucketSize, maxDepth);
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
            leaf.getParent().ifPresent(parentsOfVacatedNodes::add);
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

}
