package com.topcoder.client.contestApplet.frames;

/*
* SubmitResultsDialog.java
*
* Created on July 10, 2000, 4:08 PM
*/

//import java.util.*;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
//import javax.swing.border.*;
//import javax.swing.table.*;
//import javax.swing.event.*;
import com.topcoder.client.contestApplet.common.*;
import com.topcoder.client.contestApplet.common.LocalPreferences;

//import com.topcoder.client.contestApplet.*;
//import com.topcoder.client.contestApplet.listener.*;
//import com.topcoder.client.contestApplet.widgets.*;

/**
 *
 * @author Ryan Fairfax
 * @version
 */

public class SubmitResultsDialog extends JDialog {

    LocalPreferences pref = LocalPreferences.getInstance();

    //private JFrame frame = null;
    private JButton okButton = null;

    /**
     * Class constructor
     */
    ////////////////////////////////////////////////////////////////////////////////
    public SubmitResultsDialog(JFrame frame, String title, String msg)
            ////////////////////////////////////////////////////////////////////////////////
    {
        super(frame, title, true);

        JLabel error = new JLabel();
        error.setText(msg);
        //error.setCaretPosition(0);
        //error.setLineWrap(true);
        //error.setWrapStyleWord(true);
        //error.setEditable(false);
        //error.setBackground(pref.getColor(LocalPreferences.MESSAGEBACK));
        error.setForeground(pref.getColor(LocalPreferences.MESSAGEFORE));
        //error.setSelectedTextColor(Common.HF_COLOR);
        //error.setSelectionColor(Common.LIGHT_GREEN);
        error.setFont(new Font(error.getFont().getName(), Font.PLAIN, error.getFont().getSize()));
        
        //error.setFont(new Font(pref.getFont(LocalPreferences.MESSAGEFONT), Font.PLAIN, pref.getFontSize(LocalPreferences.MESSAGEFONTSIZE)));

        error.addKeyListener(new KeyAdapter() {
            public void keyPressed(KeyEvent e) { 
                if(e.getKeyCode()==KeyEvent.VK_SPACE || e.getKeyCode()==KeyEvent.VK_ENTER) {
                    okButtonEvent();
                }
            }
        });

        //JScrollPane scroller = new JScrollPane(error,
          //      ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED,
            //    ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);

//        JPanel p1 = new JPanel();
//        p1.setBackground(Common.WPB_COLOR);
//        p1.setLayout(new GridLayout(1, 1));

//        p1.add(error);


        JPanel p2 = new JPanel(new FlowLayout());
        p2.setBackground(Common.WPB_COLOR);
        JButton okButton = new JButton("OK");
        okButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                okButtonEvent();
            }
        });
        this.okButton = okButton;
        
        // make sure enter key can close the dialog
        okButton.setDefaultCapable(true);
        getRootPane().setDefaultButton(okButton);

        //getRootPane().setMinimumSize(new Dimension(600 - 10, 400 - 50));
        //getRootPane().setPreferredSize(new Dimension(600 - 10, 400 - 50));
        setResizable(true);

        GridBagConstraints gbc = Common.getDefaultConstraints();
        getContentPane().setLayout(new GridBagLayout());
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.BOTH;
        Common.insertInPanel(error, getContentPane(), gbc, 0, 0, 2, 1, 0.1, 1.0);
        gbc.fill = GridBagConstraints.NONE;
        Common.insertInPanel(okButton, getContentPane(), gbc, 0, 1, 1, 1, 0.1, 0.1);

        pack();

        Common.setLocationRelativeTo(frame, this);

    }



    public void pressButton()
    {
        if(okButton != null && okButton.hasFocus())
        {
            okButtonEvent();
        }
    }

    ////////////////////////////////////////////////////////////////////////////////
    public boolean showDialog()
            ////////////////////////////////////////////////////////////////////////////////
    {
        show();

        return (true);
    }

    ////////////////////////////////////////////////////////////////////////////////
    private void okButtonEvent()
            ////////////////////////////////////////////////////////////////////////////////
    {
        dispose();  // frees up the show() -- must be last
    }

}
