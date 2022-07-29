/*
 * BaseAlgoSummaryTablePanel
 * 
 * Created 06/13/2007
 */
package com.topcoder.client.contestApplet.panels.table;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.swing.DefaultListSelectionModel;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.border.EmptyBorder;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.TableColumn;

import com.topcoder.client.SortElement;
import com.topcoder.client.SortedTableModel;
import com.topcoder.client.contestApplet.ContestApplet;
import com.topcoder.client.contestApplet.common.Common;
import com.topcoder.client.contestApplet.common.LocalPreferences;
import com.topcoder.client.contestApplet.common.MenuItemInfo;
import com.topcoder.client.contestApplet.common.PopUpHelper;
import com.topcoder.client.contestApplet.frames.ChallengeFrame;
import com.topcoder.client.contestApplet.frames.RoomInfoFrame;
import com.topcoder.client.contestApplet.frames.SourceViewer;
import com.topcoder.client.contestApplet.widgets.ComponentResultDisplayRenderer;
import com.topcoder.client.contestApplet.widgets.ContestTableCellRenderer;
import com.topcoder.client.contestApplet.widgets.ContestTableHeaderRenderer;
import com.topcoder.client.contestApplet.widgets.FormatTableCellRenderer;
import com.topcoder.client.contestApplet.widgets.LanguageAndStatusColoringDecoratorRenderer;
import com.topcoder.client.contestApplet.widgets.TCIcon;
import com.topcoder.client.contestant.Coder;
import com.topcoder.client.contestant.CoderComponent;
import com.topcoder.client.contestant.Contestant;
import com.topcoder.client.contestant.ProblemComponentModel;
import com.topcoder.client.contestant.ProblemModel;
import com.topcoder.client.contestant.RoomModel;
import com.topcoder.client.contestant.RoundModel;
import com.topcoder.netCommon.contest.ContestConstants;
import com.topcoder.netCommon.contest.ResultDisplayType;
import com.topcoder.netCommon.contest.round.RoundProperties;
import com.topcoder.netCommon.contest.round.text.ComponentNameBuilder;
import com.topcoder.netCommon.contestantMessages.response.data.ComponentChallengeData;
import com.topcoder.shared.language.BaseLanguage;
import com.topcoder.shared.language.JavaLanguage;

/**
 * Base class for summary table of Algo rounds.
 *
 * Code merged from {@link DivSummaryTablePanel} and {@link ChallengeTablePanel}.
 * 
 * @autor Diego Belfer (Mural)
 * @version $Id: BaseAlgoSummaryTablePanel.java 70600 2008-05-14 21:31:02Z dbelfer $
 */
public abstract class BaseAlgoSummaryTablePanel extends AbstractSummaryTablePanel implements SourceViewer.SourceViewerListener {
    private static final boolean headersVisible = true;
    
    private final ContestApplet ca;
    private final Contestant model;
    // Table Model/View/Controller Definitions
    private ChallengeTableModel tableModel;
    private JTable table;
    private boolean once = true;
    private int lastColumnIndexFix;
    private int columnCount;
    private int maxProblemColumn;
    private boolean enabled = true;
    private Class[] tableModelClasses;
    private String[] tableModelHeaders;
    private ComponentResultDisplayRenderer[] resultDisplayRenderer;
    private int oldCompID = 0;
    private ArrayList oldArgs = null;

    
    
    
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

    // Popup Menu View Definitions
    private JPopupMenu contestPopup = new JPopupMenu();

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
                src.setVisible(true);

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

    private void setContestPopup(JPopupMenu contestPopup) {
        this.contestPopup = contestPopup;
    }

    private void setContestPopup(MenuItemInfo[] menuItemInfo) {
        String label = CHALLENGE_POPUP_TITLE;
        setContestPopup(PopUpHelper.createPopupMenu(label, menuItemInfo));
    }

