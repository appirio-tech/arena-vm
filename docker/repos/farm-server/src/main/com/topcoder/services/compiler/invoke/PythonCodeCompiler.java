/*
* Copyright (C) - 2022 TopCoder Inc., All Rights Reserved.
*/
package com.topcoder.services.compiler.invoke;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.HashMap;

import com.topcoder.netCommon.mpsqas.ApplicationConstants;
import com.topcoder.server.common.Location;
import com.topcoder.server.common.Submission;
import com.topcoder.server.tester.ComponentFiles;
import com.topcoder.server.tester.LongSubmission;
import com.topcoder.server.tester.Python3ComponentFiles;
import com.topcoder.server.tester.PythonComponentFiles;
import com.topcoder.server.util.FileUtil;
import com.topcoder.server.util.Java13Utils;
import com.topcoder.services.common.CommonDaemon;
import com.topcoder.services.common.MPSQASFiles;
import com.topcoder.services.compiler.invoke.algocomponent.AlgoCompilationRequest;
import com.topcoder.services.compiler.invoke.algocomponent.AlgoCompiler;
import com.topcoder.services.compiler.invoke.algocomponent.MPSQASAlgoAdapter;
import com.topcoder.services.compiler.invoke.longcomponent.LongCompilationRequest;
import com.topcoder.services.compiler.invoke.longcomponent.LongCompiler;
import com.topcoder.services.compiler.invoke.longcomponent.MPSQASAdapter;
import com.topcoder.services.util.Formatter;
import com.topcoder.shared.common.ServicesConstants;
import com.topcoder.shared.language.JavaLanguage;
import com.topcoder.shared.problem.DataType;
import com.topcoder.shared.problem.ProblemComponent;
import com.topcoder.shared.problem.ProblemConstants;
import com.topcoder.shared.problem.SimpleComponent;
import com.topcoder.shared.util.logging.Logger;

/**
 * <p>
 * this is the python code compiler implementation to compile the python code.
 * </p>
 *
 * <p>
 * Changes in version 1.1 (TC Competition Engine - Python Language Upgrade And Customization Support v1.0):
 * <ol>
 *      <li>Added {@link #DEFAULT_PYTHON_COMMAND} field.</li>
 *      <li>Update {@link #buildLongCompiler()} method.</li>
 *      <li>Update {@link #compile(String pythonCommand,ByteArrayOutputStream log, ArrayList args)} method.</li>
 *      <li>Added {@link #writeException(Throwable throwable,ByteArrayOutputStream log)} method.</li>
 * </ol>
 * </p>
 *
 * <p>
 * Changes in version 1.2 (TC Competition Engine - C++ and Python Customization Support for SRM v1.0):
 * <ol>
 *      <li>Update {@link #compile(Submission sub)} method.</li>
 * </ol>
 * </p>
 *
 * <p>
 * Changes in version 1.3 (BUGR-9137 - Python Enable For SRM):
 * <ol>
 *      <li>Add {@link #compileAlgo(MPSQASFiles mpsqasFiles)} method.</li>
 *      <li>Updated {@link #compileMPSQAS(MPSQASFiles mpsqasFiles)} method. </li>
 *      <li>Add {@link #algoCompiler} field.</li>
 *      <li>Add {@link #wrapperComponent(AlgoCompilationRequest sub)} method </li>
 *      <li>Add {@link #buildAlgoCompiler()} method </li>
 * </ol>
 * </p>
 *
 * <p>
 * Changes in version 1.4 (Release Assembly - TopCoder Competition Engine Improvement Series 2 v1.0):
 * <ol>
 *      <li>Added {@link #ALGO_PYTHON_COMMAND_PROPERTY_NAME} and
 *      {@link #LONG_PYTHON_COMMAND_PROPERTY_NAME} constants.</li>
 *      <li>Added {@link #getPythonCommand(String)} method.</li>
 *      <li>Updated {@link #compile(Submission)}, {@link #buildAlgoCompiler()} and {@link #buildLongCompiler()}
 *      methods to use new {@link #getPythonCommand(String)}.</li>
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
 *
 * <p>
 * Changes in version 1.6 (Python3 Support):
 * <ol>
 *      <li>Added {@link #ALGO_PYTHON3_COMMAND_PROPERTY_NAME}, {@link #LONG_PYTHON3_COMMAND_PROPERTY_NAME},
 *       {@link #DEFAULT_PYTHON3_COMMAND}, {@link #python3} fields.</li>
 *      <li>Updated constructor to take <code>python3</code> parameter.</li>
 *      <li>Updated {@link #compile(Submission)} and {@link #compile(String, ByteArrayOutputStream, ArrayList)},
 *       {@link #generateWrapperForUserCode(SimpleComponent)}, {@link #getPythonCommand(int, String)} methods.</li>
 * </ol>
 * </p>
 *
 * @author savon_cn, liuliquan
 * @version 1.6
 */
