package com.topcoder.client.mpsqasApplet.model.defaultimpl;

import com.topcoder.client.mpsqasApplet.model.MainAppletModel;
import com.topcoder.client.mpsqasApplet.model.Model;
import com.topcoder.client.mpsqasApplet.controller.Controller;
import com.topcoder.client.mpsqasApplet.view.View;

import java.awt.Dimension;
import java.awt.Rectangle;

/**
 * Implementation of Main Applet model.
 *
 * @author mitalub
 */
public class MainAppletModelImpl extends MainAppletModel {

    private boolean isAdmin;
    private boolean isWriter;
    private boolean isTester;

    private boolean hasExtras;
    private boolean isStatusPoppedUp;
    private Dimension winSize;
    private Rectangle bounds;
    private Controller controller;
    private View view;
    private Model model;


    public void init() {
        isWriter = isTester = isAdmin = false;
        hasExtras = false;
        isStatusPoppedUp = false;
        winSize = null;
        view = null;
        model = null;
        controller = null;
    }

    public void setIsAdmin(boolean isAdmin) {
        this.isAdmin = isAdmin;
    }

    public boolean isAdmin() {
        return isAdmin;
    }

    public void setIsWriter(boolean isWriter) {
        this.isWriter = isWriter;
    }

    public boolean isWriter() {
        return isWriter;
    }

    public void setIsTester(boolean isTester) {
        this.isTester = isTester;
    }

    public boolean isTester() {
        return isTester;
    }

    public void setHasExtras(boolean hasExtras) {
        this.hasExtras = hasExtras;
    }

    public boolean hasExtras() {
        return hasExtras;
    }

    public void setIsStatusPoppedUp(boolean isStatusPoppedUp) {
        this.isStatusPoppedUp = isStatusPoppedUp;
    }

    public boolean isStatusPoppedUp() {
        return isStatusPoppedUp;
    }

    public void setWinSize(Dimension winSize) {
        this.winSize = winSize;
    }

    public Dimension getWinSize() {
        return winSize;
    }

    public void setCurrentRoomController(Controller controller) {
        this.controller = controller;
    }

    public Controller getCurrentRoomController() {
        return controller;
    }

    public void setCurrentRoomView(View view) {
        this.view = view;
    }

    public View getCurrentRoomView() {
        return view;
    }

    public void setCurrentRoomModel(Model model) {
        this.model = model;
    }

    public Model getCurrentRoomModel() {
        return model;
    }

    public Rectangle getBounds() {
        return bounds;
    }

    public void setBounds(Rectangle bounds) {
        this.bounds = bounds;
    }
}
