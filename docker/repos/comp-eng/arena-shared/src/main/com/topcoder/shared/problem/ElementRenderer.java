package com.topcoder.shared.problem;

/**
 * Interface for all renderer objects.
 * @author Greg Paul
 * @version $Id: ElementRenderer.java 71757 2008-07-17 09:13:19Z qliu $
 */
public interface ElementRenderer extends Renderer {


    /**
     * Set the element for this renderer.
     * @param element the element
     * @throws Exception if the renderer is not capable of rendering the given element
     */
    void setElement(Element element) throws Exception;
}

