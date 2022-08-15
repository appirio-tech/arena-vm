/*
 * JMSConfigurationParser
 * 
 * Created Oct 6, 2007
 */
package com.topcoder.shared.messagebus.jms;

import java.io.IOException;
import java.io.InputStream;

import org.apache.commons.digester.BeanPropertySetterRule;
import org.apache.commons.digester.Digester;
import org.apache.commons.digester.ExtendedBaseRules;
import org.xml.sax.SAXException;

import com.topcoder.shared.messagebus.jms.mapper.MessageMapperConfiguration;

/**
 * @author Diego Belfer (mural)
 * @version $Id$
 */
public class JMSConfigurationParser {

    public JMSBusConfiguration getConfiguration(String resourcePath) throws IOException, SAXException {
        InputStream resource = JMSConfigurationParser.class.getResourceAsStream(resourcePath);
        return getConfiguration(resource);
        
    }

    public JMSBusConfiguration getConfiguration(InputStream resource) throws IOException, SAXException {
        Digester digester = new Digester();
        digester.setRules(new ExtendedBaseRules());
        digester.setValidating(false);
        digester.addObjectCreate("bus", JMSBusConfiguration.class);
        digester.addSetProperties("bus");
        
        digester.addObjectCreate("bus/channels/channel", JMSChannelConfiguration.class);
        digester.addSetProperties("bus/channels/channel");
        digester.addSetNext("bus/channels/channel", "addChannel", JMSChannelConfiguration.class.getName());
        digester.addRule("bus/channels/channel/*", new BeanPropertySetterRule());
        
        digester.addObjectCreate("bus/channels/channel/property", JMSChannelConfiguration.Property.class);
        digester.addSetNestedProperties("bus/channels/channel/property");
        digester.addSetProperties("bus/channels/channel/property");
        digester.addSetNext("bus/channels/channel/property", "addProperty", JMSChannelConfiguration.Property.class.getName());
        
        
        digester.addObjectCreate("bus/connectors/connector", JMSConnectorConfiguration.class);
        digester.addSetProperties("bus/connectors/connector");
        digester.addSetNestedProperties("bus/connectors/connector");
        digester.addSetNext("bus/connectors/connector", "addConnector", JMSConnectorConfiguration.class.getName());
        
        digester.addObjectCreate("bus/mappers/mapper", MessageMapperConfiguration.class);
        digester.addSetProperties("bus/mappers/mapper");
        digester.addSetNestedProperties("bus/mappers/mapper");
        digester.addSetNext("bus/mappers/mapper", "addMapper", MessageMapperConfiguration.class.getName());
        JMSBusConfiguration cfg = (JMSBusConfiguration) digester.parse(resource);
        return cfg;
    }
    
    public JMSBusConfiguration getConfiguration() throws IOException, SAXException {
        return getConfiguration("/messagebus.xml");
    }
}
