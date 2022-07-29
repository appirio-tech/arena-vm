package com.topcoder.services.compiler.invoke;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;

import com.topcoder.server.common.Submission;
import com.topcoder.server.tester.LongSubmission;
import com.topcoder.server.util.FileUtil;
import com.topcoder.services.common.MPSQASFiles;
import com.topcoder.services.compiler.invoke.algocomponent.AlgoCompilationRequest;
import com.topcoder.services.compiler.invoke.algocomponent.AlgoCompiler;
import com.topcoder.services.compiler.invoke.algocomponent.MPSQASAlgoAdapter;
import com.topcoder.services.compiler.invoke.longcomponent.LongCompilationRequest;
import com.topcoder.services.compiler.invoke.longcomponent.LongCompiler;
import com.topcoder.services.compiler.invoke.longcomponent.MPSQASAdapter;
import com.topcoder.services.compiler.util.dotnet.DotNetCompilerExecutor;
import com.topcoder.services.compiler.util.dotnet.DotNetCompilerExecutor.CompilerResult;
import com.topcoder.services.compiler.util.dotnet.DotNetSecurtyChecker;
import com.topcoder.shared.common.ServicesConstants;
import com.topcoder.shared.language.BaseLanguage;
import com.topcoder.shared.language.CSharpLanguage;
import com.topcoder.shared.language.Language;
import com.topcoder.shared.problem.ProblemComponent;
import com.topcoder.shared.problem.ProblemConstants;
import com.topcoder.shared.util.logging.Logger;


public final class DotNetCodeCompiler implements CodeCompiler {
    private static final Logger logger = Logger.getLogger(DotNetCodeCompiler.class);

    /**
     * Compiler used to compile long submissions and long solutions
     */
    private LongCompiler longCompiler = buildLongCompiler();
    private AlgoCompiler algoCompiler = buildAlgoCompiler();

    public Submission compile(Submission sub) {
        boolean success = algoCompiler.compileAlgo(sub);
        sub.setCompileStatus(success);
        return sub;
    }

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

    private String buildErrorString(String errStr, String outStr) {
        String errorString = outStr;
        if (errorString.length()==0) {
            errorString = errStr;
        } else {
            errorString += "\n"+errStr;
        }
        return errorString;
    }

    private String removeFilenameFromOutput(String fileType, String errorString) {
        return errorString.replaceAll(".*\\."+fileType+"\\(", "(");
    }

