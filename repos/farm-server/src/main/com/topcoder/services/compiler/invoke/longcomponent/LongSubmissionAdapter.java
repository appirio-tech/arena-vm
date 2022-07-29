/*
 * Copyright (C) - 2013 TopCoder Inc., All Rights Reserved.
 */
 
/*
 * LongSubmissionAdapter
 * 
 * Created 05/13/2006
 */
package com.topcoder.services.compiler.invoke.longcomponent;

import java.io.File;

import com.topcoder.server.tester.ComponentFiles;
import com.topcoder.server.tester.LongSubmission;
import com.topcoder.shared.problem.ProblemCustomSettings;

/**
 * Adapter class used to compile long submissions
 * 
 * <p>
 * Version 1.1 (TC Competition Engine Code Execution Time Issue) change notes:
 *  <ul>
 *      <li>Added {@link #compileTimeLimit} field and {@link #getCompileTimeLimit()} method.</li>
 *  </ul>
 * </p>
 *
 * <p>
 * Changes in version 1.2 (TC Competition Engine - CPP Language Upgrade And Customization Support v1.0):
 * <ol>
 *      <li>Added {@link #gccBuildCommand} field and {@link #getGccBuildCommand()} method.</li>
 * </ol>
 * </p>
 *
 * <p>
 * Changes in version 1.3 (TC Competition Engine - Python Language Upgrade And Customization Support v1.0):
 * <ol>
 *      <li>Added {@link #pythonCommand} field and {@link #getPythonCommand()} method.</li>
 * </ol>
 * </p>
 *
 * <p>
 * Changes in version 1.4 (TopCoder Competition Engine Improvement Series 3 v1.0):
 * <ol>
 *      <li>Update {@link #LongSubmissionAdapter(LongSubmission, ComponentFiles, String,String,
 *              boolean, int, ProblemCustomSettings)} method.</li>
 *      <li>Remove compileTimeLimit,gccBuildCommand,pythonCommand fields and it's getter,setter methods.</li>
 *      <li>Added {@link #customSettings} field and getter method.</li>
 * </ol>
 * </p>
 * @author Diego Belfer (mural), savon_cn
 * @version 1.4
 */
public class LongSubmissionAdapter implements LongCompilationRequest {
    private LongSubmission submission;
    private ComponentFiles componentFiles;
    private String stdErr;
    private String stdOut;
    private String stubSource;
    private String exposedClassName;
    private String exposedWrapperSource;
    private boolean threadingAllowed;
    private int roundType;
    
    /**
     * the problem custom settings.
     * @since 1.4
     */
    private ProblemCustomSettings customSettings;
    /**
     * Creates a new LongSubmissionAdapter
     *  
     * @param sub LongSubmission to adapt
     * @param componentFiles ComponentFiles for the LongSubmission
     * @param stubSource Source code for the wrapper code
     * @param exposedWrapperSource Source code for the exposed wrapper
     * @param threadingAllowed is threading allowed on the submission 
     * @param custom the problem custom settings.
     */
    public LongSubmissionAdapter(LongSubmission sub, ComponentFiles componentFiles, String stubSource,String exposedWrapperSource,
        boolean threadingAllowed, int roundType, ProblemCustomSettings custom) {
        this.submission = sub;
        this.componentFiles =  componentFiles;
        this.stubSource = stubSource;
        this.exposedWrapperSource = exposedWrapperSource;
        this.threadingAllowed= threadingAllowed;
        this.roundType= roundType;
        this.customSettings = custom;
    }

    /**
     * @see LongCompilationRequest#setCompileStatus(boolean)
     */
    public void setCompileStatus(boolean b) {
        submission.setCompileStatus(b);
        
    }

    /**
     * @see com.topcoder.services.compiler.invoke.longcomponent.LongCompilationRequest#setCompileError(java.lang.String)
     */
    public void setCompileError(String string) {
        submission.setCompileError(string);
    }

    /**
     * @see com.topcoder.services.compiler.invoke.longcomponent.LongCompilationRequest#getProgramText()
     */
    public String getProgramText() {
        return submission.getProgramText();
    }

    /**
     * @see com.topcoder.services.compiler.invoke.longcomponent.LongCompilationRequest#getPath()
     */
    public String getPath() {
        //path = submisiondir/package as path/
        return componentFiles.getFullComponentPath() + File.separator;
    }

    /**
     * @return the StdErr message
     */
    public String getStdErr() {
        return this.stdErr;
    }

    /**
     * @return the StdOut message
     */
    public String getStdOut() {
        return this.stdOut;
    }

    /**
     * @see com.topcoder.services.compiler.invoke.longcomponent.LongCompilationRequest#setStdErr(java.lang.String)
     */
    public void setStdErr(String stderr) {
        this.stdErr = stderr;
        
    }

    /**
     * @see com.topcoder.services.compiler.invoke.longcomponent.LongCompilationRequest#setStdOut(java.lang.String)
     */
    public void setStdOut(String stdout) {
        this.stdOut = stdout;
        
    }

    /**
     * @see com.topcoder.services.compiler.invoke.longcomponent.LongCompilationRequest#getClassName()
     */
    public String getClassName() {
        return componentFiles.getComponentName();
    }

    /**
     * @see com.topcoder.services.compiler.invoke.longcomponent.LongCompilationRequest#getUserWrapperSource()
     */
    public String getUserWrapperSource() {
        return stubSource;
    }

    /**
     * @see com.topcoder.services.compiler.invoke.longcomponent.LongCompilationRequest#getLanguage()
     */
    public int getLanguage() {
        return submission.getLanguage();
    }

    public String getExposedWrapperSource() {
        return exposedWrapperSource;
    }
    
    public void setExposedWrapperSource(String s) {
        exposedWrapperSource = s;
    }

    public String getExposedClassName() {
        return exposedClassName;
    }
    
    public void setExposedClassName(String s) {
        exposedClassName = s;
    }

    /**
     * @see com.topcoder.services.compiler.invoke.longcomponent.LongCompilationRequest#isThreadingAllowed()
     */
    public boolean isThreadingAllowed() {
        return threadingAllowed;
    }
    public int getRoundType(){
        return roundType;
    }
    /**
     * the custom problem settings
     * @return the custom problem settings
     * @since 1.4
     */
    public ProblemCustomSettings getProblemCustomSettings() {
        return this.customSettings;
    }
}
