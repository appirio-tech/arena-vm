package com.topcoder.client.contestApplet.frames;

/*
 * ArrayListInputDialog.java
 *
 * Created on July 10, 2000, 4:08 PM
 */

import java.util.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
//import javax.swing.border.*;
import javax.swing.table.*;
//import javax.swing.event.*;
import com.topcoder.client.contestApplet.common.*;
//import com.topcoder.client.contestApplet.*;
import com.topcoder.client.contestApplet.panels.table.*;
//import com.topcoder.client.contestApplet.listener.*;
import com.topcoder.client.contestApplet.widgets.*;
import com.topcoder.client.DummySortedTableModel;
import com.topcoder.client.SortedTableModel;

/**
 *
 * @author Alex Roman
 * @version
 */

public final class ArrayListInputDialog extends JDialog {

    private ArrayListInputPanel alip = null;
    private ArrayList info = null;
    private JList list = null;
    private DefaultListModel model = null;
    private JTextField jtf = null;
    private JButton addButton = null;
    private boolean status = false;

    /**
     * Class constructor
     */
    ////////////////////////////////////////////////////////////////////////////////
    public ArrayListInputDialog(JDialog frame, ArrayList arrayList, String title)
            ////////////////////////////////////////////////////////////////////////////////
    {
        super(frame, title + " Problem Argument", true);

        GridBagConstraints gbc = Common.getDefaultConstraints();

        ArrayListInputPanel alip = new ArrayListInputPanel(title, new EditableModel());
        this.alip = alip;

        // POPS - 1/10/03 - enable display of blank items
        JTable table = alip.getTable();
        for (int i = 0; i < table.getColumnModel().getColumnCount(); i++) {
            ((ContestTableCellRenderer) table.getColumnModel().getColumn(i).getCellRenderer()).setEnableBlankDisplay(true);
        }

        JPanel p0 = new JPanel(new GridBagLayout());
        info = arrayList;
        model = new DefaultListModel();
        list = new JList(model);
        JScrollPane jsp = new JScrollPane(list);

        // panel properties
        p0.setBorder(Common.getTitledBorder(title));
        // list properties
        list.setCellRenderer(new ContestListCellRenderer());

        for (int i = 0; i < info.size(); i++) {
            alip.addElement((String) info.get(i));
            model.addElement(info.get(i));
        }

        gbc.insets = new Insets(0, 0, 0, 0);
        Common.insertInPanel(jsp, p0, gbc, 0, 0, 1, 1, 1.0, 0.1);

        JPanel p1 = new JPanel(new GridBagLayout());
        JPanel p2 = new JPanel(new GridBagLayout());
        jtf = new JTextField();
        addButton = new JButton("+");

        // Pops - allow editing of the rows
        //alip.getTable().setDefaultEditor(String.class, new DefaultCellEditor(new JTextField()));


        // POPS - 11/26/01 - added the super add button...
        JButton superAddButton = new JButton("++");
        JButton bracketAddButton = new JButton("{}");

        JButton removeButton = new JButton("-");
        JButton upButton = new JButton("^");
        JButton downButton = new JButton("v");
        JButton clearButton = new JButton("C");

        gbc.insets = new Insets(1, 1, 1, 1);
        // POPS - 11/26/01 - expanded to fit new button
        Common.insertInPanel(jtf, p1, gbc, 0, 0, 1, 1, 1.0, 0.1);
        Common.insertInPanel(upButton, p2, gbc, 0, 0, 1, 1, 0.0, 0.);
        Common.insertInPanel(downButton, p2, gbc, 1, 0, 1, 1, 0.0, 0.);
        Common.insertInPanel(addButton, p2, gbc, 2, 0, 1, 1, 0.0, 0.);
        // POPS - 11/26/01 - added the super add button...
        Common.insertInPanel(superAddButton, p2, gbc, 3, 0, 1, 1, 0.0, 0.);
        Common.insertInPanel(bracketAddButton, p2, gbc, 4, 0, 1, 1, 0.0, 0.);
        Common.insertInPanel(removeButton, p2, gbc, 5, 0, 1, 1, 0.0, 0.);
        Common.insertInPanel(clearButton, p2, gbc, 6, 0, 1, 1, 0.0, 0.);
        Common.insertInPanel(p2, p1, gbc, 0, 1, 1, 1, 0, 0);

        //p1.setPreferredSize(new Dimension(0, 23));
        //p1.setMinimumSize(new Dimension(0, 23));
        p1.setPreferredSize(new Dimension(0, 40));
        p1.setMinimumSize(new Dimension(0, 40));
        p1.setOpaque(false);
        p2.setPreferredSize(new Dimension(0, 25));
        p2.setMinimumSize(new Dimension(0, 25));
        p2.setOpaque(false);

        JPanel p3 = new JPanel();
        p2.setOpaque(false);
        JButton okButton = new JButton("OK");
        JButton cancelButton = new JButton("Cancel");
        okButton.setDefaultCapable(true);
        getRootPane().setDefaultButton(okButton);

        //jtf.addActionListener(new al("actionPerformed", "inputEvent", this));
        jtf.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                inputEvent();
            }
        });
        //upButton.addActionListener(new al("actionPerformed", "upButtonEvent", this));
        upButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                upButtonEvent();
            }
        });
        //downButton.addActionListener(new al("actionPerformed", "downButtonEvent", this));
        downButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                downButtonEvent();
            }
        });
        //addButton.addActionListener(new al("actionPerformed", "addButtonEvent", this));
        addButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                addButtonEvent();
            }
        });
        // POPS - 11/26/01 - added the super add button...
        //superAddButton.addActionListener(new al("actionPerformed", "superAddButtonEvent", this));
        superAddButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                superAddButtonEvent();
            }
        });
        bracketAddButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                bracketAddButtonEvent();
            }
        });
        //removeButton.addActionListener(new al("actionPerformed", "removeButtonEvent", this));
        removeButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                removeButtonEvent();
            }
        });
        //clearButton.addActionListener(new al("actionPerformed", "clearButtonEvent", this));
        clearButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                clearButtonEvent();
            }
        });
        //okButton.addActionListener(new al("actionPerformed", "okButtonEvent", this));
        okButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                okButtonEvent();
            }
        });
        //cancelButton.addActionListener(new al("actionPerformed", "cancelButtonEvent", this));
        cancelButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                cancelButtonEvent();
            }
        });

        upButton.setToolTipText("Shift the selected element up one position.");
        downButton.setToolTipText("Shift the selected element down one position.");
        addButton.setToolTipText("Add a new element to the array.");
        superAddButton.setToolTipText("Add a comma delimited set of elements to the array.");
        removeButton.setToolTipText("Remove the selected element from the array.");
        clearButton.setToolTipText("Remove all the elements from the array.");

        p3.add(okButton);
        p3.add(cancelButton);

        getContentPane().setLayout(new GridBagLayout());
        getContentPane().setBackground(Common.WPB_COLOR);

        gbc.insets = new Insets(10, 10, 10, 10);
        //Common.insertInPanel(p0, getContentPane(), gbc, 0, 0, 1, 1, 0.1, 1.0);
        Common.insertInPanel(alip, getContentPane(), gbc, 0, 0, 1, 1, 0.1, 1.0);
        Common.insertInPanel(p1, getContentPane(), gbc, 0, 1, 1, 1, 0.1, 0.1);
        Common.insertInPanel(p3, getContentPane(), gbc, 0, 2, 1, 1, 0.1, 0.1);

        getRootPane().setPreferredSize(new Dimension(350 - 10, 300 - 50));
        getRootPane().setMinimumSize(new Dimension(350 - 10, 300 - 50));

        pack();

        //setResizable(false);
        setResizable(true);

        Common.setLocationRelativeTo(frame, this);
    }

    ////////////////////////////////////////////////////////////////////////////////
    public ArrayList showDialog()
            ////////////////////////////////////////////////////////////////////////////////
    {
        //MoveFocus.moveFocus(jtf);
        jtf.requestFocus();
        show();

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

        addButton.doClick();
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
            Common.showMessage("", "The list is empty", this);
        } else if (index == -1) {
            Common.showMessage("", "Please select an element from the list", this);
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
            Common.showMessage("", "The list is empty", this);
        } else if (index == -1) {
            Common.showMessage("", "Please select an element from the list", this);
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

    ////////////////////////////////////////////////////////////////////////////////
    private void addButtonEvent()
            ////////////////////////////////////////////////////////////////////////////////
    {
        // End any editing of the table
        endEditing();

        // make sure it doesn't exceed max amount of elements.
        if (model.size() >= 50) {
            Common.showMessage("Error", "You have exceeded the max array size of 50 elements.", this);

            // make sure record isn't blank
//    } else if (jtf.getText().equals("")) {
//      Common.showMessage("", "Please enter input value first.", this);

            // make sure record is a certain length
        } else if (jtf.getText().length() > 50) {
            Common.showMessage("Error", "You have exceeded the max argument size of 50 characters.", this);

        } else {
            alip.addElement(jtf.getText());
            model.addElement(jtf.getText());
            jtf.setText("");
        }
        MoveFocus.moveFocus(jtf);
    }

    ////////////////////////////////////////////////////////////////////////////////
    // POPS - 11/26/01 - added the super add button event..
    private void superAddButtonEvent()
            ////////////////////////////////////////////////////////////////////////////////
    {
        // End any editing of the table
        endEditing();
        // Tokenize the input line
        StringTokenizer str = new StringTokenizer(jtf.getText(), ",", true);

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

        // make sure it doesn't exceed max amount of elements.
        if (model.size() + tok.size() > 50) {
            Common.showMessage("Error", "You have exceeded the max array size of 50 elements. Please eliminate some of your elements", this);
            return;
        }

        // Switch telling us if we ignored some of the input
        boolean ignored = false;

        // Loop through all the tokens
        for (int x = 0; x < tok.size(); x++) {

            // Get the token
            String token = (String) tok.get(x);

            // ignore blank tokens...
//      if (token.equals("")) {
//          ignored = true;
//          continue;

            // ignore tokens that are too large
//      } else if ( token.length() > 50 ) {
            if (token.length() > 50) {
                ignored = true;
                continue;

                // otherwise - add the token
            } else {
                alip.addElement(token);
                model.addElement(token);
            }
        }

        // Let them know we ignored something...
        if (ignored) {
            Common.showMessage("Warning", "One or more entries were ignored because they were either empty (blank) or had a size greater than 50..", this);
        }

        // Reset the text and move focus to it...
        jtf.setText("");
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
            Common.showMessage("", "The list is empty", this);
        } else if (index == -1) {
            Common.showMessage("", "Please select an element from the list", this);
        } else {
            for (int i = indices.length - 1; i >= 0; i--) {
                alip.removeElement(indices[i]);
                model.removeElementAt(indices[i]);
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
        model.removeAllElements();
    }

    ////////////////////////////////////////////////////////////////////////////////
    private void okButtonEvent()
            ////////////////////////////////////////////////////////////////////////////////
    {
        // End any editing of the table
        endEditing();

        // Ask what to do if anything is left in the input line
        if (!jtf.getText().equals("")) {
            //switch (JOptionPane.showConfirmDialog(this, "You have un-saved information entered.  Do you wish to continue and lose that information?", "Un-saved information", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE)) {
            if (JOptionPane.showConfirmDialog(this, "You have un-saved information entered.  Do you wish to add it and continue?", "Warning", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE) == JOptionPane.YES_OPTION) {
                addButton.doClick();
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

        dispose();  // frees up the show() -- must be last
    }

    ////////////////////////////////////////////////////////////////////////////////
    private void cancelButtonEvent()
            ////////////////////////////////////////////////////////////////////////////////
    {
        status = false;
        dispose();  // frees up the show() -- must be last
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
        String text = jtf.getText();
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
            boolean ignored = false;
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
    //          if(param.length()>50||param.length()==0){
                        if (param.length() > 50) {
                            ignored = true;
                            continue;
                        }
                        alip.addElement(param);
                        model.addElement(param);
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
                            if (param.length() > 50) {
                                ignored = true;
                                continue;
                            }
                            alip.addElement(param);
                            model.addElement(param);
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
                            if (param.length() > 50) {
                                ignored = true;
                                continue;
                            }
                            alip.addElement(param);
                            model.addElement(param);
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
                if (param.length() > 50) {
                    ignored = true;
                } else {
                    alip.addElement(param);
                    model.addElement(param);
                }
            }
            if (ignored) {
                Common.showMessage("Warning", "One or more entries were ignored because they were either empty (blank) or had a size greater than 50..", this);
            }
        }
        // Reset the text and move focus to it...
        jtf.setText("");
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
