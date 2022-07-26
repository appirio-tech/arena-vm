/*
 * Copyright (C) 2005 TopCoder Inc., All Rights Reserved.
 */

package com.topcoder.util.syntaxhighlighter;


/**
 * <p>
 * ContentSegment represents a Segment of highlighted text. An array of ContentSegment instances are provided
 * by the HighglightedSequence class (see getOrderedSegments) to HighlightedOutput instances in order to
 * reconstruct the original string with formatting information (see Segment.getStyle()).</p>
 * <p>Thread safety: This class and its base only maintain immutable state and is thread safe.
 * </p>
 * @author duner, still
 * @version 2.0
 */
public class ContentSegment extends Segment {

    /**
     * <p>
     * Represents a segment's content (text from the start of the segment to
     * the end). This should be non-null and non-empty. This is set in the constructor and is not changed
     * afterwards.
     * </p>
     *
     */
    private final String content;

    /**
     * <p>
     * Initializes the Segment with the given start and end offsets and highlighting style.
     *
     *
     * @param start
     *            The start of the segment (>= 0 && <= end).
     * @param end
     *            The end of the segment (>= 0 && >= start).
     * @param style
     *            The TextStyle that is used to format this segment. May be null.
     * @param content
     *            A String which represents the content of the Segment. May not be null or empty (may contain
     *            only whitespace, however).
     * @throws IllegalArgumentException
     *             if start or end are outside their specified ranges or content is a zero-length String.
     * @throws NullPointerException
     *             if content is null.
     */
    public ContentSegment(int start, int end, TextStyle style, String content) {
        super(start, end, style);

        // check null or empty content
        SHHelper.checkNull(content, "content");

        if (content.length() == 0) {
            throw new IllegalArgumentException("Parameter 'content' may not be empty.");
        }
        this.content = content;
    }

    /**
     * <p>
     * Accessor for content.
     * </p>
     *
     * @return the content attribute.
     */
    public String getContent() {
        return content;
    }
}