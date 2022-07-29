/*
 * XMLHelper
 * 
 * Created 08/30/2006
 */
package com.topcoder.farm.shared.xml;

import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;
import com.thoughtworks.xstream.io.xml.CompactWriter;
import com.thoughtworks.xstream.io.xml.XppDriver;
import com.topcoder.farm.shared.xml.xstream.InetSocketAddressConverter;

/**
 * @author Diego Belfer (mural)
 * @version $Id$
 */
public class XMLHelper {
    private static XMLHelper instance;
    private final XStream transformer;

    public static synchronized XMLHelper getInstance() {
        if (instance == null) {
            instance = new XMLHelper();
        }
        return instance;
    }
    
    public XMLHelper() {
        transformer = new XStream(new XppDriver() {
            public HierarchicalStreamWriter createWriter(Writer out) {
                return new CompactWriter(out);
            }

            public HierarchicalStreamWriter createWriter(OutputStream out) {
                return createWriter(new OutputStreamWriter(out));
            }        
        });
        transformer.registerConverter(new InetSocketAddressConverter());
    }
    
    public String toXML(Object obj) {
        return transformer.toXML(obj);
    }
    
    public Object fromXML(String xml) {
        return transformer.fromXML(xml);
    }
    
    public Object fromXML(InputStream xml) {
        return transformer.fromXML(xml);
    }
}
