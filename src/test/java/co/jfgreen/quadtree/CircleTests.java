package co.jfgreen.quadtree;

import org.junit.Test;

import static org.junit.Assert.*;

public class CircleTests {

    @Test
    public void contains_shouldReturnTrue_givenPointInsideRadius() {
        Circle circle = new Circle(10,10, 100);
        assertTrue(circle.contains(10, 50));
    }

    @Test
    public void contains_shouldReturnFalse_givenPointOutsideRadius() {
        Circle circle = new Circle(10,10, 100);
        assertFalse(circle.contains(10, 115));
    }

    @Test
    public void contains_shouldReturnTrue_givenPointOnPerimeter() {
        Circle circle = new Circle(0,0, 100);
        assertTrue(circle.contains(0, 100));
    }

    @Test
    public void contains_shouldReturnTrue_givenBoxEncompassed() {
        Circle circle = new Circle(200, 200, 50);
        BoundingBox box = new BoundingBox(170,170, 230, 230);
        assertTrue(circle.contains(box));
    }

    @Test
    public void contains_shouldReturnFalse_givenBoxWithIntersectingCorners() {
        Circle circle = new Circle(200, 200, 50);
        BoundingBox box = new BoundingBox(160,160, 240, 240);
        assertFalse(circle.contains(box));
    }

    @Test
    public void contains_shouldReturnFalse_givenBoxIntersecting() {
        Circle circle = new Circle(200, 200, 50);
        BoundingBox box = new BoundingBox(170,140, 220, 210);
        assertFalse(circle.contains(box));
    }

    @Test
    public void contains_shouldReturnFalse_givenBoxOutside() {
        Circle circle = new Circle(200, 200, 50);
        BoundingBox box = new BoundingBox(100,100, 150, 150);
        assertFalse(circle.contains(box));
    }

}
