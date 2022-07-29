package com.topcoder.services.compiler.invoke;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import com.topcoder.server.common.Location;
import com.topcoder.server.common.Submission;
import com.topcoder.server.farm.common.RoundUtils;
import com.topcoder.server.tester.ComponentFiles;
import com.topcoder.server.tester.JavaComponentFiles;
import com.topcoder.server.tester.LongSubmission;
import com.topcoder.server.util.FileUtil;
import com.topcoder.services.common.CommonDaemon;
import com.topcoder.services.common.MPSQASFiles;
import com.topcoder.services.compiler.util.LongContestCodeGeneratorHelper;
import com.topcoder.services.util.Formatter;
import com.topcoder.shared.common.ServicesConstants;
import com.topcoder.shared.problem.ProblemComponent;
import com.topcoder.shared.problem.ProblemConstants;
import com.topcoder.shared.problem.SimpleComponent;
import com.topcoder.shared.util.logging.Logger;


/**
 *************************************************************************************
 * The JavaCodeCompiler is an internal java code compiler. It first constructs the path
 * to the user java "source" file (based on the submission information). The "source"
 * file is then copied to a new file with the appended package name. Finally the
 * java file is compiled and the status is returned. Any compiled errors are written
 * to a "compile.out" log file in the same directory as the source.
 * <br>
 * @author Alex Roman
 *************************************************************************************
 */

public final class JavaCodeCompiler implements CodeCompiler {

    private static final Logger logger = Logger.getLogger(JavaCodeCompiler.class);

    //private static boolean VERBOSE = true;

