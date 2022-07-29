package com.topcoder.client.contestApplet.frames;

import java.awt.Color;
import javax.swing.AbstractButton;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JRadioButton;

import com.topcoder.client.contestApplet.common.Common;

final class VotingElement {

    private static final Color FOREGROUND = Common.PT_COLOR;

    private final String name;
    private final Color background;
    private final Color coderColor;

    VotingElement(String name, Color background, Color coderColor) {
        this.name = name;
        this.background = background;
        this.coderColor = coderColor;
    }

    JComponent getStar() {
        JLabel label = new JLabel("*");
        label.setBackground(background);
        label.setForeground(FOREGROUND);
        return label;
    }

    AbstractButton getRadioButton() {
        AbstractButton radioButton = new JRadioButton();
        radioButton.setBackground(background);
        radioButton.setForeground(Color.white);
        return radioButton;
    }

    JComponent getCoderName() {
        JLabel label = new JLabel(name);
        label.setBackground(background);
        label.setForeground(coderColor);
        return label;
    }

    JButton getRoundStats() {
        return new JButton("Round Stats");
    }

}
