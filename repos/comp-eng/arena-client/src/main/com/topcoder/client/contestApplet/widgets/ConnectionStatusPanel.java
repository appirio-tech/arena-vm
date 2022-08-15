/*
 * ConnectionStatusLabel.java
 *
 * Created on May 19, 2005, 9:18 AM
 *
 * To change this template, choose Tools | Options and locate the template under
 * the Source Creation and Management node. Right-click the template and choose
 * Open. You can then make changes to the template in the Source Editor.
 */

package com.topcoder.client.contestApplet.widgets;

import com.topcoder.client.contestApplet.common.Common;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import javax.swing.ImageIcon;
import javax.swing.JLabel;

/**
 *
 * @author rfairfax
 */
public class ConnectionStatusPanel extends JLabel {
    
    /** Creates a new instance of ConnectionStatusLabel */
    public ConnectionStatusPanel() {
    }
        
    ImageIcon connectedImage = null;
    ImageIcon disconnectedImage = null;
    
    public void setConnectedImage(ImageIcon img) {
        this.connectedImage = img;
    }
    
    public void setDisconnectedImage(ImageIcon img) {
        this.disconnectedImage = img;
    }
    
    public void setStatus(boolean on) {
        if(on) {
            this.setIcon(connectedImage);
        } else {
            this.setIcon(disconnectedImage);
        }
    }
}
