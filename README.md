## Dynamic Quadtree

This is going to be an implementation of a PR quadtree designed to handle moving points.

## TODO:

- Finish writing unit tests.
- Implement some benchmarks vs naive n^2 and Apache SIS
- Then use these to experiment with the following optimisations. For each, either include it outright,
or add it as a configurable feature. Optimisations to consider:
    * Possible update optimisations:
        1) Keep track of points, if they have moved, and if so their old and new nodes.
        2) If movement deltas are tracked, the search for the new node can be optimised.
        3) Do we search for moved points, by iterating through tree, or by tracking points?
    * Wouldn't it be better to have the quadtree wait for notification and update only the objects that really moved?
      No need to access memory you won't use (at that moment).
    * Idea for speedup: Implement a quadtree node factory that uses an object pool?
    * Refactor and optimise trees update().
    * Could speed up searching for children using some sneaky maths.
    * Keep track of points to quadnode (make searching for nn faster? allow easy removal? iterating through populated leaves?)
    * Carefully consider what should return a stream and what should be a collection.
    * Really understand the performance of the data structures being used here.
    * Prune empty nodes.
- Then when all that is done, write javadoc.
