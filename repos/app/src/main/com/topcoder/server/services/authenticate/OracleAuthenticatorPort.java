package com.topcoder.server.services.authenticate;

import java.rmi.RemoteException;

public interface OracleAuthenticatorPort extends java.rmi.Remote {

    public String authenticateUser(String providerCode, String otnUsername, String otnPassword) throws RemoteException;
}
