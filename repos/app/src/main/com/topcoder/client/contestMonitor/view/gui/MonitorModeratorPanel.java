package com.topcoder.client.contestMonitor.view.gui;

import com.topcoder.client.contestMonitor.model.MonitorController;
import com.topcoder.client.contestMonitor.model.QuestionsTableModel;
import com.topcoder.server.listener.monitor.QuestionItem;
import org.apache.log4j.Logger;

import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.SwingUtilities;
import javax.swing.event.PopupMenuEvent;
import javax.swing.event.PopupMenuListener;
import javax.swing.table.JTableHeader;
import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

/**
 * GUI for manipulating recieved questions in a moderated chat room. Allows the
 * moderator to assign ratings to questions, sort on various fields, and approve
 * the questions.
 *
 *@author    Da Twink Daddy
 *@created   May 2002
 */
public class MonitorModeratorPanel extends JPanel {

    /**
     * Logger for this class. Inner classes will use this Logger; Non-inner
     * neseted classes may use this Logger, or another Logger (which should be a
     * child of this Logger).
     */
    private final static Logger log = Logger.getLogger("com.topcoder.utilities.listener.view.gui.MonitorModeratorPanel");
    /** Removal action. Used as action command string for menu item */
    private final static String REMOVE_ACTION_COMMAND = "Remove";
    /** Send action. Used as action command string for menu item */
    private final static String SEND_ACTION_COMMAND = "Send";

    /**
     * Handles the popup menu and other mouse events for the table
     *
     *@author    Da Twink Daddy
     *@created   May 2002
     */
    public class PopupManager implements MouseListener, PopupMenuListener, ActionListener {

        /** Most recently clicked QuestionItem, stored for use by the popup menu. */
        private QuestionItem clicked;

        /**
         * Handles the a mouse click. Assumes the source is a JTable over a
         * QuestionsTableModel.
         *
         *@param me  the event
         */
        public void mouseClicked(MouseEvent me) {
            JTable source = (JTable) me.getSource();
            int row = source.rowAtPoint(me.getPoint());
            if (row != -1) {
                clicked = ((QuestionsTableModel) source.getModel()).getQuestion(row);
                if (me.getClickCount() == 2) {
                    sendQuestion(clicked);
                }
                if (me.getClickCount() == 1 && SwingUtilities.isRightMouseButton(me) || questionPopup.isPopupTrigger(me)) {
                    questionPopup.show(source, me.getX(), me.getY());
                }
            } else {
                log.warn("Non-existent row clicked.");
            }
        }

        /**
         * Does nothing. Part of MouseListener interface
         *
         *@param me  the event
         */
        public void mouseEntered(MouseEvent me) {
            /*
             * Ignore
             */
        }

        /**
         * Does nothing. Part of MouseListener interface
         *
         *@param me  the event
         */
        public void mouseExited(MouseEvent me) {
            /*
             * Ignore
             */
        }

        /**
         * Does Nothing. Part of MouseListener interface
         *
         *@param me  the event
         */
        public void mousePressed(MouseEvent me) {
            /*
             * Ignore
             */
        }

        /**
         * Does Nothing. Part of MouseListener interface
         *
         *@param me  the event
         */
        public void mouseReleased(MouseEvent me) {
            /*
             * Ignore
             */
        }

        /**
         * Sets {@link #clicked} to null as a precaution to ensure the popup menu
         * only acts on the correct QuestionItem.
         *
         *@param pme  the event
         */
        public void popupMenuCanceled(PopupMenuEvent pme) {
            clicked = null;
        }

        /**
         * Does Nothing. Part of PopupMenuListener interface
         *
         *@param pme  the event
         */
        public void popupMenuWillBecomeInvisible(PopupMenuEvent pme) {
            /*
             * Ignore
             */
        }

        /**
         * Does Nothing. Part of PopupMenuListener interface
         *
         *@param pme  the event
         */
        public void popupMenuWillBecomeVisible(PopupMenuEvent pme) {
            /*
             * Ignore
             */
        }

        /**
         * Handles clicks on the popup menu's items.
         *
         *@param ae  the event
         */
        public void actionPerformed(ActionEvent ae) {
            String whatToDo = ae.getActionCommand();
            if (clicked != null) {
                if (whatToDo.equals(SEND_ACTION_COMMAND)) {
                    sendQuestion(clicked);
                } else if (whatToDo.equals(REMOVE_ACTION_COMMAND)) {
                    removeQuestion(clicked);
                } else {
                    log.error("Unknown action command (" + whatToDo + ").");
                }
            } else {
                log.error("No clicked question.");
                JOptionPane.showMessageDialog(MonitorModeratorPanel.this, "Sorry, I've lost the Question you wanted.", "Logic Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    /**
     * The controller; the model for this view. Used to locate and manipulate the
     * correct QuestionsTableModel and also send approved questions.
     */
    private MonitorController controller;
    /** The room to moderate */
    private int roomID;
    /** The popup menu for our table */
    private JPopupMenu questionPopup;

    /**
     * Constructs a new MonitorModeratorPanel
     *
     *@param mc      Our controller; used as data source and communication tool
     *@param roomID  The room to moderate
     */
    public MonitorModeratorPanel(MonitorController mc, int roomID) {
        super(new BorderLayout());

        this.controller = mc;
        this.roomID = roomID;

        buildGUI();
    }

    /** Creates all the QUI components, connects them to each other, and adds them. */
    private void buildGUI() {
        PopupManager pm = new PopupManager();
        JTable table = new JTable(controller.getQuestionsTableModel(roomID));
        JMenuItem sendItem = new JMenuItem("Send Question");
        JMenuItem removeItem = new JMenuItem("Remove Question");

        questionPopup = new JPopupMenu();

        sendItem.setActionCommand(SEND_ACTION_COMMAND);
        removeItem.setActionCommand(REMOVE_ACTION_COMMAND);

        table.addMouseListener(pm);

        table.getTableHeader().addMouseListener(
                new MouseAdapter() {
                    public void mouseClicked(MouseEvent me) {
                        JTableHeader source = (JTableHeader) me.getSource();
                        int column = source.columnAtPoint(me.getPoint());
                        if (column != -1) {
                            ((QuestionsTableModel) source.getTable().getModel()).layerSort(column, (me.getModifiers() & MouseEvent.SHIFT_MASK) != 0);
                        } else {
                            log.warn("Non-existent column clicked.");
                        }
                    }
                });

        questionPopup.addPopupMenuListener(pm);

        sendItem.addActionListener(pm);
        removeItem.addActionListener(pm);

        questionPopup.add(sendItem);
        questionPopup.add(removeItem);
        add(new JScrollPane(table), BorderLayout.CENTER);
    }

    /**
     * Sends a QuestionItem via {@link #controller}.
     *
     *@param q  QuestionItem to send
     */
    private void sendQuestion(QuestionItem q) {
        String strippedMessage = q.getMessage().trim();
        String formattedMessage = strippedMessage + '\n';
        if (MonitorGUIUtils.isConfirmed("Send the question \"" + strippedMessage + "\"?")) {
            controller.getCommandSender().sendApprovedQuestion(formattedMessage, roomID, q.getUsername());
            removeQuestion(q);
        }
    }

    /**
     * Removes a QuestionItem from out table.
     *
     *@param q  QuestionItem to remove
     */
    private void removeQuestion(QuestionItem q) {
        controller.getQuestionsTableModel(roomID).removeQuestion(q);
    }
}

