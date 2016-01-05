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
    public void getWidth_shouldReturnWidth_givenBox() {
        BoundingBox box = new BoundingBox(10, 10, 30, 40);
        assertThat(box.getWidth(), is(20F));
    }

    @Test
    public void getHeight_shouldReturnHeight_givenBox() {
        BoundingBox box = new BoundingBox(10, 10, 30, 40);
        assertThat(box.getHeight(), is(30F));
    }

    @Test
    public void getTopLeftQuad_shouldReturnTopLeftQuad_givenBox() {
        BoundingBox box = new BoundingBox(10, 10, 20, 20);
        BoundingBox topLeftQuad = box.getTopLeftQuad();
        assertThat(topLeftQuad.startX, is(10F));
        assertThat(topLeftQuad.startY, is(10F));
        assertThat(topLeftQuad.endX, is(15F));
        assertThat(topLeftQuad.endY, is(15F));
    }


    @Test
    public void getTopRightQuad_shouldReturnTopRightQuad_givenBox() {
        BoundingBox box = new BoundingBox(10, 10, 20, 20);
        BoundingBox topRightQuad = box.getTopRightQuad();
        assertThat(topRightQuad.startX, is(Math.nextUp(15F)));
        assertThat(topRightQuad.startY, is(10F));
        assertThat(topRightQuad.endX, is(20F));
        assertThat(topRightQuad.endY, is(15F));
    }

    @Test
    public void getBottomLeftQuad_shouldReturnBottomLeftQuad_givenBox() {
        BoundingBox box = new BoundingBox(10, 10, 20, 20);
        BoundingBox bottomLeftQuad = box.getBottomLeftQuad();
        assertThat(bottomLeftQuad.startX, is(10F));
        assertThat(bottomLeftQuad.startY, is(Math.nextUp(15F)));
        assertThat(bottomLeftQuad.endX, is(15F));
        assertThat(bottomLeftQuad.endY, is(20F));
    }

    @Test
    public void getBottomRightQuad_shouldReturnBottomRightQuad_givenBox() {
        BoundingBox box = new BoundingBox(10, 10, 20, 20);
        BoundingBox bottomRightQuad = box.getBottomRightQuad();
        assertThat(bottomRightQuad.startX, is(Math.nextUp(15F)));
        assertThat(bottomRightQuad.startY, is(Math.nextUp(15F)));
        assertThat(bottomRightQuad.endX, is(20F));
        assertThat(bottomRightQuad.endY, is(20F));
    }

    @Test
    public void toString_shouldNotThrowException_givenBox() {
        BoundingBox box = new BoundingBox(10, 15, 20, 20);
        box.toString();
    }

    @Test(expected=IllegalArgumentException.class)
    public void BoundingBox_shouldThrowException_givenNegativeWidth() {
        new BoundingBox(10, 10, 5, 20);
    }

    @Test(expected=IllegalArgumentException.class)
    public void BoundingBox_shouldThrowException_givenNegativeHeight() {
        new BoundingBox(10, 10, 20, 5);
    }

    @Test
    public void BoundingBox_shouldIntersect_givenBoxInside() {
        BoundingBox box1 = new BoundingBox(10, 10, 20, 20);
        BoundingBox box2 = new BoundingBox(15, 15, 17, 17);
        assertTrue(box1.intersects(box2));
        assertTrue(box2.intersects(box1));
    }

    @Test
    public void BoundingBox_shouldNotIntersect_givenBoxAbove() {
        BoundingBox box1 = new BoundingBox(10, 10, 20, 20);
        BoundingBox box2 = new BoundingBox(10, 30, 20, 40);
        assertFalse(box1.intersects(box2));
        assertFalse(box2.intersects(box1));
    }

    @Test
    public void BoundingBox_shouldNotIntersect_givenBoxToSide() {
        BoundingBox box1 = new BoundingBox(10, 10, 20, 20);
        BoundingBox box2 = new BoundingBox(30, 10, 40, 20);
        assertFalse(box1.intersects(box2));
        assertFalse(box2.intersects(box1));
    }

}
