package com.topcoder.client.spectatorApp.announcer.properties;

import java.awt.GridBagConstraints;
import java.awt.Insets;
import java.text.NumberFormat;
import java.text.ParseException;
import javax.swing.JFormattedTextField;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTextField;
import javax.swing.text.DefaultFormatterFactory;
import javax.swing.text.NumberFormatter;
import com.topcoder.client.spectatorApp.announcer.events.AnnounceCoderEvent;

/**
 * A property panel that will allow setting/getting of the AnnounceTCSCoderEvent
 * properties
 * 
 * @author Tim "Pops" Roberts
 * @version 1.0
 */
public class AnnounceCoderPropertyPanel extends PropertyPanel {
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
	 * The image filename
	 */
	private JTextField imageFileName = new JTextField();

	/**
	 * The coder handle
	 */
	private JTextField coderHandle = new JTextField();

	/**
	 * The TC Rating
	 */
	private JFormattedTextField tcRating = new JFormattedTextField();

	/**
	 * The seed
	 */
	private JFormattedTextField seed = new JFormattedTextField();

	/**
	 * The ranking
	 */
	private JFormattedTextField ranking = new JFormattedTextField();

	/**
	 * The invitational number submissions
	 */
	private JFormattedTextField invitationalNumberSubmissions = new JFormattedTextField();

	/**
	 * The invitational submissions percent
	 */
	private JFormattedTextField invitationalSubmissionPercent = new JFormattedTextField();

	/**
	 * The invitational challenge percent
	 */
	private JFormattedTextField invitationalChallengePercent = new JFormattedTextField();

	/**
	 * The invitational number challenges
	 */
	private JFormattedTextField invitationalNumberChallenges = new JFormattedTextField();

	/**
	 * The invitational number competitions
	 */
	private JFormattedTextField invitationalNumberCompetitions = new JFormattedTextField();

	/**
	 * The SRM competitions
	 */
	private JFormattedTextField srmNumberCompetitions = new JFormattedTextField();

	/**
	 * The SRM submission percent
	 */
	private JFormattedTextField srmSubmissionPercent = new JFormattedTextField();

	/**
	 * The SRM challenge percent
	 */
	private JFormattedTextField srmChallengePercent = new JFormattedTextField();

	/**
	 * The SRM submissions
	 */
	private JFormattedTextField srmNumberSubmissions = new JFormattedTextField();

	/**
	 * The SRM challenges
	 */
	private JFormattedTextField srmNumberChallenges = new JFormattedTextField();

