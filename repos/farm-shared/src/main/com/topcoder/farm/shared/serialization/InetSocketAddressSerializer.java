/*
 * InetSocketAddressSerializer
 * 
 * Created 10/20/2006
 */
package com.topcoder.farm.shared.serialization;

import java.io.IOException;
import java.net.InetSocketAddress;

import com.topcoder.shared.netCommon.CSReader;
import com.topcoder.shared.netCommon.CSWriter;
import com.topcoder.shared.netCommon.customserializer.CustomSerializer;

/**
 * CustomSerializer for {@link InetSocketAddress} class.
 * 
 * @author Diego Belfer (mural)
 * @version $Id$
 */
public class InetSocketAddressSerializer implements CustomSerializer {
    
    public Object readObject(CSReader reader) throws IOException {
        String host = reader.readString();
        int port = reader.readInt();
        return new InetSocketAddress(host, port);
    }

    public void writeObject(CSWriter writer, Object object) throws IOException {
        InetSocketAddress addr = (InetSocketAddress) object;
        String text = addr.toString();
        int pos = text.indexOf('/');
        String hostName =  null;
        if (pos != 0) { 
            hostName = text.substring(0, pos);
        } else {
            hostName = addr.getAddress().getHostAddress();
        }
        writer.writeString(hostName);
        writer.writeInt(addr.getPort());
    }

    public boolean canHandle(Class clazz) {
        return InetSocketAddress.class.equals(clazz);
    }
}
