package com.topcoder.client.contestApplet.panels.table;

import com.topcoder.client.contestApplet.ContestApplet;
import com.topcoder.client.contestApplet.widgets.TCIcon;
import com.topcoder.client.contestApplet.common.*;
import com.topcoder.client.contestant.view.UserListListener;
import com.topcoder.client.contestApplet.widgets.ContestTableHeaderRenderer;
//import com.topcoder.client.contestant.*;
import com.topcoder.netCommon.contestantMessages.response.data.*;
import java.awt.Font;

import javax.swing.*;
import javax.swing.table.TableColumn;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.border.TitledBorder;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.*;


public class UserTablePanel extends TablePanel implements UserListListener {

    final MenuItemInfo[] USER_POPUP = {
        new MenuItemInfo("Info", new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                infoPopupEvent();
            }
        })};

    private String title = "";
    private UserTableModel userTableModel;
    
    public boolean isEnabled() {
        return enabled;
    }
    
    private boolean enabled = true;
    
    public void setPanelEnabled(boolean on) {
        enabled = on;
    }
    
    protected void setupFonts() {
        ((TitledBorder) getBorder()).setTitleFont(new Font(LocalPreferences.getInstance().getFont(LocalPreferences.USERTABLEFONT), Font.BOLD, 
                LocalPreferences.getInstance().getFontSize(LocalPreferences.USERTABLEFONTSIZE)));
        ContestTableHeaderRenderer r = (ContestTableHeaderRenderer) getTable().getColumnModel().getColumn(0).getHeaderRenderer();
        r.setFont(new Font(LocalPreferences.getInstance().getFont(LocalPreferences.USERTABLEFONT), Font.PLAIN, 
                LocalPreferences.getInstance().getFontSize(LocalPreferences.USERTABLEFONTSIZE)));
        
        r = (ContestTableHeaderRenderer) getTable().getColumnModel().getColumn(1).getHeaderRenderer();
        r.setFont(new Font(LocalPreferences.getInstance().getFont(LocalPreferences.USERTABLEFONT), Font.PLAIN, 
                LocalPreferences.getInstance().getFontSize(LocalPreferences.USERTABLEFONTSIZE)));
    }

    private void setupColumns() {
        // NOTE: setDefaultRenderer(Class, Renderer) doesn't work because the table model needs elements in it to
        //   determine a column's class
        TableColumn rankColumn = getTable().getColumnModel().getColumn(0);
        rankColumn.setCellRenderer(new RankRenderer());
        rankColumn.setMaxWidth(TCIcon.DEFAULT_WIDTH * 2 + 3);
        contestTable.getColumnModel().getColumn(1).setCellRenderer(new UserNameRenderer(ca.getModel(), LocalPreferences.getInstance().getFont(LocalPreferences.USERTABLEFONT), LocalPreferences.getInstance().getFontSize(LocalPreferences.USERTABLEFONTSIZE)));
        contestTable.getTableHeader().addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                headerClickEvent(e);
            }
        });
        getTableModel().sort(1, false);
    }

    UserTablePanel(ContestApplet ca, String title, UserTableModel tableModel, boolean headersVisible) {
        super(ca, title, tableModel, headersVisible);
        if (tableModel.getColumnCount() < 2) {
            throw new IllegalArgumentException("Not enough headers in UserTablePanel()!");
        }
        this.userTableModel = tableModel;
        setupColumns();
//        this.getTableModel().setPrimaryKey(1);  TODO - replace this functionality?
    }

    public UserTablePanel(ContestApplet ca) {
        this(ca, "Who's here");
    }

    UserTablePanel(ContestApplet ca, String title) {
        super(ca, title + " [0]", new UserTableModel(ca.getModel(), CommonData.userHeader), true);
        this.title = title;

        setContestPopup("User Info", USER_POPUP);

        // event registration
        contestTable.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                if(enabled) {
                    rightClickEvent(e);
                    doubleClickEvent(e);
                }
            }
        });
        getTableModel().addTableModelListener(new TableModelListener() {
            public void tableChanged(TableModelEvent e) {
                tableCountEvent();
            }
        });

        setToolTipText("List of users currently in this room.");
        setupColumns();
        setupFonts();
//        this.getTableModel().setPrimaryKey(1);  TODO - replace this functionality?
    }

    public void updateUserList(final UserListItem[] items) {
        Runnable r = new Runnable() {
            public void run() {
                getTableModel().update(Arrays.asList(items));
            }
        };
        SwingUtilities.invokeLater(r);
    }

//    public void removeFromUserList(final UserListItem item) {
//        Runnable r = new Runnable() {
//            public void run() {
//                getTableModel().remove(item);
//            }
//        };
//        SwingUtilities.invokeLater(r);
//    }


    void tableCountEvent() {
        ((TitledBorder) getBorder()).setTitle(title + " [" + contestTable.getRowCount() + "]");
        revalidate();
        repaint();
    }


    void doubleClickEvent(MouseEvent e) {
        if ((e.getClickCount() > 1) && SwingUtilities.isLeftMouseButton(e)) {
            infoPopupEvent();
        }
    }

    private void headerClickEvent(MouseEvent e) {
        if (SwingUtilities.isLeftMouseButton(e)) {
            int col = getTable().getTableHeader().columnAtPoint(e.getPoint());
            if (col == -1) return;
            getTableModel().sort(col, (e.getModifiers() & MouseEvent.SHIFT_MASK) > 0);
            getTable().getTableHeader().repaint();
        }
    }

    void infoPopupEvent() {
        int index = contestTable.getSelectedRow();
        String handle = ((UserNameEntry) contestTable.getValueAt(index, 1)).getName();
        ca.setCurrentFrame(ca.getMainFrame());
        ca.requestCoderInfo(handle, ((UserNameEntry) contestTable.getValueAt(index, 1)).getUserType());
    }

    /**
     *  Matthew P. Suhocki (msuhocki)
     *
     * @return String The handle of the currently selected user
     */
    public String getSelectedUserHandle() {
        int index = getTable().getSelectedRow();
        if (0 > index || index >= contestTable.getRowCount()) return "";
        return userTableModel.getUser(index).getUserName();
    }
}
