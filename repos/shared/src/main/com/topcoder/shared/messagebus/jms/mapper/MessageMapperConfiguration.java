/*
 * MessageMapperConfiguration
 * 
 * Created Oct 6, 2007
 */
package com.topcoder.shared.messagebus.jms.mapper;

import java.util.regex.Pattern;

public class MessageMapperConfiguration {
    private String messageType; 
    private String bodyType;
    private String serializationMethod;
    
    private Pattern messageTypePattern; 
    private Pattern bodyTypePattern;
    private Pattern serializationMethodPattern;
    
    private String mapperFactoryClass;
    private MessageMapperFactory mapperFactory;
    
    public Pattern getMessageTypePattern() {
        return messageTypePattern;
    }
    public Pattern getBodyTypePattern() {
        return bodyTypePattern;
    }
    public void setMessageType(String messageType) {
        this.messageType = messageType;
        this.messageTypePattern = Pattern.compile(messageType);
    }
    public void setBodyType(String bodyType) {
        this.bodyType = bodyType;
        this.bodyTypePattern = Pattern.compile(bodyType);
    }
    
    public void setSerializationMethod(String serializationMethod) {
        this.serializationMethod = serializationMethod;
        this.serializationMethodPattern = Pattern.compile(serializationMethod);
    }
    public Pattern getSerializationMethodPattern() {
        return serializationMethodPattern;
    }
    public String getMapperFactoryClass() {
        return mapperFactoryClass;
    }
    
    public synchronized MessageMapperFactory getMapperFactory() throws MapperProviderException {
        if (mapperFactory == null) {
            try {
                mapperFactory = (MessageMapperFactory) Class.forName(mapperFactoryClass).newInstance();
            } catch (Exception e) {
                throw new MapperProviderException("Could not create factory class :"+mapperFactoryClass, e);
            }
        }
        return mapperFactory;
    }
    public void setMapperFactoryClass(String mapperFactoryClass) {
        this.mapperFactoryClass = mapperFactoryClass;
    }
    public String getMessageType() {
        return messageType;
    }
    public String getBodyType() {
        return bodyType;
    }
    public String getSerializationMethod() {
        return serializationMethod;
    }
}