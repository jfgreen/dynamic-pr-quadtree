package co.jfgreen.quadtree;

import org.junit.Before;
import org.junit.Test;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;

public class QuadTreeTest {

    QuadTree<NamedPoint> tree;

    @Before
    public void setupTree() {
        tree = new QuadTree<>(0,0, 100, 100);
    }

    @Test
    public void queryByPointRadius_shouldReturnNothing_givenAreaEncompassingNoPoint() {
        NamedPoint point = new NamedPoint("TestPoint", 5, 20);
        tree.add(point);
        assertThat(tree.queryByPointRadius(25, 75, 10), is(empty()));
    }

    @Test
    public void queryByPointRadius_shouldReturnPoint_givenAreaEncompassingPoint() {
        NamedPoint point = new NamedPoint("TestPoint", 25, 75);
        tree.add(point);
        assertThat(tree.queryByPointRadius(25, 75, 10), containsInAnyOrder(point));
    }

    @Test
    public void queryByPointRadius_shouldReturnPoints_givenAreaEncompassingSomePoints() {
        NamedPoint point1 = new NamedPoint("TestPoint1", 10,10);
        NamedPoint point2 = new NamedPoint("TestPoint2", 42,70);
        NamedPoint point3 = new NamedPoint("TestPoint3", 95,85);
        NamedPoint point4 = new NamedPoint("TestPoint4", 76, 70);
        NamedPoint point5 = new NamedPoint("TestPoint5", 88, 45);
        tree.add(point1);
        tree.add(point2);
        tree.add(point3);
        tree.add(point4);
        tree.add(point5);
        assertThat(tree.queryByPointRadius(80, 60, 25), containsInAnyOrder(point4, point5));
    }


    @Test
    public void queryByBoundingBox_shouldReturnNothing_givenAreaEncompassingNoPoints() {
        NamedPoint point = new NamedPoint("TestPoint", 5, 20);
        tree.add(point);
        assertThat(tree.queryByBoundingBox(10, 10, 20, 20), is(empty()));
    }


}

/*

Things to test:

- Single point.
- Multi point.
- Query by point radius.
- Query by box.
- Updating. - moving in and out of regions

- Point outside tree
- Area outside tree



For each try a variety of cases.



 */