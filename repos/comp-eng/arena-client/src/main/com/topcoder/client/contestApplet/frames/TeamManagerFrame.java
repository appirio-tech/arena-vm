/*
 * TeamManagerFrame.java
 *
 * Created on May 24, 2002, 11:14 PM
 */

package com.topcoder.client.contestApplet.frames;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.event.*;

import com.topcoder.client.contestApplet.ContestApplet;
import com.topcoder.client.contestApplet.common.*;
import com.topcoder.client.contestApplet.widgets.*;
import com.topcoder.client.contestApplet.panels.table.*;
import com.topcoder.client.contestApplet.panels.coding.*;
import com.topcoder.client.contestant.view.*;
//import com.topcoder.netCommon.contestantMessages.response.data.*;

/**
 *
 * @author  Matthew P. Suhocki (msuhocki)
 * @version
 */
public class TeamManagerFrame extends JFrame {

    /*
     * User types
     */
    public static final int MEMBER = 1;
    public static final int CAPTAIN = 2;

    private ContestApplet contestApplet = null;
    private boolean once = true; // tracks whether the frame has already been shown
    private int userType;

    private TeamListTablePanel tltpTeams = null;
    private CodingTimerPanel ctpTime = null;
    private UserMapTablePanel utpAvailable = null;
    private UserMapTablePanel utpMembers = null;

    private JButton btnAdd = null;
    private JButton btnRemove = null;

    private JButton btnJoin = null;
    private JButton btnLeave = null;

    private MouseLessTextArea msg = null;

    private static final String helpCaptain =
            "During registration, you can edit your team.  The 'Available' list " +
            "will show members who wish to be added to your team.  The 'Members' " +
            "list will show members currently selected for your team.  You can " +
            "view the members on any team by selecting the team from the team list.";
    private static final String helpMember =
            "During registration, you can join any team to make a request to be " +
            "added.  You may leave a team at any time during registration.";

    /**
     * Creates new TeamManagerFrame
     *
     * @param   contestApplet      parent contest applet
     * @param   type        user type
     * @see     #MEMBER
     * @see     #CAPTAIN
     */
    public TeamManagerFrame(ContestApplet contestApplet, int type) {
        super("TopCoder Competition Arena - Team Management");
        this.contestApplet = contestApplet;

        create();
        setUserType(type);

        setSize(600, 400);
    }

    public void setUserType(int type) {
        userType = type;
        if (userType == CAPTAIN) {
            btnAdd.setVisible(true);
            btnRemove.setVisible(true);
            btnJoin.setVisible(false);
            btnLeave.setVisible(false);
            msg.setText(helpCaptain);
        } else {
            btnAdd.setVisible(false);
            btnRemove.setVisible(false);
            btnJoin.setVisible(true);
            btnLeave.setVisible(true);
            msg.setText(helpMember);
        }
    }

    public void showFrame(boolean enabled) {
        if (once) { // only position if frame has not yet been opened
            Common.setLocationRelativeTo(contestApplet.getMainFrame(), this);
            once = false;
        }
        show();
    }

    public void show() {
        //ca.getRequester().requestGetTeamList();
        super.show();
    }

    public void hide() {
        //ca.getRequester().requestCloseTeamList();
        super.hide();
    }

    public void create() {
        // create the constraints
        GridBagConstraints gbc = Common.getDefaultConstraints();

        msg = new MouseLessTextArea("");

        // create the panels
        CodingTimerPanel ctp = new CodingTimerPanel(contestApplet);
        JPanel mp = Common.createMessagePanel("Instructions", msg, 0, 100, Common.BG_COLOR);
        JPanel tp = createTeamsPanel();
        JPanel msp = createMemberSelectionPanel();
        //JPanel adp = createAcceptDeclinePanel();

        getContentPane().setLayout(new GridBagLayout());
        getContentPane().setBackground(Common.WPB_COLOR);
        tp.setPreferredSize(new Dimension(350, 200));

        gbc.insets = new Insets(25, 5, 5, 15);
        gbc.fill = GridBagConstraints.NONE;
        Common.insertInPanel(ctp, getContentPane(), gbc, 1, 0, 1, 1, 0.0, 0.0);

        gbc.insets = new Insets(0, 5, 0, 15);
        gbc.fill = GridBagConstraints.BOTH;
        Common.insertInPanel(mp, getContentPane(), gbc, 0, 0, 1, 2, 1.0, 0.0);

        gbc.insets = new Insets(5, 15, 5, 15);
        gbc.fill = GridBagConstraints.BOTH;
        Common.insertInPanel(tp, getContentPane(), gbc, 0, 2, 2, 1, 1.0, 1.0);

        gbc.insets = new Insets(5, 15, 15, 15);
        gbc.fill = GridBagConstraints.BOTH;
        Common.insertInPanel(msp, getContentPane(), gbc, 0, 3, 2, 1, 1.0, 1.0);

        /*gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.NONE;
        Common.insertInPanel(adp, getContentPane(), gbc, 0, 4, 2, 1, 1.0, 0.0);*/

        ctpTime = ctp;

    }

