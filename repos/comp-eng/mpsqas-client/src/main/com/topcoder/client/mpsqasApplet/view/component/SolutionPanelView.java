package com.topcoder.client.mpsqasApplet.view.component;

import com.topcoder.shared.language.Language;

/**
 * Abstract class defining methods for a Solution Panel View.
 *
 * @author mitalub
 */
public abstract class SolutionPanelView extends ComponentView {

    public abstract String getSolutionText();

    public abstract Language getLanguage();
}