    private JTable createTable() {
        JTable table = new JTable(tableModel);

        table.getTableHeader().setResizingAllowed(false);
        table.getTableHeader().setReorderingAllowed(false);

        table.setBackground(Common.TB_COLOR);
        table.setForeground(Common.TF_COLOR);
        table.setSelectionBackground(Common.HB_COLOR);
        table.setSelectionForeground(Common.HF_COLOR);
        table.setShowGrid(false);

        table.setSelectionMode(DefaultListSelectionModel.SINGLE_SELECTION);
        table.getColumnModel().setColumnMargin(0);

        setHeaders(table);
        
        String fontName = LocalPreferences.getInstance().getFont(LocalPreferences.SUMMARYFONT);
        int fontSize = LocalPreferences.getInstance().getFontSize(LocalPreferences.SUMMARYFONTSIZE);

        ContestTableCellRenderer tcr = new ContestTableCellRenderer(fontName,fontSize);
        table.setDefaultRenderer(String.class, tcr);

        // set the default renderers and sizes for the headers/cells
        resultDisplayRenderer = new ComponentResultDisplayRenderer[columnCount - 3 - lastColumnIndexFix];
        for (int i = 0; i < columnCount; i++) {
            TableColumn col = table.getColumnModel().getColumn(i);
            if (i == 0 ) {
                col.setPreferredWidth(25);
                col.setCellRenderer(new ContestTableCellRenderer(fontName,fontSize));
            } else if (i == 1) {
                // Rating
                col.setPreferredWidth(TCIcon.DEFAULT_WIDTH * 2 + 3);
                col.setCellRenderer(new RankRenderer());
            } else if (i == 2) {
                // Handle
                col.setPreferredWidth(115);
                col.setCellRenderer(new UserNameRenderer(model, fontName, fontSize));
            } else if (i < maxProblemColumn) {
                // Problems
                col.setPreferredWidth(140);
                resultDisplayRenderer[getIndexOfRenderer(i)] = new ComponentResultDisplayRenderer(getRoundModel(), new ContestTableCellRenderer(fontName,fontSize));
                col.setCellRenderer(new LanguageAndStatusColoringDecoratorRenderer(resultDisplayRenderer[getIndexOfRenderer(i)], Color.WHITE) {
                    protected Integer getLanguage(Object value, int row, int column) {
                        try {
                            return ((CoderComponent) value).getLanguageID();
                        } catch (RuntimeException e) {
                            return null;
                        }
                    }
                    protected int getStatus(Object value, int row, int column) {
                        try {
                            return ((CoderComponent) value).getStatus().intValue();
                        } catch (RuntimeException e) {
                            return 0;
                        }
                    }
                });
            } else {
                // Score
                col.setPreferredWidth(100);
                col.setCellRenderer(new FormatTableCellRenderer(new ContestTableCellRenderer(fontName,fontSize), Common.newScoreFormat()));
            }
        }

        return (table);
    }

    private void setHeaders(JTable table) {
        String fontName = LocalPreferences.getInstance().getFont(LocalPreferences.SUMMARYFONT);
        int fontSize = LocalPreferences.getInstance().getFontSize(LocalPreferences.SUMMARYFONTSIZE);
        
        // set the default renderers for the headers/cells
        for (int i = 0; i < columnCount; i++) {
            table.getColumnModel().getColumn(i).setHeaderRenderer(
                    new ContestTableHeaderRenderer(headersVisible,fontName, fontSize)
            );
            table.getColumnModel().getColumn(i).setCellRenderer(
                    new ContestTableCellRenderer(fontName,fontSize)
            );
        }
    }

