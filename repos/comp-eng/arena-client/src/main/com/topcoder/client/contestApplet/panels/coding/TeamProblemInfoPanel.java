package com.topcoder.client.contestApplet.panels.coding;

import java.awt.*;
import javax.swing.*;

import com.topcoder.client.contestApplet.common.*;
import com.topcoder.client.contestApplet.*;
import com.topcoder.client.contestApplet.widgets.*;
import com.topcoder.client.contestant.*;
import com.topcoder.shared.problem.*;

public final class TeamProblemInfoPanel extends ImageIconPanel {

    private JLabel name = null;

    public TeamProblemInfoPanel(ContestApplet ca) {
        super(new GridBagLayout(), Common.getImage("coding_area.gif", ca));

        // set the size
        setMinimumSize(new Dimension(600, 53));
        setPreferredSize(new Dimension(600, 53));

        create();
    }

    private void create() {
        GridBagConstraints gbc = Common.getDefaultConstraints();

        // contest label
        JLabel l1 = new JLabel("Problem Name: ");
        JLabel l2 = new JLabel("");

        Dimension d = new Dimension(105, 13);

        l1.setForeground(Color.white);
        l2.setForeground(Color.white);
        l2.setPreferredSize(d);
        l2.setMinimumSize(d);

        Font fixed = null;
        if (UIManager.getSystemLookAndFeelClassName().equals("com.sun.java.swing.plaf.windows.WindowsLookAndFeel")) {
            fixed = new Font("Courier", Font.PLAIN, 11);
        } else {
            fixed = new Font("Courier", Font.PLAIN, 10);
        }

        l2.setFont(fixed);

        gbc.anchor = GridBagConstraints.NORTHWEST;
        gbc.fill = GridBagConstraints.NONE;
        gbc.insets = new Insets(15, 15, 1, 1);
        Common.insertInPanel(l1, this, gbc, 0, 0, 1, 1, 0.1, 0.1);
        gbc.insets = new Insets(15, 1, 1, 1);
        Common.insertInPanel(l2, this, gbc, 1, 0, 1, 1, 2.0, 0.1);

        name = l2;
    }

    public void updateProblemInfo(ProblemModel problem, int language) {
        name.setText(problem.getName());
    }

    public void clear() {
    }
}
