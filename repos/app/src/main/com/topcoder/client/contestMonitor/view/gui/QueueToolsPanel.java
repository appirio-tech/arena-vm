/*
 * QueueToolsPanel.java
 *
 * Created on April 5, 2005, 2:16 PM
 */

package com.topcoder.client.contestMonitor.view.gui;

import com.topcoder.client.contestMonitor.model.CommandSender;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JPanel;
import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.JButton;
import javax.swing.JComboBox;


/**
 *
 * @author rfairfax
 */
public class QueueToolsPanel extends JPanel {
    
    private JLabel info = new JLabel("Queue Information Label");
    private JTextField queueName = new JTextField();
    private JButton getInfoButton = new JButton("Get Info");
    private JComboBox queueList;
    private CommandSender sender;
    
    private static final String[] QUEUES = {"testingQueue","compileQueue","referenceTestingQueue", "other" };
    
    /** Creates a new instance of QueueToolsPanel */
    public QueueToolsPanel(CommandSender snd) {
        super(new GridBagLayout());
        
        this.sender = snd;
        
        queueList = new JComboBox(QUEUES);
        
        queueList.addActionListener(new ActionListener() {
           public void actionPerformed(ActionEvent ae) {
               String s = (String)queueList.getSelectedItem();
               if(s.equals("other")) {
                   queueName.setEnabled(true);
               } else {
                   queueName.setEnabled(false);
                   queueName.setText(s);
               }
               
               //do processing here
           } 
        });
        
        getInfoButton.addActionListener(new ActionListener() {
            public final void actionPerformed(ActionEvent ae) {
                //get queue info here based on name
                sender.sendGetQueueInfoRequest(queueName.getText());
            }
        });
        
        queueName.setText((String)queueList.getSelectedItem());
        queueName.setEnabled(false);
        
        add(queueList, new GridBagConstraints(1,1, 1,1,0,0, GridBagConstraints.NORTHWEST, GridBagConstraints.HORIZONTAL, new Insets(0,0,0,5), 0, 0));
        add(queueName, new GridBagConstraints(2,1, 1,1,1.0,0, GridBagConstraints.NORTHWEST, GridBagConstraints.HORIZONTAL, new Insets(0,0,0,5), 0, 0));
        add(getInfoButton, new GridBagConstraints(3,1, 1,1,0,0, GridBagConstraints.NORTHEAST, GridBagConstraints.NONE, new Insets(0,0,0,0), 0, 0));
        add(info, new GridBagConstraints(1,2, 2,1,1.0,0, GridBagConstraints.NORTH, GridBagConstraints.HORIZONTAL, new Insets(0,0,0,0), 0, 0));
    }
    
}
