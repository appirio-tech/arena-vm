/*
* Copyright (C) - 2014 TopCoder Inc., All Rights Reserved.
*/
package com.topcoder.client.contestApplet.uilogic.frames;

/*
 * ArrayListInputDialog.java
 *
 * Created on July 10, 2000, 4:08 PM
 */

import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.StringTokenizer;

import javax.swing.JDialog;
import javax.swing.JOptionPane;
import javax.swing.table.TableCellEditor;

import com.topcoder.client.DummySortedTableModel;
import com.topcoder.client.SortedTableModel;
import com.topcoder.client.contestApplet.ContestApplet;
import com.topcoder.client.contestApplet.common.Common;
import com.topcoder.client.contestApplet.uilogic.panels.ArrayListInputPanel;
import com.topcoder.client.contestApplet.widgets.MoveFocus;
import com.topcoder.client.ui.UIComponent;
import com.topcoder.client.ui.UIPage;
import com.topcoder.client.ui.event.UIActionListener;

/**
 *
 * <p>
 * Changes in version 1.1 (BUGR-10410 - Remove Hardcoded Limit of 50 on Input Data):
 * <ol>
 *      <li>Update {@link #addButtonEvent()} method to remove 50 limit.</li>
 *      <li>Update {@link #superAddButtonEvent()} method to remove 50 limit.</li>
 *      <li>Update {@link #bracketAddButtonEvent()} method to remove 50 limit.</li>
 * </ol>
 * </p>
 * @author Alex Roman, savon_cn
 * @version 1.1
 */

public class ArrayListInputDialog implements FrameLogic {
    private UIComponent dialog;
    private UIPage page;

    private ArrayListInputPanel alip = null;
    private ArrayList info = null;
    private UIComponent jtf = null;
    private UIComponent addButton = null;
    private boolean status = false;

    public UIComponent getFrame() {
        return dialog;
    }

    public ArrayListInputDialog(ContestApplet ca, UIComponent frame, ArrayList arrayList, String title) {
        page = ca.getCurrentUIManager().getUIPage("arraylist_input_dialog", true);
        dialog = page.getComponent("root_dialog", false);
        dialog.setProperty("Owner", frame.getEventSource());
        dialog.setProperty("title", title + " Problem Argument");
        dialog.create();
        alip = new ArrayListInputPanel(page, title, new EditableModel());
        info = arrayList;
        for (int i = 0; i < info.size(); i++) {
            alip.addElement((String) info.get(i));
        }
        jtf = page.getComponent("input_text_field");
        addButton = page.getComponent("add_button");
        jtf.addEventListener("action", new UIActionListener() {
                public void actionPerformed(ActionEvent e) {
                    inputEvent();
                }
            });
        page.getComponent("up_button").addEventListener("action", new UIActionListener() {
                public void actionPerformed(ActionEvent e) {
                    upButtonEvent();
                }
            });
        page.getComponent("down_button").addEventListener("action", new UIActionListener() {
                public void actionPerformed(ActionEvent e) {
                    downButtonEvent();
                }
            });
        page.getComponent("add_button").addEventListener("action", new UIActionListener() {
                public void actionPerformed(ActionEvent e) {
                    addButtonEvent();
                }
            });
        page.getComponent("superadd_button").addEventListener("action", new UIActionListener() {
                public void actionPerformed(ActionEvent e) {
                    superAddButtonEvent();
                }
            });
        page.getComponent("bracketadd_button").addEventListener("action", new UIActionListener() {
                public void actionPerformed(ActionEvent e) {
                    bracketAddButtonEvent();
                }
            });
        page.getComponent("remove_button").addEventListener("action", new UIActionListener() {
                public void actionPerformed(ActionEvent e) {
                    removeButtonEvent();
                }
            });
        page.getComponent("clear_button").addEventListener("action", new UIActionListener() {
                public void actionPerformed(ActionEvent e) {
                    clearButtonEvent();
                }
            });
        page.getComponent("ok_button").addEventListener("action", new UIActionListener() {
                public void actionPerformed(ActionEvent e) {
                    okButtonEvent();
                }
            });
        page.getComponent("cancel_button").addEventListener("action", new UIActionListener() {
                public void actionPerformed(ActionEvent e) {
                    cancelButtonEvent();
                }
            });
        dialog.performAction("pack");
        Common.setLocationRelativeTo(frame, dialog);
    }

