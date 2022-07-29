package com.topcoder.client.contestApplet.uilogic.panels;

import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.MouseEvent;
import java.lang.reflect.InvocationTargetException;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;

import javax.swing.JFrame;
import javax.swing.JTable;
import javax.swing.SwingUtilities;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;

import com.topcoder.client.SortElement;
import com.topcoder.client.SortedTableModel;
import com.topcoder.client.contestApplet.ContestApplet;
import com.topcoder.client.contestApplet.common.LocalPreferences;
import com.topcoder.client.contestApplet.uilogic.components.LongCodeViewer;
import com.topcoder.client.contestApplet.uilogic.frames.FrameLogic;
import com.topcoder.client.contestApplet.uilogic.panels.table.LanguageColoringDecoratorRenderer;
import com.topcoder.client.contestApplet.uilogic.panels.table.UserNameEntry;
import com.topcoder.client.contestApplet.uilogic.panels.table.ValueTransformDecoratorCellRenderer;
import com.topcoder.client.contestant.Coder;
import com.topcoder.client.contestant.CoderComponent;
import com.topcoder.client.contestant.RoomModel;
import com.topcoder.client.contestant.RoundModel;
import com.topcoder.client.contestant.impl.LongCoderComponentImpl;
import com.topcoder.client.ui.UIComponent;
import com.topcoder.client.ui.UIPage;
import com.topcoder.client.ui.event.UIActionListener;
import com.topcoder.client.ui.event.UIMouseAdapter;
import com.topcoder.netCommon.contest.ContestConstants;
import com.topcoder.netCommon.contest.ResultDisplayType;

public class LongSummaryTablePanel extends AbstractSummaryTablePanel {
    private static final int EXAMPLE_COUNT_COLUMN = 8;
    private static final int SUBMISSION_COUNT_COLUMN = 6;
    private final SimpleDateFormat dateFormat = new SimpleDateFormat("MM-dd-yyyy HH:mm:ss");
    
    private final ContestApplet ca;
    private final RoomModel room;
    private UIPage page;
    private TableModelListener tableModelListener;

    private ChallengeTableModel tableModel;
    private UIComponent table;
    private UIComponent submissionPopup;
    private UIComponent otherContestPopup;
    private UIComponent endedContestPopup;
    
    private Class[] tableModelClasses;
    private String[] tableModelHeaders;
    private boolean enabled = true;
    private FrameLogic frame = null;
    private boolean update;
    private LongCodeViewer viewer;
    private boolean endedContestLayout;
    private List tableColumns;
    private UIComponent panel;

