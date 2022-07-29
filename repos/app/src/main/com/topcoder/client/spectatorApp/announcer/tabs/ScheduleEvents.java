package com.topcoder.client.spectatorApp.announcer.tabs;

import java.awt.Component;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.util.EventObject;
import javax.swing.AbstractCellEditor;
import javax.swing.DefaultCellEditor;
import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.SpinnerNumberModel;
import javax.swing.Timer;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellEditor;
import com.topcoder.client.spectatorApp.announcer.Announcer;
import com.topcoder.client.spectatorApp.announcer.AnnouncerConfig;
import com.topcoder.client.spectatorApp.announcer.events.AnnouncerEvent;

/**
 * Panel that can be schedule events to be sent to the spectator
 */
public class ScheduleEvents extends AnnouncerTab {
	/** The table model */
	public DefaultTableModel model = new DefaultTableModel();

	/** The table of events */
	private JTable table = new JTable(model);

	/** The check box to determine whether to rotate the events or not */
	private JCheckBox rotateEvents = new JCheckBox("Rotate Events", false);

	/** The timer */
	private Timer timer = new Timer(1000, new RotateListener());

	/** The add button */
	private JButton add = new JButton("Add");

	/** The remove button */
	private JButton remove = new JButton("Remove");

	/** The up button */
	private JButton up = new JButton("Up");

	/** The down button */
	private JButton down = new JButton("Down");

	/** The start button */
	private JButton start = new JButton("Start");

	/** The stop button */
	private JButton stop = new JButton("Stop");

	/** The Resume button */
	private JButton resume = new JButton("Resume");

	/** The next index to show in the rotation */
	private int rotateIndex = 0;

	/** Constructor */
	public ScheduleEvents() {
		// Setup the timer
		timer.stop();
		timer.setRepeats(true); // Doesn't really repeat but needed to enable
										// state checking
		// Setup the model
		model.addColumn("Event");
		model.addColumn("Seconds before NEXT event");
		// Setup the table
		table.getColumnModel().getColumn(0).setCellEditor(new DefaultCellEditor(new JTextField()) {
			public boolean isCellEditable() {
				return false;
			}
		});
		table.getColumnModel().getColumn(1).setCellEditor(new SpinnerEditor());
		table.setRowHeight(table.getRowHeight() + 5);
		// Set the layout
		this.setLayout(new GridBagLayout());
		// Create the button panel
		JPanel buttonPanel = new JPanel(new GridBagLayout());
		buttonPanel.add(add, new GridBagConstraints(0, 0, 1, 1, 1, 1, GridBagConstraints.NORTHEAST, GridBagConstraints.NONE, new Insets(5, 5, 5, 5), 0, 0));
		buttonPanel.add(remove, new GridBagConstraints(1, 0, 1, 1, 0, 0, GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, new Insets(5, 0, 5, 5), 0, 0));
		buttonPanel.add(up, new GridBagConstraints(2, 0, 1, 1, 1, 1, GridBagConstraints.NORTHEAST, GridBagConstraints.NONE, new Insets(5, 25, 5, 5), 0, 0));
		buttonPanel.add(down, new GridBagConstraints(3, 0, 1, 1, 0, 0, GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, new Insets(5, 0, 5, 5), 0, 0));
		buttonPanel.add(start, new GridBagConstraints(4, 0, 1, 1, 1, 1, GridBagConstraints.NORTHEAST, GridBagConstraints.NONE, new Insets(5, 25, 5, 5), 0, 0));
		buttonPanel.add(stop, new GridBagConstraints(5, 0, 1, 1, 0, 0, GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, new Insets(5, 5, 5, 5), 0, 0));
		buttonPanel.add(resume, new GridBagConstraints(6, 0, 1, 1, 1, 1, GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, new Insets(5, 0, 5, 5), 0, 0));
		// Add Action listeners
		add.addActionListener(new AddAction());
		remove.addActionListener(new RemoveAction());
		up.addActionListener(new UpAction());
		down.addActionListener(new DownAction());
		start.addActionListener(new StartAction());
		stop.addActionListener(new StopAction());
		resume.addActionListener(new ResumeAction());
		table.getSelectionModel().addListSelectionListener(new SelectionHandler());
		// Set the button state
		resume.setEnabled(false);
		setState();
		// Configure the panel
		add(new JLabel("Events to Rotate"), new GridBagConstraints(0, 0, 1, 1, 0, 0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(15, 5, 0, 5), 0, 0));
		add(new JScrollPane(table), new GridBagConstraints(0, 1, 1, 1, 1, 1, GridBagConstraints.NORTHWEST, GridBagConstraints.BOTH, new Insets(5, 5, 5, 5), 0, 0));
		add(rotateEvents, new GridBagConstraints(0, 2, 1, 1, 0, 0, GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, new Insets(5, 5, 5, 5), 0, 0));
		add(buttonPanel, new GridBagConstraints(0, 3, 1, 1, 0, 0, GridBagConstraints.NORTHWEST, GridBagConstraints.HORIZONTAL, new Insets(35, 5, 5, 5), 0, 0));
	}

