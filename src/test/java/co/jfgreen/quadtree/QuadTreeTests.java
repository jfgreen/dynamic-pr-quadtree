package co.jfgreen.quadtree;

import org.junit.Before;
import org.junit.Test;

import static co.jfgreen.quadtree.TreeStateUtil.*;
import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;

public class QuadTreeTests {

    // Changing these will invalidate tests
    private final static int BUCKET_SIZE = 4;
    private final static int MAX_DEPTH = 4;
    private QuadTree<NamedPoint> tree;

    @Before
    public void setupTree() {
        tree = new QuadTree<>(0, 0, 100, 100, BUCKET_SIZE, MAX_DEPTH);
    }

    private NamedPoint addPoint(String name, float x, float y) {
        NamedPoint point = new NamedPoint(name, x, y);
        tree.add(point);
        return point;
    }

    @Test(expected = IllegalArgumentException.class)
    public void constructor_shouldThrowException_givenNegativeWidth() {
        new QuadTree<>(0,0, -100, 100);
    }

    @Test(expected = IllegalArgumentException.class)
    public void constructor_shouldThrowException_givenNegativeHeight() {
        new QuadTree<>(0,0, 100, -100);
    }

    @Test(expected = IllegalArgumentException.class)
    public void constructor_shouldThrowException_givenNegativeBucketSize() {
        new QuadTree<>(0,0, 100, 100, -5, 10);
    }

    @Test(expected = IllegalArgumentException.class)
    public void constructor_shouldThrowException_givenNegativeMaxDepth() {
        new QuadTree<>(0,0, 100, 100, 4, -10);
    }

    @Test(expected = QuadTreeException.class)
    public void addPoint_shouldThrowException_givenPointAlreadyAdded() {
        NamedPoint point = new NamedPoint("TestPoint", 20, 20);
        tree.add(point);
        tree.add(point);
    }

    @Test
    public void addPoint_shouldNotThrowException_givenTwoPointsInSamePosition() {
        addPoint("1", 20, 20);
        addPoint("2", 20, 20);
    }

    @Test
    public void addPoint_shouldNotThrowException_givenPointInsideTreeBounds() {
        addPoint("TestPoint", 50, 50);
    }

    @Test(expected = IllegalArgumentException.class)
    public void addPoint_shouldThrowException_givenPointOutsideTreeBounds() {
        addPoint("TestPoint", 50, 150);
    }

    @Test
    public void queryByPointRadius_shouldReturnNothing_givenAreaEncompassingNoPoint() {
        NamedPoint point = addPoint("TestPoint", 5, 20);
        assertThat(tree.queryByPointRadius(25, 75, 10), is(empty()));
    }

    @Test
    public void queryByPointRadius_shouldReturnPoint_givenAreaEncompassingPoint() {
        NamedPoint point = addPoint("TestPoint", 25, 75);
        assertThat(tree.queryByPointRadius(25, 75, 10), containsInAnyOrder(point));
    }

    @Test
    public void queryByPointRadius_shouldReturnPoints_givenAreaEncompassingSomePoints() {
        NamedPoint point1 = addPoint("TestPoint1", 10, 10);
        NamedPoint point2 = addPoint("TestPoint2", 42, 70);
        NamedPoint point3 = addPoint("TestPoint3", 95, 85);
        NamedPoint point4 = addPoint("TestPoint4", 76, 70);
        NamedPoint point5 = addPoint("TestPoint5", 88, 45);
        assertThat(tree.queryByPointRadius(80, 60, 25), containsInAnyOrder(point4, point5));
    }


    @Test
    public void queryByBoundingBox_shouldReturnNothing_givenAreaEncompassingNoPoints() {
        NamedPoint point = new NamedPoint("TestPoint", 5, 24);
        tree.add(point);
        assertThat(tree.queryByBoundingBox(10, 10, 20, 20), is(empty()));
    }

    @Test
    public void queryByBoundingBox_shouldReturnPoint_givenAreaEncompassingPoint() {
        NamedPoint point = new NamedPoint("TestPoint", 25, 18);
        tree.add(point);
        assertThat(tree.queryByBoundingBox(10, 10, 20, 20), contains(point));
    }

    @Test
    public void queryByBoundingBox_shouldReturnPoints_givenAreaEncompassingSomePoints() {
        NamedPoint point1 = addPoint("TestPoint1", 10, 10);
        NamedPoint point2 = addPoint("TestPoint2", 42, 70);
        NamedPoint point3 = addPoint("TestPoint3", 95, 85);
        NamedPoint point4 = addPoint("TestPoint4", 76, 70);
        NamedPoint point5 = addPoint("TestPoint5", 88, 45);
        assertThat(tree.queryByBoundingBox(75, 60, 25, 30), containsInAnyOrder(point3, point4));
    }

