package co.jfgreen.quadtree;

import java.util.stream.Stream;

public class Children<T extends Point2D> {
    private final Node<T> topLeft;
    private final Node<T> topRight;
    private final Node<T> bottomLeft;
    private final Node<T> bottomRight;

    public Children(Node<T> topLeft, Node<T> topRight, Node<T> bottomLeft, Node<T> bottomRight) {
        this.topLeft = topLeft;
        this.topRight = topRight;
        this.bottomLeft = bottomLeft;
        this.bottomRight = bottomRight;
    }

    public Node<T> getTopLeft() {
        return topLeft;
    }

    public Node<T> getTopRight() {
        return topRight;
    }

    public Node<T> getBottomLeft() {
        return bottomLeft;
    }

    public Node<T> getBottomRight() {
        return bottomRight;
    }

    public Stream<Node<T>> stream() {
        return Stream.of(topLeft, topRight, bottomLeft, bottomRight);
    }
}