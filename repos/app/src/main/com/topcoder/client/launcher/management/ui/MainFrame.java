package com.topcoder.client.launcher.management.ui;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.EventObject;
import java.util.Iterator;
import java.util.Properties;

import javax.swing.JFrame;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import javax.swing.JPanel;

import javax.swing.AbstractCellEditor;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JEditorPane;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.JPopupMenu;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;

import com.topcoder.client.launcher.common.application.Application;
import com.topcoder.client.launcher.common.application.ApplicationList;
import com.topcoder.client.launcher.common.task.ApplicationTaskException;

public class MainFrame extends JFrame implements Runnable {
    private static final String MAIN_FRAME_TITLE = "TopCoder Application Download Manager";

    private static final String DAY_URL_PROPERTY = "com.topcoder.client.launcher.management.ui.pageurl";

    private JPanel mainPanel = null;

    private JPanel applicationPanel = null;

    private JTable applicationTable = null;

    private JPopupMenu itemMenu = null; // @jve:decl-index=0:visual-constraint="719,126"

    private JMenuItem menuInstall = null;

    private JMenuItem menuUninstall = null;

    private JMenuItem menuUpdate = null;

    private JMenuItem menuExecute = null;

    private JMenuItem menuReinstall = null;

    private ApplicationList appList = null;

    private ApplicationListModel appModel = null;

    private MouseListener mouseEvent = new MainFrameMouseListener();

    private ActionListener actionEvent = new MainFrameActionListener();

    private Object syncRoot = new Object();

    private String baseDirectory;

    private URL baseUrl;

    private TaskProgressPanel progressPanel;

    private String defaultAppId;

    private JEditorPane htmlRenderer;

    private JPanel actionPanel;

    private JButton launchButton;

    public MainFrame(String baseDirectory, URL baseUrl, String defaultAppId) {
        this.baseDirectory = baseDirectory;
        this.baseUrl = baseUrl;
        this.defaultAppId = defaultAppId;
        initialize();
    }

    private void loadApplicationList() {
        try {
            appList = new ApplicationList(baseUrl, baseDirectory);
            appModel = new ApplicationListModel(appList);
            applicationTable.setModel(appModel);

            TableColumnModel columnModel = applicationTable.getColumnModel();

            // Set the width of the columns
            TableColumn column = columnModel.getColumn(0);
            column.setPreferredWidth(325);

            column = columnModel.getColumn(1);
            column.setPreferredWidth(76);
            DefaultTableCellRenderer renderer = new DefaultTableCellRenderer();
            column.setCellRenderer(renderer);
            renderer.setHorizontalAlignment(SwingConstants.CENTER);

            column = columnModel.getColumn(2);
            column.setMinWidth(95);
            column.setMaxWidth(95);
            column.setPreferredWidth(95);
            column.setResizable(false);

            setVisible(true);
        } catch (IOException e) {
            setVisible(false);
            JOptionPane.showMessageDialog(this, e.toString());
            dispose();
        }
    }

    private void updateInstalledApplications() {
        SwingUtilities.invokeLater(new Runnable() {
                public void run() {
                    for (Iterator iter = appList.iterator(); iter.hasNext();) {
                        Application app = (Application) iter.next();

                        if (app.getInfo().isInstalled()) {
                            updateApplication(app, true);
                        }
                    }
                }
            });
    }

    public Object getSyncRoot() {
        return syncRoot;
    }

    /**
     * This method initializes this
     */
    private void initialize() {
        this.setSize(700, 400);
        this.setContentPane(getMainPanel());
        this.setTitle(MAIN_FRAME_TITLE);
        this.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        this.getApplicationTable().add(getItemMenu());
        progressPanel = new TaskProgressPanel();
    }

    public void dispose() {
        super.dispose();

        progressPanel.dispose();

        synchronized (syncRoot) {
            syncRoot.notifyAll();
        }
    }

