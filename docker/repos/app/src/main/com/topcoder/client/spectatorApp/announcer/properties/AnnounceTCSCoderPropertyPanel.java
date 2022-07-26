package com.topcoder.client.spectatorApp.announcer.properties;

import java.awt.GridBagConstraints;
import java.awt.Insets;
import java.text.NumberFormat;
import java.text.ParseException;

import javax.swing.JComboBox;
import javax.swing.JFormattedTextField;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTextField;
import javax.swing.text.DefaultFormatterFactory;
import javax.swing.text.NumberFormatter;

import com.topcoder.client.spectatorApp.announcer.events.AnnounceTCSCoderEvent;

/**
 * A property panel that will allow setting/getting of the AnnounceTCSCoderEvent
 * properties
 * 
 * @author Tim "Pops" Roberts
 * @version 1.0
 */
public class AnnounceTCSCoderPropertyPanel extends PropertyPanel {

	/**
	 * The title field
	 */
	private JTextField titleField = new JTextField();
	
	/**
	 * The round id field
	 */
	private JFormattedTextField roundID = new JFormattedTextField();
	
	/**
	 * The coder name
	 */
	private JTextField coderName = new JTextField();
	
	/**
	 * The coder type
	 */
	private JComboBox coderType = new JComboBox(new Object[] {"Student","Professional"});
	
	/**
	 * The image filename
	 */
	private JTextField imageFileName = new JTextField();
	
	/**
	 * The coder handle
	 */
	private JTextField coderHandle = new JTextField();
	
      	/**
	 * The coder school
	 */
	private JTextField coderSchool = new JTextField();

	/**
	 * The TC Rating
	 */
	private JFormattedTextField tcRating = new JFormattedTextField();
	
	/**
	 * The TCS Rating
	 */
	private JFormattedTextField tcsRating = new JFormattedTextField();
	
	/**
	 * The seed
	 */
	private JFormattedTextField seed = new JFormattedTextField();
	
	/**
	 * The earnings
	 */
	private JFormattedTextField earnings = new JFormattedTextField();
	
	/**
	 * The tournament number submissions
	 */
	private JFormattedTextField tournamentNumberSubmissions = new JFormattedTextField();
	
	/**
	 * The tournament level 1 average
	 */
	private JFormattedTextField tournamentLevel1Average = new JFormattedTextField();
	
	/**
	 * The tournament level 2 average
	 */
	private JFormattedTextField tournamentLevel2Average = new JFormattedTextField();
	
	/**
	 * The tournament wins
	 */
	private JFormattedTextField tournamentWins = new JFormattedTextField();
	
	/**
	 * The lifetime number submissions
	 */
	private JFormattedTextField lifetimeNumberSubmissions = new JFormattedTextField();
	
	/**
	 * The lifetime level 1 average
	 */
	private JFormattedTextField lifetimeLevel1Average = new JFormattedTextField();
	
	/**
	 * The lifetime level 2 average
	 */
	private JFormattedTextField lifetimeLevel2Average = new JFormattedTextField();
	
