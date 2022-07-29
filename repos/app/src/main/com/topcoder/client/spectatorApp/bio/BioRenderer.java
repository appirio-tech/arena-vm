/**
 * BioRenderer.java
 *
 * Description:		The panel that displays the bio statistics for a TC coder
 * @author			Tim "Pops" Roberts
 * @version			1.0
 */

package com.topcoder.client.spectatorApp.bio;

import java.awt.Color;
import java.awt.Font;
import java.awt.Image;
import java.awt.Insets;

import org.apache.log4j.Category;

import com.topcoder.client.spectatorApp.FontFactory;
import com.topcoder.client.spectatorApp.event.AnnounceCoderEvent;
import com.topcoder.client.spectatorApp.widgets.SGridCell;
import com.topcoder.client.spectatorApp.widgets.SGridLayout;
import com.topcoder.client.spectatorApp.widgets.SGridSizingPolicy;
import com.topcoder.client.spectatorApp.widgets.SImage;
import com.topcoder.client.spectatorApp.widgets.SLabelledImage;
import com.topcoder.client.spectatorApp.widgets.SMargin;
import com.topcoder.client.spectatorApp.widgets.SPage;
import com.topcoder.client.spectatorApp.widgets.STextField;

public class BioRenderer extends SPage {

    /** Reference to the logging category */
    private static final Category cat = Category.getInstance(BioRenderer.class.getName());

    /** Font used */
    private Font titleFont = FontFactory.getInstance().getFont(FontFactory.FUTURA_BOLD, Font.PLAIN, 21);

    /** The page layer */
    private static int PAGE_LAYER=0;
    
    /** The stats layer */
	private static int STATS_LAYER=2;

    /** Preferred space around the name */
    private Insets nameMargin = new Insets(0, 20, 0, 15);

    /** Preferred space between stats */
    private Insets statsMargin = new Insets(10, 50, 10, 50);

    /** The string holding the coder stats title */
    private String coderStatsTitle = "Lifetime Stats:";

    /** The string holding the collegiate stats title */
    private String invitationalStatsTitle = "Tournament Stats:";

    /** The color background to the coder stats */
    private Color coderColor = new Color(153, 0, 0);

    /** The color background to the collegiate stats */
    private Color invitationalColor = new Color(51, 51, 51);

    /** Constructs the trailer panel */
    public BioRenderer(AnnounceCoderEvent evt, String roundName, String contestName, Image logoSmall, Image sponsorLogo) {
		// Create the header and trailer 
		super(new HeaderRenderer(roundName, logoSmall),
			  new TrailerRenderer(contestName, sponsorLogo));

		
		// Set the body panel
		setBodyPanel(new StatsPanel(evt));

    }

    /** The stats panel */
	class StatsPanel extends SGridLayout {
		public StatsPanel(AnnounceCoderEvent evt) {
			// Create the labelled image
			HandleRenderer handleRenderer = new HandleRenderer(evt.getCoderHandle(), evt.getCoderRating(), evt.getCoderSeed(), evt.getCoderRanking());
			SLabelledImage labelledImage = new SLabelledImage(new SImage(evt.getCoderImage()), handleRenderer);
			
			NameRenderer nameRenderer = new NameRenderer(evt.getCoderName(), evt.getCoderType());
			StatsRenderer coderStatsRenderer = new StatsRenderer(coderColor, evt.getCoderCompetitions(), evt.getCoderSubmissions(), evt.getCoderSubmissionPrct(), evt.getCoderChallenges(), evt.getCoderChallengePrct(), 1500);
			StatsRenderer invitationalStatsRenderer = new StatsRenderer(invitationalColor, evt.getInvitationalCompetitions(), evt.getInvitationalSubmissions(), evt.getInvitationalSubmissionPrct(), evt.getInvitationalChallenges(), evt.getInvitationalChallengePrct(), 1250);
			STextField invitationalStatsTitlePanel = new STextField(invitationalStatsTitle, Color.white, titleFont);
			STextField coderStatsTitlePanel = new STextField(coderStatsTitle, Color.white, titleFont);

			SGridLayout statsLayout = new SGridLayout(new SGridCell[][] 
				{
				  {new SGridCell(new SMargin(invitationalStatsTitlePanel, new Insets(20,0,0,0)), SGridCell.WEST, SGridCell.NONE) },
				  {new SGridCell(invitationalStatsRenderer, SGridCell.WEST, SGridCell.NONE) },
				  {new SGridCell(new SMargin(coderStatsTitlePanel, new Insets(20,0,0,0)), SGridCell.WEST, SGridCell.NONE) },
				  {new SGridCell(coderStatsRenderer, SGridCell.WEST, SGridCell.NONE) },
				}
			);
			
			SGridLayout leftLayout = new SGridLayout();
			leftLayout.setGrid(new SGridCell[][] 
							{
							  {new SGridCell(nameRenderer, SGridCell.SOUTH, SGridCell.HORIZONTAL) },
							  {new SGridCell(new SMargin(statsLayout, new Insets(0,20,0,20)), SGridCell.NORTH, SGridCell.NONE) },
							}
			);

			this.setRowSizingPolicy(SGridSizingPolicy.EDGE_SIZING);
			this.setColumnSizingPolicy(SGridSizingPolicy.EDGE_SIZING);
			this.setGrid(new SGridCell[][] {
					{ 
					  new SGridCell(labelledImage, SGridCell.EAST, SGridCell.NONE),
					  new SGridCell(new SMargin(leftLayout, new Insets(0,20,0,0)), SGridCell.WEST, SGridCell.NONE)
					}
			});
		}		
		
	}

}
