package co.jfgreen.quadtree;

import java.util.*;
import java.util.stream.Collectors;

public class QuadTree {

    public final QuadNode root;

    public QuadTree(float x, float y, float width, float height, int splitThreshold) {
        root = new QuadNode(new BoundingBox(x, y, x + width, y + height), Optional.empty(), splitThreshold);
    }

    public void add(Point2D point) {
        assert root.encloses(point);
        QuadNode destination = root.findLeafEnclosing(point).orElseThrow(() -> new RuntimeException(
                "No suitable node for point " + point + "when adding to tree."));
        destination.addPoint(point);
        destination.refine();
    }

    public ImmutableQuadNode getState() {
        return root.getState();
    }

    public void update() {
        // Rehouse all points that have moved outside their nodes bounding region, keeping track of old and new.
        // For all nodes that have received a new point, if needed, refine detail and create children.
        // For all nodes that have lost a point, if needed, coarsen detail and cull children.
        Set<QuadNode> parentsOfVacatedNodes = new HashSet<>();
        Set<QuadNode> populatedNodes = new HashSet<>();
        root.leaves().forEach(leaf -> {
            //TODO: Can we speed things up by the tree being notified that a point has moved.
            //Implement both?
            //TODO: This is needed, without it we get a concurrent modification excpetion. But why?
            Collection<Point2D> pointsOutsideBound = leaf.pointsOutsideBounds().collect(Collectors.toList());
            pointsOutsideBound.forEach(p -> {
                //TODO: Optimise by searching from root in certain cases.
                QuadNode newHome = leaf.getAncestorEnclosing(p)
                        .orElseThrow(() -> new RuntimeException("No suitable ancestor for point " + p))
                        .findLeafEnclosing(p)
                        .orElseThrow(() -> new RuntimeException("No suitable home for point " + p));
                leaf.removePoint(p);
                newHome.addPoint(p);
                leaf.parent.ifPresent(parentsOfVacatedNodes::add); // TODO: Optimise this? Only need to add it once
                populatedNodes.add(newHome);
            });
        });
        populatedNodes.forEach(QuadNode::refine);
        parentsOfVacatedNodes.forEach(QuadNode::coarsen);
    }

}
