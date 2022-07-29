package com.topcoder.client.spectatorApp.announcer.properties;

import java.awt.GridBagConstraints;
import java.awt.Insets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.StringTokenizer;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTextField;
import com.topcoder.client.spectatorApp.CommonRoutines;
import com.topcoder.client.spectatorApp.announcer.events.ShowPlacementEvent;

/**
 * A property panel that will allow setting/getting of the ShowPlacementEvent
 * properties
 * 
 * @author Tim "Pops" Roberts
 * @version 1.0
 */
public class ShowPlacementPropertyPanel extends PropertyPanel<ShowPlacementEvent> {
	/**
	 * The title field
	 */
	private JTextField titleField = new JTextField();

	/**
	 * The round id field
	 */
	private JTextField placementField = new JTextField();

	/**
	 * Creates a Title Property Panel to display the title
	 * 
	 * @param event
	 */
	public ShowPlacementPropertyPanel(ShowPlacementEvent event) {
		// Call super constructor
		super(event);
		
		// Set the title field
		titleField.setText(event.getTitle());

		int[] placements = event.getPlacements();
		Arrays.sort(placements);
		placementField.setText(CommonRoutines.prettyPrint(placements));
		
		// Setup the panel
		int currentRow = 0;
		this.add(new JLabel("Title: "), new GridBagConstraints(0, currentRow, 1, 1, 0, 0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(5, 5, 5, 5), 0, 0));
		this.add(titleField, new GridBagConstraints(1, currentRow, 1, 1, 1, 0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(5, 5, 5, 5), 0, 0));
		this.add(new JLabel("Placements: "), new GridBagConstraints(0, ++currentRow, 1, 1, 0, 0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(5, 5, 5, 5), 0, 0));
		this.add(placementField, new GridBagConstraints(1, currentRow, 1, 1, 1, 0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(5, 5, 5, 5), 0, 0));
		this.add(new JLabel("Example: '1,3-6,8'"), new GridBagConstraints(0, ++currentRow, 2, 1, 1, 0, GridBagConstraints.NORTHWEST, GridBagConstraints.HORIZONTAL, new Insets(5, 5, 5, 5), 0, 0));
		this.add(new JLabel(), new GridBagConstraints(0, ++currentRow, 2, 1, 1, 1, GridBagConstraints.NORTHWEST, GridBagConstraints.BOTH, new Insets(5, 5, 5, 5), 0, 0));
	}

	/**
	 * Sets the title to the current title displayed
	 */
	public void saveProperties() {
		// Sets the title back
		getEvent().setTitle(titleField.getText());

		List<Integer> placements = new ArrayList<Integer>();
		StringTokenizer str = new StringTokenizer(placementField.getText(), ", ");
		
		while(str.hasMoreTokens()) {
			String token = str.nextToken();
			try {
				int idx = token.indexOf('-');
				if (idx < 0) {
					placements.add(new Integer(token));
				} else {
					int left = new Integer(token.substring(0, idx));
					int right = new Integer(token.substring(idx+1));
					for(int x = Math.min(left, right); x<=Math.max(left,right); x++) {
						placements.add(x);
					}
				}
			} catch (NumberFormatException e) {
				JOptionPane.showMessageDialog(null, "'" + token + "' is not a valid number", "Error Validating Event", JOptionPane.ERROR_MESSAGE);
				return;
			}
		}
		
		int[] p = new int[placements.size()];
		int idx = 0;
		for(Integer i : placements) {
			p[idx++] = i;
		}
		getEvent().setPlacements(p);

	}
}
