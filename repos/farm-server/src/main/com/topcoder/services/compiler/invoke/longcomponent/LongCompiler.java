/*
 * Copyright (C) - 2013 TopCoder Inc., All Rights Reserved.
 */
 
/*
 * LongCompiler
 * 
 * Created 05/13/2006
 */
package com.topcoder.services.compiler.invoke.longcomponent;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;

import com.topcoder.server.farm.common.RoundUtils;
import com.topcoder.server.tester.ComponentFiles;
import com.topcoder.server.tester.LongSubmission;
import com.topcoder.server.util.FileUtil;
import com.topcoder.services.common.CommonDaemon;
import com.topcoder.services.common.MPSQASFiles;
import com.topcoder.services.compiler.util.LongContestCodeGeneratorHelper;
import com.topcoder.services.util.Formatter;
import com.topcoder.shared.problem.ProblemComponent;
import com.topcoder.shared.problem.ProblemCustomSettings;
import com.topcoder.shared.util.logging.Logger;

/**
 * Base class for all LongCompilers. It Allows to compile
 * solutions and submissions for Long Problems
 * Classes extending this class must implement
 * language specific methods required for compilation
 *  
 * <p>
 * Version 1.1 (TC Competition Engine Code Execution Time Issue) change notes:
 *  <ul>
 *      <li>Updated {@link #compileLong(LongCompilationRequest)} to use the configurable compile time limit.</li>
 *  </ul>
 * </p>
 *
 * <p>
 * Changes in version 1.2 (TC Competition Engine - CPP Language Upgrade And Customization Support v1.0):
 * <ol>
 *      <li>Updated {@link #compileLong(LongCompilationRequest)} method to add gcc build command.</li>
 * </ol>
 * </p>
 *
 * <p>
 * Changes in version 1.3 (TC Competition Engine - Python Language Upgrade And Customization Support v1.0):
 * <ol>
 *      <li>Updated {@link #compileLong(LongCompilationRequest)} method to add python command.</li>
 * </ol>
 * </p>
 *
 * <p>
 * Changes in version 1.4 (TopCoder Competition Engine Improvement Series 3 v1.0):
 * <ol>
 *      <li>Updated {@link #compileLong(LongCompilationRequest)} method to add custom settings.</li>
 * </ol>
 * </p>
 * @author Diego Belfer (mural), savon_cn
 * @version 1.4
 */
public abstract class LongCompiler {
    private Logger logger = Logger.getLogger(LongCompiler.class);
    
    /**
     * Compiles the long source code.
     * This method must implement the specific language compilation code.
     * It should set the StdErr and StdOut obtained during the compilation process,
     * and in case of failure, it should set the CompilerError with a related message.
     * It should never set the CompileStatus.
     *  
     * 
     * @param compilation LongCompilationRequest to obtain necessary data from and where to set 
     *                    the results of the compilation.  
     *                    
     * @return true if the compilation succeed, false otherwise
     */
    protected abstract boolean compileLong(LongCompilationRequest compilation);
    
    /**
     * This method is responsible for building the class map containing all generated files
     * that should be set to the mpsqasFiles as the classFiles map.
     * 
     * @param mpsqasWrapper The adapter used to compiled the mpsqasFiles
     * 
     * @return Map With the expected classFiles
     * 
     * @throws Exception If any exception occurs during the process
     */
    protected abstract HashMap buildClassMap(MPSQASAdapter mpsqasWrapper) throws Exception;
    
  
    /**
     * Creates a new LongCompiler
     */
    protected LongCompiler() {
    }

