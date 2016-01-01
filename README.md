## Dynamic Quadtree

This is going to be an implementation of a PR quadtree designed to handle moving points.

## TODO:

-> First and foremost. Write some tests. <-

- Consider whats in the tree and whats in the node.

- Possible update optimisations:
    1) Keep track of points, if they have moved, and if so their old and new nodes.
    2) If movement deltas are tracked, the search for the new node can be optimised.
    3) Do we search for moved points, by iterating through tree, or by tracking points?


- Wouldn't it be better to have the quadtree wait for notification and update only the objects that really moved?
  No need to access memory you won't use (at that moment).

- Idea for speedup: Implement a quadtree node factory that uses an object pool?

- What other parameters are there, (that could be defaulted) so we can do a builder pattern

- Genericify the tree.

- Currently things really break if we insert n+1 same points into a tree with bucket size n.
  Either check parent or check child when making a split decision to fix this.
  Or maybe a max depth?

- Need some way of asserting that there is either children or points but never both.

- What about some of node methods being leaf or connector only?

- Really understand the performance of the data structures being used here.

- When all is implemented, revisit methods, simplify, consolidate and optimise.

- Fix potential discrepancy between childCount() and gatherPoints().size() in node.

- Make things final where possible.

- Refactor and optimise trees update().

- Could speed up searching for children using some sneaky maths.

- Add search functionality.

