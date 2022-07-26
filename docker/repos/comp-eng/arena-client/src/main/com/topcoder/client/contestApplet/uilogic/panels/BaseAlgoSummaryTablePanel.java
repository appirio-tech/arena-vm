package com.topcoder.client.contestApplet.uilogic.panels;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.swing.JFrame;
import javax.swing.JTable;
import javax.swing.SwingUtilities;
import javax.swing.Timer;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.JTableHeader;

import com.topcoder.client.SortElement;
import com.topcoder.client.SortedTableModel;
import com.topcoder.client.contestApplet.ContestApplet;
import com.topcoder.client.contestApplet.common.Common;
import com.topcoder.client.contestApplet.uilogic.frames.FrameLogic;
import com.topcoder.client.contestApplet.uilogic.frames.SourceViewer;
import com.topcoder.client.contestApplet.uilogic.panels.table.ChallengeProblemEntry;
import com.topcoder.client.contestApplet.uilogic.panels.table.UserNameEntry;
import com.topcoder.client.contestApplet.uilogic.views.ChallengeViewLogic;
import com.topcoder.client.contestApplet.uilogic.views.PrettyToggleProvider;
import com.topcoder.client.contestApplet.uilogic.views.ViewerLogic;
import com.topcoder.client.contestApplet.widgets.AlgorithmCoderComponentRenderer;
import com.topcoder.client.contestant.Coder;
import com.topcoder.client.contestant.CoderComponent;
import com.topcoder.client.contestant.ProblemComponentModel;
import com.topcoder.client.contestant.ProblemModel;
import com.topcoder.client.contestant.RoomModel;
import com.topcoder.client.contestant.RoundModel;
import com.topcoder.client.ui.UIComponent;
import com.topcoder.client.ui.UIPage;
import com.topcoder.client.ui.event.UIActionListener;
import com.topcoder.client.ui.event.UIMouseAdapter;
import com.topcoder.netCommon.contest.ContestConstants;
import com.topcoder.netCommon.contest.ResultDisplayType;
import com.topcoder.netCommon.contest.round.RoundProperties;
import com.topcoder.netCommon.contest.round.text.ComponentNameBuilder;
import com.topcoder.netCommon.contestantMessages.response.data.ComponentChallengeData;

/**
 * <p>
 * this is an base panel class to view the SRM summary problems.
 * </p>
 *
 * <p>
 * Changes in version 1.0 (TC Competition Engine - Fix viewing code error in TC Arena v1.0):
 * <ol>
 * <li>Added {@link #getUniqueClickStatusKey(com.topcoder.client.contestant.CoderComponent,String writer)}()} method.</li>
 * <li>Added {@link #canOpenSource(com.topcoder.client.contestant.CoderComponent,String writer)} ()} method.</li>
 * <li>Updated {@link #sourcePopupEvent()} to control the click status</li>
 * <li>Updated {@link #sourceViewerClosing()} to remove the click status when closing the window</li>
 * <li>Updated {@link #mouseClickEvent(java.awt.event.MouseEvent)} to limit the clickCount to only handle single,double click</li>
 * </ol>
 * </p>
 *
 * @author savon_cn
 * @since 1.0
 */
public abstract class BaseAlgoSummaryTablePanel extends AbstractSummaryTablePanel implements ChallengeViewLogic {
    protected final ContestApplet ca;
    // Table Model/View/Controller Definitions
    protected ChallengeTableModel tableModel;
    protected UIComponent table;
    private boolean once = true;
    private boolean enabled = true;
    private int lastColumnIndexFix;
    private int columnCount;
    private int maxProblemColumn;
    private Class[] tableModelClasses;
    private String[] tableModelHeaders;
    private int oldCompID = 0;
    private ArrayList oldArgs = null;
    /**
     * represent the problem has already open.
     */
    private static final Boolean PROBLEM_OPENED = new Boolean(true);
    /**
     * this is the click status controller.
     * key=problemId_writer, value=true indicate that the problem has already been opened
     * cannot be opened again
     */
    private final Map clickStatusController = new HashMap();
    
    public void setPanelEnabled(boolean on) {
        enabled = on;
        if(src != null)
            src.setPanelEnabled(on);
    }

    private ProblemModel currentProblemInfo;

    /**
     * A source viewer. Made it static to fix the challenge bug (blank code and challenge button in a foreign room).
     * Anyway we had one viewer for each challengeTablePanel so it's ok to have one viewer across all challengeTablePanel
     * instances (i.e. through summary and leaderboard).
     */

