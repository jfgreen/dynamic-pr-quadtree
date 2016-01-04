package co.jfgreen.quadtree;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class QuadNode<T extends Point2D> {

    public final Optional<QuadNode<T>> parent;
    private final BoundingBox box;
    private final int splitThreshold;
    private final HashSet<T> points;
    private final LinkedList<QuadNode<T>> children;
    private final int depth;
    private final int maxDepth;

    public QuadNode(BoundingBox box, int splitThreshold, int maxDepth) {
        this(box, Optional.empty(), splitThreshold, 1, maxDepth);
    }

    private QuadNode(BoundingBox box, Optional<QuadNode<T>> parent, int splitThreshold, int depth, int maxDepth) {
        this.box = box;
        this.points = new HashSet<>();
        this.children = new LinkedList<>();
        this.parent = parent;
        this.splitThreshold = splitThreshold;
        this.depth = depth;
        this.maxDepth = maxDepth;
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

    public void refine() {
        if (points.size() > splitThreshold && depth <= maxDepth) {
            createChildren();
            points.forEach(point -> {
                QuadNode<T> containingChild = findChildEnclosing(point).orElseThrow(() -> new RuntimeException(
                        "No suitable child for point " + point + "when refining node bounding " + box));
                containingChild.addPoint(point);
            });
            points.clear();
            children.forEach(QuadNode::refine);
        }
    }

    public void coarsen() {
        if (!isLeaf() && children.stream().allMatch(QuadNode::isLeaf)) {
            int childCount = children.stream().mapToInt(c -> c.points.size()).sum();
            if (childCount <= splitThreshold) {
                List<T> childPoints = children.stream().flatMap(c -> c.points.stream()).collect(Collectors.toList());
                points.addAll(childPoints);
                children.clear();
                if (points.isEmpty()) {
                    parent.ifPresent(QuadNode::coarsen);
                }
            }
        }
    }

    private void createChildren() {
        QuadNode<T> topLeft = createChild(box.getTopLeftQuad());
        QuadNode<T> topRight = createChild(box.getTopRightQuad());
        QuadNode<T> bottomLeft = createChild(box.getBottomLeftQuad());
        QuadNode<T> bottomRight = createChild(box.getBottomRightQuad());
        children.addAll(Arrays.asList(topLeft, topRight, bottomLeft, bottomRight));// TODO: Is this most efficient?
    }

    private QuadNode<T> createChild(BoundingBox box) {
        return new QuadNode<>(box, Optional.of(this), splitThreshold, depth + 1, maxDepth);
    }

    private boolean isLeaf() {
        return children.isEmpty();
    }

    private Optional<QuadNode<T>> findChildEnclosing(T point) {
        return children.stream().filter(c -> c.encloses(point)).findFirst();
    }

    public Stream<QuadNode<T>> leaves() {
        if (isLeaf()) {
            return Stream.of(this);
        } else {
            return children.stream().flatMap(QuadNode::leaves);
        }
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

    public Stream<T> queryByBoundingBox(BoundingBox area) {
        if (isLeaf()) {
            return points.stream().filter(p -> area.contains(p.getX(), p.getY()));
        } else {
            return children.stream().filter(c -> area.intersects(c.box)).flatMap(c -> queryByBoundingBox(area));
        }

    }

}

