/*
 * XMLConfigurationProvider
 * 
 * Created 08/30/2006
 */
package com.topcoder.farm.controller.configuration;

import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;

import com.topcoder.farm.shared.xml.XMLHelper;

/**
 * @author Diego Belfer (mural)
 * @version $Id$
 */
class XMLConfigurationProvider extends ControllerConfigurationProvider {
    private static final String CONFIGURATION_XML_URL_KEY = "configuration.xml.url";
    private URL xmlURL;

    public XMLConfigurationProvider() {
        try {
            xmlURL = new URL(System.getProperty(CONFIGURATION_XML_URL_KEY));
        } catch (MalformedURLException e) {
            throw new IllegalStateException("configuration.xml.url is invalid",e);
        }
    }
    
    protected ControllerConfiguration buildConfiguration() {
        return buildFromURL(xmlURL);
    }
    
    private ControllerConfiguration buildFromURL(URL xmlURL) {
        try {
            InputStream is = xmlURL.openStream();
            ControllerConfiguration configuration = (ControllerConfiguration) XMLHelper.getInstance().fromXML(is);
            is.close();
            return configuration; 
        } catch (Exception e) {
            throw new IllegalStateException("Cannot build configuration", e);
        }
    }
}
