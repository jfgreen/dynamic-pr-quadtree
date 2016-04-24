package co.jfgreen.quadtree;

import java.util.Collection;
import java.util.Optional;

import static java.util.Collections.unmodifiableCollection;

public class ImmutableNode<T extends Point2D> {

    private final BoundingBox bounds;
    private final Collection<T> items;
    private final Node<T> topLeft;
    private final Node<T> topRight;
    private final Node<T> bottomLeft;
    private final Node<T> bottomRight;

    public ImmutableNode(BoundingBox bounds, Collection<T> items) {
        this(bounds, items, null, null, null, null);
    }

    public ImmutableNode(BoundingBox bounds, Collection<T> items,
                         Node<T> topLeft,
                         Node<T> topRight,
                         Node<T> bottomLeft,
                         Node<T> bottomRight) {
        this.topLeft = topLeft;
        this.topRight = topRight;
        this.bottomLeft = bottomLeft;
        this.bottomRight = bottomRight;
        this.items = items;
        this.bounds = bounds;
    }

    public BoundingBox getBounds() {
        return bounds;
    }

    public Collection<T> getItems() {
        return unmodifiableCollection(items);
    }

    public boolean isEmpty() {
        return items.isEmpty();
    }
    public Optional<ImmutableNode<T>> getTopLeft() {
       return getImmutableChild(topLeft);
    }

    public Optional<ImmutableNode<T>> getTopRight() {
        return getImmutableChild(topRight);
    }

    public Optional<ImmutableNode<T>> getBottomLeft() {
        return getImmutableChild(bottomLeft);
    }

    public Optional<ImmutableNode<T>> getBottomRight() {
        return getImmutableChild(bottomRight);
    }

    private Optional<ImmutableNode<T>> getImmutableChild(Node<T> child) {
        return Optional.ofNullable(child).map(Node::getState);
    }

}