    /**
     * Pops 1/6/2004 - made it non-static because of the ways the listeners worked.  This created
     * a challenge bug that allowed you to look at one problem but the internal component was for a
     * different problem in a different room (because it's listener invoked updateProblemModel after
     * the current room and it reset the source viewer's problem component to that one!).
     */
    private SourceViewer src;
    private UIComponent contestPopup;
    private UIComponent otherContestPopup;

    private final CoderComponent.Listener myCoderComponentListener = new CoderComponent.Listener() {
            public void coderComponentEvent(CoderComponent coderComponent) {
                if (coderComponent.hasSourceCode()) {
                    // TODO clean this up
                    if (currentProblemInfo == null) {
                        throw new IllegalStateException("Missing problem info");
                    }
                    ComponentNameBuilder nameBuilder = getRoundModel().getRoundType().getComponentNameBuilder();
                    String componentName =  nameBuilder.longNameForComponent(
                            coderComponent.getComponent().getClassName(), 
                            coderComponent.getComponent().getPoints().doubleValue(), 
                            getRoundModel().getRoundProperties());
                    src.clear();
                    src.setCode(coderComponent.getSourceCode(), coderComponent.getSourceCodeLanguage());
                    src.setTitle(
                                 coderComponent.getCoder().getHandle() + "'s " +
                                 componentName +" (" +
                                 coderComponent.getSourceCodeLanguage().getName() +
                                 ")"
                                 );
                } else {
                    //they tried to open a problem in a bad room, take the listener off
                    sourceViewerClosing();
                }
            }
        };

    private final ProblemModel.Listener myProblemModelListener = new ProblemModel.Listener() {
            public void updateProblemModel(final ProblemModel problemModel) {
                if (problemModel.hasProblemStatement()) {
                    ca.getInterFrame().hideMessage();
                    if (src == null) {
                        throw new IllegalStateException("Source viewer not initialized!");
                    }

                    src.setTitle(problemModel.getName());

                    if (once) {
                        // Common.setLocationRelativeTo(frame, src);  <- don't do this - screws up the saved position
                        once = false;
                    }

                    //src.pack();  <- don't do this - screws up the saved position
                    src.show();

                    src.setProblem(problemModel);

                    challengeHandle = currentComponent.getCoder().getHandle();
                    src.setCoderComponent(currentComponent);
                    src.setWriter(currentComponent.getCoder().getHandle());

                    // do this after show so we can be sure the button is created.
                    if(currentComponent.getComponent().getComponentTypeID().intValue() == 1) { //main component
                        src.setChallengeable(true);
                    } else {
                        src.setChallengeable(false);
                    }
                    src.refreshStatement();
                    //empty out code, for default display
                    //src.setCode("", BaseLanguage.getLanguage(JavaLanguage.ID));
                } else {
                    throw new IllegalStateException("Missing statement for problem " + problemModel);
                }
            }

            public void updateProblemModelReadOnly(ProblemModel problem) {
            }
        };

    private String challengeHandle;
    private CoderComponent currentComponent;
    protected UIPage page;
    private FrameLogic frame;
    protected UIComponent panel;
    private boolean update;
    protected TableModelListener tableModelListener;

    public BaseAlgoSummaryTablePanel(ContestApplet ca, FrameLogic cr, boolean update, UIPage page) {
        this.ca = ca;
        this.frame = cr;
        this.update = update;
        this.page = page;
    }

