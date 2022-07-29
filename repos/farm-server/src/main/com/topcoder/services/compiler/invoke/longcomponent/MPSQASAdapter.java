/*
 * Copyright (C) - 2013 TopCoder Inc., All Rights Reserved.
 */
 
/*
 * MPSQASAdapter
 * 
 * Created 05/13/2006
 */
package com.topcoder.services.compiler.invoke.longcomponent;

import com.topcoder.services.common.MPSQASFiles;
import com.topcoder.shared.common.ServicesConstants;
import com.topcoder.shared.problem.ProblemConstants;
import com.topcoder.shared.problem.ProblemCustomSettings;

/**
 * Adapter class used to compile MPSQAS long tester solutions
 * 
 * <p>
 * Version 1.1 (TC Competition Engine Code Execution Time Issue) change notes:
 *  <ul>
 *      <li>Added {@link #getCompileTimeLimit()} method.</li>
 *  </ul>
 * </p>
 *
 * <p>
 * Changes in version 1.2 (TC Competition Engine - CPP Language Upgrade And Customization Support v1.0):
 * <ol>
 *      <li>Update {@link #getGccBuildCommand()} method.</li>
 * </ol>
 * </p>
 *
 * <p>
 * Changes in version 1.3 (TC Competition Engine - Python Language Upgrade And Customization Support v1.0):
 * <ol>
 *      <li>Added {@link #getPythonCommand()} method.</li>
 * </ol>
 * </p>
 *
 * <p>
 * Changes in version 1.4 (TopCoder Competition Engine Improvement Series 3 v1.0):
 * <ol>
 *      <li>Update {@link #getProblemCustomSettings()} method.</li>
 *      <li>Remove {getCompileTimeLimit(), getGccBuildCommand(), getPythonCommand()} methods.</li>
 * </ol>
 * </p>
 * @author Diego Belfer (mural), savon_cn, TCSASSEMBLER
 * @version 1.4
 */
public class MPSQASAdapter implements LongCompilationRequest {
    private MPSQASFiles mpsqasFiles;
    private String path;
    private String classesDir;
    

    /**
     * Creates a new adapter for the mpsqasFiles
     * 
     * @param files MPSQASFiles adapted 
     */
    public MPSQASAdapter(MPSQASFiles files) {
        this.mpsqasFiles = files;
        classesDir = mpsqasFiles.getPackageName().replace('.', '/');
        path = ServicesConstants.SOLUTIONS +  classesDir  + '/';
        //  path = /solutions/package as path/
    }

    /**
     * @see LongCompilationRequest#setCompileStatus(boolean)
     */
    public void setCompileStatus(boolean compileStatus) {
        mpsqasFiles.setCompileStatus(compileStatus);
    }

    /**
     * @see LongCompilationRequest#setStdErr(String)
     */
    public void setStdErr(String stdErr) {
        mpsqasFiles.setStdErr(stdErr);
    }

    /**
     * @see LongCompilationRequest#setStdOut(String)
     */
    public void setStdOut(String stdOut) {
        mpsqasFiles.setStdOut(stdOut);
    }

    /**
     * @see LongCompilationRequest#setCompileError(String)
     * Sets the error message as StdErr on the mpsqasFiles
     */
    public void setCompileError(String err) {
        mpsqasFiles.setStdErr(err);
    }

    /**
     * @see LongCompilationRequest#getProgramText()
     * Returns the code for the file in the sourceFiles map with
     * name equals to &lt;package&gt;.&lt;className&gt; 
     */
    public String getProgramText() {
        return getSourceForClass(mpsqasFiles.getClassName());
    }

    /**
     * @see LongCompilationRequest#getPath()
     */
    public String getPath() {
        return path;
    }

    
    /**
     * @see LongCompilationRequest#getPath()
     */
    public String getClassName() {
        return mpsqasFiles.getClassName();
    }

    /**
     * @see LongCompilationRequest#getUserWrapperSource()
     * Returns the code for the file in the sourceFiles map with
     * name equals to &lt;package&gt;.&lt;ProblemConstants.WRAPPER_CLASS&gt; 
     */
    public String getUserWrapperSource() {
        return getSourceForClass(ProblemConstants.WRAPPER_CLASS);
    }

    /**
     * @see LongCompilationRequest#getLanguage()
     */
    public int getLanguage() {
       return mpsqasFiles.getLanguage();
    }

    /**
     * Returns the classes dir = packageName replacing '.' with File.separator
     */
    public String getClassesDir() {
        return classesDir;
    }
    
    /**
     * Returns the source code for a the className specified
     */
    private String getSourceForClass(String className) {
        return (String) mpsqasFiles.getSourceFiles().get(mpsqasFiles.getPackageName()+"."+className);
    }
    
    public String getExposedWrapperSource() {
        return getSourceForClass(getExposedClassName());
    }

    public String getExposedClassName() {
        return mpsqasFiles.getExposedClassName();
    }
    
    /**
     * @see com.topcoder.services.compiler.invoke.longcomponent.LongCompilationRequest#isThreadingAllowed()
     */
    public boolean isThreadingAllowed() {
        return mpsqasFiles.isThreadingAllowed();
    }
    public int getRoundType() {
        return mpsqasFiles.getRoundType();
    }
    
    /**
     * the custom problem settings
     * @return the custom problem settings
     * @since 1.4
     */
    public ProblemCustomSettings getProblemCustomSettings() {
        return mpsqasFiles.getProblemCustomSettings();
    }
}
