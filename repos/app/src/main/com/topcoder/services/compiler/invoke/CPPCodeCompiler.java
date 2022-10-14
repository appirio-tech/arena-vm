/*
 * Copyright (C) - 2013 TopCoder Inc., All Rights Reserved.
 */
package com.topcoder.services.compiler.invoke;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;

import com.topcoder.netCommon.contest.ContestConstants;
import com.topcoder.netCommon.contest.RoundTypes;
import com.topcoder.netCommon.mpsqas.ApplicationConstants;
import com.topcoder.server.common.Submission;
import com.topcoder.server.tester.LongSubmission;
import com.topcoder.server.util.FileUtil;
import com.topcoder.server.webservice.WebServiceGeneratorResources;
import com.topcoder.services.common.MPSQASFiles;
import com.topcoder.services.compiler.invoke.algocomponent.AlgoCompilationRequest;
import com.topcoder.services.compiler.invoke.algocomponent.AlgoCompiler;
import com.topcoder.services.compiler.invoke.algocomponent.MPSQASAlgoAdapter;
import com.topcoder.services.compiler.invoke.longcomponent.LongCompilationRequest;
import com.topcoder.services.compiler.invoke.longcomponent.LongCompiler;
import com.topcoder.services.compiler.invoke.longcomponent.MPSQASAdapter;
import com.topcoder.services.util.ExecWrapper;
import com.topcoder.services.util.Formatter;
import com.topcoder.shared.common.ServicesConstants;
import com.topcoder.shared.problem.DataType;
import com.topcoder.shared.problem.ProblemComponent;
import com.topcoder.shared.problem.ProblemConstants;
import com.topcoder.shared.util.logging.Logger;

/**
 * <p>
 * Version 1.1 (TC Competition Engine Code Execution Time Issue) change notes:
 *  <ul>
 *      <li>Updated {@link #buildLongCompiler()} to use the configurable compile time limit.</li>
 *  </ul>
 * </p>
 * 
 * <p>
 * Changes in version 1.2 (TC Competition Engine - CPP Language Upgrade And Customization Support v1.0):
 * <ol>
 *      <li>Updated {@link #buildLongCompiler()} to use the configurable gcc build command.</li>
 * </ol>
 * </p>
 *
 * <p>
 * Changes in version 1.3 (TC Competition Engine - C++ and Python Customization Support for SRM v1.0):
 * <ol>
 *      <li>Update {@link #compileAlgo(AlgoCompilationRequest sub)} method.</li>
 * </ol>
 * </p>
 *
 * <p>
 * Changes in version 1.4 (Release Assembly - TopCoder Competition Engine Improvement Series 2 v1.0):
 * <ol>
 *      <li>Added default option constants and removed corresponding old constants and configuration parameters.
 *      See {@link #DEFAULT_CC_NO_THREADING_OPTIONS}.</li>
 *      <li>Added {@link #ALGO_CC_NO_THREADING_OPTIONS_PROPERTY_NAME} and {@link #LONG_CC_NO_THREADING_OPTIONS_PROPERTY_NAME}
 *      constants.</li>
 *      <li>Added {@link #getCCNoThreadingOptions} methods.</li>
 *      <li>Added {@link #COMPILE_COMMAND_NO_THREADING_SUFFIX}, {@link #BUILD_COMMAND_NO_THREADING_SUFFIX},
 *      {@link #BUILD_COMMAND_SSE_SUFFIX}, and {@link #BUILD_COMMAND_SSE_LAPACK_SUFFIX} constants as per new requirements
 *      and removed out-dated and useless constants.</li>
 *      <li>Updated {@link #buildAlgoCompiler()} and {@link #buildLongCompiler()} methods to use configurabe values.</li>
 * </ol>
 * </p>
 *
 * <p>
 * Changes in version 1.5 (TopCoder Competition Engine Improvement Series 3 v1.0):
 * <ol>
 *      <li>Update {@link #compileAlgo(AlgoCompilationRequest sub)} method to support custom problem setting.</li>
 *      <li>Update {@link #compileLong(LongCompilationRequest sub)} method to support custom problem setting.</li>
 * </ol>
 * </p>
 * @author savon_cn
 * @version 1.5
 */
public class CPPCodeCompiler implements CodeCompiler {
    private static Logger logger = Logger.getLogger(CPPCodeCompiler.class);

    /**
     * Compiler used to compile long submissions and long solutions 
     */
    private static LongCompiler longCompiler = buildLongCompiler();
    private static AlgoCompiler algoCompiler = buildAlgoCompiler();

