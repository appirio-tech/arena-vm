package com.topcoder.services.compiler.util;

public final class CompileResult {

    private final boolean result;
    private final String error;

    CompileResult(boolean result, String error) {
        this.result = result;
        this.error = error;
    }

    public boolean getResult() {
        return result;
    }

    public String getError() {
        return error;
    }

}