    ////////////////////////////////////////////////////////////////////////////////
    public ArrayList showDialog()
        ////////////////////////////////////////////////////////////////////////////////
    {
        //MoveFocus.moveFocus(jtf);
        jtf.performAction("requestFocus");
        dialog.performAction("show");

        ArrayList total = new ArrayList(2);

        total.add(new Boolean(status));
        total.add(info);

        return (total);
    }


    ////////////////////////////////////////////////////////////////////////////////
    private void inputEvent()
        ////////////////////////////////////////////////////////////////////////////////
    {
        // End any editing of the table
        endEditing();

        addButton.performAction("doClick");
    }

    ////////////////////////////////////////////////////////////////////////////////
    private void upButtonEvent()
        ////////////////////////////////////////////////////////////////////////////////
    {
        // End any editing of the table
        endEditing();

        int index = alip.getTable().getSelectedRow();

        //int indices[] = alip.getTable().getSelectedRows();
        int count = alip.getTable().getRowCount();


        if (index == -1 && count == 0) {
            Common.showMessage("", "The list is empty", (JDialog) dialog.getEventSource());
        } else if (index == -1) {
            Common.showMessage("", "Please select an element from the list", (JDialog) dialog.getEventSource());
            // Pops - fixed if pressing up button on first row
        } else if (index == 0) {
            return;
        } else {
            alip.getTableModel().swapRows(index - 1, index);
            //      for (int i=indices.length-1; i>=0; i--) {
            //        alip.removeElement(indices[i]);
            //      }
            alip.getTable().setRowSelectionInterval(index - 1, index - 1);
            alip.getTable().getSelectionModel().setSelectionInterval(index - 1, index - 1);
            /*
      ((DefaultListSelectionModel)alip.getTable().getSelectionModel()).fireValueChanged(true);
      ((DefaultListSelectionModel)alip.getTable().getSelectionModel()).fireValueChanged(false);
      ((DefaultListSelectionModel)alip.getTable().getSelectionModel()).fireValueChanged(index-1, index-1);
            */
        }
    }

    ////////////////////////////////////////////////////////////////////////////////
    private void downButtonEvent()
        ////////////////////////////////////////////////////////////////////////////////
    {
        // End any editing of the table
        endEditing();

        int index = alip.getTable().getSelectedRow();
        //int indices[] = alip.getTable().getSelectedRows();
        int count = alip.getTable().getRowCount();


        if (index == -1 && count == 0) {
            Common.showMessage("", "The list is empty", (JDialog) dialog.getEventSource());
        } else if (index == -1) {
            Common.showMessage("", "Please select an element from the list", (JDialog) dialog.getEventSource());
            // Pops - fixed if pressing down button on last row
        } else if (index >= count - 1) {
            return;
        } else {
            alip.getTableModel().swapRows(index + 1, index);
            //      for (int i=indices.length-1; i>=0; i--) {
            //        alip.shiftUp(indices[i]);
            //      }
            alip.getTable().setRowSelectionInterval(index + 1, index + 1);
            alip.getTable().getSelectionModel().setSelectionInterval(index + 1, index + 1);
            /*
      alip.getTable().getSelectionModel().fireValueChanged(true);
      alip.getTable().getSelectionModel().fireValueChanged(false);
      alip.getTable().getSelectionModel().fireValueChanged(index-1, index-1);
            */
        }
    }

    /**
     * add the test data.
     */
    private void addButtonEvent() {
        // End any editing of the table
        endEditing();
        alip.addElement((String)jtf.getProperty("Text"));
        jtf.setProperty("Text", "");
        MoveFocus.moveFocus(jtf);
    }