	/**
	 * The lifetime wins
	 */
	private JFormattedTextField lifetimeWins = new JFormattedTextField();
	
	
	/**
	 * Creates a Title Property Panel to display the title
	 * 
	 * @param event
	 */
	public AnnounceTCSCoderPropertyPanel(AnnounceTCSCoderEvent event) {
		// Call super constructor
		super(event);
		
		// Setup the numeric field
		roundID.setFocusLostBehavior(JFormattedTextField.COMMIT_OR_REVERT);
		earnings.setFocusLostBehavior(JFormattedTextField.COMMIT_OR_REVERT);
		seed.setFocusLostBehavior(JFormattedTextField.COMMIT_OR_REVERT);
		tcRating.setFocusLostBehavior(JFormattedTextField.COMMIT_OR_REVERT);
		tcsRating.setFocusLostBehavior(JFormattedTextField.COMMIT_OR_REVERT);
		lifetimeLevel1Average.setFocusLostBehavior(JFormattedTextField.COMMIT_OR_REVERT);
		lifetimeLevel2Average.setFocusLostBehavior(JFormattedTextField.COMMIT_OR_REVERT);
		lifetimeNumberSubmissions.setFocusLostBehavior(JFormattedTextField.COMMIT_OR_REVERT);
		lifetimeWins.setFocusLostBehavior(JFormattedTextField.COMMIT_OR_REVERT);
		tournamentLevel1Average.setFocusLostBehavior(JFormattedTextField.COMMIT_OR_REVERT);
		tournamentLevel2Average.setFocusLostBehavior(JFormattedTextField.COMMIT_OR_REVERT);
		tournamentNumberSubmissions.setFocusLostBehavior(JFormattedTextField.COMMIT_OR_REVERT);
		tournamentWins.setFocusLostBehavior(JFormattedTextField.COMMIT_OR_REVERT);
		
		// Setup the number formatter
		NumberFormatter integerFormatter = new NumberFormatter(NumberFormat.getIntegerInstance());
		integerFormatter.setValueClass(Integer.class);
		integerFormatter.setMinimum(new Integer(0));
		
		NumberFormatter currencyFormatter = new NumberFormatter(NumberFormat.getCurrencyInstance());
		currencyFormatter.setValueClass(Double.class);
		currencyFormatter.setMinimum(new Double(0));
		
		NumberFormatter doubleFormatter = new NumberFormatter(NumberFormat.getNumberInstance());
		doubleFormatter.setValueClass(Double.class);
		doubleFormatter.setMinimum(new Double(0));
		
		// Create the factory and set the value
		DefaultFormatterFactory integerFactory = new DefaultFormatterFactory(integerFormatter);
		roundID.setFormatterFactory(integerFactory);
		tournamentNumberSubmissions.setFormatterFactory(integerFactory);
		tournamentWins.setFormatterFactory(integerFactory);
		lifetimeNumberSubmissions.setFormatterFactory(integerFactory);
		lifetimeWins.setFormatterFactory(integerFactory);
		seed.setFormatterFactory(integerFactory);
		tcRating.setFormatterFactory(integerFactory);
		tcsRating.setFormatterFactory(integerFactory);
		
		DefaultFormatterFactory currencyFactory = new DefaultFormatterFactory(currencyFormatter);
		earnings.setFormatterFactory(currencyFactory);
		
		
		DefaultFormatterFactory doubleFactory = new DefaultFormatterFactory(doubleFormatter);
		lifetimeLevel1Average.setFormatterFactory(doubleFactory);
		lifetimeLevel2Average.setFormatterFactory(doubleFactory);
		tournamentLevel1Average.setFormatterFactory(doubleFactory);
		tournamentLevel2Average.setFormatterFactory(doubleFactory);
		
		// Setup the fields
		titleField.setText(event.getTitle());
		roundID.setValue(new Integer(event.getRoundID()));
		coderName.setText(event.getCoderName());
		coderType.setSelectedItem(event.getCoderType());
		coderHandle.setText(event.getHandle());
		earnings.setValue(new Double(event.getEarnings()));
		seed.setValue(new Integer(event.getSeed()));
		tcRating.setValue(new Integer(event.getTcRating()));
		tcsRating.setValue(new Integer(event.getTcsRating()));
		imageFileName.setText(event.getImageFileName());
		lifetimeLevel1Average.setValue(new Double(event.getLifetimeLevel1Average()));
		lifetimeLevel2Average.setValue(new Double(event.getLifetimeLevel2Average()));
		lifetimeNumberSubmissions.setValue(new Integer(event.getLifetimeNumberSubmissions()));
		lifetimeWins.setValue(new Integer(event.getLifetimeWins()));
		tournamentLevel1Average.setValue(new Double(event.getTournamentLevel1Average()));
		tournamentLevel2Average.setValue(new Double(event.getTournamentLevel2Average()));
		tournamentNumberSubmissions.setValue(new Integer(event.getTournamentNumberSubmissions()));
		tournamentWins.setValue(new Integer(event.getTournamentWins()));
		coderSchool.setText(event.getSchool());
		
		
		
		// Setup the panel
		int currentRow = 0;
		this.add(new JLabel("Title: "), new GridBagConstraints(0,currentRow,1,1,0,0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(5,5,5,5), 0, 0));
		this.add(titleField, new GridBagConstraints(1,currentRow,1,1,1,0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(5,5,5,5), 0, 0));
		this.add(new JLabel("Round ID: "), new GridBagConstraints(0,++currentRow,1,1,0,0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(5,5,5,5), 0, 0));
		this.add(roundID, new GridBagConstraints(1,currentRow,1,1,1,0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(5,5,5,5), 0, 0));
		this.add(new JLabel("Coder Handle: "), new GridBagConstraints(0,++currentRow,1,1,0,0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(5,5,5,5), 0, 0));
		this.add(coderHandle, new GridBagConstraints(1,currentRow,1,1,1,0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(5,5,5,5), 0, 0));
		this.add(new JLabel("Coder Name: "), new GridBagConstraints(0,++currentRow,1,1,0,0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(5,5,5,5), 0, 0));
		this.add(coderName, new GridBagConstraints(1,currentRow,1,1,1,0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(5,5,5,5), 0, 0));
                this.add(new JLabel("Coder School: "), new GridBagConstraints(0,++currentRow,1,1,0,0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(5,5,5,5), 0, 0));
		this.add(coderSchool, new GridBagConstraints(1,currentRow,1,1,1,0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(5,5,5,5), 0, 0));
		this.add(new JLabel("Coder Image Filename: "), new GridBagConstraints(0,++currentRow,1,1,0,0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(5,5,5,5), 0, 0));
		this.add(imageFileName, new GridBagConstraints(1,currentRow,1,1,1,0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(5,5,5,5), 0, 0));
		this.add(new BrowseButton(imageFileName), new GridBagConstraints(2,currentRow,1,1,0,0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(5,5,5,5), 0, 0));
		this.add(new JLabel("Coder Type: "), new GridBagConstraints(0,++currentRow,1,1,0,0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(5,5,5,5), 0, 0));
		this.add(coderType, new GridBagConstraints(1,currentRow,1,1,1,0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(5,5,5,5), 0, 0));
		//this.add(new JLabel("Seed: "), new GridBagConstraints(0,++currentRow,1,1,0,0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(5,5,5,5), 0, 0));
		//this.add(seed, new GridBagConstraints(1,currentRow,1,1,1,0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(5,5,5,5), 0, 0));
		this.add(new JLabel("TC Rating: "), new GridBagConstraints(0,++currentRow,1,1,0,0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(5,5,5,5), 0, 0));
		this.add(tcRating, new GridBagConstraints(1,currentRow,1,1,1,0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(5,5,5,5), 0, 0));
		this.add(new JLabel("TCS Rating: "), new GridBagConstraints(0,++currentRow,1,1,0,0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(5,5,5,5), 0, 0));
		this.add(tcsRating, new GridBagConstraints(1,currentRow,1,1,1,0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(5,5,5,5), 0, 0));
		this.add(new JLabel("Earnings: "), new GridBagConstraints(0,++currentRow,1,1,0,0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(5,5,5,5), 0, 0));
		this.add(earnings, new GridBagConstraints(1,currentRow,1,1,1,0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(5,5,5,5), 0, 0));
		this.add(new JLabel("Tournament Submissions: "), new GridBagConstraints(0,++currentRow,1,1,0,0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(5,5,5,5), 0, 0));
		this.add(tournamentNumberSubmissions, new GridBagConstraints(1,currentRow,1,1,1,0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(5,5,5,5), 0, 0));
		this.add(new JLabel("Tournament Level 1 Avg: "), new GridBagConstraints(0,++currentRow,1,1,0,0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(5,5,5,5), 0, 0));
		this.add(tournamentLevel1Average, new GridBagConstraints(1,currentRow,1,1,1,0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(5,5,5,5), 0, 0));
		this.add(new JLabel("Tournament Level 2 Avg: "), new GridBagConstraints(0,++currentRow,1,1,0,0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(5,5,5,5), 0, 0));
		this.add(tournamentLevel2Average, new GridBagConstraints(1,currentRow,1,1,1,0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(5,5,5,5), 0, 0));
		this.add(new JLabel("Tournament Wins: "), new GridBagConstraints(0,++currentRow,1,1,0,0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(5,5,5,5), 0, 0));
		this.add(tournamentWins, new GridBagConstraints(1,currentRow,1,1,1,0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(5,5,5,5), 0, 0));
		this.add(new JLabel("Lifetime Submissions: "), new GridBagConstraints(0,++currentRow,1,1,0,0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(5,5,5,5), 0, 0));
		this.add(lifetimeNumberSubmissions, new GridBagConstraints(1,currentRow,1,1,1,0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(5,5,5,5), 0, 0));
		this.add(new JLabel("Lifetime Level 1 Avg: "), new GridBagConstraints(0,++currentRow,1,1,0,0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(5,5,5,5), 0, 0));
		this.add(lifetimeLevel1Average, new GridBagConstraints(1,currentRow,1,1,1,0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(5,5,5,5), 0, 0));
		this.add(new JLabel("Lifetime Level 2 Avg: "), new GridBagConstraints(0,++currentRow,1,1,0,0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(5,5,5,5), 0, 0));
		this.add(lifetimeLevel2Average, new GridBagConstraints(1,currentRow,1,1,1,0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(5,5,5,5), 0, 0));
		this.add(new JLabel("Lifetime Wins: "), new GridBagConstraints(0,++currentRow,1,1,0,0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(5,5,5,5), 0, 0));
		this.add(lifetimeWins, new GridBagConstraints(1,currentRow,1,1,1,0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(5,5,5,5), 0, 0));
		this.add(new JLabel(), new GridBagConstraints(0,++currentRow,2,1,1,1, GridBagConstraints.NORTHWEST, GridBagConstraints.BOTH, new Insets(5,5,5,5), 0, 0));
	}
	
