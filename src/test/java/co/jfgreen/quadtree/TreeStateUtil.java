package co.jfgreen.quadtree;

import java.util.Optional;
import java.util.function.Supplier;

import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

public class TreeStateUtil {
    public static Supplier<RuntimeException> incorrectTree() {
        return () -> new RuntimeException("Incorrect Tree");
    }

    public static void assertConnector(ImmutableNode<NamedPoint> node) {
        assertHasChildren(node);
        assertTrue(node.isEmpty());
    }

    public static void assertLeaf(ImmutableNode<NamedPoint> node, NamedPoint... points) {
        assertHasNoChildren(node);
        assertFalse(node.isEmpty());
        assertThat(node.getItems(), containsInAnyOrder(points));
    }

    public static void assertHasNoChildren(ImmutableNode<NamedPoint> node) {
        assertFalse(node.getTopLeft().isPresent());
        assertFalse(node.getTopRight().isPresent());
        assertFalse(node.getBottomLeft().isPresent());
        assertFalse(node.getBottomRight().isPresent());
    }

    public static void assertHasChildren(ImmutableNode<NamedPoint> node) {
        assertTrue(node.getTopLeft().isPresent());
        assertTrue(node.getTopRight().isPresent());
        assertTrue(node.getBottomLeft().isPresent());
        assertTrue(node.getBottomRight().isPresent());
    }

    public static ImmutableNode<NamedPoint> getNode(Optional<ImmutableNode<NamedPoint>> node) {
        return node.orElseThrow(incorrectTree());
    }
}
