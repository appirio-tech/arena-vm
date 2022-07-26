/*
 * PaymentPanelViewImpl.java
 *
 * Created on December 12, 2006, 9:44 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package com.topcoder.client.mpsqasApplet.view.defaultimpl.component;

import com.topcoder.client.mpsqasApplet.common.UpdateTypes;
import com.topcoder.client.mpsqasApplet.controller.component.ComponentController;
import com.topcoder.client.mpsqasApplet.controller.component.PaymentPanelController;
import com.topcoder.client.mpsqasApplet.model.component.ComponentModel;
import com.topcoder.client.mpsqasApplet.model.component.PaymentPanelModel;
import com.topcoder.client.mpsqasApplet.view.component.PaymentPanelView;
import com.topcoder.client.mpsqasApplet.view.defaultimpl.DefaultUIValues;
import com.topcoder.client.mpsqasApplet.view.defaultimpl.GUIConstants;
import com.topcoder.client.mpsqasApplet.view.defaultimpl.listener.AppletActionListener;
import com.topcoder.client.mpsqasApplet.view.defaultimpl.sortabletable.SortableTable;
import com.topcoder.client.mpsqasApplet.view.defaultimpl.sortabletable.SortableTableModel;
import com.topcoder.client.mpsqasApplet.view.defaultimpl.sortabletable.TextBoxRenderer;
import com.topcoder.netCommon.mpsqas.ApplicationConstants;
import com.topcoder.netCommon.mpsqas.PaymentInformation;
import com.topcoder.netCommon.mpsqas.UserInformation;
import java.awt.Component;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import javax.swing.AbstractCellEditor;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableCellEditor;

/**
 *
 * @author rfairfax
 */
public class PaymentPanelViewImpl extends PaymentPanelView {
    
    private PaymentPanelController controller;
    private PaymentPanelModel model;
    
    public void finishTesterEditing() {
        testersTable.removeEditor();
    }
    
    public void finishWriterEditing() {
        writersTable.removeEditor();
    }
    
    public int getSelectedTesterIndex() {
        return testersTable.getSelectedRow();
    }
    
    public int getSelectedWriterIndex() {
        return writersTable.getSelectedRow();
    }
    
    public double getTesterPayment(int idx) {
        return ((Double)testersTable.getTableModel().getData()[idx][1]).doubleValue();
    }
    
    public double getWriterPayment(int idx) {
        return ((Double)writersTable.getTableModel().getData()[idx][1]).doubleValue();
    }
    
    public int getTesterID(int idx) {
        return Integer.parseInt(((JButton)testersTable.getTableModel().getData()[idx][3]).getActionCommand());
    }
    
    public int getWriterID(int idx) {
        return Integer.parseInt(((JButton)writersTable.getTableModel().getData()[idx][3]).getActionCommand());
    }
    
    public void setController(ComponentController controller) {
        this.controller = (PaymentPanelController)controller;
    }
    
    public void setModel(ComponentModel model) {
        this.model = (PaymentPanelModel)model;
        model.addWatcher(this);
    }
    
    public String getName() {
        return "Payment";
    }
    
    public void init() {
        setLayout(layout = new GridBagLayout());
    }
    
    private GridBagLayout layout;
    private JLabel testerTitleLabel;
    private JLabel roundLabel;
    
    private SortableTable testersTable;
    private JScrollPane testersScrollPane;
    
    private JLabel writersTitleLabel;
    
    private SortableTable writersTable;
    private JScrollPane writersScrollPane;
    
    private final String[] COLUMN_HEADERS = new String[] { "Handle", "Amount", "Status", "" };
    
    public void update(Object arg) {
        if(arg == null) {
            //new panel
            removeAll();
            
            GridBagConstraints gbc = new GridBagConstraints();
            
            writersTitleLabel = new JLabel("Problem Writers:");
            writersTitleLabel.setFont(DefaultUIValues.HEADER_FONT);
            GUIConstants.buildConstraints(gbc, 0, 0, 1, 1, 0, 0);
            gbc.insets = new Insets(5, 5, 5, 5);
            gbc.anchor = gbc.WEST;
            gbc.fill = gbc.NONE;
            layout.setConstraints(writersTitleLabel, gbc);
            add(writersTitleLabel, gbc);
            
            writersTable = new SortableTable(COLUMN_HEADERS,
                    getAllWriters(),false);
            ((SortableTableModel)writersTable.getModel()).setEditable(
                    new boolean[] {false,true,false,true});
            writersTable.getColumnModel().getColumn(1).setCellRenderer(
                    new TextBoxRenderer());
            writersTable.getColumnModel().getColumn(3).setCellRenderer(
                    new ButtonRenderer());
            writersTable.getColumnModel().getColumn(3).setCellEditor(
                    new JTableButtonEditor());
            writersScrollPane = new JScrollPane(writersTable);
            gbc.fill = GridBagConstraints.BOTH;
            GUIConstants.buildConstraints(gbc, 0, 1, 1, 1, 100, 100);
            layout.setConstraints(writersScrollPane, gbc);
            add(writersScrollPane);
            
            testerTitleLabel = new JLabel("Problem Testers:");
            testerTitleLabel.setFont(DefaultUIValues.HEADER_FONT);
            GUIConstants.buildConstraints(gbc, 0, 2, 1, 1, 0, 0);
            gbc.insets = new Insets(5, 5, 5, 5);
            gbc.anchor = gbc.WEST;
            gbc.fill = gbc.NONE;
            layout.setConstraints(testerTitleLabel, gbc);
            add(testerTitleLabel, gbc);
            
            roundLabel = new JLabel("Round: " + model.getRoundName());
            roundLabel.setFont(DefaultUIValues.NORMAL_FONT);
            GUIConstants.buildConstraints(gbc, 0, 3, 1, 1, 0, 0);
            gbc.insets = new Insets(5, 5, 5, 5);
            gbc.anchor = gbc.WEST;
            gbc.fill = gbc.NONE;
            layout.setConstraints(roundLabel, gbc);
            add(roundLabel, gbc);
            
            testersTable = new SortableTable(COLUMN_HEADERS,
                    getAllTesters(),false);
            ((SortableTableModel)testersTable.getModel()).setEditable(
                    new boolean[] {false,true,false,true});
            testersTable.getColumnModel().getColumn(1).setCellRenderer(
                    new TextBoxRenderer());
            testersTable.getColumnModel().getColumn(3).setCellRenderer(
                    new ButtonRenderer());
            testersTable.getColumnModel().getColumn(3).setCellEditor(
                    new JTableButtonEditor());
            testersScrollPane = new JScrollPane(testersTable);
            gbc.fill = GridBagConstraints.BOTH;
            GUIConstants.buildConstraints(gbc, 0, 4, 1, 1, 100, 100);
            layout.setConstraints(testersScrollPane, gbc);
            add(testersScrollPane);
        } else if(arg == UpdateTypes.PAYMENTS_CHANGE) {
            testersTable.setData(getAllTesters());
            writersTable.setData(getAllWriters());
        }
    }
    
