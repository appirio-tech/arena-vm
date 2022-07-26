package com.topcoder.server.services.authenticate;

import java.net.URL;
import javax.xml.rpc.Service;
import javax.xml.rpc.JAXRPCException;
import javax.xml.namespace.QName;
import javax.xml.rpc.ServiceFactory;

public class OracleAuthenticateClient {

    private static final String providerCode = "A5BD3997CFEB7032E0340003BA09E100";
    private static final String nameSpaceUri = "http://otn.oracle.com/wstns/oracle/otn/utils/security/TopCoderSecurityManager";
    private static final String urlString = "http://otn.oracle.com/ws//topcoder?WSDL";
    private static final String serviceName = "TopCoderSecurityManager";
    private static boolean testing = false;

    /**
     *
     * @param username
     * @param password
     * @return "-1" if invalid, unique alphanumeric code otherwise.
     */
    public static String authenticate(String username, String password) {
        try {
            if (testing && password.equals("foo")) return "A742997C3F4E6515E034080020C594BD" + username;
            if (testing) return "-1";
            String portName = serviceName + "Port";
            URL authenticateWsdlUrl = new URL(urlString);
            ServiceFactory serviceFactory = ServiceFactory.newInstance();
            Service authenticateService = serviceFactory.createService(authenticateWsdlUrl, new QName(nameSpaceUri, serviceName));
            OracleAuthenticatorPort myProxy = (OracleAuthenticatorPort) authenticateService.getPort(
                    new QName(nameSpaceUri, portName),
                    OracleAuthenticatorPort.class);
            String ret = myProxy.authenticateUser(providerCode, username, password);
//    System.out.println(ret);
            return ret;
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return "-1";
    }

    public static void main(String args[]) {
        testing = false;
        System.out.println(authenticate(args[0], args[1]));
    }
}
