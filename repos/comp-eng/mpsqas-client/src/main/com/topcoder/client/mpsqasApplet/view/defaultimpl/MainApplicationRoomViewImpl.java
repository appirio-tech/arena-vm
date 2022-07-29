package com.topcoder.client.mpsqasApplet.view.defaultimpl;

import com.topcoder.client.mpsqasApplet.controller.MainApplicationRoomController;
import com.topcoder.client.mpsqasApplet.model.MainApplicationRoomModel;
import com.topcoder.client.mpsqasApplet.object.MainObjectFactory;
import com.topcoder.client.mpsqasApplet.view.MainApplicationRoomView;
import com.topcoder.client.mpsqasApplet.view.JPanelView;
import com.topcoder.client.mpsqasApplet.view.defaultimpl.listener.AppletActionListener;
import com.topcoder.client.mpsqasApplet.view.defaultimpl.listener.AppletMouseListener;
import com.topcoder.client.mpsqasApplet.view.defaultimpl.sortabletable.SortableTable;
import com.topcoder.netCommon.mpsqas.ApplicationInformation;

import java.awt.*;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import javax.swing.*;

/**
 * Default implementation of Main Application Room View, a room in which a list
 * of applications is shown and the user can choose an application to view.
 *
 * @author mitalub
 */
public class MainApplicationRoomViewImpl extends JPanelView
        implements MainApplicationRoomView {

    private final static String[] MAIN_APPLICATION_COLS =
            {"Handle", "Type", "Rating", "Events"};

    private final static int[] MAIN_APPLICATION_COLS_WIDTHS =
            {500, 500, 300, 100};

    public void init() {
        model = MainObjectFactory.getMainApplicationRoomModel();
        controller = MainObjectFactory.getMainApplicationRoomController();
        layout = new GridBagLayout();
        gbc = new GridBagConstraints();
        setLayout(layout);
        model.addWatcher(this);
    }

    public void update(Object arg) {
        if (arg == null) {
            removeAll();


            ArrayList apps = model.getApplications();


            titleLabel = new JLabel("Pending Applications (" +
                    apps.size() + "):");
            titleLabel.setFont(DefaultUIValues.HEADER_FONT);
            gbc.anchor = 17;
            gbc.fill = 1;
            gbc.insets = new Insets(5, 5, 5, 5);
            GUIConstants.buildConstraints(gbc, 0, 0, 1, 1, 1, 1);
            layout.setConstraints(titleLabel, gbc);
            add(titleLabel);
            appTable = new SortableTable(MAIN_APPLICATION_COLS,
                    getApplicationsTable(),
                    MAIN_APPLICATION_COLS_WIDTHS);
            appTablePane = new JScrollPane(appTable, 22, 31);
            GUIConstants.buildConstraints(gbc, 0, 1, 1, 1, 0, 100);
            layout.setConstraints(appTablePane, gbc);
            add(appTablePane);
            viewButton = new JButton("View Application");
            gbc.fill = 0;
            gbc.anchor = 13;
            GUIConstants.buildConstraints(gbc, 0, 2, 1, 1, 0, 1);
            layout.setConstraints(viewButton, gbc);
            add(viewButton);
            appTable.addMouseListener(new AppletMouseListener("appRowClicked",
                    this, "mouseClicked"));
            viewButton.addActionListener(new AppletActionListener(
                    "processViewApplication", controller, false));
        }
    }

    public void appRowClicked(MouseEvent mouseevent) {
        if (mouseevent.getClickCount() == 2
                && SwingUtilities.isLeftMouseButton(mouseevent))
            controller.processViewApplication();
    }

    private Object[][] getApplicationsTable() {
        ArrayList apps = model.getApplications();
        Object aobj[][] = new Object[apps.size()][4];
        for (int i = 0; i < apps.size(); i++) {
            ApplicationInformation info = (ApplicationInformation) apps.get(i);
            aobj[i][0] = info.getHandle();
            aobj[i][1] = info.getApplicationType();
            aobj[i][2] = new Integer(info.getRating());
            aobj[i][3] = new Integer(info.getEvents());
        }
        return aobj;
    }

    public int getSelectedApplicationIndex() {
        return appTable.getSelectedRow();
    }

    private MainApplicationRoomModel model;
    private MainApplicationRoomController controller;
    private GridBagLayout layout;
    private GridBagConstraints gbc;
    private JLabel titleLabel;
    private SortableTable appTable;
    private JScrollPane appTablePane;
    private JButton viewButton;
}
