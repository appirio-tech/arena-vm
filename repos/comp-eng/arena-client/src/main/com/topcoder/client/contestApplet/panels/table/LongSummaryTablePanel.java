/*
 * LongSummaryTablePanel
 * 
 * Created 06/13/2007
 */
package com.topcoder.client.contestApplet.panels.table;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.lang.reflect.InvocationTargetException;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import javax.swing.DefaultListSelectionModel;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.border.EmptyBorder;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;

import com.topcoder.client.SortElement;
import com.topcoder.client.SortedTableModel;
import com.topcoder.client.contestApplet.ContestApplet;
import com.topcoder.client.contestApplet.common.Common;
import com.topcoder.client.contestApplet.common.LocalPreferences;
import com.topcoder.client.contestApplet.common.MenuItemInfo;
import com.topcoder.client.contestApplet.common.PopUpHelper;
import com.topcoder.client.contestApplet.widgets.CellRendererFactory;
import com.topcoder.client.contestApplet.widgets.ContestTableCellRenderer;
import com.topcoder.client.contestApplet.widgets.ContestTableHeaderRenderer;
import com.topcoder.client.contestApplet.widgets.LanguageColoringDecoratorRenderer;
import com.topcoder.client.contestApplet.widgets.LongCodeViewer;
import com.topcoder.client.contestApplet.widgets.TCIcon;
import com.topcoder.client.contestApplet.widgets.ValueTransformDecoratorCellRenderer;
import com.topcoder.client.contestant.Coder;
import com.topcoder.client.contestant.CoderComponent;
import com.topcoder.client.contestant.Contestant;
import com.topcoder.client.contestant.RoomModel;
import com.topcoder.client.contestant.RoundModel;
import com.topcoder.client.contestant.impl.LongCoderComponentImpl;
import com.topcoder.netCommon.contest.ContestConstants;
import com.topcoder.netCommon.contest.ResultDisplayType;

/**
 * Long summary table panel
 * 
 * @autor Diego Belfer (Mural)
 * @version $Id: LongSummaryTablePanel.java 67962 2008-01-15 15:57:53Z mural $
 */
public final class LongSummaryTablePanel extends AbstractSummaryTablePanel {
    private static final int EXAMPLE_COUNT_COLUMN = 8;
    private static final int SUBMISSION_COUNT_COLUMN = 6;
    private static final boolean headersVisible = true;
    private final SimpleDateFormat dateFormat = new SimpleDateFormat("MM-dd-yyyy HH:mm:ss");
    
    
    private final ContestApplet ca;
    private final Contestant model;
    private final RoomModel room;
    private ChallengeTableModel tableModel;
    private JTable table;
    private JPopupMenu submissionPopup;
    private JPopupMenu otherContestPopup;
    private JPopupMenu endedContestPopup;
    
    private Class[] tableModelClasses;
    private String[] tableModelHeaders;
    private boolean enabled = true;
    private JFrame frame = null;
    private boolean update;
    private LongCodeViewer viewer;
    