    /**
     * add the super button.
     */
    private void superAddButtonEvent() {
        // End any editing of the table
        endEditing();
        // Tokenize the input line
        StringTokenizer str = new StringTokenizer((String)jtf.getProperty("Text"), ",", true);

        // POPS - 1/10/03 - need to parse for blank items
        ArrayList tok = new ArrayList();
        String priorTok = ",";
        while (str.hasMoreTokens()) {
            String token = str.nextToken();
            // Was the prior and current token a comma - then we have a blank attribute
            if (priorTok.equals(",") && token.equals(",")) tok.add("");

            // If the current token is a comma and there are no more tokens - we have a blank ending attribute
            if (token.equals(",") && !str.hasMoreTokens()) tok.add("");

            // If it's not a comma - simply add it
            if (!token.equals(",")) tok.add(token);

            // Save the current one
            priorTok = token;
        }

        // Loop through all the tokens
        for (int x = 0; x < tok.size(); x++) {
            // Get the token
            String token = (String) tok.get(x);
            alip.addElement(token);
        }


        // Reset the text and move focus to it...
        jtf.setProperty("Text", "");
        MoveFocus.moveFocus(jtf);
    }

    ////////////////////////////////////////////////////////////////////////////////
    private void removeButtonEvent()
        ////////////////////////////////////////////////////////////////////////////////
    {
        // End any editing of the table
        endEditing();

        //int index = list.getSelectedIndex();
        //int indices[] = list.getSelectedIndices();
        //int count = model.getSize();
        int index = alip.getTable().getSelectedRow();
        int indices[] = alip.getTable().getSelectedRows();
        int count = alip.getTable().getRowCount();


        if (index == -1 && count == 0) {
            Common.showMessage("", "The list is empty", (JDialog) dialog.getEventSource());
        } else if (index == -1) {
            Common.showMessage("", "Please select an element from the list", (JDialog) dialog.getEventSource());
        } else {
            for (int i = indices.length - 1; i >= 0; i--) {
                alip.removeElement(indices[i]);
            }
            int rc = alip.getTable().getRowCount();
            if (index < rc - 1) {
                alip.getTable().setRowSelectionInterval(index, index);
                alip.getTable().getSelectionModel().setSelectionInterval(index, index);
            } else if (rc > 0) {
                alip.getTable().setRowSelectionInterval(rc - 1, rc - 1);
                alip.getTable().getSelectionModel().setSelectionInterval(rc - 1, rc - 1);
            }
        }
    }

    private void clearButtonEvent() {
        alip.clear();
    }

    ////////////////////////////////////////////////////////////////////////////////
    private void okButtonEvent()
        ////////////////////////////////////////////////////////////////////////////////
    {
        // End any editing of the table
        endEditing();

        // Ask what to do if anything is left in the input line
        if (!jtf.getProperty("Text").equals("")) {
            //switch (JOptionPane.showConfirmDialog(this, "You have un-saved information entered.  Do you wish to continue and lose that information?", "Un-saved information", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE)) {
            if (JOptionPane.showConfirmDialog((JDialog) dialog.getEventSource(), "You have un-saved information entered.  Do you wish to add it and continue?", "Warning", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE) == JOptionPane.YES_OPTION) {
                addButton.performAction("doClick");
            } else {
                return;
            }
        }


        info.clear();

        SortedTableModel stm = alip.getTableModel();
        for (int i = 0; i < stm.getRowCount(); i++) {
            info.add(stm.getValueAt(i, 0));
        }

        /*
    for (Enumeration en = model.elements(); en.hasMoreElements() ;) {
      info.add(en.nextElement());
    }
        */
        status = true;

        dialog.performAction("dispose");  // frees up the show() -- must be last
    }

    ////////////////////////////////////////////////////////////////////////////////
    private void cancelButtonEvent()
        ////////////////////////////////////////////////////////////////////////////////
    {
        status = false;
        dialog.performAction("dispose");  // frees up the show() -- must be last
    }


    ////////////////////////////////////////////////////////////////////////////////
    private void endEditing() {
        // Force any pending editing changes when a button is pressed
        if (alip.getTable().isEditing()) {
            TableCellEditor tc = alip.getTable().getCellEditor(alip.getTable().getEditingRow(), alip.getTable().getEditingColumn());
            if (tc != null) tc.stopCellEditing();
        }
    }

