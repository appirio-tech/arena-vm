package com.topcoder.server.webservice.remoteserver;

/**
 * <p>Title: CSServer</p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2002</p>
 * <p>Company: TopCoder</p>
 * @author Jeremy Nuanes
 * @version 1.0
 */

import com.topcoder.server.common.RemoteFile;
import com.topcoder.server.util.FileUtil;
import com.topcoder.server.webservice.WebServiceRemoteFile;
import com.topcoder.server.webservice.WebServiceGeneratorResources;
import com.topcoder.shared.language.*;

import java.rmi.server.UnicastRemoteObject;
import java.rmi.RemoteException;
import java.rmi.NotBoundException;
import java.rmi.Naming;
import java.net.MalformedURLException;
import java.io.File;
import java.io.IOException;
import java.lang.InterruptedException;
import java.lang.Process;
import java.lang.Runtime;

public class CSServer extends UnicastRemoteObject implements ICSServer {

    private static final String CS_SERVER_NAME = "WebServiceGeneratorCSServer";

    private CSServer() throws RemoteException {
        super();
    }

    public static ICSServer createInstanceOf() throws NotBoundException, MalformedURLException, RemoteException {
        String csRMIServerURL = null;
        String port = WebServiceGeneratorResources.getProperty(WebServiceGeneratorResources.REMOTE_SERVER_PORT);
        if (port != null && port.length() != 0)
            csRMIServerURL = "rmi://localhost:" + port + "/" + CS_SERVER_NAME;
        else
            csRMIServerURL = "rmi://localhost/" + CS_SERVER_NAME;
        ICSServer server = new CSServer();
        Naming.rebind(csRMIServerURL, server);
        //Ensure that RMI is not down.
        return (ICSServer) Naming.lookup(csRMIServerURL);
    }

    public static ICSServer getInstanceOf(String server, String port) throws NotBoundException, MalformedURLException, RemoteException {
        if (port != null && port.length() != 0)
            return (ICSServer) Naming.lookup("rmi://" + server + ":" + port + "/" + CS_SERVER_NAME);
        return (ICSServer) Naming.lookup("rmi://" + server + "/" + CS_SERVER_NAME);
    }

    // generate and return the C# code for the client of the WebService
    public WebServiceRemoteFile generateCSClient(String serviceName, RemoteFile wsdlRemoteFile) throws InterruptedException, IOException, RemoteException {
        WebServiceRemoteFile remoteFile = null;
        File csClientLocation = new File(WebServiceGeneratorResources.getProperty(WebServiceGeneratorResources.WINDOWS_TEMP_LOCATION), "cs\\" + serviceName);
        try {
            if (!csClientLocation.exists()) {
                if (!csClientLocation.mkdirs())
                    throw new IOException("Could not construct directory " + csClientLocation.getPath());
            }
            File wsdlFile = wsdlRemoteFile.reconstruct(csClientLocation);
            File outputFile = new File(csClientLocation, serviceName + ".cs");
            Process process = Runtime.getRuntime().exec(WebServiceGeneratorResources.getProperty(WebServiceGeneratorResources.WSDLEXE_FULL_PATH) + " /nologo /out:" + outputFile.getPath() + " " + wsdlFile.getPath());
            if (process.waitFor() != 0)
                throw new IOException("Could not generate client side C# classes!");
            //XXX: Hack!! removes strange characters in first line before comment
            String contents = new String(FileUtil.getContents(outputFile));
            contents = contents.substring(contents.indexOf("/"));

            //XXX: Hack!! replaces "REPLACE_WITH_ACTUAL_URL" with actual urs
            contents = contents.replaceAll("REPLACE_WITH_ACTUAL_URL",
                    "http://" +
                    WebServiceGeneratorResources.getProperty(WebServiceGeneratorResources.TOMCAT_SERVER) +
                    ":" +
                    WebServiceGeneratorResources.getProperty(WebServiceGeneratorResources.TOMCAT_SERVER_PORT) +
                    "/" +
                    serviceName +
                    "/topcoderws");
            remoteFile = new WebServiceRemoteFile(outputFile.getName(), contents.getBytes(), WebServiceRemoteFile.WEB_SERVICE_CLIENT_SOURCE, CSharpLanguage.ID);
        } finally {
            //  clean up File
            FileUtil.removeFile(csClientLocation);
        }
        return remoteFile;
    }

    public static void main(String[] args) throws Exception {
        ICSServer server = CSServer.createInstanceOf();
    }
}