    protected void init(UIComponent contestPopup, UIComponent otherContestPopup, UIComponent table, UIComponent panel,
                        UIComponent sourceMenu, UIComponent infoMenu, UIComponent historyMenu) {
        this.contestPopup = contestPopup;
        this.otherContestPopup = otherContestPopup;
        this.table = table;
        this.panel = panel;
        prepareForTableModel();
        this.tableModel = new ChallengeTableModel();
        tableModelListener = new TableModelListener() {
                public void tableChanged(TableModelEvent e) {
                    if (currentProblemInfo != null) {
                        if (currentComponent.getStatus().intValue() == ContestConstants.CHALLENGE_SUCCEEDED) {
                            src.notifyChallengeSucceeded(currentComponent.getCoder().getHandle(), Common.formatNoFractions(currentProblemInfo.getComponents()[0].getPoints()));
                        }
                    }
                    updateRoomLeader();
                }
            };
        
        tableModel.addTableModelListener(tableModelListener);
        infoMenu.addEventListener("action", new UIActionListener() {
                public void actionPerformed(ActionEvent e) {
                    infoPopupEvent();
                }
            });
        historyMenu.addEventListener("action", new UIActionListener() {
                public void actionPerformed(ActionEvent e) {
                    historyPopupEvent();
                }
            });
        sourceMenu.addEventListener("action", new UIActionListener() {
                public void actionPerformed(ActionEvent e) {
                    sourcePopupEvent();
                }
            });
        table.setProperty("model", tableModel);
        initTable();
        table.addEventListener("mouse", new UIMouseAdapter() {
                public void mouseClicked(MouseEvent e) {
                    if(enabled) {
                        mouseClickEvent(e);
                        otherMouseClickEvent(e);
                    }
                }
            });

        ((JTableHeader) table.getProperty("tableheader")).addMouseListener(new UIMouseAdapter() {
                public void mouseClicked(MouseEvent e) {
                    headerClickEvent(e);
                }
            });
    }

    public UIComponent getTable() {
        return table;
    }

    private void showContestPopup(MouseEvent e) {
        contestPopup.performAction("show", new Object[] {e.getComponent(), new Integer(e.getX()), new Integer(e.getY())});
    }

    private void mouseClickEvent(MouseEvent e) {
        JTable eventTable = ((JTable) e.getComponent());
        int r = eventTable.rowAtPoint(e.getPoint());
        int c = eventTable.columnAtPoint(e.getPoint());
        eventTable.setRowSelectionInterval(r, r);
        eventTable.setColumnSelectionInterval(c, c);

        // If any column BUT the score column
        if ((c > 2 && c < maxProblemColumn)) {
            Coder coder = tableModel.getCoder(r);
            int index = 2;
            //double totalPoints;
            ProblemModel[] problems = getRoundModel().getProblems(getDivisionID());
            CoderComponent component = null;
            boolean done = false;
            for (int i = 0; !done && i < problems.length; i++) {
                for (int j = 0; j < problems[i].getComponents().length; j++) {
                    index++;
                    if (index == c) {
                        component = coder.getComponent(problems[i].getComponents()[j].getID());
                        done = true;
                    }
                }

                if (problems[i].getProblemType().intValue()
                    == ContestConstants.TEAM_PROBLEM_TYPE_ID) {
                    index++;
                    if (index == c) {
                        component = null;
                        done = true;
                    }
                }
            }

            if (component != null && component.getStatus().intValue() >= ContestConstants.LOOKED_AT) {
                if (SwingUtilities.isRightMouseButton(e)) {
                    showContestPopup(e);
                } else if ((e.getClickCount() == 2) &&
                           SwingUtilities.isLeftMouseButton(e)) { // we only need to handle the double-click here
                    sourcePopupEvent();
                } else if ((e.getClickCount() == 1) &&
                           SwingUtilities.isLeftMouseButton(e)) {
                    // display score if status is currently displayed
                    getCellRenderer(eventTable, c).toggleDisplayTypeForComponent(component, getRoundModel().getRoundProperties().getAllowedScoreTypesToShow());
                    tableModel.fireTableCellUpdated(r, c);
                }
            }
        }
    }

    protected AlgorithmCoderComponentRenderer getCellRenderer(JTable eventTable, int c) {
        return ((AlgorithmCoderComponentRenderer) eventTable.getColumnModel().getColumn(c).getCellRenderer());
    }

    private void otherMouseClickEvent(MouseEvent e) {
        int r = ((JTable) e.getComponent()).rowAtPoint(e.getPoint());
        int c = ((JTable) e.getComponent()).columnAtPoint(e.getPoint());
        ((JTable) e.getComponent()).setRowSelectionInterval(r, r);
        ((JTable) e.getComponent()).setColumnSelectionInterval(c, c);

        // make sure it doesn't show up on either the users/scores columns or
        // if there was no code submitted for this problem

        // Any NON problem column\
            if (c < 3 || c >= maxProblemColumn) {
                if (SwingUtilities.isRightMouseButton(e)) {
                    otherContestPopup.performAction("show", new Object[] {e.getComponent(), new Integer(e.getX()), new Integer(e.getY())});
                } else if ((e.getClickCount() > 1) &&
                           SwingUtilities.isLeftMouseButton(e)) {
                    infoPopupEvent();
                }
            }
    }