    public LongSummaryTablePanel(ContestApplet ca, RoomModel room, FrameLogic cr, boolean update, UIPage page) {
        this.page = page;
        this.ca = ca;
        this.room = room;
        this.update = update;
        this.frame = cr;

        panel = page.getComponent("long_summary_table_panel");
        table = page.getComponent("long_summary_table");
        submissionPopup = page.getComponent("long_submission_popup");
        otherContestPopup = page.getComponent("long_other_contest_popup");
        endedContestPopup = page.getComponent("long_ended_contest_popup");

        // Apply action listeners
        page.getComponent("long_other_info_menu").addEventListener("action", new UIActionListener() {
                public void actionPerformed(ActionEvent e) {
                    infoPopupEvent();
                }
            });
        page.getComponent("long_other_history_menu").addEventListener("action", new UIActionListener() {
                public void actionPerformed(ActionEvent e) {
                    historyPopupEvent(1);
                }
            });
        page.getComponent("long_other_submission_menu").addEventListener("action", new UIActionListener() {
                public void actionPerformed(ActionEvent e) {
                    historyPopupEvent(2);
                }
            });
        page.getComponent("long_other_example_menu").addEventListener("action", new UIActionListener() {
                public void actionPerformed(ActionEvent e) {
                    historyPopupEvent(3);
                }
            });
        page.getComponent("long_ended_info_menu").addEventListener("action", new UIActionListener() {
                public void actionPerformed(ActionEvent e) {
                    infoPopupEvent();
                }
            });
        page.getComponent("long_ended_history_menu").addEventListener("action", new UIActionListener() {
                public void actionPerformed(ActionEvent e) {
                    historyPopupEvent(1);
                }
            });
        page.getComponent("long_ended_submission_menu").addEventListener("action", new UIActionListener() {
                public void actionPerformed(ActionEvent e) {
                    historyPopupEvent(2);
                }
            });
        page.getComponent("long_ended_example_menu").addEventListener("action", new UIActionListener() {
                public void actionPerformed(ActionEvent e) {
                    historyPopupEvent(3);
                }
            });
        page.getComponent("long_ended_results_menu").addEventListener("action", new UIActionListener() {
                public void actionPerformed(ActionEvent e) {
                    systemTestResultsEvent();
                }
            });
        page.getComponent("long_source_menu").addEventListener("action", new UIActionListener() {
                public void actionPerformed(ActionEvent e) {
                    sourcePopupEvent();
                }
            });

        // Save columns
        TableColumnModel model = (TableColumnModel) table.getProperty("columnmodel");
        tableColumns = new ArrayList();
        for (Enumeration iter = model.getColumns(); iter.hasMoreElements(); ) {
            tableColumns.add(iter.nextElement());
        }
        tableModelListener = new TableModelListener() {
                public void tableChanged(TableModelEvent e) {
                    updateRoomLeader();
                }
            };
        createTable();
        initTable();
    }

    private void initTable() {
        //restore columns
        TableColumnModel model = (TableColumnModel) table.getProperty("columnmodel");
        for (Iterator iter = tableColumns.iterator(); iter.hasNext(); ) {
            model.removeColumn((TableColumn) iter.next());
        }
        for (Iterator iter = tableColumns.iterator(); iter.hasNext(); ) {
            model.addColumn((TableColumn) iter.next());
        }

        if (tableModel != null) {
            this.tableModel.removeTableModelListener(tableModelListener);
            this.tableModel = null;
        }
        prepareForTableModel();
        this.tableModel = new ChallengeTableModel();
        tableModel.addTableModelListener(tableModelListener);
        if (!endedContestLayout) {
            // remove the final score column
            table.performAction("removeColumn", new Object[] {table.performAction("getColumn", new Object[] {"final_score"})});
        }
        table.setProperty("model", tableModel);
    }
        
