package com.topcoder.client.render;

import com.topcoder.shared.language.Language;
import com.topcoder.shared.problem.Element;
import com.topcoder.shared.problem.WebService;

/**
 * Handles the display of a <code>com.topcoder.shared.problem.WebServiceRenderer</code>
 * 
 * @author Greg Paul
 * @version $Id: WebServiceRenderer.java 71732 2008-07-16 05:53:02Z qliu $
 */
public class WebServiceRenderer extends BaseRenderer {
    /** Represents the web service to be rendered. */
    private WebService webService;

    /**
     * Creates a new instance of <code>WebServiceRenderer</code>. The web service to be rendered is set to be
     * <code>null</code>.
     */
    public WebServiceRenderer() {
        this.webService = null;
    }

    /**
     * Creates a new instance of <code>WebServiceRenderer</code>. The web service to be rendered is given.
     * 
     * @param webService the web service to be rendered.
     */
    public WebServiceRenderer(WebService webService) {
        this.webService = webService;
    }

    /**
     * Sets the element to be rendered. The given element must be a web service.
     * 
     * @param element the web service to be rendered.
     * @throws IllegalArgumentException if the element to be rendered is not a web service.
     */
    public void setElement(Element element) {
        if (element instanceof WebService) {
            webService = (WebService) element;
        } else {
            throw new IllegalArgumentException("element must be a WebService Object.");
        }
    }

    /**
     * Renders the element into HTML with proper escaping.
     * 
     * @param language the programming language to be rendered with.
     * @return the HTML rendered according to the web service.
     * @throws IllegalStateException if the element to be rendered is not set.
     */
    public String toHTML(Language language) {
        if (webService == null) {
            throw new IllegalStateException("The web service is not set.");
        }

        StringBuffer html = new StringBuffer();
        html.append("<h3>Web Service: ");
        html.append(BaseRenderer.encodeHTML(webService.getName()));
        html.append("</h3>");
        return html.toString();
    }

    /**
     * Renders the element into plain text.
     * 
     * @param language the programming language to be rendered with.
     * @return the plain text rendered according to the web service.
     * @throws IllegalStateException if the element to be rendered is not set.
     * @deprecated
     */
    public String toPlainText(Language language) {
        if (webService == null) {
            throw new IllegalStateException("The web service is not set.");
        }

        StringBuffer html = new StringBuffer();
        html.append("WEB SERVICE: ");
        html.append(webService.getName());
        html.append("\n");
        return html.toString();
    }
}
