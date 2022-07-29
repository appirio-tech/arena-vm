/*
 * Copyright (C) 2005 TopCoder Inc., All Rights Reserved.
 */
package com.topcoder.util.syntaxhighlighter;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;


/**
 * <p>
 * The HighlightedSequence class is the main driver in parsing and highlighting text. This class provides a
 * sequence of characters to the Matcher class in order to support Java regular expressions. There are two 
 * highlight methods provided. For performance reasons, the highlight method which takes two lists is 
 * recommended. Conducting a set of removals at once is far more efficient than doing them one by one.
 * Calling the other method instead of this one is equivalent to using a series of strings to make changes to a
 * base string rather than taking advantage of the efficiency of StringBuffer. The getOrderedSegments() method is
 * the key to providing data to the output plugins as it constructs a contiguous set of ContentSegments which can
 * be applied in order to form a new highlighted document.
 * </p>
 * <p>
 * Thread Safety: This class is most definitely not thread safe. Concurrent calls to getNextToken() would be
 * harmful.
 * </p>
 *
 * Changes by WB:
 * - Used segmentation tree (PointIndexer) to replace the original list besed index.
 * - Delegated break point discovery to PointIndexer.
 * - Removed lastIndex modifiers for better encapsulation.
 * - Fixed lastIndex updating bug.
 *
 * @author duner, still
 * @author WishingBone
 * @version 2.0
 */
public class HighlightedSequence implements CharSequence {
    /**
     * <p>
     * This represents the original String that is to be highlighted. This is set in the constructor and is
     * unchanged afterwards. It may not be null or empty.
     * </p>
     *
     */
    private final String originalString;

    /**
     * <p>
     * This represents the buffer used to store an intermediate version of the sequence . This is initialized in
     * the constructor, is guaranteed to be non-null and is changed in the highlight(...) methods as described
     * above.
     * </p>
     *
     */
    private StringBuffer buffer;

    /**
     * This represents the mirror of buffer with orginal index rather than content(buffer with content).
     *
     */
    private PointIndexer indexer;

    /**
     * <p>
     * Represents the list of segments removed from the sequence. Each newly removed segment should be placed on
     * "top" of the List. Processing the list backwards allows the original string to be reconstructed based on
     * positioning relative to the sequence at the time of removal. This is set in the constructor and is not
     * changed afterwards, though its contents are modified in the highlight(...) methods.
     * </p>
     *
     */
    private final List segments;

    /**
     * <p>
     * This variable keeps track of the last position used in the getNextToken() method. This is used to keep
     * track of where in the document to begin searching for the next token. This becomes invalid once the
     * sequence has been modified by a call to highlight(...) and should be reset using the reset() method.
     * This is initially zero to signal the beginning of the string and will change with each call to
     * getNextToken() unless the end of the string has been reached.
     * </p>
     *
     */
    private int lastIndex = 0;

    /**
     * <p>
     * This variable, when set, contains the current <code>buffer</code> content.
     * It acts like a buffer content cache, avoiding unnecessary garbage generation.
     * It is set to <code>null</code> every time the buffer changes and set to <code>buffer.toString()</code> 
     * when toString method is called, only if it was <code>null</code>.
     * </p>
     */
    private String bufferContent;

    /**
     * <p>
     * Intializes the HighlightedSequence with the given source string.
     * </p>
     *
     *
     * @param original
     *            The original String that is to be highlighted. Must not be null.
     * @throws NullPointerException
     *             if original is null.
     * @throws IllegalArgumentException if (trimed) orginal is empty
     */
    public HighlightedSequence(String original) {
        SHHelper.checkString(original, "original");
        this.originalString = original;
        this.buffer = new StringBuffer(original);
        this.segments = new ArrayList();
        this.indexer = new PointIndexer(0, original.length());
    }

    /**
     * <p>
     * Returns the character at the specified index.
     * </p>
     *
     *
     * @param index
     *            the index of the character to be removed.
     * @return the specified character at the given index.
     * @throws IndexOutOfBoundsException
     *             if the index argument is negative or not less than length().
     */
    public char charAt(int index) {
        return buffer.charAt(index);
    }

    /**
     * <p>
     * Returns the length of this character sequence.
     * </p>
     *
     *
     * @return the number of characters in this sequence.
     */
    public int length() {
        return buffer.length();
    }

