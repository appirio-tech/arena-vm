/*
 * Copyright (C) 2005 TopCoder Inc., All Rights Reserved.
 */
package com.topcoder.util.syntaxhighlighter;


/**
 * <p>
 * The HighlightedOutput interface defines a method for providing an output destination for highlighted text.
 * It defines a sole method, setText, which is used to signal the plugin that it should reform a set of
 * ContentSegments, which define highlighted segments, into an entire document. For instance, the HtmlOutput
 * class reforms the String by applying a span to all highighted segments and reconstructing the document.
 * Thread Safety: Implementors of this interface need not worry about thread safety. This component is not
 * meant to be used by multiple threads concurrently, so the only threading issues are if the user is
 * manipulating a HighlightedOutput instance while the component is performing highlighting. Since the
 * component is not thread safe and has no requirement to be, the user should not concurrently access it in
 * the manner specified.
 * </p>
 * @author duner, still
 * @version 2.0
 */
public interface HighlightedOutput {
    /**
     * <p>
     * This sole interface method is responsible for taking an array of ContentSegments and patching them back
     * up into a complete document. The ContentSegment array is ordered sequentially based on placement in the
     * text. Each ContentSegment contains a TextStyle instance signifying the formatting options that should
     * be applied to the segment (this may be null to indicate no or default formatting). There is no
     * constraint on how this method needs to be implemented. It should simply alter the state of the instance
     * as necessary in order to allow the user to handle the result. For instance, the HtmlOutput class simply
     * updates a state variable (a String) which allows the user to make a subsequent call to retrieve the
     * HTML. Note: The TextStyle contained within each ContentSegment may be a null reference. This indicates
     * no formatting information. Thread Safety: Instances of subclasses of this interface will only be used
     * during a call to SyntaxHighlighter.highlightText(). As a result, this class does not need to be thread
     * safe, however, care should be taken by the user to not use ensure another thread is not manipulating an
     * instance while the highlighting is taking place.
     *
     *
     * @param contentSegments
     *            A non-empty array of ContentSegment instances used to reconstruct the document with
     *            formatting.
     * @throws HighlightingException
     *             if the highlighting was not able to be completed due to some error.
     */
    public void setText(ContentSegment[] contentSegments) throws HighlightingException;
}

