package co.jfgreen.quadtree;

import java.util.*;

public class QuadTree<T extends Point2D> {

    private final Node<T> root;

    public static int DEFAULT_MAX_BUCKET_SIZE = 4;
    public static int DEFAULT_MAX_DEPTH = 10;

    //TODO: Maintain a hashmap of point -> node? This could speedup various operations
    // Example 1: update by iterating map, then we don't have to iterate empty leaves:
    // Example 2: easy duplicate check on 'add'
    // Example 3: easy removal

    public QuadTree(float x, float y, float width, float height) {
       this(x, y, width, height, DEFAULT_MAX_BUCKET_SIZE, DEFAULT_MAX_DEPTH);
    }

    public QuadTree(float x, float y, float width, float height, int maxBucketSize, int maxDepth) {
        BoundingBox box = new BoundingBox(x, y, x + width, y + height);
        root = new Node<>(box, maxBucketSize, maxDepth);
    }

    public void add(T point) {
        if (!root.encloses(point)) {
            throw new IllegalArgumentException("Point is outside tree bounds.");
        }
        Node<T> destination = findLeafEnclosing(root, point);
        destination.addPoint(point);
        destination.refine();
    }

    public ImmutableNode<T> getState() {
        return root.getState();
    }

    public void update() {
        Set<Node<T>> parentsOfVacatedNodes = new HashSet<>();
        Set<Node<T>> populatedNodes = new HashSet<>();
        root.leaves().forEach(leaf -> leaf.getPointsOutsideBounds().forEach(p -> {
            //TODO: Optimise by searching from root in certain cases.
            Node<T> ancestor = findAncestorEnclosing(leaf, p);
            Node<T> newHome = findLeafEnclosing(ancestor, p);
            leaf.removePoint(p);
            newHome.addPoint(p);
            leaf.getParent().ifPresent(parentsOfVacatedNodes::add);
            populatedNodes.add(newHome);
        }));
        populatedNodes.forEach(Node::refine);
        parentsOfVacatedNodes.forEach(Node::coarsen);
    }

    private Node<T> findAncestorEnclosing(Node<T> node, T point) {
        return node.findAncestorEnclosing(point).
                orElseThrow(() -> new RuntimeException("No suitable ancestor for point " + point));
    }

    private Node<T> findLeafEnclosing(Node<T> node, T point) {
        return node.findLeafEnclosing(point).orElseThrow(
                () -> new RuntimeException("No suitable home for point " + point));
    }

    public Collection<T> queryByBoundingBox(float x, float y, float width, float height) {
        return root.queryByShape(new BoundingBox(x, y, x+width, y+height));
    }

    public Collection<T> queryByPointRadius(float x, float y, float radius) {
        return root.queryByShape(new Circle(x, y, radius));
    }


}
