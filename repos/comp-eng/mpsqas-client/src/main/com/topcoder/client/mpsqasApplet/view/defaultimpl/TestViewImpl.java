package com.topcoder.client.mpsqasApplet.view.defaultimpl;

import com.topcoder.client.mpsqasApplet.common.UpdateTypes;
import com.topcoder.client.mpsqasApplet.view.TestView;
import com.topcoder.client.mpsqasApplet.model.TestModel;
import com.topcoder.client.mpsqasApplet.controller.TestController;
import com.topcoder.client.mpsqasApplet.view.JFrameView;
import com.topcoder.client.mpsqasApplet.object.MainObjectFactory;
import com.topcoder.client.mpsqasApplet.view.defaultimpl.widget.PanelTextField;
import com.topcoder.client.mpsqasApplet.view.defaultimpl.listener.AppletActionListener;
import com.topcoder.client.mpsqasApplet.view.defaultimpl.listener.AppletWindowListener;
import com.topcoder.shared.problem.DataType;

import javax.swing.*;
import java.awt.*;

/**
 * Default implementation of TestView, a JFrame which pops up and
 * presents the user with fields in which to enter data values for
 * the data types.
 */
public class TestViewImpl extends JFrameView implements TestView {

    private TestModel model;
    private TestController controller;

    private PanelTextField[] args;
    private Container panel;
    private GridBagLayout layout;
    private GridBagConstraints gbc;
    private JButton testButton;

    /**
     * Stores the model and controller and sets up the layout.
     */
    public void init() {
        model = MainObjectFactory.getTestModel();
        controller = MainObjectFactory.getTestController();
        model.addWatcher(this);

        setTitle("Argument Entry");

        panel = getContentPane();
        layout = new GridBagLayout();
        gbc = new GridBagConstraints();

        panel.setLayout(layout);
        
        setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
        addWindowListener(
                new AppletWindowListener("windowClosing", "close", controller, false));
    }

    /**
     * Updates the visibility of the JFrame and lays out the JFrame giving
     * each parameter a label and field.
     */
    public void update(Object arg) {
        if (arg == null) {
            DataType[] dataTypes = model.getDataTypes();
            args = new PanelTextField[dataTypes.length];
            JLabel label;

            panel.removeAll();

            if (model.isVisible()) {
                gbc.insets = new Insets(5, 5, 5, 5);
                gbc.anchor = GridBagConstraints.WEST;
                gbc.fill = GridBagConstraints.HORIZONTAL;

                int y = 0;
                for (int i = 0; i < dataTypes.length; i++) {
                    label = new JLabel(dataTypes[i].getDescription() + ":");
                    GUIConstants.buildConstraints(gbc, 0, y, 1, 1, 1, 1);
                    layout.setConstraints(label, gbc);
                    panel.add(label);

                    args[i] = new PanelTextField(GUIConstants.getTextFieldWidth(
                            dataTypes[i]));
                    GUIConstants.buildConstraints(gbc, 1, y, 1, 1, 100, 1);
                    layout.setConstraints(args[i], gbc);
                    panel.add(args[i]);

                    y++;
                }

                JLabel spacer = new JLabel();
                GUIConstants.buildConstraints(gbc, 0, y++, 2, 1, 0, 100);
                layout.setConstraints(spacer, gbc);
                panel.add(spacer);

                testButton = new JButton("Test");
                testButton.addActionListener(new AppletActionListener(
                        "processTest", controller, false));
                gbc.fill = GridBagConstraints.NONE;
                gbc.anchor = GridBagConstraints.CENTER;
                GUIConstants.buildConstraints(gbc, 0, y++, 2, 1, 0, 1);
                layout.setConstraints(testButton, gbc);
                panel.add(testButton);
            }
            updateVisible();
        } else if (arg.equals(UpdateTypes.VISIBILITY)) {
            updateVisible();
        }
    }

    private void updateBounds() {
        Rectangle bounds = MainObjectFactory.getMainAppletModel().getBounds();
        Dimension winSize = MainObjectFactory.getMainAppletModel().getWinSize();
        setBounds((int) (bounds.x + bounds.width / 2 - winSize.getWidth() / 6),
                (int) (bounds.y + bounds.height / 2 - winSize.getHeight() / 6),
                (int) (winSize.getWidth() / 3),
                (int) (winSize.getHeight() / 3));
    }

    private void updateVisible() {
        if (!model.isVisible()) {
            dispose();
        } else {
            updateBounds();
            setVisible(model.isVisible());
        }
    }

    /**
     * Returns a String[] of the arguments entered by the user.
     */
    public String[] getArgs() {
        String[] argStrings = new String[args.length];
        for (int i = 0; i < args.length; i++) {
            argStrings[i] = args[i].getText();
        }
        return argStrings;
    }
}
