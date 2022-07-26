package com.topcoder.client.mpsqasApplet.view.defaultimpl;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.MouseEvent;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenuBar;
import javax.swing.JPanel;
import javax.swing.JSplitPane;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.WindowConstants;

import com.topcoder.client.mpsqasApplet.controller.MainAppletController;
import com.topcoder.client.mpsqasApplet.model.MainAppletModel;
import com.topcoder.client.mpsqasApplet.object.MainObjectFactory;
import com.topcoder.client.mpsqasApplet.view.JFrameView;
import com.topcoder.client.mpsqasApplet.view.MainAppletView;
import com.topcoder.client.mpsqasApplet.view.defaultimpl.listener.AppletActionListener;
import com.topcoder.client.mpsqasApplet.view.defaultimpl.listener.AppletComponentListener;
import com.topcoder.client.mpsqasApplet.view.defaultimpl.listener.AppletMouseListener;
import com.topcoder.client.mpsqasApplet.view.defaultimpl.listener.AppletWindowListener;

/**
 * Implementation of the Main Applet View.  Holds the room views, and
 * controls location of status window.
 *
 * @author mitalub
 */
public class MainAppletViewImpl extends JFrameView implements MainAppletView {

    private JMenuBar toolBar;
    private JButton backButton;
    private JButton reloadButton;
    private JButton forwardButton;
    private JButton clearStatusButton;
    private JButton goButton;
    private JTextField jumpField;

    private Component mainComponent;
    private Component thisRoom;
    private JSplitPane splitPane;
    private JPanel topPanel;
    private JPanel bottomPanel;
    private JFrame statusPopUp;
    private AppletWindowListener popUpListener1;
    private AppletWindowListener popUpListener2;

    private boolean splitHasResized;

    private MainAppletModel model;
    private MainAppletController controller;

    /**
     * Create the main applet frame.
     */
    public void init() {
        DefaultUIValues.set();
        SwingUtilities.updateComponentTreeUI(this);

        //store stuff
        model = MainObjectFactory.getMainAppletModel();
        controller = MainObjectFactory.getMainAppletController();

        //initiate window
        setTitle(GUIConstants.MAIN_WINDOW_TITLE);
        setBounds(0, 0, 800, 600);
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        //listen to window events
        addComponentListener(new AppletComponentListener("componentMoved",
                "processWindowLocationChange", controller, false));
        addComponentListener(new AppletComponentListener("componentResized",
                "processWindowLocationChange", controller, false));
        addWindowListener(
                new AppletWindowListener("windowClosing", "close", controller, false));

        //the main split pane to hold status box and room
        splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
        splitPane.setBorder(null);
        topPanel = new JPanel();
        bottomPanel = new JPanel();

        splitPane.setTopComponent(topPanel);
        splitPane.setBottomComponent(bottomPanel);

        getContentPane().setLayout(new BorderLayout());
        topPanel.setLayout(new BorderLayout());
        bottomPanel.setLayout(new BorderLayout());
        bottomPanel.add((Component) MainObjectFactory.getStatusView(),
                BorderLayout.CENTER);

        //create menu and toolbar
        backButton = new JButton("Back");
        reloadButton = new JButton("Reload");
        forwardButton = new JButton("Forward");
        jumpField = new JTextField(15);
        goButton = new JButton("Go");
        clearStatusButton = new JButton("Clear Status");

        toolBar = new JMenuBar();
        GridBagLayout toolBarLayout = new GridBagLayout();
        GridBagConstraints gbc = new GridBagConstraints();
        toolBar.setLayout(toolBarLayout);

        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.NONE;
        gbc.anchor = GridBagConstraints.WEST;

        GUIConstants.buildConstraints(gbc, 0, 0, 1, 1, 1, 1);
        toolBarLayout.setConstraints(backButton, gbc);
        toolBar.add(backButton);

        GUIConstants.buildConstraints(gbc, 1, 0, 1, 1, 1, 0);
        toolBarLayout.setConstraints(reloadButton, gbc);
        toolBar.add(reloadButton);

        GUIConstants.buildConstraints(gbc, 2, 0, 1, 1, 1, 0);
        toolBarLayout.setConstraints(forwardButton, gbc);
        toolBar.add(forwardButton);

        GUIConstants.buildConstraints(gbc, 3, 0, 1, 1, 1, 0);
        toolBarLayout.setConstraints(clearStatusButton, gbc);
        toolBar.add(clearStatusButton);
        
        JLabel spacer = new JLabel();
        GUIConstants.buildConstraints(gbc, 4, 0, 1, 1, 100, 0);
        toolBarLayout.setConstraints(spacer, gbc);
        toolBar.add(spacer);

        GUIConstants.buildConstraints(gbc, 5, 0, 1, 1, 1, 0);
        toolBarLayout.setConstraints(jumpField, gbc);
        toolBar.add(jumpField);

        GUIConstants.buildConstraints(gbc, 6, 0, 1, 1, 1, 0);
        toolBarLayout.setConstraints(goButton, gbc);
        toolBar.add(goButton);

        //add listeners to buttons
        backButton.addActionListener(new AppletActionListener(
                "goBack", this));
        reloadButton.addActionListener(new AppletActionListener(
                "reload", this));
        forwardButton.addActionListener(new AppletActionListener(
                "goForward", this));
        jumpField.addActionListener(new AppletActionListener(
                "jump", this));
        goButton.addActionListener(new AppletActionListener(
                "jump", this));
        clearStatusButton.addActionListener(new AppletActionListener(
                "clearStatus", this));

        //make the pop up window for the status, but keep it hidden
        statusPopUp = new JFrame();
        statusPopUp.setBounds(getWinSize().width - 300, 0, 300, 300);
        statusPopUp.getContentPane().setLayout(new BorderLayout());
        statusPopUp.setTitle("Status Messages");
        statusPopUp.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
        popUpListener2 = new AppletWindowListener("windowClosing",
                "reverseStatusWindow", controller, false);
        statusPopUp.addWindowListener(popUpListener1);
        statusPopUp.addWindowListener(popUpListener2);
        statusPopUp.setVisible(false);
        ((StatusViewImpl) MainObjectFactory.getStatusView()).addMouseListenerToAll(
                new AppletMouseListener("statusBoxClicked", this, "mouseClicked"));

        setVisible(true);
        //The close operation is handled by the "windowClosingEvent"
        setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);

