/*
 * LongSubmitter
 *
 * Created 10/26/2006
 */
package com.topcoder.server.ejb.TestServices.longtest;

import com.topcoder.server.ejb.ProblemServices.ProblemServicesLocator;
import com.topcoder.server.farm.compiler.CompilerInvoker;
import com.topcoder.server.farm.compiler.CompilerTimeoutException;
import com.topcoder.server.tester.LongSubmission;
import com.topcoder.shared.problem.ProblemComponent;
import com.topcoder.shared.util.logging.Logger;

/**
 * LongSubmitter is responsible for compiling LongSubmission submission.<p>
 *
 * Only one instance of this class must be created with using a given id.<p>
 *
 * TODO refactor this and move to more specific package
 *
 * @author Diego Belfer (mural)
 * @version $Id: LongSubmitter.java 74113 2008-12-29 19:34:43Z dbelfer $
 */
@Deprecated
public class LongSubmitter {
    private static final String LONG_SUBMITTER_PREFIX = "LS-";
    private static final Logger logger = Logger.getLogger(LongSubmitter.class);
    private final String longSubmitterUniqueId;
    private CompilerInvoker compiler =  null;

    /**
     * Creates a LongSubmitter for the given longSubmitterUniqueId.<p>
     *
     * Note: Only one instance of this class should be created for the given longSubmitterUniqueId
     *
     * @param longSubmitterUniqueId The unique id given to this LongSubmitter.
     */
    public LongSubmitter(String longSubmitterUniqueId) {
        this.longSubmitterUniqueId = longSubmitterUniqueId;
    }

    public LongSubmission compileLong(LongSubmission sub) throws CompilerTimeoutException {
        try {
            logger.info("compile long");
            ProblemComponent problemComponent = ProblemServicesLocator.getService().getProblemComponent(sub.getComponentID(), true);
            int codeLength = sub.getCode().getBytes().length;
            int lengthLimit = problemComponent.getCodeLengthLimit();
            logger.info("code length limit:" + lengthLimit + ", source length:" + codeLength);
            if (codeLength > lengthLimit) {
                sub.setCompileError("Your code is too long to be compiled.\nThe allowed code length is " + lengthLimit + " bytes.\nYour code length is " + codeLength + " bytes.\n");
                sub.setCompileStatus(false);
            } else {
            sub = getCompiler().compileLongSubmission(sub, problemComponent);
            }
            return sub;
        } catch (CompilerTimeoutException e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException("Compiler invocation failed", e);
        }
    }

    public synchronized CompilerInvoker getCompiler() {
        if (compiler == null) {
            compiler = CompilerInvoker.create(LONG_SUBMITTER_PREFIX+longSubmitterUniqueId);
        }
        return compiler;
    }
}
