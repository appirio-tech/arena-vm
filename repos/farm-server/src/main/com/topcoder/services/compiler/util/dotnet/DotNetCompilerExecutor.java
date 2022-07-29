/*
* Copyright (C) 2006 - 2014 TopCoder Inc., All Rights Reserved.
*/

/*
 * DotNetCompilerExecutor
 * 
 * Created 12/20/2006
 */
package com.topcoder.services.compiler.util.dotnet;

import java.io.File;
import java.util.ArrayList;

import com.topcoder.farm.controller.configuration.ApplicationContextProvider;
import com.topcoder.farm.processor.ProcessorConfig;
import com.topcoder.shared.util.logging.Logger;
import com.topcoder.services.compiler.invoke.CompilerHelperThread;
import com.topcoder.shared.language.Language;

/**
 * DotNetCompilerExecutor provides methods that simplify DotNet compiler
 * invocation. <p> 
 *
 * <p>
 * Changes in version 1.0 (TopCoder Competition Engine - Enable New DotNET Features v1.0):
 * <ol>
 * <li>Added {@link #getReferenceDlls()}  method to refer the required dll in dotNet.</li>
 * <li>Updated {@link #compile(Language,Target,File,String,File[],File[],File)} to support the <code>System.Numerics</code></li>
 * </ol>
 * </p>
 *
 * <p>
 * Changes in version 1.1 (Update DotNet TestProcess Code for x64 environment v1.0):
 * <ol>
 *      <li>Add {@link #platformFlag} field.</li>
 *      <li>Updated {@link #compile(Language,Target,File,String,File[],File[],File)} to support the <code>platform</code></li>
 * </ol>
 * </p>
 * @author Diego Belfer (mural), TCSASSEMBLER
 * @version 1.1
 */
public class DotNetCompilerExecutor {
    private static final Logger logger = Logger.getLogger(DotNetCompilerExecutor.class);
    public static final Target TARGET_DLL = new DllTarget();
    public static final Target TARGET_EXE = new ExeTarget();
    public static final Target TARGET_MODULE = new ModuleTarget();
    private boolean logo =  false;
    private boolean optimize = true;
    private boolean debug = true;
    /**
     * <p>
     * the reference dlls used to dotNet assembly reference dlls.
     * it can be many dlls,so split with comma.
     * for example
     * <code>System.Numerics.dll</code>
     * </p>
     */
    private static final String referDlls; // = System.getProperty("com.topcoder.services.compiler.util.dotnet.DotNetCompilerExecutor.refdll", "");

    /**
     * <p>
     * The platform flag that the compilation is x86 or x64.
     * the default value is x86.
     * </p>
     * @since 1.1
     */
    private static final String platformFlag; // = System.getProperty("com.topcoder.services.compiler.util.dotnet.DotNetCompilerExecutor.platform","x86");
    
    static {
    	ProcessorConfig config = ApplicationContextProvider.getContext().getBean(ProcessorConfig.class);
    	referDlls = config.getDotNetReferenceDlls();
    	platformFlag = config.getDotNetPlatformFlag();
    }
    
    /**
     * <p>
     * get the reference dlls used to dotNet assembly reference dlls.
     * </p>
     * @return the refer dlls
     */
    private String getReferenceDlls() {
        if(referDlls!=null&&referDlls.trim().length()>0) {
           return referDlls;
        }
        return null;
    }
    /**
     * Creates a new DotNetCompilerExecutor with <code>debug</code> option set to <code>true</code>
     * and <code>optimize</code> option set <code>true</code>. 
     */
    public DotNetCompilerExecutor() {
    }
    
    /**
     * @return true if debug option is set
     */
    public boolean isDebug() {
        return debug;
    }

    /**
     * Sets debug option. Setting this to true will add /DEBUG directive 
     * to the compiler command line
     * 
     * @param debug The value to set
     */
    public void setDebug(boolean debug) {
        this.debug = debug;
    }

    /**
     * @return true if optimize option is set
     */
    public boolean isOptimize() {
        return optimize;
    }

    /**
     * Sets optimize option. Setting this to true will add /optimize directive to 
     * the compiler command line
     * 
     * @param optimize The value to set
     */
    public void setOptimize(boolean optimize) {
        this.optimize = optimize;
    }


    /**
     * Compiles the source file residing in <code>srcFolder</code>. The source filename is generated using the className and the
     * default language extension. 
     *  
     * @param language The language of the source file
     * @param target The target of the compilation. Expected values TARGET_DLL, TARGET_MODULE or TARGET_EXE 
     * @param srcFolder The source folder where source file resides
     * @param className The className. It is used for generated source file name and ouput filename.
     * @param dstFolder The folder where outputfile will be generated
     * 
     * @return The result of the compilation process
     */
    public CompilerResult compileLanguage(Language language, Target target, File srcFolder, String className, File dstFolder) {
        File srcFile = new File(srcFolder, className+"."+language.getDefaultExtension());
        return compile(language, target, srcFolder, className, new File[] {srcFile}, dstFolder);
    }


