package co.jfgreen.quadtree;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class QuadNode {

    public final Optional<QuadNode> parent;
    private final BoundingBox boundingBox;
    private final int splitThreshold;
    private final HashSet<Point2D> points;
    private final LinkedList<QuadNode> children;

    public QuadNode(BoundingBox boundingBox, Optional<QuadNode> parent, int splitThreshold) {
        this.boundingBox = boundingBox;
        this.points = new HashSet<>();
        this.children = new LinkedList<>();
        this.parent = parent;
        this.splitThreshold = splitThreshold;
    }

    public void addPoint(Point2D point) {
        assert encloses(point) && isLeaf();
        this.points.add(point);
    }

    public void removePoint(Point2D p) {
        points.remove(p);
    }

    private boolean encloses(Point2D point) {
        return boundingBox.contains(point.getX(), point.getY());
    }

    public void refine() {
        if (points.size() > splitThreshold) {
            createChildren();
            points.forEach(point -> {
                QuadNode containingChild = findChildEnclosing(point).orElseThrow(() -> new RuntimeException(
                        "No suitable child for point " + point + "when refining node bounding " + boundingBox));
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
                List<Point2D> childPoints = children.stream().flatMap(c -> c.points.stream()).collect(Collectors.toList());
                points.addAll(childPoints);
                children.clear();
                if (points.isEmpty()) {
                    parent.ifPresent(QuadNode::coarsen);
                }
            }
        }
    }

    private void createChildren() {
        QuadNode topLeft = new QuadNode(boundingBox.getTopLeftQuad(), Optional.of(this), splitThreshold);
        QuadNode topRight = new QuadNode(boundingBox.getTopRightQuad(), Optional.of(this), splitThreshold);
        QuadNode bottomLeft = new QuadNode(boundingBox.getBottomLeftQuad(), Optional.of(this), splitThreshold);
        QuadNode bottomRight = new QuadNode(boundingBox.getBottomRightQuad(), Optional.of(this), splitThreshold);
        children.addAll(Arrays.asList(topLeft, topRight, bottomLeft, bottomRight));// TODO: Is this most efficient?
    }

    private boolean isLeaf() {
        return children.isEmpty();
    }

    private Optional<QuadNode> findChildEnclosing(Point2D point) {
        return children.stream().filter(c -> c.encloses(point)).findFirst();
    }

    public Stream<QuadNode> leaves() {
        if (isLeaf()) {
            return Stream.of(this);
        } else {
            return children.stream().flatMap(QuadNode::leaves);
        }
    }

    public Collection<Point2D> getPointsOutsideBounds() {
        return points.stream().filter(p -> !encloses(p)).collect(Collectors.toList());
    }

    public Optional<QuadNode> getAncestorEnclosing(Point2D point) {
        return parent.flatMap(p -> {
            if (p.encloses(point)) {
                return Optional.of(p);
            } else {
                return p.getAncestorEnclosing(point);
            }
        });
    }

    public Optional<QuadNode> findLeafEnclosing(Point2D point) {
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

    public ImmutableQuadNode getState() {
        Collection<ImmutableQuadNode> childState = children.stream()
                .map(QuadNode::getState)
                .collect(Collectors.toList());
        return new ImmutableQuadNode(boundingBox, points, childState);
    }

}