    /**
     * <p>
     * Returns a new character sequence that is a subsequence of this sequence.
     * </p>
     *
     *
     * @param start
     *            the start index, inclusive
     * @param end
     *            the end index, exclusive
     * @return the specified subsequence
     * @throws IndexOutOfBoundsException
     *             if start or end are negative, if end is greater than length(), or if start is greater than
     *             end
     */
    public CharSequence subSequence(int start, int end) {
        return buffer.substring(start, end);
    }

    /**
     * <p>
     * Returns a string containing the characters in this sequence in the same order as this sequence. The
     * length of the string will be the length of this sequence.
     * </p>
     *
     *
     * @return a string consisting of exactly this sequence of characters.
     */
    public String toString() {
        if (bufferContent == null) {
            bufferContent = buffer.toString();
        }
        return bufferContent;
    }

    /**
     * <p>
     * "Highlights" this sequence starting from the startPos (inclusive) and ending at endPos (exclusive). This
     * method removes the sequence at the specified position and add a Segment entry corresponding to this segment
     * to the segments list.
     * </p>
     *
     *
     * @param startPosList
     *            A list of starting positions of the highlighted segment. Element i of startPos corresponds to
     *            element i of endPos. may be empty list.
     * @param endPosList
     *            A list of ending positions of the highlighted segment. Element i of endPos corresponds to element
     *            i of startPos. may be empty list.
     * @param style
     *            The TextStyle used to highlight this segment. May be null.
     * @throws IndexOutOfBoundsException
     *             if any element of startPos or endPos are negative, if any element of endPos is greater than
     *             length(), or if element i of startPos is greater than element i of endPos
     * @throws NullPointerException
     *             if startPosList or endPosList is null
     * @throws IllegalArgumentException
     *             if startPosList or endPosListontains null or invalid element or size
     */
    public void highlight(List startPosList, List endPosList, TextStyle style) {
        SHHelper.checkList(startPosList, "startPosList", Integer.class);
        SHHelper.checkList(endPosList, "endPosList", Integer.class);

        if (startPosList.size() != endPosList.size()) {
            throw new IllegalArgumentException("startPosList and endPosList must be of the same size.");
        }

        // the new temp buffer to collect every non-matched part(defined by startPosList and endPosList)
        // and new index point(newindexer) mirror this new buffer
        StringBuffer sb = new StringBuffer(buffer.length());
        Iterator startIt = startPosList.iterator();

        Iterator endIt = endPosList.iterator();

        // the start index of text segments in buffer
        int cur = 0;

        // the added up removal length
        int offset = 0;

        while (startIt.hasNext()) {
            int start = ((Integer) startIt.next()).intValue();
            int end = ((Integer) endIt.next()).intValue();

            if ((start < 0) || (end < 0) || (end > buffer.length()) || (start >= end)) {
                throw new IndexOutOfBoundsException("Some start-end pair was not properly specified.");
            }

            if (cur < start) {
                // 'sb' collect the left part of buffer after this removal(start to end)
                // 'newindexer' collect the index of new buffer from old indexer
                sb.append(buffer.substring(cur, start));
            }

            // start points to the first charactor in this segment
            // 'end - 1' points to the last charactor in this segment
            int startIndex = indexer.get(start - offset);
            int endIndex = indexer.get(end - 1 - offset);
            segments.add(new Segment(startIndex, endIndex, style));
            indexer.remove(start - offset, end - 1 - offset);
            if (end - offset < lastIndex) {
                lastIndex -= end - start;
            } else if (start - offset < lastIndex) {
                lastIndex = start - offset;
            }

            // adjust cur and offset
            cur = end;
            offset += (end - start);
        }

        // add the last part of unhightlighted part(if have)
        if (cur < buffer.length()) {
            sb.append(buffer.substring(cur));
        }

        buffer = sb;
        bufferContent = null;
    }