    /**
     * Compiles all sources files given
     * 
     * @param language The language of source files.
     * @param target The target of the compilation. Expected values TARGET_DLL, TARGET_MODULE or TARGET_EXE 
     * @param srcFolder The default folder where source file resides.  
     * @param dstFileName The fileName without extension that output file will have.
     * @param srcFiles The files to compile. Non absolute files will be made absolute using srcFolder as root.  
     * @param dstFolder The folder where outputfile will be generated
     * 
     * @return The result of the compilation process
     */
    public CompilerResult compile(Language language, Target target, File srcFolder, String dstFileName, File[] srcFiles, File dstFolder) {
        return compile(language, target, srcFolder, dstFileName, srcFiles, null, dstFolder); 
    }
    
    
    /**
     * Compiles all sources files given.
     * 
     * @param language The language of source files.
     * @param target The target of the compilation. Expected values TARGET_DLL, TARGET_MODULE or TARGET_EXE 
     * @param srcFolder The default folder where source file resides.  
     * @param dstFileName The fileName without extension that output file will have.
     * @param srcFiles The files to compile. Non absolute files will be made absolute using srcFolder as root.
     * @param modules Modules to add while linking the output file.  Non absolute files will be made absolute using 
     *                dstFolder as root.
     * @param dstFolder The folder where outputfile will be generated
     * 
     * @return The result of the compilation process
     */
    public CompilerResult compile(Language language, Target target,
        File srcFolder, String dstFileName, File[] srcFiles, File[] modules,
        File dstFolder) {
        ArrayList args = new ArrayList();

        String refDlls = getReferenceDlls();

        if (refDlls != null) {
            args.add("/r:" + refDlls);
        }

        if (platformFlag != null && platformFlag.trim().length() > 0) {
            args.add("/platform:" + platformFlag);
        }
        
        if (!logo) {
            args.add("/nologo");
        }

        args.add("/target:" + target.getTargetString());

        if (debug) {
            args.add("/debug");
        }

        if (optimize) {
            args.add("/optimize");
        }

        args.add("/out:" +
            new File(dstFolder, dstFileName + "." +
                target.getTargetExtension()).getAbsolutePath());

        if ((modules != null) && (modules.length > 0)) {
            args.add("/addmodule:" + buildFileList(modules, dstFolder, ";"));
        }

        if (srcFiles != null) {
            args.add(buildFileList(srcFiles, srcFolder, " "));
        }

        return compile(args, language.getDefaultExtension() + "c ");
    }

    
    private static CompilerResult compile(ArrayList args, String cmd) {
        //convert ArrayList to String
        for (int i = 0; i < args.size(); i++) {
            cmd += (String) args.get(i) + " ";
        }

        Process p;
        try {
            logger.info("Invoking compiler: "+cmd);
            p = Runtime.getRuntime().exec(cmd);
        } catch (Exception e) {
            logger.error("Exception launching compiler process: "+ cmd, e);
            return new CompilerResult(false, "", "Could not launch compiler");
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
            logger.error("Exception while destroying process",e);
            p = null;
        }

        CompilerResult result = new CompilerResult(false, new String(out.getStreamContents()), new String(err.getStreamContents()));

        if(ret == 1) {
            //no class files generated
            result.setSuccess(false);
        } else if(ret == 0) {
            result.setSuccess(true);
        } else {
            //we don't know what this is
            logger.error("FIND OUT WHAT ERROR CODE " + ret + " IS");
            result.setSuccess(true);
        }
        return result;
    }

    
    private String buildFileList(File[] files, File rootFolder, String separator) {
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < files.length; i++) {
            File file = getAbsoluteFile(files[i], rootFolder);
            sb.append(separator).append(file.getAbsolutePath());
        }
        if (sb.length() == 0) {
            return "";
        }
        return sb.substring(separator.length());
    }

    private File getAbsoluteFile(File file, File rootFolder) {
        if (file.isAbsolute()) {
            return file;
        } else if (rootFolder == null) {
            return file.getAbsoluteFile();
        } else {
            return new File(rootFolder, file.getPath());
        }
    }
    
    private static interface Target {
        String getTargetString();
        String getTargetExtension();
    }
    
    private static class DllTarget implements Target {
        public String getTargetExtension() {
            return "dll";
        }

        public String getTargetString() {
            return "library";
        }
    }
    
    private static class ModuleTarget implements Target {
        public String getTargetExtension() {
            return "netmodule";
        }

        public String getTargetString() {
            return "module";
        }
    }
    
    private static class ExeTarget implements Target {
        public String getTargetExtension() {
            return "exe";
        }

        public String getTargetString() {
            return "exe";
        }
    }
    
    public static class CompilerResult {
        private boolean success;
        private String stdOut;
        private String stdErr;
        
        public CompilerResult(boolean success, String stdOut, String stdErr) {
            this.success = success;
            this.stdOut = stdOut;
            this.stdErr = stdErr;
        }
        
        public String getStdErr() {
            return stdErr;
        }
        
        public void setStdErr(String stdErr) {
            this.stdErr = stdErr;
        }
        
        public String getStdOut() {
            return stdOut;
        }
        
        public void setStdOut(String stdOut) {
            this.stdOut = stdOut;
        }
        
        public boolean isSuccess() {
            return success;
        }
        
        public void setSuccess(boolean success) {
            this.success = success;
        }
    }
}
