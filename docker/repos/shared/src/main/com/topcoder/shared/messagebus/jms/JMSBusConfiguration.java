/*
 * JMSBusConfiguration
 * 
 * Created Oct 6, 2007
 */
package com.topcoder.shared.messagebus.jms;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.topcoder.shared.messagebus.jms.mapper.MessageMapperConfiguration;

/**
 * Bus Configuration for JMS implementations.
 * 
 * @author Diego Belfer (mural)
 * @version $Id$
 */
public class JMSBusConfiguration {
    /**
     * Channels configurations. Contains JMS specific configuration as destinations, selector strings, etc.
     */
    private Map<String, JMSChannelConfiguration> channels = new HashMap<String, JMSChannelConfiguration>();
    /**
     * Connector configurations. This contains configuration for Publishers, and Listeners
     */
    private Map<String, JMSConnectorConfiguration> connectors = new HashMap<String, JMSConnectorConfiguration>();
    /**
     * Message mappers configured
     */
    private List<MessageMapperConfiguration> mappers = new ArrayList<MessageMapperConfiguration>();
    
    /**
     * Adds a channel configuration. If this channel extends other channel configuration.
     * This configuration is filled from the extended channel.
     * 
     * @param channel The channel to add
     */
    public void addChannel(JMSChannelConfiguration channel) {
        if (channel.getExtendsConfig() != null) {
            channel.fillFrom(channels.get(channel.getExtendsConfig()));
        }
        channels.put(channel.getName(), channel);
    }
    
    /**
     * Adds a new connector to this configuration
     * 
     * @param connector The connector to add
     */
    public void addConnector(JMSConnectorConfiguration connector) {
        connectors.put(buildKey(connector.getType(), connector.getKey(), connector.getModule()), connector);
    }
    
    private String buildKey(String type, String key, String module) {
        if (module != null && module.trim().length() == 0) {
            module = null;
        }
        StringBuilder sb = new StringBuilder(type.length()+key.length()+ (module!=null ? module.length() : 0) +12);
        sb.append(type).append("||")
          .append(key).append("||")
          .append(module);
        return sb.toString();
    }

    /**
     * Add a mapper configuration.
     * 
     * @param mapper The mapper to add.
     */
    public void addMapper(MessageMapperConfiguration mapper) {
        mappers.add(mapper);
    }
    
    /**
     * Returns the Map containing all channels configured within this configuration
     * 
     * @return The map
     */
    public Map<String, JMSChannelConfiguration> getChannels() {
        return channels;
    }

    /**
     * Returns the Map containing all connectors configured in this configuration 
     * @return the map
     */
    public Map<String, JMSConnectorConfiguration> getConnectors() {
        return connectors;
    }

    /**
     * Returns the list of Mappers configurations in this configuration
     * @return the list
     */
    public List<MessageMapperConfiguration> getMappers() {
        return mappers;
    }

    /**
     * Given a type of connector a configuration key and a module name returns the  channel Configuration that should be used by the connector
     * 
     * @param type Type of connector
     * @param configurationKey The configuration key
     * @param moduleName The module Name requesting the connector
     * @return The {@link JMSChannelConfiguration} that the connector should use.
     * 
     * @throws ConfigurationNotFoundException If a configuration was not found for the connector.
     */
    public JMSChannelConfiguration getChannelForConnector(String type, String configurationKey, String moduleName) throws ConfigurationNotFoundException {
        JMSConnectorConfiguration connector = getConnectorConfiguration(type, configurationKey, moduleName);
        if (connector == null) {
            connector = getConnectorConfiguration(type, configurationKey, null);
            if (connector == null) {
                throw new ConfigurationNotFoundException("Could not find connector configuration: type="+type+" configurationKey="+configurationKey+" moduleName="+moduleName);
            }
        }
        String channelName = connector.getChannel();
        return getChannel(channelName);
    }

    private JMSChannelConfiguration getChannel(String channelName) throws ConfigurationNotFoundException {
        JMSChannelConfiguration channelConfiguration = channels.get(channelName);
        if (channelConfiguration == null) {
            throw new ConfigurationNotFoundException("Could not find channel configuration: channel="+channelName);
        }
        return channelConfiguration;
    }

    private JMSConnectorConfiguration getConnectorConfiguration(String type, String configurationKey, String moduleName) {
        return connectors.get(buildKey(type, configurationKey, moduleName));
    }
}
