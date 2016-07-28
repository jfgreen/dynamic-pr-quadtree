package co.jfgreen.quadtree;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Node<T extends Point2D> {

    private final Node<T> parent;
    private final BoundingBox box;
    private final int maxBucketSize;
    private final HashSet<T> points;
    private Node<T> topLeft;
    private Node<T> topRight;
    private Node<T> bottomLeft;
    private Node<T> bottomRight;
    private final int depth;

    public Node(BoundingBox box, int maxBucketSize, int depth) {
        this(box, null, maxBucketSize, depth);
    }

    private Node(BoundingBox box, Node<T> parent, int maxBucketSize, int depth) {
        this.box = box;
        this.points = new HashSet<>();
        this.parent = parent;
        this.maxBucketSize = maxBucketSize;
        this.depth = depth;
    }

    public void addPoint(T point) {
        if (!encloses(point)) {
           throw new RuntimeException("Point added to node that doesn't enclose it");
        }
        if (!isLeaf()) {
            throw new RuntimeException("Point added to node that isn't a leaf");
        }
        //TODO: Once we add an index to quadtree, move this check there.
        if (points.contains(point)) {
            throw new QuadTreeException("Point already exists in tree");
        }
        this.points.add(point);
    }

    private Stream<Node<T>> children() {
        return Stream.of(topLeft, topRight, bottomLeft, bottomRight) ;
    }


    public void removePoint(T p) {
        points.remove(p);
    }

    public boolean encloses(T point) {
        return box.contains(point.getX(), point.getY());
    }

    private boolean isLeaf() {
        return topLeft == null && topRight == null && bottomLeft == null && bottomRight == null;
    }

    public Collection<T> getPointsOutsideBounds() {
        return points.stream().filter(p -> !encloses(p)).collect(Collectors.toList());
    }

    public ImmutableNode<T> getState() {
        if (isLeaf()) {
            return new ImmutableNode<T>(box, new HashSet<>(points), null, null, null, null);
        } else {
            return new ImmutableNode<T>(box, new HashSet<>(points),
                    topLeft.getState(),
                    topRight.getState(),
                    bottomLeft.getState(),
                    bottomRight.getState());
        }
    }

    public Optional<Node<T>> getParent() {
        return Optional.ofNullable(parent);
    }

    //TODO: Kinda smelly, remove and use a hashmap instead
    public Collection<Node<T>> leaves() {
        Collection<Node<T>> leaves = new LinkedList<>();
        traverse((node) -> {
            if (node.isLeaf()) {
                leaves.add(node);
                return Collections.EMPTY_LIST;
            } else {
                return node.children().collect(Collectors.toList());
            }
        });
        return leaves;
    }

    // TODO: If we remove leaves() then we dont have to have the slightly obscure traverse() here
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
        return children().filter(c -> area.intersects(c.box)).collect(Collectors.toList());
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
            children().forEach(Node::refine);
        }
    }

    private boolean isRefinable() {
        return points.size() > maxBucketSize && depth > 0;
    }

    private void createChildren() {
        topLeft = createChild(box.getTopLeftQuad());
        topRight = createChild(box.getTopRightQuad());
        bottomLeft = createChild(box.getBottomLeftQuad());
        bottomRight= createChild(box.getBottomRightQuad());
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
        boolean childrenAreLeaves = children().allMatch(Node::isLeaf);
        int combinedChildPointCount = children().mapToInt(c -> c.points.size()).sum();
        return (!isLeaf() && childrenAreLeaves) && combinedChildPointCount <= maxBucketSize;
    }

    private void gatherPointsFromChildren() {
        List<T> childPoints = children().flatMap(c -> c.points.stream()).collect(Collectors.toList());
        points.addAll(childPoints);
    }

    private void destroyChildren() {
        topLeft = null;
        topRight = null;
        bottomLeft = null;
        bottomRight = null;
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
        return children().filter(c -> c.encloses(point)).findFirst();
    }

    @Override
    public String toString() {
        return "Node{" +
                "depth=" + depth +
                ", box=" + box +
                ", points=" + points.size() +
                '}';
    }
}

