/*
 * BaseLocalizableException
 * 
 * Created Oct 26, 2007
 */
package com.topcoder.shared.exception;

import com.topcoder.shared.i18n.Message;

/**
 * Base exception implementing {@link LocalizableException}. 
 * 
 * It allows localization of error messages, and formatting of message arguments.
 * 
 * @author Diego Belfer (mural)
 * @version $Id$
 */
public class BaseLocalizableException extends Exception implements LocalizableException {
    private Message localizableMessage;
    
    public BaseLocalizableException(String key) {
        super(key);
        this.localizableMessage = new Message(key);
    }
    
    public BaseLocalizableException(String bundleName, String key) {
        super(key);
        this.localizableMessage = new Message(bundleName, key);
    }
    
    public BaseLocalizableException(String bundleName, String key, Object[] messageArgs) {
        super(key);
        this.localizableMessage = new Message(bundleName, key, messageArgs);
    }
    
    public BaseLocalizableException(Message message) {
        super(message.getKey());
        this.localizableMessage = message;
    }

    /**
     * Returns the localizable message describing the reason of the failure
     * 
     * @return the message
     * 
     * @see com.topcoder.shared.i18n.MessageProvider
     */
    public Message getLocalizableMessage() {
        return localizableMessage;
    }
    
    
    public String toString() {
        return super.toString() + " [message="+localizableMessage+"]";
    }
}