	// Set the states
	private void setState() {
		// If the timer is running, disable everything (except start)
		// and return
		if (timer.isRunning() || resume.isEnabled()) {
			add.setEnabled(false);
			remove.setEnabled(false);
			up.setEnabled(false);
			down.setEnabled(false);
			start.setEnabled(false);
			stop.setEnabled(true);
			if (timer.isRunning()) resume.setEnabled(false);
			rotateEvents.setEnabled(false);
			return;
		}
		// Enable the add button
		add.setEnabled(true);
		// Enable the rotate events
		rotateEvents.setEnabled(true);
		// Stop/Resume is disabled
		stop.setEnabled(false);
		resume.setEnabled(false);
		// Determine if the remove/up/down is enabled
		remove.setEnabled(table.getSelectedRow() >= 0);
		up.setEnabled(table.getSelectedRow() >= 1);
		down.setEnabled(table.getSelectedRow() >= 0 && table.getSelectedRow() < model.getRowCount() - 1);
		// Allow the start if there is items
		start.setEnabled(model.getRowCount() > 0);
	}

	/** Drop the model */
	public void reConfigure() {
		// Reset the selected index
		table.clearSelection();
		// Stop anything running...
		stop.doClick();
		// Reset the state
		setState();
	}
	
	public void reConfigureAndClear() {
		// Clear the model
		while (model.getRowCount() > 0)
			model.removeRow(0);
		this.reConfigure();
	}

	/** App is exiting */
	public void exitApp() {
		stop.doClick();
	}

	/** Return the title for the tab */
	public String getTitle() {
		return "Schedule Events";
	}

	/** Return the icon used for the tab */
	public Icon getIcon() {
		return null;
	}

	/** Return the tab tip */
	public String getTip() {
		return "Events to Schedule";
	}

	/** Add an event to the schedule */
	public void scheduleEvent(AnnouncerEvent evt) {
		model.addRow(new Object[] { 
				evt, 
				Integer.getInteger("com.topcoder.client.spectatorApp.announcer.tabs.ScheduleEvents.seconds", 3) 
				});
	}
	
	public void scheduleEvent(ScheduledEvent sev){
		model.addRow(new Object[] {
				sev.getContEvent(),
				Integer.getInteger("com.topcoder.client.spectatorApp.announcer.tabs.ScheduleEvents.seconds", sev.getTime()) 
				});
	}

