package com.topcoder.client.contestApplet.uilogic.panels;

import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.MouseEvent;
import java.util.Arrays;

import javax.swing.SwingUtilities;
import javax.swing.border.TitledBorder;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;

import com.topcoder.client.contestApplet.ContestApplet;
import com.topcoder.client.contestApplet.common.CommonData;
import com.topcoder.client.contestApplet.common.LocalPreferences;
import com.topcoder.client.contestApplet.uilogic.panels.table.UserNameEntry;
import com.topcoder.client.contestApplet.uilogic.panels.table.UserTableModel;
import com.topcoder.client.contestant.view.UserListListener;
import com.topcoder.client.ui.UIComponent;
import com.topcoder.client.ui.UIPage;
import com.topcoder.client.ui.event.UIActionListener;
import com.topcoder.client.ui.event.UIMouseAdapter;
import com.topcoder.netCommon.contestantMessages.response.data.UserListItem;

public class UserTablePanel extends TablePanel implements UserListListener {
    protected UIComponent tablePanel;
    protected TitledBorder border;
    protected boolean enabled;
    protected UIPage page;
    protected String title;
    protected UserTableModel userTableModel;

    protected String getTablePanelName() {
        return "user_table_panel";
    }

    protected String getTableName() {
        return "user_table";
    }

    protected String getMenuName() {
        return "user_table_menu";
    }

    public boolean isPanelEnabled() {
        return enabled;
    }

    public void setPanelEnabled(boolean on) {
        enabled = on;
    }

    public UserTablePanel(ContestApplet ca, UIPage page) {
        super(ca, page, new UserTableModel(ca.getModel(), CommonData.userHeader));

        this.page = page;
        userTableModel = (UserTableModel) getTableModel();
        tablePanel = page.getComponent(getTablePanelName());
        border = (TitledBorder) tablePanel.getProperty("border");
        title = border.getTitle();
        border.setTitle(title + " [0]");
        table.addEventListener("mouse", new UIMouseAdapter() {
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
        page.getComponent("user_table_menu_info").addEventListener("action", new UIActionListener() {
                public void actionPerformed(ActionEvent e) {
                    infoPopupEvent();
                }
            });
        setupColumns("user_table_user_renderer");
        setupFonts("user_table_header_renderer");
    }

    protected UserTablePanel(ContestApplet ca, UIPage page, String userColumnName, String headerName) {
        super(ca, page, new UserTableModel(ca.getModel(), CommonData.userHeader));

        this.page = page;
        userTableModel = (UserTableModel) getTableModel();
        tablePanel = page.getComponent(getTablePanelName());
        border = (TitledBorder) tablePanel.getProperty("border");
        title = border.getTitle();
        border.setTitle(title + " [0]");
        table.addEventListener("mouse", new UIMouseAdapter() {
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
        setupColumns(userColumnName);
        setupFonts(headerName);
    }

    protected UserTablePanel(ContestApplet ca, UIPage page, UserTableModel tableModel, String userColumnName, String headerName) {
        super(ca, page, tableModel);
        if (tableModel.getColumnCount() < 2) {
            throw new IllegalArgumentException("Not enough headers in UserTablePanel()!");
        }
        this.page = page;
        userTableModel = tableModel;
        tablePanel = page.getComponent(getTablePanelName());
        border = (TitledBorder) tablePanel.getProperty("border");
        title = border.getTitle();
        border.setTitle(title + " [0]");
        setupColumns(userColumnName);
        setupFonts(headerName);
    }

    protected void tableCountEvent() {
        border.setTitle(title + " [" + table.getProperty("RowCount") + "]");
        tablePanel.performAction("revalidate");
        tablePanel.performAction("repaint");
    }

    protected void setupFonts(String headerName) {
        border.setTitleFont(new Font(LocalPreferences.getInstance().getFont(LocalPreferences.USERTABLEFONT), Font.BOLD, 
                                     LocalPreferences.getInstance().getFontSize(LocalPreferences.USERTABLEFONTSIZE)));
        page.getComponent(headerName).
            setProperty("font", new Font(LocalPreferences.getInstance().getFont(LocalPreferences.USERTABLEFONT),
                                         Font.PLAIN, 
                                         LocalPreferences.getInstance().getFontSize(LocalPreferences.USERTABLEFONTSIZE)));
    }

    private void setupColumns(String userColumnName) {
        // NOTE: setDefaultRenderer(Class, Renderer) doesn't work because the table model needs elements in it to
        //   determine a column's class
        //UIComponent userColumnRenderer = page.getComponent("user_table_user_renderer");
        UIComponent userColumnRenderer = page.getComponent(userColumnName);
        userColumnRenderer.setProperty("font",
                                       new Font(LocalPreferences.getInstance().getFont(LocalPreferences.USERTABLEFONT),
                                                Font.PLAIN,
                                                LocalPreferences.getInstance().getFontSize(LocalPreferences.USERTABLEFONTSIZE)));
        userColumnRenderer.setProperty("model", ca.getModel());
        getTable().getTableHeader().addMouseListener(new UIMouseAdapter() {
                public void mouseClicked(MouseEvent e) {
                    headerClickEvent(e);
                }
            });
        getTableModel().sort(1, false);
    }

    public void updateUserList(final UserListItem[] items) {
        Runnable r = new Runnable() {
                public void run() {
                    getTableModel().update(Arrays.asList(items));
                }
            };
        SwingUtilities.invokeLater(r);
    }

    protected void doubleClickEvent(MouseEvent e) {
        if ((e.getClickCount() > 1) && SwingUtilities.isLeftMouseButton(e)) {
            infoPopupEvent();
        }
    }

    protected void headerClickEvent(MouseEvent e) {
        if (SwingUtilities.isLeftMouseButton(e)) {
            int col = getTable().getTableHeader().columnAtPoint(e.getPoint());
            if (col == -1) return;
            getTableModel().sort(col, (e.getModifiers() & MouseEvent.SHIFT_MASK) > 0);
            getTable().getTableHeader().repaint();
        }
    }

    protected void infoPopupEvent() {
        int index = getTable().getSelectedRow();
        String handle = ((UserNameEntry) getTable().getValueAt(index, 1)).getName();
        ca.setCurrentFrame(ca.getMainFrame());
        ca.requestCoderInfo(handle, ((UserNameEntry) getTable().getValueAt(index, 1)).getUserType());
    }

    /**
     *  Matthew P. Suhocki (msuhocki)
     *
     * @return String The handle of the currently selected user
     */
    public String getSelectedUserHandle() {
        int index = getTable().getSelectedRow();
        if (0 > index || index >= getTable().getRowCount()) return "";
        return userTableModel.getUser(index).getUserName();
    }
}
