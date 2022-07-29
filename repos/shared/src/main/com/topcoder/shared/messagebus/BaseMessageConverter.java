/*
 * BaseMessageConverter
 * 
 * Created Oct 3, 2007
 */
package com.topcoder.shared.messagebus;

import java.util.Date;

import com.topcoder.shared.util.VMUtil;

/**
 * Sample message generated:
 * BusMessage
 *      version = 1.0  -  CURRENT_VERSIOn
 *      originVM = WEB_SEVER1  - (VM ID configured)
 *      originModule = EDU_CRUD  - moduleName in constructor
 *      messageType  = RoundEvent - messageType in constructor
 *      date = now
 *      bodyType = namespaces:simpleClassNameOfObj
 *      body = Obj
 * 
 * 
 * @author Diego Belfer (mural)
 * @version $Id$
 */
public abstract class BaseMessageConverter<T> implements MessageConverter<T> {
    private String moduleName;
    private String messageType;
    private String namespace;
    private String namespacePrefix;

    public BaseMessageConverter(String moduleName, String messageType, String namespace) {
        this.moduleName = moduleName;
        this.messageType = messageType;
        this.namespace = namespace;
        this.namespacePrefix = namespace+":";
    }

    public T fromMessage(BusMessage message) {
        if (message.getMessageBodyType().startsWith(namespacePrefix)) {
            return (T) message.getMessageBody();
        } else {
            throw new IllegalArgumentException("Unknown message body type received: "+message.getMessageBodyType());
        }
    }

    public BusMessage toMessage(T object) {
        BusMessage message = newMessage();
        message.setMessageOriginVM(VMUtil.getVMInstanceId());
        message.setMessageOriginModule(getModuleName());
        message.setMessageType(getMessageType());
        message.setMessageDate(new Date());
        message.setMessageBodyType(resolveMessageBodyType(object));
        message.setMessageBody(object);
        return message;
    }

    protected String resolveMessageBodyType(T object) {
        return buildBodyType(object.getClass().getSimpleName());
    }
    
    protected String buildBodyType(String simpleName) {
        return namespacePrefix + simpleName;
    }

    protected BusMessage newMessage() {
        return new BusMessage();
    }

    public String getModuleName() {
        return moduleName;
    }

    public String getMessageType() {
        return messageType;
    }

    public String getNamespace() {
        return namespace;
    }

    public String getNamespacePrefix() {
        return namespacePrefix;
    }

}