    private void headerClickEvent(MouseEvent e) {
        if (SwingUtilities.isLeftMouseButton(e)) {

            int col = ((JTableHeader) table.getProperty("TableHeader")).columnAtPoint(e.getPoint());

            if (col == -1) return;
            tableModel.sort(
                            col,
                            (e.getModifiers() & MouseEvent.SHIFT_MASK) > 0
                            );
            ((JTableHeader) table.getProperty("TableHeader")).repaint();
        }
    }

    private void infoPopupEvent() {
        int index = ((Integer) table.getProperty("SelectedRow")).intValue();
        UserNameEntry entry = (UserNameEntry) table.performAction("getValueAt", new Object[] {new Integer(index), new Integer(2)});
        challengeHandle = entry.getName();
        ca.setCurrentFrame((JFrame) frame.getFrame().getEventSource());
        ca.requestCoderInfo(challengeHandle, entry.getUserType());
    }

    private void historyPopupEvent() {
        int index = ((Integer) table.getProperty("SelectedRow")).intValue();
        if (index >= 0) {
            String handle = tableModel.getCoder(index).getHandle();
            ca.setCurrentFrame((JFrame) frame.getFrame().getEventSource());
            ca.requestCoderHistory(handle, getRoomByCoder(handle).getRoomID().longValue(), tableModel.getCoder(index).getUserType());
        }
    }

    private void sourcePopupEvent() {

        int r = ((Integer) table.getProperty("SelectedRow")).intValue();
        int c = ((Integer) table.getProperty("SelectedColumn")).intValue();

        // Any column EXCEPT the score column
        if (c > 2 && c < maxProblemColumn) {
            CoderComponent coderComponent = tableModel.getCoderComponent(r, c);
            if (coderComponent.getStatus().intValue() >
                ContestConstants.NOT_OPENED) {
                //this is current clicked code writer
                String codeWriter = tableModel.getValueAt(r, 2).toString();
                
                if(!canOpenSource(coderComponent,codeWriter))
                    return;
                
                if(coderComponent.getStatus().intValue() > ContestConstants.LOOKED_AT)
                    createNewSourceViewer(true);
                else
                    createNewSourceViewer(false);
                //            System.out.println("----> sourcePopup event." + (currentComponent==null ? "null" : currentComponent.getCoder().getHandle() + "-" + currentComponent.getPoints()));
                //            System.out.println("----> sourcePopup event." + (coderComponent==null ? "null" : coderComponent.getCoder().getHandle() + "-" + coderComponent.getPoints()));                
                currentComponent = coderComponent;
                currentProblemInfo =
                    currentComponent.getComponent().getProblem();
                // request problem information
                challengeHandle = codeWriter;
                
                ca.setCurrentFrame((JFrame) frame.getFrame().getEventSource());
                boolean prettyToggle = false;
                if (frame instanceof PrettyToggleProvider) {
                    prettyToggle = ((PrettyToggleProvider) frame).getPrettyToggle();
                }

                // So we are notified when the stmt + code is updated
                currentComponent.addListener(myCoderComponentListener);
                currentProblemInfo.addListener(myProblemModelListener);
                ca.setCurrentFrame((JFrame) frame.getFrame().getEventSource());
                ca.getInterFrame().showMessage(
                                               "Fetching problem...",
                                               (JFrame) frame.getFrame().getEventSource(), ContestConstants.GET_CHALLENGE_PROBLEM
                                               );
                long problemID = currentComponent.getComponent().getID().longValue();
                //it will add element when view the source code
                SourceViewer.PROBLEM_STATE.add(problemID+"_"+codeWriter);
                ca.getModel().getRequester().requestChallengeComponent(
                                                                       problemID,
                                                                       prettyToggle,
                                                                       getRoomByCoder(challengeHandle).getRoomID().longValue(),
                                                                       challengeHandle
                                                                       );
            }
        }
    }

    public void setOldArgs(ArrayList args, int compID) {
        oldArgs = args;
        oldCompID = compID;

    }

