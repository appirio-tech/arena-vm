/*
 * ResultDisplayTypeSelectionPanel
 * 
 * Created 09/21/2007
 */
package com.topcoder.client.contestApplet.widgets;

import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Set;

import javax.swing.AbstractButton;
import javax.swing.ButtonGroup;
import javax.swing.ButtonModel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;

import com.topcoder.client.contestApplet.common.Common;
import com.topcoder.client.contestant.RoundModel;
import com.topcoder.netCommon.contest.ResultDisplayType;

/**
 * @author Diego Belfer (mural)
 * @version $Id: ResultDisplayTypeSelectionPanel.java 67962 2008-01-15 15:57:53Z mural $
 */
public class ResultDisplayTypeSelectionPanel extends JPanel {
    private ButtonGroup viewGroup;
    private Set resultDisplayTypes;
    private Listener listener;

    public ResultDisplayTypeSelectionPanel(RoundModel model, Listener listener) {
        super(new FlowLayout(FlowLayout.LEFT));
        setOpaque(false);
        this.listener = listener;
        init(model);
    }

    private void init(RoundModel model) {
        resultDisplayTypes = new HashSet(Arrays.asList(model.getRoundProperties().getAllowedScoreTypesToShow()));
        viewGroup = new ButtonGroup();
        addViewButton(viewGroup, this, ResultDisplayType.STATUS);
        addViewButton(viewGroup, this,ResultDisplayType.POINTS);
        addViewButton(viewGroup, this,ResultDisplayType.PASSED_TESTS);
        viewGroup.setSelected(((AbstractButton) viewGroup.getElements().nextElement()).getModel(), true);
    }
    
    private void addViewButton(ButtonGroup group, JPanel panel, final ResultDisplayType type) {
        if (resultDisplayTypes.contains(type)) {
            JRadioButton button = new JRadioButton(type.getDescription());
            button.setBackground(Common.WPB_COLOR);
            button.setForeground(Color.white);
            button.setActionCommand(String.valueOf(type.getId()));
            button.setOpaque(false);
            button.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    listener.typeChanged(type);
                }
            });
            group.add(button);
            panel.add(button);
        }
    }

    public void setSelectedType(ResultDisplayType type) {
        String action = String.valueOf(type.getId());
        Enumeration elements = viewGroup.getElements();
        while (elements.hasMoreElements()) {
            ButtonModel button = ((AbstractButton) elements.nextElement()).getModel();
            if (button.getActionCommand().equals(action)) {
                viewGroup.setSelected(button, true);
            }
        }
    }
    
    public ResultDisplayType getSelectedType() {
        return ResultDisplayType.get(Integer.parseInt(viewGroup.getSelection().getActionCommand()));
    }
    
    public static interface Listener {
        void typeChanged(ResultDisplayType newType);
    }
}
