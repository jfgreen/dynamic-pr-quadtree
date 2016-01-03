package co.jfgreen.quadtree;

import java.util.Collection;

import static java.util.Collections.unmodifiableCollection;

public class ImmutableQuadNode<T extends Point2D> {

    public final BoundingBox bounds;
    public final Collection<T> items;
    public final Collection<ImmutableQuadNode> children;

    public ImmutableQuadNode(BoundingBox bounds, Collection<T> items, Collection<ImmutableQuadNode> children) {
        this.items = unmodifiableCollection(items);
        this.children = unmodifiableCollection(children);
        this.bounds = bounds;
    }
}
