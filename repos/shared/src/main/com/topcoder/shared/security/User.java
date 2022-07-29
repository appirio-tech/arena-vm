package com.topcoder.shared.security;

import java.io.Serializable;

/**
 * interface for User objects.
 *
 * @author Greg Paul, Ambrose Feinstein
 */
public interface User extends Serializable {
    /** Constants for anonymous users */
    public long getId();

    public String getUserName();

    public String getPassword();

    public boolean isAnonymous();
}
