package com.topcoder.server.webservice;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface GracefulWordsIF extends Remote {

    public String[] breakDownSentence(String s) throws RemoteException;

    public int sentenceGrace(String sentence) throws RemoteException;
}

