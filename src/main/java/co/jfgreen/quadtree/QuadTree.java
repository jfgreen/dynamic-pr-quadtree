package co.jfgreen.quadtree;

import java.util.*;
import java.util.stream.Collectors;

public class QuadTree<T extends Point2D> {

    public final QuadNode root;

    public QuadTree(float x, float y, float width, float height, int splitThreshold) {
        root = new QuadNode(new BoundingBox(x, y, x + width, y + height), Optional.empty(), splitThreshold);
    }

    public void add(T point) {
        QuadNode destination = root.findLeafEnclosing(point).orElseThrow(() -> new RuntimeException(
                "No suitable node for point " + point + "when adding to tree."));
        destination.addPoint(point);
        destination.refine();
    }

    public ImmutableQuadNode getState() {
        return root.getState();
    }

    public void update() {
        Set<QuadNode> parentsOfVacatedNodes = new HashSet<>();
        Set<QuadNode> populatedNodes = new HashSet<>();
        root.leaves().forEach(leaf -> leaf.getPointsOutsideBounds().forEach(p -> {
            //TODO: Optimise by searching from root in certain cases.
            QuadNode newHome = leaf.getAncestorEnclosing(p)
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

}
