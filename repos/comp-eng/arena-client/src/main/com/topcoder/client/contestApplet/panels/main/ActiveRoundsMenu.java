/*
 * User: Michael Cervantes
 * Date: Aug 8, 2002
 * Time: 3:43:26 PM
 */
package com.topcoder.client.contestApplet.panels.main;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Arrays;
import java.util.Comparator;

import javax.swing.JMenuItem;

import com.topcoder.client.contestApplet.ContestApplet;
import com.topcoder.client.contestApplet.rooms.RoomManager;
import com.topcoder.client.contestApplet.uilogic.frames.DivSummaryFrame;
import com.topcoder.client.contestApplet.uilogic.frames.RoundScheduleFrame;
import com.topcoder.client.contestApplet.uilogic.panels.IntermissionPanelManager;
import com.topcoder.client.contestant.Contestant;
import com.topcoder.client.contestant.RoomModel;
import com.topcoder.client.contestant.RoundModel;
import com.topcoder.client.contestant.view.PhaseListener;
import com.topcoder.client.contestant.view.RoomListListener;
import com.topcoder.client.contestant.view.RoundView;
import com.topcoder.netCommon.contest.ContestConstants;

public class ActiveRoundsMenu extends TCMenu implements RoundView {

    private ContestApplet contestApplet;
    private Contestant contestantModel;
    private RoomManager roomManager;
    private boolean gotRounds = false;

    private static final String NO_ACTIVE_CONTESTS = "No Active Contests";
    private JMenuItem dummy = new TCMenuItem(NO_ACTIVE_CONTESTS);

    private boolean enabled = true;
    
    public void setPanelEnabled(boolean on) {
        enabled = on;
        for(int i = 0; i < this.getItemCount(); i++) {
            JMenuItem itm = this.getItem(i);
            if(itm instanceof SubMenu) {
                ((SubMenu)itm).setPanelEnabled(on);
            }
        }
    }
    
    public ActiveRoundsMenu(String s, int width, int height, char mnemonic, ContestApplet contestApplet) {
        super(s, width, height, mnemonic);
        this.contestApplet = contestApplet;
        this.contestantModel = contestApplet.getModel();
        this.roomManager = contestApplet.getRoomManager();
        dummy.setEnabled(false);
        add(dummy);
    }

    public void clearRoundList() {
        this.removeAll();
        gotRounds = false;
        add(dummy);
    }

    public void updateActiveRoundList(Contestant model) {
        clearRoundList();
        RoundModel[] rounds = model.getActiveRounds();
        Arrays.sort(rounds, new Comparator() {
                public int compare(Object o1, Object o2) {
                    int i = ((RoundModel) o1).getRoundType().getRatingType() - ((RoundModel) o2).getRoundType().getRatingType();
                    if (i != 0) {
                        return i;
                    }

                    return ((RoundModel) o1).getRoundID().compareTo(((RoundModel) o2).getRoundID());
                }
            });
        int typeId = -1;
        for (int i = 0; i < rounds.length; i++) {
            if (typeId == -1) {
                typeId = rounds[i].getRoundType().getRatingType();
            } else if (typeId != rounds[i].getRoundType().getRatingType()) {
                addSeparator();
                typeId = rounds[i].getRoundType().getRatingType();
            }
            newActiveRound(rounds[i]);
        }
    }

    private void newActiveRound(RoundModel roundModel) {
        if (!gotRounds) {
            this.removeAll();
            gotRounds = true;
        }
        add(new SubMenu(roundModel));
    }

    //TODO create 2 classes. One for Algo rounds and other for MM rounds
    private final class SubMenu extends TCMenu implements PhaseListener, RoomListListener {

        private boolean enabled = true;
        
        public void setPanelEnabled(boolean on) {
            enabled = on;
            if(divFrame != null) {
                divFrame.setPanelEnabled(on);
            }
        }
        
        private final RoundModel roundModel;

