/*
 * Author: Michael Cervantes (emcee)
 * Date: Jun 15, 2002
 * Time: 9:59:24 PM
 */
package com.topcoder.client.contestMonitor.view.gui;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class DateChooser extends JPanel {

    private Calendar selectedDate = Calendar.getInstance();
    private Calendar displayedDate = selectedDate;
    private DateChooserTableModel tableModel = new DateChooserTableModel();
    private JTable table = new JTable(tableModel);
    private JDialog dialog;

    private DateFormat monthLabelFormatter = new SimpleDateFormat("MMMMMMMMMMMMM yyyy");

    private JButton prevMonthButton = new JButton("<");
    private JButton nextMonthButton = new JButton(">");
    private JLabel monthLabel = new JLabel("", JLabel.CENTER) {
        public Dimension getPreferredSize() {
            String old = getText();
            setText("SEPTEMBER 2000");
            Dimension r = super.getPreferredSize();
            setText(old);
            return r;
        }

        public Dimension getMinimumSize() {
            return getPreferredSize();
        }
    };

    public void setDate(Date date) {
        if (date != null)
            selectedDate.setTime(date);
        displayedDate = (Calendar) selectedDate.clone();
        displayedDate.set(Calendar.DAY_OF_MONTH, 1);
        tableModel.setDate(displayedDate);
        monthLabel.setText(monthLabelFormatter.format(date));
        updateSelectedDate();
        repaint();
    }

    public Date getDate() {
        return selectedDate.getTime();
    }

    private void nextMonth() {
        displayedDate.add(Calendar.MONTH, 1);
        tableModel.setDate(displayedDate);
        monthLabel.setText(monthLabelFormatter.format(displayedDate.getTime()));
        updateSelectedDate();
        repaint();
    }

    private void updateSelectedDate() {
        if (selectedDate.getTime().compareTo(displayedDate.getTime()) >= 0) {
            Calendar nextMonth = Calendar.getInstance();
            nextMonth.setLenient(true);
            nextMonth.setTime(displayedDate.getTime());
            nextMonth.add(Calendar.MONTH, 1);
            if (selectedDate.getTime().compareTo(nextMonth.getTime()) < 0) {
                int selectedRow = tableModel.getRow(selectedDate);
                int selectedCol = tableModel.getCol(selectedDate);
                table.setRowSelectionInterval(selectedRow, selectedRow);
                table.setColumnSelectionInterval(selectedCol, selectedCol);
            }
        } else {
            table.clearSelection();
        }
    }

    private void prevMonth() {
        displayedDate.add(Calendar.MONTH, -1);
        tableModel.setDate(displayedDate);
        monthLabel.setText(monthLabelFormatter.format(displayedDate.getTime()));
        updateSelectedDate();
        repaint();
    }

    public DateChooser() {
        super(new BorderLayout());
        build();
    }

    private void build() {
        JPanel top = new JPanel(new FlowLayout());
        nextMonthButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                nextMonth();
            }
        });
        prevMonthButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                prevMonth();
            }
        });
        top.add(prevMonthButton);
        top.add(monthLabel);
        top.add(nextMonthButton);
        add(top, BorderLayout.NORTH);

        for (int i = 0; i < 7; i++)
            table.getColumnModel().getColumn(i).setPreferredWidth(20);
        table.setRowHeight(20);
        table.setCellSelectionEnabled(true);
        table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        table.getTableHeader().setDefaultRenderer(new DefaultTableCellRenderer());
        table.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            protected void setValue(Object value) {
                setHorizontalTextPosition(JLabel.CENTER);
                setVerticalTextPosition(JLabel.CENTER);
                if (value instanceof String)
                    setFont(getFont().deriveFont(Font.ITALIC));
                super.setValue(value);
            }
        });
        table.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                Point clickPoint = e.getPoint();
                int row = table.rowAtPoint(clickPoint);
                if (row < 1) {
                    table.clearSelection();
                    return;
                }
                int col = table.columnAtPoint(clickPoint);
                Integer dayOfMonth = (Integer) tableModel.getValueAt(row, col);
                if (dayOfMonth != null) {
                    selectedDate = (Calendar) displayedDate.clone();
                    selectedDate.add(Calendar.DAY_OF_MONTH, dayOfMonth.intValue() - 1);
                    updateSelectedDate();
                    if (e.getClickCount() >= 2 && dialog != null) {
                        dialog.setVisible(false);
                    }
                } else {
                    table.clearSelection();
                }
            }
        });
        table.addKeyListener(new KeyAdapter() {
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    int row = table.getSelectedRow();
                    if (row < 1) {
                        table.clearSelection();
                        return;
                    }
                    int col = table.getSelectedColumn();
                    Integer dayOfMonth = (Integer) tableModel.getValueAt(row, col);
                    if (dayOfMonth != null) {
                        selectedDate = (Calendar) displayedDate.clone();
                        selectedDate.add(Calendar.DAY_OF_MONTH, dayOfMonth.intValue() - 1);
                        updateSelectedDate();
                        dialog.setVisible(false);
                    } else {
                        table.clearSelection();
                    }
                }
            }
        });
        add(table, BorderLayout.SOUTH);
        setDate(new Date());
    }

    class DateChooserTableModel extends AbstractTableModel {

        private int firstDayOfMonth;
        private int totalDaysInMonth;

        private final String[] colNames = new String[]{
            "S", "M", "T", "W", "TH", "F", "S"
        };

        public String getColumnName(int column) {
            return colNames[column];
        }

        int getRow(Calendar date) {
            return 1 + ((date.get(Calendar.DAY_OF_MONTH) + firstDayOfMonth - 1) / 7);
        }

        int getCol(Calendar date) {
            return (date.get(Calendar.DAY_OF_MONTH) + firstDayOfMonth - 1) % 7;
        }

        public void setDate(Calendar date) {
            Calendar myDate = (Calendar) date.clone();
            myDate.set(myDate.DATE, 1);
            firstDayOfMonth = myDate.get(Calendar.DAY_OF_WEEK) - 1;
            myDate.add(date.MONTH, 1);
            myDate.add(date.DATE, -1);
            totalDaysInMonth = myDate.get(myDate.DATE);
            fireTableDataChanged();
        }

        public int getRowCount() {
            return 7;
        }

        public int getColumnCount() {
            return 7;
        }

        public Object getValueAt(int rowIndex, int columnIndex) {
            if (rowIndex == 0)
                return colNames[columnIndex];
            int idx = ((rowIndex - 1) * 7) + columnIndex - firstDayOfMonth;
            if (idx < 0 || idx >= totalDaysInMonth)
                return null;
            return new Integer(idx + 1);
        }

        public boolean isCellEditable(int rowIndex, int columnIndex) {
            return false;
        }
    }

    public JDialog getDialog(JDialog parent) {
        if (dialog == null)
            buildDialog(parent);
        return dialog;
    }

    public JDialog getDialog(JFrame parent) {
        if (dialog == null)
            buildDialog(parent);
        return dialog;
    }

    private void buildDialog(JFrame parent) {
        dialog = new JDialog(parent, "Select Date", true);
        buildDialog();
    }

    private void buildDialog(JDialog parent) {
        dialog = new JDialog(parent, "Select Date", true);
        buildDialog();
    }

    private void buildDialog() {
        dialog.setContentPane(this);
        dialog.pack();
        dialog.setResizable(false);
    }

    public void scrollToSelectedDate() {
        setDate(selectedDate.getTime());
    }

    public static void main(String[] args) {
        JFrame f = new JFrame("Test");
        f.getContentPane().add(new DateChooser());
        f.pack();
        f.show();
    }
}


