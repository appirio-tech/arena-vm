/*
 * PrivateKeyObtainer
 * 
 * Created Jul 12, 2008
 */
package com.topcoder.server.security;

import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.spec.KeySpec;
import java.security.spec.PKCS8EncodedKeySpec;
import java.util.Properties;

import com.topcoder.shared.util.encoding.Base64Encoding;
import com.topcoder.shared.util.logging.Logger;

/**
 * @author Diego Belfer (Mural)
 * @version $Id: PrivateKeyObtainer.java 71656 2008-07-12 17:02:01Z dbelfer $
 */
public class PrivateKeyObtainer {
    private static final Logger log = Logger.getLogger(PrivateKeyObtainer.class);
    
    public static PrivateKey obtainPrivateKey() {
        try {
            Properties prop = new Properties();
            prop.load(PrivateKeyObtainer.class.getResourceAsStream("/ServerEncryption.properties"));
            String algorithm = prop.getProperty("Algorithm");
            KeyFactory factory = KeyFactory.getInstance(algorithm);
            KeySpec keySpec = new PKCS8EncodedKeySpec(Base64Encoding.decode64(prop.getProperty("PrivateKey")));
            return factory.generatePrivate(keySpec);
        } catch (Exception e) {
            log.error("Message encryption key invalid.",e);
            return null;
        }
    }
}
