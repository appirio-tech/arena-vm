package com.topcoder.client.contestApplet.widgets;

import com.topcoder.client.contestApplet.ContestApplet;
import com.topcoder.client.contestApplet.common.*;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.net.URL;
import java.net.MalformedURLException;


public final class SoftwareButton extends JPanel {
    
    private ContestApplet ca;
    
    private static final String SOFTWARE_IMAGE_FILENAME = "Software.gif";
    private static final String DESIGN_IMAGE_FILENAME = "design.gif";
    private static final String DEVELOPMENT_IMAGE_FILENAME = "development.gif";
    
    /**
     * Constructor
     */
    public SoftwareButton(ContestApplet _ca) {
        super(new GridBagLayout());
        this.ca = _ca;

        Insets is = new Insets(0,0,0,0);
        
        GridBagConstraints gbc = Common.getDefaultConstraints();
        gbc.insets = is;
        gbc.anchor = gbc.NORTH;
        
        Common.insertInPanel(new DesignButton(), this, gbc, 0, 0, 0, 0);
        Common.insertInPanel(new DevButton(), this, gbc, 0, 1, 0, 0);
        
        setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));
        setOpaque(false);
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
    
    private class DesignButton extends JButton {
        public DesignButton() {
            super();
            
            ImageIcon img = Common.getImage(DESIGN_IMAGE_FILENAME, ca);
            setIcon(img);
            setPreferredSize(new Dimension(img.getIconWidth(), img.getIconHeight()));
            setMaximumSize(new Dimension(img.getIconWidth(), img.getIconHeight()));
            addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    DesignClick();
                }
            });
            setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));
            setOpaque(false);
        }
        
    }
    
    private class DevButton extends JButton {
        public DevButton() {
            super();
            
            ImageIcon img = Common.getImage(DEVELOPMENT_IMAGE_FILENAME, ca);
            setIcon(img);
            setPreferredSize(new Dimension(img.getIconWidth(), img.getIconHeight()));
            setMaximumSize(new Dimension(img.getIconWidth(), img.getIconHeight()));
            addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    DevClick();
                }
            });
            setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));
            setOpaque(false);
        }
        
    }
}