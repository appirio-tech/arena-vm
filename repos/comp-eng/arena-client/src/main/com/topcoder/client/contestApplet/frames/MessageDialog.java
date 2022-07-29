package com.topcoder.client.contestApplet.frames;

/*
* MessageDialog.java
*
* Created on July 10, 2000, 4:08 PM
*/

//import java.util.*;

import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JEditorPane;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextPane;
import javax.swing.ScrollPaneConstants;
import javax.swing.text.JTextComponent;
import javax.swing.text.html.HTMLEditorKit;
import javax.swing.text.html.StyleSheet;

import com.topcoder.client.contestApplet.common.Common;
import com.topcoder.client.contestApplet.common.LocalPreferences;

//import com.topcoder.client.contestApplet.*;
//import com.topcoder.client.contestApplet.listener.*;
//import com.topcoder.client.contestApplet.widgets.*;

/**
 *
 * @author Alex Roman
 * @version
 */

public class MessageDialog extends JDialog {

    LocalPreferences pref = LocalPreferences.getInstance();

    //private JFrame frame = null;
    private JButton okButton = null;
    private JButton cancelButton = null;
    private boolean agreed = true;

    /**
     * Class constructor
     */
    ////////////////////////////////////////////////////////////////////////////////
    public MessageDialog(JFrame frame, String title, String msg)
            ////////////////////////////////////////////////////////////////////////////////
    {
        this(frame, title, msg, false, false);
    }

    /*
    ////////////////////////////////////////////////////////////////////////////////
    public MessageDialog(JFrame frame, String title, String msg, boolean modal)
    ////////////////////////////////////////////////////////////////////////////////
    {
    this(frame, title, msg, false, false);
    }
    */

