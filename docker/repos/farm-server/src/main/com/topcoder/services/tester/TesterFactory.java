/*
 * TesterFactory
 * 
 * Created 12/28/2006
 */
package com.topcoder.services.tester;

import com.topcoder.shared.language.CPPLanguage;
import com.topcoder.shared.language.CSharpLanguage;
import com.topcoder.shared.language.JavaLanguage;
import com.topcoder.shared.language.PythonLanguage;
import com.topcoder.shared.language.VBLanguage;

/**
 * @autor Diego Belfer (Mural)
 * @version $Id: TesterFactory.java 56700 2007-01-29 21:13:11Z thefaxman $
 */
public class TesterFactory {
    public static BaseTester getTester(int languageId) {
        switch (languageId) {
            case JavaLanguage.ID    : return new JAVATester();
            case CPPLanguage.ID     : return new CPPTester();
            case PythonLanguage.ID  : return new PythonTester();
            case CSharpLanguage.ID  : return new DotNetTester();
            case VBLanguage.ID      : return new DotNetTester();
            default:
                throw new IllegalArgumentException("Illegal languageid="+languageId);
        }
    }
}