    /**
     * This constant defines name for CC no threading options property for SRM.
     * @since 1.4 
     */
    private static final String ALGO_CC_NO_THREADING_OPTIONS_PROPERTY_NAME =
            "com.topcoder.services.compiler.invoke.CPPCodeCompiler.srmCCNoThreadingOptions";

    /**
     * This constant defines name for CC no threading options property for MM.
     * @since 1.4 
     */
    private static final String LONG_CC_NO_THREADING_OPTIONS_PROPERTY_NAME =
            "com.topcoder.services.compiler.invoke.CPPCodeCompiler.mmCCNoThreadingOptions";

    private static final String INTEL_THREADING = "icc -Kc++ -Wall -Wno-missing-prototypes -O2 -lpthread -openmp ";

    /**
     * This constant defines default compiler command options related to CC no treading.
     * @since 1.4
     */
    private static final String DEFAULT_CC_NO_THREADING_OPTIONS = "g++ -W -Wall -Wno-sign-compare -O2 ";

    private static final String CUDA_NO_THREADING = "/usr/local/cuda/bin/nvcc -I/home/farm/processor/deploy/app/cpp/ -I. -I/usr/local/cuda/include -I../../common/inc -DUNIX -O3 -arch sm_13 --ptxas-options -v -lcuda --compiler-options -msse,-msse2,-msse3 ";

    private static final String IFLAGS = " -I"+ServicesConstants.CPP_INCLUDE_FOLDER;
    private static final String DFLAGS = " -s "; //@@@ " -ggdb ";
    private static final String THREAD_FLAGS = "-D_TC_THREADING_ ";
    private static final String PFLAGS = " -pipe ";
    private static final String SSEFLAGS = " -mmmx -msse -msse2 -msse3 ";
    private static final String CFLAGS_THREADING = IFLAGS + DFLAGS + PFLAGS + THREAD_FLAGS;
    
    private static final String CFLAGS_NO_THREADING = IFLAGS + DFLAGS + PFLAGS;

    /**
     * This constant defines compiler command options for no threading.
     * @since 1.4
     */
    private static final String COMPILE_COMMAND_NO_THREADING_SUFFIX = CFLAGS_NO_THREADING + " -c -o";

    private static final String BUILD_COMMAND_THREADING  = INTEL_THREADING + CFLAGS_THREADING + " -o ";

    /**
     * This constant defines compiler command options with no-threading options included.
     * @since 1.4
     */
    private static final String BUILD_COMMAND_NO_THREADING_SUFFIX  = CFLAGS_NO_THREADING + " -o ";

    /**
     * This constant defines SSE compiler command options.
     * @since 1.4
     */
    private static final String BUILD_COMMAND_SSE_SUFFIX = CFLAGS_NO_THREADING + SSEFLAGS + " -o ";

    /**
     * This constant defines SSE LA pack compiler command options.
     * @since 1.4
     */
    private static final String BUILD_COMMAND_SSE_LAPACK_SUFFIX = CFLAGS_NO_THREADING + SSEFLAGS + " -llapack -o ";

    private static final String BUILD_COMMAND_CUDA = CUDA_NO_THREADING + IFLAGS+" -o ";
    private static final int CPP_COMPILE_TIMEOUT = 30000;

    /**
     * Returns configured CC no threading options, or default value if none configured.
     *
     * @param componentTypeId The component type ID.
     * @return CC no threading options.
     * @since 1.4
     */
    private static String getCCNoThreadingOptions(int componentTypeId) {
        if (componentTypeId == ApplicationConstants.LONG_PROBLEM) {
            return System.getProperty(LONG_CC_NO_THREADING_OPTIONS_PROPERTY_NAME, DEFAULT_CC_NO_THREADING_OPTIONS);
        } else {
            return System.getProperty(ALGO_CC_NO_THREADING_OPTIONS_PROPERTY_NAME, DEFAULT_CC_NO_THREADING_OPTIONS);
        }
    }

//    private static String makePrototypeSource(String className, String methodName,
//            String returnType, DataType[] paramTypes) {
//        String source = "";
//
//        source += "class " + className + " {\n";
//        source += "public:\n";
//        source += returnType + " " + methodName + "(";
//
//        for (int i = 0; i < paramTypes.length; i++) {
//            if(i > 0)
//                source += ", ";
//            source += paramTypes[i].getDescriptor(ContestConstants.CPP);
//        }
//
//        source += ");\n";
//        source += "};\n";
//        
//        return source;
//    }


