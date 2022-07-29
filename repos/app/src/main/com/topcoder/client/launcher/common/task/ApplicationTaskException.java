package com.topcoder.client.launcher.common.task;

public class ApplicationTaskException extends Exception {
    public ApplicationTaskException() {
        super();
    }
    
    public ApplicationTaskException(String msg) {
        super(msg);
    }
    
    public ApplicationTaskException(String msg, Throwable cause) {
        super(msg, cause);
    }
    
    public ApplicationTaskException(Throwable cause) {
        super(cause);
    }
}
