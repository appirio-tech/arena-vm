package com.topcoder.client.mpsqasApplet.messaging;

import com.topcoder.shared.language.Language;

import java.util.HashMap;

/**
 * Interface containing methods for making solution related (compile / test)
 * requests.
 *
 * @author mitalub
 */
public interface SolutionRequestProcessor {

    public void compile(HashMap codeFiles, Language language);

    public void test(Object[] args, int testType);

    public void systemTestAll();

    public void systemTest(int testType);
}
