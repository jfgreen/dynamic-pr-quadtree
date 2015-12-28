package co.jfgreen.quadtree;

import org.junit.Test;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

public class BoundingBoxTests {

    @Test
    public void contains_shouldReturnTrue_givenPointWithinBounds() {
        BoundingBox box = new BoundingBox(10, 10, 20, 20);
        assertThat(box.contains(15, 27), is(true));
    }

    @Test
    public void contains_shouldReturnFalse_givenPointOutsideBounds() {
        BoundingBox box = new BoundingBox(10, 10, 20, 20);
        assertThat(box.contains(15, 31), is(false));
    }

    @Test
    public void contains_shouldReturnFalse_givenPointOnLeftBoundary() {
        BoundingBox box = new BoundingBox(10, 10, 5, 5);
        assertThat(box.contains(10, 12.5f), is(false));
    }

    @Test
    public void contains_shouldReturnFalse_givenPointOnLowerBoundary() {
        BoundingBox box = new BoundingBox(10, 10, 5, 5);
        assertThat(box.contains(12.5f, 15), is(false));
    }

    @Test
    public void contains_shouldReturnTrue_givenPointOnRightBoundary() {
        BoundingBox box = new BoundingBox(10, 10, 5, 5);
        assertThat(box.contains(15, 12.5f), is(true));
    }

    @Test
    public void contains_shouldReturnTrue_givenPointOnUpperBoundary() {
        BoundingBox box = new BoundingBox(10, 10, 5, 5);
        assertThat(box.contains(12.5f, 10), is(true));
    }

    @Test
    public void getTopLeftQuad_shouldReturnTopLeftQuad_givenBox() {
        BoundingBox box = new BoundingBox(0, 0, 10, 10);
        BoundingBox topLeftQuad = box.getTopLeftQuad();
        assertThat(topLeftQuad.x, is(0F));
        assertThat(topLeftQuad.y, is(0F));
        assertThat(topLeftQuad.width, is(5F));
        assertThat(topLeftQuad.height, is(5F));
    }

    @Test
    public void getTopRightQuad_shouldReturnTopRightQuad_givenBox() {
        BoundingBox box = new BoundingBox(0, 0, 10, 10);
        BoundingBox topRightQuad = box.getTopRightQuad();
        assertThat(topRightQuad.x, is(5F));
        assertThat(topRightQuad.y, is(0F));
        assertThat(topRightQuad.width, is(5F));
        assertThat(topRightQuad.height, is(5F));
    }

    @Test
    public void getBottomLeftQuad_shouldReturnBottomLeftQuad_givenBox() {
        BoundingBox box = new BoundingBox(0, 0, 10, 10);
        BoundingBox bottomLeftQuad = box.getBottomLeftQuad();
        assertThat(bottomLeftQuad.x, is(0F));
        assertThat(bottomLeftQuad.y, is(5F));
        assertThat(bottomLeftQuad.width, is(5F));
        assertThat(bottomLeftQuad.height, is(5F));
    }

    @Test
    public void getBottomRightQuad_shouldReturnBottomRightQuad_givenBox() {
        BoundingBox box = new BoundingBox(0, 0, 10, 10);
        BoundingBox bottomRightQuad = box.getBottomRightQuad();
        assertThat(bottomRightQuad.x, is(5F));
        assertThat(bottomRightQuad.y, is(5F));
        assertThat(bottomRightQuad.width, is(5F));
        assertThat(bottomRightQuad.height, is(5F));
    }

    @Test
    public void toString_shouldNotThrowException_givenBox() {
        BoundingBox box = new BoundingBox(10, 15, 20, 20);
        box.toString();
    }

    @Test(expected=IllegalArgumentException.class)
    public void BoundingBox_shouldThrowException_givenNegativeWidth() {
        new BoundingBox(10, 15, -20, 20);
    }

    @Test(expected=IllegalArgumentException.class)
    public void BoundingBox_shouldThrowException_givenNegativeHeight() {
        new BoundingBox(10, 15, 20, -20);
    }

}
