/*
 * Copyright (C) 2014 TopCoder Inc., All Rights Reserved.
 */
package com.topcoder.server.listener.wss.request;

/**
 * The request to login.
 *
 * @author Standlove
 * @version 1.0
 */
public class LoginRequest {
    /**
     * The user name
     */
    private String username;

    /**
     * The password
     */
    private String password;

    /**
     * Empty constructor.
     */
    public LoginRequest() {
    }

    /**
     * Creates a new instance of this class.
     *
     * @param   username    the username
     * @param   password    the password
     */
    public LoginRequest(String username, String password) {
        this.username = username;
        this.password = password;
    }

    /**
     * Getter for the user name.
     *
     * @return the user name.
     */
    public String getUsername() {
        return username;
    }

    /**
     * Setter for the user name.
     *
     * @param username the user name.
     */
    public void setUsername(String username) {
        this.username = username;
    }

    /**
     * Getter for the password.
     *
     * @return the password
     */
    public String getPassword() {
        return password;
    }

    /**
     * Setter for the password
     *
     * @param password the password
     */
    public void setPassword(String password) {
        this.password = password;
    }
}
