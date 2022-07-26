package com.topcoder.server.webservice.remoteserver;

/**
 * <p>Title: </p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2002</p>
 * <p>Company: TopCoder</p>
 * @author Jeremy Nuanes
 * @version 1.0
 */

import com.topcoder.server.common.RemoteFile;
import com.topcoder.server.webservice.WebServiceRemoteFile;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.io.File;
import java.io.IOException;
import java.lang.InterruptedException;

public interface ICSServer extends Remote {

    // generate and return the C# code for the client of the WebService
    // File is passed to the service as a string, this is so setting up the server
    // is easier.  Don't have to have a repository of WSDL files with URLs
    // Return a WebServiceRemoteFile.

    public WebServiceRemoteFile generateCSClient(String serviceName, RemoteFile wsdlRemoteFile) throws InterruptedException, IOException, RemoteException;
}