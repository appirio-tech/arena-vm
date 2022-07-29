package com.topcoder.client.mpsqasApplet.controller.defaultimpl.component;

import com.topcoder.client.mpsqasApplet.messaging.ProblemUpdateResponseProcessor;
import com.topcoder.client.mpsqasApplet.object.MainObjectFactory;
import com.topcoder.client.mpsqasApplet.model.component.ComponentModel;
import com.topcoder.client.mpsqasApplet.view.component.ComponentView;
import com.topcoder.client.mpsqasApplet.controller.component.ComponentsPanelController;
import com.topcoder.client.mpsqasApplet.model.component.ComponentsPanelModel;
import com.topcoder.client.mpsqasApplet.view.component.ComponentsPanelView;
import com.topcoder.client.mpsqasApplet.view.defaultimpl.treetable.MutableTreeTableNode;
import com.topcoder.client.mpsqasApplet.common.*;
import com.topcoder.client.mpsqasApplet.util.Watchable;
import com.topcoder.netCommon.mpsqas.*;
import com.topcoder.shared.problem.*;

import java.util.ArrayList;
import javax.swing.SwingUtilities;

/**
 * Default implementation of the components panel controller.  Processes the
 * adding and removing of team problem components and web services.
 *
 * @author mitalub
 */
public class ComponentsPanelControllerImpl extends ComponentsPanelController
        implements ProblemUpdateResponseProcessor {

    private ComponentsPanelView view;
    private ComponentsPanelModel model;

    public void init() {
        MainObjectFactory.getResponseHandler().registerResponseProcessor(this,
                ResponseClassTypes.PROBLEM_MODIFIED);
    }

    public void close() {
        MainObjectFactory.getResponseHandler().unregisterResponseProcessor(this,
                ResponseClassTypes.PROBLEM_MODIFIED);
    }

    public void setModel(ComponentModel model) {
        this.model = (ComponentsPanelModel) model;
    }

    public void setView(ComponentView view) {
        this.view = (ComponentsPanelView) view;
    }

    /**
     * Adds the component specified by the user input to the problem.
     */
    public void processAddComponent() {
        ComponentInformation c = new OpenComponentInformation();

        if (!view.getComponentClassName().trim().equals("") &&
                !view.getComponentMethodName().trim().equals("")) {
            //see if there is a root, and if there is make this a child of it.
            //if there is no root, then this will be the root.
            int componentTypeId = ApplicationConstants.MAIN_COMPONENT;
            ArrayList components = model.getComponents();
            if (components.size() > 0) {
                componentTypeId = ApplicationConstants.SECONDARY_COMPONENT;
            }

            c.setClassName(view.getComponentClassName().trim());
            c.setMethodName(view.getComponentMethodName()
                    .trim());
            c.setComponentTypeID(componentTypeId);
            c.setProblemId(model.getProblemInformation().getProblemId());
            c.setStatus(model.getProblemInformation().getStatus());

            components.add(c);
            updateProblemComponents();
            model.getMainModel().notifyWatchers(UpdateTypes.COMPONENTS_LIST);
            view.clearComponent();
        }
    }

    /**
     * Removes the selected component from the problem.
     */
    public void processRemoveComponent() {
        Object[] path = view.getSelectedComponentPath();
        if (path != null) {
            if (path.length == 2 && model.getComponents().size() > 1) {
                MainObjectFactory.getIStatusMessageRequestProcessor().addMessage(
                        "Root cannot be deleted unless it has 0 children.", true);
            } else if (path.length > 1) {
                ComponentInformation info = (ComponentInformation) ((HiddenObject)
                        ((MutableTreeTableNode) path[path.length - 1])
                        .getValueInColumn(0)).getObject();
                ArrayList components = model.getComponents();
                int index = components.indexOf(info);
                if (index != -1) components.remove(index);
                updateProblemComponents();
                model.getMainModel().notifyWatchers(UpdateTypes.COMPONENTS_LIST);
            }
        }
    }

    /**
     * Views the selected component.
     */
    public void processViewComponent() {
        if (model.canMove()) {
            Object[] path = view.getSelectedComponentPath();
            if (path != null && path.length > 1) {
                ComponentInformation info = (ComponentInformation) ((HiddenObject)
                        ((MutableTreeTableNode) path[path.length - 1])
                        .getValueInColumn(0)).getObject();

                if (info.getComponentId() == -1) {
                    MainObjectFactory.getIStatusMessageRequestProcessor().addMessage(
                            "This component does not yet exist on the server. "
                            + "Please submit the problem.", true);
                } else {
                    MainObjectFactory.getMoveRequestProcessor().viewComponent(
                            info.getComponentId());
                }
            }
        } else {
            MainObjectFactory.getIStatusMessageRequestProcessor().addMessage(
                    "Please reload the problem to get updated information.", true);
        }
    }

    /**
     * Adds the web service specified by the user input to the problem.
     */
    public void processAddWebService() {
        if (!view.getWebServiceName().trim().equals("")) {
            WebServiceInformation w = new OpenWebServiceInformation();
            w.setName(view.getWebServiceName().trim());
//      ComponentInformation component = view.getSelectedComponent();
            WebService[] webServices = new WebService[
                    model.getProblemInformation().getWebServices().length + 1];
            System.arraycopy(model.getProblemInformation().getWebServices(), 0,
                    webServices, 0,
                    model.getProblemInformation().getWebServices().length);
            webServices[webServices.length - 1] = w;
            model.getProblemInformation().setWebServices(webServices);
            updateModelWebServices();
            model.getMainModel().notifyWatchers(UpdateTypes.WEBSERVICES_LIST);
            view.clearWebService();
        }
    }

    /**
     * Removes the selected web service from the problem.
     */
    public void processRemoveWebService() {
        int index = view.getSelectedWebServiceIndex();
        if (index != -1) {
            WebService w = (WebService) model.getWebServiceObjects().get(index);
            WebService[] oldWS = model.getProblemInformation().getWebServices();
            for (int i = 0; i < oldWS.length; i++) {
                if (oldWS[i] == w) {
                    WebService[] webServices = new WebService[oldWS.length - 1];
                    for (int k = 0; k < oldWS.length; k++) {
                        if (k < i) {
                            webServices[k] = oldWS[k];
                        } else if (k > i) {
                            webServices[k - 1] = oldWS[k];
                        }
                    }
                    model.getProblemInformation().setWebServices(webServices);
                }
            }
            updateModelWebServices();
            model.getMainModel().notifyWatchers(UpdateTypes.WEBSERVICES_LIST);
        }
    }

    /**
     * Updates the webServiceObjects and data fields in the model based on the
     * web services in the components.
     */
    public void updateModelWebServices() {
        ArrayList webServiceObjects = new ArrayList();
        ArrayList o_data = new ArrayList();
        ProblemInformation c = model.getProblemInformation();
        for (int j = 0; j < c.getWebServices().length; j++) {
            webServiceObjects.add(c.getWebServices()[j]);
            o_data.add(new Object[]{c.getWebServices()[j].getName()});
        }
        Object[][] data = new Object[o_data.size()][];
        for (int i = 0; i < o_data.size(); i++) {
            data[i] = (Object[]) o_data.get(i);
        }
        model.setWebServiceObjects(webServiceObjects);
        model.setWebServiceTableData(data);
    }

    /**
     * Views the selected web service.
     */
    public void processViewWebService() {
        int index = view.getSelectedWebServiceIndex();
        if (index != -1) {
            if (((WebService) model.getWebServiceObjects().get(index)).getWebServiceId()
                    == -1) {
                MainObjectFactory.getIStatusMessageRequestProcessor().addMessage(
                        "This is a new web service and does not exist on the server, "
                        + "please save the team problem before viewing it.", true);
            } else {
                MainObjectFactory.getMoveRequestProcessor().viewWebService(
                        ((WebServiceInformation) model.getWebServiceObjects().get(index))
                        .getWebServiceId());
            }
        }
    }

    private void updateProblemComponents() {
        ProblemComponent[] components = new ProblemComponent[
                model.getComponents().size()];
        for (int i = 0; i < model.getComponents().size(); i++) {
            components[i] = (ComponentInformation) model.getComponents().get(i);
        }
        model.getProblemInformation().setProblemComponents(components);
    }

    /**
     * Passes the notification to the component model's watchers.
     */
    public void update(Watchable w, Object arg) {
        model.notifyWatchers(arg);
    }

    public void processProblemModified(String modifierName) {
        model.setCanMove(false);
        model.notifyWatchers(UpdateTypes.PROBLEM_MODIFIED);
    }
}
