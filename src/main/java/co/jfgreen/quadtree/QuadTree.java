package co.jfgreen.quadtree;

import java.util.*;
import java.util.function.Function;
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
        QuadNode<T> destination = findLeafEnclosing(root, point).orElseThrow(() -> new RuntimeException(
                "No suitable node for point " + point + "when adding to tree."));
        destination.addPoint(point);
        refine(destination);
    }

    public ImmutableQuadNode<T> getState() {
        return root.getState();
    }

    public void update() {
        Set<QuadNode<T>> parentsOfVacatedNodes = new HashSet<>();
        Set<QuadNode<T>> populatedNodes = new HashSet<>();
        leaves().forEach(leaf -> leaf.getPointsOutsideBounds().forEach(p -> {
            //TODO: Optimise by searching from root in certain cases.
            QuadNode<T> ancestor = findAncestorEnclosing(leaf, p)
                    .orElseThrow(() -> new RuntimeException("No suitable ancestor for point " + p));
            QuadNode<T> newHome = findLeafEnclosing(ancestor, p)
                    .orElseThrow(() -> new RuntimeException("No suitable home for point " + p));
            leaf.removePoint(p);
            newHome.addPoint(p);
            leaf.getParent().ifPresent(parentsOfVacatedNodes::add);
            populatedNodes.add(newHome);
        }));
        populatedNodes.forEach(this::refine);
        parentsOfVacatedNodes.forEach(this::coarsen);
    }

    public Optional<QuadNode<T>> findAncestorEnclosing(QuadNode<T> node, T point) {
        Optional<QuadNode<T>> parent = node.getParent();
        while(parent.map(p -> !p.encloses(point)).orElse(false)) {
            parent = parent.flatMap(QuadNode::getParent);
        }
        return parent;
    }

    public Optional<QuadNode<T>> findLeafEnclosing(QuadNode<T> node, T point) {
        Optional<QuadNode<T>> currentNode = Optional.of(node);
        while(currentNode.map(p -> !p.isLeaf()).orElse(false)) {
            currentNode = currentNode.flatMap(c -> c.findChildEnclosing(point));
        }
        return currentNode;
    }

    private void refine(QuadNode<T> node) {
        if (node.isRefinable()) {
            node.createChildren();
            node.distributePointsToChildren();
            node.getChildren().forEach(this::refine);
        }
    }

    private void coarsen(QuadNode<T> node) {
        if (node.isCoursenable()) {
            node.gatherPointsFromChildren();
            node.destroyChildren();
            if (node.isEmpty()) {
                node.getParent().ifPresent(this::coarsen);
            }
        }
    }

    public Collection<T> queryByBoundingBox(float x, float y, float width, float height) {
        return queryByShape(new BoundingBox(x, y, x+width, y+height));
    }

    public Collection<T> queryByPointRadius(float x, float y, float radius) {
        return queryByShape(new Circle(x, y, radius));
    }

    private Collection<T> queryByShape(Shape area) {
        Set<T> foundPoints= new HashSet<>();
        traverse((node) -> {
            if (node.isLeaf()) foundPoints.addAll(node.getPointsEnclosedBy(area));
            return node.childrenIntersecting(area);
        });
        return foundPoints;
    }

    private Collection<QuadNode<T>> leaves() {
        return allNodes().stream().filter(QuadNode::isLeaf).collect(Collectors.toList());
    }

    // TODO: Do we need this function to be seperate from leaves?
    private Collection<QuadNode<T>> allNodes() {
        Collection<QuadNode<T>> nodes = new LinkedList<>();
        traverse((node) -> {
            nodes.add(node);
            return node.getChildren();
        });
        return nodes;
    }

    private void traverse(Function<QuadNode<T>, Collection<QuadNode<T>>> visitor) {
        Queue<QuadNode<T>> nodesToExplore = new LinkedList<>();
        nodesToExplore.add(root);
        while (!nodesToExplore.isEmpty()) {
            QuadNode<T> currentNode = nodesToExplore.remove();
            nodesToExplore.addAll(visitor.apply(currentNode));
        }
    }

}