    /**
     * <p>
     * "Highlights" this sequence starting from the startPos (inclusive) and ending at endPos (exclusive).
     * Similar to highlight(List, List, TextStyle), just for a single pair segment highlight.
     *
     * </p>
     *
     *
     * @param startPos
     *            The starting position of the highlighted segment.
     * @param endPos
     *            The ending position of the highlighted segment.
     * @param style
     *            The TextStyle used to highlight this segment.
     * @throws IndexOutOfBoundsException
     *             if start or end are negative, if end is greater than length(), or if start is greater than
     *             end
     */
    public void highlight(int startPos, int endPos, TextStyle style) {
        if ((startPos < 0) || (endPos < 0) || (endPos > buffer.length()) || (startPos >= endPos)) {
            throw new IndexOutOfBoundsException("The startPos or endPos was not properly specified.");
        }

        // start points to the first charactor in this segment
        // 'end - 1' points to the last charactor in this segment
        int startIndex = indexer.get(startPos);
        int endIndex = indexer.get(endPos - 1);
        segments.add(new Segment(startIndex, endIndex, style));

        // change the index points and buffer, adjust last index
        buffer = buffer.delete(startPos, endPos);
        bufferContent = null;
        indexer.remove(startPos, endPos - 1);
        if (endPos < lastIndex) {
            lastIndex -= endPos - startPos;
        } else if (startPos < lastIndex) {
            lastIndex = startPos;
        }
    }

    /**
     * <p>
     * This constructs a complete list of segments (including unformatted segments) suitable for use by an
     * output plugin (HighlightedOutput).
     * </p>
     * <p>
     * See Component_Specification for internal algorithm.
     * </p>
     *
     * @return an array of ContentSegment instances which represent the document in segments. This should be
     *         ordered based on the start positiion so that it is possible to simply enumerate the array and
     *         repiece the document.
     */
    public ContentSegment[] getOrderedSegments() {
        // should not sort segments because segments are ordered by the sequence of highlighte,
        // precedence is responsed by this order

        SortedSet ss = new TreeSet();
        List list = new ArrayList();

        // deal with overlap, for instance: (if content has a length of 15)
        // start: 3, end:12
        // start:10, end:11
        // will be reconstructed to be:
        // start: 3, end: 9
        // start: 10, end: 11
        // start: 12, end: 15

        // note that SortedSet will sort the newly added segment automately
        // use SortedSet is to use its headSet to find the position where the segment's
        // start(may not be in the set) should be.

        for (Iterator iterator = segments.iterator(); iterator.hasNext();) {
            Segment seg = (Segment) iterator.next();
            int start = seg.getStart();
            int end = seg.getEnd();
            TextStyle style = seg.getStyle();

            // find the last start whose end larger than seg.start,
            // for example above [10,11] is the last start is  whose end larger than seg.start
            SortedSet header = ss.headSet(new Segment(start, start, null));

            // find the gaps which are produced by previous cut and put them into SortedSet ss.
            // the gaps in above example is [3,9], [12,15]

            // find the first gap start
            // for example above it is 3
            if (header.size() > 0) {
                int tempEnd = ((Segment) header.last()).getEnd() + 1;
                start = (tempEnd > start) ? tempEnd : start;
            }

            // find the gaps and add them to ss with proper style

            SortedSet tailer = ss.tailSet(new Segment(start, start, null));

            for (Iterator it = tailer.iterator(); it.hasNext() && (start <= end);) {
                Segment tempSeg = (Segment) it.next();
                int tempStart = tempSeg.getStart();

                // tempStart > end means that now there isn't any segment in [start, end],
                // add [start, end] and get out
                if (tempStart > end) {
                    list.add(new Segment(start, end, style));
                    start = end + 1;
                    break;
                }

                // start < tempStart means there is a gap of [start, tempStart - 1]
                if (start < tempStart) {
                    list.add(new Segment(start, tempStart - 1, style));
                }

                // start is set to the next valid start
                start = tempSeg.getEnd() + 1;
            }
            // start is the last gap start
            if (start <= end) {
                list.add(new Segment(start, end, style));
            }

            ss.addAll(list);
            list.clear();
        }

        // fill in blank
        List newContentSegments = fillBlanks(ss);

        return (ContentSegment[]) newContentSegments.toArray(new ContentSegment[newContentSegments.size()]);
    }