    public void doChallenge(String writer, CoderComponent coderComponent, FrameLogic parentFrame) {
        if(!enabled)
            return;
        
        //            System.out.println("----> doChallenge event." + (currentComponent==null ? "null" : currentComponent.getCoder().getHandle() + "-" + currentComponent.getPoints()));
        //            System.out.println("----> doChallenge event." + (coderComponent==null ? "null" : coderComponent.getCoder().getHandle() + "-" + coderComponent.getPoints()));
        ComponentChallengeData ccd = coderComponent.getComponent().getComponentChallengeData();
        challengeHandle = writer;
        this.currentComponent = coderComponent;  // todo why reset?
        ca.setCurrentFrame((JFrame) frame.getFrame().getEventSource());
        String message = "You are entering arguments to challenge the " + ccd.getMethodName()
                + " method of the " + ccd.getClassName()
                + " class.  If successful, this challenge will be worth " + ContestConstants.EASY_CHALLENGE
                + " points. If it is unsuccessful, it will cost you "
                + ContestConstants.UNSUCCESSFUL_CHALLENGE + " points.";
        //ArrayList info = Common.showArgInput(
        //        message,
        //        ccd.getParamTypes(),
        //        frame,
        //        true,
        //        ccd.getParameterNames()
        //);
        if(oldCompID != coderComponent.getComponent().getID().intValue()) {
            Common.showArgInput(
                    ca,
                    message,
                    ccd.getParamTypes(),
                    null,
                    parentFrame,
                    true,
                    //ccd.getParameterNames(),
                    coderComponent.getComponent()
            );
        } else {
            Common.showArgInput(
                    ca,
                    message,
                    ccd.getParamTypes(),
                    oldArgs,
                    parentFrame,
                    true,
                    //ccd.getParameterNames(),
                    coderComponent.getComponent()
            );
        }
    }

    /*
     * bold the room leader in the challenge table
     */
    private void updateRoomLeader() {
        tableModel.updateRoomLeader();
        panel.performAction("repaint");
    }
    
    /*
     * Toggles display to show status or scores of all problem submissions.
     */
     public void updateView(ResultDisplayType type) {
         JTable t =  (JTable) getTable().getEventSource();
         for (int i = 3; i < maxProblemColumn; i++) {
             getCellRenderer(t, i).setDisplayType(type);
        }
        tableModel.fireTableDataChanged();
     }

    protected void prepareForTableModel() {
        RoundModel round = getRoundModel();
        if (!round.hasProblems(getDivisionID())) {
            throw new IllegalStateException(
                    "Can't build challenge table model, " +
                    "no problems for round: " + round
            );
        }
        ProblemModel[] problems = round.getProblems(getDivisionID());
        boolean hasTeamProblems = false;
        for (int i = 0; i < problems.length; i++) {
            if (problems[i].getProblemType().intValue() == ContestConstants.TEAM_PROBLEM_TYPE_ID) {
                hasTeamProblems = true;
            }
        }

        String componentPrefix = hasTeamProblems ? "C: " : "";
        String problemPrefix = "P: ";

        ArrayList al_tableModelClasses = new ArrayList();
        ArrayList al_tableModelHeaders = new ArrayList();

        al_tableModelClasses.add(Integer.class);
        al_tableModelHeaders.add("Place");
        al_tableModelClasses.add(Integer.class);
        al_tableModelHeaders.add("R");
        al_tableModelClasses.add(UserNameEntry.class);
        al_tableModelHeaders.add("Handle");

        ComponentNameBuilder nameBuilder = round.getRoundType().getComponentNameBuilder();
        RoundProperties roundProperties = round.getRoundProperties();
        for (int i = 0; i < problems.length; i++) {
            for (int j = 0; j < problems[i].getComponents().length; j++) {
                al_tableModelClasses.add(ChallengeProblemEntry.class);
                ProblemComponentModel problemComponent = problems[i].getComponents()[j];
                String name = nameBuilder.shortNameForComponent(
                        problemComponent.getClassName(), 
                        problemComponent.getPoints().doubleValue(), 
                        roundProperties);
                al_tableModelHeaders.add(componentPrefix + name); 
                }
            if (problems[i].getProblemType().intValue() == ContestConstants.TEAM_PROBLEM_TYPE_ID) {
                al_tableModelClasses.add(String.class);
                al_tableModelHeaders.add(problemPrefix + problems[i].getName());
            }
        }
        lastColumnIndexFix = 0;
        if (round.getRoundProperties().usesScore()) {
            al_tableModelClasses.add(String.class);
            al_tableModelHeaders.add("Score");
            lastColumnIndexFix = 1;
        }
        tableModelClasses = (Class[]) al_tableModelClasses.toArray(new Class[0]);
        tableModelHeaders = (String[]) al_tableModelHeaders.toArray(new String[0]);
        columnCount = tableModelClasses.length;
        maxProblemColumn = columnCount - lastColumnIndexFix;
    }


