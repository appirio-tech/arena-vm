package com.topcoder.server.common;

import java.io.Serializable;

public final class RegistrationResult implements Serializable {

    private final boolean registered;
    private final String message;

    private RegistrationResult(boolean registered) {
        this(registered, null);
    }

    private RegistrationResult(boolean registered, String message) {
        this.registered = registered;
        this.message = message;
    }

    public static RegistrationResult getSuccessfulRegistration() {
        return new RegistrationResult(true);
    }

    public static RegistrationResult getUnsuccessfulRegistration(String message) {
        return new RegistrationResult(false, message);
    }

    public boolean isRegistered() {
        return registered;
    }

    public String getMessage() {
        return message;
    }

}
