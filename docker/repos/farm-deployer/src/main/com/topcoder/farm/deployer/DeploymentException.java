/*
 * DeploymentException
 * 
 * Created 10/19/2006
 */
package com.topcoder.farm.deployer;

/**
 * Exception thrown if the Application could not be deployed
 * 
 * @author Diego Belfer (mural)
 * @version $Id$
 */
public class DeploymentException extends Exception {

    public DeploymentException(String message, Throwable cause) {
        super(message, cause);
    }

    public DeploymentException(String message) {
        super(message);
    }

    public DeploymentException(Throwable cause) {
        super(cause);
    }

    public DeploymentException() {
    }
}
