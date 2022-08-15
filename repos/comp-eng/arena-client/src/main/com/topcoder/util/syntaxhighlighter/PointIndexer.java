/*
 * Copyright (C) 2005 TopCoder Inc., All Rights Reserved.
 */
package com.topcoder.util.syntaxhighlighter;

/**
 * PointIndexer is a tree structure to map to the original string index.
 * It provides log(n) lookup and internal removal.
 *
 * @author WishingBone
 * @version 2.0
 * @since 2.0
 */
class PointIndexer {

    /**
     * The start index for this node.
     */
    private int index;

    /**
     * The size of the node.
     */
    private int size;

    /**
     * Left child is node is split.
     */
    private PointIndexer left = null;

    /**
     * Right child is node is split.
     */
    private PointIndexer right = null;

    /**
     * Creates a new PointIndexer with index and size.
     * The new node covers the internal [index, index + size - 1].
     *
     * @param index the start index for this node.
     * @param size the size of the node.
     */
    PointIndexer(int index, int size) {
        this.index = index;
        this.size = size;
    }

    /**
     * Get the size of the node.
     *
     * @return the size of the node.
     */
    int getSize() {
        return size;
    }

    /**
     * Lookup the original index for the position.
     *
     * @param pos the position to look up.
     *
     * @return the original index.
     */
    int get(int pos) {
        if (left == null) {
            // If node is not split, simply return the index with offset.
            return index + pos;
        } else {
            // Otherwise determine go into which child.
            if (pos < left.size) {
                return left.get(pos);
            } else {
                return right.get(pos - left.size);
            }
        }
    }

    /**
     * Remove the interval [from, to].
     *
     * @param from the start index to remove.
     * @param to the end index to remove.
     */
    void remove(int from, int to) {
        // If the node is completely removed, simply mark it and return.
        if (from == 0 && to == size - 1) {
            size = 0;
            return;
        }
        if (left == null) {
            // Remove from the left end.
            if (from == 0) {
                index += to + 1;
                size -= to + 1;
                return;
            }
            // Remove from the right end.
            if (to == size - 1) {
                size = from;
                return;
            }
            // Otherwise we need a split. Divide the size equally.
            int mid = size / 2;
            left = new PointIndexer(index, mid);
            right = new PointIndexer(index + mid, size - mid);
        }
        // The case to recurse into the left child.
        if (to >= left.size) {
            if (from >= left.size) {
                right.remove(from - left.size, to - left.size);
            } else {
                right.remove(0, to - left.size);
            }
        }
        // The case to recurse into the right child.
        if (from < left.size) {
            if (to < left.size) {
                left.remove(from, to);
            } else {
                left.remove(from, left.size - 1);
            }
        }
        // If a child is completely removed, short-curcuit the node with the other child.
        if (left.size == 0) {
            index = right.index;
            size = right.size;
            left = right.left;
            right = right.right;
        } else if (right.size == 0) {
            index = left.index;
            size = left.size;
            right = left.right;
            left = left.left;
        } else {
            size = left.size + right.size;
        }
    }

    /**
     * Find a break point within the interval [from, to].
     * A break point is the first position in the internal where index[i] != index[i - 1] + 1.
     *
     * @param from the start index to lookup.
     * @param to the end index to lookup.
     *
     * @return the break point if it exists, -1 otherwise.
     */
    int findBreakPoint(int from, int to) {
        // If node is not split, there is no break point.
        if (left == null) {
            return -1;
        }
        if (to < left.size) {
            // If the provided interval is covered by the left child, delegate.
            return left.findBreakPoint(from, to);
        } else if (from >= left.size) {
            // If the provided interval is covered by the right child, delegate.
            int pos = right.findBreakPoint(from - left.size, to - left.size);
            if (pos != -1) {
                return left.size + pos;
            }
        } else  {
            // Otherwise check if the left child has a break point.
            int pos = left.findBreakPoint(from, left.size - 1);
            if (pos != -1) {
                return pos;
            }
            // If the interval is split, check if there is a break point between the children.
            if (left.index + left.size != right.index) {
                return left.size;
            }
            // Otherwise check if the right child has a break point.
            pos = right.findBreakPoint(0, to - left.size);
            if (pos != -1) {
                return left.size + pos;
            }
        }
        // No break point found.
        return -1;
    }
}
