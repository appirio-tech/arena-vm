/*
 * AlgoCompiler
 * 
 * Created 05/13/2006
 */
package com.topcoder.services.compiler.invoke.algocomponent;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;

import com.topcoder.server.common.Submission;
import com.topcoder.server.tester.ComponentFiles;
import com.topcoder.server.util.FileUtil;
import com.topcoder.services.common.CommonDaemon;
import com.topcoder.services.common.MPSQASFiles;
import com.topcoder.services.util.Formatter;
import com.topcoder.shared.util.logging.Logger;

/**
 * Base class for all AlgoCompilers. It Allows to compile
 * solutions and submissions for Long Problems
 * Classes extending this class must implement
 * language specific methods required for compilation
 *  
 * @author Diego Belfer (mural)
 * @version $Id: AlgoCompiler.java 71574 2008-07-09 20:40:39Z dbelfer $
 */
public abstract class AlgoCompiler {
    private Logger logger = Logger.getLogger(AlgoCompiler.class);
    
    /**
     * Compiles the algorithm source code.
     * This method must implement the specific language compilation code.
     * It should set the StdErr and StdOut obtained during the compilation process,
     * and in case of failure, it should set the CompilerError with a related message.
     * It should never set the CompileStatus.
     *  
     * 
     * @param compilation AlgoCompilationRequest to obtain necessary data from and where to set 
     *                    the results of the compilation.  
     *                    
     * @return true if the compilation succeed, false otherwise
     */
    protected abstract boolean compileAlgo(AlgoCompilationRequest compilation);
    
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
    protected abstract HashMap buildClassMap(MPSQASAlgoAdapter mpsqasWrapper) throws Exception;
    
  
    /**
     * Creates a new AlgoCompiler
     */
    protected AlgoCompiler() {
    }

    /**
     * Compiles the code of the LongSubmission
     * 
     * @param sub LongSubmission to compile
     * @param problemComponent for which the submission belongs to
     * 
     * @return <code>true</code> if the compilation succeed, <code>false</code> otherwise
     */
    public boolean compileAlgo(Submission sub) {
        ComponentFiles componentFiles = ComponentFiles.getInstance(
                                    sub.getLanguage(), sub.getCoderID(), sub.getLocation().getContestID(), sub.getLocation().getRoundID(), 
                                    sub.getComponentID(), sub.getComponent().getClassName());
        
        SubmissionAdapter submissionWrapper = new SubmissionAdapter(
                                sub, 
                                componentFiles);
        
        if  (!compileAlgo(submissionWrapper)) {
            if (sub.getCompileError() == null) {
                sub.setCompileError(Formatter.truncateOutErr(submissionWrapper.getStdOut(), submissionWrapper.getStdErr()));
            }
            return false;
        }
        
        if(!componentFiles.setClasses(sub)) {
            sub.setCompileError("Your compiled binary is too large.\n\n" + submissionWrapper.getStdErr());
            return false;
        }

        sub.setClassFiles(componentFiles);
       
        //GT Added this check to ensure people do not have massive classfiles
        if (!CommonDaemon.checkObjectSize(componentFiles)) {
            sub.setClassFiles(null);
            sub.setCompileError(CommonDaemon.SIZE_LIMIT_MESSAGE);
            return false;
        }
        sub.setClassFiles(componentFiles);
        sub.setCompileError(Formatter.truncateOutErr(submissionWrapper.getStdOut(), submissionWrapper.getStdErr()));
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
    public boolean compileAlgo(MPSQASFiles mpsqasFiles) {
        MPSQASAlgoAdapter mpsqasWrapper = new MPSQASAlgoAdapter(mpsqasFiles);
        if (!compileAlgo(mpsqasWrapper)) {
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
     * @param mpsqasWrapper MPSQASAlgoAdapter of the compiled solution
     * @param classList Destination map of the load class
     * @param fileExtension extesion of the class to load
     * 
     * @throws FileNotFoundException If the file don't exists in the expected dir
     * @throws IOException If an IO error occurs.
     */
    protected void loadExtension(MPSQASAlgoAdapter mpsqasWrapper, HashMap classList, String fileExtension) throws FileNotFoundException, IOException {
        String fileAbsolutePath = mpsqasWrapper.getPath()+mpsqasWrapper.getClassName() + fileExtension;
        String fileRelativePath = mpsqasWrapper.getClassesDir() + '/' + mpsqasWrapper.getClassName() + fileExtension;
        
        //sore the file in the map using a relative path
        classList.put(fileRelativePath.replace('/',File.separatorChar), FileUtil.getContents(new File(fileAbsolutePath)));
    }
}
