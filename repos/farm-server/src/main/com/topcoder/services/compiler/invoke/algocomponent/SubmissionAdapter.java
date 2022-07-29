/*
* Copyright (C) - 2013 TopCoder Inc., All Rights Reserved.
*/

package com.topcoder.services.compiler.invoke.algocomponent;

import java.io.File;
import java.util.Arrays;
import java.util.List;

import com.topcoder.server.common.Submission;
import com.topcoder.server.tester.ComponentFiles;
import com.topcoder.services.compiler.invoke.longcomponent.LongCompilationRequest;
import com.topcoder.shared.problem.ProblemCustomSettings;

/**
 * Adapter class used to compile algo submissions
 *
 * <p>
 * Changes in version 1.1 (TC Competition Engine - C++ and Python Customization Support for SRM v1.0):
 * <ol>
 *      <li>Add {@link #getGccBuildCommand()} method.</li>
 * </ol>
 * </p>
 *
 * <p>
 * Changes in version 1.2 (BUGR-9137 - Python Enable For SRM):
 * <ol>
 *      <li>Add {@link #getPythonCommand()} method.</li>
 * </ol>
 * </p>
 *
 * <p>
 * Changes in version 1.3 (TopCoder Competition Engine Improvement Series 3 v1.0):
 * <ol>
 *      <li>Add {@link #getProblemCustomSettings()} method.</li>
 * </ol>
 * </p>
 * @author Diego Belfer (mural), savon_cn
 * @version 1.3
 */
public class SubmissionAdapter implements AlgoCompilationRequest {
    private Submission submission;
    private ComponentFiles componentFiles;
    private String stdErr;
    private String stdOut;

    /**
     * Creates a new SubmissionAdapter
     *  
     * @param sub LongSubmission to adapt
     * @param componentFiles ComponentFiles for the LongSubmission
     */
    public SubmissionAdapter(Submission sub, ComponentFiles componentFiles) {
        this.submission = sub;
        this.componentFiles =  componentFiles;
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

    public String getMethodName() {
        return submission.getComponent().getMethodName();
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
        if (stdErr == null || stdErr.length() == 0) return null; else return stdErr;
    }

    /**
     * @return the StdOut message
     */
    public String getStdOut() {
        if (stdOut == null || stdOut.length() == 0) return null; else return stdOut;
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
     * @see com.topcoder.services.compiler.invoke.longcomponent.LongCompilationRequest#getLanguage()
     */
    public int getLanguage() {
        return submission.getLanguage();
    }

    public List getParamTypes() {
        return Arrays.asList(submission.getComponent().getParamTypes());
    }

    public String getReturnType(int language) {
        return submission.getComponent().getReturnType(language);
    }
	
    /**
     * get the problem custom settings.
     * @return the problem custom settings.
     * @since 1.3
     */
    public ProblemCustomSettings getProblemCustomSettings() {
        return submission.getComponent().getProblemCustomSettings();
    }
}
