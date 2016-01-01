package co.jfgreen.quadtree;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class QuadNode {

    private final BoundingBox boundingBox;
    private final int splitThreshold;
    public final Optional<QuadNode> parent;
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

    public boolean encloses(Point2D point) {
        return boundingBox.contains(point.getX(), point.getY());
    }

    private int itemCount() {
        return points.size();
    }

   // Transformation

    public void refine() {
        if (itemCount() > splitThreshold) {
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
            if (childCount() <= splitThreshold) {
                points.addAll(gatherPoints().collect(Collectors.toList()));
                children.clear();
                if (points.isEmpty()) {
                    parent.ifPresent(QuadNode::coarsen);
                }
            }
        }
    }

    // WORKING DIRECTLY WITH CHILDREN

    private void createChildren() {
        QuadNode topLeft = new QuadNode(boundingBox.getTopLeftQuad(), Optional.of(this), splitThreshold);
        QuadNode topRight = new QuadNode(boundingBox.getTopRightQuad(), Optional.of(this), splitThreshold);
        QuadNode bottomLeft = new QuadNode(boundingBox.getBottomLeftQuad(), Optional.of(this), splitThreshold);
        QuadNode bottomRight = new QuadNode(boundingBox.getBottomRightQuad(), Optional.of(this), splitThreshold);
        children.addAll(Arrays.asList(topLeft, topRight, bottomLeft, bottomRight));// TODO: Is this most efficient?
    }

    private int childCount() {
        return children.stream().mapToInt(QuadNode::itemCount).sum();
    }

    public boolean isLeaf() {
        return children.isEmpty();
    }

    private Optional<QuadNode> findChildEnclosing(Point2D point) {
        return children.stream().filter(c -> c.encloses(point)).findFirst();
    }

    // Recursive stuff

    private Stream<Point2D> gatherPoints() {
        if (isLeaf()) {
            return points.stream();
        } else {
            return children.stream().flatMap(QuadNode::gatherPoints);
        }
    }

    public Stream<QuadNode> leaves() {
        if (isLeaf()) {
            return Stream.of(this);
        } else {
            return children.stream().flatMap(QuadNode::leaves);
        }
    }

    public Stream<Point2D> pointsOutsideBounds() {
        return points.stream().filter(p -> !encloses(p));
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