    private void reinitTable() {
        try {
            SwingUtilities.invokeAndWait(new Runnable() {
                    public void run() {
                        initTable();
                        panel.performAction("invalidate");
                        panel.performAction("repaint");
                    }
                });
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
    }

    public void setPanelEnabled(boolean on) {
        enabled = on;
    }

    protected boolean isEndedContestLayout() {
        return endedContestLayout;
    }
    
    protected boolean isContestEnded() {
        return this.room.getRoundModel().getPhase().intValue() == ContestConstants.CONTEST_COMPLETE_PHASE;
    }

    private int fixedColIndex(int i) {
        if (i >= 4 && !isEndedContestLayout()) {
            return i + 1;
        }
        return i;
    }

    private void createTable() {
        String fontName = LocalPreferences.getInstance().getFont(LocalPreferences.SUMMARYFONT);
        int fontSize = LocalPreferences.getInstance().getFontSize(LocalPreferences.SUMMARYFONTSIZE);
        page.getComponent("long_summary_table_header_renderer").setProperty("font", new Font(fontName, Font.PLAIN, fontSize));
        page.getComponent("long_summary_table_header_center_renderer").setProperty("font", new Font(fontName, Font.PLAIN, fontSize));
        page.getComponent("long_summary_user_renderer").setProperty("font", new Font(fontName, Font.PLAIN, fontSize));
        page.getComponent("long_summary_user_renderer").setProperty("model", ca.getModel());
        page.getComponent("long_summary_cell_renderer").setProperty("fontName", fontName);
        page.getComponent("long_summary_cell_renderer").setProperty("fontSize", new Integer(fontSize));
        page.getComponent("long_summary_nocolor_cell_renderer").setProperty("fontName", fontName);
        page.getComponent("long_summary_nocolor_cell_renderer").setProperty("fontSize", new Integer(fontSize));
        page.getComponent("long_summary_cell_rightaligned_renderer").setProperty("fontName", fontName);
        page.getComponent("long_summary_cell_rightaligned_renderer").setProperty("fontSize", new Integer(fontSize));
        page.getComponent("long_summary_cell_center_renderer").setProperty("fontName", fontName);
        page.getComponent("long_summary_cell_center_renderer").setProperty("fontSize", new Integer(fontSize));

        // setup columns
        final DecimalFormat doubleFormat = new DecimalFormat("0.00");
        // Status
        page.getComponent("long_summary_status_column").setProperty("CellRenderer", new LanguageColoringDecoratorRenderer(new ValueTransformDecoratorCellRenderer((TableCellRenderer) page.getComponent("long_summary_status_column").getProperty("CellRenderer")) {
                protected Object transform(Object value, int row, int column) {
                    int status = ((Integer)value ).intValue();
                    switch (status) {
                    case ContestConstants.NOT_OPENED:
                        return "Unopened";
                    case ContestConstants.LOOKED_AT:
                        return "Opened";
                    case ContestConstants.COMPILED_UNSUBMITTED:
                        return "Compiled";
                    case ContestConstants.NOT_CHALLENGED:
                        return "Pending tests";
                    case ContestConstants.SYSTEM_TEST_SUCCEEDED:
                        return "Test completed";
                    default:
                        throw new IllegalStateException("Invalid component state: " + status);
                    }
                }
            }) {
                protected Integer getLanguage(Object value, int row, int col) {
                    Coder coder = tableModel.getCoder(row);
                    return coder.getComponents()[0].getLanguageID();
                }
            });
        //Final score
        page.getComponent("long_summary_final_score_column").setProperty("CellRenderer", new LanguageColoringDecoratorRenderer(new ValueTransformDecoratorCellRenderer((TableCellRenderer) page.getComponent("long_summary_final_score_column").getProperty("CellRenderer")) {
                protected Object transform(Object value, int row, int column) {
                    if (value == null) return null;
                    return doubleFormat.format(value);
                }
            }) {
                protected Integer getLanguage(Object value, int row, int col) {
                    Coder coder = tableModel.getCoder(row);
                    return coder.getComponents()[0].getLanguageID();
                }
            });
        //Score
        page.getComponent("long_summary_score_column").setProperty("CellRenderer", new LanguageColoringDecoratorRenderer(new ValueTransformDecoratorCellRenderer((TableCellRenderer) page.getComponent("long_summary_score_column").getProperty("CellRenderer")) {
                protected Object transform(Object value, int row, int column) {
                    if (value == null) return null;
                    return doubleFormat.format(value);
                }
            }) {
                protected Integer getLanguage(Object value, int row, int col) {
                    Coder coder = tableModel.getCoder(row);
                    return coder.getComponents()[0].getLanguageID();
                }
            });
        //Submissions
        page.getComponent("long_summary_submissions_column").setProperty("CellRenderer", new LanguageColoringDecoratorRenderer((TableCellRenderer) page.getComponent("long_summary_submissions_column").getProperty("CellRenderer")) {
                protected Integer getLanguage(Object value, int row, int col) {
                    Coder coder = tableModel.getCoder(row);
                    return coder.getComponents()[0].getLanguageID();
                }
            });
        //Last submission
        page.getComponent("long_summary_last_submission_column").setProperty("CellRenderer", new LanguageColoringDecoratorRenderer(new ValueTransformDecoratorCellRenderer((TableCellRenderer) page.getComponent("long_summary_last_submission_column").getProperty("CellRenderer")) {
                protected Object transform(Object value, int row, int column) {
                    if (value == null) return null;
                    return dateFormat.format(value);
                }
            }) {
                protected Integer getLanguage(Object value, int row, int col) {
                    Coder coder = tableModel.getCoder(row);
                    return coder.getComponents()[0].getLanguageID();
                }
            });
        //Examples
        page.getComponent("long_summary_examples_column").setProperty("CellRenderer", new LanguageColoringDecoratorRenderer((TableCellRenderer) page.getComponent("long_summary_examples_column").getProperty("CellRenderer")) {
                protected Integer getLanguage(Object value, int row, int col) {
                    Coder coder = tableModel.getCoder(row);
                    return coder.getComponents()[0].getLanguageID();
                }
            });
        //Last example
        page.getComponent("long_summary_last_example_column").setProperty("CellRenderer", new LanguageColoringDecoratorRenderer(new ValueTransformDecoratorCellRenderer((TableCellRenderer) page.getComponent("long_summary_last_example_column").getProperty("CellRenderer")) {
                protected Object transform(Object value, int row, int column) {
                    if (value == null) return null;
                    return dateFormat.format(value);
                }
            }) {
                protected Integer getLanguage(Object value, int row, int col) {
                    Coder coder = tableModel.getCoder(row);
                    return coder.getComponents()[0].getLanguageID();
                }
            });

        table.addEventListener("mouse", new UIMouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                if(enabled) {
                    mouseClickEvent(e);
                    otherMouseClickEvent(e);
                }
            }
        });

