/*
 * MultiMethodProblemInfoPanel
 * 
 * Created 06/12/2007
 */
package com.topcoder.client.contestApplet.panels.coding;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;

import javax.swing.BorderFactory;
import javax.swing.JScrollBar;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.ScrollPaneConstants;
import javax.swing.UIManager;

import com.topcoder.client.contestApplet.ContestApplet;
import com.topcoder.client.contestApplet.common.Common;
import com.topcoder.client.contestApplet.widgets.ImageIconPanel;
import com.topcoder.client.contestant.ProblemComponentModel;
import com.topcoder.shared.language.BaseLanguage;
import com.topcoder.shared.language.Language;
import com.topcoder.shared.problem.DataType;

/**
 * Panel for displaying multiple methods on the ProblemInfo component
 * 
 * @autor Diego Belfer (Mural)
 * @version $Id: MultiMethodProblemInfoPanel.java 65513 2007-09-24 19:31:43Z thefaxman $
 */
public final class MultiMethodProblemInfoPanel extends ImageIconPanel implements ProblemInfoComponent {
    private JTextArea text = null;
    private int firstMethod;
    
    public MultiMethodProblemInfoPanel(ContestApplet ca) {
        this(ca,0);
        
    }
    public MultiMethodProblemInfoPanel(ContestApplet ca, int firstMethodIndex) {
        super(new BorderLayout(), Common.getImage("coding_area.gif", ca));
        this.firstMethod = firstMethodIndex;
        // set the size
        setMinimumSize(new Dimension(600, 53));
        setPreferredSize(new Dimension(600, 53));
        create();
    }

    private void create() {
        Font fixed;
        if (UIManager.getSystemLookAndFeelClassName().equals("com.sun.java.swing.plaf.windows.WindowsLookAndFeel")) {
            fixed = new Font("Courier", Font.PLAIN, 11);
        } else {
            fixed = new Font("Courier", Font.PLAIN, 10);
        }
        text = new JTextArea();
        text.setEditable(false);
        text.setAutoscrolls(true);
        text.setFont(fixed);
        text.setOpaque(false);
        text.setForeground(Color.WHITE);
        text.setLineWrap(true);
        text.setWrapStyleWord(true);
        
        JScrollPane scroller = new JScrollPane(text,
                ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED,
                ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        JScrollBar scroll = scroller.getVerticalScrollBar();
        scroll.setPreferredSize(new Dimension(10, (int) scroll.getPreferredSize().getHeight()));
        scroller.setOpaque(false);
        scroller.getViewport().setOpaque(false);
        scroller.setBorder(BorderFactory.createEmptyBorder());
        setBorder(BorderFactory.createEmptyBorder(10,13,13,13));
        add(scroller, BorderLayout.CENTER);
    }

    public void updateComponentInfo(ProblemComponentModel problemComponent, int language) {
        StringBuffer sb = new StringBuffer(200);
        sb.append("ClassName: ").append(problemComponent.getClassName()).append("\n");
        String[] allMethodNames = problemComponent.getComponent().getAllMethodNames();
        String[][] allParamNames = problemComponent.getComponent().getAllParamNames();
        DataType[][] allParamTypes = problemComponent.getComponent().getAllParamTypes();
        DataType[] allReturnTypes = problemComponent.getComponent().getAllReturnTypes();
        Language lang = BaseLanguage.getLanguage(language);
        for (int i = firstMethod; i < allMethodNames.length; i++) {
            sb.append(lang.getMethodSignature(allMethodNames[i], allReturnTypes[i], allParamTypes[i], allParamNames[i])).append("\n");
        }
        sb.setLength(sb.length()-1);
        text.setText(sb.toString());
        text.setCaretPosition(0);
        text.repaint();
    }

    public void clear() {
    }
}
