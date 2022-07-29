/*
 * Copyright (C) - 2022 TopCoder Inc., All Rights Reserved.
 */
package com.topcoder.services.tester;

import com.topcoder.shared.language.CPPLanguage;
import com.topcoder.shared.language.CSharpLanguage;
import com.topcoder.shared.language.JavaLanguage;
import com.topcoder.shared.language.PythonLanguage;
import com.topcoder.shared.language.Python3Language;
import com.topcoder.shared.language.VBLanguage;

/**
 * Tester factory.
 *
 * <p>
 * Changes in version 1.1 (Python3 support):
 * <ol>
 *      <li>Updated {@link #getTester(int)} method to support Python3 language.</li>
 * </ol>
 * </p>
 *
 * @autor Diego Belfer (Mural), liuliquan
 * @version 1.1
 */
public class TesterFactory {
    public static BaseTester getTester(int languageId) {
        switch (languageId) {
            case JavaLanguage.ID    : return new JAVATester();
            case CPPLanguage.ID     : return new CPPTester();
            case PythonLanguage.ID  : return new PythonTester(false);
            case Python3Language.ID  : return new PythonTester(true);
            case CSharpLanguage.ID  : return new DotNetTester();
            case VBLanguage.ID      : return new DotNetTester();
            default:
                throw new IllegalArgumentException("Illegal languageid="+languageId);
        }
    }
}