    /**
     * The compile method performs the compilation of the source file.
     * @param sub            Filled Submission object
     * @return boolean       Returns the compilation status (success/fail)
     */
    public Submission compile(Submission sub) {
        //if (VERBOSE) Log.msg("In JavaCodeCompiler.compile...");

        String sourceDir;
        String classSource;
        String classFileDir;
        //File classesDir = null;
        //ArrayList classFileNames = null;
        SimpleComponent component = sub.getComponent();

        Location location = sub.getLocation();

        ComponentFiles componentFiles = new JavaComponentFiles(sub.getCoderID(),
                location.getContestID(), location.getRoundID(),
                component.getComponentID(), component.getClassName());

        classFileDir = componentFiles.getClassesDir();

        sourceDir = ServicesConstants.JAVA_SUBMISSIONS + "/compile/" + classFileDir + "/";
        //if (VERBOSE) Log.msg("Source Directory = " + sourceDir);

        classSource = sourceDir + component.getClassName() + ".java";
        logger.info("JAVA Source: " + classSource);

        String packageName = CommonDaemon.getPackageName(sub);

        boolean compileRetVal = false;

        // our compile classpath
        String classPath = sourceDir;

        // since this is a webservice, we have to add the
        // ws-related jars to the compile class path.
        classPath += File.pathSeparator +
                WebServiceGeneratorResources.getProperty(
                        WebServiceGeneratorResources.WS_JAXRPC_RI_JAR) + File.pathSeparator +
                WebServiceGeneratorResources.getProperty(
                        WebServiceGeneratorResources.WS_JAXRPC_API_JAR) + File.pathSeparator +
                WebServiceGeneratorResources.getProperty(
                        WebServiceGeneratorResources.WS_ACTIVATION_JAR);

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

            // write out the web service client stub, if necessary
//            if (component.hasWebServiceDependencies()) {
//
//
//
//                // start building the command line, if we need to do a WS compile
//                ArrayList wsArgs = null;
//
//                // keep track of which WS files got compiled
//                ArrayList wsArray = null;
//
//                // id to keep each source file in separate directories
//                int tmpid = 0;
//
//                logger.debug("component has web service dependencies - gonna retreive them");
//
//                WebServiceRemoteFile[] wsrf;
//
//                try {
//                    wsrf = TeamServices.getInstance().getWebServiceClients(
//                            component.getProblemID(), JavaLanguage.ID);
//                } catch (TeamServicesException e) {
//                    // something happened while trying to retreive the
//                    // code from the database
//
//                    logger.error("TeamServicesException caught while trying to retreive WS client", e);
//
//                    // the message the user will see...
//                    sub.setCompileError("Internal compiler error");
//                    sub.setCompileStatus(false);
//
//                    return false;
//                }
//
//                // extract the WS dependencies
//                for (int i = 0; i < wsrf.length; i++) {
//                    logger.info("considering file: " + wsrf[i].getPath());
//                    // each file type requires different things to be done.
//                    switch (wsrf[i].getType()) {
//                    case WebServiceRemoteFile.WEB_SERVICE_IMPLEMENTATION:
//                    case WebServiceRemoteFile.WEB_SERVICE_HELPER:
//                    case WebServiceRemoteFile.WEB_SERVICE_USER_HELPER:
//                        logger.info("ws itself, ignoring..");
//                        // this is the webservice itself
//                        break;
//
//                    case WebServiceRemoteFile.WEB_SERVICE_INTERFACE:
//                    case WebServiceRemoteFile.WEB_SERVICE_CLIENT_HEADER:
//                    case WebServiceRemoteFile.WEB_SERVICE_CLIENT_SOURCE:
//                        logger.info("compile and cache, if necessary");
//                        // compile and cache it (if necessary).
//
//                        // compile each .java in a separate directory, so
//                        // each .class file that the file generates goes
//                        // into that directory, making it easy to tell
//                        // what source files created which .class files
//
//                        if (wsrf[i].hasCompiledObjectFiles()) {
//                            logger.info("hasCompiledObjectFiles = true, reconstructing.");
//                            WebServiceRemoteFile[] cwsrf =
//                                    wsrf[i].getCompiledObjectFiles();
//
//                            for (int j = 0; j < cwsrf.length; j++) {
//                                logger.info("reconstructing " + cwsrf[j].getPath() + " to " + inputDir);
//                                cwsrf[j].reconstruct(inputDir);
//                            }
//                        } else {
//                            logger.info("hasCompiledObjectFiles = false...");
//                            if (wsArgs == null) {
//                                wsArgs = new ArrayList();
//                                wsArray = new ArrayList();
//
//                                wsArgs.add("-classpath");
//                                wsArgs.add(classPath);
//                            }
//
//                            File f = new File(inputDir, "wstmp" + "/" + tmpid + "/");
//                            wsrf[i].reconstruct(f); // save it here
//                            logger.info("reconstructing " + wsrf[i].getPath() + " to " + f);
//                            tmpid++;
//
//                            wsArgs.add(f.getPath() + ((!f.getPath().endsWith("/") && !wsrf[i].getPath().startsWith("/")) ? "/" : "")
//                                    + wsrf[i].getPath());
//                            wsArray.add(wsrf[i]);
//                        }
//
//                        break;
//
//                    case WebServiceRemoteFile.WEB_SERVICE_CLIENT_OBJECT:
//                        // don't think this is supposed to happen
//                        // wsrf[i].reconstruct(inputDir);
//                        logger.info("java compiler got WS remote file type: " +
//                                wsrf[i].getType());
//
//                        break;
//                    }
//                }
//
//                if (wsArgs != null) {
//                    // guess we gotta compile something
//                    logger.info("java compiler compiling WS stub");
//
//                    ByteArrayOutputStream log = new ByteArrayOutputStream();
//
//                    // compile the thing
//                    logger.info("wsArgs: " + wsArgs);
//                    compileRetVal = compile(log, wsArgs);
//                    log.close();
//
//                    if (compileRetVal == false) {
//                        logger.error("Error while trying to compile WS client stub"
//                                + log.toString());
//
//                        // the message the user will see...
//                        sub.setCompileError("Internal compiler error");
//                        sub.setCompileStatus(false);
//
//                        return false;
//                    }
//
//                    for (int i = 0; i < wsArray.size(); i++) {
//                        WebServiceRemoteFile rf = (WebServiceRemoteFile) wsArray.get(i);
//
//                        // get the directory for the file
//                        File file = new File((String) wsArgs.get(i + 2));
//                        File dir = file.getParentFile();
//
//                        ArrayList classes = getClassFileNames(dir);
//
//                        if (classes.size() == 0) {
//                            logger.info("compiliation of " +
//                                    (String) wsArgs.get(i + 2) + " generated no classes");
//
//                            continue;
//                        }
//
//                        List r = new Vector();
//
//                        for (int j = 0; j < classes.size(); j++) {
//                            String clazz = (String) classes.get(j);
//                            logger.info("Reading in: " + clazz);
//                            FileInputStream fis = new FileInputStream(clazz);
//
//                            byte[] b = new byte[fis.available()];
//                            fis.read(b);
//                            fis.close();
//
//
//                            WebServiceRemoteFile mywsrf = new WebServiceRemoteFile(rf.getSourceFileID().longValue(),
//                                    rf.getBasePath() + clazz.substring(clazz.lastIndexOf("/")), b,
//                                    WebServiceRemoteFile.WEB_SERVICE_CLIENT_OBJECT,
//                                    null, rf.getLanguageID().intValue());
//
//                            // save the class file in it's proper spot
//                            mywsrf.reconstruct(inputDir);
//
//                            r.add(mywsrf);
//                        }
//
//                        try {
//                            TeamServices.getInstance().saveCompiledWebServiceClients(
//                                    rf.getSourceFileID().longValue(),
//                                    (WebServiceRemoteFile[]) r.toArray(new WebServiceRemoteFile[r.size()]));
//                        } catch (TeamServicesException e) {
//                            // something happened while trying to save the
//                            // code from the database
//
//                            logger.error("TeamServicesException caught while trying to store compiled WS client", e);
//
//                            // the message the user will see...
//                            sub.setCompileError("Internal compiler error");
//                            sub.setCompileStatus(false);
//
//                            return false;
//                        }
//                    }
//
//                    logger.info("web service object files successfully cached...");
//                }
//            }

            ArrayList imports = new ArrayList();

            // writing out team dependency class files
//            ComponentFiles[] dependencyComponentFiles = TestService.getDependencyComponentFiles(location.getContestID(), location.getRoundID(),
//                    sub.getComponent().getComponentID(), sub.getCoderID(), ContestConstants.SUBMITTED_CLASS);
//            logger.info("dependencies.length = " + dependencyComponentFiles.length);
//            for (int i = 0; i < dependencyComponentFiles.length; i++) {
//                HashMap classFiles = dependencyComponentFiles[i].getClassMap();
//                for (Iterator it = classFiles.keySet().iterator(); it.hasNext();) {
//                    String key = (String) it.next();
//                    if (!imports.contains(key)) {
//                        imports.add(key);
//                    }
//                    String path = convertJavaClassNameToPath(key);
//                    logger.info("making " + key + "     " + path);
//                    File file = new File(inputDir, path);
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

            out.write("package " + packageName + ";");
            for (int i = 0; i < imports.size(); i++) {
                out.write("import " + (String) imports.get(i) + ";");
            }
            out.write(sub.getProgramText());
            out.close();

            ArrayList args = new ArrayList();
            args.add("-classpath");
            args.add(classPath);
            args.add(classSource);


            ByteArrayOutputStream log = new ByteArrayOutputStream();

            // compile the thing
            compileRetVal = compile(log, args);

            sub.setCompileError(Formatter.truncate(log.toString()));
            log.close();


            // clean up the compile error text
            sub.setCompileError(
                    cleanErrorLog(sub.getCompileError(), sourceDir, packageName));

        } catch (IOException e) {
            logger.error("IO Exception caught compiling source code", e);
        }