    /**
     * The compile method is the main class method, which performs the full compilation
     * of the source file, given by the Submission object.
     * @param sub            Filled Submission object needing to be compiled
     * @return boolean       Returns the compilation status (success/fail)
     */

    public Submission compile(Submission sub) {
        boolean success = algoCompiler.compileAlgo(sub);
        sub.setCompileStatus(success);
        return sub;
    }

    /**
     * The compile method performs the compilation of the source file.
     * @param mpsqasFiles    Filled MPSQASFiles object
     * @return boolean       Returns the compilation status (success/fail)
     */
    public MPSQASFiles compileMPSQAS(MPSQASFiles mpsqasFiles) {
        boolean success = false;
        if (mpsqasFiles.getComponentType() ==  ProblemConstants.LONG_COMPONENT) {
            success = longCompiler.compileLong(mpsqasFiles);
        } else {
            success = algoCompiler.compileAlgo(mpsqasFiles);
        }
        mpsqasFiles.setCompileStatus(success);
        return mpsqasFiles;
    }
    
    
    public LongSubmission compileLong(LongSubmission sub, ProblemComponent problemComponent) {
        boolean success = longCompiler.compileLong(sub, problemComponent);
        sub.setCompileStatus(success);
        return sub;
    }

    /**
     * Builds an algorithm compiler for CPP language.
     * 
     * @return returns the compiler built
     */
    private static AlgoCompiler buildAlgoCompiler() {
        return new AlgoCompiler() {
                /**
                 * <p>
                 * wrapper the default gcc command
                 * </p>
                 * @param gccBuildCommand the gcc build command.
                 * @param action compile or build action
                 * @param defCommand the default command if the user customization command is not set.
                 * @return the wrapper gcc build command.
                 */
                private String wrapperGccBuildCommand(String gccBuildCommand,String action,String defCommand) {
                    if (gccBuildCommand != null && gccBuildCommand.trim().length() > 0) {
                        return gccBuildCommand + CFLAGS_NO_THREADING + action;
                    }
                    return defCommand;
                }
                /**
                 * <p>
                 * compile the srm problem.
                 * </p>
                 * @param sub the compilation request.
                 */
                protected boolean compileAlgo(AlgoCompilationRequest sub) {
                    sub.setCompileStatus(false);
                    String classname = sub.getClassName();
                    File pathfile = new File(sub.getPath());
                    String source = sub.getProgramText() + "\n";
                    String stdErr = "";
                    String stdOut = "";
                    ExecWrapper ew;
                    String sourceStub = "";
                    DataType[] paramTypes = (DataType[]) sub.getParamTypes().toArray(new DataType[0]);;

                    sourceStub += "#line 2 \"top level\"\n";
                    sourceStub += "#ifdef WRAPPER_THUNK\n";
                    sourceStub += "#include \"" + sub.getClassName() + ".cc\"\n";
                    sourceStub += "#endif\n";
                    sourceStub += "#define CLASS_NAME " + sub.getClassName() + "\n";
                    sourceStub += "#define METHOD_NAME " + sub.getMethodName() + "\n";
                    sourceStub += "#define RETURN_TYPE " + sub.getReturnType(ContestConstants.CPP) + "\n";
                    for (int i = 0; i < paramTypes.length; i++) {
                        sourceStub += "#define ARG" + i + "_TYPE " +
                            paramTypes[i].getDescriptor(ContestConstants.CPP) + "\n";
                    }

                    sourceStub += "#ifndef NO_WRAPPER\n";
                    sourceStub += "#include \"" + ServicesConstants.WRAPPER + "\"\n";
                    sourceStub += "#endif\n";
                    
                    pathfile.mkdirs();

                    String gccBuildCommand = sub.getProblemCustomSettings().getGccBuildCommand();
                    // this next part only needs to be done once per problem... this is my crude way of caching @@@
                    if (!(new File(pathfile, classname + "-stub.o").exists()) /*|| cf.length != 0*/) {
                        // compile the stub
                        FileWriter fw = null;
                        try {
                            fw = new FileWriter(new File(pathfile, classname + "-stub.cc"));
                            fw.write(sourceStub);
                        } catch (Exception e) {
                            sub.setCompileError("could not write generated stub to file: " + e.toString());
                            return false;
                        } finally {
                            try {
                                if(fw!=null)
                                    fw.close();
                            } catch(IOException e1) {
                                sub.setCompileError("error occured while closing the FileWriter: " + e1.toString());
                            }
                        }
                      
                        String command = wrapperGccBuildCommand(gccBuildCommand," -c -o ",
                                getCCNoThreadingOptions(ApplicationConstants.SINGLE_PROBLEM) + " " +
                                COMPILE_COMMAND_NO_THREADING_SUFFIX) +
                            " " + classname + "-stub.o " + classname + "-stub.cc -DWRAPPER_ENTRY";
                        logger.info("Executing: " + command);
                        ew = new ExecWrapper(command, null, pathfile, "", CPP_COMPILE_TIMEOUT, Formatter.MAX_USER_STRING);
                        if (ew.error) {
                            logger.error("could not execute compiler on generated stub: " + ew.stderr + "\n" + ew.stdout);

                            sub.setCompileError("could not execute compiler on generated stub");
                            return false;
                        }
                        if (!ew.finished) {
                            sub.setCompileError("compiling generated stub/wrapper took too long (not done after " + ew.timetaken + " ms)");
                            return false;
                        }
                        if (ew.exitval != 0) {
                            sub.setCompileError("errors compiling generated stub/wrapper: ("
                                                + ew.exitval + ")\n\n" + ew.stderr);
                            return false;
                        }
                        if (ew.stderr.length() > 0) {
                            sub.setCompileError("warnings compiling generated stub/wrapper:\n\n" + ew.stderr);
                            return false;
                        }
                    }

                    FileWriter fw = null;
                    try {
                        fw = new FileWriter(new File(pathfile, classname + ".cc"));
                        fw.write(source);
                    } catch (Exception e) {
                        sub.setCompileError("could not write submission to file: " + e.toString());
                        return false;
                    } finally {
                        try {
                            if(fw!=null)
                                fw.close();
                        } catch(Exception e1) {
                            sub.setCompileError("error occur whiling closing filewriter: " + e1.toString());
                        }
                    }

                    // compile the submission
                    String command =  wrapperGccBuildCommand(gccBuildCommand," -c -o ",
                            getCCNoThreadingOptions(ApplicationConstants.SINGLE_PROBLEM) + " " +
                            COMPILE_COMMAND_NO_THREADING_SUFFIX) + " " +
                        classname + ".o " + classname + "-stub.cc" + " -DWRAPPER_THUNK" + " -I" +
                        WebServiceGeneratorResources.getProperty(WebServiceGeneratorResources.CPP_SOAP_HEADER_FILE);
        
                    logger.info("executing: " + command);
                    ew = new ExecWrapper(command, null, pathfile, "", CPP_COMPILE_TIMEOUT,
                                         Formatter.MAX_USER_STRING);

                    if (ew.error) {
                        logger.error("could not execute compiler on submission: "
                                     + ew.stderr + ew.stdout);
                        sub.setCompileError("Could not execute compiler on submission");
                        return false;
                    }

                    if (!ew.finished) {
                        String oops = "took too long (not done after " + ew.timetaken + " ms)";
                        if (ew.stderr.length() > 0)
                            sub.setCompileError(ew.stderr + "\n\n(Also, compilation " + oops + ".)");
                        else
                            sub.setCompileError("Compiling and linking submission " + oops);
                        return false;
                    }
                    if (ew.exitval != 0) {
                        sub.setCompileError("errors compiling:\n\n" + ew.stderr);
                        return false;
                    }

                    stdErr = ew.stderr;
                    stdOut = ew.stdout;

                    // link it all together
                    command =  wrapperGccBuildCommand(gccBuildCommand," -o ",
                            getCCNoThreadingOptions(ApplicationConstants.SINGLE_PROBLEM) + " " +
                            BUILD_COMMAND_NO_THREADING_SUFFIX) +
                        classname + " " + classname + ".o " + classname + "-stub.o ";

                    logger.info("executing: " + command);
                    ew = new ExecWrapper(command, null, pathfile, "", CPP_COMPILE_TIMEOUT,
                                         Formatter.MAX_USER_STRING);

                    if (ew.error) {
                        logger.error("could not execute linker on submission: " + ew.stderr + ew.stdout);

                        sub.setCompileError("Could not execute linker on submission");
                        return false;
                    }
                    if (!ew.finished) {
                        sub.setCompileError("linking submission took too long (not done after " + ew.timetaken + " ms)");
                        return false;
                    }
                    if (ew.exitval != 0) {
                        sub.setCompileError("errors linking:\n\n" + ew.stderr);
                        return false;
                    }

                    stdErr += ew.stderr;
                    stdOut += ew.stdout;

                    sub.setStdOut(stdOut);
                    sub.setStdErr(stdErr);

                    return true;
                }

                protected HashMap buildClassMap(MPSQASAlgoAdapter mpsqasWrapper) throws Exception {
                    HashMap classMap = new HashMap();
                    loadExtension(mpsqasWrapper, classMap, "");
                    return classMap;
                }
            };
    }
    
