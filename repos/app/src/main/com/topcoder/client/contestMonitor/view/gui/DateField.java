/*
 * User: Mike Cervantes (emcee)
 * Date: May 17, 2002
 * Time: 6:15:57 AM
 */
package com.topcoder.client.contestMonitor.view.gui;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTextField;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class DateField extends JPanel {

    private JComboBox hour = new JComboBox();
    private JComboBox min = new JComboBox();
    private JComboBox ampm = new JComboBox();

    private JTextField dateField = new JTextField();
    private JButton setButton = new JButton("Set");
    private JDialog dialog = new JDialog();

    private DateChooser datePicker = new DateChooser();
    private NumberFormat numFormat = new DecimalFormat("00");


    public Date getDate() {
        Calendar cal = Calendar.getInstance();
        cal.setTime(datePicker.getDate());
        cal.set(Calendar.HOUR, ((Integer) hour.getSelectedItem()).intValue() % 12);
        cal.set(Calendar.AM_PM, ampm.getSelectedItem().equals("AM") ? Calendar.AM : Calendar.PM);
        cal.set(Calendar.MINUTE, min.getSelectedIndex());
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        return cal.getTime();
    }

    public void setDate(Date date) {
        Calendar cal = Calendar.getInstance();
        if (date == null)
            date = new Date();
        cal.setTime(date);
        dateField.setText(format.format(date));
        datePicker.setDate(date);

        int h = cal.get(Calendar.HOUR);
        hour.setSelectedItem(new Integer(h == 0 ? 12 : h));
        ampm.setSelectedItem(cal.get(Calendar.AM_PM) == Calendar.AM ? "AM" : "PM");
        min.setSelectedIndex(cal.get(Calendar.MINUTE));
    }


    public DateField(JDialog parent) {
        super(new GridBagLayout());
        dialog = datePicker.getDialog(parent);
        build();
    }

    public DateField(JFrame parent) {
        super(new GridBagLayout());
        dialog = datePicker.getDialog(parent);
        build();
    }

    private DateFormat format = new SimpleDateFormat("MMMMMMMMMMMMM dd, yyyy");

    private void build() {
        dateField.setColumns(12);
        dateField.setMaximumSize(dateField.getPreferredSize());
        dateField.setEditable(false);
        setButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                datePicker.scrollToSelectedDate();
                dialog.setLocationRelativeTo(dialog.getParent());
                dialog.setVisible(true);
                dateField.setText(format.format(datePicker.getDate()));
            }
        });
        setButton.setMaximumSize(setButton.getPreferredSize());

        for (int i = 1; i <= 12; i++) {
            hour.addItem(new Integer(i));
        }

        for (int i = 0; i < 60; i++) {
            min.addItem(numFormat.format(i));
        }

        ampm.addItem("PM");
        ampm.addItem("AM");

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.weightx = 0;
        gbc.weighty = 0;
        gbc.fill = GridBagConstraints.NONE;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(0, 0, 0, 2);
        gbc.gridx = 0;
        gbc.gridy = 0;

        gbc.gridwidth = 3;
        add(dateField, gbc);
        gbc.insets = new Insets(0, 2, 1, 2);
        gbc.weightx = .1;
        gbc.gridwidth = 1;
        gbc.gridx = 3;
        add(setButton, gbc);

        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.gridwidth = 1;
        gbc.weightx = .1;
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.insets = new Insets(0, 0, 0, 2);
        this.add(hour, gbc);
        gbc.insets = new Insets(1, 2, 0, 2);
        gbc.gridx = 1;
        this.add(min, gbc);
        gbc.gridx = 2;
        this.add(ampm, gbc);
    }
}
