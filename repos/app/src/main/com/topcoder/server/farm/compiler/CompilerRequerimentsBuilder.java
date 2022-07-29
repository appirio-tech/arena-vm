/*
 * Copyright (C) 2006-2013 TopCoder Inc., All Rights Reserved.
 */
package com.topcoder.server.farm.compiler;

import com.topcoder.farm.shared.expression.Expressions;
import com.topcoder.farm.shared.processorproperties.RequerimentsBuilder;
import com.topcoder.netCommon.contest.ContestConstants;

/**
 * <p>
 * Changes in (Round Type Option Support For SRM Problem):
 * <ol>
 * <li>Update {@link #roundType(int roundType)}  method.</li>
 * </ol>
 * </p>
 * @author Diego Belfer (mural), savon_cn
 * @version 1.0
 */
public class CompilerRequerimentsBuilder extends RequerimentsBuilder {
    public static final String THREADING_PROP_NAME = "threading";
    public static final String COMPILER_MODULE = "compiler";

    public CompilerRequerimentsBuilder() {
        deployedModule(COMPILER_MODULE);
    }

    public void languageId(int languageId) {
        addNotNullAnd(getLanguagesProperty(), Expressions.contains(getLanguagesProperty(), new Integer(languageId)));
    }

    public static String getLanguagesProperty() {
        return getModulePropertyName(COMPILER_MODULE, "languages");
    }

    public void compilerActionId(int actionId) {
        addNotNullAnd(getActionProperty(), Expressions.contains(getActionProperty(), new Integer(actionId)));
    }

    public static String getActionProperty() {
        return getModulePropertyName(COMPILER_MODULE, "action");
    }

    public void threadingAllowed(boolean allowed) {
        final String allowThreadProp = getModulePropertyName(COMPILER_MODULE, THREADING_PROP_NAME);
        andExpression( Expressions.or(
                        Expressions.eq(allowThreadProp, null),
                        Expressions.eq(allowThreadProp, Boolean.valueOf(allowed))
                     ));
    }

    public static String getThreadingAllowedProperty() {
        return getModulePropertyName(COMPILER_MODULE, THREADING_PROP_NAME);
    }

    public void roundType(int roundType) {
        final String roundTypesProp = getModulePropertyName(COMPILER_MODULE, "roundTypes");
        /**
         * the default old processor have no compiler.roundTypes definition in mysql
         * <code>roundTypesProp</code> will always null
         * we can handle the <code>roundType=-1</code> here
         *
         * the new processor must have roundTypes
         */
        andExpression( Expressions.or(
                        Expressions.eq(roundTypesProp, null),
                        Expressions.contains(roundTypesProp, new Integer(roundType))
                     ));
    }
    
    public static String getRoundTypesProperty() {
        return getModulePropertyName(COMPILER_MODULE, "roundTypes");
    }

}
