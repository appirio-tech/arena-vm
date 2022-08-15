/*
 * LongTestRequerimentsBuilder
 * 
 * Created 14/09/2006
 */
package com.topcoder.server.farm.longtester;

import com.topcoder.farm.shared.expression.Expressions;
import com.topcoder.farm.shared.processorproperties.RequerimentsBuilder;

/**
 * @author Diego Belfer (mural)
 * @version $Id: LongTestRequerimentsBuilder.java 54869 2006-12-01 18:02:46Z thefaxman $
 */
public class LongTestRequerimentsBuilder extends RequerimentsBuilder {
    public static final String LONG_TEST_MODULE = "long-test";

    public LongTestRequerimentsBuilder() {
        deployedModule(LONG_TEST_MODULE);
    }
    
    public void languageId(int languageId) {
        addNotNullAnd(getLanguagesProperty(), Expressions.contains(getLanguagesProperty(), new Integer(languageId)));
    }

    public void roundType(int roundType) {
        final String roundTypesProp = getModulePropertyName(LONG_TEST_MODULE, "roundTypes");
        addNotNullAnd(roundTypesProp, Expressions.contains(roundTypesProp, new Integer(roundType)));
    }

    public static String getLanguagesProperty() {
        return getModulePropertyName(LONG_TEST_MODULE, "languages");
    }

    public static String getRoundTypesProperty() {
        return getModulePropertyName(LONG_TEST_MODULE, "roundTypes");
    }
}