    ////////////////////////////////////////////////////////////////////////////////
    public MessageDialog(JFrame frame, String title, String msg, boolean modal, boolean wrap)
            ////////////////////////////////////////////////////////////////////////////////
    {
        super(frame, title, modal);

        //this.frame = frame;

        //JTextArea error = new JTextArea(msg, 25, 45);
        JTextComponent error;
        if (msg != null && (msg.startsWith("<html") || msg.startsWith("<HTML"))) {
            error = buildTextPane();
        } else {
            error = buildTextArea(wrap);
        }
        error.setText(msg);
        error.setCaretPosition(0);
        error.setEditable(false);
        error.setBackground(pref.getColor(LocalPreferences.MESSAGEBACK));
        error.setForeground(pref.getColor(LocalPreferences.MESSAGEFORE));
        error.setSelectedTextColor(Common.HF_COLOR);
        error.setSelectionColor(Common.LIGHT_GREEN);
        error.setFont(new Font(pref.getFont(LocalPreferences.MESSAGEFONT), Font.PLAIN, pref.getFontSize(LocalPreferences.MESSAGEFONTSIZE)));
        // Pops - 2/20/03 - added listener for the space or
        // the enter key to close dialog if either pressed.
        error.addKeyListener(new KeyAdapter() {
            public void keyPressed(KeyEvent e) { 
                if(e.getKeyCode()==KeyEvent.VK_SPACE || e.getKeyCode()==KeyEvent.VK_ENTER) {
                    okButtonEvent();
                }
            }
        });

        JScrollPane scroller = new JScrollPane(error,
                ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED,
                ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);

        JPanel p1 = new JPanel();
        p1.setBackground(Common.WPB_COLOR);
        p1.setLayout(new GridLayout(1, 1));

        p1.add(scroller);


        JPanel p2 = new JPanel(new FlowLayout());
        p2.setBackground(Common.WPB_COLOR);
        JButton okButton = new JButton("OK");
        //okButton.addActionListener(new al("actionPerformed", "okButtonEvent", this));
        okButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                okButtonEvent();
            }
        });
        this.okButton = okButton;
        JButton cancelButton = new JButton("Cancel");
        cancelButton.setVisible(false);
        //cancelButton.addActionListener(new al("actionPerformed", "cancelButtonEvent", this));
        cancelButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                cancelButtonEvent();
            }
        });
        this.cancelButton = cancelButton;

        // make sure enter key can close the dialog
        okButton.setDefaultCapable(true);
        getRootPane().setDefaultButton(okButton);

        cancelButton.setDefaultCapable(true);

        getRootPane().setMinimumSize(new Dimension(600 - 10, 400 - 50));
        getRootPane().setPreferredSize(new Dimension(600 - 10, 400 - 50));
        setResizable(true);

        GridBagConstraints gbc = Common.getDefaultConstraints();
        getContentPane().setLayout(new GridBagLayout());
        gbc.insets = new Insets(15, 15, 15, 15);
        gbc.fill = GridBagConstraints.BOTH;
        Common.insertInPanel(scroller, getContentPane(), gbc, 0, 0, 2, 1, 0.1, 1.0);
        gbc.fill = GridBagConstraints.NONE;
        Common.insertInPanel(okButton, getContentPane(), gbc, 0, 1, 1, 1, 0.1, 0.1);
        Common.insertInPanel(cancelButton, getContentPane(), gbc, 1, 1, 1, 1, 0.1, 0.1);

        pack();
        setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        Common.setLocationRelativeTo(frame, this);
    }

    private JTextComponent buildTextArea(boolean wrap) {
        JTextComponent error;
        JTextArea textArea = new JTextArea();
        error = textArea;
        textArea.setLineWrap(wrap);
        textArea.setWrapStyleWord(wrap);
        return error;
    }

    private JTextComponent buildTextPane() {
        JEditorPane textPane;
        JTextComponent error;
        textPane = new JTextPane();
        error = textPane;
        textPane.setContentType("text/html");
        //TODO 1.5: remove this and set HONOR... property
        HTMLEditorKit editorKit = (HTMLEditorKit) textPane.getEditorKit().clone();
        //StyleSheet is shared, we don't want to change defaults
        StyleSheet oldCss = editorKit.getStyleSheet();
        StyleSheet css = new StyleSheet();
        css.addRule(
                "body {" +
                "color: #"+Integer.toHexString(pref.getColor(LocalPreferences.MESSAGEFORE).getRGB() & 0x00FFFFFF)+";" +
                "background: #"+Integer.toHexString(pref.getColor(LocalPreferences.MESSAGEBACK).getRGB()& 0x00FFFFFF)+";" +
                "font-family: "+pref.getFont(LocalPreferences.MESSAGEFONT)+";" +
                "font-size: "+pref.getFontSize(LocalPreferences.MESSAGEFONTSIZE)+"pt;" +
                " }");
        css.addStyleSheet(oldCss);
        editorKit.setStyleSheet(css);
        textPane.setEditorKit(editorKit);
        return error;
    }

    public void pressButton()
    {
        if(okButton != null && okButton.hasFocus())
        {
            okButtonEvent();
        }
        else if(cancelButton != null && cancelButton.hasFocus())
        {
            cancelButtonEvent();
        }
    }

    ////////////////////////////////////////////////////////////////////////////////
    public boolean showDialog()
            ////////////////////////////////////////////////////////////////////////////////
    {
        agreed = false;

        show();

        return (agreed);
    }

    ////////////////////////////////////////////////////////////////////////////////
    public void setButtonText(String t)
            ////////////////////////////////////////////////////////////////////////////////
    {
        okButton.setText(t);
    }

    ////////////////////////////////////////////////////////////////////////////////
    public void setButton2Text(String t)
            ////////////////////////////////////////////////////////////////////////////////
    {
        cancelButton.setText(t);
        cancelButton.setVisible(true);
    }

    ////////////////////////////////////////////////////////////////////////////////
    private void okButtonEvent()
            ////////////////////////////////////////////////////////////////////////////////
    {
        agreed = true;
        dispose();  // frees up the show() -- must be last
    }

    ////////////////////////////////////////////////////////////////////////////////
    private void cancelButtonEvent()
            ////////////////////////////////////////////////////////////////////////////////
    {
        dispose();  // frees up the show() -- must be last
    }
}
