package co.jfgreen.quadtree;

import org.junit.Test;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.*;

public class NamedPointTests {

    @Test
    public void getName_shouldReturnName() {
        NamedPoint point = new NamedPoint("TestPoint1", 10, 20);
        assertThat(point.getName(), is("TestPoint1"));
    }

    @Test
    public void getX_shouldReturnX() {
        NamedPoint point = new NamedPoint("TestPoint1", 10, 20);
        assertThat(point.getX(), is(10F));
    }

    @Test
    public void getY_shouldReturnY() {
        NamedPoint point = new NamedPoint("TestPoint1", 10, 20);
        assertThat(point.getY(), is(20F));
    }

    @Test
    public void move_shouldMovePoint() {
        NamedPoint point = new NamedPoint("TestPoint1", 10, 20);
        assertThat(point.getX(), is(10F));
        assertThat(point.getY(), is(20F));

        point.moveTo(20, 30);

        assertThat(point.getX(), is(20F));
        assertThat(point.getY(), is(30F));
    }

    @Test
    public void move_shouldNotChangeHashCode() {
        NamedPoint point = new NamedPoint("TestPoint1", 10, 20);
        int hashCodeBeforeMove = point.hashCode();
        point.moveTo(20, 30);
        int hashCodeAfterMove = point.hashCode();
        assertThat(hashCodeBeforeMove, is(hashCodeAfterMove));
    }

}
