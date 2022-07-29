package com.topcoder.client.contestApplet.panels.coding;

import java.awt.*;
import javax.swing.*;

import com.topcoder.client.contestApplet.common.*;
import com.topcoder.client.contestApplet.*;
import com.topcoder.client.contestApplet.widgets.*;
import com.topcoder.client.contestant.*;
import com.topcoder.shared.problem.*;

public final class ProblemInfoPanel extends ImageIconPanel implements ProblemInfoComponent {

    private JLabel className = null;
    private JLabel methodName = null;
    private JLabel returnType = null;
    private JLabel argTypes = null;

    public ProblemInfoPanel(ContestApplet ca) {
        super(new GridBagLayout(), Common.getImage("coding_area.gif", ca));

        // set the size
        setMinimumSize(new Dimension(600, 53));
        setPreferredSize(new Dimension(600, 53));

        create();
    }

    private void create() {
        GridBagConstraints gbc = Common.getDefaultConstraints();

        // contest label
        JLabel l1 = new JLabel("Class Name: ");
        JLabel l2 = new JLabel("");
        JLabel l3 = new JLabel("Method Name: ");
        JLabel l4 = new JLabel("");
        JLabel l5 = new JLabel("Return Type: ");
        JLabel l6 = new JLabel("");
        JLabel l7 = new JLabel("Arg Types: ");
        JLabel l8 = new JLabel("");

        Dimension d = new Dimension(105, 13);

        l1.setForeground(Color.white);
        l2.setForeground(Color.white);
        l2.setPreferredSize(d);
        l2.setMinimumSize(d);
        l3.setForeground(Color.white);
        l4.setForeground(Color.white);
        l4.setPreferredSize(d);
        l4.setMinimumSize(d);

        Font fixed;
        if (UIManager.getSystemLookAndFeelClassName().equals("com.sun.java.swing.plaf.windows.WindowsLookAndFeel")) {
            fixed = new Font("Courier", Font.PLAIN, 11);
        } else {
            fixed = new Font("Courier", Font.PLAIN, 10);
        }

        l2.setFont(fixed);
        l4.setFont(fixed);
        l6.setFont(fixed);
        l8.setFont(fixed);

        //Dimension d3= new Dimension(100, 13);
        //Dimension d3= new Dimension(170, 13);
        Dimension d3 = new Dimension(292, 13);

        l5.setForeground(Color.white);
        l6.setForeground(Color.white);
        l6.setPreferredSize(d3);
        l6.setMinimumSize(d3);
        l7.setForeground(Color.white);
        l8.setForeground(Color.white);
        l8.setPreferredSize(d3);
        l8.setMinimumSize(d3);

        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.NONE;
        gbc.insets = new Insets(15, 15, 1, 1);
        Common.insertInPanel(l1, this, gbc, 0, 0, 1, 1, 1.0, 0.1);
        gbc.insets = new Insets(15, 1, 1, 1);
        Common.insertInPanel(l2, this, gbc, 1, 0, 1, 1, 1.0, 0.1);
        gbc.insets = new Insets(1, 15, 15, 1);
        Common.insertInPanel(l3, this, gbc, 0, 1, 1, 1, 1.0, 0.1);
        gbc.insets = new Insets(1, 1, 15, 1);
        Common.insertInPanel(l4, this, gbc, 1, 1, 1, 1, 1.0, 0.1);
        gbc.insets = new Insets(15, 1, 1, 1);
        Common.insertInPanel(l5, this, gbc, 2, 0, 1, 1, 1.0, 0.1);
        gbc.insets = new Insets(15, 1, 1, 15);
        Common.insertInPanel(l6, this, gbc, 3, 0, 1, 1, 1.0, 0.1);
        gbc.insets = new Insets(1, 1, 15, 0);
        Common.insertInPanel(l7, this, gbc, 2, 1, 1, 1, 1.0, 0.1);
        gbc.insets = new Insets(1, 1, 15, 15);
        Common.insertInPanel(l8, this, gbc, 3, 1, 1, 1, 1.0, 0.1);

        className = l2;
        methodName = l4;
        returnType = l6;
        argTypes = l8;
    }

    public void updateComponentInfo(ProblemComponentModel problemComponent, int language) {
        className.setText(problemComponent.getClassName());
        methodName.setText(problemComponent.getMethodName());
        returnType.setText(problemComponent.getReturnType().getDescriptor(language));

        String args = "(";
        DataType[] paramTypes = problemComponent.getParamTypes();
        if (paramTypes.length > 0) {
            args += paramTypes[0].getDescriptor(language);
            for (int i = 1; i < paramTypes.length; i++) {
                args += "," + paramTypes[i].getDescriptor(language);
            }
        }
        argTypes.setText(args + ")");
        argTypes.repaint();
    }

    public void clear() {
    }
}
