package co.jfgreen.quadtree;

import java.util.Collection;
import java.util.Optional;

import static java.util.Collections.unmodifiableCollection;

public class ImmutableNode<T extends Point2D> {

    private final BoundingBox bounds;
    private final Collection<T> items;
    private final ImmutableNode<T> topLeft;
    private final ImmutableNode<T> topRight;
    private final ImmutableNode<T> bottomLeft;
    private final ImmutableNode<T> bottomRight;

    public ImmutableNode(BoundingBox bounds, Collection<T> items,
                         ImmutableNode<T> topLeft,
                         ImmutableNode<T> topRight,
                         ImmutableNode<T> bottomLeft,
                         ImmutableNode<T> bottomRight) {
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
       return Optional.ofNullable(topLeft);
    }

    public Optional<ImmutableNode<T>> getTopRight() {
        return Optional.ofNullable(topRight);
    }

    public Optional<ImmutableNode<T>> getBottomLeft() {
        return Optional.ofNullable(bottomLeft);
    }

    public Optional<ImmutableNode<T>> getBottomRight() {
        return Optional.ofNullable(bottomRight);
    }


}
