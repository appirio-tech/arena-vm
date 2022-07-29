package com.topcoder.client.spectatorApp.announcer.properties;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.Insets;
import java.text.NumberFormat;
import java.util.ArrayList;

import javax.swing.JFormattedTextField;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.JCheckBox;
import javax.swing.DefaultCellEditor;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumnModel;
import javax.swing.text.DefaultFormatterFactory;
import javax.swing.text.NumberFormatter;

import com.topcoder.client.spectatorApp.announcer.events.AnnounceTableResultsEvent;
import com.topcoder.client.spectatorApp.messages.TCSCoderInfo;

/**
 * A property panel that will allow setting/getting of the ShowRoundEvent
 * properties
 * 
 * @author Tim "Pops" Roberts
 * @version 1.0
 */
public class AnnounceTableResultsPropertyPanel extends PropertyPanel {

	/**
	 * The title field
	 */
	private JTextField titleField = new JTextField();
	
	/**
	 * The round id field
	 */
	private JFormattedTextField roundID = new JFormattedTextField();
	
	/**
	 * The results table
	 */
	private JTable resultsTable = new JTable();
	
	/**
	 * The results model
	 */
	private ResultsModel resultsModel;
	
	/**
	 * The column headers table
	 */
	private JTable headersTable = new JTable();
	
	/**
	 * The column headers model
	 */
	private HeadersModel headersModel;
	
	/**
	 * The coder table
	 */
	private JTable coderTable = new JTable();
	
	/**
	 * The coder model
	 */
	private CoderModel coderModel;
	
	/**
	 * Creates a Title Property Panel to display the title
	 * @param event
	 */
	public AnnounceTableResultsPropertyPanel(AnnounceTableResultsEvent event) {
		// Call super constructor
		super(event);
		
		// Setup the roundid text field
		
		// Setup the number formatter
		NumberFormatter integerFormatter = new NumberFormatter(NumberFormat.getIntegerInstance());
		integerFormatter.setValueClass(Integer.class);
		integerFormatter.setMinimum(new Integer(0));		
		DefaultFormatterFactory integerFactory = new DefaultFormatterFactory(integerFormatter);
		
		// Setup the table(s)
		headersModel = new HeadersModel(event);
		headersTable.setModel(headersModel);
		headersTable.setPreferredScrollableViewportSize(new Dimension(100,20));
		headersTable.getTableHeader().setReorderingAllowed(false);
		
		coderModel = new CoderModel(event);
		coderTable.setModel(coderModel);
		coderTable.getColumnModel().getColumn(1).setCellEditor(new FormattedTextEditor(new JFormattedTextField(integerFactory)));
		coderTable.getColumnModel().getColumn(2).setCellEditor(new FormattedTextEditor(new JFormattedTextField(integerFactory)));
		coderTable.setPreferredScrollableViewportSize(new Dimension(100,20));
		coderTable.getTableHeader().setReorderingAllowed(false);
		
		resultsModel = new ResultsModel(event);
		resultsTable.setModel(resultsModel);
		resultsTable.setPreferredScrollableViewportSize(new Dimension(100,20));
		resultsTable.getTableHeader().setReorderingAllowed(false);
		TableColumnModel columnModel = resultsTable.getColumnModel();
		NumberFormatter rankFormatter = new NumberFormatter(NumberFormat.getIntegerInstance());
		rankFormatter.setValueClass(Integer.class);
		rankFormatter.setMinimum(new Integer(1));
		rankFormatter.setMaximum(new Integer(event.getCoders().length));
		DefaultFormatterFactory rankFactory = new DefaultFormatterFactory(rankFormatter);
        columnModel.getColumn(1).setCellEditor(new FormattedTextEditor(new JFormattedTextField(rankFactory)));
        columnModel.getColumn(columnModel.getColumnCount() - 1).setCellEditor(new DefaultCellEditor(new JCheckBox()));
        columnModel.getColumn(columnModel.getColumnCount() - 1).setCellRenderer(new ResultsRenderer());
			
		// Set the title field
		titleField.setText(event.getTitle());
		roundID.setFormatterFactory(integerFactory);
		roundID.setValue(new Integer(event.getRoundID()));
		
		// Setup the panel
		int currentRow = 0;
		this.add(new JLabel("Title: "), new GridBagConstraints(0,currentRow,1,1,0,0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(5,5,5,5), 0, 0));
		this.add(titleField, new GridBagConstraints(1,currentRow,1,1,1,0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(5,5,5,5), 0, 0));
		this.add(new JLabel("Round ID: "), new GridBagConstraints(0,++currentRow,1,1,0,0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(5,5,5,5), 0, 0));
		this.add(roundID, new GridBagConstraints(1,currentRow,1,1,1,0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(5,5,5,5), 0, 0));
		this.add(new JScrollPane(headersTable), new GridBagConstraints(0,++currentRow,2,1,1,0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(5,5,5,5), 0, 0));
		this.add(new JScrollPane(coderTable), new GridBagConstraints(0,++currentRow,2,1,1,1, GridBagConstraints.WEST, GridBagConstraints.BOTH, new Insets(5,5,5,5), 0, 0));
		this.add(new JScrollPane(resultsTable), new GridBagConstraints(0,++currentRow,2,1,1,1, GridBagConstraints.WEST, GridBagConstraints.BOTH, new Insets(5,5,5,5), 0, 0));
		this.add(new JLabel(), new GridBagConstraints(0,++currentRow,2,1,1,1, GridBagConstraints.NORTHWEST, GridBagConstraints.BOTH, new Insets(5,5,5,5), 0, 0));
		
	}