        ((JTableHeader) table.getProperty("TableHeader")).addMouseListener(new UIMouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                headerClickEvent(e);
            }
        });
    }

    private void showSubmissionPopup(MouseEvent e) {
        submissionPopup.performAction("show", new Object[] {e.getComponent(), new Integer(e.getX()), new Integer(e.getY())});
    }

    public UIComponent getTable() {
        return table;
    }

    private void mouseClickEvent(MouseEvent e) {
        int r = ((JTable) e.getComponent()).rowAtPoint(e.getPoint());
        int c = ((JTable) e.getComponent()).columnAtPoint(e.getPoint());
        ((JTable) e.getComponent()).setRowSelectionInterval(r, r);
        ((JTable) e.getComponent()).setColumnSelectionInterval(c, c);
        int fixedIndex = fixedColIndex(c);
        if (fixedIndex == SUBMISSION_COUNT_COLUMN || fixedIndex == EXAMPLE_COUNT_COLUMN) {
            Coder coder = tableModel.getCoder(r);
            CoderComponent component = coder.getComponents()[0];
            if (component != null && component.getStatus().intValue() >= ContestConstants.LOOKED_AT) {
                if (SwingUtilities.isRightMouseButton(e)) {
                    showSubmissionPopup(e);
                } else if ((e.getClickCount() > 1) && SwingUtilities.isLeftMouseButton(e)) {
                    sourcePopupEvent();
                } 
            }
        }
    }

    private void otherMouseClickEvent(MouseEvent e) {
        int r = ((JTable) e.getComponent()).rowAtPoint(e.getPoint());
        int c = ((JTable) e.getComponent()).columnAtPoint(e.getPoint());
        ((JTable) e.getComponent()).setRowSelectionInterval(r, r);
        ((JTable) e.getComponent()).setColumnSelectionInterval(c, c);

        // make sure it doesn't show up on either the users/scores columns or
        // if there was no code submitted for this problem
        int fixedIndex = fixedColIndex(c);
        if (fixedIndex != SUBMISSION_COUNT_COLUMN && fixedIndex != EXAMPLE_COUNT_COLUMN) {
            if (SwingUtilities.isRightMouseButton(e)) {
                if (isEndedContestLayout()) {
                    endedContestPopup.performAction("show", new Object[] {e.getComponent(), new Integer(e.getX()), new Integer(e.getY())});
                } else {
                    otherContestPopup.performAction("show", new Object[] {e.getComponent(), new Integer(e.getX()), new Integer(e.getY())});
                }
            } else if ((e.getClickCount() > 1) && SwingUtilities.isLeftMouseButton(e)) {
                infoPopupEvent();
            }
        }
    }

    private void headerClickEvent(MouseEvent e) {
        if (SwingUtilities.isLeftMouseButton(e)) {
            int col = ((JTableHeader)table.getProperty("TableHeader")).columnAtPoint(e.getPoint());
            if (col == -1) return;
            col = ((TableColumnModel)table.getProperty("ColumnModel")).getColumn(col).getModelIndex();
            tableModel.sort(
                            col,
                            (e.getModifiers() & MouseEvent.SHIFT_MASK) > 0
                            );
            table.performAction("repaint");
        }
    }

    private void infoPopupEvent() {
        int index = ((Integer) table.getProperty("SelectedRow")).intValue();
        String challengeHandle = ((UserNameEntry) table.performAction("getValueAt", new Object[] {new Integer(index), new Integer(2)})).getName();
        ca.setCurrentFrame((JFrame) frame.getFrame().getEventSource());
        ca.requestCoderInfo(challengeHandle, ((UserNameEntry) table.performAction("getValueAt", new Object[] {new Integer(index), new Integer(2)})).getUserType());
    }

    private void sourcePopupEvent() {
        int r = ((Integer) table.getProperty("SelectedRow")).intValue();
        int c = ((Integer)table.getProperty("SelectedColumn")).intValue();
        int fixedCol = fixedColIndex(c);
        // Any column EXCEPT the score columnx
        if (fixedCol == SUBMISSION_COUNT_COLUMN || fixedCol == EXAMPLE_COUNT_COLUMN) {
            Coder coder = tableModel.getCoder(r);
            int subnum = ((Integer) tableModel.getValueAt(r, fixedCol)).intValue();
            if (subnum == 0) {
                return;
            }
            if (viewer != null) {
                closeSourceViewer();
            }
            viewer = new LongCodeViewer(ca, (JFrame) frame.getFrame().getEventSource(), 
                                        room.getRoundModel().getRoundID().intValue(), 
                                        coder.getHandle(), 
                                        coder.getComponents()[0].getComponent().getID().intValue(),
                                        fixedCol == EXAMPLE_COUNT_COLUMN, 
                                        subnum, 
                                        true);
            viewer.show();
        }
    }



    /*
     * bold the room leader in the challenge table
     */
    private void updateRoomLeader() {
        tableModel.updateRoomLeader();
        panel.performAction("repaint");
    }
    
    private void historyPopupEvent(int type) {
        int index = ((Integer)table.getProperty("SelectedRow")).intValue();
        if (index >= 0) {
            String handle = tableModel.getCoder(index).getHandle();
            ca.setCurrentFrame((JFrame) frame.getFrame().getEventSource());
            if (type == 1) {
                ca.requestCoderHistory(handle, room.getRoomID().longValue(), tableModel.getCoder(index).getUserType());
            } else if (type == 2) {
                ca.requestSubmissionHistory(handle, room.getRoomID().longValue(), tableModel.getCoder(index).getUserType(), false);
            } else {
                ca.requestSubmissionHistory(handle, room.getRoomID().longValue(), tableModel.getCoder(index).getUserType(), true);
            }
        }
    }
    
    private void systemTestResultsEvent() {
        int index = ((Integer)table.getProperty("SelectedRow")).intValue();
        if (index >= 0) {
            Coder coder = tableModel.getCoder(index);
            String handle = coder.getHandle();
            ca.setCurrentFrame((JFrame) frame.getFrame().getEventSource());
            ca.getInterFrame().showMessage("Fetching results...", (JFrame) frame.getFrame().getEventSource(), ContestConstants.LONG_TEST_RESULTS_REQUEST);
            ca.getRequester().requestLongTestResults(coder.getComponents()[0].getComponent().getID().longValue(), room.getRoomID().longValue(), handle, 2);
        }
    }

    private void prepareForTableModel() {
        if (!room.hasRoundModel()) {
            throw new IllegalStateException(
                    "Can't build challenge table model, no round for room: " +
                    room
            );
        }
        RoundModel round = room.getRoundModel();
        if (!round.hasProblems(room.getDivisionID())) {
            throw new IllegalStateException(
                    "Can't build challenge table model, " +
                    "no problems for round: " + round
            );
        }
        ArrayList al_tableModelClasses = new ArrayList();
        ArrayList al_tableModelHeaders = new ArrayList();

        al_tableModelClasses.add(Integer.class);
        al_tableModelHeaders.add("Place");
        al_tableModelClasses.add(Integer.class);
        al_tableModelHeaders.add("R");
        al_tableModelClasses.add(UserNameEntry.class);
        al_tableModelHeaders.add("Handle");
        al_tableModelClasses.add(String.class);
        al_tableModelHeaders.add("Status");
        al_tableModelClasses.add(Double.class);
        endedContestLayout = false;
        if (isContestEnded()) {
            endedContestLayout = true;
        }
        al_tableModelHeaders.add("Final Score");
        al_tableModelClasses.add(Double.class);
        al_tableModelHeaders.add("Score");
        al_tableModelClasses.add(Integer.class);
        al_tableModelHeaders.add("Submissions");
        al_tableModelClasses.add(String.class);
        al_tableModelHeaders.add("Last submission");
        al_tableModelClasses.add(Integer.class);
        al_tableModelHeaders.add("Examples");
        al_tableModelClasses.add(String.class);
        al_tableModelHeaders.add("Last example");

        tableModelClasses = (Class[]) al_tableModelClasses.toArray(new Class[0]);
        tableModelHeaders = (String[]) al_tableModelHeaders.toArray(new String[0]);
    }


    public void updateChallengeTable(RoomModel room) {
        if (this.room != room) {
            throw new IllegalStateException(
                    "Got event for unrecognized room: " + room
            );
        }
        if (isEndedContestLayout() ^ isContestEnded()) {
            reinitTable();
        }
        tableModel.updateChallengeTable();
    }

    private final class ChallengeTableModel extends SortedTableModel
            implements Coder.Listener {

        private List coderList = new ArrayList();
        private RankComparator rankCompare = new RankComparator();
        
        private ChallengeTableModel() {
            super(tableModelHeaders, tableModelClasses);
            for(int i=0; i < 10; i++) {
                addSortElement(new SortElement(i, false));
            }
            updateChallengeTable();
        }

        private void updateChallengeTable() {
            if (!update) {
                return;
            }
            if (!room.hasCoders()) {
                throw new IllegalStateException("No coders for room: " + room);
            }
            Coder[] coders = room.getCoders();
            for (int i = 0; i < coders.length; i++) {
                coders[i].addListener(this);
            }

            update(Arrays.asList(coders));

            updateRoomLeader();
        }


        public void updateRoomLeader()  {
            if (!update) {
                return;
            }
            synchronized (coderList) {
                List tmpList = (List)this.getItemList();

                coderList.clear();
                for(int i = 0; i < tmpList.size(); i++) {
                    Coder c = (Coder)tmpList.get(i);
                    coderList.add(getRelevantScoreForStatus(c));
                }
    
                Collections.sort(coderList, this.rankCompare);

                sort();
            }
        }

        public int getCoderRank(Coder c)  {
            if(coderList == null) {
                return 1;
            }
            for(int i = 0; i < coderList.size(); i++) {
                Double v = getRelevantScoreForStatus(c);
                Double v1 = ((Double)coderList.get(i));
                if(v.doubleValue() == v1.doubleValue()) {
                    return i+1;
                }
            }
            return -1;
        }



        public void coderEvent(Coder coder) {
            fireTableDataChanged();
        }

        public Object getValueAt(int rowIndex, int columnIndex) {
            Coder coder = getCoder(rowIndex);
            return getColValue(coder, columnIndex);
        }

        private Object getColValue(Coder coder, int columnIndex) {
            int fixedIndex = columnIndex;
            LongCoderComponentImpl cc;
            switch (fixedIndex) {
                case 0 :
                    synchronized (coderList) {
                        return new Integer(getCoderRank(coder));
                    }
                case 1 :
                    return coder.getRating();
                case 2 :
                    return new UserNameEntry(coder.getHandle(),
                            coder.getRating().intValue(),
                            room.hasLeader() && room.getLeader().getUserName().equals(coder.getHandle()),
                            coder.getUserType());
                case 3 :
                    cc = (LongCoderComponentImpl) coder.getComponents()[0];
                    return cc.getStatus();
                case 4 :
                    return coder.getFinalScore() == null ? null : new Double(coder.getFinalScore().doubleValue()/100.0);
                case 5 :
                    return new Double(coder.getScore().doubleValue()/100.0);
                case 6 :
                    cc = (LongCoderComponentImpl) coder.getComponents()[0];
                    return new Integer(cc.getSubmissionCount());
                case 7 :
                    cc = (LongCoderComponentImpl) coder.getComponents()[0];
                    return cc.getLastSubmissionTime() == 0 ? null : new Date(cc.getLastSubmissionTime());
                case 8 :
                    cc = (LongCoderComponentImpl) coder.getComponents()[0];
                    return new Integer(cc.getExampleSubmissionCount());
                case 9 :
                    cc = (LongCoderComponentImpl) coder.getComponents()[0];
                    return cc.getExampleLastSubmissionTime() == 0 ? null : new Date(cc.getExampleLastSubmissionTime());
                default :
                    return null;
            }
        }
        
        
        private Double getRelevantScoreForStatus(Coder c) {
            if (isEndedContestLayout()) {
                return c.getFinalScore();
            } else {
                return c.getScore();
            }
        }

        Coder getCoder(int rowIndex) {
            return (Coder) get(rowIndex);
        }

        public int compare(Object o1, Object o2) {
            Coder c1 = (Coder) o1;
            Coder c2 = (Coder) o2;

            for (Iterator it = getSortListIterator(); it.hasNext();) {
                SortElement sortElem = (SortElement) it.next();
                int col = sortElem.getColumn();
                int sign = sortElem.isOpposite() ? -1 : 1;
                
                Comparable v1 = (Comparable) getColValue(c1, col);
                Comparable v2 = (Comparable) getColValue(c2, col);
                int res = 0;
                if (v1 != null) {
                    if (v2 != null) {
                        res = v1.compareTo(v2);
                    } else  {
                        res = 1;
                    }
                } else if (v2 != null) {
                    res = -1;
                } 
                if (res != 0) {
                    return sign*res;
                }
            }
            return 0;
        }
    }


    private final class RankComparator implements Comparator
    {
        public int compare(Object o1, Object o2) {
            Double c1 = (Double) o1;
            Double c2 = (Double) o2;


            double diff = c1.doubleValue() -
                    c2.doubleValue();
            if (diff != 0) return -1 * (diff > 0 ? 1 : -1);

            return 0;
        }

        protected int compareStrings(String s1, String s2) {
            if (s1 == null)
                return s2 == null ? 0 : -1;
            if (s2 == null)
                return 1;

            return s1.compareToIgnoreCase(s2);
        }

    }

    public void closeSourceViewer() {
        if (viewer != null) {
            viewer.close();
            viewer = null;
        }
    }
    
    public void setUpdate(boolean update) {
        this.update = update;
    }

    public void updateView(ResultDisplayType displayType) {
    }
}