public final class PythonCodeCompiler implements CodeCompiler {
    /**
     * This constant defines name for Python command property for SRM.
     * @since 1.4
     */
    private static final String ALGO_PYTHON_COMMAND_PROPERTY_NAME =
            "com.topcoder.services.compiler.invoke.PythonCodeCompiler.srmPythonCommand";
    private static final String ALGO_PYTHON3_COMMAND_PROPERTY_NAME =
            "com.topcoder.services.compiler.invoke.PythonCodeCompiler.srmPython3Command";

    /**
     * This constant defines name for Python command property for MM.
     * @since 1.4
     */
    private static final String LONG_PYTHON_COMMAND_PROPERTY_NAME =
            "com.topcoder.services.compiler.invoke.PythonCodeCompiler.mmPythonCommand";
    private static final String LONG_PYTHON3_COMMAND_PROPERTY_NAME =
            "com.topcoder.services.compiler.invoke.PythonCodeCompiler.mmPython3Command";

    private final Logger logger = Logger.getLogger(PythonCodeCompiler.class);
    private final LongCompiler longCompiler = buildLongCompiler();
    /**
     * <p>
     * the SRM compilation of python code.
     * </p>
     */
    private final AlgoCompiler algoCompiler = buildAlgoCompiler();
    //private static boolean VERBOSE = true;

    /**
     * <p>
     * the default python compile command.
     * </p>
     */
    private static final String DEFAULT_PYTHON_COMMAND = "python";
    private static final String DEFAULT_PYTHON3_COMMAND = "python3";

    /**
     * <p>
     * when true, use python3 to compile.
     * </p>
     */
    private final boolean python3;

    public PythonCodeCompiler(boolean python3) {
        this.python3 = python3;
    }