    private AlgoCompiler buildAlgoCompiler() {
        return new AlgoCompiler() {
                protected boolean compileAlgo(AlgoCompilationRequest sub) {
                    sub.setCompileStatus(false);
                    String className = sub.getClassName();
                    String source = sub.getProgramText() + "\n";
                    String binDir = sub.getPath().replace('/',File.separatorChar);
                    String sourceDir = binDir + "compile" + File.separatorChar;

                    Language language = BaseLanguage.getLanguage(sub.getLanguage());
                    File classSource = new File(sourceDir, className + "." + language.getDefaultExtension());
                    logger.info("SRM .NET Source: " + classSource.getAbsolutePath());
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
                        FileUtil.writeContents(classSource, sub.getProgramText());

                        DotNetCompilerExecutor compiler = new DotNetCompilerExecutor();
                        CompilerResult result = compiler.compileLanguage(
                                                                         language,
                                                                         DotNetCompilerExecutor.TARGET_DLL,
                                                                         inputDir,
                                                                         className,
                                                                         rootDir);

                        if(result.isSuccess()){
                            DotNetSecurtyChecker dotNetSecurtyChecker = new DotNetSecurtyChecker();

                            String securityErrors = dotNetSecurtyChecker.checkCompiledClass(
                                                                                            new File(binDir),
                                                                                            className, "dll",
                                                                                            null,
                                                                                            false,
                                                                                            new String[] {sub.getMethodName()});

                            if (securityErrors != null && securityErrors.length() > 0) {
                                sub.setCompileError(securityErrors);
                                return false;
                            }
                        }

                        if (logger.isDebugEnabled()) {
                            logger.debug("stdout:\n"+result.getStdOut());
                            logger.debug("stderr:\n"+result.getStdErr());
                        }
                        compileRetVal = result.isSuccess();
                        sub.setStdErr(removeFilenameFromOutput(language.getDefaultExtension(), result.getStdErr()));
                        sub.setStdOut(removeFilenameFromOutput(language.getDefaultExtension(), result.getStdOut()));
                    } catch (IOException e) {
                        logger.error("IO Exception caught compiling source code", e);
                        sub.setCompileError("Internal compiler error.");
                        compileRetVal = false;
                    }
                    return compileRetVal;
                }

                protected HashMap buildClassMap(MPSQASAlgoAdapter mpsqasWrapper) throws Exception {
                    HashMap classList = new HashMap();
                    loadExtension(mpsqasWrapper, classList, ".dll");
                    return classList;
                }
            };
    }


    /**
     * Builds a long compiler for .NET languages
     *
     * @return returns the compiler built
     */
    private LongCompiler buildLongCompiler() {
        return new LongCompiler() {
            protected boolean compileLong(LongCompilationRequest sub) {
                String binDir = sub.getPath().replace('/',File.separatorChar);
                String sourceDir = binDir + "compile" + File.separatorChar;

                Language language = BaseLanguage.getLanguage(sub.getLanguage());

                //if (VERBOSE) Log.msg("Source Directory = " + sourceDir);

                String classSource = sourceDir + sub.getClassName() + "." + language.getDefaultExtension();
                logger.info("C# Source: " + classSource);

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


                    String exposedWrapperClassName = sub.getExposedClassName();
                    File sourceFile = new File(classSource);
                    File wrapperFile = new File(sourceDir, "Wrapper.cs");
                    File exposedWrapperFile = new File(sourceDir, exposedWrapperClassName + ".cs");


                    // write the user's submitted Java source code
                    FileUtil.writeContents(sourceFile, sub.getProgramText());
                    FileUtil.writeContents(wrapperFile, sub.getUserWrapperSource());
                    FileUtil.writeContents(exposedWrapperFile, sub.getExposedWrapperSource());


                    DotNetCompilerExecutor compiler = new DotNetCompilerExecutor();
                    CompilerResult result = compiler.compile(CSharpLanguage.CSHARP_LANGUAGE,
                                DotNetCompilerExecutor.TARGET_MODULE,
                                inputDir,
                                exposedWrapperClassName,
                                new File[] {exposedWrapperFile, new File(ServicesConstants.LONG_DOTNET_IO)},
                                rootDir);

                    StringBuffer logErr = new StringBuffer();
                    StringBuffer logOut = new StringBuffer();

                    logErr.append(result.getStdErr());
                    logOut.append(result.getStdOut());

                    //user code
                    if(result.isSuccess()) {
                        result = compiler.compile(language,
                                DotNetCompilerExecutor.TARGET_MODULE,
                                inputDir,
                                sub.getClassName(),
                                new File[] {sourceFile},
                                new File[] {new File(exposedWrapperClassName+".netmodule")},
                                rootDir);

                        logErr.append(result.getStdErr());
                        logOut.append(result.getStdOut());

                        if(result.isSuccess()) {
                            DotNetSecurtyChecker dotNetSecurtyChecker = new DotNetSecurtyChecker();

                            String securityErrors = dotNetSecurtyChecker.checkCompiledClass(
                                        new File(binDir),
                                        sub.getClassName(),
                                        "netmodule",
                                        new String[] {sub.getExposedClassName()},
                                        sub.isThreadingAllowed());

                            if (securityErrors != null && securityErrors.length() > 0) {
                                sub.setCompileError(securityErrors);
                                sub.setCompileStatus(false);
                                return false;
                            }

                            result = compiler.compile(CSharpLanguage.CSHARP_LANGUAGE,
                                    DotNetCompilerExecutor.TARGET_EXE,
                                    inputDir,
                                    sub.getClassName(),
                                    new File[] {wrapperFile},
                                    new File[] {new File(sub.getClassName()+".netmodule"), new File(exposedWrapperClassName+".netmodule")},
                                    rootDir);

                            logErr.append(result.getStdErr());
                            logOut.append(result.getStdOut());
                        }
                    }
                    String stdOut = logOut.toString();
                    String stdErr = logErr.toString();
                    if (logger.isDebugEnabled()) {
                        logger.debug("stdout:\n"+stdOut);
                        logger.debug("stderr:\n"+stdErr);
                    }
                    compileRetVal = result.isSuccess();
                    sub.setStdErr(stdErr);
                    sub.setStdOut(stdOut);
                } catch (IOException e) {
                    logger.error("IO Exception caught compiling source code", e);
                    compileRetVal = false;
                }
               return compileRetVal;
            }

            protected HashMap buildClassMap(MPSQASAdapter mpsqasWrapper) throws Exception {
                HashMap classList = new HashMap();
                loadExtension(mpsqasWrapper, classList, ".exe");
                loadExtension(mpsqasWrapper, classList, ".netmodule");

                String fileAbsolutePath = mpsqasWrapper.getPath()+mpsqasWrapper.getExposedClassName() + ".netmodule";
                String fileRelativePath = mpsqasWrapper.getClassesDir() + '/' + mpsqasWrapper.getExposedClassName() + ".netmodule";

                //sore the file in the map using a relative path
                classList.put(fileRelativePath.replace('/',File.separatorChar), FileUtil.getContents(new File(fileAbsolutePath)));

                return classList;
            }
        };
    }
}
