package com.topcoder.client.mpsqasApplet.controller.defaultimpl.component;

import com.topcoder.client.mpsqasApplet.controller.component.JavaDocPanelController;
import com.topcoder.client.mpsqasApplet.model.component.JavaDocPanelModel;
import com.topcoder.client.mpsqasApplet.view.component.JavaDocPanelView;
import com.topcoder.client.mpsqasApplet.model.component.ComponentModel;
import com.topcoder.client.mpsqasApplet.view.component.ComponentView;
import com.topcoder.client.mpsqasApplet.object.MainObjectFactory;
import com.topcoder.client.mpsqasApplet.util.Watchable;
import com.topcoder.client.mpsqasApplet.messaging.JavaDocUpdateResponseProcessor;
import com.topcoder.client.mpsqasApplet.common.UpdateTypes;
import com.topcoder.client.mpsqasApplet.common.ResponseClassTypes;

/**
 * Default implementation of the java doc panel controller.
 */
public class JavaDocPanelControllerImpl extends JavaDocPanelController
        implements JavaDocUpdateResponseProcessor {

    private JavaDocPanelModel model;
    private JavaDocPanelView view;

    public void init() {
        MainObjectFactory.getResponseHandler().registerResponseProcessor(this,
                ResponseClassTypes.GENERATE_JAVA_DOC);
    }

    public void setModel(ComponentModel model) {
        this.model = (JavaDocPanelModel) model;
    }

    public void setView(ComponentView view) {
        this.view = (JavaDocPanelView) view;
    }

    public void close() {
        MainObjectFactory.getResponseHandler().unregisterResponseProcessor(this,
                ResponseClassTypes.GENERATE_JAVA_DOC);
    }

    public void processGenerate() {
        MainObjectFactory.getIStatusMessageRequestProcessor().addMessage(
                "Generating java docs....", false);
        MainObjectFactory.getWebServiceRequestProcessor().generateJavaDocs();
    }

    public void processJavaDocUpdate(String html) {
        model.setPreviewHTML(html);
        model.notifyWatchers(UpdateTypes.PREVIEW_CONTENTS);
    }

    /**
     * Passes the notification to the component model's watchers.
     */
    public void update(Watchable w, Object arg) {
        model.notifyWatchers(arg);
    }
}
