package com.topcoder.client.contestApplet.panels;

import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.net.*;
import javax.swing.*;

import com.topcoder.client.contestApplet.common.*;
import com.topcoder.client.contestApplet.*;

public class EmploymentServicesLogoPanel extends JPanel {

    private ContestApplet ca = null;
    private static String image = "Employment_Services.gif";

    public EmploymentServicesLogoPanel(ContestApplet ca) {
        super();
        this.ca = ca;
        setOpaque(false);
        ImageIcon img = Common.getImage(image, ca);
        JButton label = Common.getImageButton(image, ca);
        label.setCursor(new Cursor(Cursor.HAND_CURSOR));
        setPreferredSize(new Dimension(img.getIconWidth(), img.getIconHeight()));
        setMaximumSize(new Dimension(img.getIconWidth(), img.getIconHeight()));
        label.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                EmploymentServicesClick();
            }
        }
        );
        add(label);

        // Start loading the sponsor image
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