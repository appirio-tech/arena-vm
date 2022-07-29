/*
 * Author: Michael Cervantes (emcee)
 * Date: Jun 24, 2002
 * Time: 1:16:45 AM
 */
package com.topcoder.server.services;

import java.rmi.RemoteException;
import java.util.List;

import javax.ejb.CreateException;
import javax.naming.NamingException;

import com.topcoder.server.ejb.TestServices.TestServices;
import com.topcoder.server.ejb.TestServices.TestServicesException;
import com.topcoder.server.ejb.TestServices.TestServicesLocator;
import com.topcoder.shared.util.logging.Logger;
import com.topcoder.server.webservice.WebServiceRemoteFile;

public class TeamServices {

    private static Logger log = Logger.getLogger(TeamServices.class);

    private static TeamServices singleton;

    public static TeamServices getInstance() {
        if (singleton == null)
            singleton = new TeamServices();
        return singleton;
    }

    private TestServices testServices;

    private TeamServices() {
        try {
            log.debug("Initializing TeamServices...");
            testServices = TestServicesLocator.getService();
        } catch (NamingException e) {
            log.fatal("Error looking up TestServices", e);
            throw new IllegalStateException();
        } catch (CreateException e) {
            log.fatal("Error creating TestServices", e);
            throw new IllegalStateException();
        } catch (RemoteException e) {
            log.fatal("Error creating TestServices", e);
            throw new IllegalStateException();
        }
    }

    public boolean isTeamComponent(long componentId) throws TeamServicesException {
        try {
            return testServices.isTeamComponent(componentId);
        } catch (TestServicesException e) {
            throw new TeamServicesException(e);
        } catch (RemoteException e) {
            throw new TeamServicesException(e);
        }
    }

    public void setWebServiceClients(String serviceName, int languageID, List sourceFiles) throws TeamServicesException {
        try {
            testServices.setWebServiceClients(serviceName, languageID, sourceFiles);
        } catch (TestServicesException e) {
            throw new TeamServicesException(e);
        } catch (RemoteException e) {
            throw new TeamServicesException(e);
        }
    }

    public WebServiceRemoteFile[] getWebServiceClients(long problemID, int languageID) throws TeamServicesException {
        try {
            return testServices.getWebServiceClientsForProblem(problemID, languageID);
        } catch (TestServicesException e) {
            throw new TeamServicesException(e);
        } catch (RemoteException e) {
            throw new TeamServicesException(e);
        }
    }

    public void saveCompiledWebServiceClients(long webServiceClientID, WebServiceRemoteFile[] compiledClasses) throws TeamServicesException {
        try {
            testServices.saveCompiledWebServiceClients(webServiceClientID, compiledClasses);
        } catch (TestServicesException e) {
            throw new TeamServicesException(e);
        } catch (RemoteException e) {
            throw new TeamServicesException(e);
        }
    }
}
