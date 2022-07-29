package com.topcoder.client.contestApplet.panels;

import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.net.*;
import javax.swing.*;

import com.topcoder.client.contestApplet.common.*;
import com.topcoder.client.contestApplet.*;

public class SoftwareLogoPanel extends JPanel {
    
    private ContestApplet ca = null;
    
    private static final String DESIGN_IMAGE_FILENAME = "design.gif";
    private static final String DEVELOPMENT_IMAGE_FILENAME = "development.gif";
    
    public SoftwareLogoPanel(ContestApplet ca) {
        super(new GridBagLayout());
        this.ca = ca;
        setOpaque(false);
        ImageIcon img = Common.getImage(DESIGN_IMAGE_FILENAME, ca);
        JButton label = Common.getImageButton(DESIGN_IMAGE_FILENAME, ca);
        label.setCursor(new Cursor(Cursor.HAND_CURSOR));
        label.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                DesignClick();
            }
        }
        );
        label.setPreferredSize(new Dimension(img.getIconWidth(), img.getIconHeight()));
        label.setMinimumSize(new Dimension(img.getIconWidth(), img.getIconHeight()));
        
        ImageIcon img2 = Common.getImage(DEVELOPMENT_IMAGE_FILENAME, ca);
        JButton label2 = Common.getImageButton(DEVELOPMENT_IMAGE_FILENAME, ca);
        label2.setCursor(new Cursor(Cursor.HAND_CURSOR));
        setPreferredSize(new Dimension(img.getIconWidth() + img2.getIconWidth(), img.getIconHeight() + img2.getIconHeight()));
        setMaximumSize(new Dimension(img.getIconWidth() + img2.getIconWidth(), img.getIconHeight() + img2.getIconHeight()));
        label2.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                DevClick();
            }
        }
        );
        
        label2.setPreferredSize(new Dimension(img2.getIconWidth(), img2.getIconHeight()));
        label2.setMinimumSize(new Dimension(img2.getIconWidth(), img2.getIconHeight()));
        
        GridBagConstraints gbc = Common.getDefaultConstraints();
        gbc.insets.top = 10;
        gbc.insets.bottom = 0;
        gbc.insets.right = 0;
        gbc.insets.left = 0;
        gbc.fill = gbc.NONE;
        gbc.weightx = 0;
        gbc.weighty = 0;
        gbc.anchor = gbc.NORTH;
        
        Common.insertInPanel(label, this, gbc, 0, 0, 1, 1);
        
        gbc.insets.top = 0;
        gbc.anchor = gbc.NORTH;
        
        Common.insertInPanel(label2, this, gbc, 0, 1, 1, 1);
    }
    
    public void SoftwareClick() {
        try {
            Common.showURL(ca.getAppletContext(), new URL(Common.URL_SOFTWARE));
        } catch (MalformedURLException ex) {
            ex.printStackTrace();
        }
    }
    
    public void DesignClick() {
        try {
            Common.showURL(ca.getAppletContext(), new URL(Common.URL_SOFTWARE_DESIGN));
        } catch (MalformedURLException ex) {
            ex.printStackTrace();
        }
    }
    
    public void DevClick() {
        try {
            Common.showURL(ca.getAppletContext(), new URL(Common.URL_SOFTWARE_DEVELOPMENT));
        } catch (MalformedURLException ex) {
            ex.printStackTrace();
        }
    }
}