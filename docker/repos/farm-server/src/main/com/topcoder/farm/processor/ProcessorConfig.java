/*
* Copyright (C) - 2014 TopCoder Inc., All Rights Reserved.
*/
package com.topcoder.farm.processor;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * Configuration object for a processor process. These values are populated by
 * processor-applicationContext.xml.
 *
 * <p>
 * Changes in version 1.1 (Fix New Arena Services Running in Arena VM v1.0) :
 * <ol>
 *      <li>Add {@link #cppArguments} field.</li>
 *      <li>Add {@link #CPP_OPTIONS} field.</li>
 *      <li>Add {@link #DEFAULT_MATCH_CPP_NO_THREAD_OPTIONS} field.</li>
 *      <li>Add {@link #DEFAULT_MARATHON_CPP_NO_THREAD_OPTIONS} field.</li>
 *      <li>Add {@link #getCppArguments()} method.</li>
 *      <li>Update {@link #getMatchCppNoThreadingOptions()} method.</li>
 *      <li>Update {@link #getMarathonCppNoThreadingOptions()} method.</li>
 *      <li>Add {@link #setCppArguments(String cppArguments)} method.</li>
 * </ol>
 * </p>
 * @author james, TCSASSEMBLER
 */
public class ProcessorConfig {
    private List<String> monitoredQueues;
    private String monitoredQueuesString;
    private String workDir;
    private String rootDir;
    private int maxMessages = 1;
    private boolean serialProcessing = true;
    private int defaultTimeout;
    private boolean analyzeLog = false;
    private boolean keepResultFolder = false;
    private String pythonTestCommand = "/usr/bin/python";
    /**
     * The cpp arguments.
     * @since 1.1
     */
    private String cppArguments;
    /**
     * The cpp build options
     * @since 1.1
     */
    private static final String CPP_OPTIONS = " -W -Wall -Wno-sign-compare -O2";
    /**
     * The default srm match cpp no thread options.
     * @since 1.1
     */
    private static final String DEFAULT_MATCH_CPP_NO_THREAD_OPTIONS = "g++ " + CPP_OPTIONS;
    /**
     * The default marathon match cpp no thread options.
     * @since 1.1
     */
    private static final String DEFAULT_MARATHON_CPP_NO_THREAD_OPTIONS = "g++ " + CPP_OPTIONS;
    private String matchCppNoThreadingOptions;
    private String marathonCppNoThreadingOptions;
    private int defaultExtraExecutionTime = 5000;
    private boolean checkDotNetMethods = true;
    private String dotNetSecurityCheckerPath;
    private String dotNetReferenceDlls = "System.Numerics.dll";
    private String dotNetPlatformFlag = "x64";
    private long monitorInterval = 50L;

    public String getDotNetReferenceDlls() {
        return dotNetReferenceDlls;
    }

    public void setDotNetReferenceDlls(String dotNetReferenceDlls) {
        this.dotNetReferenceDlls = dotNetReferenceDlls;
    }

    public String getDotNetPlatformFlag() {
        return dotNetPlatformFlag;
    }

    public void setDotNetPlatformFlag(String dotNetPlatformFlag) {
        this.dotNetPlatformFlag = dotNetPlatformFlag;
    }

    public boolean isCheckDotNetMethods() {
        return checkDotNetMethods;
    }

    public void setCheckDotNetMethods(boolean checkDotNetMethods) {
        this.checkDotNetMethods = checkDotNetMethods;
    }

    public String getDotNetSecurityCheckerPath() {
        return dotNetSecurityCheckerPath;
    }

    public void setDotNetSecurityCheckerPath(String dotNetSecurityCheckerPath) {
        this.dotNetSecurityCheckerPath = dotNetSecurityCheckerPath;
    }
    /**
     * Getter the srm match cpp no threading options.
     * @return the srm match cpp no threading options.
     */
    public String getMatchCppNoThreadingOptions() {
        if (matchCppNoThreadingOptions != null && matchCppNoThreadingOptions.trim().length() > 0) {
            return matchCppNoThreadingOptions;
        } else {
            if (cppArguments != null && cppArguments.trim().length() > 0) {
                return "g++ " + cppArguments + CPP_OPTIONS;
            }
        }
        return DEFAULT_MATCH_CPP_NO_THREAD_OPTIONS;
    }

    public void setMatchCppNoThreadingOptions(String srmCppNoThreadingOptions) {
        this.matchCppNoThreadingOptions = srmCppNoThreadingOptions;
    }
    /**
     * Getter the marathon cpp no threading options.
     * @return the marathon cpp no threading options.
     */
    public String getMarathonCppNoThreadingOptions() {
        if (marathonCppNoThreadingOptions != null && marathonCppNoThreadingOptions.trim().length() > 0) {
            return marathonCppNoThreadingOptions;
        } else {
            if (cppArguments != null && cppArguments.trim().length() > 0) {
                return "g++ " + cppArguments + CPP_OPTIONS;
            }
        }
        return DEFAULT_MARATHON_CPP_NO_THREAD_OPTIONS;
    }

    public void setMarathonCppNoThreadingOptions(String mmCppNoThreadingOptions) {
        this.marathonCppNoThreadingOptions = mmCppNoThreadingOptions;
    }

    public int getDefaultExtraExecutionTime() {
        return defaultExtraExecutionTime;
    }

    public void setDefaultExtraExecutionTime(int defaultExtraExecutionTime) {
        this.defaultExtraExecutionTime = defaultExtraExecutionTime;
    }

    public boolean isAnalyzeLog() {
        return analyzeLog;
    }

    public void setAnalyzeLog(boolean analyzeLog) {
        this.analyzeLog = analyzeLog;
    }

    public boolean isKeepResultFolder() {
        return keepResultFolder;
    }

    public void setKeepResultFolder(boolean keepResultFolder) {
        this.keepResultFolder = keepResultFolder;
    }

    public String getPythonTestCommand() {
        return pythonTestCommand;
    }

    public void setPythonTestCommand(String pythonTestCommand) {
        this.pythonTestCommand = pythonTestCommand;
    }

    public String getMonitoredQueuesString() {
        return monitoredQueuesString;
    }

    public void setMonitoredQueuesString(String monitoredQueuesString) {
        this.monitoredQueuesString = monitoredQueuesString;
        if (monitoredQueuesString == null || monitoredQueuesString.isEmpty()) {
            monitoredQueues = Collections.emptyList();
        } else {
            monitoredQueues = Arrays.asList(monitoredQueuesString.split(","));
        }
    }

    public List<String> getMonitoredQueues() {
        return monitoredQueues;
    }

    public void setMonitoredQueues(List<String> monitoredQueues) {
        this.monitoredQueues = monitoredQueues;
    }

    public String getWorkDir() {
        return workDir;
    }

    public void setWorkDir(String workDir) {
        this.workDir = workDir;
    }

    public String getRootDir() {
        return rootDir;
    }

    public void setRootDir(String rootDir) {
        this.rootDir = rootDir;
    }

    public int getMaxMessages() {
        return maxMessages;
    }

    public void setMaxMessages(int maxMessages) {
        this.maxMessages = maxMessages;
    }

    public boolean isSerialProcessing() {
        return serialProcessing;
    }

    public void setSerialProcessing(boolean serialProcessing) {
        this.serialProcessing = serialProcessing;
    }

    public int getDefaultTimeout() {
        return defaultTimeout;
    }

    public void setDefaultTimeout(int defaultTimeout) {
        this.defaultTimeout = defaultTimeout;
    }

    public long getMonitorInterval() {
        return monitorInterval;
    }

    public void setMonitorInterval(long monitorInterval) {
        this.monitorInterval = monitorInterval;
    }
    /**
     * Getter the cpp arguments.
     * @return the cpp arguments.
     * @since 1.1
     */
    public String getCppArguments() {
        return cppArguments;
    }
    /**
     * Setter the cpp arguments.
     * @param cppArguments the cpp arguments.
     * @since 1.1
     */
    public void setCppArguments(String cppArguments) {
        this.cppArguments = cppArguments;
    }
}
