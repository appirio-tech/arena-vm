package com.topcoder.shared.docGen.xml.xsl;

import javax.xml.transform.*;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import java.io.*;

import javax.xml.transform.SourceLocator;
import javax.xml.transform.TransformerException;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import org.apache.xml.utils.SAXSourceLocator;
import org.apache.xml.utils.WrappedRuntimeException;

/**
 * XSLTransformerWrapper.java
 *
 * Description: Wrapper class for rendering an XML data source using an XSL template.
 *
 * @author  James Lee (jameslee@cs.stanford.edu)
 * @version  $Revision$
 *
 */
public class XSLTransformerWrapper {
    /**
     * The actual transformer object that is being wrapped
     */
    protected Transformer transformer;

    /**
     * Creates a transformer with the specified XSL template.  Internally,
     * a javax.xml.transform.Transformer object is created, and that's
     * used for the actual XSL transformations.
     *
     * @param xslInputStream  the input stream containing the XSL template
     * @throws Exception
     */
    public XSLTransformerWrapper(InputStream xslInputStream)
            throws Exception {
        try {
            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Source xslSource = new StreamSource(xslInputStream);

            this.transformer = transformerFactory.newTransformer(xslSource);
        } catch (TransformerConfigurationException e) {
            throw e;
        }
    }


    /**
     * Creates a transformer with the specified XSL template.  Internally,
     * a javax.xml.transform.Transformer object is created, and that's
     * used for the actual XSL transformations.
     *
     * @param xslFile  the input file containing the XSL template
     * @throws Exception
     */
    public XSLTransformerWrapper(File xslFile)
            throws Exception {
        try {
            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Source xslSource = new StreamSource(xslFile);

            this.transformer = transformerFactory.newTransformer(xslSource);
        } catch (TransformerConfigurationException e) {
            //killing most of the stack trace cuz it's just too much
            //and not very informative.
            SourceLocator s = getRootSourceLocator(e);
            throw new Exception(e.getMessage() +
                    "\n column  : " + s.getColumnNumber() +
                    "\n line    : " + s.getLineNumber() +
                    "\n publicid: " + s.getPublicId() +
                    "\n systemid: " + s.getSystemId());
        }
    }

    /**
     * Transforms the specified XML data and sends the output of the transformation
     * to the specified target OutputStream.
     *
     * @param xmlInputStream  the input stream containing source XML data
     * @param targetOutputStream  the output stream that receives the transformed result
     * @throws Exception
     */
    public void transform(InputStream xmlInputStream, OutputStream targetOutputStream)
            throws Exception {
        try {
            Source xmlSource = new StreamSource(xmlInputStream);
            Result result = new StreamResult(targetOutputStream);
            transformer.transform(xmlSource, result);
        } catch (TransformerException e) {
            throw e;
        }
    }

    /**
     * Transforms the specified XML data and sends the output of the transformation
     * to the specified target OutputStream.
     *
     * @param xmlInputReader  the input reader containing source XML data
     * @param targetOutputStream  the output stream that receives the transformed result
     *
     * @throws Exception
     */
    public void transform(Reader xmlInputReader, OutputStream targetOutputStream)
            throws Exception {
        try {
            Source xmlSource = new StreamSource(xmlInputReader);
            Result result = new StreamResult(targetOutputStream);
            transformer.transform(xmlSource, result);
        } catch (TransformerException e) {
            throw e;
        }
    }



    private SourceLocator getRootSourceLocator(Throwable exception) {
      SourceLocator locator = null;
      Throwable cause = exception;

      // Try to find the locator closest to the cause.
      do {
        if(cause instanceof SAXParseException) {
          locator = new SAXSourceLocator((SAXParseException)cause);
        }
        else if (cause instanceof TransformerException) {
          SourceLocator causeLocator =
                        ((TransformerException)cause).getLocator();
          if(null != causeLocator)
            locator = causeLocator;
        }
        if(cause instanceof TransformerException)
          cause = ((TransformerException)cause).getCause();
        else if(cause instanceof WrappedRuntimeException)
          cause = ((WrappedRuntimeException)cause).getException();
        else if(cause instanceof SAXException)
          cause = ((SAXException)cause).getException();
        else
          cause = null;
      }
      while(null != cause);

      return locator;
    }


}