	/**
	 * Sets the title to the current title displayed
	 */
	public void saveProperties() {
		
		// Force the table to stop editing
		if(resultsTable.isEditing()) resultsTable.getCellEditor().stopCellEditing();
		if(headersTable.isEditing()) headersTable.getCellEditor().stopCellEditing();
		if(coderTable.isEditing()) coderTable.getCellEditor().stopCellEditing();
		
		// Convience cast
		AnnounceTableResultsEvent event = (AnnounceTableResultsEvent)getEvent();
		
		// Set the title
		event.setTitle(titleField.getText());
		event.setRoundID(((Integer)roundID.getValue()).intValue());
		event.setCoders(coderModel.getCoderInfo());
		event.setColumnHeaders(headersModel.getColumnHeaders());
		event.setHighlights(resultsModel.getHighlights());
		event.setScores(resultsModel.getScores());
		event.setRanks(resultsModel.getRanks());
	
		// Validate event
		try {
			getEvent().validateEvent();
		} catch (Exception e) {
			JOptionPane.showMessageDialog(null, "Error Validating the event: \r\n" + e.toString(), "Error Validating Event", JOptionPane.ERROR_MESSAGE);
		}

	}

	/**
	 * Table model used for the coder's table
	 * 
	 * @author Tim "Pops" Roberts
	 * @version 1.0
	 */
	class CoderModel extends DefaultTableModel {
		/** Constructor from the event */
		public CoderModel(AnnounceTableResultsEvent event) {
			// Create the columns
			addColumn("Handle");
			addColumn("TCRating");
			addColumn("Seed");
			
			// Create the rows
			TCSCoderInfo[] info = event.getCoders();
			for(int x=0;x<info.length;x++) {
				addRow(new Object[] {
					info[x].getHandle(),
					new Integer(info[x].getTcRating()),
					new Integer(info[x].getSeed())
				});
			}
		}
		
		/** Overridden to ensure handle update affects the results table */
		public void setValueAt(Object o, int row, int col) {
			// Call the super set
			super.setValueAt(o, row, col);
			
			// If it's the coder handle, update the results table
			if(col==0) {
				resultsModel.setCoderName(row, (String)o);
			}
		}
		
		/**
		 * Returns the list of coder info from this model
		 * @return the list of coder info from this model
		 */
		public TCSCoderInfo[] getCoderInfo() {
			TCSCoderInfo[] coders = new TCSCoderInfo[getRowCount()];
			for(int x=0;x<coders.length;x++) {
				coders[x] = new TCSCoderInfo((String)getValueAt(x,0),
						((Integer)getValueAt(x,2)).intValue(),
						((Integer)getValueAt(x,1)).intValue());
			}
			
			return coders;
		}
	}
	
	/**
	 * Default cell renderer to yellow the background of calculated totals
	 */
	class ResultsRenderer extends DefaultTableCellRenderer {
		Color norm;
		private JCheckBox check = new JCheckBox();
		public ResultsRenderer() {
			super();
			norm = getBackground();
		}
		public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
			Component comp = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
			comp.setBackground(norm);
			
