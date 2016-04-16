package co.jfgreen.quadtree;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

public class QuadNode<T extends Point2D> {

    private final QuadNode<T> parent;
    private final BoundingBox box;
    private final int maxBucketSize;
    private final HashSet<T> points;
    private final LinkedList<QuadNode<T>> children;
    private final int depth;

    public QuadNode(BoundingBox box, int maxBucketSize, int depth) {
        this(box, null, maxBucketSize, depth);
    }

    private QuadNode(BoundingBox box, QuadNode<T> parent, int maxBucketSize, int depth) {
        this.box = box;
        this.points = new HashSet<>();
        this.children = new LinkedList<>();
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

    public ImmutableQuadNode<T> getState() {
        Collection<ImmutableQuadNode> childState = children.stream()
                .map(QuadNode::getState)
                .collect(Collectors.toList());
        return new ImmutableQuadNode<>(box, points, childState);
    }

    public Optional<QuadNode<T>> getParent() {
        return Optional.ofNullable(parent);
    }

    public Collection<QuadNode<T>> leaves() {
        Collection<QuadNode<T>> leaves = new LinkedList<>();
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

    private Collection<QuadNode<T>> childrenIntersecting(Shape area) {
        return children.stream().filter(c -> area.intersects(c.box)).collect(Collectors.toList());
    }

    private Collection<T> getPointsEnclosedBy(Shape area) {
        if (area.contains(box)) {
            return points;
        } else {
            return points.stream().filter(p -> area.contains(p.getX(), p.getY())).collect(Collectors.toList());
        }
    }

    private void traverse(Function<QuadNode<T>, Collection<QuadNode<T>>> visitor) {
        Queue<QuadNode<T>> nodesToExplore = new LinkedList<>();
        nodesToExplore.add(this);
        while (!nodesToExplore.isEmpty()) {
            QuadNode<T> currentNode = nodesToExplore.remove();
            nodesToExplore.addAll(visitor.apply(currentNode));
        }
    }

    public void refine() {
        if (isRefinable()) {
            createChildren();
            distributePointsToChildren();
            children.forEach(QuadNode::refine);
        }
    }

    private boolean isRefinable() {
        return points.size() > maxBucketSize && depth > 0;
    }

    private void createChildren() {
        QuadNode<T> topLeft = createChild(box.getTopLeftQuad());
        QuadNode<T> topRight = createChild(box.getTopRightQuad());
        QuadNode<T> bottomLeft = createChild(box.getBottomLeftQuad());
        QuadNode<T> bottomRight = createChild(box.getBottomRightQuad());
        children.addAll(Arrays.asList(topLeft, topRight, bottomLeft, bottomRight));// TODO: Is this most efficient?
    }

    private QuadNode<T> createChild(BoundingBox box) {
        return new QuadNode<>(box, this, maxBucketSize, depth - 1);
    }

    private void distributePointsToChildren() {
        points.forEach(point -> {
            QuadNode<T> containingChild = findChildEnclosing(point).orElseThrow(() -> new RuntimeException(
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
        boolean childrenAreLeaves = children.stream().allMatch(QuadNode::isLeaf);
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

    public Optional<QuadNode<T>> findAncestorEnclosing(T point) {
        QuadNode<T> ancestor = parent;
        while (ancestor != null && !ancestor.encloses(point)) {
            ancestor = ancestor.parent;
        }
        return Optional.ofNullable(ancestor);
    }

    public Optional<QuadNode<T>> findLeafEnclosing(T point) {
        QuadNode<T> currentNode = this;
        while (currentNode != null && !currentNode.isLeaf()) {
            currentNode = currentNode.findChildEnclosing(point).orElseGet(null);
        }
        return Optional.ofNullable(currentNode);
    }

    private Optional<QuadNode<T>> findChildEnclosing(T point) {
        return children.stream().filter(c -> c.encloses(point)).findFirst();
    }

}