	/**
	 * Creates a Title Property Panel to display the title
	 * 
	 * @param event
	 */
	public AnnounceCoderPropertyPanel(AnnounceCoderEvent event) {
		// Call super constructor
		super(event);
		// Setup the numeric field
		roundID.setFocusLostBehavior(JFormattedTextField.COMMIT_OR_REVERT);
		ranking.setFocusLostBehavior(JFormattedTextField.COMMIT_OR_REVERT);
		seed.setFocusLostBehavior(JFormattedTextField.COMMIT_OR_REVERT);
		tcRating.setFocusLostBehavior(JFormattedTextField.COMMIT_OR_REVERT);
		srmSubmissionPercent.setFocusLostBehavior(JFormattedTextField.COMMIT_OR_REVERT);
		srmChallengePercent.setFocusLostBehavior(JFormattedTextField.COMMIT_OR_REVERT);
		srmNumberCompetitions.setFocusLostBehavior(JFormattedTextField.COMMIT_OR_REVERT);
		srmNumberSubmissions.setFocusLostBehavior(JFormattedTextField.COMMIT_OR_REVERT);
		srmNumberChallenges.setFocusLostBehavior(JFormattedTextField.COMMIT_OR_REVERT);
		invitationalSubmissionPercent.setFocusLostBehavior(JFormattedTextField.COMMIT_OR_REVERT);
		invitationalChallengePercent.setFocusLostBehavior(JFormattedTextField.COMMIT_OR_REVERT);
		invitationalNumberSubmissions.setFocusLostBehavior(JFormattedTextField.COMMIT_OR_REVERT);
		invitationalNumberCompetitions.setFocusLostBehavior(JFormattedTextField.COMMIT_OR_REVERT);
		invitationalNumberChallenges.setFocusLostBehavior(JFormattedTextField.COMMIT_OR_REVERT);
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
		invitationalNumberSubmissions.setFormatterFactory(integerFactory);
		invitationalNumberCompetitions.setFormatterFactory(integerFactory);
		invitationalNumberChallenges.setFormatterFactory(integerFactory);
		srmNumberCompetitions.setFormatterFactory(integerFactory);
		srmNumberSubmissions.setFormatterFactory(integerFactory);
		srmNumberChallenges.setFormatterFactory(integerFactory);
		seed.setFormatterFactory(integerFactory);
		tcRating.setFormatterFactory(integerFactory);
		ranking.setFormatterFactory(integerFactory);
		DefaultFormatterFactory doubleFactory = new DefaultFormatterFactory(doubleFormatter);
		srmSubmissionPercent.setFormatterFactory(doubleFactory);
		srmChallengePercent.setFormatterFactory(doubleFactory);
		invitationalSubmissionPercent.setFormatterFactory(doubleFactory);
		invitationalChallengePercent.setFormatterFactory(doubleFactory);
		// Setup the fields
		titleField.setText(event.getTitle());
		roundID.setValue(new Integer(event.getRoundID()));
		coderName.setText(event.getCoderName());
		coderHandle.setText(event.getHandle());
		ranking.setValue(new Integer(event.getRanking()));
		seed.setValue(new Integer(event.getSeed()));
		tcRating.setValue(new Integer(event.getRating()));
		imageFileName.setText(event.getImageFileName());
		srmSubmissionPercent.setValue(new Double(event.getSRMSubmissionPercent()));
		srmChallengePercent.setValue(new Double(event.getSRMChallengePercent()));
		srmNumberCompetitions.setValue(new Integer(event.getSRMNumberCompetitions()));
		srmNumberSubmissions.setValue(new Integer(event.getSRMNumberSubmissions()));
		srmNumberChallenges.setValue(new Integer(event.getSRMNumberChallenges()));
		invitationalSubmissionPercent.setValue(new Double(event.getInvitationalSubmissionPercent()));
		invitationalChallengePercent.setValue(new Double(event.getInvitationalChallengePercent()));
		invitationalNumberSubmissions.setValue(new Integer(event.getInvitationalNumberSubmissions()));
		invitationalNumberCompetitions.setValue(new Integer(event.getInvitationalNumberCompetitions()));
		invitationalNumberChallenges.setValue(new Integer(event.getInvitationalNumberChallenges()));
		// Setup the panel
		int currentRow = 0;
		this.add(new JLabel("Title: "), new GridBagConstraints(0, currentRow, 1, 1, 0, 0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(5, 5, 5, 5), 0, 0));
		this.add(titleField, new GridBagConstraints(1, currentRow, 1, 1, 1, 0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(5, 5, 5, 5), 0, 0));
		this.add(new JLabel("Round ID: "), new GridBagConstraints(0, ++currentRow, 1, 1, 0, 0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(5, 5, 5, 5), 0, 0));
		this.add(roundID, new GridBagConstraints(1, currentRow, 1, 1, 1, 0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(5, 5, 5, 5), 0, 0));
		this.add(new JLabel("Coder Handle: "), new GridBagConstraints(0, ++currentRow, 1, 1, 0, 0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(5, 5, 5, 5), 0, 0));
		this.add(coderHandle, new GridBagConstraints(1, currentRow, 1, 1, 1, 0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(5, 5, 5, 5), 0, 0));
		this.add(new JLabel("Coder Name: "), new GridBagConstraints(0, ++currentRow, 1, 1, 0, 0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(5, 5, 5, 5), 0, 0));
		this.add(coderName, new GridBagConstraints(1, currentRow, 1, 1, 1, 0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(5, 5, 5, 5), 0, 0));
		this.add(new JLabel("Coder Image Filename: "), new GridBagConstraints(0, ++currentRow, 1, 1, 0, 0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(5, 5, 5, 5), 0, 0));
		this.add(imageFileName, new GridBagConstraints(1, currentRow, 1, 1, 1, 0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(5, 5, 5, 5), 0, 0));
		this.add(new BrowseButton(imageFileName), new GridBagConstraints(2, currentRow, 1, 1, 0, 0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(5, 5, 5, 5), 0, 0));
		this.add(new JLabel("Seed: "), new GridBagConstraints(0, ++currentRow, 1, 1, 0, 0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(5, 5, 5, 5), 0, 0));
		this.add(seed, new GridBagConstraints(1, currentRow, 1, 1, 1, 0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(5, 5, 5, 5), 0, 0));
		this.add(new JLabel("TC Rating: "), new GridBagConstraints(0, ++currentRow, 1, 1, 0, 0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(5, 5, 5, 5), 0, 0));
		this.add(tcRating, new GridBagConstraints(1, currentRow, 1, 1, 1, 0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(5, 5, 5, 5), 0, 0));
		this.add(new JLabel("Ranking: "), new GridBagConstraints(0, ++currentRow, 1, 1, 0, 0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(5, 5, 5, 5), 0, 0));
		this.add(ranking, new GridBagConstraints(1, currentRow, 1, 1, 1, 0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(5, 5, 5, 5), 0, 0));
		this.add(new JLabel("Invitational Submissions: "), new GridBagConstraints(0, ++currentRow, 1, 1, 0, 0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(5, 5, 5, 5), 0, 0));
		this.add(invitationalNumberSubmissions, new GridBagConstraints(1, currentRow, 1, 1, 1, 0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(5, 5, 5, 5), 0, 0));
		this.add(new JLabel("Invitational Submission %: "), new GridBagConstraints(0, ++currentRow, 1, 1, 0, 0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(5, 5, 5, 5), 0, 0));
		this.add(invitationalSubmissionPercent, new GridBagConstraints(1, currentRow, 1, 1, 1, 0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(5, 5, 5, 5), 0, 0));
		this.add(new JLabel("Invitational Challenges: "), new GridBagConstraints(0, ++currentRow, 1, 1, 0, 0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(5, 5, 5, 5), 0, 0));
		this.add(invitationalNumberChallenges, new GridBagConstraints(1, currentRow, 1, 1, 1, 0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(5, 5, 5, 5), 0, 0));
		this.add(new JLabel("Invitational Challenge %: "), new GridBagConstraints(0, ++currentRow, 1, 1, 0, 0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(5, 5, 5, 5), 0, 0));
		this.add(invitationalChallengePercent, new GridBagConstraints(1, currentRow, 1, 1, 1, 0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(5, 5, 5, 5), 0, 0));
		this.add(new JLabel("Invitational Competitions: "), new GridBagConstraints(0, ++currentRow, 1, 1, 0, 0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(5, 5, 5, 5), 0, 0));
		this.add(invitationalNumberCompetitions, new GridBagConstraints(1, currentRow, 1, 1, 1, 0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(5, 5, 5, 5), 0, 0));
		this.add(new JLabel("SRM Submissions: "), new GridBagConstraints(0, ++currentRow, 1, 1, 0, 0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(5, 5, 5, 5), 0, 0));
		this.add(srmNumberSubmissions, new GridBagConstraints(1, currentRow, 1, 1, 1, 0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(5, 5, 5, 5), 0, 0));
		this.add(new JLabel("SRM Submission %: "), new GridBagConstraints(0, ++currentRow, 1, 1, 0, 0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(5, 5, 5, 5), 0, 0));
		this.add(srmSubmissionPercent, new GridBagConstraints(1, currentRow, 1, 1, 1, 0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(5, 5, 5, 5), 0, 0));
		this.add(new JLabel("SRM Challenges: "), new GridBagConstraints(0, ++currentRow, 1, 1, 0, 0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(5, 5, 5, 5), 0, 0));
		this.add(srmNumberChallenges, new GridBagConstraints(1, currentRow, 1, 1, 1, 0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(5, 5, 5, 5), 0, 0));
		this.add(new JLabel("SRM Challenge %: "), new GridBagConstraints(0, ++currentRow, 1, 1, 0, 0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(5, 5, 5, 5), 0, 0));
		this.add(srmChallengePercent, new GridBagConstraints(1, currentRow, 1, 1, 1, 0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(5, 5, 5, 5), 0, 0));
		this.add(new JLabel("SRM Competitions: "), new GridBagConstraints(0, ++currentRow, 1, 1, 0, 0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(5, 5, 5, 5), 0, 0));
		this.add(srmNumberCompetitions, new GridBagConstraints(1, currentRow, 1, 1, 1, 0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(5, 5, 5, 5), 0, 0));
		this.add(new JLabel(), new GridBagConstraints(0, ++currentRow, 2, 1, 1, 1, GridBagConstraints.NORTHWEST, GridBagConstraints.BOTH, new Insets(5, 5, 5, 5), 0, 0));
	}

