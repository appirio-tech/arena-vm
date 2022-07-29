/*
* Copyright (C) - 2013 TopCoder Inc., All Rights Reserved.
*/
package com.topcoder.services.compiler.invoke.algocomponent;

import java.util.List;

import com.topcoder.services.common.MPSQASFiles;
import com.topcoder.services.compiler.invoke.longcomponent.LongCompilationRequest;
import com.topcoder.shared.common.ServicesConstants;
import com.topcoder.shared.problem.DataType;
import com.topcoder.shared.problem.ProblemCustomSettings;

/**
 * Adapter class used to compile MPSQAS long tester solutions
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
 * @author Diego Belfer (mural), savon_cn, TCSASSEMBLER
 * @version 1.3
 */
public class MPSQASAlgoAdapter implements AlgoCompilationRequest {
    private MPSQASFiles mpsqasFiles;
    private String path;
    private String classesDir;
    private DataType returnType;

    /**
     * Creates a new adapter for the mpsqasFiles
     * 
     * @param files MPSQASFiles adapted 
     */
    public MPSQASAlgoAdapter(MPSQASFiles files) {
        this.mpsqasFiles = files;
        classesDir = mpsqasFiles.getPackageName().replace('.', '/');
        path = ServicesConstants.SOLUTIONS +  classesDir  + '/';
        //  path = /solutions/package as path/
        returnType = mpsqasFiles.getResultType();
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

    public String getMethodName() {
        return mpsqasFiles.getMethodName();
    }
    
    /**
     * @see LongCompilationRequest#getPath()
     */
    public String getClassName() {
        return mpsqasFiles.getClassName();
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

    public List getParamTypes() {
        return mpsqasFiles.getArgTypes();
    }

    public String getReturnType(int language) {
        return returnType.getDescriptor(language);
    }

    private String getSourceForClass(String className) {
        return (String) mpsqasFiles.getSourceFiles().get(mpsqasFiles.getPackageName()+"."+className);
    }
	
    /**
     * get the problem custom settings.
     * @return the problem custom settings.
     * @since 1.3
     */
    public ProblemCustomSettings getProblemCustomSettings() {
        return mpsqasFiles.getProblemCustomSettings();
    }
}
