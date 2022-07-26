/*
 * Copyright (C) - 2013 TopCoder Inc., All Rights Reserved.
 */

/*
 * FarmCompiler
 * 
 * Created 10/14/2006
 */
package com.topcoder.services.compiler.invoke;

import java.util.HashMap;
import java.util.Map;

import com.topcoder.netCommon.contest.ContestConstants;
import com.topcoder.server.common.Submission;
import com.topcoder.server.tester.LongSubmission;
import com.topcoder.shared.util.logging.Logger;
import com.topcoder.services.common.MPSQASFiles;
import com.topcoder.shared.language.BaseLanguage;
import com.topcoder.shared.problem.ProblemComponent;


/**
 * Compiler entry for compilation execution.
 *
 * <p>
 * Changes in version 1.0 (TC Competition Engine - R Language Compilation Support):
 * <ol>
 *      <li>Update {@link #FarmCompiler()} method.</li>
 * </ol>
 * </p>
 * @author Diego Belfer (Mural), TCSASSEMBLER
 * @version 1.0
 */
public class FarmCompiler implements CodeCompiler {
    private final Logger logger = Logger.getLogger(FarmCompiler.class);
    private final Map codeCompilers = new HashMap();
    /**
     * <p>
     * the farm compiler constructor to add all the support language compiler.
     * </p>
     */
    public FarmCompiler() {
       codeCompilers.put(new Integer(ContestConstants.JAVA), new JavaCodeCompiler());
       codeCompilers.put(new Integer(ContestConstants.CPP), new CPPCodeCompiler());
       codeCompilers.put(new Integer(ContestConstants.PYTHON), new PythonCodeCompiler());
       DotNetCodeCompiler dotNetCodeCompiler = new DotNetCodeCompiler();
       codeCompilers.put(new Integer(ContestConstants.VB), dotNetCodeCompiler);
       codeCompilers.put(new Integer(ContestConstants.CSHARP), dotNetCodeCompiler);
       codeCompilers.put(new Integer(ContestConstants.R),new RCodeCompiler());
    }
    
    /**
     * Compiles a long submission.
     * 
     * @param compilationRequest Compilation request providing all necessary information to compile 
     *      the submission (Keeping compatibility with previous implementation)
     * @return The LongSubmission object containg compilation result 
     */
    public LongSubmission compileLong(LongSubmission sub, ProblemComponent problemComponent) {
        int language = sub.getLanguage();
        CodeCompiler compiler = (CodeCompiler) codeCompilers.get(new Integer(language));
        if (compiler == null) {
            logger.error("Invalid language received at FarmCompiler, languageId="+language);
            sub.setCompileStatus(false);
            sub.setCompileError("Compiler error - Invalid languageId="+language);
        } else {
            String languageName = BaseLanguage.getLanguage(language).getName();
            logger.info("PROCESSING "+languageName+" LONG COMPILE: coderId=" + sub.getCoderID() + " componentId=" + sub.getComponentID()); 
            sub = compiler.compileLong(sub, problemComponent);
            logger.info(languageName+" Compile Status: " + sub.getCompileStatus());
            logger.info(languageName+" Compile Return:\n" + sub.getCompileError());
        }
        return sub;
    }
    

    /**
     * Compiles a MPSQASFiles 
     * 
     * @param compilationRequest Compilation request providing all necessary information to compile 
     *      the submission (Keeping compatibility with previous implementation)
     * @return The LongSubmission object containg compilation result 
     */
    public MPSQASFiles compileMPSQAS(MPSQASFiles mf) {
        int language = mf.getLanguage();
        CodeCompiler compiler = (CodeCompiler) codeCompilers.get(new Integer(language));
        if (compiler == null) {
            logger.error("Invalid language received at FarmCompiler, languageId="+language);
            mf.setCompileStatus(false);
            mf.setStdErr("Compiler error - Invalid languageId="+language);
        } else {
            String languageName = BaseLanguage.getLanguage(language).getName();
            logger.info("PROCESSING "+languageName+" MPSQAS COMPILE: Id=" + mf.getId() + " solutionId=" + mf.getSolutionId()); 
            mf  = compiler.compileMPSQAS(mf);
            logger.info(languageName+" Compile Status: " + mf.getCompileStatus());
            logger.info(languageName+" Compile Return:\n" + mf.getStdErr());
        }
        return mf;
    }

    public Submission compile(Submission sub) {
        int language = sub.getLanguage();
        CodeCompiler compiler = (CodeCompiler) codeCompilers.get(new Integer(language));
        if (compiler == null) {
            logger.error("Invalid language received at FarmCompiler, languageId="+language);
            sub.setCompileStatus(false);
            sub.setCompileError("Compiler error - Invalid languageId="+language);
        } else {
            String languageName = BaseLanguage.getLanguage(language).getName();
            logger.info("PROCESSING "+languageName+" COMPILE: coderId=" + sub.getCoderID() + " componentId=" + sub.getComponentID()); 
            sub = compiler.compile(sub);
            logger.info(languageName+" Compile Status: " + sub.getCompileStatus());
            logger.info(languageName+" Compile Return:\n" + sub.getCompileError());
        }
        return sub;
    }
}
