package com.topcoder.client.contestApplet.widgets.ui;

import java.awt.Component;
import java.awt.GridBagConstraints;
import java.awt.Insets;
import java.util.ArrayList;

import javax.swing.ListCellRenderer;
import javax.swing.DefaultListCellRenderer;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;

import com.topcoder.client.contestApplet.widgets.SortedComboBoxModel;
import com.topcoder.client.contestant.view.*;
import com.topcoder.client.contestant.*;
import com.topcoder.netCommon.contest.*;
import com.topcoder.netCommon.contestantMessages.response.data.*;
import com.topcoder.client.contestApplet.common.Common;
import com.topcoder.shared.problem.*;
import com.topcoder.client.ui.UIComponent;
import com.topcoder.client.ui.UIComponentException;
import com.topcoder.client.ui.impl.component.UIPanel;

public class UIUserAssignmentContentPanel extends UIPanel {
    private JPanel panel;
    private ComponentAssignmentData cad;
    private UIComponent labelTemplate;
    private UIComponent comboTemplate;
    private RoomModel room;
    /** Problem info set */
    private ArrayList problemSet = null;

    /** Names of problems */
    private String[] problems = null;

    /** Names of components */
    private String[][] components = null;
    private String[] headers = null;

    /** Names of users */
    private ArrayList users;


    /** User model array for each problem */
    private SortedComboBoxModel[][] userModel = null;

    /** Combo box array for each problem */
    private JComboBox[][] combos = null;

    protected void initialize() throws UIComponentException {
        super.initialize();

        panel = (JPanel) getEventSource();
    }

    protected void setPropertyImpl(String name, Object value) throws UIComponentException {
        if ("ComponentAssignmentData".equalsIgnoreCase(name)) {
            cad = (ComponentAssignmentData) value;
            if (cad != null && room != null) {
                createPanel();
            }
        } else if ("RoomModel".equalsIgnoreCase(name)) {
            room = (RoomModel) value;
            if (cad != null && room != null) {
                createPanel();
            }
        } else if ("LabelTemplate".equalsIgnoreCase(name)) {
            labelTemplate = (UIComponent) value;
        } else if ("ComboBoxTemplate".equalsIgnoreCase(name)) {
            comboTemplate = (UIComponent) comboTemplate;
        } else {
            super.setPropertyImpl(name, value);
        }
    }

    protected Object getPropertyImpl(String name) throws UIComponentException {
        if ("ComponentAssignmentData".equalsIgnoreCase(name)) {
            build();
            return cad;
        } else if ("RoomModel".equalsIgnoreCase(name)) {
            return room;
        } else {
            return super.getPropertyImpl(name);
        }
    }

    private void build() {
        if (room == null || cad == null) {
            throw new UIComponentException("The necessary runtime information is not available.");
        }
        RoundModel round = room.getRoundModel();
        cad = new ComponentAssignmentData(cad.getTeamID(), cad.getRoundID());
        ProblemModel[] problems = round.getProblems(room.getDivisionID());
        for (int i = 0; i < components.length; i++) {
            for (int j = 0; j < components[i].length; j++) {
                if (combos[i][j].getSelectedItem() instanceof UserListItem) {
                    cad.assignComponent((int) problems[i].getComponents()[j].getID().longValue(),
                                        ((UserListItem) combos[i][j].getSelectedItem()).getUserID());
                }
            }
        }
    }

    private void createPanel() {
        createProblems();

        GridBagConstraints gbc = new GridBagConstraints();
        ProblemModel[] problemModels = room.getRoundModel().getProblems(room.getDivisionID());
        combos = new JComboBox[problems.length][headers.length - 1];
        userModel = new SortedComboBoxModel[problems.length][headers.length - 1];

        gbc.insets = new Insets(5, 5, 5, 5);

        ListCellRenderer renderer = new DefaultListCellRenderer() {
                public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                    if (value instanceof UserListItem) {
                        value = ((UserListItem) value).getUserName();
                    }
                    return super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                }
            };

