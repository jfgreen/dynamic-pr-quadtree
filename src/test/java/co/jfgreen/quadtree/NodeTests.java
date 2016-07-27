package co.jfgreen.quadtree;

import org.junit.Test;
import static junit.framework.TestCase.assertTrue;

public class NodeTests {

    private final static int BUCKET_SIZE = 3;
    private final static int DEPTH = 3;
    private final static BoundingBox BOX = new BoundingBox(0,0,100,100);

    @Test
    public void coarsen_shouldNoNothing_givenNodeIsEmptyRoot() {
        Node node = new Node(BOX, BUCKET_SIZE, DEPTH);

        node.coarsen();

        ImmutableNode nodeState = node.getState();
        assertTrue(nodeState.isEmpty());
    }

    //TODO: Test more cases here.

}
