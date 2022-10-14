package com.topcoder.shared.problem;

import java.util.Arrays;
import java.util.List;

/**
 * Defines an abstract base class of all elements in the problem XML.
 * 
 * @author Qi Liu
 * @version $Id: BaseElement.java 71757 2008-07-17 09:13:19Z qliu $
 */
abstract class BaseElement implements Element {
    /** Represents an array of valid HTML tags in the problem XML. */
    protected static final String[] USER_ONLY_TAGS = {"ul", "ol", "li", "tt", "i", "b", "h1", "h2", "h3", "h4",
                                                 "h5", "a", "img", "br", "sub", "sup", "p", "pre", "hr", "list", "type"};
    /** Represents a list of valid HTML tags in the problem XML. */
    protected static final List USER_ONLY_TAGS_LIST = Arrays.asList(USER_ONLY_TAGS);
    
    /** Represents the element renderer used to render this element. */
    private ElementRenderer renderer;

    /**
     * Creates a new instance of <code>BaseElement</code>.
     */
    public BaseElement() {
    }

    public void setRenderer(ElementRenderer renderer) {
        this.renderer = renderer;
    }

    public ElementRenderer getRenderer() {
        return renderer;
    }

    /**
     * Encodes certain characters in the text into HTML entities. '&amp;', '&lt;', '&gt;' and '&quot;' are encoded into
     * HTML entities.
     * 
     * @param text the text to be encoded.
     * @return the HTML encoded text.
     */
    static String encodeHTML(String text) {
        return HTMLCharacterHandler.encodeSimple(text);
    }
    
    
    /**
     * Replaces all escaped entities &amp;lt;, &amp;gt;, &amp;quot; 
     * and &amp;amp; with its unescaped representation (&lt; &gt; &quot; &,a[;)
     *  
     * Note: This is a bad implementation, it is cunrrently use in just a few places
     * A better implementation should be done for intensive use
     * 
     * @param text text to unescape
     * 
     * @return the resulting text
     */
    public static String decodeHTML(String text) {
        return HTMLCharacterHandler.decode(text);
    }
}