        boolean valid = true;

        if (compileRetVal)      // If it compiled successfully, continue processing...
        {
            valid = componentFiles.setClasses(sub);

            if (valid && new File(sourceDir + "/" + component.getClassName() + ".class").exists()) {
                sub.setClassFiles(componentFiles);
            } else {
                valid = false;
                if (sub.getCompileError().equals(""))
                    sub.setCompileError("Compilation was unsuccessful because no class file was created.");
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

//    private static String convertJavaClassNameToPath(String className) {
//        return className.replace('.', '/') + ".class";
//    }

    /**
     * The compile method performs the compilation of the source file.
     *
     * @param  mpsqasFiles   Filled MPSQASFiles object
     * @return               Returns the compilation status (success/fail)
     */
    public MPSQASFiles compileMPSQAS(MPSQASFiles mpsqasFiles) {
        String packageName = mpsqasFiles.getPackageName();
        String sourceDir = ServicesConstants.SOLUTIONS + "/compile";
        String classSource = null;
        String longProb = null;
        File classesDir = null;

        Map webServiceFiles;
        logger.info("MPSQAS JAVA Source: " + classSource);

        boolean compileRetVal = false;

        // start building our command line
        String cp = sourceDir + File.pathSeparator +
                WebServiceGeneratorResources.getProperty(
                        WebServiceGeneratorResources.WS_JAXRPC_RI_JAR) + File.pathSeparator +
                WebServiceGeneratorResources.getProperty(
                        WebServiceGeneratorResources.WS_JAXRPC_API_JAR);
        ArrayList args = new ArrayList();
        args.add("-classpath");
        args.add(cp);

        try {
            classesDir = new File(sourceDir);

            if (classesDir.exists()) {
                // delete the directory to get rid of old files
                if (!FileUtil.deleteRecursive(classesDir)) {
                    logger.info("was not able to delete directory " + classesDir);
                }
            }

            classesDir.mkdirs();

            // write out each of the source files
            Iterator iter = mpsqasFiles.getSourceFiles().entrySet().iterator();
            boolean isWriterLongSolution = false;
            while (iter.hasNext()) {
                Map.Entry me = (Map.Entry) iter.next();

                String sourceFile = sourceDir + "/" + ((String) me.getKey()).replace('.', '/') + ".java";

                File outputFile = new File(sourceFile);
                File parent = outputFile.getParentFile();
                if (parent != null) {
                    parent.mkdirs();
                }

                FileWriter out = new FileWriter(outputFile);

                out.write((String) me.getValue());
                out.close();
                if(sourceFile.endsWith("/"+ProblemConstants.TESTER_IO_CLASS+".java") ||
                   sourceFile.endsWith("/"+ProblemConstants.WRAPPER_CLASS+".java")) {
                    isWriterLongSolution = sourceFile.endsWith("/"+ProblemConstants.TESTER_IO_CLASS+".java");
                    longProb = sourceFile;
                }else if(sourceFile.endsWith("/"+mpsqasFiles.getClassName()+".java")){
                    args.add(sourceFile);
                } else {
//                    exposedClass = sourceFile;
                }
                logger.info(sourceFile);
            }

            // write out each of the client stub's source files, if it exist
            webServiceFiles = mpsqasFiles.getWebServiceFiles();

            if (webServiceFiles != null) {
                iter = webServiceFiles.entrySet().iterator();

                while (iter.hasNext()) {
                    Map.Entry me = (Map.Entry) iter.next();

                    String fileName = (String) me.getKey();

                    String sourceFile = sourceDir + "/" + fileName;

                    File outputFile = new File(sourceFile);

                    // create the parent directory...
                    File parent = outputFile.getParentFile();
                    if (parent != null) {
                        parent.mkdirs();
                    }

                    FileWriter out = new FileWriter(outputFile);

                    out.write((String) me.getValue());
                    out.close();

                    args.add(sourceFile);
                }
            }

            logger.info("args = " + args);

            ByteArrayOutputStream log = new ByteArrayOutputStream();

            // do the compile
            if(longProb != null){
                ArrayList al = new ArrayList();
                al.add("-classpath");
                al.add(cp+File.pathSeparator+ServicesConstants.APPS_CLASSES+File.pathSeparator);
                al.add(longProb);
                logger.info(al);
                compileRetVal = compile(log, al);
            }
            if(longProb == null || compileRetVal){
                if (isWriterLongSolution) {
                    args.set(1, args.get(1)+File.pathSeparator+ServicesConstants.WRITER_JAR);
                }
                compileRetVal = compile(log, args);
            }

            mpsqasFiles.setStdErr(Formatter.truncate(log.toString()));
            log.close();

            // clean up the error output
            mpsqasFiles.setStdErr(
                    cleanErrorLog(mpsqasFiles.getStdErr(), sourceDir, packageName));

        } catch (IOException e) {
            logger.error("IO Exception caught compiling source code", e);
        }

        boolean valid = true;

        if (compileRetVal)      // If it compiled successfully, continue processing...
        {
            ArrayList list = new ArrayList();
            HashMap classMap = new HashMap();

            try {
                // get all of the class files from the user's submission
                listClassFiles(classesDir, "", list);

                for (int i = 0; i < list.size(); i++) {
                    FileInputStream fis = new FileInputStream(new File(
                            classesDir, (String) list.get(i)));

                    byte[] b = new byte[fis.available()];
                    fis.read(b);
                    fis.close();

                    classMap.put((String) list.get(i), b);
                }
                logger.info("mpsqas classMap=" + classMap);
                mpsqasFiles.setClassFiles(classMap);
            } catch (Exception e) {
                logger.error("failed to retreive MPSQAS Java compile class files", e);

                valid = false;
                if (mpsqasFiles.getStdErr().equals(""))
                    mpsqasFiles.setStdErr("Compilation was unsuccessful because no class file was created.");
            }
        }

        mpsqasFiles.setCompileStatus(compileRetVal && valid);
        return mpsqasFiles;
    }

    private static String cleanErrorLog(String toSplice, String sourceDir,
            String packageName) {

        // First splice out the filesystem path
        String searchStr = sourceDir;
        if (!searchStr.endsWith("/")) {
            searchStr += "/";
        }

        //System.out.println("GTGT: " + toSplice);
        toSplice = remove(toSplice, searchStr);

        //We did this bc sun deprecated Javac
        final String LINE_SEPARATOR = System.getProperty("line.separator");
        searchStr = "Note: sun.tools.javac.Main has been deprecated." + LINE_SEPARATOR + "1 warning" + LINE_SEPARATOR;
        toSplice = remove(toSplice, searchStr);

        searchStr = "Note: sun.tools.javac.Main has been deprecated." + LINE_SEPARATOR;
        toSplice = remove(toSplice, searchStr);

        searchStr = ", 1 warning";
        toSplice = remove(toSplice, searchStr);

        // Now splice out the package name
        searchStr = "package " + packageName + ";";
        toSplice = remove(toSplice, searchStr);

        // Remove all other remnants of the package name
        searchStr = packageName + ".";
        toSplice = remove(toSplice, searchStr);

        return toSplice;
    }

    private static String remove(final String toSplice, String searchStr) {
        String result = toSplice;
        boolean keepGoing = true;
        while (keepGoing) {
            int index = result.indexOf(searchStr);
            if (index == -1)
                keepGoing = false;
            else {
                String begin = result.substring(0, index);
                String end = result.substring(index + searchStr.length());
                result = begin + end;
            }
        }
        return result;
    }

    private static boolean compile(ByteArrayOutputStream log, ArrayList args) {
        // convert ArrayList to String
        String cmd = "javac ";

        for (int i = 0; i < args.size(); i++) {
            cmd += (String) args.get(i) + " ";
        }
        logger.info("Executing : "+cmd);
        Process p;

        try {
            p = Runtime.getRuntime().exec(cmd);
        } catch (Exception e) {
            e.printStackTrace();
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
                logger.error("Exception while waiting for compiler",e);
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

//    /**
//     * This method creates an ArrayList of class file names from the given classDir.
//     *
//     * @param classDir - File - A File object containing a directory where a user's compiled
//     *                          class files are located.
//     * @return ArrayList - Strings of class file names.
//     *
//     * @author ademich
//     **/
//    ////////////////////////////////////////////////////////////////////////////////
//    private static ArrayList getClassFileNames(File classDir) {
//        ////////////////////////////////////////////////////////////////////////////////
//
//        String list[];
//        ArrayList classFiles = new ArrayList();
//
//        list = classDir.list(new FilenameFilter() {
//            public boolean accept(File dir, String file) {
//                String fileName = new File(file).getName().toLowerCase();
//                return fileName.indexOf(".class") != -1;
//            }
//        }
//        );
//
//        StringBuffer fullClassName;
//        for (int i = 0; i < list.length; i++) {
//            fullClassName = new StringBuffer();
//            fullClassName.append(classDir.getAbsolutePath()).
//                    append("/").
//                    append(list[i]);
//            classFiles.add(fullClassName.toString());
//        }
//
//        return classFiles;
//    }

    /**
     * Recursively stores a list of all files ending in ".class" in the storeList.
     * Prepends path to the the file names.
     */
    private static void listClassFiles(File classesDir, String path, ArrayList storeList) throws Exception {
        String[] s_list = classesDir.list();
        File file;
        for (int i = 0; i < s_list.length; i++) {
            file = new File(classesDir, s_list[i]);
            if (file.isDirectory()) {
                listClassFiles(file, path + s_list[i] + "/", storeList);
            } else if (s_list[i].endsWith(".class")) {
                storeList.add(path + s_list[i]);
            }
        }
    }

    public LongSubmission compileLong(LongSubmission sub, ProblemComponent component) {
        String sourceDir;
        String classSource;
        String classFileDir;
        long componentId = sub.getComponentID();
        long coderId = sub.getCoderID();
        long contestId = sub.getContestID();
        long roundId = sub.getRoundID();
        JavaComponentFiles componentFiles = new JavaComponentFiles(sub.getCoderID(),sub.getContestID(), sub.getRoundID(),sub.getComponentID(), component.getClassName());
        componentFiles.setThreadingAllowed(RoundUtils.isThreadingAllowed(component.getRoundType()));
        //File classesDir = null;
        //ArrayList classFileNames = null;
        
        classFileDir = "u"+coderId+"/c"+contestId+"/r"+roundId+"/p"+componentId;

        sourceDir = ServicesConstants.JAVA_SUBMISSIONS + "/compile/" + classFileDir + "/";
        //if (VERBOSE) Log.msg("Source Directory = " + sourceDir);

        classSource = sourceDir + component.getClassName() + ".java";
        logger.info("JAVA Source: " + classSource);

        String packageName = CommonDaemon.getPackageName(sub);

        boolean compileRetVal = false;

        // our compile classpath
        String classPath = ServicesConstants.JAVA_SUBMISSIONS + "/compile/";

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

            // write the user's submitted Java source code
            File outputFile = new File(classSource);
            FileWriter out = new FileWriter(outputFile);

            out.write("package " + packageName + ";");
            out.write(sub.getCode());
            out.close();

            String wrapperSourceFile = sourceDir + ProblemConstants.WRAPPER_CLASS + ".java";
            out = new FileWriter(new File(wrapperSourceFile));
            out.write(LongContestCodeGeneratorHelper.generateWrapperForUserCode(component, packageName, sub.getLanguageID()));
            out.close();

            //write out the exposed wrapper
            String name = component.getExposedClassName();
            if(name == null || name.equals("")) {
                name = "ExposedWrapper";
            }

            sub.setWrapperClassName(name);

            String exposedWrapperSourceFile = sourceDir + name + ".java";
            out = new FileWriter(new File(exposedWrapperSourceFile));
            out.write("package " + packageName + ";");
            out.write(LongContestCodeGeneratorHelper.generateWrapperForExposedCode(component, packageName, sub.getLanguageID()));
            out.close();

            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                //Should not happen.
                Thread.currentThread().interrupt();
            }

            //do the exposed wrapper first
            ArrayList args = new ArrayList();
            args.add("-classpath");
            args.add(classPath + File.pathSeparator + ServicesConstants.APPS_CLASSES);
            args.add(exposedWrapperSourceFile);

            ByteArrayOutputStream log = new ByteArrayOutputStream();

            //compile the thing
            compileRetVal = compile(log, args);

            if(compileRetVal) {

                args = new ArrayList();
                args.add("-classpath");
                args.add(classPath);
                args.add(classSource);

                compileRetVal = compile(log,args);

                if(compileRetVal){
                    args.set(args.size()-2,classPath+File.pathSeparator+ServicesConstants.APPS_CLASSES);
                    args.add(wrapperSourceFile);
                    logger.info(args.toString());
                    compileRetVal = compile(log,args);

                }
            }

            sub.setCompileError(Formatter.truncate(log.toString()));
            log.close();


            // clean up the compile error text
            sub.setCompileError(cleanErrorLog(sub.getCompileError(), sourceDir, packageName));

        } catch (IOException e) {
            logger.error("IO Exception caught compiling source code", e);
        }

        boolean valid = true;

        if (compileRetVal)      // If it compiled successfully, continue processing...
        {
            valid = componentFiles.setClasses(sub);

            if (valid && new File(sourceDir + "/" + component.getClassName() + ".class").exists()) {
                sub.setClassFiles(componentFiles);
            } else {
                valid = false;
                if (sub.getCompileError().equals(""))
                    sub.setCompileError("Compilation was unsuccessful because no class file was created.");
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
}