    /**
     * Builds a long compiler for CPP language
     * 
     * @return returns the compiler built
     */
    private static LongCompiler buildLongCompiler() {
        return new LongCompiler() {
            protected boolean compileLong(LongCompilationRequest sub) {
                String classname = sub.getClassName();
                File pathfile = new File(sub.getPath());
                String source = sub.getProgramText() + "\n";
                String extension = "cc";
                if(sub.getRoundType() == RoundTypes.CUDA_LONG_PROBLEM_ROUND_TYPE_ID || sub.getRoundType() == RoundTypes.CUDA_LONG_PROBLEM_PRACTICE_ROUND_TYPE_ID){
                    extension = "cu";
                }
        
                FileUtil.deleteRecursive(pathfile);
                
                pathfile.mkdirs();
                try {
                    FileWriter fw = new FileWriter(sub.getPath() + classname + "-stub."+extension);
                    fw.write(sub.getUserWrapperSource());
                    fw.close();
                } catch (Exception e) {
                    sub.setCompileError("could not write generated stub to file: " + e.toString());
                    return false;
                }
                
                try {
                    FileWriter fw = new FileWriter(sub.getPath() + sub.getExposedClassName() + ".cc");
                    fw.write(sub.getExposedWrapperSource());
                    fw.close();
                } catch (Exception e) {
                    sub.setCompileError("could not write generated stub to file: " + e.toString());
                    return false;
                }
        
                try {
                    FileWriter fw = new FileWriter(sub.getPath() + classname + ".cc");
                    fw.write(source);
                    fw.close();
                } catch (Exception e) {
                    sub.setCompileError("could not write submission to file: " + e.toString());
                    return false;
                }
                
                // compile the submission
                String gccBuildCommand = sub.getProblemCustomSettings().getGccBuildCommand();
                String command;
                if(gccBuildCommand!=null && gccBuildCommand.trim().length()>0) {
                    command = gccBuildCommand + IFLAGS+" -o " + classname + " " + classname + "-stub."+extension;
                } else {
                    if(sub.getRoundType() == RoundTypes.CUDA_LONG_PROBLEM_ROUND_TYPE_ID || sub.getRoundType() == RoundTypes.CUDA_LONG_PROBLEM_PRACTICE_ROUND_TYPE_ID){
                        command = BUILD_COMMAND_CUDA;
                    }else{
                        command = getCCNoThreadingOptions(ApplicationConstants.LONG_PROBLEM) + " " +
                                BUILD_COMMAND_SSE_SUFFIX;
                        /* Quick hack for GWAS Speedup Challenge.
                         * TODO: remove once the challenge is over.
                         */
                        if (classname.equals("GWASSpeedup")) {
                            command = getCCNoThreadingOptions(ApplicationConstants.LONG_PROBLEM) + " " +
                                    BUILD_COMMAND_SSE_LAPACK_SUFFIX;
                        }
                    }
                    
                    command = command + " " + classname + " " + classname + "-stub."+extension;
                }
        
                //add header info
                logger.info("executing: " + command);
                logger.info("Compile Time Limit: " + sub.getProblemCustomSettings().getCompileTimeLimit());
                ExecWrapper ew = new ExecWrapper(command, null, pathfile, "",
                        sub.getProblemCustomSettings().getCompileTimeLimit(), 100000);
        
                if (ew.error) {
                    logger.error("could not execute compiler on submission: " + ew.stderr + ew.stdout);
                    sub.setCompileError("Could not execute compiler on submission");
                    return false;
                }
        
                if (!ew.finished) {
                    String oops = "took too long (not done after " + ew.timetaken + " ms)";
                    if (ew.stderr.length() > 0)
                        sub.setCompileError(ew.stderr + "\n\n(Also, compilation " + oops + ".)");
                    else
                        sub.setCompileError("Compiling and linking submission " + oops);
                    return false;
                }
                if (ew.exitval != 0) {
                    sub.setCompileError("errors compiling:\n\n" + ew.stderr);
                    return false;
                }
                
                sub.setStdErr(ew.stderr);
                sub.setStdOut(ew.stdout);
                return true;
            }
    
            protected HashMap buildClassMap(MPSQASAdapter mpsqasWrapper) throws Exception {
                HashMap classMap = new HashMap();
                loadExtension(mpsqasWrapper, classMap, "");
                return classMap;
            }
        };
    }
}
