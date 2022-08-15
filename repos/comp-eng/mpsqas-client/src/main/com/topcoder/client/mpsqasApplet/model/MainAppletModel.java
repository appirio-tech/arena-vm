package com.topcoder.client.mpsqasApplet.model;

import com.topcoder.client.mpsqasApplet.view.View;
import com.topcoder.client.mpsqasApplet.controller.Controller;

import java.awt.Dimension;
import java.awt.Rectangle;

/**
 * An interface for the Main Applet Model.
 *
 * @author mitalub
 */
public abstract class MainAppletModel extends Model {

    public abstract boolean isAdmin();

    public abstract void setIsAdmin(boolean isAdmin);

    public abstract boolean isWriter();

    public abstract void setIsWriter(boolean isWriter);

    public abstract boolean isTester();

    public abstract void setIsTester(boolean isTester);

    public abstract boolean hasExtras();

    public abstract void setHasExtras(boolean hasExtras);

    public abstract boolean isStatusPoppedUp();

    public abstract void setIsStatusPoppedUp(boolean isStatusPoppedUp);

    public abstract Dimension getWinSize();

    public abstract void setWinSize(Dimension winSize);

    public abstract Rectangle getBounds();

    public abstract void setBounds(Rectangle bounds);

    public abstract Controller getCurrentRoomController();

    public abstract void setCurrentRoomController(Controller controller);

    public abstract View getCurrentRoomView();

    public abstract void setCurrentRoomView(View view);

    public abstract Model getCurrentRoomModel();

    public abstract void setCurrentRoomModel(Model model);
}
