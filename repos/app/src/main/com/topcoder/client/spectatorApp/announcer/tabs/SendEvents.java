package com.topcoder.client.spectatorApp.announcer.tabs;

import java.awt.BorderLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;

import javax.swing.DefaultListModel;
import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import com.topcoder.client.spectatorApp.announcer.Announcer;
import com.topcoder.client.spectatorApp.announcer.AnnouncerConfig;
import com.topcoder.client.spectatorApp.announcer.events.AnnouncerEvent;
import com.topcoder.client.spectatorApp.announcer.events.DefineContestEvent;
import com.topcoder.client.spectatorApp.announcer.events.DefineRoundEvent;
import com.topcoder.client.spectatorApp.announcer.properties.PropertyPanel;

/**
 * Panel that will show the preview of the current item
 */
public class SendEvents extends AnnouncerTab {
	
	/** The list model */
	private DefaultListModel model = new DefaultListModel();
	
	/** The list of events */
	private JList list = new JList(model);
	
	/** The define button */
	private JButton define = new JButton("Define");
	
	/** The send button */
	private JButton send = new JButton("Send");
	
	/** The schedule button */
	private JButton schedule = new JButton("Schedule");
	
	/** The split pane */
	private JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
	
	/** The current property panel */
	private PropertyPanel currentPropertyPanel = null;
	
	/** Constructor */
	public SendEvents() {
		// Set the layout
		this.setLayout(new BorderLayout());

		// Add the split pane as the top level panel 
		add(splitPane, BorderLayout.CENTER);
		
		// Create the button panel
		JPanel buttonPanel = new JPanel(new GridBagLayout());
		buttonPanel.add(send, new GridBagConstraints(0,0,1,1,0,0,GridBagConstraints.NORTHWEST, GridBagConstraints.HORIZONTAL, new Insets(5,5,5,5), 0,0));
		buttonPanel.add(schedule, new GridBagConstraints(0,1,1,1,0,0,GridBagConstraints.NORTHWEST, GridBagConstraints.HORIZONTAL, new Insets(5,5,5,5), 0,0));
		buttonPanel.add(define, new GridBagConstraints(0,2,1,1,0,0,GridBagConstraints.NORTHWEST, GridBagConstraints.HORIZONTAL, new Insets(5,5,5,5), 0,0));
		
		// Configure the panel
		JPanel topPanel = new JPanel(new GridBagLayout());
		topPanel.add(new JLabel("Defined Events"), new GridBagConstraints(0,0,1,1,0,0,GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(15,5,0,5), 0,0));
		topPanel.add(new JScrollPane(list), new GridBagConstraints(0,1,1,1,1,1,GridBagConstraints.NORTHWEST, GridBagConstraints.BOTH, new Insets(5,5,5,5), 0,0));
		topPanel.add(buttonPanel, new GridBagConstraints(1,1,1,1,0,0,GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, new Insets(5,5,5,5), 0,0));
		splitPane.setLeftComponent(topPanel);

		
		// Add the listener for a define event
		define.addActionListener(new DefineHandler());
		define.setEnabled(false);
		
		// Add the listener for a send event
		send.addActionListener(new SendHandler());
		send.setEnabled(false);
		
		// Add the listener for a schedule event
		schedule.addActionListener(new ScheduleHandler());
		schedule.setEnabled(false);
		
		// Add a selection handler
		list.addListSelectionListener(new SelectionHandler());
		
	}
		
	/** Reconfigure the events listed */
	public void reConfigure() {
		// Clear the selection
		list.setSelectedIndex(-1);
		
		// Clear the model
		model.clear();
		
		// Set the current property panel to nothing
		setPropertyPanel(null);
		
		// Disable the send button
		send.setEnabled(false);
		
		// Add the events back
		AnnouncerEvent[] events = AnnouncerConfig.getInstance().getEvents();
		boolean contestFound = false;
		boolean roundFound = false;
		for(int x=0;x<events.length;x++) {
			if(events[x] instanceof DefineContestEvent) contestFound = true;
			if(events[x] instanceof DefineRoundEvent) roundFound = true;
			model.addElement(events[x]);
		}
	
		// Enable the define button if both the contest and round was found
		define.setEnabled(contestFound && roundFound);
	}
	
	/** Resync the properties with the event*/
	public void tabUnSelected() {
		if(currentPropertyPanel!=null) currentPropertyPanel.saveProperties();
	}
	
