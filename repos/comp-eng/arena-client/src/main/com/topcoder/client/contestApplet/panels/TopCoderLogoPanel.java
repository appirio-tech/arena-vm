package com.topcoder.client.contestApplet.panels;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.MalformedURLException;
import java.net.URL;
import javax.swing.JButton;
import javax.swing.JPanel;

import com.topcoder.client.contestApplet.ContestApplet;
import com.topcoder.client.contestApplet.common.Common;
import com.topcoder.netCommon.contest.ContestConstants;

public final class TopCoderLogoPanel extends JPanel {

    private static final String image = "top_coder.gif";

    private final ContestApplet ca;

    public TopCoderLogoPanel(ContestApplet ca) {
        super();
        this.ca = ca;
        setOpaque(false);

        String imageFileName;
        int width;
        int height;

        //if (ca.getCompanyName().startsWith(ContestConstants.COMPANY_SUN)) {
        if (ca.getPoweredByView()) {
            imageFileName = "powered_tc.gif";
            width = 300;
            height = 50;
        } else {
            imageFileName = image;
            width = 179;
            height = 60;
        }

        JButton label = Common.getImageButton(imageFileName, ca);

        label.setMinimumSize(new Dimension(width, height));
        label.setPreferredSize(new Dimension(width, height));
        label.setMaximumSize(new Dimension(width, height));
        label.setCursor(new Cursor(Cursor.HAND_CURSOR));
        label.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                TopCoderClick();
            }
        }
        );
        add(label);

        // Start loading the sponsor image
    }

    public void TopCoderClick() {
        try {
            Common.showURL(ca.getAppletContext(), new URL(Common.URL_TOPCODER));
        } catch (MalformedURLException ex) {
            ex.printStackTrace();
        }
    }

}
