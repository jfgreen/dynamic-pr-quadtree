package co.jfgreen.quadtree;

import org.junit.Test;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.*;

public class BoundingBoxTests {

    @Test
    public void contains_shouldReturnTrue_givenPointWithinBounds() {
        BoundingBox box = new BoundingBox(10, 10, 30, 30);
        assertTrue(box.contains(15, 27));
    }

    @Test
    public void contains_shouldReturnFalse_givenPointOutsideBounds() {
        BoundingBox box = new BoundingBox(10, 10, 30, 30);
        assertFalse(box.contains(15, 31));
    }

    @Test
    public void contains_shouldReturnTrue_givenPointOnBounds() {
        BoundingBox box = new BoundingBox(10, 10, 30, 30);
        assertTrue(box.contains(15, 30));
    }

    @Test
    public void getWidth_shouldReturnWidth() {
        BoundingBox box = new BoundingBox(10, 10, 30, 40);
        assertThat(box.getWidth(), is(20F));
    }

    @Test
    public void getHeight_shouldReturnHeight() {
        BoundingBox box = new BoundingBox(10, 10, 30, 40);
        assertThat(box.getHeight(), is(30F));
    }

    @Test
    public void getTopLeftQuad_shouldReturnTopLeftQuad() {
        BoundingBox box = new BoundingBox(10, 10, 20, 20);
        BoundingBox topLeftQuad = box.getTopLeftQuad();
        assertThat(topLeftQuad.startX, is(10F));
        assertThat(topLeftQuad.startY, is(10F));
        assertThat(topLeftQuad.endX, is(15F));
        assertThat(topLeftQuad.endY, is(15F));
    }


    @Test
    public void getTopRightQuad_shouldReturnTopRightQuad() {
        BoundingBox box = new BoundingBox(10, 10, 20, 20);
        BoundingBox topRightQuad = box.getTopRightQuad();
        assertThat(topRightQuad.startX, is(Math.nextUp(15F)));
        assertThat(topRightQuad.startY, is(10F));
        assertThat(topRightQuad.endX, is(20F));
        assertThat(topRightQuad.endY, is(15F));
    }

    @Test
    public void getBottomLeftQuad_shouldReturnBottomLeftQuad() {
        BoundingBox box = new BoundingBox(10, 10, 20, 20);
        BoundingBox bottomLeftQuad = box.getBottomLeftQuad();
        assertThat(bottomLeftQuad.startX, is(10F));
        assertThat(bottomLeftQuad.startY, is(Math.nextUp(15F)));
        assertThat(bottomLeftQuad.endX, is(15F));
        assertThat(bottomLeftQuad.endY, is(20F));
    }

    @Test
    public void getBottomRightQuad_shouldReturnBottomRightQuad() {
        BoundingBox box = new BoundingBox(10, 10, 20, 20);
        BoundingBox bottomRightQuad = box.getBottomRightQuad();
        assertThat(bottomRightQuad.startX, is(Math.nextUp(15F)));
        assertThat(bottomRightQuad.startY, is(Math.nextUp(15F)));
        assertThat(bottomRightQuad.endX, is(20F));
        assertThat(bottomRightQuad.endY, is(20F));
    }

    @Test
    public void toString_shouldNotThrowException() {
        BoundingBox box = new BoundingBox(10, 15, 20, 20);
        box.toString();
    }

    @Test(expected=IllegalArgumentException.class)
    public void constructor_shouldThrowException_givenNegativeWidth() {
        new BoundingBox(10, 10, 5, 20);
    }

    @Test(expected=IllegalArgumentException.class)
    public void constructor_shouldThrowException_givenNegativeHeight() {
        new BoundingBox(10, 10, 20, 5);
    }

    @Test
    public void intersects_shouldReturnTrue_givenBoxInside() {
        BoundingBox box1 = new BoundingBox(10, 10, 20, 20);
        BoundingBox box2 = new BoundingBox(15, 15, 17, 17);
        assertTrue(box1.intersects(box2));
        assertTrue(box2.intersects(box1));
    }

    @Test
    public void intersects_shouldReturnFalse_givenBoxAbove() {
        BoundingBox box1 = new BoundingBox(10, 10, 20, 20);
        BoundingBox box2 = new BoundingBox(10, 30, 20, 40);
        assertFalse(box1.intersects(box2));
        assertFalse(box2.intersects(box1));
    }

    @Test
    public void intersects_shouldReturnFalse_givenBoxToSide() {
        BoundingBox box1 = new BoundingBox(10, 10, 20, 20);
        BoundingBox box2 = new BoundingBox(30, 10, 40, 20);
        assertFalse(box1.intersects(box2));
        assertFalse(box2.intersects(box1));
    }

    @Test
    public void intersects_shouldReturnTrue_givenBoxEncompassed() {
        BoundingBox box1 = new BoundingBox(10, 10, 20, 20);
        BoundingBox box2 = new BoundingBox(11, 11, 19, 19);
        assertTrue(box1.contains(box2));
    }

    @Test
    public void intersects_shouldReturnFalse_givenBoxEncompassing() {
        BoundingBox box1 = new BoundingBox(10, 10, 20, 20);
        BoundingBox box2 = new BoundingBox(9, 9, 21, 21);
        assertFalse(box1.contains(box2));
    }

    @Test
    public void intersects_shouldReturnFalse_givenBoxIntersecting() {
        BoundingBox box1 = new BoundingBox(10, 10, 20, 20);
        BoundingBox box2 = new BoundingBox(5, 5, 15, 15);
        assertFalse(box1.contains(box2));
    }

    @Test
    public void intersects_shouldReturnFalse_givenBoxNotIntersecting() {
        BoundingBox box1 = new BoundingBox(10, 10, 20, 20);
        BoundingBox box2 = new BoundingBox(30, 30, 40, 40);
        assertFalse(box1.contains(box2));
    }
}
