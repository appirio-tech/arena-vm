package com.topcoder.client.mpsqasApplet.view.defaultimpl;

import com.topcoder.client.mpsqasApplet.view.JPanelView;
import com.topcoder.client.mpsqasApplet.controller.MainUserRoomController;
import com.topcoder.client.mpsqasApplet.model.MainUserRoomModel;
import com.topcoder.client.mpsqasApplet.object.MainObjectFactory;
import com.topcoder.client.mpsqasApplet.view.MainUserRoomView;
import com.topcoder.client.mpsqasApplet.view.defaultimpl.listener.AppletActionListener;
import com.topcoder.client.mpsqasApplet.view.defaultimpl.listener.AppletMouseListener;
import com.topcoder.client.mpsqasApplet.view.defaultimpl.sortabletable.SortableTable;
import com.topcoder.netCommon.mpsqas.UserInformation;

import java.awt.*;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import javax.swing.*;

public class MainUserRoomViewImpl extends JPanelView
        implements MainUserRoomView {

    private final static String[] MAIN_USER_COLS = {"Handle", "Name", "Paid",
                                                    "Pending", "Pay"};

    private final static int[] MAIN_USER_COLS_WIDTHS = {100, 100, 70, 70, 50};

    private final static boolean[] MAIN_USER_COLS_EDIT = {false, false, false,
                                                          false, true};

    private MainUserRoomController controller;
    private MainUserRoomModel model;
    private GridBagConstraints gbc;
    private GridBagLayout layout;
    private JLabel title;
    private SortableTable dataT;
    private JScrollPane dataSP;
    private JButton payButton;
    private JButton userInfoButton;
    private Box buttonBox;

    public void init() {
        model = MainObjectFactory.getMainUserRoomModel();
        controller = MainObjectFactory.getMainUserRoomController();
        layout = new GridBagLayout();
        gbc = new GridBagConstraints();
        setLayout(layout);
        model.addWatcher(this);
    }

    public void update(Object obj) {
        removeAll();
        ArrayList users = model.getUsers();
        title = new JLabel("Users (" + users.size() + "):");
        title.setFont(DefaultUIValues.HEADER_FONT);
        GUIConstants.buildConstraints(gbc, 0, 0, 1, 1, 1, 1);
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = 17;
        layout.setConstraints(title, gbc);
        add(title);
        Object aobj[][] = new Object[users.size()][5];
        for (int i = 0; i < users.size(); i++) {
            UserInformation userinformation = (UserInformation) users.get(i);
            aobj[i][0] = userinformation.getHandle();
            aobj[i][1] = userinformation.getFirstName() + " " +
                    userinformation.getLastName();
            aobj[i][2] = new Double(userinformation.getPaid());
            aobj[i][3] = new Double(userinformation.getPending());
            aobj[i][4] = new Boolean(false);
        }

        dataT = new SortableTable(MAIN_USER_COLS,
                aobj, MAIN_USER_COLS_WIDTHS,
                MAIN_USER_COLS_EDIT);
        dataSP = new JScrollPane(dataT, 22, 31);
        GUIConstants.buildConstraints(gbc, 0, 1, 1, 1, 0, 100);
        gbc.fill = 1;
        layout.setConstraints(dataSP, gbc);
        add(dataSP);
        buttonBox = Box.createHorizontalBox();
        userInfoButton = new JButton("Details");
        payButton = new JButton("Pay");
        buttonBox.add(userInfoButton);
        buttonBox.add(Box.createHorizontalStrut(5));
        buttonBox.add(payButton);
        gbc.fill = 0;
        gbc.anchor = 13;
        GUIConstants.buildConstraints(gbc, 0, 2, 1, 1, 0, 1);
        layout.setConstraints(buttonBox, gbc);
        add(buttonBox);
        payButton.addActionListener(new AppletActionListener("processPay",
                controller, false));
        userInfoButton.addActionListener(new AppletActionListener(
                "processViewUser", controller, false));
        dataT.addMouseListener(new AppletMouseListener("getUserInfoMouse", this,
                "mouseClicked"));
        repaint();
    }

    public void getUserInfoMouse(MouseEvent mouseevent) {
        if (mouseevent.getClickCount() == 2
                && SwingUtilities.isLeftMouseButton(mouseevent))
            controller.processViewUser();
    }

    public int getSelectedUserIndex() {
        return dataT.getSelectedRow();
    }

    public boolean isPaid(int i) {
        return ((Boolean) dataT.getAbsoluteValueAt(i, 4)).booleanValue();
    }
}
