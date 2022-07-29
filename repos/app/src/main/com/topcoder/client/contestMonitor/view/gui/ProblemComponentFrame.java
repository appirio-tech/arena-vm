/**
 * @author John Waymouth
 */
package com.topcoder.client.contestMonitor.view.gui;

import com.topcoder.client.contestMonitor.model.ContestManagementController;
import com.topcoder.client.contestMonitor.model.ProblemComponentsTableModel;
import com.topcoder.server.contest.Difficulty;
import com.topcoder.server.contest.Division;

import javax.swing.BorderFactory;
import javax.swing.DefaultCellEditor;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
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
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Collection;
import java.util.Iterator;
import java.util.Vector;

public class ProblemComponentFrame implements ContestManagementController.SetupListener {

    private JDialog frame;
    private ContestManagementController controller;
    private ProblemComponentsTableModel problemComponentsTableModel;
    private JTable problemComponentsTable;
    private JButton okButton;
    private JButton cancelButton;

    private JComboBox divisions,difficulty;


    public ProblemComponentFrame(ContestManagementController controller, JDialog parent) {
        frame = new JDialog(parent, "Problem Components Frame");
        this.controller = controller;
        problemComponentsTableModel = controller.getProblemComponentsTableModel();
        controller.registerSetupListener(this);
        build();
    }

    public void setup() {
        divisions.removeAllItems();
        for (Iterator it = controller.getDivisions().iterator(); it.hasNext();) {
            divisions.addItem((Division) it.next());
        }
        difficulty.removeAllItems();
        for (Iterator it = controller.getDifficultyLevels().iterator(); it.hasNext();) {
            difficulty.addItem((Difficulty) it.next());
        }
    }

    public void display() {
        frame.setLocationRelativeTo(frame.getParent());
        frame.setVisible(true);
    }

    private void build() {
        frame.setContentPane(new JPanel(new GridBagLayout()));

        buildProblemComponentsTable();
        problemComponentsTable.setPreferredScrollableViewportSize(new Dimension(830, 120));
        JScrollPane pane = new JScrollPane(problemComponentsTable);
        pane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        pane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        pane.setBorder(BorderFactory.createTitledBorder("Components"));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.BOTH;
        gbc.weightx = gbc.weighty = .5;
        gbc.anchor = GridBagConstraints.WEST;
        insert(pane, frame.getContentPane(), gbc, 0, 0, 1, 1);

        JPanel panel = new JPanel(new GridBagLayout());
        gbc.weightx = gbc.weighty = .1;
        gbc.anchor = GridBagConstraints.EAST;
        gbc.fill = GridBagConstraints.NONE;
        gbc.insets = new Insets(0, 2, 0, 3);
        cancelButton = new JButton("Cancel");
        cancelButton.setMnemonic('c');
        cancelButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if (problemComponentsTable.isEditing()) {
                    TableCellEditor ce = problemComponentsTable.getCellEditor();
                    if (ce != null)
                        ce.stopCellEditing();
                }
                frame.dispose();
            }
        });
        insert(cancelButton, panel, gbc, 0, 0, 1, 1);

        okButton = new JButton("OK");
        okButton.setMnemonic('O');
        okButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if (problemComponentsTable.isEditing()) {
                    TableCellEditor ce = problemComponentsTable.getCellEditor();
                    if (ce != null)
                        ce.stopCellEditing();
                }
                commit();
                frame.dispose();
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
        controller.setComponents(new Vector(problemComponentsTableModel.getComponents()));
    }

    private void buildProblemComponentsTable() {
        problemComponentsTable = new JTable(problemComponentsTableModel);
        problemComponentsTable.getColumnModel().setColumnSelectionAllowed(false);
        problemComponentsTable.setAutoResizeMode(JTable.AUTO_RESIZE_SUBSEQUENT_COLUMNS);
        problemComponentsTable.setRowHeight(20);
        for (int i = 0; i < 9; i++) {
            TableColumn col = problemComponentsTable.getColumnModel().getColumn(i);
            if (i == 0 || i == 6)
                col.setPreferredWidth(50);
            else if (i == 1 || i == 2)
                col.setPreferredWidth(150);
            else if (i == 4) {
                col.setPreferredWidth(90);
                Collection c = controller.getDivisions();
                divisions = new JComboBox(new Vector(c));
                col.setCellEditor(new DefaultCellEditor(divisions));
            } else if (i == 5) {
                col.setPreferredWidth(90);
                Collection c = controller.getDifficultyLevels();
                difficulty = new JComboBox(new Vector(c));
                col.setCellEditor(new DefaultCellEditor(difficulty));
            } else
                col.setPreferredWidth(100);
        }
        problemComponentsTable.getTableHeader().addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 1) {
                    int viewColumn = problemComponentsTable.getColumnModel().getColumnIndexAtX(e.getX());
                    int column = problemComponentsTable.convertColumnIndexToModel(viewColumn);
                    if (column != -1) {
                        boolean shiftPressed = (e.getModifiers() & InputEvent.SHIFT_MASK) != 0;
                        problemComponentsTableModel.sort(column, shiftPressed);
                    }
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
