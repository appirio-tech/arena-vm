package com.topcoder.client.mpsqasApplet.view.component;

import com.topcoder.netCommon.mpsqas.ComponentInformation;

/**
 * Abstract class for the components panel view.
 */
public abstract class ComponentsPanelView extends ComponentView {

    /** In treetable */
    public abstract Object[] getSelectedComponentPath();

    public abstract int getSelectedWebServiceIndex();

    /** In JComboBox */
//  public abstract ComponentInformation getSelectedComponent();

    public abstract String getWebServiceName();

    public abstract String getComponentClassName();

    public abstract String getComponentMethodName();

    public abstract void clearWebService();

    public abstract void clearComponent();
}