    /**
     * Compiles the code of the LongSubmission
     * 
     * @param sub LongSubmission to compile
     * @param problemComponent for which the submission belongs to
     * 
     * @return <code>true</code> if the compilation succeed, <code>false</code> otherwise
     */
    public boolean compileLong(LongSubmission sub, ProblemComponent component) {
        ComponentFiles componentFiles = ComponentFiles.getInstance(
                                    sub.getLanguage(), sub.getCoderID(), sub.getContestID(), sub.getRoundID(), 
                                    sub.getComponentID(), component.getClassName());
        
        String wrapperCode = LongContestCodeGeneratorHelper.generateWrapperForUserCode(component, null, sub.getLanguage());
        String exposedCode = LongContestCodeGeneratorHelper.generateWrapperForExposedCode(component, null, sub.getLanguage());
        
        LongSubmissionAdapter longSubmissionWrapper = new LongSubmissionAdapter(
                                sub, 
                                componentFiles, 
                                wrapperCode, 
                                exposedCode,
                                RoundUtils.isThreadingAllowed(component.getRoundType()),
                                component.getRoundType(),
                                component.getProblemCustomSettings());
        
        String name = component.getExposedClassName();
        if(name == null || name.equals("")) {
            name = "ExposedWrapper";
        }
        longSubmissionWrapper.setExposedClassName(name);
        sub.setWrapperClassName(name);
        
        if  (!compileLong(longSubmissionWrapper)) {
            if (sub.getCompileError() == null) {
                sub.setCompileError(Formatter.truncateOutErr(longSubmissionWrapper.getStdOut(), longSubmissionWrapper.getStdErr()));
            }
            return false;
        }
        
        if(!componentFiles.setClasses(sub)) {
            sub.setCompileError("Your compiled binary is too large.\n\n" + longSubmissionWrapper.getStdErr());
            return false;
        }
       
        //GT Added this check to ensure people do not have massive classfiles
        if (!CommonDaemon.checkObjectSize(componentFiles)) {
            sub.setCompileError(CommonDaemon.SIZE_LIMIT_MESSAGE);
            return false;
        }
        sub.setClassFiles(componentFiles);
        sub.setCompileError(Formatter.truncateOutErr(longSubmissionWrapper.getStdOut(), longSubmissionWrapper.getStdErr()));
        sub.setCompileStatus(true);
        return true;
    }
    
    
    /**
     * Compiles the code of a long tester solution.
     * 
     * @param mpsqasFiles MPSQASFiles to compile
     * 
     * @return <code>true</code> if the compilation succeed, <code>false</code> otherwise
     */
    public boolean compileLong(MPSQASFiles mpsqasFiles) {
        MPSQASAdapter mpsqasWrapper = new MPSQASAdapter(mpsqasFiles);
        if (!compileLong(mpsqasWrapper)) {
            return false;
        }
        
        try {
            HashMap classMap = buildClassMap(mpsqasWrapper);
            mpsqasFiles.setClassFiles(classMap);
        } catch (Exception e) {
            logger.error("failure when trying to read back compiled output: " + e.toString());
            mpsqasFiles.setStdErr("failure when trying to read back compiled output: " + e.toString());
            return false;
        }
        mpsqasFiles.setCompileStatus(true);
        return true;
    }

    /**
     * Helper method that loads files with name of the form ClassName + fileExtension and add it to the
     * classList maps with the classesDir concatenated
     * 
     * @param mpsqasWrapper MPSQASAdapter of the compiled solution
     * @param classList Destination map of the load class
     * @param fileExtension extesion of the class to load
     * 
     * @throws FileNotFoundException If the file don't exists in the expected dir
     * @throws IOException If an IO error occurs.
     */
    protected void loadExtension(MPSQASAdapter mpsqasWrapper, HashMap classList, String fileExtension) throws FileNotFoundException, IOException {
        String fileAbsolutePath = mpsqasWrapper.getPath()+mpsqasWrapper.getClassName() + fileExtension;
        String fileRelativePath = mpsqasWrapper.getClassesDir() + '/' + mpsqasWrapper.getClassName() + fileExtension;
        
        //sore the file in the map using a relative path
        classList.put(fileRelativePath.replace('/',File.separatorChar), FileUtil.getContents(new File(fileAbsolutePath)));
    }
}
