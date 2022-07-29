package com.topcoder.server.docGen.xml.xsl;

import java.io.*;
import java.net.*;
import javax.xml.transform.*;
import javax.xml.transform.stream.*;

/**
 * XSLTransformerWrapper.java
 *
 * Description: Wrapper class for rendering an XML data source using an XSL template.
 *
 * @author	James Lee (jameslee@cs.stanford.edu)
 * @version	1.0
 *
 */

public class XSLTransformerWrapper {

    // The actual transformer object that is being wrapped
    protected Transformer transformer;

    /**
     * Creates a transformer with the specified XSL template.  Internally,
     * a javax.xml.transform.Transformer object is created, and that's
     * used for the actual XSL transformations.
     *
     * @param xslInputStream	the input stream containing the XSL template
     *
     * @throws XSLTransformerWrapperException
     */

    public XSLTransformerWrapper(InputStream xslInputStream)
            throws XSLTransformerWrapperException {
        try {
            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Source xslSource = new StreamSource(xslInputStream);

            this.transformer = transformerFactory.newTransformer(xslSource);
        } catch (TransformerConfigurationException e) {
            throw new XSLTransformerWrapperException(e);
        }
    }

    /**
     * Transforms the specified XML data and sends the output of the transformation
     * to the specified target OutputStream.
     *
     * @param xmlInputStream	the input stream containing source XML data
     * @param targetOutputStream	the output stream that receives the transformed result
     *
     * @throws XSLTransformerWrapperException
     */

    public void transform(InputStream xmlInputStream, OutputStream targetOutputStream)
            throws XSLTransformerWrapperException {
        try {
            Source xmlSource = new StreamSource(xmlInputStream);
            Result result = new StreamResult(targetOutputStream);
            transformer.transform(xmlSource, result);
        } catch (TransformerException e) {
            throw new XSLTransformerWrapperException(e);
        }
    }
}