	/**
	 * Sets the title to the current title displayed
	 */
	public void saveProperties() {
		// Commit if valid
		if (roundID.isEditValid()) try { roundID.commitEdit(); } catch (ParseException e) {}
		if (earnings.isEditValid()) try { earnings.commitEdit(); } catch (ParseException e) {}
		if (lifetimeLevel1Average.isEditValid()) try { lifetimeLevel1Average.commitEdit(); } catch (ParseException e) {}
		if (lifetimeLevel2Average.isEditValid()) try { lifetimeLevel2Average.commitEdit(); } catch (ParseException e) {}
		if (lifetimeWins.isEditValid()) try { lifetimeWins.commitEdit(); } catch (ParseException e) {}
		if (lifetimeNumberSubmissions.isEditValid()) try { lifetimeNumberSubmissions.commitEdit(); } catch (ParseException e) {}
		if (tournamentLevel1Average.isEditValid()) try { tournamentLevel1Average.commitEdit(); } catch (ParseException e) {}
		if (tournamentLevel2Average.isEditValid()) try { tournamentLevel2Average.commitEdit(); } catch (ParseException e) {}
		if (tournamentWins.isEditValid()) try { tournamentWins.commitEdit(); } catch (ParseException e) {}
		if (tournamentNumberSubmissions.isEditValid()) try { tournamentNumberSubmissions.commitEdit(); } catch (ParseException e) {}
		if (seed.isEditValid()) try { seed.commitEdit(); } catch (ParseException e) {}
		if (tcRating.isEditValid()) try { tcRating.commitEdit(); } catch (ParseException e) {}
		if (tcsRating.isEditValid()) try { tcsRating.commitEdit(); } catch (ParseException e) {}
		
		// Convienence cast
		AnnounceTCSCoderEvent event = (AnnounceTCSCoderEvent) getEvent();
		
		// Sets the title back
		event.setTitle(titleField.getText());
		event.setRoundID(((Integer) roundID.getValue()).intValue());
		event.setCoderName(coderName.getText());
                event.setSchool(coderSchool.getText());
		event.setHandle(coderHandle.getText());
		event.setCoderType((String)coderType.getSelectedItem());
		event.setEarnings(((Double)earnings.getValue()).doubleValue());
		event.setSeed(((Integer)seed.getValue()).intValue());
		event.setTcRating(((Integer)tcRating.getValue()).intValue());
		event.setTcsRating(((Integer)tcsRating.getValue()).intValue());
		event.setImageFileName(imageFileName.getText());
		
		event.setLifetimeLevel1Average(((Double)lifetimeLevel1Average.getValue()).doubleValue());
		event.setLifetimeLevel2Average(((Double)lifetimeLevel2Average.getValue()).doubleValue());
		event.setLifetimeNumberSubmissions(((Integer)lifetimeNumberSubmissions.getValue()).intValue());
		event.setLifetimeWins(((Integer)lifetimeWins.getValue()).intValue());
		
		event.setTournamentLevel1Average(((Double)tournamentLevel1Average.getValue()).doubleValue());
		event.setTournamentLevel2Average(((Double)tournamentLevel2Average.getValue()).doubleValue());
		event.setTournamentNumberSubmissions(((Integer)tournamentNumberSubmissions.getValue()).intValue());
		event.setTournamentWins(((Integer)tournamentWins.getValue()).intValue());
		
		
		// Validate event
		try {
			event.validateEvent();
		} catch (Exception e) {
			JOptionPane.showMessageDialog(null, "Error Validating the event: \r\n" + e.toString(), "Error Validating Event", JOptionPane.ERROR_MESSAGE);
		}
	}
}
