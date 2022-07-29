/*
 * Copyright (C) 2006-2013 TopCoder Inc., All Rights Reserved.
 */
package com.topcoder.server.farm.tester;

import com.topcoder.farm.shared.expression.Expressions;
import com.topcoder.farm.shared.processorproperties.RequerimentsBuilder;
import com.topcoder.netCommon.contest.ContestConstants;

/**
 * <p>
 * Changes in (Round Type Option Support For SRM Problem):
 * <ol>
 * <li>Added {@link #roundType(int roundType)}  method.</li>
 * <li>Added {@link #getRoundTypesProperty()}  method.</li>
 * </ol>
 * </p>
 * @author Diego Belfer (mural), savon_cn
 * @version 1.0
 */
public class TesterRequerimentsBuilder extends RequerimentsBuilder {
    public static final String SRM_TEST_MODULE = "srm-test";

    public TesterRequerimentsBuilder() {
        deployedModule(SRM_TEST_MODULE);
    }
    /**
     * <p>
     * add round type as an item of test srm requirement.
     * if no specific round type passed,we must let it to the default <code>SRM_ROUND_TYPE_ID</code>
     * </p>
     * @param roundType
     *         the round type.
     */
    public void roundType(int roundType) {
        final String roundTypesProp = getModulePropertyName(SRM_TEST_MODULE, "roundTypes");        
        if(roundType == -1)
            roundType = ContestConstants.SRM_ROUND_TYPE_ID;
        
        if(roundType == ContestConstants.SRM_ROUND_TYPE_ID) {
            andExpression( Expressions.or(
                            Expressions.eq(roundTypesProp, null),
                            Expressions.contains(roundTypesProp, new Integer(roundType))
                         ));
        } else {
            addNotNullAnd(roundTypesProp, Expressions.contains(roundTypesProp, new Integer(roundType)));
        }
    }
    
    /**
     * <p>
     * get the round types property.
     * </p>
     * @return the round types
     */
    public static String getRoundTypesProperty() {
        return getModulePropertyName(SRM_TEST_MODULE, "roundTypes");
    }
    
    public void languageId(int languageId) {
        addNotNullAnd(getLanguagesProperty(), Expressions.contains(getLanguagesProperty(), new Integer(languageId)));
    }

    public static String getLanguagesProperty() {
        return getModulePropertyName(SRM_TEST_MODULE, "languages");
    }
    
    public void testerActionId(int actionId) {
        addNotNullAnd(getActionProperty(), Expressions.contains(getActionProperty(), new Integer(actionId)));
    }

    public static String getActionProperty() {
        return getModulePropertyName(SRM_TEST_MODULE, "action");
    }
}
