/*
 * LongContestServicesException
 * 
 * Created 07/17/2007
 */
package com.topcoder.server.ejb.TestServices;

import com.topcoder.shared.i18n.Message;


/**
 * Exceptions thrown by the LongContestServices when the operation failed,
 * by any reason.
 * 
 * The localizableMessage contains a description of reason
 * 
 * @autor Diego Belfer (Mural)
 * @version $Id: LongContestServicesException.java 65513 2007-09-24 19:31:43Z thefaxman $
 */
@SuppressWarnings("serial")
public class LongContestServicesException extends Exception {
    private Message localizableMessage;
    
    public LongContestServicesException(String message) {
        super(message);
        this.localizableMessage = new Message(LongContestServices.class.getName(), message);
    }
    
    public LongContestServicesException(String message, Throwable cause) {
        super(message, cause);
        this.localizableMessage = new Message(LongContestServices.class.getName(), message);
    }
    
    public LongContestServicesException(String message, Object[] messageArgs) {
        super(message);
        this.localizableMessage = new Message(LongContestServices.class.getName(), message, messageArgs);
    }
    
    public LongContestServicesException(Message message) {
        super(message.getKey());
        this.localizableMessage = message;
    }

    /**
     * Returns the localizable message describring the reason of the failure
     * 
     * @return the message
     * 
     * @see com.topcoder.shared.i18n.MessageProvider
     */
    public Message getLocalizableMessage() {
        return localizableMessage;
    }
}