    private final MenuItemInfo[] ON_USER_POPUP = {
            new MenuItemInfo("Info", new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    infoPopupEvent();
                }
            }),
            new MenuItemInfo("History", new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    historyPopupEvent(1);
                }
            }),
            new MenuItemInfo("Submission History", new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    historyPopupEvent(2);
                }
            }),
            new MenuItemInfo("Example History", new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    historyPopupEvent(3);
                }
            })
        };
    
    private final MenuItemInfo[] ON_END_CONTEST_POPUP = {
            new MenuItemInfo("Info", new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    infoPopupEvent();
                }
            }),
            new MenuItemInfo("History", new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    historyPopupEvent(1);
                }
            }),
            new MenuItemInfo("Submission History", new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    historyPopupEvent(2);
                }
            }),
            new MenuItemInfo("Example History", new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    historyPopupEvent(3);
                }
            }),
            new MenuItemInfo("System test results", new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    systemTestResultsEvent();
                }
            })
        };

    private final MenuItemInfo[] ON_SUBMISSION_POPUP = {new MenuItemInfo(
            "Source",
            new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    sourcePopupEvent();
                }
            })};
    private boolean endedContestLayout;


    
    
    public LongSummaryTablePanel(ContestApplet ca, RoomModel room, JFrame cr, boolean update) {
        super(new GridBagLayout());
        
        UIManager.put("MenuItem.selectionBackground", Common.HB_COLOR);
        
        this.ca = ca;
        this.model = ca.getModel();
        this.room = room;
        this.update= update;
        
        submissionPopup = PopUpHelper.createPopupMenu("Info", ON_SUBMISSION_POPUP);
        otherContestPopup = PopUpHelper.createPopupMenu("", ON_USER_POPUP);
        endedContestPopup = PopUpHelper.createPopupMenu("", ON_END_CONTEST_POPUP);
        this.frame = cr;
        initTable();
    }

    private void initTable() {
        prepareForTableModel();
        this.tableModel = new ChallengeTableModel();
        tableModel.addTableModelListener(new TableModelListener() {
            public void tableChanged(TableModelEvent e) {
                updateRoomLeader();
            }
        });
        this.table = createTable();
        createTablePanel();
    }
    
    
    private void reinitTable() {
        try {
            SwingUtilities.invokeAndWait(new Runnable() {
                public void run() {
                    LongSummaryTablePanel.this.remove(0);
                    initTable();
                    LongSummaryTablePanel.this.getRootPane().validate();
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
        int colCount = table.getColumnModel().getColumnCount();
        for (int i = 0; i < colCount; i++) {
            TableColumn col = table.getColumnModel().getColumn(i);
            ContestTableHeaderRenderer headerRenderer = new ContestTableHeaderRenderer(headersVisible, fontName, fontSize);
            col.setHeaderRenderer(headerRenderer);
            int fixedIndex = fixedColIndex(i);
            if (fixedIndex == 0) {
                col.setPreferredWidth(25);
                col.setCellRenderer(new ContestTableCellRenderer(fontName,fontSize));
            } else if (fixedIndex == 1) {
                // Rating
                col.setPreferredWidth(TCIcon.DEFAULT_WIDTH * 2 + 3);
                col.setCellRenderer(new RankRenderer());
            } else if (fixedIndex == 2) {
                // Handle
                col.setPreferredWidth(115);
                col.setCellRenderer(new UserNameRenderer(model, fontName, fontSize));
            } else if (fixedIndex == 3) {
                //status
                col.setPreferredWidth(140);
                TableCellRenderer renderer = new ComponentStatusRenderer(new ContestTableCellRenderer(fontName, fontSize)); 
                col.setCellRenderer(new LanguageColoringDecoratorRenderer(renderer) {
                    protected Integer getLanguage(Object value, int row, int col) {
                        Coder coder = tableModel.getCoder(row);
                        return coder.getComponents()[0].getLanguageID();
                    }
                });
            } else if (fixedIndex == 4) {
                col.setPreferredWidth(100);
                headerRenderer.setHorizontalAlignment(SwingConstants.CENTER);
                
                TableCellRenderer renderer = CellRendererFactory.create(fontName, fontSize, new DecimalFormat("0.00"), SwingConstants.RIGHT);
                col.setCellRenderer(new LanguageColoringDecoratorRenderer(renderer) {
                    protected Integer getLanguage(Object value, int row, int col) {
                        Coder coder = tableModel.getCoder(row);
                        return coder.getComponents()[0].getLanguageID();
                    }
                });
            } else if (fixedIndex == 5) {
                //score
                headerRenderer.setHorizontalAlignment(SwingConstants.CENTER);
                col.setPreferredWidth(100);
                TableCellRenderer renderer = CellRendererFactory.create(fontName, fontSize, new DecimalFormat("0.00"), SwingConstants.RIGHT);
                col.setCellRenderer(new LanguageColoringDecoratorRenderer(renderer) {
                    protected Integer getLanguage(Object value, int row, int col) {
                        Coder coder = tableModel.getCoder(row);
                        return coder.getComponents()[0].getLanguageID();
                    }
                });
            } else if (fixedIndex == SUBMISSION_COUNT_COLUMN) {
                //submission count
                headerRenderer.setHorizontalAlignment(SwingConstants.CENTER);
                col.setPreferredWidth(80);
                TableCellRenderer renderer = CellRendererFactory.create(fontName, fontSize, SwingConstants.RIGHT);
                col.setCellRenderer(new LanguageColoringDecoratorRenderer(renderer) {
                    protected Integer getLanguage(Object value, int row, int col) {
                        Coder coder = tableModel.getCoder(row);
                        return coder.getComponents()[0].getLanguageID();
                    }
                });
            } else if (fixedIndex == 7) {
                //last submission date
                headerRenderer.setHorizontalAlignment(SwingConstants.CENTER);
                col.setPreferredWidth(140);
                TableCellRenderer renderer = CellRendererFactory.create(fontName, fontSize, dateFormat, SwingConstants.CENTER);
                col.setCellRenderer(new LanguageColoringDecoratorRenderer(renderer) {
                    protected Integer getLanguage(Object value, int row, int col) {
                        Coder coder = tableModel.getCoder(row);
                        return coder.getComponents()[0].getLanguageID();
                    }
                });
            } else if (fixedIndex == EXAMPLE_COUNT_COLUMN) {
                //example submission count
                headerRenderer.setHorizontalAlignment(SwingConstants.CENTER);
                col.setPreferredWidth(80);
                TableCellRenderer renderer = CellRendererFactory.create(fontName, fontSize, SwingConstants.RIGHT);
                col.setCellRenderer(new LanguageColoringDecoratorRenderer(renderer) {
                    protected Integer getLanguage(Object value, int row, int col) {
                        Coder coder = tableModel.getCoder(row);
                        return new Integer(((LongCoderComponentImpl)coder.getComponents()[0]).getExampleLastLanguage());
                    }
                });
            } else if (fixedIndex == 9) {
                //last example submission date
                headerRenderer.setHorizontalAlignment(SwingConstants.CENTER);
                col.setPreferredWidth(140);
                TableCellRenderer renderer = CellRendererFactory.create(fontName, fontSize, dateFormat, SwingConstants.CENTER);
                col.setCellRenderer(new LanguageColoringDecoratorRenderer(renderer) {
                    protected Integer getLanguage(Object value, int row, int col) {
                        Coder coder = tableModel.getCoder(row);
                        return new Integer(((LongCoderComponentImpl)coder.getComponents()[0]).getExampleLastLanguage());
                    }
                });    
            }
        }

        return (table);
    }

    private int fixedColIndex(int i) {
        if (i >= 4 && !isEndedContestLayout()) {
            return i + 1;
        }
        return i;
    }

    protected boolean isEndedContestLayout() {
        return endedContestLayout;
    }
    
    protected boolean isContestEnded() {
        return this.room.getRoundModel().getPhase().intValue() == ContestConstants.CONTEST_COMPLETE_PHASE;
    }

    private static void setHeaders(JTable table) {
        String fontName = LocalPreferences.getInstance().getFont(LocalPreferences.SUMMARYFONT);
        int fontSize = LocalPreferences.getInstance().getFontSize(LocalPreferences.SUMMARYFONTSIZE);
        
        // set the default renderers for the headers/cells
        for (int i = 0; i < table.getColumnModel().getColumnCount(); i++) {
            table.getColumnModel().getColumn(i).setHeaderRenderer(
                    new ContestTableHeaderRenderer(headersVisible,fontName, fontSize)
            );
            table.getColumnModel().getColumn(i).setCellRenderer(
                    new ContestTableCellRenderer(fontName,fontSize)
            );
        }
    }

    private void createTablePanel() {
        String title = "Details Table";
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

    private void showSubmissionPopup(MouseEvent e) {
        submissionPopup.show(e.getComponent(), e.getX(), e.getY());
    }


    
    
    public JComponent getTable() {
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
                    endedContestPopup.show(e.getComponent(), e.getX(), e.getY());
                } else {
                    otherContestPopup.show(e.getComponent(), e.getX(), e.getY());
                }
            } else if ((e.getClickCount() > 1) && SwingUtilities.isLeftMouseButton(e)) {
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
        String challengeHandle = ((UserNameEntry) table.getValueAt(index, 2)).getName();
        ca.setCurrentFrame(frame);
        ca.requestCoderInfo(challengeHandle, ((UserNameEntry) table.getValueAt(index, 2)).getUserType());
    }

    private void sourcePopupEvent() {

        int r = table.getSelectedRow();
        int c = table.getSelectedColumn();
        int fixedCol = fixedColIndex(c);
        // Any column EXCEPT the score columnx
        if (fixedCol == SUBMISSION_COUNT_COLUMN || fixedCol == EXAMPLE_COUNT_COLUMN) {
            Coder coder = tableModel.getCoder(r);
            int subnum = ((Integer) tableModel.getValueAt(r, c)).intValue();
            if (subnum == 0) {
                return;
            }
            if (viewer != null) {
                closeSourceViewer();
            }
            viewer = new LongCodeViewer(ca, frame, 
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
        repaint();
    }
    
     public void updateView(ResultDisplayType viewType) {
         //This is not used for MM problems
     }

    private void historyPopupEvent(int type) {
        int index = table.getSelectedRow();
        if (index >= 0) {
            String handle = tableModel.getCoder(index).getHandle();
            ca.setCurrentFrame(frame);
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
        int index = table.getSelectedRow();
        if (index >= 0) {
            Coder coder = tableModel.getCoder(index);
            String handle = coder.getHandle();
            ca.setCurrentFrame(frame);
            ca.getInterFrame().showMessage("Fetching results...", this, ContestConstants.LONG_TEST_RESULTS_REQUEST);
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
            al_tableModelHeaders.add("Final Score");
            al_tableModelClasses.add(Double.class);
        }
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

    private static class ComponentStatusRenderer extends ValueTransformDecoratorCellRenderer {
        public ComponentStatusRenderer(TableCellRenderer renderer) {
            super(renderer);
        }
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
            int fixedIndex = fixedColIndex(columnIndex);
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
}