        private TCMenuItem statementItem = null;
        private final TCMenuItem registerEnterItem = new TCMenuItem("Register", 'g');
        private final TCMenuItem enterItem = new TCMenuItem("Enter", 'n');
        private final TCMenuItem registrantsItem = new TCMenuItem("Registrants", 'n');
        private final TCMenuItem scheduleItem = new TCMenuItem("Schedule", 's');
        private final TCMenuItem adminRoomItem = new TCMenuItem("Admin Room", 'a');
        private final TCMenuItem myTeamInfoItem = new TCMenuItem("My Team Info", 'm');
        private final TCMenuItem teamsInfoItem = new TCMenuItem("Teams Info", 't');
        //private final TCMenuItem testGetSourceItem = new TCMenuItem("Test Get Source",'s');
        private final TCMenu coderRoomsMenu = new TCMenu("Rooms", 'r');

        private boolean divOpen = false;
        private DivSummaryFrame divFrame = null;

        //added 2-24 rfairfax
        private final TCMenuItem divSummaryItem = new TCMenuItem("Division Summary", 'd');

        private final ActionListener moveRoomActionListener = new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                TCMenuItem menuItem = (TCMenuItem) e.getSource();
                if (!menuItem.hasUserData()) {
                    throw new IllegalStateException("Missing room model in menu item: " + menuItem);
                }
                RoomModel room = (RoomModel) menuItem.getUserData();
                roomManager.loadRoom(room.getType().intValue(), room.getRoomID().longValue(),
                        IntermissionPanelManager.MOVE_INTERMISSION_PANEL);
            }
        };


        SubMenu(final RoundModel roundModel) {
            super(roundModel.getDisplayName() );
            this.roundModel = roundModel;
            
            if (roundModel.getRoundType().isLongRound()) {
                statementItem = new TCMenuItem("Statement", 's');
                statementItem.addActionListener(new ActionListener() {
                    public void actionPerformed(ActionEvent e) {
                        statementEvent();
                    }
                });
            }
            
            registerEnterItem.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    registerEvent();
                }
            });
            enterItem.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    enterEvent();
                }
            });
            registrantsItem.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    contestantModel.getRequester().requestRegisterUsers(roundModel.getRoundID().longValue());
                }
            });
            scheduleItem.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    new RoundScheduleFrame(contestApplet, roundModel, contestApplet.getCurrentFrame()).show();
                }
            });
            if (!roundModel.getRoundType().hasDivisions()) {
                divSummaryItem.setText("Summary");
            }
            divSummaryItem.addActionListener(new ActionListener() {
               public void actionPerformed(ActionEvent e) {
                    //spawn div summary
                   if(divFrame == null || !divFrame.isVisible())
                   {
                       divFrame = new DivSummaryFrame(roundModel, contestApplet);
                       divFrame.showFrame(true);
                   }
                   else
                       divFrame.requestFocus();

               }
            });
            adminRoomItem.addActionListener(moveRoomActionListener);
            if (statementItem != null) {
                add(statementItem);
            }
            add(registerEnterItem);
            add(enterItem);
            add(registrantsItem);
            if(roundModel.getRoundTypeId().intValue() != ContestConstants.FORWARDER_LONG_ROUND_TYPE_ID &&
                    roundModel.getRoundTypeId().intValue() != ContestConstants.FORWARDER_ROUND_TYPE_ID)
            add(scheduleItem);
            //RFAIRFAX
            boolean isAdmin = contestantModel.getUserInfo().isAdmin();
            //if(isAdmin)
            add(divSummaryItem);
            
            //add(divSummaryItem);
            boolean isWeakestLinkParticipant = contestantModel.getUserInfo().isWeakestLinkParticipant();
            if (isWeakestLinkParticipant) {
                myTeamInfoItem.addActionListener(new ActionListener() {
                    public void actionPerformed(ActionEvent e) {
                        int roundId = (int) roundModel.getRoundID().longValue();
                        contestantModel.getRequester().requestWLMyTeamInfo(roundId);
                    }
                });
                add(myTeamInfoItem);
            }
            if (roundModel.getRoundTypeId().intValue() == ContestConstants.WEAKEST_LINK_ROUND_TYPE_ID) {
                teamsInfoItem.addActionListener(new ActionListener() {
                    public void actionPerformed(ActionEvent e) {
                        int roundId = (int) roundModel.getRoundID().longValue();
                        contestantModel.getRequester().requestWLTeamsInfo(roundId);
                    }
                });
                add(teamsInfoItem);
            }
            /*
            if (isWeakestLinkParticipant) {
                testGetSourceItem.addActionListener(new ActionListener() {
                    public void actionPerformed(ActionEvent e) {
                        int roundId=5101;
                        String roundName="WL1";
                        String coderName="t";
                        RoundStatsProblem[] problems={
                            new RoundStatsProblem("Simple", 249.42, 250, "249.92 points", "0 min 0 sec", 1),
                        };
                        RoundStatsResponse roundStatsResponse=new RoundStatsResponse(roundId, roundName, coderName, problems);
                        Coder coder=contestApplet.findCoder(roundId, coderName);
                        RoundStatsFrame.showFrame(contestApplet, roundStatsResponse, coder, roundId);
                    }
                });
                add(testGetSourceItem);
            }
            */
            // Don't add adminRoomItem yet
            // Don't add roomsMenu yet

            roundModel.addPhaseListener(this);
            roundModel.addRoomListListener(this);
            reset();
            roomListEvent(roundModel);
        }

        private void reset() {
            boolean isRegisterEnabled = false;
            boolean isLongRegisterEnabled = false;
            boolean isEnterEnabled = false;
            boolean showDivSummary = false;
            boolean isAdmin = contestantModel.getUserInfo().isAdmin();
            int phase = roundModel.getPhase().intValue();
            removeDialogs(phase);
            switch (phase) {
            case ContestConstants.REGISTRATION_PHASE:
                isRegisterEnabled = true;
                isLongRegisterEnabled = true;
                break;
            case ContestConstants.INACTIVE_PHASE:
            case ContestConstants.STARTS_IN_PHASE:
                break;
            case ContestConstants.ALMOST_CONTEST_PHASE:
            case ContestConstants.CODING_PHASE:
                isLongRegisterEnabled = true;
            case ContestConstants.INTERMISSION_PHASE:
            case ContestConstants.CHALLENGE_PHASE:
            case ContestConstants.VOTING_PHASE:
            case ContestConstants.TIE_BREAKING_VOTING_PHASE:
            case ContestConstants.PENDING_SYSTESTS_PHASE:
            case ContestConstants.SYSTEM_TESTING_PHASE:
            case ContestConstants.CONTEST_COMPLETE_PHASE:
                showDivSummary = roundModel.canDisplaySummary();
                isEnterEnabled = roundModel.hasCoderRooms() || (isAdmin && roundModel.hasAdminRoom());
                break;
            default:
                throw new IllegalArgumentException("Invalid phase: " + roundModel.getPhase());
            }
            if (isAdmin) {
                showDivSummary = true;
            }
            if (roundModel.getRoundType().isLongRound()) {
                isRegisterEnabled = isLongRegisterEnabled;
                statementItem.setEnabled(phase >= ContestConstants.REGISTRATION_PHASE && phase <= ContestConstants.CONTEST_COMPLETE_PHASE);
            }
            if(roundModel.getRoundTypeId().intValue() == ContestConstants.FORWARDER_LONG_ROUND_TYPE_ID) {
                isRegisterEnabled = false;
            }
            if(roundModel.getRoundTypeId().intValue() == ContestConstants.FORWARDER_ROUND_TYPE_ID) {
                isRegisterEnabled = false;
            }
            
            registerEnterItem.setEnabled(isRegisterEnabled);
            enterItem.setEnabled(isEnterEnabled);
            coderRoomsMenu.setEnabled(isEnterEnabled);
            divSummaryItem.setEnabled(isEnterEnabled && showDivSummary);
            revalidate();
            repaint();
        }

        private void removeDialogs(int phase) {
            switch (phase) {
            case ContestConstants.TIE_BREAKING_VOTING_PHASE:
                contestApplet.disposeVotingFrame();
                break;
            case ContestConstants.CONTEST_COMPLETE_PHASE:
                contestApplet.disposeTieBreakVotingFrame();
                break;
            }
        }

        private void registerEvent() {
            boolean mustRegister = false;
            boolean mustRegisterLong = false;
            boolean longRoundType = roundModel.getRoundType().isLongRound();
            
            switch (roundModel.getPhase().intValue()) {
                case ContestConstants.INACTIVE_PHASE:
                case ContestConstants.STARTS_IN_PHASE:
                    mustRegisterLong = true;
                    break;
                case ContestConstants.REGISTRATION_PHASE:
                    mustRegister = true;
                    mustRegisterLong = true;
                    break;
                case ContestConstants.ALMOST_CONTEST_PHASE:
                case ContestConstants.CODING_PHASE:
                    mustRegisterLong = true;
                case ContestConstants.INTERMISSION_PHASE:
                case ContestConstants.CHALLENGE_PHASE:
                case ContestConstants.PENDING_SYSTESTS_PHASE:
                case ContestConstants.SYSTEM_TESTING_PHASE:
                case ContestConstants.VOTING_PHASE:
                case ContestConstants.TIE_BREAKING_VOTING_PHASE:
                case ContestConstants.CONTEST_COMPLETE_PHASE:
                default:
                    if (!longRoundType || !mustRegisterLong) { 
                        throw new IllegalArgumentException("Invalid phase: " + roundModel.getPhase());
                    }
            } 
            
            if (longRoundType) {
                mustRegister = mustRegisterLong;
            }
            if (mustRegister) {
                contestantModel.getRequester().requestRegisterEventInfo(roundModel.getRoundID().longValue());
            }
        }
        
        private void statementEvent() {
            int phase = roundModel.getPhase().intValue();
            if (phase >= ContestConstants.REGISTRATION_PHASE && phase <= ContestConstants.CONTEST_COMPLETE_PHASE) {
                contestApplet.showProblemStatement(roundModel.getProblems(roundModel.getCoderRooms()[0].getDivisionID())[0]);
            } else {
                throw new IllegalArgumentException("Invalid phase: " + roundModel.getPhase());
            }
        }
        
        private void enterEvent() {
            switch (roundModel.getPhase().intValue()) {
            case ContestConstants.ALMOST_CONTEST_PHASE:
            case ContestConstants.CODING_PHASE:
            case ContestConstants.INTERMISSION_PHASE:
            case ContestConstants.CHALLENGE_PHASE:
            case ContestConstants.PENDING_SYSTESTS_PHASE:
            case ContestConstants.SYSTEM_TESTING_PHASE:
            case ContestConstants.VOTING_PHASE:
            case ContestConstants.TIE_BREAKING_VOTING_PHASE:
            case ContestConstants.CONTEST_COMPLETE_PHASE:
                roomManager.loadRoom(ContestConstants.CONTEST_ROOM, roundModel.getRoundID().longValue(), IntermissionPanelManager.MOVE_INTERMISSION_PANEL);
                break;
            default:
                throw new IllegalArgumentException("Invalid phase: " + roundModel.getPhase());
            }
        }


        public void enableRound(RoundModel round) {
            registerEnterItem.setEnabled(roundModel.getMenuStatus());
            enterItem.setEnabled(roundModel.getMenuStatus());
            reset();
        }

        public void phaseEvent(int phase, RoundModel roundModel) {
            reset();
        }

        public void updateSystestProgress(int completed, int total, RoundModel roundModel) {
        }

        public void roomListEvent(RoundModel round) {
            // clear out the menu items if they exist
            remove(adminRoomItem);
            remove(coderRoomsMenu);

            if (contestantModel.getUserInfo().isAdmin() && round.hasAdminRoom()) {
                RoomModel adminRoom = round.getAdminRoom();
                adminRoomItem.setUserData(adminRoom);
                add(adminRoomItem);
            }

            if (round.hasCoderRooms()) {
                RoomModel coderRooms[] = round.getCoderRooms();
                String names[] = new String[coderRooms.length];
                for (int i = 0; i < coderRooms.length; i++) {
                    names[i] = coderRooms[i].getName();
                }
                coderRoomsMenu.removeAll();
                coderRoomsMenu.buildIndexedCascadingMenu(names, coderRooms, moveRoomActionListener);
                add(coderRoomsMenu);
            }
            reset();
        }
    }
}
