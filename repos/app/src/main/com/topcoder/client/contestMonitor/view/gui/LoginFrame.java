package com.topcoder.client.contestMonitor.view.gui;

import com.topcoder.client.contestMonitor.model.MonitorController;
import com.topcoder.client.contestMonitor.model.ResponseWaiter;
import com.topcoder.client.contestMonitor.model.WrappedResponseWaiter;
import com.topcoder.server.AdminListener.response.LoginResponse;
import org.apache.log4j.Logger;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.UIManager;
import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.GridLayout;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Modifications for AdminTool 2.0 are :
 * processLoginResponse(LoginResponse) method modified to set TCSubject 
 * associated with created MonitorFrame
 *
 * @author TCDEVELOPER
 */
public class LoginFrame extends JFrame {

    private static final Logger log = Logger.getLogger(LoginFrame.class);
    private static final int LOGIN_TIMEOUT_MS = 20 * 1000;

    private final Object lock = new Object();

    private JLabel statusMsg;
    private JTextField userField;
    private JPasswordField pwField;
    private JButton loginButton;
    private int tries = 0;
    private MonitorController controller;

    private MonitorFrame monitorFrame;
    private Timer responseTimer = new Timer();
    private TimerTask responseTimeoutTask;

    private ResponseWaiter waiter = new WrappedResponseWaiter(new FrameWaiter(this)) {
        protected void _waitForResponse() {
            userField.setEditable(false);
            pwField.setEditable(false);
            loginButton.setEnabled(false);
            statusMsg.setText("Sending login request...");
        }

        protected void _errorResponseReceived(Throwable t) {
            statusMsg.setText("Login failed.");
            userField.setEditable(true);
            pwField.setEditable(true);
            loginButton.setEnabled(true);
        }

        protected void _responseReceived() {
            statusMsg.setText("Enter login information.");
            userField.setEditable(true);
            pwField.setEditable(true);
            loginButton.setEnabled(true);
        }
    };

    public LoginFrame(MonitorController controller) {
        super(MonitorGUIConstants.PROGRAM_NAME);
        this.controller = controller;
        setLookAndFeel();
        setResizable(false);

        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                closeAndExit();
            }
        }
        );

        JPanel upper = new JPanel();
        upper.setLayout(new GridLayout(2, 2));

        JLabel userLabel = new JLabel("User ID");
        userField = new JTextField(10);
        userField.addKeyListener(new KeyAdapter() {
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    pwField.requestFocusInWindow();
                }
            }
        });
        JLabel pwLabel = new JLabel("Password");
        pwField = new JPasswordField(10);
        pwField.addKeyListener(new KeyAdapter() {
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    loginButton.doClick();
                }
            }
        });

        upper.add(userLabel);
        upper.add(userField);
        upper.add(pwLabel);
        upper.add(pwField);

        JPanel lower = new JPanel();
        lower.setLayout(new BorderLayout());
        loginButton = new JButton("Login");
        loginButton.addActionListener(new LoginButtonListener());
        lower.add(loginButton, BorderLayout.NORTH);
        statusMsg = new JLabel("Enter login information.");
        lower.add(statusMsg);

        Container cp = getContentPane();
        cp.add(upper, BorderLayout.NORTH);
        cp.add(lower, BorderLayout.SOUTH);
        pack();

        Point p = controller.getLoginLocation();
        setLocation((int) p.getX(), (int) p.getY());
        setForeground(getBackground());
        setVisible(true);
    }

    /**
     * This method was updated for AdminTool 2.0 to store the
     * TCSubject returned in the login response into the MonitorFrame
     * 
     * @param response the login response
     * @see MonitorFrame#setTCSubject()
     */
    public void processLoginResponse(LoginResponse response) {
        synchronized (lock) {
            if (responseTimeoutTask != null) {
                boolean alreadyTimedOut = !responseTimeoutTask.cancel();
                responseTimeoutTask = null;
                if (alreadyTimedOut) {
                    return;
                }
            }

            if (response.getSucceeded()) {
                statusMsg.setText("Login succeeded...");
                // Elements in the login response determine which menu items are enabled in the monitor window
                if (monitorFrame == null) {
                    try {
                        // dpecora - MonitorFrame construction should go inside try block
                        monitorFrame = new MonitorFrame(controller, response.getAllowedFunctions(),response.getUserId());
                        controller.setMonitorWindow(monitorFrame);
                    } catch (Exception e) {
                        log.error("Error starting up monitor window", e);
                        closeAndExit();
                    }
                }
                setVisible(false);
                tries = 0;
                waiter.responseReceived();
                return;
            }
            // Login failed.
            waiter.errorResponseReceived(new Exception("Login failed"));
            if (tries > 2) {
                statusMsg.setText("Login failed; application closing.");
                loginButton.setText("Close");
                loginButton.setEnabled(true);
                return;
            }
        }
    }

    // Moved from monitor frame code
    private void setLookAndFeel() {
        String lookAndFeel = controller.getLookAndFeel().toLowerCase();
        String lookAndFeelClassName = null;
        if (lookAndFeel.equals(MonitorController.SYSTEM_LF)) {
            lookAndFeelClassName = UIManager.getSystemLookAndFeelClassName();
        } else {
            UIManager.LookAndFeelInfo[] infoArray = UIManager.getInstalledLookAndFeels();
            for (int i = 0; i < infoArray.length; i++) {
                UIManager.LookAndFeelInfo info = infoArray[i];
                if (info.getName().toLowerCase().indexOf(lookAndFeel) != -1) {
                    lookAndFeelClassName = info.getClassName();
                    break;
                }
            }
            if (lookAndFeelClassName == null) {
                lookAndFeelClassName = UIManager.getCrossPlatformLookAndFeelClassName();
            }
        }
        try {
            UIManager.setLookAndFeel(lookAndFeelClassName);
        } catch (Exception e) {
            log.error("Error setting look and feel", e);
        }
    }

    private void closeAndExit() {
        LoginFrame.this.dispose();
        System.exit(0);
    }

    private class LoginButtonListener implements ActionListener {

        public synchronized void actionPerformed(ActionEvent e) {
            if (tries > 2) {
                closeAndExit();
            }
            if (responseTimeoutTask != null) {
                throw new IllegalStateException("Already waiting for response!");
            }
            responseTimeoutTask = new TimerTask() {
                public void run() {
                    waiter.errorResponseReceived(new Exception("Login attempt timed out"));
                    responseTimeoutTask = null;
                }
            };
            responseTimer.schedule(responseTimeoutTask, LOGIN_TIMEOUT_MS);
            waiter.waitForResponse();
            tries++;
            char passwordArray[] = pwField.getPassword();
            controller.getCommandSender().sendLoginRequest(userField.getText(), passwordArray);
            pwField.setText("");
        }
    }
}