    private void createTablePanel() {
        String title = getTitle();
        int width = 0;
        int height = 0;

        JScrollPane pane = new JScrollPane(table);
        GridBagConstraints gbc = Common.getDefaultConstraints();

        gbc.insets = new Insets(-1, -1, -1, -1);
        this.setBorder(Common.getTitledBorder(title));
        this.setPreferredSize(new Dimension(width, height));
        pane.setBorder(new EmptyBorder(0, 0, 0, 0));
        pane.getViewport().setBackground(Common.TB_COLOR);
        table.setPreferredScrollableViewportSize(pane.getSize());

        // new workspace variables
        this.setBackground(Common.WPB_COLOR);
        this.setOpaque(true);

        Common.insertInPanel(pane, this, gbc, 0, 0, 1, 1);
        
        String fontName = LocalPreferences.getInstance().getFont(LocalPreferences.SUMMARYFONT);
        int fontSize = LocalPreferences.getInstance().getFontSize(LocalPreferences.SUMMARYFONTSIZE);
        
        table.setRowSelectionAllowed(true);
        table.setColumnSelectionAllowed(true);
        table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        table.getTableHeader().setBackground(Common.BG_COLOR);
        table.getTableHeader().setResizingAllowed(true);
        table.getTableHeader().setDefaultRenderer(
                new ContestTableHeaderRenderer(true,fontName,fontSize)
        );

        table.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                if(enabled) {
                    mouseClickEvent(e);
                    otherMouseClickEvent(e);
                }
            }
        });

        table.getTableHeader().addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                headerClickEvent(e);
            }
        });
    }

    private void showContestPopup(MouseEvent e) {
        contestPopup.show(e.getComponent(), e.getX(), e.getY());
    }

    private static final String CHALLENGE_POPUP_TITLE = "Challenge Info";

    private final MenuItemInfo[] OTHER_CHALLENGE_POPUP = {
        new MenuItemInfo("Info", new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                infoPopupEvent();
            }
        }),
        new MenuItemInfo("History", new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                historyPopupEvent();
            }
        }),
    };

    private final MenuItemInfo SOURCE_ITEM = new MenuItemInfo(
            "Source",
            new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    sourcePopupEvent();
                }
            });

    private final MenuItemInfo[] CHALLENGE_POPUP = {
        SOURCE_ITEM
    };

    private final MenuItemInfo[] ONLY_SOURCE_POPUP = {SOURCE_ITEM};

    private JFrame frame = null;
    private JPopupMenu otherContestPopup = new JPopupMenu();
    private String challengeHandle;
    private CoderComponent currentComponent;
    private boolean onlySourceMenuItem;
    private boolean update;
    
    public BaseAlgoSummaryTablePanel(
            ContestApplet ca, 
            JFrame cr, boolean isOnlySourceMenuItem, boolean update) {
        super(new GridBagLayout());
        this.ca = ca;
        this.model = ca.getModel();
        this.frame = cr;
        this.onlySourceMenuItem = isOnlySourceMenuItem;
        this.update = update;
    }
    
    protected void init() {
        prepareForTableModel();
        this.tableModel = new ChallengeTableModel();
        tableModel.addTableModelListener(new TableModelListener() {
            public void tableChanged(TableModelEvent e) {
                if (currentProblemInfo != null) {
                    if (currentComponent.getStatus().intValue() == ContestConstants.CHALLENGE_SUCCEEDED) {
                        src.notifyChallengeSucceeded(currentComponent.getCoder().getHandle(), Common.formatNoFractions(currentProblemInfo.getComponents()[0].getPoints()));
                    }
                }
                updateRoomLeader();
            }
        });
        this.table = createTable();
        UIManager.put("MenuItem.selectionBackground", Common.HB_COLOR);


        MenuItemInfo[] menuItemInfo;
        if (onlySourceMenuItem) {
            menuItemInfo = ONLY_SOURCE_POPUP;
        } else {
            menuItemInfo = CHALLENGE_POPUP;
        }
        setContestPopup(menuItemInfo);

        otherContestPopup = PopUpHelper.createPopupMenu("", OTHER_CHALLENGE_POPUP);

        createTablePanel();

        
    }

    public JComponent getTable() {
        return table;
    }

    private void mouseClickEvent(MouseEvent e) {
        int r = ((JTable) e.getComponent()).rowAtPoint(e.getPoint());
        int c = ((JTable) e.getComponent()).columnAtPoint(e.getPoint());
        ((JTable) e.getComponent()).setRowSelectionInterval(r, r);
        ((JTable) e.getComponent()).setColumnSelectionInterval(c, c);

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
                } else if ((e.getClickCount() > 1) &&
                        SwingUtilities.isLeftMouseButton(e)) {
                    sourcePopupEvent();
                } else if ((e.getClickCount() == 1) &&
                        SwingUtilities.isLeftMouseButton(e)) {
            		// display score if status is currently displayed
                	resultDisplayRenderer[getIndexOfRenderer(c)].toggleDisplayTypeForComponent(component, getRoundModel().getRoundProperties().getAllowedScoreTypesToShow());
            		tableModel.fireTableCellUpdated(r, c);
                }
                }
            }
        }

    private int getIndexOfRenderer(int c) {
        return c-3;
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
                otherContestPopup.show(e.getComponent(), e.getX(), e.getY());
            } else if ((e.getClickCount() > 1) &&
                    SwingUtilities.isLeftMouseButton(e)) {
                infoPopupEvent();
            }
        }
    }

    private void headerClickEvent(MouseEvent e) {
        if (SwingUtilities.isLeftMouseButton(e)) {

            int col = table.getTableHeader().columnAtPoint(e.getPoint());

            if (col == -1) return;
            tableModel.sort(
                    col,
                    (e.getModifiers() & MouseEvent.SHIFT_MASK) > 0
            );
            table.getTableHeader().repaint();
        }
    }

    private void infoPopupEvent() {
        int index = table.getSelectedRow();
        challengeHandle = ((UserNameEntry) table.getValueAt(index, 2)).getName();
        ca.setCurrentFrame(frame);
        ca.requestCoderInfo(challengeHandle, ((UserNameEntry) table.getValueAt(index, 2)).getUserType());
    }

    private void sourcePopupEvent() {

        int r = table.getSelectedRow();
        int c = table.getSelectedColumn();

        // Any column EXCEPT the score column
        if (c > 2 && c < maxProblemColumn) {
            CoderComponent coderComponent = tableModel.getCoderComponent(r, c);
            if (coderComponent.getStatus().intValue() >
                    ContestConstants.NOT_OPENED) {
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
                challengeHandle = tableModel.getValueAt(r, 2).toString();
                ca.setCurrentFrame(frame);
                boolean prettyToggle = false;
                if (frame instanceof ChallengeFrame) {
                    prettyToggle = ((ChallengeFrame) frame).getPrettyToggle();
                } else if (frame instanceof RoomInfoFrame) {
                    prettyToggle = ((RoomInfoFrame) frame).getPrettyToggle();
                }

                // So we are notified when the stmt + code is updated
                currentComponent.addListener(myCoderComponentListener);
                currentProblemInfo.addListener(myProblemModelListener);
                ca.setCurrentFrame(frame);
                ca.getInterFrame().showMessage(
                        "Fetching problem...",
                        frame, ContestConstants.GET_CHALLENGE_PROBLEM
                );
                ca.getModel().getRequester().requestChallengeComponent(
                        currentComponent.getComponent().getID().longValue(),
                        prettyToggle,
                        getRoomByCoder(challengeHandle).getRoomID().longValue(),
                        challengeHandle
                );
            }
        }
    }

    /**
     * challenge the given challenge index and problem index.
     *
     * This will request the argument types from the server, and popup the
     * challenge input dialog.
     */
    public void setOldArgs(ArrayList args, int compID) {
        oldArgs = args;
        oldCompID = compID;

    }

    public void doChallenge(String writer, CoderComponent coderComponent, JFrame parentFrame) {
        if(!enabled)
            return;
        
//            System.out.println("----> doChallenge event." + (currentComponent==null ? "null" : currentComponent.getCoder().getHandle() + "-" + currentComponent.getPoints()));
//            System.out.println("----> doChallenge event." + (coderComponent==null ? "null" : coderComponent.getCoder().getHandle() + "-" + coderComponent.getPoints()));
        ComponentChallengeData ccd = coderComponent.getComponent().getComponentChallengeData();
        challengeHandle = writer;
        this.currentComponent = coderComponent;  // todo why reset?
        ca.setCurrentFrame(frame);
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
            /*
            Common.showArgInput(
                    message,
                    ccd.getParamTypes(),
                    null,
                    parentFrame,
                    true,
                    //ccd.getParameterNames(),
                    coderComponent.getComponent()
            );
            */
        } else {
            /*
            Common.showArgInput(
                    message,
                    ccd.getParamTypes(),
                    oldArgs,
                    parentFrame,
                    true,
                    //ccd.getParameterNames(),
                    coderComponent.getComponent()
            );
            */
        }

    }



    /*
    * bold the room leader in the challenge table
    */
    private void updateRoomLeader() {
        tableModel.updateRoomLeader();
        repaint();
    }
    
    /*
     * Toggles display to show status or scores of all problem submissions.
     */
     public void updateView(ResultDisplayType type) {
         for (int i = 0; i < resultDisplayRenderer.length; i++) {
             ComponentResultDisplayRenderer renderer = resultDisplayRenderer[i];
             renderer.setDisplayType(type);
        }
		tableModel.fireTableDataChanged();
     }

    private void historyPopupEvent() {
        int index = table.getSelectedRow();
        if (index >= 0) {
            Coder coder = tableModel.getCoder(index);
            String handle = coder.getHandle();
            ca.setCurrentFrame(frame);
            ca.requestCoderHistory(handle, getRoomByCoder(handle).getRoomID().longValue(), coder.getUserType());
        }
    }


    private void prepareForTableModel() {
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
        
        private ChallengeTableModel() {
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
            return getRoundModel().getPhase().intValue() >= ContestConstants.CONTEST_COMPLETE_PHASE && ResultDisplayType.PASSED_TESTS.equals(resultDisplayRenderer[getIndexOfRenderer(columnIndex)].getDisplayType());    
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

    /**
     * Temporary fix to the focus problem. If focus is lost in the window,
     * normally the user would have to log out the browser and reload the
     * applet. Now the user just has to reload the problem, and a new coding
     * window will get created.
     */
    private void createNewSourceViewer(boolean canChallenge) {
//            System.out.println("----> createNewSourceViewer event." + (currentComponent==null ? "null" : currentComponent.getCoder().getHandle() + "-" + currentComponent.getPoints()));
        closeSourceViewer();
        
        RoomModel room = getRoomByCoder(tableModel.getValueAt(table.getSelectedRow(), 2).toString());
        boolean challengable = room.isPracticeRoom() ||  getRoundModel().isInChallengePhase();
        
        
        if (challengable) {
            challengable = room.isAssigned(ca.getModel().getCurrentUser());
        }
        if(!canChallenge)
            challengable = false;
        src = new SourceViewer(ca, challengable);
        src.setPanel(this);
        
//        src.create();
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
//            System.out.println("----> closeSourceViewer event." + (currentComponent==null ? "null" : currentComponent.getCoder().getHandle() + "-" + currentComponent.getPoints()));
        if (src != null) {
            sourceViewerClosing();  // TODO - do we need to call this here?
            src.setVisible(false);
            src.dispose();
            src = null;
        }
    }

    public void sourceViewerClosing() {
//            System.out.println("----> sourceViewerClosing event." + (currentComponent==null ? "null" : currentComponent.getCoder().getHandle() + "-" + currentComponent.getPoints()));
        if (currentProblemInfo != null) {
            currentProblemInfo.removeListener(myProblemModelListener);
            currentProblemInfo = null;
        }
        if (currentComponent != null) {
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
    
    protected abstract String getTitle();

    public ChallengeTableModel getTableModel() {
        return tableModel;
    }

    public void setUpdate(boolean update) {
        this.update = update;
    }
}
 

