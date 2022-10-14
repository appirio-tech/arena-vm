package com.topcoder.client.mpsqasApplet.messaging;

import com.topcoder.netCommon.mpsqas.WebServiceInformation;

/**
 * Interface for a Web Service Request Processor.
 *
 * @author mitalub
 */
public interface WebServiceRequestProcessor {

    public void deployWebService(WebServiceInformation webService);

    public void generateJavaDocs();
}