    @Test
    public void getState_shouldReturnStateOfTree() {
        // Add some points to the tree
        //TL
        NamedPoint point1 = addPoint("1", 10, 10);
        //TR
        NamedPoint point2 = addPoint("2", 88, 45);
        //BL -> TR
        NamedPoint point3 = addPoint("3", 36, 63);
        NamedPoint point4 = addPoint("4", 42, 70);
        //BL -> BL
        NamedPoint point5 = addPoint("5", 12, 89);
        NamedPoint point6 = addPoint("6", 21, 80);
        //BL -> BR
        NamedPoint point7 = addPoint("7", 30, 96);
        //BR
        NamedPoint point8 = addPoint("8", 76, 70);
        NamedPoint point9 = addPoint("9", 95, 85);

        // Retrieve state
        ImmutableNode<NamedPoint> root = tree.getState();
        ImmutableNode<NamedPoint> tl = getNode(root.getTopLeft());
        ImmutableNode<NamedPoint> tr = getNode(root.getTopRight());
        ImmutableNode<NamedPoint> bl = getNode(root.getBottomLeft());
        ImmutableNode<NamedPoint> bltr = getNode(bl.getTopRight());
        ImmutableNode<NamedPoint> blbl = getNode(bl.getBottomLeft());
        ImmutableNode<NamedPoint> blbr = getNode(bl.getBottomRight());
        ImmutableNode<NamedPoint> br = getNode(root.getBottomRight());

        // Assert state is as expected
        assertConnector(root);
        assertConnector(bl);
        assertLeaf(tl, point1);
        assertLeaf(tr, point2);
        assertLeaf(bltr, point3, point4);
        assertLeaf(blbl, point5, point6);
        assertLeaf(blbr, point7);
        assertLeaf(br, point8, point9);
    }

    @Test
    public void getState_shouldReturnCopyOfState() {
        // Add some points, capture state & ensure its correct
        NamedPoint point1 = addPoint("1", 25, 25);
        NamedPoint point2 = addPoint("2", 75, 25);
        NamedPoint point3 = addPoint("3", 75, 75);
        NamedPoint point4 = addPoint("4", 25, 75);
        NamedPoint point5 = addPoint("5", 10, 10);
        ImmutableNode<NamedPoint> state = tree.getState();
        assertConnector(state);
        assertLeaf(getNode(state.getTopLeft()), point1, point5);
        assertLeaf(getNode(state.getTopRight()), point2);
        assertLeaf(getNode(state.getBottomLeft()), point4);
        assertLeaf(getNode(state.getBottomRight()), point3);

        // Move points
        point1.moveTo(25, 75);
        point2.moveTo(75, 75);
        point3.moveTo(75, 25);
        point4.moveTo(25, 25);
        tree.update();

        // Assert capture of state has not changed.
        assertLeaf(getNode(state.getTopLeft()), point1, point5);
        assertLeaf(getNode(state.getTopRight()), point2);
        assertLeaf(getNode(state.getBottomLeft()), point4);
        assertLeaf(getNode(state.getBottomRight()), point3);
    }

    @Test
    public void update_shouldUpdateTree_givenPointsHaveMoved() {
        // Add some points
        NamedPoint point1 = addPoint("1", 25, 25);
        NamedPoint point2 = addPoint("2", 75, 25);
        NamedPoint point3 = addPoint("3", 75, 75);
        NamedPoint point4 = addPoint("4", 25, 75);
        NamedPoint point5 = addPoint("5", 10, 10);

        // Move them
        point1.moveTo(25, 75);
        point2.moveTo(75, 75);
        point3.moveTo(75, 25);
        point4.moveTo(25, 25);
        tree.update();

        // Ensure they got moved
        ImmutableNode<NamedPoint> stateAfterUpdate = tree.getState();
        assertLeaf(getNode(stateAfterUpdate.getTopLeft()), point4, point5);
        assertLeaf(getNode(stateAfterUpdate.getTopRight()), point3);
        assertLeaf(getNode(stateAfterUpdate.getBottomLeft()), point1);
        assertLeaf(getNode(stateAfterUpdate.getBottomRight()), point2);
    }

