/*
 * ComponentNameBuilder Created Oct 14, 2007
 */
package com.topcoder.netCommon.contest.round.text;

import com.topcoder.netCommon.contest.round.RoundProperties;

/**
 * Defines an interface which can create descriptive names for a problem component according to certain logic.
 * 
 * @author Diego Belfer (mural)
 * @version $Id: ComponentNameBuilder.java 72092 2008-08-05 06:27:07Z qliu $
 */
public interface ComponentNameBuilder {
    /**
     * Creates a short name for the problem component in a round.
     * 
     * @param className the class name of the problem component.
     * @param points the maximum score of the problem component.
     * @param roundProperties the properties of the round.
     * @return the short name for the problem component in a round.
     */
    String shortNameForComponent(String className, double points, RoundProperties roundProperties);

    /**
     * Creates a long name for the problem component in a round.
     * 
     * @param className the class name of the problem component.
     * @param points the maximum score of the problem component.
     * @param roundProperties the properties of the round.
     * @return the long name for the problem component in a round.
     */
    String longNameForComponent(String className, double points, RoundProperties roundProperties);
}