/*
 * JMSConnectorConfiguration
 * 
 * Created Oct 6, 2007
 */
package com.topcoder.shared.messagebus.jms;

/**
 * @author Diego Belfer (mural)
 * @version $Id$
 */
public class JMSConnectorConfiguration {
    private String type;
    private String key;
    private String module;
    private String channel;
    
    public String getType() {
        return type;
    }
    public void setType(String type) {
        this.type = type;
    }
    public String getModule() {
        return module;
    }
    public void setModule(String moduleName) {
        this.module = moduleName;
    }
    public String getChannel() {
        return channel;
    }
    public void setChannel(String channel) {
        this.channel = channel;
    }
    public String getKey() {
        return key;
    }
    public void setKey(String key) {
        this.key = key;
    }
}
