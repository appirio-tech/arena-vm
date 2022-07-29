/*
 * MPSQASRendererFactory
 * 
 * Created 06/13/2006
 */
package com.topcoder.client.mpsqasApplet.common;

import com.topcoder.client.render.RendererFactory;
import com.topcoder.shared.language.Language;
import com.topcoder.shared.problem.DataType;
import com.topcoder.shared.problem.Element;
import com.topcoder.shared.problem.ElementRenderer;

/**
 * Renderer factory used to render elements into the MPSQAS
 * views. The MPSQAS views use a mixed version of an HTML, some
 * tags must be preserved and shown e.g. : type
 *  
 * @author Diego Belfer (mural)
 * @version $Id: MPSQASRendererFactory.java 47978 2006-07-07 15:28:06Z thefaxman $
 */
public class MPSQASRendererFactory {
    private static RendererFactory instance;

    private MPSQASRendererFactory() {
    }
    
    /**
     * Returns the RendererFactory instance to be used on the 
     * MPSQAS application 
     * 
     * @return the RendererFactory
     */
    public static synchronized RendererFactory getInstance() {
        if (instance == null) {
            buildInstance();
        }
        return instance;
    }

    private static void buildInstance() {
        instance = new RendererFactory();
        instance.registerRenderer(DataType.class, MPSQASDataTypeRenderer.class);
    }

    /**
     * Instances of this Renderer displays the DataType elements as 
     * &lt;type&gt;XXXXX&lt;/type&gt;
     */
    public static final class MPSQASDataTypeRenderer implements ElementRenderer {
        DataType element;
        public String toPlainText(Language language) throws Exception {
            return element.getDescription();
        }
        public String toHTML(Language language) throws Exception {
            return element.toXML();
        }
        public void setElement(Element element) throws Exception {
            this.element = (DataType) element;
        }
    }
}
