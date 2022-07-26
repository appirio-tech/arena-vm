/*
 * CodeCompiler
 * 
 * Created 12/12/2006
 */
package com.topcoder.services.compiler.invoke;

import com.topcoder.server.common.Submission;
import com.topcoder.server.tester.LongSubmission;
import com.topcoder.services.common.MPSQASFiles;
import com.topcoder.shared.problem.ProblemComponent;

/**
 * @author Diego Belfer (mural)
 * @version $Id: CodeCompiler.java 56700 2007-01-29 21:13:11Z thefaxman $
 */
public interface CodeCompiler {

    /**
     * The compile method performs the compilation of the source file.
     * @param sub            Filled Submission object
     * @return boolean       Returns the compilation status (success/fail)
     */
    public Submission compile(Submission sub);

    /**
     * The compile method performs the compilation of the source file.
     *
     * @param  mpsqasFiles   Filled MPSQASFiles object
     * @return               Returns the compilation status (success/fail)
     */
    public MPSQASFiles compileMPSQAS(MPSQASFiles mpsqasFiles);

    public LongSubmission compileLong(LongSubmission sub, ProblemComponent component);

}