package com.topcoder.client.contestApplet.panels.main;

import java.awt.*;
import javax.swing.*;

import com.topcoder.client.contestApplet.common.*;
import com.topcoder.client.contestApplet.*;

public class IntermissionPanel extends JPanel {

    private JLabel messageLabel = new JLabel("", JLabel.CENTER);

    ////////////////////////////////////////////////////////////////////////////////
    public IntermissionPanel(ImageIcon image)
            ////////////////////////////////////////////////////////////////////////////////
    {
        super(new GridBagLayout());
        setBackground(Color.black);
        messageLabel.setIcon(image);
        GridBagConstraints gbc = Common.getDefaultConstraints();
        gbc.insets = new Insets(5, 5, 70, 5);
        add(messageLabel, gbc);
    }

    ////////////////////////////////////////////////////////////////////////////////
    public JPanel get()
            ////////////////////////////////////////////////////////////////////////////////
    {
        return (this);
    }
}