    /**
     * The compile method performs the compilation of the source file.
     * @param sub            Filled Submission object
     * @return boolean       Returns the compilation status (success/fail)
     */
    public Submission compile(Submission sub) {
        //if (VERBOSE) Log.msg("In CodeCompiler.compile...");

        String sourceDir;
        String classSource;
        String classFileDir;

        //File classesDir = null;
        //ArrayList classFileNames = null;
        SimpleComponent component = sub.getComponent();

        Location location = sub.getLocation();

        ComponentFiles componentFiles = python3 ? new Python3ComponentFiles(sub.getCoderID(),
                location.getContestID(), location.getRoundID(),
                sub.getComponentID(), component.getClassName()): new PythonComponentFiles(sub.getCoderID(),
                location.getContestID(), location.getRoundID(),
                sub.getComponentID(), component.getClassName());

        classFileDir = componentFiles.getClassesDir();

        sourceDir = (python3 ? ServicesConstants.PYTHON3_SUBMISSIONS : ServicesConstants.PYTHON_SUBMISSIONS) + classFileDir + "/compile/";
        //if (VERBOSE) Log.msg("Source Directory = " + sourceDir);

        classSource = sourceDir + component.getClassName() + ".py";
        logger.info("Python Source: " + classSource);

        boolean compileRetVal = false;

        String pythonCommand = getPythonCommand(component.getComponentTypeID(),
                component.getProblemCustomSettings().getPythonCommand());

        // our compile classpath
        try {
            File inputDir = new File(sourceDir);

            if (inputDir.exists()) {
                // delete the directory to get rid of old files
                if (!FileUtil.deleteRecursive(inputDir)) {
                    logger.info("was not able to delete directory " + inputDir);
                }
            }

            // create the directory structure
            inputDir.mkdirs();

            // writing out team dependency class files
//            ComponentFiles[] dependencyComponentFiles = TestService.getDependencyComponentFiles(location.getContestID(), location.getRoundID(),
//                    sub.getComponent().getComponentID(), sub.getCoderID(), ContestConstants.SUBMITTED_CLASS);
//            logger.info("dependencies.length = " + dependencyComponentFiles.length);
//            for (int i = 0; i < dependencyComponentFiles.length; i++) {
//                HashMap classFiles = dependencyComponentFiles[i].getClassMap();
//                for (Iterator it = classFiles.keySet().iterator(); it.hasNext();) {
//                    String key = (String) it.next();
//                    logger.info("making " + key);
//                    File file = new File(inputDir, key);
//                    file.getParentFile().mkdirs();
//                    BufferedOutputStream stream = new BufferedOutputStream(new FileOutputStream(file));
//                    stream.write((byte[]) classFiles.get(key));
//                    stream.flush();
//                    stream.close();
//                }
//            }

            // write the user's submitted Java source code
            File outputFile = new File(classSource);
            FileWriter out = new FileWriter(outputFile);

            out.write(sub.getProgramText());
            out.close();

            ArrayList args = new ArrayList();
            args.add(classSource);
            args.add(component.getClassName());

            ByteArrayOutputStream log = new ByteArrayOutputStream();
            
            // compile the thing
            compileRetVal = compile(pythonCommand, log, args);

            logger.info("LOG:" + log.toString());

            sub.setCompileError(Formatter.truncate(log.toString()));
            log.close();

        } catch (IOException e) {
            logger.error("IO Exception caught compiling source code", e);
        }

        boolean valid = true;

        if (compileRetVal)      // If it compiled successfully, continue processing...
        {
            //compile wrapper
            String wrapperSourceFile = sourceDir + ProblemConstants.WRAPPER_CLASS + ".py";
            String wrapper = "";
            FileWriter out = null;
            try {
                out = new FileWriter(new File(wrapperSourceFile));
                wrapper = generateWrapperForUserCode(component);
                out.write(wrapper);
            } catch (IOException ex) {
                logger.error("IO Exception caught while generating Wrapper code", ex);
            } finally {
                try {
                    if(out!=null)
                        out.close();
                } catch(IOException e1) {
                    logger.error("IO Exception caught while closing Wrapper code", e1);
                }
            }

            ArrayList args = new ArrayList();
            args.add(wrapperSourceFile);
            args.add(ProblemConstants.WRAPPER_CLASS);

            ByteArrayOutputStream log = new ByteArrayOutputStream();

            // compile the thing
            compileRetVal = compile(pythonCommand, log, args);

            logger.info("LOG:" + log.toString());

            sub.setCompileError(Formatter.truncate(log.toString()));
            try {
                log.close();
            } catch (IOException ex) {
                logger.error("IO Exception caught while closing compilation result", ex);
            }

            if(compileRetVal) {
                valid = componentFiles.setClasses(sub);

                if (valid && new File(sourceDir + "/" + component.getClassName() + ".pyc").exists()) {
                    sub.setClassFiles(componentFiles);
                } else {
                    valid = false;
                    if (sub.getCompileError().equals(""))
                        sub.setCompileError("Compilation was unsuccessful because no class file was created.");
                }
            }
        }

        //GT Added this check to ensure people do not have massive classfiles
        if (!CommonDaemon.checkObjectSize(sub.getClassFiles())) {
            sub.setClassFiles(null);
            sub.setCompileStatus(false);
            sub.setCompileError(CommonDaemon.SIZE_LIMIT_MESSAGE);
            return sub;
        }
        sub.setCompileStatus(compileRetVal && valid);
        return sub;
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
     * compile the python code.
     * </p>
     * @param pythonCommand the python command.
     * @param log the compile output log.
     * @param args the arguments of compilation command.
     * @return true=the compilation is successful.
     */
    private boolean compile(String pythonCommand,ByteArrayOutputStream log, ArrayList args) {
        // convert ArrayList to String
        String cmd = pythonCommand + " " + (python3 ? ServicesConstants.PYTHON3_COMPILER : ServicesConstants.PYTHON_COMPILER) + " ";

        for (int i = 0; i < args.size(); i++) {
            cmd += (String) args.get(i) + " ";
        }

        System.out.println(cmd);

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

        if(ret == 1) {
            //no class files generated
            return false;
        } else if(ret == 0) {
            return true;
        } else {
            //we don't know what this is
            logger.error("FIND OUT WHAT ERROR CODE " + ret + " IS");
            return true;
        }
    }

    public String generateWrapperForUserCode(SimpleComponent pc) {
        try{
            StringBuffer sol = new StringBuffer(1000);

            //load the code from the local fs, then replace variables as needed
            try {
                BufferedReader ir = new BufferedReader(new FileReader(python3 ? ServicesConstants.PYTHON3_WRAPPER : ServicesConstants.PYTHON_WRAPPER));
                while(ir.ready()) {
                    sol.append(ir.readLine());
                    sol.append("\n");
                }
                ir.close();
            } catch(IOException ios) {
                logger.error("Error loading wrapper", ios);
            }

            //replace simple things
            Java13Utils.replace(sol, "<CLASS_NAME>", pc.getClassName());

            //get the code from <ARGS> to </ARGS>, repeat over each method
            String innerLoop = sol.substring(sol.indexOf("<ARGS>"), sol.indexOf("</ARGS>") + "</ARGS>".length());
            String innerTemplate = innerLoop.substring("<ARGS>".length(), innerLoop.length() - "</ARGS>".length());

            StringBuffer inner = new StringBuffer();

            String methodName = pc.getMethodName();
            DataType[] paramTypes = pc.getParamTypes();
            DataType returnType = pc.getReturnType();

            for(int j = 0; j<paramTypes.length; j++){
                inner.append(innerTemplate);
                String type = paramTypes[j].getDescriptor(JavaLanguage.ID);

                Java13Utils.replace(inner, "<ARG_NAME>", "v" + j);

                type = Java13Utils.replace(new StringBuffer(type), "[]", "Array").toString();
                type = Character.toUpperCase(type.charAt(0))+type.substring(1);

                Java13Utils.replace(inner, "<ARG_METHOD_NAME>", "read" + type);
            }


            Java13Utils.replace(sol, innerLoop, inner.toString());


            String type = returnType.getDescriptor(JavaLanguage.ID);

            type = Java13Utils.replace(new StringBuffer(type), "[]", "Array").toString();
            type = Character.toUpperCase(type.charAt(0))+type.substring(1);

            Java13Utils.replace(sol, "<WRITE_METHOD_NAME>", "write" + type);
            Java13Utils.replace(sol, "<METHOD_NAME>", methodName);

            String params = "";
            for(int j = 0; j<paramTypes.length; j++){
                if(j!=0){
                    params += ", ";
                }
                params += "v";
                params += j;
            }

            Java13Utils.replace(sol, "<PARAMS>", params);

            if (logger.isDebugEnabled()) {
                logger.debug("Long user wrapper solution is: \n" + sol.toString());
            }
            return sol.toString();
        }catch(Exception e){
            logger.error("Error building wrapper.",e);
            return "";
        }
    }

    /**
     * The compile method performs the compilation of the source file.
     * @param mpsqasFiles    Filled MPSQASFiles object
     * @return boolean       Returns the compilation status (success/fail)
     */
    public MPSQASFiles compileMPSQAS(MPSQASFiles mpsqasFiles) {
        if (mpsqasFiles.getComponentType() ==  ProblemConstants.LONG_COMPONENT) {
            boolean success = longCompiler.compileLong(mpsqasFiles);
            mpsqasFiles.setCompileStatus(success);
        } else {
            mpsqasFiles.setCompileStatus(algoCompiler.compileAlgo(mpsqasFiles));
        }
        return mpsqasFiles;
    }

    public LongSubmission compileLong(LongSubmission sub, ProblemComponent problemComponent) {
        boolean success = longCompiler.compileLong(sub, problemComponent);
        sub.setCompileStatus(success);
        return sub;
    }
    /**
     * <p>
     * wrapper the <code>AlgoCompilationRequest</code> to generate the SRM user Wrapper code.
     * </p>
     * @param sub the SRM compilation submission
     * @return the wrappered <code>SimpleComponent</code>
     */
    private SimpleComponent wrapperComponent(AlgoCompilationRequest sub) {
        SimpleComponent sc = new SimpleComponent();
        sc.setClassName(sub.getClassName());
        DataType[] dataTypes = (DataType[])sub.getParamTypes().toArray(new DataType[0]);
        sc.setParamTypes(dataTypes);
        /**
         * @see line 380 
         * String type = returnType.getDescriptor(JavaLanguage.ID);
         */
        String returnDesc = sub.getReturnType(JavaLanguage.ID);
        sc.setReturnType(new DataType(returnDesc));
        sc.setMethodName(sub.getMethodName());
        return sc;
    }

    /**
     * <p>
     * Gets the Python command.
     * </p>
     *
     * @param componentTypeID The component type ID.
     * @param customCommand The custom Python command.
     *      If null/empty, then pre-configured/default value will be used.
     * @return The Python command to be used.
     * @since 1.4
     */
    private String getPythonCommand(int componentTypeId, String customCommand) {
        if (customCommand == null || customCommand.trim().length() == 0) {
            if (componentTypeId == ApplicationConstants.LONG_PROBLEM) {
                return System.getProperty(python3 ? LONG_PYTHON3_COMMAND_PROPERTY_NAME : LONG_PYTHON_COMMAND_PROPERTY_NAME,
                        python3 ? DEFAULT_PYTHON3_COMMAND : DEFAULT_PYTHON_COMMAND);
            } else {
                return System.getProperty(python3 ? ALGO_PYTHON3_COMMAND_PROPERTY_NAME : ALGO_PYTHON_COMMAND_PROPERTY_NAME,
                        python3 ? DEFAULT_PYTHON3_COMMAND :DEFAULT_PYTHON_COMMAND);
            }
        }
        return customCommand;
    }

    /**
     * <p>
     * build the SRM compiler of python.
     * </p>
     * @return the SRM compiler of python
     */
    private AlgoCompiler buildAlgoCompiler() {
        return new AlgoCompiler() {
            /**
             * <p>
             * compile the SRM submission in mpsqas client.
             * </p>
             */
            protected boolean compileAlgo(AlgoCompilationRequest sub) {
                String binDir = sub.getPath().replace('/',File.separatorChar);
                String sourceDir = binDir + "compile" + File.separatorChar;

                String classSource = sourceDir + sub.getClassName() + ".py";

                boolean compileRetVal = false;
                
                String pythonCommand = getPythonCommand(ApplicationConstants.SINGLE_PROBLEM,
                        sub.getProblemCustomSettings().getPythonCommand());

                try {
                    File rootDir = new File(binDir);
                    File inputDir = new File(sourceDir);

                    if (rootDir.exists()) {
                        // delete the directory to get rid of old files
                        if (!FileUtil.deleteRecursive(rootDir)) {
                            sub.setCompileError("was not able to delete directory " + rootDir);
                        }
                    }

                    // create the directory structure
                    inputDir.mkdirs();

                    // write the user's submitted Java source code
                    File outputFile = new File(classSource);
                    FileWriter out = new FileWriter(outputFile);

                    out.write(sub.getProgramText());
                    out.close();
                    out = new FileWriter(new File(sourceDir + ProblemConstants.WRAPPER_CLASS + ".py"));
                    
                    String userWrapperSource = generateWrapperForUserCode(wrapperComponent(sub));
                    out.write(userWrapperSource);
                    out.close();

                    ArrayList args = new ArrayList();
                    args.add(classSource);
                    args.add(sub.getClassName());

                    ByteArrayOutputStream log = new ByteArrayOutputStream();

                    // compile the thing
                    compileRetVal = compile(pythonCommand, log, args);

                    if(compileRetVal){
                        args = new ArrayList();

                        args.add(sourceDir + ProblemConstants.WRAPPER_CLASS + ".py");
                        args.add(ProblemConstants.WRAPPER_CLASS);

                        compileRetVal = compile(pythonCommand, log, args);
                    }
                    
                    //if compile successful, print stOut, else print stdErr
                    if(compileRetVal)
                        sub.setStdOut(log.toString());
                    else
                        sub.setStdErr(log.toString());
                    log.close();
                } catch (IOException e) {
                    logger.error("IO Exception caught compiling source code", e);
                    compileRetVal = false;
                }
               return compileRetVal;
            }

            /**
             * <p>
             * build the mpqas class files
             * </p>
             * @param mpsqasWrapper the mpsqas wrapper.
             * @return the class files.
             */
            protected HashMap buildClassMap(MPSQASAlgoAdapter mpsqasWrapper)
                    throws Exception {
                HashMap classList = new HashMap();

                String fileAbsolutePath = mpsqasWrapper.getPath()+"compile"+File.separatorChar+mpsqasWrapper.getClassName() + ".pyc";
                String fileRelativePath = mpsqasWrapper.getClassesDir() + '/' + mpsqasWrapper.getClassName() + ".pyc";

                //sore the file in the map using a relative path
                classList.put(fileRelativePath.replace('/',File.separatorChar), FileUtil.getContents(new File(fileAbsolutePath)));

                //wrapper

                fileAbsolutePath = mpsqasWrapper.getPath()+"compile"+File.separatorChar+"Wrapper.pyc";
                fileRelativePath = mpsqasWrapper.getClassesDir() + '/' + "Wrapper.pyc";

                //sore the file in the map using a relative path
                classList.put(fileRelativePath.replace('/',File.separatorChar), FileUtil.getContents(new File(fileAbsolutePath)));
                return classList;
            }
            
        };
    }
    /**
     * build the long problem of python.
     * @return the long compiler entity of result.
     */
    private LongCompiler buildLongCompiler() {
        return new LongCompiler() {
            /**
             * <p>
             * compile the long problem
             * </p>
             * @param sub the long compilation request.
             * @return true = the compilation is successful.
             */
            protected boolean compileLong(LongCompilationRequest sub) {
                String binDir = sub.getPath().replace('/',File.separatorChar);
                String sourceDir = binDir + "compile" + File.separatorChar;

                String classSource = sourceDir + sub.getClassName() + ".py";
                logger.info("Python Source: " + classSource);

                boolean compileRetVal = false;
                
                String pythonCommand = getPythonCommand(ApplicationConstants.LONG_PROBLEM,
                        sub.getProblemCustomSettings().getPythonCommand());

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

                    out.write("import " + sub.getExposedClassName() + "\n");
                    out.write(sub.getProgramText());
                    out.close();
                    out = new FileWriter(new File(sourceDir + "Wrapper.py"));
                    out.write(sub.getUserWrapperSource());
                    out.close();

                    out = new FileWriter(new File(sourceDir + sub.getExposedClassName() + ".py"));
                    out.write(sub.getExposedWrapperSource());
                    out.close();

                    ArrayList args = new ArrayList();
                    args.add(classSource);
                    args.add(sub.getClassName());

                    ByteArrayOutputStream log = new ByteArrayOutputStream();

                    // compile the thing
                    compileRetVal = compile(pythonCommand, log, args);

                    if(compileRetVal){
                        args = new ArrayList();

                        args.add(sourceDir + "Wrapper.py");
                        args.add("Wrapper");

                        compileRetVal = compile(pythonCommand, log, args);

                        if(compileRetVal) {
                            args = new ArrayList();
                            args.add(sourceDir + sub.getExposedClassName() + ".py");
                            args.add(sub.getExposedClassName());

                            compileRetVal = compile(pythonCommand, log, args);
                        }
                    }
                    
                    //if compile successful, print stOut, else print stdErr
                    if(compileRetVal)
                        sub.setStdOut(log.toString());
                    else
                        sub.setStdErr(log.toString());
                    log.close();
                } catch (IOException e) {
                    logger.error("IO Exception caught compiling source code", e);
                    compileRetVal = false;
                }
               return compileRetVal;
            }

            protected HashMap buildClassMap(MPSQASAdapter mpsqasWrapper) throws Exception {
                HashMap classList = new HashMap();

                String fileAbsolutePath = mpsqasWrapper.getPath()+"compile"+File.separatorChar+mpsqasWrapper.getClassName() + ".pyc";
                String fileRelativePath = mpsqasWrapper.getClassesDir() + '/' + mpsqasWrapper.getClassName() + ".pyc";

                //sore the file in the map using a relative path
                classList.put(fileRelativePath.replace('/',File.separatorChar), FileUtil.getContents(new File(fileAbsolutePath)));

                //wrapper

                fileAbsolutePath = mpsqasWrapper.getPath()+"compile"+File.separatorChar+"Wrapper.pyc";
                fileRelativePath = mpsqasWrapper.getClassesDir() + '/' + "Wrapper.pyc";

                //sore the file in the map using a relative path
                classList.put(fileRelativePath.replace('/',File.separatorChar), FileUtil.getContents(new File(fileAbsolutePath)));

                fileAbsolutePath = mpsqasWrapper.getPath()+"compile"+File.separatorChar+mpsqasWrapper.getExposedClassName() + ".pyc";
                fileRelativePath = mpsqasWrapper.getClassesDir() + '/' + mpsqasWrapper.getExposedClassName() + ".pyc";

                //sore the file in the map using a relative path
                classList.put(fileRelativePath.replace('/',File.separatorChar), FileUtil.getContents(new File(fileAbsolutePath)));

                return classList;
            }
        };
    }

}
