package com.topcoder.client.contestApplet.widgets;

import com.topcoder.client.contestApplet.ContestApplet;
import com.topcoder.client.contestApplet.common.*;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.net.URL;
import java.net.MalformedURLException;


public final class EmploymentButton extends JButton {

    private ContestApplet ca;
    private LocalPreferences pref;

    private static final String SOFTWARE_IMAGE_FILENAME = "Employment_Services.gif";

    /**
     * Constructor
     */
    public EmploymentButton(ContestApplet _ca) {
        super();
        this.ca = _ca;
        pref = LocalPreferences.getInstance();

        ImageIcon img = Common.getImage(SOFTWARE_IMAGE_FILENAME, ca);
        setIcon(img);
        setPreferredSize(new Dimension(img.getIconWidth(), img.getIconHeight()));
        setMaximumSize(new Dimension(img.getIconWidth(), img.getIconHeight()));
        addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                EmploymentServicesClick();
            }
        });
        setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));
        setOpaque(false);
    }

    ////////////////////////////////////////////////////////////////////////////////
    public void EmploymentServicesClick()
            ////////////////////////////////////////////////////////////////////////////////
    {
        try {
            Common.showURL(ca.getAppletContext(), new URL(Common.URL_EMPLOYMENT));
        } catch (MalformedURLException ex) {
            ex.printStackTrace();
        }
    }
}