        model.addWatcher(this);
    }

    /**
     * Called when user clicks the go back button.
     */
    public void goBack(ActionEvent e) {
        controller.doRelativeMove(-1);
    }

    /**
     * Called when user clicks the reload button.
     */
    public void reload(ActionEvent e) {
        controller.doRelativeMove(0);
    }

    /**
     * Called when user clicks the forward button.
     */
    public void goForward(ActionEvent e) {
        controller.doRelativeMove(1);
    }

    /**
     * Called when the go button or enter is pressed
     */
    public void jump(ActionEvent e) {
        controller.jump(jumpField.getText());
    }

    /**
     * When the status box is double clicked, it switches locations.
     */
    public void statusBoxClicked(MouseEvent e) {
        if (e.getClickCount() == 2 && SwingUtilities.isLeftMouseButton(e)) {
            controller.reverseStatusWindow();
        }
    }

    /**
     * Called when the clear status button is clicked. Clears the status messages window
     */
    public void clearStatus(ActionEvent e) {
        controller.clearStatus();
    }
    
    /**
     * Redraws everything using the info in the model.
     */
    public void update(Object arg) {
        getContentPane().removeAll();

        if (thisRoom != null) {
            topPanel.remove(thisRoom);
        }

        thisRoom = (Component) model.getCurrentRoomView();

        topPanel.add(thisRoom, BorderLayout.CENTER);

        if (model.hasExtras()) {
            setJMenuBar((JMenuBar) MainObjectFactory.getMenuView());
            getContentPane().add(toolBar, BorderLayout.NORTH);
            if (model.isStatusPoppedUp()) {
                mainComponent = thisRoom;
            } else {
                mainComponent = splitPane;
            }
        } else {
            setJMenuBar(null);
            mainComponent = thisRoom;
        }

        if (model.isStatusPoppedUp()) {
            splitPane.remove((JPanel) MainObjectFactory.getStatusView());
            statusPopUp.getContentPane().add((JPanel)
                    MainObjectFactory.getStatusView(), BorderLayout.CENTER);
            statusPopUp.setVisible(true);
        } else {
            statusPopUp.getContentPane().remove(
                    (JPanel) MainObjectFactory.getStatusView());
            bottomPanel.add(
                    (JPanel) MainObjectFactory.getStatusView(), BorderLayout.CENTER);
            statusPopUp.setVisible(false);
        }

        getContentPane().add(mainComponent, BorderLayout.CENTER);


        if (!splitHasResized && model.hasExtras()) {
            splitHasResized = true;
            splitPane.setDividerLocation(400);
        }
        setVisible(true);
    }

    /**
     * Hides the window.
     */
    public void close() {
        statusPopUp.setVisible(false);
        statusPopUp.dispose();
        setVisible(false);
        dispose();
    }

    /**
     * Returns the window size.
     */
    public Dimension getWinSize() {
        Toolkit winInfo = getToolkit();
        return winInfo.getScreenSize();
    }
}

