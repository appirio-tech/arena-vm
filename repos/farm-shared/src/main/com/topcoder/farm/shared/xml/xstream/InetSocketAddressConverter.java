/*
 * InetSocketAddressConverter
 * 
 * Created 08/30/2006
 */
package com.topcoder.farm.shared.xml.xstream;

import java.net.InetSocketAddress;

import com.thoughtworks.xstream.converters.ConversionException;
import com.thoughtworks.xstream.converters.basic.AbstractBasicConverter;

/**
 * XML converter for InetSocketAddress
 * 
 * Simplified notation: <code>host:port</code>
 * 
 * @author Diego Belfer (mural)
 * @version $Id$
 */
public class InetSocketAddressConverter extends AbstractBasicConverter {

    public boolean canConvert(Class type) {
        return type.equals(InetSocketAddress.class);
    }

    protected Object fromString(String str) {
        try {
            int pos = str.indexOf(':');
            if (pos == -1) {
                throw new ConversionException("InetSocketAddress invalid format. host:port expected");
            }
            String host = str.substring(0, pos);
            int port = Integer.parseInt(str.substring(pos+1));
            return new InetSocketAddress(host, port);
        } catch (Exception e) {
            throw new ConversionException(e);
        }
    }
    
    protected String toString(Object obj) {
        InetSocketAddress addr = (InetSocketAddress) obj;
        String text = addr.toString();
        int pos = text.indexOf('/');
        if (pos != 0) { 
            return text.substring(0, pos) + ":"+ addr.getPort();
        }
        return addr.getAddress().getHostAddress()+":"+addr.getPort();
    }
}