	/** Return the title for the tab */
	public String getTitle() {
		return "Send Events";
	}

	/** Return the icon used for the tab */
	public Icon getIcon() {
		return null;
	}

	/** Return the tab tip */
	public String getTip() {
		return "Events to send";
	}
	
	/**
	 * Sets the property panel and shows it
	 * @param panel the panel to set
	 */
	public void setPropertyPanel(PropertyPanel panel) {
		int pos = splitPane.getDividerLocation();
		// Save the reference
		if(panel==null) {
			currentPropertyPanel = null;
			splitPane.setRightComponent(new JPanel());
		} else {
			currentPropertyPanel = (PropertyPanel)panel;
			splitPane.setRightComponent(new JScrollPane(panel));
		}
		splitPane.setDividerLocation(pos);
	}
	
	/** Handler for selection events */
	class SelectionHandler implements ListSelectionListener {
		public void valueChanged(ListSelectionEvent arg0) {
			// Save the current properties
			if(currentPropertyPanel!=null) currentPropertyPanel.saveProperties();

			// Enable the send button if something is selected
			if(list.getSelectedIndex()>=0) {
				send.setEnabled(true);
				schedule.setEnabled(true);
			}
			
			// Get the property panel and set it
			AnnouncerEvent evt = (AnnouncerEvent)list.getSelectedValue();
			setPropertyPanel(evt==null ? null : evt.getPropertyPanel());
		}
	
	}
	
	/** Handler for the send button events */
	class SendHandler implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			// Save the current properties
			if(currentPropertyPanel!=null) currentPropertyPanel.saveProperties();

			// Get all the selected values
			Object[] evt = list.getSelectedValues();
			if(evt==null || evt.length==0) return;

			// Loop and send each event			
			for(int x=0;x<evt.length;x++) {
				try {
					Object message = ((AnnouncerEvent)evt[x]).getMessage();
					if(message!=null) Announcer.getInstance().getClient().sendMessage(message);
				} catch (IOException io) {
					JOptionPane.showMessageDialog(null, io.toString(), "Error sending the message", JOptionPane.ERROR_MESSAGE);
				}
			}
			
			// Clear the selection
			list.getSelectionModel().clearSelection();
			setPropertyPanel(null);
		}
	}

	/** Handler for the schedule button events */
	class ScheduleHandler implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			// Save the current properties
			if(currentPropertyPanel!=null) currentPropertyPanel.saveProperties();

			// Get all the selected values
			Object[] evt = list.getSelectedValues();
			if(evt==null || evt.length==0) return;

			// Loop and send each event			
			for(int x=0;x<evt.length;x++) {
				getParentTabPane().getScheduleEventsTab().scheduleEvent((AnnouncerEvent)evt[x]);
			}
			
			// Clear the selection
			list.getSelectionModel().clearSelection();
			setPropertyPanel(null);
		}
	}
	
	/** Handler for the schedule button events */
	class DefineHandler implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			// Save the current properties
			if(currentPropertyPanel!=null) currentPropertyPanel.saveProperties();

			DefineContestEvent contest = null;
			DefineRoundEvent round = null;
			
			// Get all the selected values
			for(int x=list.getModel().getSize()-1;x>=0;x--) {
				AnnouncerEvent evt = (AnnouncerEvent)list.getModel().getElementAt(x);
				if(evt instanceof DefineContestEvent) contest = (DefineContestEvent)evt;
				if(evt instanceof DefineRoundEvent) round = (DefineRoundEvent)evt;
			}

			// Did we have the contest event
			if(contest==null) {
				JOptionPane.showMessageDialog(null, "A DefineContest event was not found", "Not Found", JOptionPane.ERROR_MESSAGE);
				return;
			}

			// Did we have the round event
			if(round==null) {
				JOptionPane.showMessageDialog(null, "A DefineRound event was not found", "Not Found", JOptionPane.ERROR_MESSAGE);
				return;
			}

			// Send the contest and round event
			try {
				Object message = contest.getMessage();
				if(message!=null) Announcer.getInstance().getClient().sendMessage(message);
				
				message = round.getMessage();
				if(message!=null) Announcer.getInstance().getClient().sendMessage(message);

			} catch (IOException io) {
				JOptionPane.showMessageDialog(null, io.toString(), "Error sending the message", JOptionPane.ERROR_MESSAGE);
			}
			
		}
	}

}
