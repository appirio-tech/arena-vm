/*
 * PointsComponentNameBuilder
 * 
 * Created Oct 14, 2007
 */
package com.topcoder.netCommon.contest.round.text;

import com.topcoder.netCommon.contest.round.RoundProperties;

/**
 * @author Diego Belfer (mural)
 * @version $Id: PointsComponentNameBuilder.java 67962 2008-01-15 15:57:53Z mural $
 */
public class PointsComponentNameBuilder implements ComponentNameBuilder {
    public String shortNameForComponent(String className, double points, RoundProperties roundProperties) {
        return String.valueOf(((int) points));
    }

    public String longNameForComponent(String className, double points, RoundProperties roundProperties) {
        return String.valueOf(((int) points))+"-point problem";
    }
}
