/*
 * XMLConfigurationProvider
 * 
 * Created 08/30/2006
 */
package com.topcoder.farm.processor.configuration;

import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;

import com.topcoder.farm.shared.xml.XMLHelper;

/**
 * @author Diego Belfer (mural)
 * @version $Id$
 */
class XMLConfigurationProvider extends ProcessorConfigurationProvider {
    private URL xmlURL;

    public XMLConfigurationProvider() {
        try {
            xmlURL = new URL(System.getProperty("configuration.xml.url"));
        } catch (MalformedURLException e) {
            throw new IllegalStateException("configuration.xml.url is invalid",e);
        }
    }
    
    protected ProcessorConfiguration buildConfiguration() {
        return buildFromURL(xmlURL);
    }
    
    private ProcessorConfiguration buildFromURL(URL xmlURL) {
        try {
            InputStream is = xmlURL.openStream();
            ProcessorConfiguration configuration = (ProcessorConfiguration) XMLHelper.getInstance().fromXML(is);
            is.close();
            return configuration; 
        } catch (Exception e) {
            throw new IllegalStateException("Cannot build configuration", e);
        }
    }
}