    @Test
    public void update_shouldNotUpdateTree_givenPointsHaveNotMoved() {
        NamedPoint point1 = addPoint("1", 25, 25);
        assertLeaf(tree.getState(), point1);
        tree.update();
        assertLeaf(tree.getState(), point1);
    }

    @Test
    public void queryByPointRadius_shouldReturnPoints_givenPointsMovedIntoQueryArea() {
        // Add point & make sure its outside of query range
        NamedPoint point1 = addPoint("1", 25, 25);
        assertTrue(tree.queryByPointRadius(75, 75, 5).isEmpty());
        // Move point inside query range
        point1.moveTo(72, 76);
        tree.update();
        // Assert that point is now inside query range
        assertThat(tree.queryByPointRadius(75, 75, 5), contains(point1));
    }


    @Test
    public void queryByBoundingBox_shouldReturnPoints_givenPointsMovedIntoQueryArea() {
        // Add point & make sure its outside of query range
        NamedPoint point1 = addPoint("1", 25, 25);
        assertTrue(tree.queryByBoundingBox(70, 70, 10, 10).isEmpty());
        // Move point inside query range
        point1.moveTo(72, 76);
        tree.update();
        // Assert that point is now inside query range
        assertThat(tree.queryByBoundingBox(70, 70, 10, 10), contains(point1));
    }

    @Test
    public void queryByPointRadius_shouldReturnNoPoints_givenPointsMovedOutOfQueryArea() {
        // Add point & make sure its inside of query range
        NamedPoint point1 = addPoint("1", 24, 22);
        assertThat(tree.queryByPointRadius(25, 25, 5), contains(point1));
        // Move point outside query range
        point1.moveTo(75, 75);
        tree.update();
        // Assert that point is now outside query range
        assertTrue(tree.queryByPointRadius(25, 25, 5).isEmpty());
    }

    @Test
    public void queryByBoundingBox_shouldReturnNoPoints_givenPointsMovedOutOfQueryArea() {
        // Add point & make sure its inside of query range
        NamedPoint point1 = addPoint("1", 24, 22);
        assertThat(tree.queryByBoundingBox(20, 20, 10, 10), contains(point1));
        // Move point outside query range
        point1.moveTo(75, 75);
        tree.update();
        // Assert that point is now outside query range
        assertTrue(tree.queryByBoundingBox(20, 20, 10, 10).isEmpty());
    }

    @Test
    public void queryByPointRadius_shouldReturnNoPoints_givenQueryOutsideTreeBounds() {
        assertTrue(tree.queryByPointRadius(200, 30, 10).isEmpty());
    }

    @Test
    public void queryByBoundingBox_shouldReturnNoPoints_givenQueryOutsideTreeBounds() {
        assertTrue(tree.queryByBoundingBox(200, 30, 10, 10).isEmpty());
    }

    @Test
    public void queryByPointRadius_shouldReturnAllPoints_givenQueryEngulfingTree() {
        NamedPoint point1 = addPoint("1", 20, 20);
        NamedPoint point2 = addPoint("2", 90, 75);
        NamedPoint point3 = addPoint("3", 28, 50);
        assertThat(tree.queryByPointRadius(50, 50, 100), containsInAnyOrder(point1, point2, point3));
    }

    @Test
    public void queryByBoundingBox_shouldReturnAllPoints_givenQueryEngulfingTree() {
        NamedPoint point1 = addPoint("1", 20, 20);
        NamedPoint point2 = addPoint("2", 90, 75);
        NamedPoint point3 = addPoint("3", 28, 50);
        assertThat(tree.queryByBoundingBox(-10,-10, 200, 200), containsInAnyOrder(point1, point2, point3));
    }

    @Test
    public void queryByPointRadius_shouldReturnSomePoints_givenQueryIntersectingTree() {
        NamedPoint point1 = addPoint("1", 20, 20);
        NamedPoint point2 = addPoint("2", 10, 15);
        NamedPoint point3 = addPoint("3", 17, 20);
        assertThat(tree.queryByPointRadius(-10, -10, 50), containsInAnyOrder(point1, point2, point3));
    }

    @Test
    public void queryByBoundingBox_shouldReturnSomePoints_givenQueryIntersectingTree() {
        NamedPoint point1 = addPoint("1", 20, 20);
        NamedPoint point2 = addPoint("2", 10, 15);
        NamedPoint point3 = addPoint("3", 17, 20);
        assertThat(tree.queryByBoundingBox(-10,-10, 40, 40), containsInAnyOrder(point1, point2, point3));
    }

    //TODO: Test that get state should return immutable result (Maybe test this in immutable state classes tests)

}