			ResultsModel model = (ResultsModel)table.getModel();
			/*
            if(!model.isHighlighted(row)) {
				comp.setBackground(Color.yellow);
			}
			*/
			if(value instanceof Boolean) {
                check.setSelected(((Boolean)value).booleanValue());
                return check;
			}
			return comp;
		}
	}
	/**
	 * Table model used for the results table
	 * 
	 * @author Tim "Pops" Roberts
	 * @version 1.0
	 */
	class ResultsModel extends DefaultTableModel {
		/** Constructor from the event */
		public ResultsModel(AnnounceTableResultsEvent event) {
			String[][] scores = event.getScores();
			boolean[] highlights = event.getHighlights();
			
			// Setup the columns
			addColumn("Handle");			
			addColumn("Rank");			
			for(int x=0;x<event.getColumnHeaders().length;x++) addColumn(event.getColumnHeaders()[x]);
			addColumn("Highlight");
			
			// Setup all the handles
			for(int x=0;x<scores.length;x++) {
				ArrayList list = new ArrayList();
				list.add(event.getCoders()[x].getHandle());
				list.add(new Integer(event.getRanks()[x]));
				
				for(int y=0;y<scores[x].length;y++) {
                    list.add(scores[x][y]);
				}
				
                list.add(new Boolean(event.getHighlights()[x]));
				
				addRow(list.toArray());
			}
			
		}

		/**
		 * Sets the coder name directly (since it's uneditable)
		 * @param row the row to set
		 * @param handle the handle to use
		 */
		public void setCoderName(int row, String handle) {
			super.setValueAt(handle, row, 0);
		}
		
		/**
		 * Overridden to make the handles and final totals uneditable
		 */
		public boolean isCellEditable(int r, int c) {
			// If it's the coder handle column 
			if(c==0 || c>=getColumnCount()) return false;
			return true;
		}
		
		public boolean isHighlighted(int r) {
            return ((Boolean)getValueAt(r,getColumnCount()-1)).booleanValue();
		}
		
		/**
		 * Returns the total scores
		 * @return the total scores
		 */
		public boolean[] getHighlights() {
			boolean[] highlights = new boolean[getRowCount()];
			for(int x=0;x<highlights.length;x++)
                highlights[x] = ((Boolean)getValueAt(x,getColumnCount()-1)).booleanValue();
			return highlights;
		}
		
		/**
		 * Returns the scores
		 * @return the scores
		 */
		public String[][] getScores() {
			String[][] scores = new String[getRowCount()][getColumnCount()-3];

			for(int x=0;x<getRowCount();x++) {
				for(int y=2;y<getColumnCount()-1;y++) {
					scores[x][y-2] = (String)getValueAt(x,y);
				}
			}
			return scores;
		}

        public int[] getRanks() {
            int[] ranks = new int[getRowCount()];
            for (int x=0;x<getRowCount();++x) {
                ranks[x] = ((Integer)getValueAt(x,1)).intValue();
            }
            
            return ranks;
        }
	}
	
	/**
	 * Table model used for the column headers table
	 * 
	 * @author Tim "Pops" Roberts
	 * @version 1.0
	 */
	class HeadersModel extends DefaultTableModel {
		/** Constructor from the event */
		public HeadersModel(AnnounceTableResultsEvent event) {
			String[] names = event.getColumnHeaders();
			for(int x=0;x<names.length;x++) addColumn("Column " + x, new Object[] { names[x] });
		}
	
		/** Overridden to make sure the results table column is sync'd */
		public void setValueAt(Object o, int row, int col) {
			super.setValueAt(o, row, col);
			
			// Set the column (+1 to get over the handle)
			resultsTable.getColumnModel().getColumn(col+1).setHeaderValue(o);
			resultsTable.getTableHeader().resizeAndRepaint();
			
		}
		
		/**
		 * Returns the column headers
		 * @return the column headers
		 */
		public String[] getColumnHeaders() {
			String[] names = new String[getColumnCount()];
			for(int x=0;x<names.length;x++) names[x] = (String)getValueAt(0,x);
			return names;
		}
	}
}
