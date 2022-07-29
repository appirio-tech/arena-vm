package com.topcoder.client.mpsqasApplet.controller.defaultimpl;

import com.topcoder.client.mpsqasApplet.object.*;
import com.topcoder.client.mpsqasApplet.controller.WebServiceRoomController;
import com.topcoder.client.mpsqasApplet.model.WebServiceRoomModel;
import com.topcoder.client.mpsqasApplet.view.WebServiceRoomView;
import com.topcoder.client.mpsqasApplet.controller.component.*;
import com.topcoder.client.mpsqasApplet.model.component.*;
import com.topcoder.client.mpsqasApplet.view.component.*;
import com.topcoder.client.mpsqasApplet.common.UpdateTypes;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Default implementation of WebServiceRoomController.
 *
 * @author mitalub
 */
public class WebServiceRoomControllerImpl
        implements WebServiceRoomController {

    private WebServiceRoomModel model;
    private WebServiceRoomView view;

    public void init() {
        model = MainObjectFactory.getWebServiceRoomModel();
        view = MainObjectFactory.getWebServiceRoomView();
    }

    public void placeOnHold() {
        view.removeAllComponents();
    }

    public void takeOffHold() {
        ArrayList classes = (ArrayList)
                model.getWebServiceInformation().getHelperClasses().clone();
        if (!model.getWebServiceInformation().getInterfaceClass().equals("")) {
            classes.add(model.getWebServiceInformation().getInterfaceClass());
        }
        if (!model.getWebServiceInformation().getImplementationClass().equals("")) {
            classes.add(model.getWebServiceInformation().getImplementationClass());
        }
        Object[] component;

        component = ComponentObjectFactory.createJavaDocPanel();
        ((JavaDocPanelModel) component[1]).setPreviewHTML("");
        ((JavaDocPanelModel) component[1]).notifyWatchers();
        view.addComponent((ComponentView) component[2]);

        for (int i = 0; i < classes.size(); i++) {
            component = ComponentObjectFactory.createClassEditorPanel();

            //store name and text
            ((ClassEditorPanelModel) component[1]).setName((String) classes.get(i)
                    + ".java");
            ((ClassEditorPanelModel) component[1]).setText(
                    model.getWebServiceInformation().getSource(
                            (String) classes.get(i) + ".java"));
            ((ClassEditorPanelModel) component[1]).setIsEditable(
                    model.isEditable());
            ((ClassEditorPanelModel) component[1]).notifyWatchers();

            //add to view
            view.addComponent((ComponentView) component[2]);

            //store view in model
            model.getClassViews().put((String) classes.get(i), component[2]);
        }
    }

    /**
     * Sends a request to the server to deploy the web service
     */
    public void processDeploy() {
        if (model.getWebServiceInformation().getImplementationClass().equals("") ||
                model.getWebServiceInformation().getInterfaceClass().equals("")) {
            MainObjectFactory.getIStatusMessageRequestProcessor().addMessage(
                    "The web service must have an interface and implementation file.",
                    true);
        } else {
            //First, update the source in the WebServiceInformation object
            ArrayList classes = new ArrayList();
            classes.add(model.getWebServiceInformation().getImplementationClass());
            classes.add(model.getWebServiceInformation().getInterfaceClass());
            classes.addAll(model.getWebServiceInformation().getHelperClasses());

            for (int i = 0; i < classes.size(); i++) {
                model.getWebServiceInformation().setSource(
                        (String) classes.get(i) + ".java",
                        ((ClassEditorPanelView) model.getClassViews().get(
                                classes.get(i))).getText());
            }

            MainObjectFactory.getIStatusMessageRequestProcessor().addMessage(
                    "Building and deploying Web Service...", false);
            MainObjectFactory.getWebServiceRequestProcessor().deployWebService(
                    model.getWebServiceInformation());
        }
    }

    public void processTest() {
    }

    /**
     * Adds the selected helper class to the problem.
     */
    public void processAddClass() {
        String name = view.getClassName().trim();
        if (model.getWebServiceInformation().getHelperClasses().contains(name) ||
                model.getWebServiceInformation().getInterfaceClass().equals(name) ||
                model.getWebServiceInformation().getImplementationClass().equals(name)) {
            MainObjectFactory.getIStatusMessageRequestProcessor().addMessage(
                    "A class with this name already exists.", true);
        } else if (!name.equals("")) {
            model.getWebServiceInformation().getHelperClasses().add(name);
            model.getWebServiceInformation().setSource(name + ".java", "");
            model.notifyWatchers(UpdateTypes.CLASS_LIST);
            view.clearClass();

            Object[] component = ComponentObjectFactory.createClassEditorPanel();
            ((ClassEditorPanelModel) component[1]).setName(name + ".java");
            ((ClassEditorPanelModel) component[1]).setText("");
            ((ClassEditorPanelModel) component[1]).setIsEditable(
                    model.isEditable());
            ((ClassEditorPanelModel) component[1]).notifyWatchers();

            view.addComponent((ComponentView) component[2]);

            model.getClassViews().put(name, component[2]);
        }
    }

    /**
     * Removes the selected helper class from the problem.
     */
    public void processRemoveClass() {
        int index = view.getSelectedClassIndex();
        if (index != -1) {
            String name = (String) model.getWebServiceInformation().getHelperClasses()
                    .remove(index);

            view.removeComponent((ComponentView) model.getClassViews().remove(name));

            model.notifyWatchers(UpdateTypes.CLASS_LIST);
        }
    }

    /**
     * Called when the user may have potentially changed the name of the
     * interface class of the web service.
     * Makes sure the webserviceinformation object is up to date with
     * the applet, and that there is a panel for the user to enter the
     * source in.
     */
    public void processInterfaceChanged() {
        String newClass = view.getInterface();
        String oldClass = model.getWebServiceInformation().getInterfaceClass();
        if (!newClass.equals(oldClass)) {
            model.getWebServiceInformation().setInterfaceClass(newClass);

            //oldCode will hold the source of oldClass and put it in the
            //newly created source panel
            String oldCode = "";

            //remove the old interface file panel, if this file is not also
            //a helper class or implementation file
            if (!oldClass.equals("")
                    && !model.getWebServiceInformation().getImplementationClass().equals(
                            oldClass)
                    && !model.getWebServiceInformation().getHelperClasses().contains(
                            oldClass)) {
                oldCode = ((ClassEditorPanelView) model.getClassViews().get(oldClass))
                        .getText();
                view.removeComponent((ComponentView) model.getClassViews()
                        .remove(oldClass));
            }

            //if this interface class used to be a helper class, remove it from
            //helper class list
            if (model.getWebServiceInformation().getHelperClasses().contains(newClass)) {
                model.getWebServiceInformation().getHelperClasses().remove(
                        model.getWebServiceInformation().getHelperClasses().indexOf(
                                newClass));
                model.notifyWatchers(UpdateTypes.CLASS_LIST);
            }

            //add a Class Editor Panel for the class if one doesn't already exist
            if (!newClass.equals("")
                    && !model.getClassViews().containsKey(newClass)) {
                model.getWebServiceInformation().setSource(newClass + ".java", "");
                Object[] component = ComponentObjectFactory.createClassEditorPanel();
                ((ClassEditorPanelModel) component[1]).setName(newClass + ".java");
                ((ClassEditorPanelModel) component[1]).setText(oldCode);
                ((ClassEditorPanelModel) component[1]).setIsEditable(
                        model.isEditable());
                ((ClassEditorPanelModel) component[1]).notifyWatchers();
                view.addComponent((ComponentView) component[2]);
                model.getClassViews().put(newClass, component[2]);
            }
        }
    }

    /**
     * Called when the user may have potentially changed the name of the
     * implementation class of the web service.
     * Makes sure the webserviceinformation object is up to date with
     * the applet, and that there is a panel for the user to enter the
     * source in.
     */
    public void processImplementationChanged() {
        String newClass = view.getImplementation();
        String oldClass = model.getWebServiceInformation().getImplementationClass();
        if (!newClass.equals(oldClass)) {
            model.getWebServiceInformation().setImplementationClass(newClass);

            //oldCode will hold the source of oldClass and put it in the
            //newly created source panel
            String oldCode = "";

            //remove the old implementation file panel, if this file is not also
            //a helper class or interface file
            if (!oldClass.equals("")
                    && !model.getWebServiceInformation().getInterfaceClass().equals(
                            oldClass)
                    && !model.getWebServiceInformation().getHelperClasses().contains(
                            oldClass)) {
                oldCode = ((ClassEditorPanelView) model.getClassViews().get(oldClass))
                        .getText();
                view.removeComponent((ComponentView) model.getClassViews()
                        .remove(oldClass));
            }

            //if this implementation class used to be a helper class, remove it from
            //helper class list
            if (model.getWebServiceInformation().getHelperClasses().contains(newClass)) {
                model.getWebServiceInformation().getHelperClasses().remove(
                        model.getWebServiceInformation().getHelperClasses().indexOf(
                                newClass));
                model.notifyWatchers(UpdateTypes.CLASS_LIST);
            }

            //add a Class Editor Panel for the class if one doesn't already exist
            if (!newClass.equals("")
                    && !model.getClassViews().containsKey(newClass)) {
                model.getWebServiceInformation().setSource(newClass + ".java", "");
                Object[] component = ComponentObjectFactory.createClassEditorPanel();
                ((ClassEditorPanelModel) component[1]).setName(newClass + ".java");
                ((ClassEditorPanelModel) component[1]).setText(oldCode);
                ((ClassEditorPanelModel) component[1]).setIsEditable(
                        model.isEditable());
                ((ClassEditorPanelModel) component[1]).notifyWatchers();
                view.addComponent((ComponentView) component[2]);
                model.getClassViews().put(newClass, component[2]);
            }
        }
    }
}
