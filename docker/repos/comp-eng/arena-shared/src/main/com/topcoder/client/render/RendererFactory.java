/*
 * RendererFactory Created 06/13/2006
 */
package com.topcoder.client.render;

import com.topcoder.shared.problem.DataType;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import com.topcoder.shared.problem.Element;
import com.topcoder.shared.problem.ElementRenderer;
import com.topcoder.shared.problem.NodeElement;
import com.topcoder.shared.problem.TextElement;
import com.topcoder.shared.problem.UserConstraint;

/**
 * Simple Renderer factory Obtains the renderer for an element using the className as part of the renderer className.
 * Allows to register specific renderer for a given element class Code partially extracted from BaseRenderer
 * 
 * @author Diego Belfer (mural)
 * @version $Id: RendererFactory.java 71732 2008-07-16 05:53:02Z qliu $
 */
public class RendererFactory {
    /** Represents the map of all registered element renderers. */
    private Map renderers = Collections.synchronizedMap(new HashMap());

    /**
     * Creates a new instance of <code>RendererFactory</code>. Node, text, user constraint and data type renderers
     * are registered.
     */
    public RendererFactory() {
        // register defaults so we can obfuscate successfully
        registerRenderer(NodeElement.class, NodeElementRenderer.class);
        registerRenderer(TextElement.class, TextElementRenderer.class);
        registerRenderer(UserConstraint.class, UserConstraintRenderer.class);
        registerRenderer(DataType.class, DataTypeRenderer.class);
    }

    /**
     * Register a specific renderer class for a give element class
     * 
     * @param elementClass the element class for which the renderer class is registered
     * @param rendererClass The renderer class
     */
    public void registerRenderer(Class elementClass, Class rendererClass) {
        renderers.put(elementClass, rendererClass);
    }

    /**
     * Returns the ElementRenderer for the specified element. If one renderer was registered for the element class, a
     * new instance of that renderer is returned. Otherwise, uses the classname of the given element to figure out what
     * renderer class should be used.
     * 
     * @param element for which a renderer is required
     * @return the renderer for the given element.
     * @throws Exception Exception 1. if we can't find the renderer class 2. if we can't instantiate it 3. if we're not
     *             allowed to access it.
     */
    public ElementRenderer getRenderer(Element element) throws Exception {
        Class rendererClass = (Class) renderers.get(element.getClass());
        if (rendererClass == null) {
            rendererClass = resolveFromName(element);
            renderers.put(element.getClass(), rendererClass);
        }
        try {
            ElementRenderer ret = (ElementRenderer) rendererClass.newInstance();
            ret.setElement(element);
            if (ret instanceof BaseRenderer) {
                ((BaseRenderer) ret).setRendererFactory(this);
            }
            return ret;
        } catch (InstantiationException e) {
            throw new Exception("Could not instantiate: " + rendererClass.getName());
        } catch (IllegalAccessException e) {
            throw new Exception("Illegal Access: " + rendererClass.getName());
        }
    }

    /**
     * Obtains the renderer class from the element className
     * 
     * @param element the element whose renderer class is composed.
     * @return the renderer class
     * @throws Exception If the class could not be found
     */
    private Class resolveFromName(Element element) throws Exception {
        System.out.println("Element:" + element.getClass().getName());
        String elementClassName = element.getClass().getName().substring(
            element.getClass().getName().lastIndexOf(".") + 1);
        String rendererClassName = elementClassName + "Renderer";
        String rendererPackage = ProblemRenderer.class.getName().substring(0,
            ProblemRenderer.class.getName().lastIndexOf("."));
        try {
            return Class.forName(rendererPackage + "." + rendererClassName);
        } catch (ClassNotFoundException cnfe) {
            throw new Exception("Could not find class: " + rendererPackage + rendererClassName);
        }
    }
}