        int y = 0;
        for (int i = 0; i < problems.length; i++) {
            int j = 0;
            JLabel jl = (JLabel) labelTemplate.performAction("clone");
            jl.setText(problems[i] + ":");
            gbc.anchor = GridBagConstraints.CENTER;
            Common.insertInPanel(jl, panel, gbc, 0, y, 1, 1, 1.0, 0.0);
            Common.insertInPanel(new JLabel(" "), panel, gbc, 0, y + 1, 1, 1, 0.0, 0.0);
            gbc.fill = GridBagConstraints.NONE;
            gbc.anchor = GridBagConstraints.CENTER;
            for (j = 0; j < ((ProblemModel) problemSet.get(i)).getComponents().length; j++) {
                jl = (JLabel) labelTemplate.performAction("clone");
                jl.setText(components[i][j]);
                Common.insertInPanel(jl, panel, gbc, j + 1, y, 1, 1, 0.0, 1.0);
                combos[i][j] = (JComboBox) comboTemplate.performAction("clone");
                combos[i][j].setRenderer(renderer);
                int selectedIndex = 0;
                for (int k = 0; k < users.size(); k++) {
                    combos[i][j].addItem(users.get(k));
                    if (cad.getAssignedUserForComponent((int) problemModels[i].getComponents()[j].getID().longValue()) ==
                        ((UserListItem) users.get(k)).getUserID()) {
                        selectedIndex = k;
                    }
                }
                if (users.size() > 0) {
                    combos[i][j].setSelectedIndex(selectedIndex);
                }
                combos[i][j].setUI(combos[i][j].getUI());
                Common.insertInPanel(combos[i][j], panel, gbc, j + 1, y + 1, 1, 1, 1.0, 1.0);
            }
            for (; j < headers.length - 1; j++) {
                combos[i][j] = null;
                userModel[i][j] = null;
                Common.insertInPanel(new JLabel(""), panel, gbc, j + 1, y, 1, 1, 1.0, 0.0);
                Common.insertInPanel(new JLabel(""), panel, gbc, j + 1, y + 1, 1, 1, 1.0, 0.0);
            }
            y += 2;
        }
    }

    private void createProblems() {
        if (!room.hasRoundModel()) {
            throw new IllegalStateException("Can't build assignment panel, no round for room: " + room);
        }
        RoundModel round = room.getRoundModel();
        if (!round.hasProblems(room.getDivisionID())) {
            throw new IllegalStateException("Can't build assignment panel, no problems for round: " + round);
        }
        ProblemModel[] problems = round.getProblems(room.getDivisionID());

        ArrayList al = new ArrayList();
        int nCols = 0;

        for (int i = 0; i < problems.length; i++) {
            al.add(problems[i]);
        }

        for (int i = 0; i < al.size(); i++) {
            if (nCols < ((ProblemModel) al.get(i)).getComponents().length)
                nCols = ((ProblemModel) al.get(i)).getComponents().length;
        }

        headers = new String[nCols + 1];
        headers[0] = "Problem";
        for (int i = 0; i < nCols; i++) {
            headers[i + 1] = "Component " + (i + 1);
        }

        this.problems = new String[al.size()];
        this.components = new String[al.size()][];
        for (int i = 0; i < al.size(); i++) {
            this.problems[i] = ((ProblemModel) al.get(i)).getName();
            this.components[i] = new String[((ProblemModel) al.get(i)).getComponents().length];
            for (int j = 0; j < this.components[i].length; j++) {
                components[i][j] = ((ProblemModel) al.get(i)).getComponents()[j].getClassName();
            }
        }

        this.problemSet = al;

        this.users = new ArrayList();
        for (int i = 0; i < cad.getTeamMembers().size(); i++) {
            this.users.add(cad.getTeamMembers().get(i));
        }
    }
}