    final class ChallengeTableModel extends SortedTableModel implements Coder.Listener {
        private Map rankByScore = new HashMap();
        private RankComparator rankComparator = new RankComparator();
        
        protected ChallengeTableModel() {
            super(tableModelHeaders, tableModelClasses);
            if (lastColumnIndexFix > 0) {
                addSortElement(new SortElement(maxProblemColumn, true));
            }
            addSortElement(new SortElement(0, true));
            addSortElement(new SortElement(1, true));
            addSortElement(new SortElement(2, true));
            for (int i = 2; i < maxProblemColumn; i++) {
                addSortElement(new SortElement(i, true));
            }
            updateChallengeTable();
        }

        public void updateChallengeTable() {
            if (!update) {
                return;
            }
            Collection coders = getFilteredCoders();
            for (Iterator it = coders.iterator(); it.hasNext();) {
                Coder coder = (Coder) it.next();
                coder.addListener(this);
            }
            update(coders);
            updateRoomLeader();
        }
        
        public void updateRoomLeader() {
            if (!update) {
                return;
            }
            List tmpList = (List)this.getItemList();
            
            Double[] points = new Double[tmpList.size()]; 
            
            for(int i = 0; i < tmpList.size(); i++) {
                Coder c = (Coder)tmpList.get(i);
                points[i] = c.getScore();
            }
            Arrays.sort(points, 0, points.length, rankComparator);
            rankByScore.clear();
            Double currentPoints = new Double(Double.MIN_VALUE);
            for (int i = 0; i < points.length; i++) {
                if (!currentPoints.equals(points[i])) {
                    currentPoints = points[i];
                    rankByScore.put(currentPoints, new Integer(i + 1));
                }
            }
            sort();
        }

        private int getCoderRank(Coder c) {
            if (getRoundModel().getRoundProperties().usesScore()) {
                Integer rank = (Integer) rankByScore.get(c.getScore());
                return rank != null ? rank.intValue() : -1;
            } else {
                return 1;
            }
        }



        public void coderEvent(Coder coder) {
            fireTableDataChanged();
        }

        public Object getValueAt(int rowIndex, int columnIndex) {
            Coder coder = getCoder(rowIndex);
            return getValueAt(coder, columnIndex);
        }

        private Object getValueAt(Coder coder, int columnIndex) {
            if (columnIndex == 0) {
               return new Integer(getCoderRank(coder));
            }
            else if (columnIndex == 1) {
                return coder.getRating();
            } else if (columnIndex == 2) {
                return new UserNameEntry(coder.getHandle(),
                        coder.getRating().intValue(),
                        getRoundModel().getRoundProperties().usesScore() && isRoomLeader(coder.getHandle()),
                        coder.getUserType());
            } else if (columnIndex >= 3 && columnIndex < maxProblemColumn) {
                if (shouldUsePassedSystemTestsOnColumn(columnIndex)) {
                    return getComponentColumnValueAsTests(coder, columnIndex);
                } else {
                    return getComponentColumnValueAsPoints(coder, columnIndex);
                }
            } else if (columnIndex == maxProblemColumn) {
                return new Double(coder.getScore().doubleValue() / 100.0);
            } else {
                throw new IllegalArgumentException("Invalid column: " + columnIndex);
            }
        }

        private boolean shouldUsePassedSystemTestsOnColumn(int columnIndex) {
            ResultDisplayType columnDisplayType = getCellRenderer((JTable) table.getEventSource(), columnIndex).getDisplayType();
            return getRoundModel().getPhase().intValue() >= ContestConstants.CONTEST_COMPLETE_PHASE && ResultDisplayType.PASSED_TESTS.equals(columnDisplayType);    
        }

        private Object getComponentColumnValueAsPoints(Coder coder, int columnIndex) {
                int index = 2;
                double totalPoints;
                ProblemModel[] problems = getRoundModel().getProblems(getDivisionID());
                for (int i = 0; i < problems.length; i++) {
                    totalPoints = 0;
                    for (int j = 0; j < problems[i].getComponents().length; j++) {
                        totalPoints += (coder.getComponent(problems[i].getComponents()[j].getID()).getPoints().doubleValue() / 100);
                        index++;
                        if (index == columnIndex) {
                            return coder.getComponent(problems[i].getComponents()[j].getID());
                    }
                }
                if (problems[i].getProblemType().intValue() == ContestConstants.TEAM_PROBLEM_TYPE_ID) {
                    index++;
                    if (index == columnIndex) {
                        return new Double(totalPoints);
                    }
                }
                        }
            throw new IllegalArgumentException("Invalid column: " + columnIndex);
                    }