    public void run() {
        Properties properties = new Properties();
        InputStream is = null;

        try {
            is = getClass().getClassLoader().getResourceAsStream(
                                                                 "com/topcoder/client/launcher/management/ui.properties");

            properties.load(is);
        } catch (IOException e) {
            // ignore
        } finally {
            try {
                if (is != null) {
                    is.close();
                }
            } catch (IOException e) {
                // ignore
            }
        }

        // Show the model dialog for the daily message
        final String infoUrl = properties.getProperty(DAY_URL_PROPERTY);

        new Thread(new Runnable() {
                public void run() {
                    try {
                        htmlRenderer.setPage(infoUrl);
                    } catch (IOException e) {
                        JOptionPane.showMessageDialog(MainFrame.this, "Application cannot open the information page.",
                                                      "Error", JOptionPane.ERROR_MESSAGE);
                    }
                }
            }).start();

        loadApplicationList();
        updateInstalledApplications();

        launchButton.setEnabled(false);
        if ((defaultAppId != null) && appList.contains(defaultAppId)) {
            // If there is a default application, install and run the default application
            Application app = (Application) appList.get(defaultAppId);

            if (!app.getInfo().isInstalled()) {
                installApplication(app, true);
            }

            if (app.getInfo().isInstalled()) {
                launchButton.setEnabled(true);
            }
        }
    }

    public void setSize(int width, int height) {
        super.setSize(width, height);
        Dimension scrSize = getToolkit().getScreenSize();
        setLocation((scrSize.width - getWidth()) / 2, (scrSize.height - getHeight()) / 2);
    }

    /**
     * This method initializes mainPanel
     * 
     * @return javax.swing.JPanel
     */
    private JPanel getMainPanel() {
        if (mainPanel == null) {
            mainPanel = new JPanel();

            BorderLayout layout = new BorderLayout();

            layout.setHgap(10);
            layout.setVgap(10);
            mainPanel.setLayout(layout);

            JPanel infoPanel = new JPanel(new BorderLayout());

            htmlRenderer = new JEditorPane();

            htmlRenderer.setContentType("text/html");
            htmlRenderer.setEditable(false);
            htmlRenderer.setFocusable(false);

            infoPanel.add(new JScrollPane(htmlRenderer), BorderLayout.CENTER);
            infoPanel.setPreferredSize(new Dimension(261, 343));

            mainPanel.add(infoPanel, BorderLayout.EAST);
            mainPanel.add(getApplicationPanel(), BorderLayout.CENTER);
        }
        return mainPanel;
    }

