package com.topcoder.client.launcher.management.ui;

import java.awt.Dimension;
import java.awt.Insets;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.swing.JButton;
import javax.swing.table.AbstractTableModel;

import com.topcoder.client.launcher.common.application.Application;
import com.topcoder.client.launcher.common.application.ApplicationList;

public class ApplicationListModel extends AbstractTableModel {
    private List appList = new ArrayList();

    private List buttonList = new ArrayList();

    public ApplicationListModel(ApplicationList apps) {
        for (Iterator iter = apps.iterator(); iter.hasNext();) {
            Application app = (Application) iter.next();
            JButton button = new JButton();

            appList.add(app);
            button.setActionCommand(app.getInfo().getId());
            button.setMargin(new Insets(8, 8, 8, 8));
            button.setMinimumSize(new Dimension(73, 26));
            button.setMaximumSize(new Dimension(73, 26));
            button.setPreferredSize(new Dimension(73, 26));
            buttonList.add(button);
        }
    }

    public Class getColumnClass(int columnIndex) {
        switch (columnIndex) {
        case 0:
            return Application.class;
        case 1:
            return String.class;
        case 2:
            return JButton.class;
        default:
            return void.class;
        }
    }

    public int getColumnCount() {
        return 3;
    }

    public String getColumnName(int columnIndex) {
        switch (columnIndex) {
        case 0:
            return "Applications & Plugins";
        case 1:
            return "Version";
        case 2:
            return "Install";
        default:
            return "Error";
        }
    }

    public int getRowCount() {
        return appList.size();
    }

    public Object getValueAt(int rowIndex, int columnIndex) {
        switch (columnIndex) {
        case 0:
            return appList.get(rowIndex);
        case 1:
            return ((Application) appList.get(rowIndex)).getInfo().getVersion();
        case 2:
            JButton button = (JButton) buttonList.get(rowIndex);
            String text = ((Application) appList.get(rowIndex)).getInfo().isInstalled() ? "Uninstall" : "Install";

            button.setText(text);

            return button;
        default:
            return null;
        }
    }

    public boolean isCellEditable(int rowIndex, int columnIndex) {
        return columnIndex == 2;
    }
}
