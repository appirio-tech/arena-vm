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
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumnModel;
import javax.swing.text.DefaultFormatterFactory;
import javax.swing.text.NumberFormatter;

import com.topcoder.client.spectatorApp.announcer.events.AnnounceReviewBoardResultsEvent;
import com.topcoder.client.spectatorApp.messages.TCSCoderInfo;

/**
 * A property panel that will allow setting/getting of the ShowRoundEvent
 * properties
 * 
 * @author Tim "Pops" Roberts
 * @version 1.0
 */
public class AnnounceReviewBoardResultsPropertyPanel extends PropertyPanel {

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
	 * The reviewers table
	 */
	private JTable reviewersTable = new JTable();
	
	/**
	 * The reviewers model
	 */
	private ReviewersModel reviewersModel;
	
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
	public AnnounceReviewBoardResultsPropertyPanel(AnnounceReviewBoardResultsEvent event) {
		// Call super constructor
		super(event);
		
		//reviewersTable.getCol
		
		// Setup the roundid text field
		
		// Setup the number formatter
		NumberFormatter integerFormatter = new NumberFormatter(NumberFormat.getIntegerInstance());
		integerFormatter.setValueClass(Integer.class);
		integerFormatter.setMinimum(new Integer(0));		
		DefaultFormatterFactory integerFactory = new DefaultFormatterFactory(integerFormatter);
		
		NumberFormatter doubleFormatter = new NumberFormatter(NumberFormat.getNumberInstance());
		doubleFormatter.setValueClass(Double.class);
		doubleFormatter.setMinimum(new Double(-1));		
		DefaultFormatterFactory doubleFactory = new DefaultFormatterFactory(doubleFormatter);
		
		// Setup the table(s)
		reviewersModel = new ReviewersModel(event);
		reviewersTable.setModel(reviewersModel);
		reviewersTable.setPreferredScrollableViewportSize(new Dimension(100,20));
		reviewersTable.getTableHeader().setReorderingAllowed(false);
		
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
		for(int x=1;x<columnModel.getColumnCount();x++) {
			columnModel.getColumn(x).setCellEditor(new FormattedTextEditor(new JFormattedTextField(doubleFactory)));
			if(x==columnModel.getColumnCount()-1) {
				columnModel.getColumn(x).setCellRenderer(new ResultsRenderer());
			}
		}
		
			
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
		this.add(new JScrollPane(reviewersTable), new GridBagConstraints(0,++currentRow,2,1,1,0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(5,5,5,5), 0, 0));
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
		if(reviewersTable.isEditing()) reviewersTable.getCellEditor().stopCellEditing();
		if(coderTable.isEditing()) coderTable.getCellEditor().stopCellEditing();
		
		// Convience cast
		AnnounceReviewBoardResultsEvent event = (AnnounceReviewBoardResultsEvent)getEvent();
		
		// Set the title
		event.setTitle(titleField.getText());
		event.setRoundID(((Integer)roundID.getValue()).intValue());
		event.setCoders(coderModel.getCoderInfo());
		event.setReviewerNames(reviewersModel.getReviewerNames());
		event.setFinalScores(resultsModel.getTotals());
		event.setScores(resultsModel.getScores());
	
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
		public CoderModel(AnnounceReviewBoardResultsEvent event) {
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
		public ResultsRenderer() {
			super();
			norm = getBackground();
		}
		public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
			Component comp = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
			comp.setBackground(norm);
			
			ResultsModel model = (ResultsModel)table.getModel();
			if(column==model.getColumnCount()-1) {
				if(!model.isTotalCalculated(row)) {
					comp.setBackground(Color.yellow);
				}
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
		/** Boolean indicating whether the final total is calculated or not */
		private boolean[] finalTotalCalculated;
		
		/** Constructor from the event */
		public ResultsModel(AnnounceReviewBoardResultsEvent event) {
			double[][] scores = event.getScores();
			double[] finalScores = event.getFinalScores();
			finalTotalCalculated = new boolean[finalScores.length];
			
			// Setup the columns
			addColumn("Handle");			
			for(int x=0;x<event.getReviewerNames().length;x++) addColumn(event.getReviewerNames()[x]);
			addColumn("Total");
			
			// Setup all the handles
			for(int x=0;x<scores.length;x++) {
				ArrayList list = new ArrayList();
				list.add(event.getCoders()[x].getHandle());
				
				int total = 0;
				
				for(int y=0;y<scores[x].length;y++) {
					list.add(new Double(scores[x][y]));
					if(scores[x][y]>=0) total += Math.round(scores[x][y] * 100);
				}
				total =(int)Math.round((double)total / scores[x].length);
				
				// Determine if the final score overrides the total or not
				if(total==Math.round(finalScores[x]*100)) {
					finalTotalCalculated[x] = true;
					list.add(new Double(total/100.0));
				} else {  
					finalTotalCalculated[x] = false;
					list.add(new Double(finalScores[x]));
				}
				
				addRow(list.toArray());
			}
			
		}
		
		/** Returns whether the total is calculated for the row or not */
		public boolean isTotalCalculated(int row) {
			return finalTotalCalculated[row];
		}
		
		/** Overridden to ensure final total is recalculated when necessary */
		public void setValueAt(Object o, int row, int col) {
			// Round
			Double d= (Double)o;
			d = new Double(Math.round(d.doubleValue() * 100.0)/100.0);
			
			super.setValueAt(d, row, col);
			
			// Determine if it's calculated or not
			if(col==getColumnCount()-1) {
				if(d.doubleValue()==0.0 || calcTotal(row).equals(d)) {
					finalTotalCalculated[row] = true;
				} else {
					finalTotalCalculated[row] = false;
				}
			}
			
			// Recalc totals and set the final total
			if(finalTotalCalculated[row] && col>0) {
				// Set final total
				super.setValueAt(calcTotal(row), row, getColumnCount()-1);
			}
		}
		
		private Double calcTotal(int row) {
			// Recalc
			int total = 0;
			for(int x=1;x<getColumnCount()-1;x++) {
				double score = ((Double)getValueAt(row, x)).doubleValue();
				if(score>=0) total+= Math.round(score*100);
			}
			total = (int)Math.round(((double)total / (getColumnCount() - 2)));
			
			return new Double(total/100.0);
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
		
		/**
		 * Returns the total scores
		 * @return the total scores
		 */
		public double[] getTotals() {
			double[] totals = new double[getRowCount()];
			for(int x=0;x<totals.length;x++) totals[x] = ((Double)getValueAt(x,getColumnCount()-1)).doubleValue();
			return totals;
		}
		
		/**
		 * Returns the scores
		 * @return the scores
		 */
		public double[][] getScores() {
			double[][] scores = new double[getRowCount()][getColumnCount()-2];

			for(int x=0;x<getRowCount();x++) {
				for(int y=1;y<getColumnCount()-1;y++) {
					scores[x][y-1] = ((Double)getValueAt(x,y)).doubleValue();
				}
			}
			return scores;
		}

	}
	
	/**
	 * Table model used for the reviewers table
	 * 
	 * @author Tim "Pops" Roberts
	 * @version 1.0
	 */
	class ReviewersModel extends DefaultTableModel {
		/** Constructor from the event */
		public ReviewersModel(AnnounceReviewBoardResultsEvent event) {
			String[] names = event.getReviewerNames();
			for(int x=0;x<names.length;x++) addColumn("Reviewer " + x, new Object[] { names[x] });
		}
	
		/** Overridden to make sure the results table column is sync'd */
		public void setValueAt(Object o, int row, int col) {
			super.setValueAt(o, row, col);
			
			// Set the column (+1 to get over the handle)
			resultsTable.getColumnModel().getColumn(col+1).setHeaderValue(o);
			resultsTable.getTableHeader().resizeAndRepaint();
			
		}
		
		/**
		 * Returns the reviewer names
		 * @return the reviewer names
		 */
		public String[] getReviewerNames() {
			String[] names = new String[getColumnCount()];
			for(int x=0;x<names.length;x++) names[x] = (String)getValueAt(0,x);
			return names;
		}
	}
}