    /**
     * This method initializes applicationList
     * 
     * @return javax.swing.JScrollPane
     */
    private JPanel getApplicationPanel() {
        if (applicationPanel == null) {
            BorderLayout layout = new BorderLayout();
            JLabel label;
            JPanel empty;

            layout.setHgap(10);
            layout.setVgap(10);
            applicationPanel = new JPanel(layout);
            actionPanel = new JPanel(new BorderLayout());
            launchButton = new JButton("Launch");
            launchButton.addActionListener(actionEvent);
            launchButton.setMinimumSize(new Dimension(73, 26));
            launchButton.setMaximumSize(new Dimension(73, 26));
            launchButton.setPreferredSize(new Dimension(73, 26));
            actionPanel.add(launchButton, BorderLayout.CENTER);
            empty = new JPanel();
            empty.setPreferredSize(new Dimension(490, 10));
            actionPanel.add(empty, BorderLayout.SOUTH);
            empty = new JPanel();
            empty.setPreferredSize(new Dimension(170, 26));
            actionPanel.add(empty, BorderLayout.EAST);
            empty = new JPanel();
            empty.setPreferredSize(new Dimension(170, 26));
            actionPanel.add(empty, BorderLayout.WEST);
            actionPanel.setPreferredSize(new Dimension(490, 36));
            applicationPanel.add(actionPanel, BorderLayout.SOUTH);
            label = new JLabel(new ImageIcon(getClass().getClassLoader().getResource(
                                                                                     "com/topcoder/client/launcher/management/ui/tclogo.png")));
            label.setHorizontalAlignment(SwingConstants.LEFT);
            applicationPanel.add(label, BorderLayout.NORTH);
            JScrollPane appPane = new JScrollPane(getApplicationTable(), JScrollPane.VERTICAL_SCROLLBAR_NEVER,
                                                  JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
            applicationPanel.add(appPane, BorderLayout.CENTER);
        }

        return applicationPanel;
    }

    private class TableComponentRenderer implements TableCellRenderer {
        private final ActionListener listener = new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    String id = e.getActionCommand();
                    Application app = appList.get(id);

                    if (app.getInfo().isInstalled()) {
                        uninstallApplication(app, false);
                    } else {
                        installApplication(app, false);
                    }

                    applicationTable.editingCanceled(new ChangeEvent(e.getSource()));
                }
            };

        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected,
                                                       boolean hasFocus, int row, int column) {
            Component component = (Component) value;

            if (component instanceof JButton) {
                JButton button = (JButton) component;

                if (button.getActionListeners().length == 0) {
                    button.addActionListener(listener);
                }
            }

            JPanel panel = new JPanel(new BorderLayout());
            JPanel empty = new JPanel();
            empty.setBackground(applicationTable.getBackground());
            empty.setMinimumSize(new Dimension(0, 0));
            panel.add(component, BorderLayout.CENTER);
            panel.add(empty, BorderLayout.EAST);
            empty = new JPanel();
            empty.setBackground(applicationTable.getBackground());
            empty.setMinimumSize(new Dimension(0, 0));
            panel.add(empty, BorderLayout.WEST);

            return panel;
        }
    }

    private class TableComponentEditor extends AbstractCellEditor implements TableCellEditor {
        private Component editing = null;

        public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
            editing = (Component) value;
            JPanel panel = new JPanel(new BorderLayout());
            JPanel empty = new JPanel();
            empty.setBackground(applicationTable.getBackground());
            empty.setMinimumSize(new Dimension(0, 0));
            panel.add(editing, BorderLayout.CENTER);
            panel.add(empty, BorderLayout.EAST);
            empty = new JPanel();
            empty.setBackground(applicationTable.getBackground());
            empty.setMinimumSize(new Dimension(0, 0));
            panel.add(empty, BorderLayout.WEST);

            return panel;
        }

        public Object getCellEditorValue() {
            return editing;
        }

        public boolean shouldSelectCell(EventObject anEvent) {
            return false;
        }
    }

    /**
     * This method initializes applicationTable
     * 
     * @return javax.swing.JTable
     */
    private JTable getApplicationTable() {
        if (applicationTable == null) {
            applicationTable = new JTable();
            applicationTable.addMouseListener(mouseEvent);
            applicationTable.setColumnSelectionAllowed(false);
            applicationTable.setRowSelectionAllowed(true);
            applicationTable.setDefaultRenderer(Component.class, new TableComponentRenderer());
            applicationTable.setDefaultEditor(Component.class, new TableComponentEditor());
            applicationTable.setRowHeight(36);
            applicationTable.setRowMargin(10);
            applicationTable.setPreferredSize(new Dimension(490, 270));
            /*applicationTable.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
              public void valueChanged(ListSelectionEvent e) {
              int row = applicationTable.getSelectedRow();

              if (row == -1) {
              launchButton.setEnabled(false);
              } else {
              Application app = (Application) applicationTable.getValueAt(row, 0);

              launchButton.setEnabled(app.getInfo().isInstalled() && app.getInfo().isExecutable());
              }
              }
              });*/
        }
        return applicationTable;
    }

    /**
     * This method initializes itemMenu
     * 
     * @return javax.swing.JPopupMenu
     */
    private JPopupMenu getItemMenu() {
        if (itemMenu == null) {
            itemMenu = new JPopupMenu();
            menuExecute = itemMenu.add("Execute");
            menuInstall = itemMenu.add("Install");
            menuUninstall = itemMenu.add("Uninstall");
            menuUpdate = itemMenu.add("Update");
            menuReinstall = itemMenu.add("Reinstall");
            menuExecute.addActionListener(actionEvent);
            menuInstall.addActionListener(actionEvent);
            menuUninstall.addActionListener(actionEvent);
            menuUpdate.addActionListener(actionEvent);
            menuReinstall.addActionListener(actionEvent);
        }
        return itemMenu;
    }

    private class MainFrameMouseListener implements MouseListener {
        public void mouseClicked(MouseEvent e) {
            Object source = e.getSource();
            Application app = null;

            if (applicationTable.getSelectedRow() != -1) {
                app = (Application) applicationTable.getValueAt(applicationTable.getSelectedRow(), 0);
            }

            if (applicationTable.equals(source)) {
                itemMenu.setVisible(false);
                // Click on the application table
                if ((e.getButton() == MouseEvent.BUTTON1) && (e.getClickCount() >= 2) && (app != null)) {
                    // Left double click on the table
                    if (app.getInfo().isInstalled()) {
                        if (app.getInfo().isExecutable()) {
                            executeApplication(app);
                        }
                    } else {
                        installApplication(app, false);
                    }
                }
            }
        }

        public void mouseEntered(MouseEvent e) {
        }

        public void mouseExited(MouseEvent e) {
        }

        public void mousePressed(MouseEvent e) {
            mouseReleased(e);
        }

        public void mouseReleased(MouseEvent e) {
            if (e.isPopupTrigger() && applicationTable.equals(e.getSource())) {
                int row = applicationTable.rowAtPoint(e.getPoint());

                if (row != -1) {
                    applicationTable.setRowSelectionInterval(row, row);
                    Application app = (Application) applicationTable.getValueAt(applicationTable.getSelectedRow(), 0);

                    // Right click on the table
                    itemMenu.setLocation(e.getPoint());
                    if (app.getInfo().isInstalled()) {
                        menuInstall.setVisible(false);
                        menuUninstall.setVisible(true);
                        menuUpdate.setVisible(true);
                        menuExecute.setVisible(app.getInfo().isExecutable());
                        menuReinstall.setVisible(true);
                    } else {
                        menuInstall.setVisible(true);
                        menuUninstall.setVisible(false);
                        menuUpdate.setVisible(false);
                        menuExecute.setVisible(false);
                        menuReinstall.setVisible(false);
                    }
                    itemMenu.show(applicationTable, e.getX(), e.getY());
                } else {
                    applicationTable.clearSelection();
                }
            }
        }
    }

    private void executeApplication(final Application app) {
        // Hide the main window before running the application.
        setVisible(false);

        Runnable task = new Runnable() {
                public void run() {
                    try {
                        app.execute();
                    } catch (ApplicationTaskException appException) {
                        JOptionPane.showMessageDialog(MainFrame.this, appException.getMessage(),
                                                      "Application Task Execution Error", JOptionPane.ERROR_MESSAGE);
                    }
                }
            };

        SwingUtilities.invokeLater(task);

        // After executing the application, it should be disposed.
        dispose();
    }

    private void showProgress() {
        applicationPanel.remove(actionPanel);
        applicationPanel.add(progressPanel, BorderLayout.SOUTH);
        this.repaint();
    }

    private void hideProgress() {
        applicationPanel.remove(progressPanel);
        applicationPanel.add(actionPanel, BorderLayout.SOUTH);
        this.repaint();
    }

    private void installApplication(final Application app, boolean wait) {
        app.addTaskProgressListener(progressPanel);
        showProgress();
        setTitle("Install " + app.getInfo().getName());

        Runnable task = new Runnable() {
                public void run() {
                    try {
                        try {
                            app.install();
                        } finally {
                            setTitle(MAIN_FRAME_TITLE);
                            hideProgress();
                            app.removeTaskProgressListener(progressPanel);
                        }

                        appList.saveInstalled();
                        appModel.fireTableDataChanged();
                    } catch (ApplicationTaskException appException) {
                        JOptionPane.showMessageDialog(MainFrame.this, appException.getMessage(),
                                                      "Application Task Execution Error", JOptionPane.ERROR_MESSAGE);
                    } catch (IOException ioException) {
                        JOptionPane.showMessageDialog(MainFrame.this, ioException.getMessage(), "I/O Error",
                                                      JOptionPane.ERROR_MESSAGE);
                    }
                }
            };

        if (wait) {
            task.run();
        } else {
            SwingUtilities.invokeLater(task);
        }
    }

    private void reinstallApplication(final Application app, boolean wait) {
        app.addTaskProgressListener(progressPanel);
        showProgress();
        setTitle("Install " + app.getInfo().getName());

        Runnable task = new Runnable() {
                public void run() {
                    try {
                        try {
                            app.reinstall();
                        } finally {
                            setTitle(MAIN_FRAME_TITLE);
                            hideProgress();
                            app.removeTaskProgressListener(progressPanel);
                        }

                        appList.saveInstalled();
                        appModel.fireTableDataChanged();
                    } catch (IOException ioException) {
                        JOptionPane.showMessageDialog(MainFrame.this, ioException.getMessage(), "I/O Error",
                                                      JOptionPane.ERROR_MESSAGE);
                    }
                }
            };

        if (wait) {
            task.run();
        } else {
            SwingUtilities.invokeLater(task);
        }
    }

    private void uninstallApplication(final Application app, boolean wait) {
        app.addTaskProgressListener(progressPanel);
        showProgress();
        setTitle("Uninstall " + app.getInfo().getName());

        Runnable task = new Runnable() {
                public void run() {
                    try {
                        try {
                            app.uninstall();
                        } finally {
                            setTitle(MAIN_FRAME_TITLE);
                            hideProgress();
                            app.removeTaskProgressListener(progressPanel);

                            // Uninstall should always save the installed application, regardless of error
                            appList.saveInstalled();
                            appModel.fireTableDataChanged();
                        }
                    } catch (ApplicationTaskException appException) {
                        JOptionPane.showMessageDialog(MainFrame.this, appException.getMessage(),
                                                      "Application Task Execution Error", JOptionPane.ERROR_MESSAGE);
                    } catch (IOException ioException) {
                        JOptionPane.showMessageDialog(MainFrame.this, ioException.getMessage(), "I/O Error",
                                                      JOptionPane.ERROR_MESSAGE);
                    }
                }
            };

        if (wait) {
            task.run();
        } else {
            SwingUtilities.invokeLater(task);
        }
    }

    private void updateApplication(final Application app, boolean wait) {
        app.addTaskProgressListener(progressPanel);
        showProgress();
        setTitle("Update " + app.getInfo().getName());

        Runnable task = new Runnable() {
                public void run() {
                    try {
                        try {
                            app.update();
                        } finally {
                            setTitle(MAIN_FRAME_TITLE);
                            hideProgress();
                            app.removeTaskProgressListener(progressPanel);
                        }
                    } catch (ApplicationTaskException appException) {
                        JOptionPane.showMessageDialog(MainFrame.this, appException.getMessage(),
                                                      "Application Task Execution Error", JOptionPane.ERROR_MESSAGE);
                    } catch (IOException ioException) {
                        JOptionPane.showMessageDialog(MainFrame.this, ioException.getMessage(), "I/O Error",
                                                      JOptionPane.ERROR_MESSAGE);
                    }
                }
            };

        if (wait) {
            task.run();
        } else {
            SwingUtilities.invokeLater(task);
        }
    }

    private class MainFrameActionListener implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            Object source = e.getSource();
            Application app = null;

            if (applicationTable.getSelectedRow() != -1) {
                app = (Application) applicationTable.getValueAt(applicationTable.getSelectedRow(), 0);
            }

            if (menuExecute.equals(source)) {
                itemMenu.setVisible(false);

                if (app != null) {
                    executeApplication(app);
                }
            } else if (menuInstall.equals(source)) {
                itemMenu.setVisible(false);

                if (app != null) {
                    installApplication(app, false);
                }
            } else if (menuUninstall.equals(source)) {
                itemMenu.setVisible(false);

                if (app != null) {
                    uninstallApplication(app, false);
                }
            } else if (menuUpdate.equals(source)) {
                itemMenu.setVisible(false);

                if (app != null) {
                    updateApplication(app, false);
                }
            } else if (menuReinstall.equals(source)) {
                itemMenu.setVisible(false);

                if (app != null) {
                    reinstallApplication(app, false);
                }
            } else if (launchButton.equals(source)) {
                app = appList.get(defaultAppId);
                if ((app != null) && (app.getInfo().isInstalled())) {
                    executeApplication(app);
                }
            }
        }
    }
} // @jve:decl-index=0:visual-constraint="10,10"
