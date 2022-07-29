package com.topcoder.server.docGen.xml.xsl;

/**
 * XSLTransformerException.java
 *
 * @author	James Lee (jameslee@cs.stanford.edu)
 * @version	1.0
 *
 */

public class XSLTransformerWrapperException
        extends Exception {

    public XSLTransformerWrapperException() {
        super();
    }

    public XSLTransformerWrapperException(String message) {
        super(message);
    }

    public XSLTransformerWrapperException(Throwable t) {
        super(t.getMessage());
    }
}