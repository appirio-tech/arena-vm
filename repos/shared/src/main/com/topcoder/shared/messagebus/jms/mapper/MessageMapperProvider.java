/*
 * MessageMapperProvider
 * 
 * Created Oct 3, 2007
 */
package com.topcoder.shared.messagebus.jms.mapper;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Diego Belfer (mural)
 * @version $Id$
 */
public class MessageMapperProvider {
    private List<MessageMapperConfiguration> configs;
    private Map<String, MessageMapperFactory> factoryCache = new HashMap<String, MessageMapperFactory>();
   
    public MessageMapperProvider(List<MessageMapperConfiguration> configs) {
        this.configs = configs;
    }
    
    public MessageMapper getMapper(String messageType, String messageBodyType, String serializationMethod) throws MapperNotFoundException, MapperProviderException {
        MessageMapperFactory factory = factoryCache.get(buildKey(messageType, messageBodyType, serializationMethod));
        if (factory == null) {
            factory = getFactoryFromConfig(messageType, messageBodyType, serializationMethod);
        }
        return factory.create();
    }

    public MessageMapper getMapper(String messageType, String messageBodyType) throws MapperNotFoundException, MapperProviderException {
        return getMapper(messageType, messageBodyType, null);
    }
    
    private String buildKey(String messageType, String messageBodyType, String serializationMethod) {
        StringBuilder sb = new StringBuilder(messageBodyType.length()+messageBodyType.length()+(serializationMethod!=null ? serializationMethod.length() : 0)+12);
        sb.append(messageType).append("||")
          .append(messageBodyType).append("||")
          .append(serializationMethod);
        return sb.toString();
    }

    private MessageMapperFactory getFactoryFromConfig(String messageType, String messageBodyType, String serializationMethod) throws MapperNotFoundException, MapperProviderException {
        for (MessageMapperConfiguration cfg : configs) {
            if (cfg.getMessageTypePattern().matcher(messageType).matches() && cfg.getBodyTypePattern().matcher(messageBodyType).matches()) {
                if (cfg.getSerializationMethodPattern() == null || (serializationMethod != null && cfg.getSerializationMethodPattern().matcher(serializationMethod).matches())) {
                    MessageMapperFactory mapperFactory = cfg.getMapperFactory();
                    factoryCache.put(buildKey(messageType, messageBodyType, serializationMethod), mapperFactory);
                    return mapperFactory;
                }
            }
        }
        throw new MapperNotFoundException("Could not find a mapper for: messageType="+messageType+" messageBodyType="+messageBodyType+" serializationMethod="+serializationMethod);
    }

}