    private JPanel createTeamsPanel() {
        JPanel jp = new JPanel(new BorderLayout());
        tltpTeams = new TeamListTablePanel(contestApplet);

        jp.add(tltpTeams, BorderLayout.CENTER);
        tltpTeams.getTable().getSelectionModel().addListSelectionListener(new ListSelectionListener() {
            public void valueChanged(ListSelectionEvent e) {
                System.out.println("valueChanged");
                utpAvailable.setTeam(tltpTeams.getSelectedTeam());
                utpMembers.setTeam(tltpTeams.getSelectedTeam());
            }
        });

        //if (userType != CAPTAIN) {
        JPanel jlp = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        btnJoin = Common.getButton("Join");
        btnJoin.setMinimumSize(new Dimension(75, 25));
        btnJoin.setPreferredSize(new Dimension(75, 25));
        btnJoin.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                joinButtonEvent();
            }
        });

        btnLeave = Common.getButton("Leave");
        btnLeave.setMinimumSize(new Dimension(75, 25));
        btnLeave.setPreferredSize(new Dimension(75, 25));
        btnLeave.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                leaveButtonEvent();
            }
        });

        jlp.add(btnJoin);
        jlp.add(btnLeave);
        jp.add(jlp, BorderLayout.SOUTH);
        //}
        return jp;
    }


    private JPanel createMemberSelectionPanel() {
        GridBagConstraints gbc = new GridBagConstraints();
        JPanel jp = new JPanel(new GridBagLayout());
        utpAvailable = new UserMapTablePanel(contestApplet, "Avaiable");
        utpMembers = new UserMapTablePanel(contestApplet, "Team Members");

        utpAvailable.setMinimumSize(new Dimension(160, 0));
        utpAvailable.setPreferredSize(new Dimension(200, 200));
        utpMembers.setMinimumSize(new Dimension(160, 0));
        utpMembers.setPreferredSize(new Dimension(200, 200));

        utpAvailable.setBackground(Common.WPB_COLOR);
        utpMembers.setBackground(Common.WPB_COLOR);

        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.BOTH;
        Common.insertInPanel(utpAvailable, jp, gbc, 0, 0, 1, 1, 1.0, 1.0);

        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.BOTH;
        Common.insertInPanel(utpMembers, jp, gbc, 2, 0, 1, 1, 1.0, 1.0);

        //if (userType == CAPTAIN) {
        BorderLayout bl = new BorderLayout();
        bl.setVgap(5);
        JPanel arp = new JPanel(bl);

        btnAdd = Common.getButton("Add >>");
        btnAdd.setToolTipText("Add avaiable member to your team");
        btnAdd.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                addButtonEvent();
            }
        });
        btnAdd.setMnemonic(KeyEvent.VK_A);

        btnRemove = Common.getButton("<< Remove");
        btnRemove.setToolTipText("Remove member from your team");
        btnRemove.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                removeButtonEvent();
            }
        });
        btnRemove.setMnemonic(KeyEvent.VK_R);

        arp.add(btnAdd, BorderLayout.NORTH);
        arp.add(btnRemove, BorderLayout.SOUTH);

        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.NONE;
        Common.insertInPanel(arp, jp, gbc, 1, 0, 1, 1, 0.0, 0.0);
        //}

        return jp;
    }

    // Captain buttons
    private void addButtonEvent() {
        contestApplet.getModel().getRequester().requestAddTeamMember(utpAvailable.getSelectedUserHandle());
        //utpAvailable.moveSelectedToUTP(utpMembers);
    }

    private void removeButtonEvent() {
        contestApplet.getModel().getRequester().requestRemoveTeamMember(utpMembers.getSelectedUserHandle());
        //utpMembers.moveSelectedToUTP(utpAvailable);
        //setUserType(MEMBER);
    }

    // Member buttons
    private void joinButtonEvent() {
        contestApplet.getModel().getRequester().requestJoinTeam(tltpTeams.getSelectedTeam());
    }

    private void leaveButtonEvent() {
        //setUserType(CAPTAIN);
        contestApplet.getModel().getRequester().requestLeaveTeam(tltpTeams.getSelectedTeam());
    }


    public CodingTimerPanel getTimerPanel() {
        return ctpTime;
    }


    public TeamListView getTeamListView() {
        return tltpTeams;
    }

    public UserListListener getAvailableListView() {
        return utpAvailable;
    }

    public UserListListener getMemberListView() {
        return utpMembers;
    }


/*
    public void test() {
        utpAvailable.addToUserList(new UserListItem("msuhocki", 5000, "Team 1"));
        utpAvailable.addToUserList(new UserListItem("guest100", 0, "Team 1"));
        utpAvailable.addToUserList(new UserListItem("guest101", 1499, "Team 2"));
        utpAvailable.addToUserList(new UserListItem("guest105", 1500, "Team 2"));
        utpAvailable.addToUserList(new UserListItem("asdf", 1699, "Team 2"));

        tltpTeams.updateTeamList(new TeamListInfo("Team 1", 1500, "msuhocki",    -1, 7, 3, ""));
        tltpTeams.updateTeamList(new TeamListInfo("Team 2", 3000, "guest509",  1000, 7, 4, ""));
        tltpTeams.updateTeamList(new TeamListInfo("Team 3",   -1, "guest169",  2000, 1, 5, "Pending"));
    }


    public static void main(String[] argv) {
        ContestApplet ca = new ContestApplet("www.topcoder.com", 5001, "", "");
        //ca.getMainFrame().show();
        TeamManagerFrame tmf = new TeamManagerFrame(ca, CAPTAIN);

        tmf.setSize(720, 640);
        //tmf.create();
        tmf.test();
        tmf.showFrame(true);
    }
*/
}
