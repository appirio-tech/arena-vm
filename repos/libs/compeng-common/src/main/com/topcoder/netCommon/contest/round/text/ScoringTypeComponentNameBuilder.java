/*
 * ScoringTypeComponentNameBuilder
 * 
 * Created Oct 14, 2007
 */
package com.topcoder.netCommon.contest.round.text;

import com.topcoder.netCommon.contest.round.RoundProperties;

/**
 * @author Diego Belfer (mural)
 * @version $Id: ScoringTypeComponentNameBuilder.java 67962 2008-01-15 15:57:53Z mural $
 */
public class ScoringTypeComponentNameBuilder implements ComponentNameBuilder {
    private static final ComponentNameBuilder pointsBuilder = new PointsComponentNameBuilder();
    private static final ComponentNameBuilder namedBuilder = new NamedComponentNameBuilder();

    public String longNameForComponent(String className, double points, RoundProperties roundProperties) {
        return getBuilder(roundProperties).longNameForComponent(className, points, roundProperties);
    }
    public String shortNameForComponent(String className, double points, RoundProperties roundProperties) {
        return getBuilder(roundProperties).shortNameForComponent(className, points, roundProperties);
    }

    private ComponentNameBuilder getBuilder(RoundProperties roundProperties) {
        if (roundProperties.usesScore()) {
            return pointsBuilder;
        } else {
            return namedBuilder;
        }
    }
}
