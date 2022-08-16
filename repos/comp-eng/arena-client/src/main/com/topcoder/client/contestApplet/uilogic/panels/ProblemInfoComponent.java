package com.topcoder.client.contestApplet.uilogic.panels;

import com.topcoder.client.contestant.ProblemComponentModel;

/**
 * Common interface for Components used for display summary info of the problem
 * 
 * @author Diego Belfer (mural)
 * @version $Id: ProblemInfoComponent.java 67261 2007-12-04 16:49:51Z thefaxman $
 */
public interface ProblemInfoComponent {

    void updateComponentInfo(ProblemComponentModel problemComponent, int language);

    void clear();

}
