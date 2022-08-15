package com.topcoder.server.webservice;

import java.io.*;

/**
 * A class used for returning the results of a web service deployement.
 *
 * @author mitalub
 */
public class WebServiceDeploymentResult implements Serializable {

    private boolean success;
    private String exceptionText;

    /**
     * @param success This is <code>true</code> if the web service is built
     *                and deployed without problems.
     * @param exceptionText Contains any error messages.
     */
    public WebServiceDeploymentResult(boolean success, String exceptionText) {
        this.success = success;
        this.exceptionText = exceptionText;
    }

    /**
     * Constructs a WebServiveDeploymentResult with no exception text.
     */
    public WebServiceDeploymentResult(boolean success) {
        this(success, "");
    }

    /**
     * Returns whether the deployment was succesful.
     */
    public boolean success() {
        return this.success;
    }

    /**
     * Returns the exception text.
     */
    public String getExceptionText() {
        return this.exceptionText;
    }
}