        private Object getComponentColumnValueAsTests(Coder coder, int columnIndex) {
            int index = 2;
            int totalTests;
            ProblemModel[] problems = getRoundModel().getProblems(getDivisionID());
            for (int i = 0; i < problems.length; i++) {
                totalTests = 0;
                for (int j = 0; j < problems[i].getComponents().length; j++) {
                    Integer tests = coder.getComponent(problems[i].getComponents()[j].getID()).getPassedSystemTests();
                    totalTests += tests == null ? 0 : tests.intValue();
                    index++;
                    if (index == columnIndex) {
                        return coder.getComponent(problems[i].getComponents()[j].getID());
                    }
                }
                if (problems[i].getProblemType().intValue() == ContestConstants.TEAM_PROBLEM_TYPE_ID) {
                        index++;
                        if (index == columnIndex) {
                        return new Integer(totalTests);
                        }
                    }
                }
                throw new IllegalArgumentException("Invalid column: " + columnIndex);
            }


        Coder getCoder(int rowIndex) {
            return (Coder) get(rowIndex);
        }

        private CoderComponent getCoderComponent(int rowIndex, int colIndex) {
            Coder coder = ((Coder) get(rowIndex));
            ProblemModel[] problems = getRoundModel().getProblems(getDivisionID());
            int index = 2;
            CoderComponent component = null;
            boolean done = false;
            for (int i = 0; !done && i < problems.length; i++) {
                for (int j = 0; j < problems[i].getComponents().length; j++) {
                    index++;
                    if (index == colIndex) {
                        component = coder.getComponent(problems[i].getComponents()[j].getID());
                        done = true;
                    }
                }

                if (problems[i].getProblemType().intValue()
                        == ContestConstants.TEAM_PROBLEM_TYPE_ID) {
                    index++;
                    if (index == colIndex) {
                        component = null;
                        done = true;
                    }
                }
            }
            return component;
        }

        public int compare(Object o1, Object o2) {
            Coder c1 = (Coder) o1;
            Coder c2 = (Coder) o2;

            for (Iterator it = getSortListIterator(); it.hasNext();) {
                SortElement sortElem = (SortElement) it.next();
                int col = sortElem.getColumn();
                int sign = sortElem.isOpposite() ? -1 : 1;
                if (col == 0) {
                    int diff = getCoderRank(c1) - getCoderRank(c2);
                    if(diff != 0 )
                        return sign * diff;
                }
                else if (col == 1) {
                    int diff = c1.getRating().intValue() - c2.getRating().intValue();
                    if (diff != 0) return sign * diff;
                } else if (col == 2) {
                    int diff = compareStrings(c1.getHandle(), c2.getHandle());
                    if (diff != 0) return sign * diff;
                } else if (col >= 3 && col < maxProblemColumn) {
                    int result;
                    if (shouldUsePassedSystemTestsOnColumn(col)) {
                        result = compareCoderByComponentPassedTestsColumn(c1, c2, col);
                    } else if (getRoundModel().getRoundProperties().usesScore()) {
                        result = compareCoderByComponentPointsColumn(c1, c2, col);
                    } else  {
                        result = compareCoderByComponentStatusColumn(c1, c2, col);
                    }
                    if (result != 0) return result * sign;
                } else if (col == maxProblemColumn) {
                    double diff = c1.getScore().doubleValue() - c2.getScore().doubleValue();
                    if (diff != 0) return sign * (diff > 0 ? 1 : -1);
                                } else {
                    throw new IllegalStateException( "bad sort column =" + sortElem );
                }
            }
                                    return 0;
                                }

        private int compareCoderByComponentStatusColumn(Coder c1, Coder c2, int column) {
            Object value1 = getValueAt(c1, column);
            Object value2 = getValueAt(c2, column);
            if (value1 instanceof CoderComponent) {
                return ((CoderComponent) value1).getStatus().compareTo(((CoderComponent) value2).getStatus());
            } else {
                return ((Comparable) value1).compareTo(value2);
                            }
                        }

