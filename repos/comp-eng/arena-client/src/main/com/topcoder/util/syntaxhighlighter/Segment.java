/*
 * Copyright (C) 2005 TopCoder Inc., All Rights Reserved.
 */
package com.topcoder.util.syntaxhighlighter;


/**
 * <p>
 * The Segment class serves as the base for the ContentSegment class. The Segment class is only used
 * internally by the HighlightedSequence class. This class doesn't store the content, it only store the
 * start and end position acording to the original string. The ContentSegment class extend from this class
 * stores the content.
 * </p>
 * <p>Thread Safety:
 * This class only maintains immutable state and is thread safe.
 * </p>
 * @author duner, still
 * @version 2.0
 */
class Segment implements Comparable {
    /**
     * <p>
     * Represents the start of this segment. It is guaranteed to be less than or equal to the end attribute
     * and greater than or equal to 0. This is set in the constructor and is immutable afterwards.
     * </p>
     *
     */
    private final int start;

    /**
     * <p>
     * Represents the end of this segment. It is guaranteed to be greater than or equal to the start attribute
     * and greater than or equal to 0. This is set in the constructor and is immutable afterwards.
     * </p>
     *
     */
    private final int end;

    /**
     * <p>
     * Represents the TextStyle instance that should be applied to this segment during highlighting.
     * HighlightedOutput subclasses will use this to determine the style to use when highlighting. This is set
     * in the constructor and is immutable afterwards. This may be null to indicate no style (i.e. default)
     * </p>
     *
     */
    private final TextStyle style;

    /**
     * <p>
     * Initializes the Segment with the given start and end offsets and highlighting style. Simply assign the
     * parameters to their corresponding attributes.
     *
     *
     * @param start
     *            The start of the segment (>= 0 && <= end).
     * @param end
     *            The end of the segment (>= 0 && >= start).
     * @param style
     *            The TextStyle that is used to format this segment. May be null.
     * @throws IllegalArgumentException
     *             if start or end are outside their specified ranges.
     */
    public Segment(int start, int end, TextStyle style) {
        SHHelper.checkStartEnd(start, end);

        this.start = start;
        this.end = end;
        this.style = style;
    }

    /**
     * <p>
     * Accessor for start. Simply returns the start attribute.
     *
     *
     * @return the start attribute.
     */
    public int getStart() {
        return this.start;
    }

    /**
     * <p>
     * Accessor for end. Simply returns the end attribute.
     *
     *
     * @return the end attribute.
     */
    public int getEnd() {
        return this.end;
    }

    /**
     * <p>
     * Accessor for style. Simply returns the style attribute.
     *
     *
     * @return the style attribute.
     */
    public TextStyle getStyle() {
        return this.style;
    }

    /**
     * <p>
     * Implements the sole interface method for the Comparable interface. This should return start - (
     * (Segment) obj).getStart(). This is used when ordering segments such that the one with an earlier
     * starting segment will be ordered prior to one with a later starting segment.
     *
     *
     * @param obj
     *            An object to compare to this one. This should be an instance of Segment (or a subclass).
     * @return < 0 if start comes before obj.getStart(), 0 if start is equal to obj.getStart(), > 0 otherwise.
     * @throws ClassCastException
     *             if obj is not or does not derive from Segment.
     * @throws NullPointerException if obj is null.
     */
    public int compareTo(Object obj) {
        SHHelper.checkNull(obj, "obj");

        // return the comparation of this.start and obj.start
        return start - ((Segment) obj).getStart();
    }
}