    /**
     * <p>This method get the whole segments for the sorted segments with blanks.
     * </p>
     * @param ss the sorted segments with blanks
     * @return the whole segments without blanks
     */
    private List fillBlanks(SortedSet ss) {
        // ss contains segments whose start and end are both inclusive
        List newContentSegments = new ArrayList();
        int cur = 0;

        // if found any blank part, add new ContentSegment of that part, for example(if content has a length of
        // 20):
        // start: 3, end: 6
        // start: 10, end: 10
        // start: 12, end: 14
        // should be filled with
        // [0,2],[7,9],[11,11],[15,19]

        for (Iterator it = ss.iterator(); it.hasNext();) {
            Segment tempSeg = (Segment) it.next();
            // check if there is a gap between cur and tempSeg.start
            if (tempSeg.getStart() > cur) {
                // if have fill the gap
                newContentSegments.add(getContentSegment(cur, tempSeg.getStart() - 1, null));
            }
            newContentSegments.add(getContentSegment(tempSeg.getStart(), tempSeg.getEnd(), tempSeg.getStyle()));
            cur = tempSeg.getEnd() + 1;
        }

        // check if there is a last gap of [cur, end]
        if (cur < originalString.length()) {
            newContentSegments.add(getContentSegment(cur, originalString.length() - 1, null));
        }

        return newContentSegments;
    }

    /**
     *
     * <p>
     * This simply resets the state of the splitter such that a subsequent call to
     * getNextToken() will start at the beginning to the sequence.
     * </p>
     *
     */
    public void reset() {
        lastIndex = 0;
    }

    /**
     * <p>
     * This retrieves the next token where a token is defined as a valid Java identifier. This method iterates
     * starting from lastIndex to find a start position and then iterate from that point to find an end point
     * (the returned end point points to the index immediately after the last matching character as in
     * Java's String.substring(). Null will returned if there is no suitable token.
     * </p>
     *
     *
     * @return A ContentSegment representing the next found token. This includes the start and end index and
     *         the content. The style should be null.
     */
    public ContentSegment getNextToken() {
        // find the start index
        int start = lastIndex;

        // while not reach the end
        while (start < buffer.length()) {
            // get the valid start position
            if (Character.isJavaIdentifierStart(buffer.charAt(start))) {
                break;
            }
            start++;
        }

        // reach the end
        if (start >= buffer.length()) {
            lastIndex = buffer.length();
            return null;
        }

        // find the whole word
        // care about breakpoint
        int end = start + 1;

        while ((end < buffer.length()) && Character.isJavaIdentifierPart(buffer.charAt(end))) {
            end++;
        }

        lastIndex = end;
        return new ContentSegment(start, end, null, buffer.substring(start, end));
    }

    /**
     * <p>
     * Returns the index within this string of the first occurrence of the specified substring.
     * </p>
     *
     *
     * @param str
     *            any string
     * @return if the string argument occurs as a substring within this object, then the index of the first
     *         character of the first such substring is returned; if it does not occur as a substring, -1 is
     *         returned.
     * @throws NullPointerException
     *             if str is null.
     */
    public int indexOf(String str) {
        return indexOf(str, 0);
    }

    /**
     * <p>
     * Returns the index within this string of the first occurrence of the specified substring, starting at
     * the specified index.
     * </p>
     *
     *
     * @param str
     *            the substring for which to search.
     * @param fromIndex
     *            the index from which to start the search.
     * @return the index within this string of the first occurrence of the specified substring, starting at
     *         the specified index.
     * @throws NullPointerException
     *             if str is null.
     *
     */
    public int indexOf(String str, int fromIndex) {
        SHHelper.checkNull(str, "str");

        // empty str match nothing
        if (str.length() == 0) {
            return -1;
        }

        while (true) {
            // find str in buffer
            int index = buffer.indexOf(str, fromIndex);
    
            if (index < 0) {
                return -1;
            }
    
            if (str.length() < 2) {
                return index;
            }

            // if found, check if there is break within the str
            fromIndex = indexer.findBreakPoint(index, index + str.length() - 1);

            if (fromIndex == -1) {
                return index;
            }
        }
    }

    /**
     *<p>
     * This is helper method for make new ContentSegment with start and end(both inclusive).
     * @param start start index of the segment.
     * @param end end index of the segment.
     * @param style the style for this segment.
     * @return the contensement with content.
     */
    private ContentSegment getContentSegment(int start, int end, TextStyle style) {
        return new ContentSegment(start, end, style, originalString.substring(start, end + 1));
    }
}
