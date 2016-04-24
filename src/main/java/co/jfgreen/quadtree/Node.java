package co.jfgreen.quadtree;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

public class Node<T extends Point2D> {

    private final int TOP_LEFT = 0;
    private final int TOP_RIGHT = 1;
    private final int BOTTOM_LEFT = 2;
    private final int BOTTOM_RIGHT = 3;

    private final Node<T> parent;
    private final BoundingBox box;
    private final int maxBucketSize;
    private final HashSet<T> points;
    private final ArrayList<Node<T>> children;
    private final int depth;

    public Node(BoundingBox box, int maxBucketSize, int depth) {
        this(box, null, maxBucketSize, depth);
    }

    private Node(BoundingBox box, Node<T> parent, int maxBucketSize, int depth) {
        this.box = box;
        this.points = new HashSet<>();
        this.children = new ArrayList<>(4);
        this.parent = parent;
        this.maxBucketSize = maxBucketSize;
        this.depth = depth;
    }

    public void addPoint(T point) {
        assert encloses(point) && isLeaf();
        this.points.add(point);
    }

    public void removePoint(T p) {
        points.remove(p);
    }

    public boolean encloses(T point) {
        return box.contains(point.getX(), point.getY());
    }

    private boolean isLeaf() {
        return children.isEmpty();
    }

    public Collection<T> getPointsOutsideBounds() {
        return points.stream().filter(p -> !encloses(p)).collect(Collectors.toList());
    }

    public ImmutableNode<T> getState() {
        if (children.isEmpty()) {
            return new ImmutableNode<T>(box, points);
        } else {
            return new ImmutableNode<T>(box, points,
                    children.get(TOP_LEFT),
                    children.get(TOP_RIGHT),
                    children.get(BOTTOM_LEFT),
                    children.get(BOTTOM_RIGHT));
        }
    }

    public Optional<Node<T>> getParent() {
        return Optional.ofNullable(parent);
    }

    public Collection<Node<T>> leaves() {
        Collection<Node<T>> leaves = new LinkedList<>();
        traverse((node) -> {
            if (node.isLeaf()) {
                leaves.add(node);
                return Collections.EMPTY_LIST;
            } else {
                return node.children;
            }
        });
        return leaves;
    }

    public Collection<T> queryByShape(Shape area) {
        Set<T> foundPoints = new HashSet<>();
        traverse((node) -> {
            if (node.isLeaf()) {
                foundPoints.addAll(node.getPointsEnclosedBy(area));
                return Collections.EMPTY_LIST;
            } else {
                return node.childrenIntersecting(area);
            }
        });
        return foundPoints;
    }

    private Collection<Node<T>> childrenIntersecting(Shape area) {
        return children.stream().filter(c -> area.intersects(c.box)).collect(Collectors.toList());
    }

    private Collection<T> getPointsEnclosedBy(Shape area) {
        if (area.contains(box)) {
            return points;
        } else {
            return points.stream().filter(p -> area.contains(p.getX(), p.getY())).collect(Collectors.toList());
        }
    }

    private void traverse(Function<Node<T>, Collection<Node<T>>> visitor) {
        Queue<Node<T>> nodesToExplore = new LinkedList<>();
        nodesToExplore.add(this);
        while (!nodesToExplore.isEmpty()) {
            Node<T> currentNode = nodesToExplore.remove();
            nodesToExplore.addAll(visitor.apply(currentNode));
        }
    }

    public void refine() {
        if (isRefinable()) {
            createChildren();
            distributePointsToChildren();
            children.forEach(Node::refine);
        }
    }

    private boolean isRefinable() {
        return points.size() > maxBucketSize && depth > 0;
    }

    private void createChildren() {
        // The order here is important
        children.add(createChild(box.getTopLeftQuad()));
        children.add(createChild(box.getTopRightQuad()));
        children.add(createChild(box.getBottomLeftQuad()));
        children.add(createChild(box.getBottomRightQuad()));
    }

    private Node<T> createChild(BoundingBox box) {
        return new Node<>(box, this, maxBucketSize, depth - 1);
    }

    private void distributePointsToChildren() {
        points.forEach(point -> {
            Node<T> containingChild = findChildEnclosing(point).orElseThrow(() -> new RuntimeException(
                    "No suitable child for point " + point + "when refining node bounding " + box));
            containingChild.addPoint(point);
        });
        points.clear();
    }

    public void coarsen() {
        if (isCoursenable()) {
            gatherPointsFromChildren();
            destroyChildren();
            if (points.isEmpty() && parent != null) {
                parent.coarsen();
            }
        }
    }

    private boolean isCoursenable() {
        boolean childrenAreLeaves = children.stream().allMatch(Node::isLeaf);
        int combinedChildPointCount = children.stream().mapToInt(c -> c.points.size()).sum();
        return (!isLeaf() && childrenAreLeaves) && combinedChildPointCount <= maxBucketSize;
    }

    private void gatherPointsFromChildren() {
        List<T> childPoints = children.stream().flatMap(c -> c.points.stream()).collect(Collectors.toList());
        points.addAll(childPoints);
    }

    private void destroyChildren() {
        children.clear();
    }

    public Optional<Node<T>> findAncestorEnclosing(T point) {
        Node<T> ancestor = parent;
        while (ancestor != null && !ancestor.encloses(point)) {
            ancestor = ancestor.parent;
        }
        return Optional.ofNullable(ancestor);
    }

    public Optional<Node<T>> findLeafEnclosing(T point) {
        Node<T> currentNode = this;
        while (currentNode != null && !currentNode.isLeaf()) {
            currentNode = currentNode.findChildEnclosing(point).orElseGet(null);
        }
        return Optional.ofNullable(currentNode);
    }

    private Optional<Node<T>> findChildEnclosing(T point) {
        return children.stream().filter(c -> c.encloses(point)).findFirst();
    }

}

