package com.topcoder.server.mpsqas.listener.impl;

import com.topcoder.netCommon.mpsqas.*;
import com.topcoder.netCommon.mpsqas.communication.message.DeployWebServiceRequest;
import com.topcoder.server.mpsqas.webservice.*;
import com.topcoder.netCommon.mpsqas.communication.message.MessageProcessor;
import com.topcoder.netCommon.mpsqas.communication.Peer;
import com.topcoder.server.webservice.*;
import com.topcoder.server.common.*;
import com.topcoder.shared.language.*;

import java.util.*;
import java.rmi.RemoteException;

import org.apache.log4j.Logger;

/**
 * Calls on the MPSQASService bean to deploy a web service.
 *
 * @author mitalub
 */
public class DeployWebServiceRequestImpl
        extends DeployWebServiceRequest
        implements MessageProcessor {

    private static String BASE_PATH =
            WebServiceGeneratorResources.getProperty(
                    WebServiceGeneratorResources.WEB_SERVICE_SOURCE_BASE_PATH);

    public void process(Peer peer) {
        MPSQASProcessorPeer mpeer = (MPSQASProcessorPeer) peer;
        Logger logger = Logger.getLogger(getClass());

        if (!mpeer.isLoggedIn()) {
            mpeer.sendErrorMessage("Not logged in.");
            logger.info("Unauthorized user tried to deploy web service.");
        } else {
            boolean hasPermissionToDeploy = false;
            try {
                hasPermissionToDeploy = mpeer.getServices().hasPermissionToDeploy(
                        getWebService().getWebServiceId(), mpeer.getUserId());
            } catch (Exception e) {
                logger.error("Error calling bean to get permissions.", e);
                mpeer.sendErrorMessage(ApplicationConstants.SERVER_ERROR);
            }

            if (hasPermissionToDeploy) {
                boolean success = false;
                WebServiceInformation webService = getWebService();

                int problemId = webService.getProblemId();
                String serviceName = webService.getName();

                WebServiceRemoteFile interfaceFile = new WebServiceRemoteFile(
                        BASE_PATH + serviceName + "/" +
                        webService.getInterfaceClass() + ".java",
                        webService.getSource(webService.getInterfaceClass() + ".java")
                        .getBytes(),
                        WebServiceRemoteFile.WEB_SERVICE_INTERFACE, JavaLanguage.ID);

                WebServiceRemoteFile implementationFile = new WebServiceRemoteFile(
                        BASE_PATH + serviceName + "/" + webService
                        .getImplementationClass() + ".java",
                        webService.getSource(webService.getImplementationClass() + ".java")
                        .getBytes(),
                        WebServiceRemoteFile.WEB_SERVICE_IMPLEMENTATION, JavaLanguage.ID);

                WebServiceRemoteFile[] helperFiles = new WebServiceRemoteFile[
                        webService.getHelperClasses().size()];
                for (int i = 0; i < helperFiles.length; i++) {
                    String className = (String) webService.getHelperClasses().get(i);
                    helperFiles[i] = new WebServiceRemoteFile(
                            BASE_PATH + serviceName + "/" + className + ".java",
                            webService.getSource(className + ".java").getBytes(),
                            WebServiceRemoteFile.WEB_SERVICE_USER_HELPER,
                            JavaLanguage.ID);
                }

                //insert the source into the DB
                boolean insertOk = true;

                try {
                    List source = new ArrayList();
                    source.add(interfaceFile);
                    source.add(implementationFile);
                    for (int i = 0; i < helperFiles.length; i++)
                        source.add(helperFiles[i]);
                    insertOk = mpeer.getServices().setWebServiceServer(
                            getWebService().getWebServiceId(), source);
                } catch (Exception e) {
                    logger.error("Error setting web service server files.", e);
                    insertOk = false;
                }

                if (insertOk) {
                    WebServiceProblem wsp = new WebServiceProblem(problemId,
                            serviceName, interfaceFile, implementationFile, helperFiles);

                    //now send it on over to the generator, catching any exceptions to
                    //return to the user.
                    try {
                        WebServiceDeploymentResult result =
                                WebServiceWaiter.deployService(wsp);

                        if (result.success()) {
                            mpeer.sendMessage("Web service deployed.");
                        } else {
                            mpeer.sendErrorMessage("Error deploying web service:\n"
                                    + result.getExceptionText());
                        }
                    } catch (Exception e) {
                        logger.error("Error using WebServiceWaiter.", e);
                        mpeer.sendErrorMessage("Server error deploying service.");
                    }
                } else {
                    mpeer.sendErrorMessage("Server error saving web service source.");
                }
            } else {
                mpeer.sendErrorMessage("You do not have permission to deploy the web "
                        + "service.");
            }
        }
    }
}

