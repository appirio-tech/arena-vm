/*
 * User: Mike Cervantes (emcee)
 * Date: May 16, 2002
 * Time: 10:57:26 PM
 */
package com.topcoder.client.contestMonitor.view.gui;

import com.topcoder.client.contestMonitor.model.AssignedProblemsTableModel;
import com.topcoder.client.contestMonitor.model.AvailableProblemsTableModel;
import com.topcoder.client.contestMonitor.model.ContestManagementController;
import com.topcoder.client.contestMonitor.model.ResponseWaiter;
import com.topcoder.client.contestMonitor.model.WrappedResponseWaiter;
import com.topcoder.server.contest.Division;
import com.topcoder.server.contest.ProblemData;
import com.topcoder.server.contest.RoundData;
import com.topcoder.server.contest.RoundProblemData;

import javax.swing.BorderFactory;
import javax.swing.DefaultCellEditor;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JOptionPane;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableColumn;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.InputEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.Vector;
import java.util.ArrayList;

public class RoundProblemFrame implements ContestManagementController.SetupListener {

    private JDialog frame;
    private ProblemComponentFrame problemComponentFrame;
    private ContestManagementController controller;
    private RoundData round;
    private AssignedProblemsTableModel assignedProblemsTableModel;
    private AvailableProblemsTableModel availableProblemsTableModel;
    private JTable assignedProblemsTable;
    private JTable availableProblemsTable;
    private JButton addButton;
    private JButton deleteButton;
    private JButton okButton;
    private JButton cancelButton;
    private JButton componentsButton;
    private JTextField searchText;
    private ResponseWaiter waiter;
    private Collection nonMatchingProblems;

    private JComboBox divisions;

    private boolean disposeMe = false;

    public RoundProblemFrame(ContestManagementController controller, JDialog parent) {
        frame = new JDialog(parent, "Round Problem Frame");
        nonMatchingProblems = new ArrayList();
        this.controller = controller;
        assignedProblemsTableModel = controller.getAssignedProblemsTableModel();
        availableProblemsTableModel = controller.getAvailableProblemsTableModel();
        problemComponentFrame = new ProblemComponentFrame(controller, parent);
        waiter = new WrappedResponseWaiter(new FrameWaiter(frame)) {
            protected void _waitForResponse() {
                disableButtons();
            }

            protected void _errorResponseReceived(Throwable t) {
                enableButtons();
            }

            protected void _responseReceived() {
                enableButtons();
                if (disposeMe) {
                    frame.dispose();
                    (RoundProblemFrame.this.controller).removeProblems();
                }
                disposeMe = false;
            }
        };
        controller.registerSetupListener(this);
        build();
    }

    public void setup() {
        divisions.removeAllItems();
        for (Iterator it = controller.getDivisions().iterator(); it.hasNext();) {
            divisions.addItem((Division) it.next());
        }
    }

    private void enableButtons() {
        okButton.setEnabled(true);
        cancelButton.setEnabled(true);
        addButton.setEnabled(true);
        deleteButton.setEnabled(true);
        componentsButton.setEnabled(true);
    }

    private void disableButtons() {
        okButton.setEnabled(false);
        cancelButton.setEnabled(false);
        addButton.setEnabled(false);
        deleteButton.setEnabled(false);
        componentsButton.setEnabled(false);
    }

    public void display(RoundData round) {
        this.round = round;
        frame.setLocationRelativeTo(frame.getParent());
        frame.setVisible(true);
    }


