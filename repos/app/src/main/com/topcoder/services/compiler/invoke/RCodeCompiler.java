/*
 * Copyright (C) 2013 TopCoder Inc., All Rights Reserved.
 */
package com.topcoder.services.compiler.invoke;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.HashMap;

import com.topcoder.server.common.Submission;
import com.topcoder.server.tester.LongSubmission;
import com.topcoder.server.util.FileUtil;
import com.topcoder.server.util.Java13Utils;
import com.topcoder.services.common.MPSQASFiles;
import com.topcoder.services.compiler.invoke.longcomponent.LongCompilationRequest;
import com.topcoder.services.compiler.invoke.longcomponent.LongCompiler;
import com.topcoder.services.compiler.invoke.longcomponent.MPSQASAdapter;
import com.topcoder.shared.common.ServicesConstants;
import com.topcoder.shared.problem.ProblemComponent;
import com.topcoder.shared.problem.ProblemConstants;
import com.topcoder.shared.util.logging.Logger;

/**
 * <p>
 * the R language code compiler.
 * </p>
 *
 * <p>
 * Changes in version 1.1 (TC Competition Engine - R Language Test Support v1.0):
 * <ol>
 *      <li>Update {@link #C_HELPER_SIGNATURE} field.</li>
 *      <li>Added {@link #wrapperRLanguageCode(String userWrapperCode)} method.</li>
 * </ol>
 * </p>
 * @author TCSASSEMBLER
 * @version 1.1
 */
public class RCodeCompiler implements CodeCompiler {
    /**
     * the logger.
     */
    private final Logger logger = Logger.getLogger(RCodeCompiler.class);
    /**
     * the long compiler.
     */
    private final LongCompiler longCompiler = buildLongCompiler();
    /**
     * <p> the c helper signature used for R language</p>
     */
    private static final String C_HELPER_SIGNATURE = "<C_HELPER>";
    /**
     * <p>
     * TODO implement it for compile the algorithm problem SRM that submitted by users.
     * </p>
     * @param sub
     *       the long submission.
     */
    public Submission compile(Submission sub) {
       return null;
    }
    /**
     * <p>
     * compile the mpsqas client solution.
     * </p>
     * @param mpsqasFiles
     *        the mpsqas file submitted by the mpsqas client.
     */
    public MPSQASFiles compileMPSQAS(MPSQASFiles mpsqasFiles) {
        if (mpsqasFiles.getComponentType() ==  ProblemConstants.LONG_COMPONENT) {
            boolean success = longCompiler.compileLong(mpsqasFiles);
            mpsqasFiles.setCompileStatus(success);
            return mpsqasFiles;
        } else {
            throw new UnsupportedOperationException("MPSQAS files compilation only supported for long tester solutions");
        }
    }
    
    /**
     * <p>
     * TODO implement it for compile the marathon long problem that submitted by users.
     * </p>
     * @param sub
     *       the long submission.
     * @param component
     *       the problem component info entity.
     */
    public LongSubmission compileLong(LongSubmission sub, ProblemComponent component) {
        boolean success = longCompiler.compileLong(sub, component);
        sub.setCompileStatus(success);
        return sub;
    }
    
