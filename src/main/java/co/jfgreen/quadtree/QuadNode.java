package co.jfgreen.quadtree;

import java.util.*;
import java.util.stream.Collectors;

public class QuadNode<T extends Point2D> {

    private final Optional<QuadNode<T>> parent;
    private final BoundingBox box;
    private final int maxBucketSize;
    private final HashSet<T> points;
    private final LinkedList<QuadNode<T>> children;
    private final int depth;

    public QuadNode(BoundingBox box, int splitThreshold, int depth) {
        this(box, Optional.empty(), splitThreshold, depth);
    }

    private QuadNode(BoundingBox box, Optional<QuadNode<T>> parent, int maxBucketSize, int depth) {
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

    private boolean encloses(T point) {
        return box.contains(point.getX(), point.getY());
    }

    public boolean isRefinable() {
        return points.size() > maxBucketSize && depth > 0;
    }

    public boolean isCoursenable() {
        boolean childrenAreLeaves = children.stream().allMatch(QuadNode::isLeaf);
        int combinedChildPointCount = children.stream().mapToInt(c -> c.points.size()).sum();
        return (!isLeaf() && childrenAreLeaves) && combinedChildPointCount <= maxBucketSize;
    }

    public void distributePointsToChildren() {
        points.forEach(point -> {
            QuadNode<T> containingChild = findChildEnclosing(point).orElseThrow(() -> new RuntimeException(
                    "No suitable child for point " + point + "when refining node bounding " + box));
            containingChild.addPoint(point);
        });
        points.clear();
    }

    public void gatherPointsFromChildren() {
        List<T> childPoints = children.stream().flatMap(c -> c.points.stream()).collect(Collectors.toList());
        points.addAll(childPoints);
    }

    public void destroyChildren() {
        children.clear();
    }

    public void createChildren() {
        QuadNode<T> topLeft = createChild(box.getTopLeftQuad());
        QuadNode<T> topRight = createChild(box.getTopRightQuad());
        QuadNode<T> bottomLeft = createChild(box.getBottomLeftQuad());
        QuadNode<T> bottomRight = createChild(box.getBottomRightQuad());
        children.addAll(Arrays.asList(topLeft, topRight, bottomLeft, bottomRight));// TODO: Is this most efficient?
    }

    private QuadNode<T> createChild(BoundingBox box) {
        return new QuadNode<>(box, Optional.of(this), maxBucketSize, depth - 1);
    }

    public boolean isLeaf() {
        return children.isEmpty();
    }

    private Optional<QuadNode<T>> findChildEnclosing(T point) {
        return children.stream().filter(c -> c.encloses(point)).findFirst();
    }

    public Collection<T> getPointsOutsideBounds() {
        return points.stream().filter(p -> !encloses(p)).collect(Collectors.toList());
    }

    public Optional<QuadNode<T>> getAncestorEnclosing(T point) {
        return parent.flatMap(p -> {
            if (p.encloses(point)) {
                return Optional.of(p);
            } else {
                return p.getAncestorEnclosing(point);
            }
        });
    }

    public Optional<QuadNode<T>> findLeafEnclosing(T point) {
        if (isLeaf()) {
            if (encloses(point)) {
                return Optional.of(this);
            } else {
                return Optional.empty();
            }
        } else {
            return findChildEnclosing(point).flatMap(c -> c.findLeafEnclosing(point));
        }
    }

    public ImmutableQuadNode<T> getState() {
        Collection<ImmutableQuadNode> childState = children.stream()
                .map(QuadNode::getState)
                .collect(Collectors.toList());
        return new ImmutableQuadNode<>(box, points, childState);
    }

    public Optional<QuadNode<T>> getParent() {
        return parent;
    }

    public Collection<QuadNode<T>> childrenIntersecting(Shape area) {
        return children.stream().filter(c -> area.intersects(c.box)).collect(Collectors.toList());
    }

    public Collection<T> getPointsEnclosedBy(Shape area) {
        if (area.contains(box)) {
            return points;
        } else {
            return points.stream().filter(p -> area.contains(p.getX(), p.getY())).collect(Collectors.toList());
        }
    }

    public LinkedList<QuadNode<T>> getChildren() {
        return children;
    }

    public boolean isEmpty() {
       return points.isEmpty();
    }

}

