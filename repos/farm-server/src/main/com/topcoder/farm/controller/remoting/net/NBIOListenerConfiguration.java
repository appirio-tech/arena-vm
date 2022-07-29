/*
 * NBIOListenerConfiguration
 * 
 * Created 07/26/2006
 */
package com.topcoder.farm.controller.remoting.net;


/**
 * @author Diego Belfer (mural)
 * @version $Id$
 */
public class NBIOListenerConfiguration extends ConnectionListenerConfiguration {
    private int port;
    private int numAcceptThreads;
    private int numReadThreads;
    private int numWriteThreads;
    private String monitorClassName;
    private String csHandlerFactoryClassName;
    private String ipsStringList;
    private boolean isAllowedSet;
    private long scanInterval;
    private long keepAliveTimeout;
    
    public String getCsHandlerFactoryClassName() {
        return csHandlerFactoryClassName;
    }
    public void setCsHandlerFactoryClassName(String handlerFactoryClassName) {
        csHandlerFactoryClassName = handlerFactoryClassName;
    }
    public String getIpsStringList() {
        return ipsStringList;
    }
    public void setIpsStringList(String ipsStringList) {
        this.ipsStringList = ipsStringList;
    }
    public boolean isAllowedSet() {
        return isAllowedSet;
    }
    public void setAllowedSet(boolean isAllowedSet) {
        this.isAllowedSet = isAllowedSet;
    }
    public String getMonitorClassName() {
        return monitorClassName;
    }
    public void setMonitorClassName(String monitorClassName) {
        this.monitorClassName = monitorClassName;
    }
    public int getNumAcceptThreads() {
        return numAcceptThreads;
    }
    public void setNumAcceptThreads(int numAcceptThreads) {
        this.numAcceptThreads = numAcceptThreads;
    }
    public int getNumReadThreads() {
        return numReadThreads;
    }
    public void setNumReadThreads(int numReadThreads) {
        this.numReadThreads = numReadThreads;
    }
    public int getNumWriteThreads() {
        return numWriteThreads;
    }
    public void setNumWriteThreads(int numWriteThreads) {
        this.numWriteThreads = numWriteThreads;
    }
    public int getPort() {
        return port;
    }
    public void setPort(int port) {
        this.port = port;
    }
    public long getKeepAliveTimeout() {
        return keepAliveTimeout;
    }
    public void setKeepAliveTimeout(long keepAliveTimeOut) {
        this.keepAliveTimeout = keepAliveTimeOut;
    }
    public long getScanInterval() {
        return scanInterval;
    }
    public void setScanInterval(long scanInterval) {
        this.scanInterval = scanInterval;
    }
}