    /**
     * <p>replace the c_helper lib to the real path.</p>
     * @param the wrapper file
     */
    private String wrapperRLanguageCode(String userWrapperCode) {
        if(userWrapperCode!=null) {
            StringBuffer rRunCodeBuffer = new StringBuffer(userWrapperCode);
            Java13Utils.replace(rRunCodeBuffer, C_HELPER_SIGNATURE, ServicesConstants.R_C_HELPER);
            return rRunCodeBuffer.toString();
        }
        return "";
    }
    /**
     * <p>
     * build the long compiler.
     * </p>
     * @return the built long compiler.
     */
    private LongCompiler buildLongCompiler() {
        return new LongCompiler() {
            /**
             * <p>
             * compile the r long problem
             * </p>
             * @param sub the long compilation request.
             * @return true = the compilation is successful.
             */
            protected boolean compileLong(LongCompilationRequest sub) {
                String binDir = sub.getPath().replace('/',File.separatorChar);
                String sourceDir = binDir + "compile" + File.separatorChar;

                String classSource = sourceDir + sub.getClassName() + ".R";
                logger.info("R language Source: " + classSource);

                boolean compileRetVal = false;

                try {
                    File rootDir = new File(binDir);
                    File inputDir = new File(sourceDir);

                    if (rootDir.exists()) {
                        // delete the directory to get rid of old files
                        if (!FileUtil.deleteRecursive(rootDir)) {
                            logger.info("was not able to delete directory " + rootDir);
                        }
                    }

                    // create the directory structure
                    inputDir.mkdirs();

                    // write the user's submitted Java source code
                    File outputFile = new File(classSource);
                    FileWriter out = new FileWriter(outputFile);

                    out.write(sub.getProgramText());
                    out.close();
                    
                    out = new FileWriter(new File(sourceDir + "Wrapper.R"));
                    out.write(wrapperRLanguageCode(sub.getUserWrapperSource()));
                    out.close();
                    
                    out = new FileWriter(new File(sourceDir + sub.getExposedClassName() + ".R"));
                    out.write(sub.getExposedWrapperSource());
                    out.close();
                    
                    ArrayList args = new ArrayList();
                    args.add(classSource);
                    // add the executive file name
                    String execFileName = sourceDir + sub.getClassName()+".rlc";
                    args.add(execFileName);

                    ByteArrayOutputStream log = new ByteArrayOutputStream();

                    // compile the thing
                    compileRetVal = compile(log, args, execFileName);
                    
                    if(compileRetVal){
                        args = new ArrayList();

                        args.add(sourceDir + "Wrapper.R");
                        String wrapperFileName = sourceDir + "Wrapper.rlc";
                        args.add(wrapperFileName);

                        compileRetVal = compile(log, args,wrapperFileName);
                        
                        if(compileRetVal) {
                            args = new ArrayList();
                            args.add(sourceDir + sub.getExposedClassName() + ".R");
                            String exposedWrapperFileName = sourceDir + sub.getExposedClassName() + ".rlc";
                            args.add(exposedWrapperFileName);

                            compileRetVal = compile(log, args,exposedWrapperFileName);
                        }
                        
                    }
                    //if compile successful, print stOut, else print stdErr
                    if(compileRetVal)
                        sub.setStdOut(log.toString());
                    else
                        sub.setStdErr(log.toString());
                    log.close();
                } catch (IOException e) {
                    logger.error("IO Exception caught compiling R source code", e);
                    compileRetVal = false;
                }
               return compileRetVal;
            }
            
            /**
             * <p>
             * build the wrapper for test.
             * </p>
             * @param mpsqasWrapper
             *       the test wrapper file.
             *
             */
            protected HashMap buildClassMap(MPSQASAdapter mpsqasWrapper) throws Exception {
                HashMap classList = new HashMap();

                String fileAbsolutePath = mpsqasWrapper.getPath()+"compile"+File.separatorChar+mpsqasWrapper.getClassName() + ".rlc";
                String fileRelativePath = mpsqasWrapper.getClassesDir() + '/' + mpsqasWrapper.getClassName() + ".rlc";

                //sore the file in the map using a relative path
                classList.put(fileRelativePath.replace('/',File.separatorChar), FileUtil.getContents(new File(fileAbsolutePath)));

                //wrapper

                fileAbsolutePath = mpsqasWrapper.getPath()+"compile"+File.separatorChar+"Wrapper.rlc";
                fileRelativePath = mpsqasWrapper.getClassesDir() + '/' + "Wrapper.rlc";

               
                //sore the file in the map using a relative path
                classList.put(fileRelativePath.replace('/',File.separatorChar), FileUtil.getContents(new File(fileAbsolutePath)));
                
                
                fileAbsolutePath = mpsqasWrapper.getPath()+"compile"+File.separatorChar+mpsqasWrapper.getExposedClassName() + ".rlc";
                fileRelativePath = mpsqasWrapper.getClassesDir() + '/' + mpsqasWrapper.getExposedClassName() + ".rlc";

                //sore the file in the map using a relative path
                classList.put(fileRelativePath.replace('/',File.separatorChar), FileUtil.getContents(new File(fileAbsolutePath)));
                
                return classList;
            }
        };
    }
    
    /**
     * <p>
     * write the throwable stack trace.
     * </p>
     * @param throwable
     *         the throwable
     * @param log
     *         the log
     */
    private void writeException(Throwable throwable,ByteArrayOutputStream log) {
        try {
            StringWriter sw = new StringWriter();
            PrintWriter pw = new PrintWriter(sw);
            throwable.printStackTrace(pw);
            String stackTrace = sw.toString();
            if(stackTrace!=null) {
                log.write(stackTrace.getBytes());
            }
        } catch(IOException e) {
            logger.error("error occure while writing exception to sterr",e);
        }
    }
    /**
     * <p>
     * compile the R submission with specific command lines.
     * </p>
     * @param log
     *         the log info.
     * @param args
     *         the command line arguments.
     * @param execFileName
     *         the generated executive file.
     * @return true=the compilation is successful.
     */
    private boolean compile(ByteArrayOutputStream log, ArrayList args, String execFileName) {
        // convert ArrayList to String
        String cmd = "Rscript " + ServicesConstants.R_COMPILER + " ";

        for (int i = 0; i < args.size(); i++) {
            cmd += (String) args.get(i) + " ";
        }

        logger.info(cmd);

        Process p;

        try {
            p = Runtime.getRuntime().exec(cmd);
        } catch (Exception e) {
            writeException(e,log);
            return false;
        }

        CompilerHelperThread err = new CompilerHelperThread(p.getErrorStream());
        CompilerHelperThread out = new CompilerHelperThread(p.getInputStream());
        
        int ret = 0;
        
        try {
            ret = p.exitValue();
        } catch (Exception e) {
            try {
                p.waitFor();
                ret = p.exitValue();
            } catch(Exception ex) {
            }
        }

        try {
            err.quit();
            out.quit();

            err.join();
            out.join();
        } catch(Exception e) {
            //interrupted
        }

        try {
            p.destroy();
        } catch(Exception e) {
            e.printStackTrace();

            p = null;
        }

        //merge streams
        out.appendTo(log);
        err.appendTo(log);
        
        //we must first check if the executive file is exist
        File execFile = new File(execFileName);
        if(!execFile.exists()) {
            logger.error("The executive file "+execFileName+" generated by R does not exist.");
            return false;
        }

        if(ret == 1) {
            //no class files generated
            return false;
        } else if(ret == 0) {
            return true;
        } else {            
            //we don't know what this is,but anyway the executive file is exist for test.
            //so return true here
            logger.error("FIND OUT WHAT ERROR CODE " + ret + " IS");
            return true;
        }
    }
}