	/** Add action */
	class AddAction implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			// Get the events
			AnnouncerEvent[] events = AnnouncerConfig.getInstance().getEvents();
			// Combine them with the 'forced' events
			AnnouncerEvent[] allEvents = new AnnouncerEvent[events.length + 1];
			allEvents[0] = new PauseEvent();
			System.arraycopy(events, 0, allEvents, 1, events.length);
			// If none defined...
			if (allEvents.length == 0) {
				JOptionPane.showMessageDialog(null, "No events are defined", "Error adding event", JOptionPane.ERROR_MESSAGE);
				return;
			}
			// Ask to show an event
			AnnouncerEvent evt = (AnnouncerEvent) JOptionPane.showInputDialog(Announcer.getInstance(), "Choose the event to add", "Add Event", JOptionPane.PLAIN_MESSAGE, null, allEvents, allEvents[0]);
			// Was it canceled
			if (evt == null) return;
			// Add the element
			scheduleEvent(evt);
			table.getSelectionModel().setSelectionInterval(model.getRowCount() - 1, model.getRowCount() - 1);
			// Reset the state
			setState();
		}
	}

	/** Remove action */
	class RemoveAction implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			// If running - ignore
			if (timer.isRunning()) return;
			// Get the item to delete
			int idx = table.getSelectedRow();
			if (idx >= 0) {
				// Remove the item and selected index
				model.removeRow(idx);
				table.clearSelection();
			}
			// Reset the state
			setState();
		}
	}

	/** Up action */
	class UpAction implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			// Make sure the timer isnt running
			if (timer.isRunning()) return;
			// Get the index
			int idx = table.getSelectedRow();
			if (idx < 1 || idx >= model.getRowCount()) return;
			// Swap the elements
			model.moveRow(idx, idx, idx - 1);
			// Set the selection index
			table.getSelectionModel().setSelectionInterval(idx - 1, idx - 1);
		}
	}

	/** Down action */
	class DownAction implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			// Make sure the timer isnt running
			if (timer.isRunning()) return;
			// Get the index
			int idx = table.getSelectedRow();
			if (idx < 0 || idx >= model.getRowCount() - 1) return;
			// Swap the elements
			model.moveRow(idx, idx, idx + 1);
			// Set the selection index
			table.getSelectionModel().setSelectionInterval(idx + 1, idx + 1);
		}
	}

	/** Start action */
	class StartAction implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			// Start the timer
			if (timer.isRunning() || model.getRowCount() == 0) return;
			if (table.getEditingColumn() >= 0 && table.getEditingRow() >= 0) {
				table.getCellEditor(table.getEditingRow(), table.getEditingColumn()).stopCellEditing();
			}
			// Setup and start the timer
			rotateIndex = 0;
			timer.setInitialDelay(0);
			timer.start();
			// Set the state
			setState();
		}
	}

	/** Stop action */
	class StopAction implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			// Stop the timer
			timer.stop();
			rotateIndex = 0;
			// Turn off resume if it's enabled
			resume.setEnabled(false);
			// Set the state
			setState();
		}
	}

	/** Resume action */
	class ResumeAction implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			// Resumes the timer
			if (timer.isRunning() || model.getRowCount() == 0) return;
			if (table.getEditingColumn() >= 0 && table.getEditingRow() >= 0) {
				table.getCellEditor(table.getEditingRow(), table.getEditingColumn()).stopCellEditing();
			}
			// Setup and start the timer
			rotateIndex++;
			timer.setInitialDelay(0);
			timer.start();
			// Set the state
			setState();
		}
	}

	/** The timer task */
	class RotateListener implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			// Sanity check on the model
			if (model.getRowCount() == 0) {
				stop.doClick();
				return;
			}
			// If the index is greater than the model size, reset
			if (rotateIndex >= model.getRowCount()) {
				// If we don't rotate, end now
				if (!rotateEvents.isSelected()) {
					stop.doClick();
					return;
				}
				rotateIndex = 0;
			}
			// Select the event (to show what event it's sending)
			table.getSelectionModel().setSelectionInterval(rotateIndex, rotateIndex);
			// Gets the event from the model
			AnnouncerEvent evt = (AnnouncerEvent) model.getValueAt(rotateIndex, 0);
			// Is it our pause?
			if (evt instanceof PauseEvent) {
				timer.stop();
				resume.setEnabled(true);
				setState();
				return;
			}
			try {
				// Send the event
				Announcer.getInstance().getClient().sendMessage(evt.getMessage());
				// Get the milliseconds to delay to the next event and restart timer
				int milliSeconds = ((Integer) model.getValueAt(rotateIndex, 1)).intValue() * 1000;
				timer.setInitialDelay(milliSeconds);
				timer.restart();
				// Up to the next event
				rotateIndex++;
			} catch (IOException e1) {
				e1.printStackTrace();
			}
		}
	}

	/** Selection handler to reset state when the list selection changes */
	class SelectionHandler implements ListSelectionListener {
		public void valueChanged(ListSelectionEvent arg0) {
			// Reset the state
			setState();
		}
	}

	/** Editor for the spinner control */
	class SpinnerEditor extends AbstractCellEditor implements TableCellEditor {
		final JSpinner spinner = new JSpinner(new SpinnerNumberModel(1, 0, 60, 1));

		// Initializes the spinner.
		public SpinnerEditor() {}

		// Prepares the spinner component and returns it.
		public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
			spinner.setValue(value);
			return spinner;
		}

		// Enables the editor only for double-clicks.
		public boolean isCellEditable(EventObject evt) {
			return true;
		}

		// Returns the spinners current value.
		public Object getCellEditorValue() {
			return spinner.getValue();
		}
	}

	/** A pause event */
	class PauseEvent extends AnnouncerEvent {
		/** Construct with our title */
		public PauseEvent() {
			setTitle("Pause");
		}

		/** Not really an event that sends a message */
		public Object getMessage() {
			return null;
		}

		/** Nothing to validate */
		public void validateEvent() throws Exception {}
	}
}
