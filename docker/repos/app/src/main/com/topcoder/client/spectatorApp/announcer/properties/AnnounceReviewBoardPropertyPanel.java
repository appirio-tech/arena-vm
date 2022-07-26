package com.topcoder.client.spectatorApp.announcer.properties;

import java.awt.GridBagConstraints;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.NumberFormat;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFormattedTextField;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.table.DefaultTableModel;
import javax.swing.text.DefaultFormatterFactory;
import javax.swing.text.NumberFormatter;

import com.topcoder.client.spectatorApp.announcer.events.AnnounceReviewBoardEvent;

/**
 * A property panel that will allow setting/getting of the ShowRoundEvent
 * properties
 * 
 * @author Tim "Pops" Roberts
 * @version 1.0
 */
public class AnnounceReviewBoardPropertyPanel extends PropertyPanel implements ActionListener {

	/**
	 * The title field
	 */
	private JTextField titleField = new JTextField();
	
	/**
	 * The Reviewers table
	 */
	private JTable reviewersTable = new JTable();
	
	/**
	 * The table model
	 */
	private DefaultTableModel reviewersModel = new DefaultTableModel();
	
	/**
	 * Creates a Title Property Panel to display the title
	 * @param event
	 */
	public AnnounceReviewBoardPropertyPanel(AnnounceReviewBoardEvent event) {
		// Call super constructor
		super(event);
		
		//reviewersTable.getCol
		
		// Setup the roundid text field
		final JFormattedTextField tcRatingField = new JFormattedTextField();
		tcRatingField.setFocusLostBehavior(JFormattedTextField.COMMIT_OR_REVERT);
		
		JFormattedTextField tcsRatingField = new JFormattedTextField();
		tcsRatingField.setFocusLostBehavior(JFormattedTextField.COMMIT_OR_REVERT);
		
		// Setup the number formatter
		NumberFormatter formatter = new NumberFormatter(NumberFormat.getIntegerInstance());
		formatter.setValueClass(Integer.class);
		formatter.setMinimum(new Integer(0));
		
		// Create the factory and set the value
		DefaultFormatterFactory factory = new DefaultFormatterFactory(formatter);
		tcRatingField.setFormatterFactory(factory);
		tcsRatingField.setFormatterFactory(factory);

		
		// Populate the rows
		reviewersModel.addColumn("Handles");
		reviewersModel.addColumn("TCRating");
		reviewersModel.addColumn("TCSRating");
		reviewersModel.addColumn("Image FileName");
		for(int x=0;x<event.getReviewerHandles().length;x++) {
			reviewersModel.addRow(new Object[] { 
					event.getReviewerHandles()[x],
					new Integer(event.getReviewerTCRating()[x]),
					new Integer(event.getReviewerTCSRating()[x]),
					event.getReviewerImageFileName()[x]
			});
		}
		
		// Setup the table
		reviewersTable.setModel(reviewersModel);
		reviewersTable.getColumnModel().getColumn(1).setCellEditor(new FormattedTextEditor(tcRatingField));
		reviewersTable.getColumnModel().getColumn(1).setMaxWidth(70);
		reviewersTable.getColumnModel().getColumn(2).setCellEditor(new FormattedTextEditor(tcsRatingField));
		reviewersTable.getColumnModel().getColumn(2).setMaxWidth(70);
		
			
		// Set the title field
		titleField.setText(event.getTitle());
		
		// The browse button
		JButton browse = new JButton("Browse");
		browse.addActionListener(this);
		
		// Setup the panel
		int currentRow = 0;
		this.add(new JLabel("Title: "), new GridBagConstraints(0,currentRow,1,1,0,0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(5,5,5,5), 0, 0));
		this.add(titleField, new GridBagConstraints(1,currentRow,1,1,1,0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(5,5,5,5), 0, 0));
		this.add(new JScrollPane(reviewersTable), new GridBagConstraints(0,++currentRow,2,1,1,0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(5,5,5,5), 0, 0));
		this.add(browse, new GridBagConstraints(2,currentRow,1,1,0,0, GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, new Insets(5,5,5,5), 0, 0));
		this.add(new JLabel(), new GridBagConstraints(0,++currentRow,2,1,1,1, GridBagConstraints.NORTHWEST, GridBagConstraints.BOTH, new Insets(5,5,5,5), 0, 0));
		
	}

	/**
	 * Sets the title to the current title displayed
	 */
	public void saveProperties() {
		
		// Force the table to stop editing
		if(reviewersTable.isEditing()) reviewersTable.getCellEditor().stopCellEditing();
		
		// Convience cast
		AnnounceReviewBoardEvent event = (AnnounceReviewBoardEvent)getEvent();
		
		// Set the title
		event.setTitle(titleField.getText());
	
		// Get all the values
		int N = reviewersModel.getRowCount();
		String[] handles = new String[N];
		int[] tcRating = new int[N];
		int[] tcsRating = new int[N];
		String[] images = new String[N];
		for(int x=0;x<N;x++) {
			handles[x] = (String)reviewersModel.getValueAt(x, 0);
			tcRating[x] = ((Integer)reviewersModel.getValueAt(x, 1)).intValue();
			tcsRating[x] = ((Integer)reviewersModel.getValueAt(x, 2)).intValue();
			images[x] = (String)reviewersModel.getValueAt(x, 3);
		}
		
		// Set'em
		event.setReviewerHandles(handles);
		event.setReviewerTCRating(tcRating);
		event.setReviewerTCSRating(tcsRating);
		event.setReviewerImageFileName(images);
		
		// Validate event
		try {
			getEvent().validateEvent();
		} catch (Exception e) {
			JOptionPane.showMessageDialog(null, "Error Validating the event: \r\n" + e.toString(), "Error Validating Event", JOptionPane.ERROR_MESSAGE);
		}

	}

	/**
	 * Implements the browse button fucntionality
	 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
	 */
	public void actionPerformed(ActionEvent e) {
		// Get the selected row (if none, silently return)
		int row = reviewersTable.getSelectedRow();
		if(row<0) return;
		
		// Create a file chooser initialized with the value
		JFileChooser fileChooser = new JFileChooser((String)reviewersModel.getValueAt(row, 3));
		
		// Open and reset
		int returnVal = fileChooser.showOpenDialog(null);
		if(returnVal == JFileChooser.APPROVE_OPTION) {
			reviewersModel.setValueAt(fileChooser.getSelectedFile().getAbsolutePath(), row, 3);
		}

		
	}
	
}