    private void build() {
        frame.setContentPane(new JPanel(new GridBagLayout()));

        buildAvailableProblemTable();
        availableProblemsTable.setPreferredScrollableViewportSize(new Dimension(370, 200));
        JScrollPane pane = new JScrollPane(availableProblemsTable);
        pane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        pane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        pane.setBorder(BorderFactory.createTitledBorder("Available"));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.BOTH;
        gbc.weightx = gbc.weighty = .5;
        gbc.anchor = GridBagConstraints.WEST;
        insert(pane, frame.getContentPane(), gbc, 0, 0, 1, 1);

        buildAssignedProblemTable();
        assignedProblemsTable.setPreferredScrollableViewportSize(new Dimension(460, 120));
        pane = new JScrollPane(assignedProblemsTable);
        pane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        pane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        pane.setBorder(BorderFactory.createTitledBorder("Assigned"));

        gbc.anchor = GridBagConstraints.EAST;
        insert(pane, frame.getContentPane(), gbc, 0, 2, 1, 1);


        JPanel panel = new JPanel(new GridBagLayout());
        JButton add = addButton = buildAddButton();
        JButton delete = deleteButton = buildDeleteButton();
        JButton components = componentsButton = buildComponentsButton();
        JLabel searchLabel = new JLabel("Search: ");
        JTextField search = searchText = buildSearchText();
        add.setMnemonic('A');
        delete.setMnemonic('U');
        components.setMnemonic('P');

        gbc.weightx = 0;
        gbc.weighty = 0.1;
        gbc.fill = GridBagConstraints.NONE;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(0, 0, 0, 0);
        insert(add, panel, gbc, 0, 0, 1, 1);
        gbc.insets = new Insets(0, 5, 0, 0);
        gbc.weightx = .1;
        insert(delete, panel, gbc, 1, 0, 1, 1);
        insert(components, panel, gbc, 2, 0, 1, 1);
        insert(searchLabel, panel, gbc, 3, 0, 1, 1);
        insert(search, panel, gbc, 4, 0, 1, 1);
        gbc.insets = new Insets(10, 3, 10, 3);
        insert(panel, frame.getContentPane(), gbc, 0, 1, 1, 1);

        panel = new JPanel(new GridBagLayout());
        gbc.weightx = gbc.weighty = .1;
        gbc.anchor = GridBagConstraints.EAST;
        gbc.fill = GridBagConstraints.NONE;
        gbc.insets = new Insets(0, 2, 0, 3);
        cancelButton = new JButton("Cancel");
        cancelButton.setMnemonic('c');
        cancelButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if (assignedProblemsTable.isEditing()) {
                    TableCellEditor ce = assignedProblemsTable.getCellEditor();
                    if (ce != null)
                        ce.stopCellEditing();
                }
                controller.removeProblems();
                frame.dispose();
            }
        });
        insert(cancelButton, panel, gbc, 0, 0, 1, 1);

        okButton = new JButton("OK");
        okButton.setMnemonic('O');
        okButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if (controller.canSetProblems()) {
                    disposeMe = true;
                    if (assignedProblemsTable.isEditing()) {
                        TableCellEditor ce = assignedProblemsTable.getCellEditor();
                        if (ce != null)
                            ce.stopCellEditing();
                    }
                    commit();
                } else {
                    JOptionPane.showMessageDialog(null, "You must enter information (such as difficulty " +
                            "level and division)\nfor all components of all problems you have selected.", "Information needed", JOptionPane.ERROR_MESSAGE);
                }
            }
        });
        insert(okButton, panel, gbc, 1, 0, 1, 1);

        gbc.anchor = GridBagConstraints.SOUTHEAST;
        gbc.fill = GridBagConstraints.NONE;
        gbc.insets = new Insets(10, 3, 5, 3);
        insert(panel, frame.getContentPane(), gbc, 0, 3, 1, 1);

        frame.pack();
    }

    private void commit() {
        controller.setProblems(round, waiter);
    }


    private void unassignProblemEvent() {
        int[] rows = assignedProblemsTable.getSelectedRows();
        Arrays.sort(rows);
        for (int i = rows.length - 1; i >= 0; i--) {
            int row = rows[i];
            if (row >= 0) {
                controller.removeProblem(assignedProblemsTableModel.getRoundProblem(row));
                assignedProblemsTableModel.removeProblem(row);
            }
        }
    }

    private void assignProblemEvent() {
        int[] rows = availableProblemsTable.getSelectedRows();
        for (int i = 0; i < rows.length; i++) {
            int row = rows[i];
            if (row >= 0) {
                ProblemData data = availableProblemsTableModel.getProblem(row);
                assignedProblemsTableModel.add(new RoundProblemData(data, new Division()));
            }
        }
    }

    private void editComponentsEvent() {
        int rows[] = assignedProblemsTable.getSelectedRows();

        if (rows.length != 1)
            return;

        final RoundProblemData rp = assignedProblemsTableModel.getRoundProblem(rows[0]);
        if (rp.getDivision().getId() == 0) {
            JOptionPane.showMessageDialog(null, "You must specify the division for this problem before editting components",
                    "Division Needed", JOptionPane.ERROR_MESSAGE);
            return;
        }

        controller.getComponents(round, assignedProblemsTableModel.getRoundProblem(rows[0]),
                new WrappedResponseWaiter(waiter) {
                    protected void _waitForResponse() {
                        disableButtons();
                    }

                    protected void _errorResponseReceived(Throwable t) {
                        enableButtons();
                    }

                    protected void _responseReceived() {
                        enableButtons();
                        controller.setDivision(rp.getDivision());
                        problemComponentFrame.display();
                    }
                });
    }

    private JButton buildDeleteButton() {
        JButton delete = new JButton("Unassign");
        delete.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                unassignProblemEvent();
            }
        });
        return delete;
    }

    private JButton buildAddButton() {
        JButton add = new JButton("Assign");
        add.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                assignProblemEvent();
            }
        });
        return add;
    }

    private JButton buildComponentsButton() {
        JButton components = new JButton("Components");

        components.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                editComponentsEvent();
            }
        });
        return components;
    }
    
    private JTextField buildSearchText() {
        final JTextField search = new JTextField(10);
        search.getDocument().addDocumentListener(new DocumentListener() {
            public void insertUpdate(DocumentEvent e) {
                String text = search.getText().toLowerCase();
                Object[] problems = availableProblemsTableModel.getProblems().toArray();
                for(int i = 0; i < problems.length; i++) {
                    if(!((ProblemData)problems[i]).getName().toLowerCase().startsWith(text)) {
                        if(availableProblemsTableModel.remove(problems[i])) {
                            nonMatchingProblems.add(problems[i]);
                        }
                    }
                }
            }

            public void removeUpdate(DocumentEvent e) {
                String text = search.getText().toLowerCase();
                Object[] problems = nonMatchingProblems.toArray();
                for(int i = 0; i < problems.length; i++) {
                    if(((ProblemData)problems[i]).getName().toLowerCase().startsWith(text)) {
                        availableProblemsTableModel.add(problems[i]);
                        nonMatchingProblems.remove(problems[i]);
                    }
                }
            }
            

            public void changedUpdate(DocumentEvent e) {
            }
        });
        return search;
    }


    private void buildAssignedProblemTable() {
        assignedProblemsTable = new JTable(assignedProblemsTableModel);
        assignedProblemsTable.getColumnModel().setColumnSelectionAllowed(false);
        assignedProblemsTable.setAutoResizeMode(JTable.AUTO_RESIZE_SUBSEQUENT_COLUMNS);
        assignedProblemsTable.setRowHeight(20);
        for (int i = 0; i < 5; i++) {
            TableColumn col = assignedProblemsTable.getColumnModel().getColumn(i);
            if (i == 0) // ID
                col.setPreferredWidth(50);
            else if (i == 1) // problem name
                col.setPreferredWidth(150);
            else if (i == 2) // type
                col.setPreferredWidth(100);
            else if (i == 3) // status
                col.setPreferredWidth(70);
            else if (i == 4) { // division
                col.setPreferredWidth(90);
                Collection c = controller.getDivisions();
                divisions = new JComboBox(new Vector(c));
                col.setCellEditor(new DefaultCellEditor(divisions));
            }

        }
        assignedProblemsTable.getTableHeader().addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 1) {
                    int viewColumn = assignedProblemsTable.getColumnModel().getColumnIndexAtX(e.getX());
                    int column = assignedProblemsTable.convertColumnIndexToModel(viewColumn);
                    if (column != -1) {
                        boolean shiftPressed = (e.getModifiers() & InputEvent.SHIFT_MASK) != 0;
                        assignedProblemsTableModel.sort(column, shiftPressed);
                    }
                }
            }
        });
        assignedProblemsTable.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() >= 2) {
                    componentsButton.doClick();
                }
            }
        });
        assignedProblemsTable.addKeyListener(new KeyAdapter() {
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_DELETE) {
                    deleteButton.doClick();
                } else if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    componentsButton.doClick();
                }
            }
        });
    }


    private void buildAvailableProblemTable() {
        final JTable table = availableProblemsTable = new JTable(availableProblemsTableModel);
        table.getColumnModel().setColumnSelectionAllowed(false);
        table.setAutoResizeMode(JTable.AUTO_RESIZE_SUBSEQUENT_COLUMNS);
        for (int i = 0; i < 4; i++) {
            TableColumn col = availableProblemsTable.getColumnModel().getColumn(i);
            if (i == 0) // ID
                col.setPreferredWidth(50);
            else if (i == 1) // problem name
                col.setPreferredWidth(150);
            else if (i == 2) // type
                col.setPreferredWidth(100);
            else if (i == 3) // status
                col.setPreferredWidth(70);
        }
        table.getTableHeader().addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 1) {
                    int viewColumn = table.getColumnModel().getColumnIndexAtX(e.getX());
                    int column = table.convertColumnIndexToModel(viewColumn);
                    if (column != -1) {
                        boolean shiftPressed = (e.getModifiers() & InputEvent.SHIFT_MASK) != 0;
                        availableProblemsTableModel.sort(column, shiftPressed);
                    }
                }
            }
        });
        table.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() >= 2) {
                    addButton.doClick();
                }
            }
        });
        table.addKeyListener(new KeyAdapter() {
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    addButton.doClick();
                }
            }
        });
    }

    private void insert(Component comp, Container container, GridBagConstraints gbc, int x, int y, int xspan, int yspan) {
        gbc.gridx = x;
        gbc.gridy = y;
        gbc.gridwidth = xspan;
        gbc.gridheight = yspan;
        container.add(comp, gbc);
    }
}
