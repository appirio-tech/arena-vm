/*
 * NamedComponentNameBuilder
 * 
 * Created Oct 14, 2007
 */
package com.topcoder.netCommon.contest.round.text;

import com.topcoder.netCommon.contest.round.RoundProperties;

/**
 * @author Diego Belfer (mural)
 * @version $Id: NamedComponentNameBuilder.java 67962 2008-01-15 15:57:53Z mural $
 */
public class NamedComponentNameBuilder implements ComponentNameBuilder {
    public String shortNameForComponent(String className, double points, RoundProperties roundProperties) {
        return className;
    }

    public String longNameForComponent(String className, double points, RoundProperties roundProperties) {
        return className+" problem";
    }
}