        private int compareCoderByComponentPassedTestsColumn(Coder c1, Coder c2, int column) {
            Object value1 = getValueAt(c1, column);
            Object value2 = getValueAt(c2, column);
            if (value1 instanceof CoderComponent) {
                Integer tests1 = ((CoderComponent) value1).getPassedSystemTests();
                Integer tests2 = ((CoderComponent) value2).getPassedSystemTests();
                if (tests1 == null) {
                    return tests2 == null ? 0 : -1;
                } else if (tests2 == null) {
                    return 1;
                            }
                return tests1.compareTo(tests2);
            } else {
                return ((Comparable) value1).compareTo(value2);
                        }
                    }

        private int compareCoderByComponentPointsColumn(Coder c1, Coder c2, int column) {
            Object value1 = getValueAt(c1, column);
            Object value2 = getValueAt(c2, column);
            if (value1 instanceof CoderComponent) {
                return ((CoderComponent) value1).getPoints().compareTo(((CoderComponent) value2).getPoints());
                } else {
                return ((Comparable) value1).compareTo(value2);
            }
        }
    }


    private final class RankComparator implements Comparator  {
        public int compare(Object o1, Object o2) {
            Double c1 = (Double) o1;
            Double c2 = (Double) o2;
            return c2.compareTo(c1);
        }
    }
    
    private void createNewSourceViewer(boolean canChallenge) {
        closeSourceViewer();
        RoomModel room = getRoomByCoder(tableModel.getValueAt(((Integer)table.getProperty("SelectedRow")).intValue(), 2).toString());
        boolean challengable = room.isPracticeRoom() ||
                room.getRoundModel().isInChallengePhase();
        if (challengable) {
            challengable = room.isAssigned(ca.getModel().getCurrentUser());
        }
        if(!canChallenge)
            challengable = false;
        src = new SourceViewer(ca, challengable);
        src.setPanel(this);
    }

    public UIComponent getPanel() {
        return panel;
    }



    public Collection getFilteredCoders() {
        Collection coders = getCoders();
        if (getRoundModel().getRoundProperties().getShowScoresOfOtherCoders().booleanValue()) {
            return coders;
        }
        for (Iterator it = coders.iterator(); it.hasNext();) {
            Coder coder = (Coder) it.next();
            if (coder.getHandle().equals(ca.getModel().getCurrentUser())) {
                return Collections.singletonList(coder);
            }
        }
        return Collections.EMPTY_LIST;

    }
    
    public void closeSourceViewer() {
        if (src != null) {
            sourceViewerClosing();  // TODO - do we need to call this here?
            src.hide();
            src.dispose();
            src = null;
        }
    }
    /**
     * return the click status key.
     *
     * @param codeComponent
     *         the code which is clicked to view
     * @param writer
     *         the writer of the code
     * @return the click status key as problemId+_+writerHandler
     */
    private String getUniqueClickStatusKey(CoderComponent codeComponent,String writer) {
        if(codeComponent!=null && writer!=null &&writer.trim().length()>0) {
            return codeComponent.getComponent().getID().longValue()+"_"+writer;
        }
        return null;
    }
    /**
     * check whether the source view can open.
     *
     * @param codeComponent
     *         the code which is clicked to view
     * @param writer
     *         the writer of the code
     * @return whether the source code can open or not
     */
    private boolean canOpenSource(CoderComponent codeComponent,String writer) {
        String key = getUniqueClickStatusKey(codeComponent,writer);        
        if(key==null)
            return false;
        if(!clickStatusController.containsKey(key)) {
            clickStatusController.put(key, PROBLEM_OPENED);
            return true;
        } else 
            return false;
    }
    public void sourceViewerClosing() {
        if (currentProblemInfo != null) {
            currentProblemInfo.removeListener(myProblemModelListener);
            currentProblemInfo = null;
        }
        if (currentComponent != null) {
            //remove the problem click status
            clickStatusController.remove(getUniqueClickStatusKey(currentComponent,challengeHandle));
            currentComponent.removeListener(myCoderComponentListener);
            currentComponent = null;
        }
    }
    
    public abstract void updateChallengeTable(RoomModel room);
    
    protected abstract RoundModel getRoundModel();
    
    protected abstract Integer getDivisionID();

    protected abstract boolean isRoomLeader(String handle);

    protected abstract Collection getCoders();
    
    protected abstract RoomModel getRoomByCoder(String string);

    protected abstract void initTable();

    public ChallengeTableModel getTableModel() {
        return tableModel;
    }

    public void setUpdate(boolean update) {
        this.update = update;
    }

    public int getMaxProblemColumn() {
        return maxProblemColumn;
    }
    
    public boolean hasFinalScoreColumn() {
        return maxProblemColumn != columnCount;
    }
}
