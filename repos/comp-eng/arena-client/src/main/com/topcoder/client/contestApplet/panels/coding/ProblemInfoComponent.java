/*
 * ProblemInfoComponent
 * 
 * Created 06/12/2007
 */
package com.topcoder.client.contestApplet.panels.coding;

import com.topcoder.client.contestant.ProblemComponentModel;

/**
 * Common interface for Components used for display summary info of the problem
 * 
 * @author Diego Belfer (mural)
 * @version $Id: ProblemInfoComponent.java 65513 2007-09-24 19:31:43Z thefaxman $
 */
public interface ProblemInfoComponent {

    void updateComponentInfo(ProblemComponentModel problemComponent, int language);

    void clear();

}