    private final int START = 0;
    private final int IN_QUOTE = 1;
    private final int ESCAPE = 2;

    /**
     * This will take text formatted as a comma delimated list of tokens.  Tokens surrounded by quotes will
     * be counted as one token, even if they have commas in them.  Whitespace not surrounded by quotes will be ignored.
     * To put quotes or backslashed in a string, they must be escaped.
     */
    private void bracketAddButtonEvent() {
        // End any editing of the table
        endEditing();
        String text = (String)jtf.getProperty("Text");
        //Trim off extra spaces and starting/ending braces
        text = text.trim();

        //
        // modified 4/9/2003 by schveiguy
        //
        // Fixed bug where empty string or empty brackets causes exception
        //
        if (text.length() > 0 && text.charAt(0) == '{') text = text.substring(1);
        if (text.length() > 0 && text.charAt(text.length() - 1) == '}') text = text.substring(0, text.length() - 1);
        if(text.length() != 0)
        {
            int state = START;
            StringBuffer buf = new StringBuffer(50);
            for (int i = 0; i < text.length(); i++) {
                char ch = text.charAt(i);
                switch (state) {
                case ESCAPE:
                    switch (ch) {
                    case '\\':
                        buf.append('\\');
                        break;
                    case '"':
                        buf.append('"');
                        break;
                    default: //we'll just assume it was a mistake, problems really should not use tabs, line feeds, etc.
                        buf.append('\\');
                        buf.append(ch);
                    }
                    state = IN_QUOTE;
                    break;
                case IN_QUOTE:
                    switch (ch) {
                    case '\\':
                        state = ESCAPE;
                        break;
                    case '"':
                        String param = buf.toString();
                        buf.delete(0, buf.length());
                        state = START;
                        alip.addElement(param);
                        break;
                    default:
                        buf.append(ch);
                        break;
                    }
                    break;
                case START:
                    if (Character.isWhitespace(ch)) {
                        if (buf.length() > 0) {
                            String param = buf.toString().trim();
                            buf.delete(0, buf.length());
                            alip.addElement(param);
                        }
                        continue;
                    }
                    switch (ch) {
                    case '"':
                        if (buf.length() > 0) {
                            buf.append('"');
                        } else {
                            state = IN_QUOTE;
                        }
                        break;
                    case ',':
                        if (buf.length() > 0 || (i == 0) || (i > 0 && text.charAt(i - 1) == ',')) {
                            String param = buf.toString().trim();
                            buf.delete(0, buf.length());
                            alip.addElement(param);
                        }
                        break;
                    default:
                        buf.append(ch);
                    }
                }
            }
            if (buf.length() > 0 || text.charAt(text.length() - 1) == ',') {
                String param = buf.toString().trim();
                buf.delete(0, buf.length());
                alip.addElement(param);
            }
            
        }
        // Reset the text and move focus to it...
        jtf.setProperty("Text", "");
        MoveFocus.moveFocus(jtf);
    }

    /**
     * The poorest implementation of a SortedTableModel I've ever seen forces me to use it but override
     * everything of value.  Wished someone had tested that out...
     */
    private class EditableModel extends DummySortedTableModel {

        ArrayList l = new ArrayList();

        public EditableModel() {
            setZeroAllowed(true);
        }

        public int getRowCount() {
            return l.size();
        }

        public boolean isCellEditable(int r, int c) {
            return true;
        }

        public void setValueAt(Object a, int r, int c) {
            l.set(r, a);
            fireTableRowsUpdated(r, r);
        }

        public Object getValueAt(int r, int c) {
            return l.get(r);
        }

        public void add(Object item) {
            l.add(item);
            fireTableRowsInserted(l.size() - 1, l.size() - 1);
        }

        public Object remove(int row) {
            Object o = l.remove(row);
            fireTableRowsDeleted(row, row);
            return o;
        }

        public void swapRows(int r, int r2) {
            Object o = l.get(r);
            l.set(r, l.get(r2));
            l.set(r2, o);
            fireTableRowsUpdated(Math.min(r, r2), Math.max(r, r2));
        }

        public void clear() {
            l.clear();
            fireTableDataChanged();
        }
    }
}
