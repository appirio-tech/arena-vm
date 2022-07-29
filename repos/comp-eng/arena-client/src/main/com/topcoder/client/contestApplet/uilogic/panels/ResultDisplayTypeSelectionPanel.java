/*
 * ResultDisplayTypeSelectionPanel
 * 
 * Created 09/21/2007
 */
package com.topcoder.client.contestApplet.uilogic.panels;

import java.awt.event.ActionEvent;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Set;

import javax.swing.AbstractButton;
import javax.swing.ButtonGroup;
import javax.swing.ButtonModel;

import com.topcoder.client.contestant.RoundModel;
import com.topcoder.client.ui.UIComponent;
import com.topcoder.client.ui.UIPage;
import com.topcoder.client.ui.event.UIActionListener;
import com.topcoder.netCommon.contest.ResultDisplayType;

/**
 * @author Diego Belfer (mural)
 * @version $Id: ResultDisplayTypeSelectionPanel.java 67962 2008-01-15 15:57:53Z mural $
 */
public class ResultDisplayTypeSelectionPanel {
    private ButtonGroup viewGroup;
    private Set resultDisplayTypes;
    private Listener listener;
    private boolean selectedSet = false;

    public ResultDisplayTypeSelectionPanel(UIPage page, RoundModel model, Listener listener) {
        //super(new FlowLayout(FlowLayout.LEFT));
        //setOpaque(false);
        this.listener = listener;
        init(model, page);
    }

    private void init(RoundModel model, UIPage page) {
        resultDisplayTypes = new HashSet(Arrays.asList(model.getRoundProperties().getAllowedScoreTypesToShow()));
        viewGroup = new ButtonGroup();
        addViewButton(viewGroup, page, ResultDisplayType.STATUS, "status_button");
        addViewButton(viewGroup, page, ResultDisplayType.POINTS, "points_button");
        addViewButton(viewGroup, page, ResultDisplayType.PASSED_TESTS, "passed_tests_button");
    }
    
    private void addViewButton(ButtonGroup group, UIPage page, final ResultDisplayType type, String buttonName) {
        UIComponent button = (UIComponent) page.getComponent(buttonName);
        button.addEventListener("action", new UIActionListener() {
            public void actionPerformed(ActionEvent e) {
                listener.typeChanged(type);
            }
        });
        boolean isAvailable = resultDisplayTypes.contains(type);
        button.setProperty("visible",  Boolean.valueOf(isAvailable));
        button.setProperty("actionCommand", String.valueOf(type.getId()));
        AbstractButton jButton = (AbstractButton) button.getEventSource();
        group.add(jButton);
        if (!selectedSet && isAvailable) {
            group.setSelected(jButton.getModel(), true);
            selectedSet = true;
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