	/**
	 * Sets the title to the current title displayed
	 */
	public void saveProperties() {
		// Commit if valid
		if (roundID.isEditValid()) try {
			roundID.commitEdit();
		} catch (ParseException e) {
		}
		if (ranking.isEditValid()) try {
			ranking.commitEdit();
		} catch (ParseException e) {
		}
		if (srmSubmissionPercent.isEditValid()) try {
			srmSubmissionPercent.commitEdit();
		} catch (ParseException e) {
		}
		if (srmChallengePercent.isEditValid()) try {
			srmChallengePercent.commitEdit();
		} catch (ParseException e) {
		}
		if (srmNumberSubmissions.isEditValid()) try {
			srmNumberSubmissions.commitEdit();
		} catch (ParseException e) {
		}
		if (srmNumberCompetitions.isEditValid()) try {
			srmNumberCompetitions.commitEdit();
		} catch (ParseException e) {
		}
		if (srmNumberChallenges.isEditValid()) try {
			srmNumberChallenges.commitEdit();
		} catch (ParseException e) {
		}
		if (invitationalSubmissionPercent.isEditValid()) try {
			invitationalSubmissionPercent.commitEdit();
		} catch (ParseException e) {
		}
		if (invitationalChallengePercent.isEditValid()) try {
			invitationalChallengePercent.commitEdit();
		} catch (ParseException e) {
		}
		if (invitationalNumberCompetitions.isEditValid()) try {
			invitationalNumberCompetitions.commitEdit();
		} catch (ParseException e) {
		}
		if (invitationalNumberSubmissions.isEditValid()) try {
			invitationalNumberSubmissions.commitEdit();
		} catch (ParseException e) {
		}
		if (invitationalNumberChallenges.isEditValid()) try {
			invitationalNumberChallenges.commitEdit();
		} catch (ParseException e) {
		}
		if (seed.isEditValid()) try {
			seed.commitEdit();
		} catch (ParseException e) {
		}
		if (tcRating.isEditValid()) try {
			tcRating.commitEdit();
		} catch (ParseException e) {
		}
		// Convienence cast
		AnnounceCoderEvent event = (AnnounceCoderEvent) getEvent();
		// Sets the title back
		event.setTitle(titleField.getText());
		event.setRoundID(((Integer) roundID.getValue()).intValue());
		event.setCoderName(coderName.getText());
		event.setHandle(coderHandle.getText());
		event.setRanking(((Integer) ranking.getValue()).intValue());
		event.setSeed(((Integer) seed.getValue()).intValue());
		event.setRating(((Integer) tcRating.getValue()).intValue());
		event.setImageFileName(imageFileName.getText());
		event.setInvitationalSubmissionPercent(((Double) invitationalSubmissionPercent.getValue()).doubleValue());
		event.setInvitationalChallengePercent(((Double) invitationalChallengePercent.getValue()).doubleValue());
		event.setInvitationalNumberCompetitions(((Integer) invitationalNumberCompetitions.getValue()).intValue());
		event.setInvitationalNumberSubmissions(((Integer) invitationalNumberSubmissions.getValue()).intValue());
		event.setInvitationalNumberChallenges(((Integer) invitationalNumberChallenges.getValue()).intValue());
		event.setSrmSubmissionPercent(((Double) srmSubmissionPercent.getValue()).doubleValue());
		event.setSrmChallengePercent(((Double) srmChallengePercent.getValue()).doubleValue());
		event.setSrmNumberCompetitions(((Integer) srmNumberCompetitions.getValue()).intValue());
		event.setSrmNumberSubmissions(((Integer) srmNumberSubmissions.getValue()).intValue());
		event.setSrmNumberChallenges(((Integer) srmNumberChallenges.getValue()).intValue());
		// Validate event
		try {
			event.validateEvent();
		} catch (Exception e) {
			JOptionPane.showMessageDialog(null, "Error Validating the event: \r\n" + e.toString(), "Error Validating Event", JOptionPane.ERROR_MESSAGE);
		}
	}
}
