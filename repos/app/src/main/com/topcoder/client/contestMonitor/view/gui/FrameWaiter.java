/*
 * Author: Michael Cervantes (emcee)
 * Date: Jun 12, 2002
 * Time: 2:08:47 AM
 */
package com.topcoder.client.contestMonitor.view.gui;

import com.topcoder.client.contestMonitor.model.WrappedResponseWaiter;
import org.apache.log4j.Logger;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextPane;
import javax.swing.WindowConstants;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridBagLayout;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public final class FrameWaiter extends WrappedResponseWaiter {

    private static final Logger logger = Logger.getLogger(FrameWaiter.class);

    private Delegate delegate;
    private boolean waitingForResponse = false;
    private JDialog errorDialog;
    private JDialog exceptionDialog;
    private JLabel messageLabel;
    private JTextPane exceptionPane;
    private JButton dismissButton, viewExceptionButton;

    public FrameWaiter(Window frame) {
        if (frame instanceof JFrame) {
            delegate = new JFrameDelegate((JFrame) frame);
        } else if (frame instanceof JDialog) {
            delegate = new JDialogDelegate((JDialog) frame);
        } else {
            throw new IllegalArgumentException("Unsupported window type: " + frame);
        }
        build();
    }

    private void build() {
//        errorDialog = delegate.getChildDialog("Error",false);
//        exceptionDialog = new JDialog(errorDialog,"Exception",false);
        exceptionDialog = delegate.getChildDialog("Error", true);
        JPanel panel = new JPanel(new GridBagLayout());
/*        errorDialog.setContentPane(panel);
        messageLabel = new JLabel("",JLabel.CENTER);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10,5,10,5);
        gbc.weighty = gbc.weightx = .1;
        gbc.gridwidth = 2;
        gbc.gridheight = 1;
        gbc.gridx = gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.fill = GridBagConstraints.NONE;
        panel.add(messageLabel,gbc);
        gbc.insets = new Insets(5,5,5,5);
        gbc.gridwidth = 1;
        viewExceptionButton = new JButton("View Stack Trace");
        viewExceptionButton.setMnemonic('V');
        viewExceptionButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                exceptionDialog.setLocationRelativeTo(errorDialog);
                exceptionDialog.setVisible(true);
            }
        });
        gbc.gridy = 1;
        panel.add(viewExceptionButton,gbc);
        gbc.gridx = 1;
        dismissButton = new JButton("Dismiss");
        dismissButton.setMnemonic('d');
        dismissButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                errorDialog.setVisible(false);
            }
        });
        panel.add(dismissButton,gbc);
*/

        exceptionPane = new JTextPane();
        dismissButton = new JButton("Dismiss");
        dismissButton.setMnemonic('d');
        dismissButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                exceptionDialog.setLocationRelativeTo(errorDialog);
                exceptionDialog.setVisible(false);
            }
        });
        JScrollPane scroller = new JScrollPane(exceptionPane);
        scroller.setPreferredSize(new Dimension(500, 400));
        panel = new JPanel(new BorderLayout());
        exceptionDialog.setContentPane(panel);
        panel.add(scroller, BorderLayout.CENTER);
        panel.add(dismissButton, BorderLayout.SOUTH);
    }


    protected synchronized void _waitForResponse() {
        if (waitingForResponse) {
            throw new IllegalStateException("Already waiting for response!");
        }
        logger.debug("Waiting for response..");
        waitingForResponse = true;
        delegate.startIntercepting();
    }

    protected synchronized void _responseReceived() {
        logger.debug("Received response..");
        if (waitingForResponse) {
            waitingForResponse = false;
            delegate.stopIntercepting();
        }
    }

    protected synchronized void _errorResponseReceived(Throwable t) {
        logger.debug("Received error response..");
        if (waitingForResponse) {
            waitingForResponse = false;
/*            messageLabel.setText(t.getMessage());
            messageLabel.setFont(new Font("",Font.BOLD,13));
            if (t.getStackTrace() != null) {
                viewExceptionButton.setVisible(true);
                StringWriter sw = new StringWriter();
                t.printStackTrace(new PrintWriter(sw));
                exceptionPane.setText(sw.toString());
                exceptionPane.revalidate();
                exceptionPane.repaint();
                exceptionDialog.pack();
            }
            else {
                viewExceptionButton.setVisible(false);
            }
            errorDialog.pack();
            errorDialog.setLocationRelativeTo(null);
            errorDialog.setVisible(true);
*/
            try {
                exceptionPane.setText(t.toString());
                exceptionPane.revalidate();
                exceptionPane.repaint();
                exceptionDialog.pack();
                exceptionDialog.setLocationRelativeTo(null);
                exceptionDialog.setVisible(true);
            } finally {
                delegate.stopIntercepting();
            }
        }
    }

    private interface Delegate {

        void startIntercepting();

        void stopIntercepting();

        JDialog getChildDialog(String name, boolean modal);
    }

    private class JFrameDelegate implements Delegate {

        private JFrame frame;
        private int oldDefaultCloseOperation;

        public JFrameDelegate(JFrame frame) {
            this.frame = frame;
            if (!(frame.getGlassPane() instanceof MouselessGlassPane))
                frame.setGlassPane(new MouselessGlassPane());
        }

        public void startIntercepting() {
            frame.getGlassPane().setVisible(true);
            oldDefaultCloseOperation = frame.getDefaultCloseOperation();
            frame.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
        }

        public void stopIntercepting() {
            frame.getGlassPane().setVisible(false);
            frame.setDefaultCloseOperation(oldDefaultCloseOperation);
        }

        public JDialog getChildDialog(String name, boolean modal) {
            return new JDialog(frame, name, modal);
        }
    }

    private class JDialogDelegate implements Delegate {

        private JDialog frame;
        private int oldDefaultCloseOperation;

        public JDialogDelegate(JDialog frame) {
            this.frame = frame;
            if (!(frame.getGlassPane() instanceof MouselessGlassPane))
                frame.setGlassPane(new MouselessGlassPane());
        }

        public void startIntercepting() {
            frame.getGlassPane().setVisible(true);
            oldDefaultCloseOperation = frame.getDefaultCloseOperation();
            frame.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
        }

        public void stopIntercepting() {
            frame.getGlassPane().setVisible(false);
            frame.setDefaultCloseOperation(oldDefaultCloseOperation);
        }

        public JDialog getChildDialog(String name, boolean modal) {
            return new JDialog(frame, name, modal);
        }
    }
}