    private Object[][] getAllTesters() {
        Object[][] tableData = new Object[model.getTesters().size()][4];
        for (int i = 0; i < model.getTesters().size(); i++) {
            tableData[i][0] = ((UserInformation) model.getTesters().get(i))
            .getHandle();
            
            double payment = 0;
            //look up default
            if(model.getDivision() == -1)
                payment = 225;
            else
                payment = ApplicationConstants.TESTING_PAYMENT[model.getDivision()][model.getDifficulty()];
            
            PaymentInformation info = getPaymentInfoForTester(((UserInformation) model.getTesters().get(i)).getUserId());
            String status = "Not Paid";
            boolean enabled = true;
            String buttonText = "Pay";
            if(info != null) {
                status = info.getStatus();
                payment = info.getAmount();
                buttonText = "Update";
                if(!status.equals("Not Paid") && !status.equals("Pending")) {
                    enabled = false;
                }
            }
            
            tableData[i][1] = new Double(payment);
            tableData[i][2] = status;
            JButton button = new JButton(buttonText);
            button.setEnabled(enabled);
            
            button.addActionListener(new AppletActionListener(
                    "processTesterPayment", controller, false));
            button.setActionCommand(""+((UserInformation) model.getTesters().get(i)).getUserId());
            tableData[i][3] = button;
        }
        
        return tableData;
    }
    
    private PaymentInformation getPaymentInfoForTester(int coderId) {
        PaymentInformation ret = null;
        for(int i = 0; i < model.getTesterPayments().size(); i++) {
            PaymentInformation info = (PaymentInformation)model.getTesterPayments().get(i);
            if(info.getCoderID() == coderId)
                return info;
        }
        return ret;
    }
    
    private PaymentInformation getPaymentInfoForWriter(int coderId) {
        PaymentInformation ret = null;
        for(int i = 0; i < model.getWriterPayments().size(); i++) {
            PaymentInformation info = (PaymentInformation)model.getWriterPayments().get(i);
            if(info.getCoderID() == coderId)
                return info;
        }
        return ret;
    }
    
    private Object[][] getAllWriters() {
        Object[][] tableData = new Object[model.getWriters().size()][4];
        for (int i = 0; i < model.getWriters().size(); i++) {
            tableData[i][0] = ((UserInformation) model.getWriters().get(i))
            .getHandle();
            
            double payment = 0;
            //look up default
            if(model.getDivision() == -1)
                payment = 150;
            else
                payment = ApplicationConstants.WRITING_PAYMENT[model.getDivision()][model.getDifficulty()];
            
            PaymentInformation info = getPaymentInfoForWriter(((UserInformation) model.getWriters().get(i)).getUserId());
            String status = "Not Paid";
            boolean enabled = true;
            String buttonText = "Pay";
            if(info != null) {
                status = info.getStatus();
                payment = info.getAmount();
                buttonText = "Update";
                if(!status.equals("Not Paid") && !status.equals("Pending")) {
                    enabled = false;
                }
            }
            
            tableData[i][1] = new Double(payment);
            tableData[i][2] = status;
            JButton button = new JButton(buttonText);
            button.setEnabled(enabled);
            button.addActionListener(new AppletActionListener(
                    "processWriterPayment", controller, false));
            button.setActionCommand(""+((UserInformation) model.getWriters().get(i)).getUserId());
            tableData[i][3] = button;
        }
        return tableData;
    }
    
    
    
    //class to generate buttons for payment
    public class ButtonRenderer extends DefaultTableCellRenderer {
       
        public Component getTableCellRendererComponent(JTable table, Object value,
                boolean isSelected, boolean hasFocus, int row, int column) {
            
            return (Component)value;
        }
    }
    
    public class JTableButtonEditor extends AbstractCellEditor
            implements TableCellEditor {
        
        public JTable table;
        
        public Component getTableCellEditorComponent(
                JTable table, Object value, boolean isSelected,
                int row, int column) {
            this.table = table;
            return (Component)value;
        }
        
        public Object getCellEditorValue() {
            return table.getValueAt(table.getEditingRow(), 3);
        }
        
    }
    
    
}
