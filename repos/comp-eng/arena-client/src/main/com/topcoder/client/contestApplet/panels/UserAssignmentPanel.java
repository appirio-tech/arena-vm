/**
 *
 * UserAssignmentPanel.java
 *
 * @author   Matthew P. Suhocki
 * @version
 */

package com.topcoder.client.contestApplet.panels;

import java.awt.*;
import java.awt.event.*;
import java.util.*;

import javax.swing.*;
//import javax.swing.event.*;

import com.topcoder.client.contestApplet.*;
import com.topcoder.client.contestApplet.common.*;
import com.topcoder.client.contestApplet.widgets.*;
import com.topcoder.client.contestant.view.*;
import com.topcoder.client.contestant.*;
import com.topcoder.netCommon.contest.*;
import com.topcoder.netCommon.contestantMessages.response.data.*;
import com.topcoder.shared.problem.*;

/**
 * UserAssignmentPanel
 */
public class UserAssignmentPanel extends JPanel implements ChallengeView, AssignmentView {

    /** Parent frame */
    private ContestApplet ca = null;

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

    /**
     * Constructor
     *
     * @param   ca      Parent frame
     */
    public UserAssignmentPanel(ContestApplet ca, RoomModel room) {
        this.room = room;
        this.ca = ca;
        createProblems();
    }

    /**
     * Creates the contests of the panel
     */
    private void create() {
        GridBagConstraints gbc = new GridBagConstraints();
        JButton commitButton = new JButton("Commit");
        ProblemModel[] problemModels = room.getRoundModel().getProblems(room.getDivisionID());
        ComponentAssignmentData cad = ca.getModel().getComponentAssignmentData();

        this.setBorder(Common.getTitledBorder("User Assignment"));

        if (problemSet == null) {
            System.err.println("No problem set specified");
            return;
        }

        combos = new JComboBox[problems.length][headers.length - 1];
        userModel = new SortedComboBoxModel[problems.length][headers.length - 1];

        setLayout(new GridBagLayout());

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
            JLabel jl = new JLabel(problems[i] + ":");
            jl.setForeground(Common.STATUS_COLOR);
            gbc.anchor = GridBagConstraints.CENTER;
            Common.insertInPanel(jl, this, gbc, 0, y, 1, 1, 1.0, 0.0);
            Common.insertInPanel(new JLabel(" "), this, gbc, 0, y + 1, 1, 1, 0.0, 0.0);
            gbc.fill = GridBagConstraints.NONE;
            gbc.anchor = GridBagConstraints.CENTER;
            for (j = 0; j < ((ProblemModel) problemSet.get(i)).getComponents().length; j++) {
                jl = new JLabel(components[i][j]);
                Common.insertInPanel(jl, this, gbc, j + 1, y, 1, 1, 0.0, 1.0);
                jl.setForeground(Common.STATUS_COLOR);
                combos[i][j] = Common.createComboBox();
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
                combos[i][j].setFont(new Font("SansSerif", Font.PLAIN, 12));
                combos[i][j].setPreferredSize(new Dimension(90, 20));
                combos[i][j].setEditable(false);
                combos[i][j].setBackground(Common.BG_COLOR);
                Common.insertInPanel(combos[i][j], this, gbc, j + 1, y + 1, 1, 1, 1.0, 1.0);
            }
            for (; j < headers.length - 1; j++) {
                combos[i][j] = null;
                userModel[i][j] = null;
                Common.insertInPanel(new JLabel(""), this, gbc, j + 1, y, 1, 1, 1.0, 0.0);
                Common.insertInPanel(new JLabel(""), this, gbc, j + 1, y + 1, 1, 1, 1.0, 0.0);
            }
            y += 2;
        }

        gbc.fill = GridBagConstraints.NONE;
        gbc.anchor = GridBagConstraints.EAST;
        Common.insertInPanel(commitButton, this, gbc, headers.length, 0, 1, y, 10.0, 0.0);
        commitButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                commit();
            }
        });
    }

    /**
     * Commits changes to team assignments to server.
     */
    public void commit() {
        RoundModel round = room.getRoundModel();
        ComponentAssignmentData cad = ca.getModel().getComponentAssignmentData();
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
        ca.getRequester().requestAssignComponents(cad);
    }

    // ChallengeView
    public void setTable(ArrayList table, ArrayList ranks) {
    }

    public void updateRow(String handle, ArrayList data) {
    }

    public void updateCell(String handle, int componentID, Object status) {
    }

    public void setArguments(DataType[] args, String msg) {
    }

    public void setChallengeProblem(Problem problem) {
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
        for (int i = 0; i < ca.getModel().getComponentAssignmentData().getTeamMembers().size(); i++) {
            this.users.add(ca.getModel().getComponentAssignmentData().getTeamMembers().get(i));
        }
        create();
    }

    public void updateChallengeTable(RoomModel room) {
    }
}
