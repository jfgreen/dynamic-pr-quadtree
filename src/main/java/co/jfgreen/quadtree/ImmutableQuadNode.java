package co.jfgreen.quadtree;

import java.util.Collection;

import static java.util.Collections.unmodifiableCollection;

public class ImmutableQuadNode {

    public final BoundingBox bounds;
    public final Collection<Point2D> items;
    public final Collection<ImmutableQuadNode> children;

    public ImmutableQuadNode(BoundingBox bounds, Collection<Point2D> items, Collection<ImmutableQuadNode> children) {
        this.items = unmodifiableCollection(items);
        this.children = unmodifiableCollection(children);
        this.bounds = bounds;
    }
}
