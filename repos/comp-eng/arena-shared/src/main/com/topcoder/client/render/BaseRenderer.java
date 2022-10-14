package com.topcoder.client.render;

import com.topcoder.shared.problem.Element;
import com.topcoder.shared.problem.ElementRenderer;
import com.topcoder.shared.problem.HTMLCharacterHandler;

/**
 * base abstract class for applet side renderer objects
 * 
 * @author Greg Paul
 * @version $Id: BaseRenderer.java 71757 2008-07-17 09:13:19Z qliu $
 */
abstract class BaseRenderer implements ElementRenderer {
    /**
     * Represents a list of tags not to be outputted when toHTML called. It must be sorted so that binary search can
     * work.
     */
    static final String[] XML_ONLY_TAGS = {"annotation", "block", "constraints", "example", "flow", "fontstyle",
        "heading", "inline", "input", "intro", "list", "name", "note", "notes", "output", "problem", "signature",
        "spec", "special", "tctype", "test-case", "test-cases", "type", "user-constraint"};

    /** Represents a list of HTML tags which should be stripped out when converting HTML into plain text. */
    static final String[] HTML_ONLY_TAGS = {"ul", "ol", "li", "tt", "i", "b", "h1", "h2", "h3", "h4", "h5", "a", "img",
        "br", "sub", "sup", "p", "pre", "hr", "list"};

    /**
     * Default renderer factory to use if other is not set
     */
    private static final RendererFactory RENDERER_FACTORY_DEFAULT = new RendererFactory();

    /**
     * Renderer factory to obtain renderers for contained elements
     */
    private RendererFactory rendererFactory;

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
     * Use the class of the given element to figure out what renderer class should be used, if no other renderer factory
     * has been set.
     * 
     * @param element the element that we want to get a renderer for
     * @return the renderer for the given element
     * @throws Exception 1. if we can't find the renderer class 2. if we can't instantiate it 3. if we're not allowed to
     *             access it.
     */
    ElementRenderer getRenderer(Element element) throws Exception {
        if (rendererFactory == null) {
            rendererFactory = RENDERER_FACTORY_DEFAULT;
        }
        return rendererFactory.getRenderer(element);
    }

    /**
     * Go through the given string and remove all the html tags
     * 
     * @param text the string to remove the html tags from
     * @return the string without html tags
     */
    static String removeHtmlTags(String text) {

        StringBuffer buf = new StringBuffer(text);
        for (int i = 0; i < HTML_ONLY_TAGS.length; i++) {
            boolean clear = false;
            for (; !clear;) {
                int beginIndex = buf.indexOf("<" + HTML_ONLY_TAGS[i] + ">");
                int endIndex1 = buf.indexOf("</" + HTML_ONLY_TAGS[i] + ">");
                int endIndex2 = buf.indexOf("<" + HTML_ONLY_TAGS[i] + "/>");
                clear = beginIndex < 0 && endIndex1 < 0 && endIndex2 < 0;
                if (beginIndex > -1) {
                    buf.delete(beginIndex, beginIndex + HTML_ONLY_TAGS[i].length() + 2);
                }
                if (endIndex1 > -1) {
                    buf.delete(endIndex1, endIndex1 + HTML_ONLY_TAGS[i].length() + 3);
                }
                if (endIndex2 > -1) {
                    buf.delete(endIndex2, endIndex2 + HTML_ONLY_TAGS[i].length() + 3);
                }

            }
        }

        return buf.toString();
    }

    /**
     * Gets the renderer factory used to generate element renderers.
     * 
     * @return Returns the renderer factory.
     */
    public RendererFactory getRendererFactory() {
        return rendererFactory;
    }

    /**
     * Sets the renderer factory used to generate element renderers.
     * 
     * @param rendererFactory the renderer factory to set.
     */
    public void setRendererFactory(RendererFactory rendererFactory) {
        this.rendererFactory = rendererFactory;
    }
}
