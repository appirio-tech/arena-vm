package com.topcoder.shared.screening.common;

import com.topcoder.shared.util.TCException;

public class TimeExpiredException extends TCException {

    /**
     * Default Constructor
     */
    public TimeExpiredException() {
        super();
    }

    /**
     * <p>
     * Constructor taking a string message
     * </p>
     *
     * @param message - the message of the exception
     */
    public TimeExpiredException(String message) {
        super(message);
    }
    
    public TimeExpiredException(String message, boolean logout) {
        super(message);
        this.logout = logout;
    }
    
    public boolean isLogout() {
        return logout;
    }
    
    private boolean logout = false;

    /**
     * <p>
     * Constructor taking a nested exception
     * </p>
     *
     * @param nestedException the nested exception
     */
    public TimeExpiredException(Throwable nestedException) {
        super(nestedException);
    }

    /**
     * <p>
     * Constructor taking a nested exception and a string
     * </p>
     *
     * @param message the message of this exception
     * @param nestedException the nested exception
     */
    public TimeExpiredException(String message, Throwable nestedException) {
        super(message, nestedException);
    }

}
