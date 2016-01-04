package co.jfgreen.quadtree;

import java.util.*;
import java.util.stream.Collectors;

public class QuadTree<T extends Point2D> {

    public final QuadNode<T> root;

    public QuadTree(float x, float y, float width, float height, int splitThreshold, int maxDepth) {
        root = new QuadNode<>(new BoundingBox(x, y, x + width, y + height), splitThreshold, maxDepth);
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
            leaf.parent.ifPresent(parentsOfVacatedNodes::add);
            populatedNodes.add(newHome);
        }));
        populatedNodes.forEach(QuadNode::refine);
        parentsOfVacatedNodes.forEach(QuadNode::coarsen);
    }

    public Collection<T> queryByBoundingBox(float startX, float startY, float endX, float endY) {
        return root.queryByBoundingBox(new BoundingBox(startX, startY, endX, endY)).collect(Collectors.toList());
    }

}
