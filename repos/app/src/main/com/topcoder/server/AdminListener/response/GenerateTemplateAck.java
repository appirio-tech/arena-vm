/*
 * GenerateTemplateAck.java
 *
 * Created on October 24, 2005, 10:36 AM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package com.topcoder.server.AdminListener.response;

/**
 *
 * @author rfairfax
 */
public class GenerateTemplateAck extends ContestManagementAck {

    public GenerateTemplateAck() {
        super();
    }
    
    public GenerateTemplateAck(String message) {
        super(true, message);
    }

    public GenerateTemplateAck(Throwable errorDetails) {
        super(errorDetails);
    }
}
