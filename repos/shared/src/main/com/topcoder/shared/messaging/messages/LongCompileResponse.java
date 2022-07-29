package com.topcoder.shared.messaging.messages;

import java.io.Serializable;

public class LongCompileResponse implements Serializable{
    private String compileError;
    private boolean compileStatus;
    /**
     * @param compileError
     * @param compileStatus
     */
    public LongCompileResponse(String compileError, boolean compileStatus) {
        super();
        this.compileError = compileError;
        this.compileStatus = compileStatus;
    }
    /**
     * @return Returns the compileError.
     */
    public String getCompileError() {
        return compileError;
    }
    /**
     * @return Returns the compileStatus.
     */
    public boolean getCompileStatus() {
        return compileStatus;
    }
}