/**
 * TCSBioRenderer.java
 *
 * Description:		The Renderer for the TCS bios...
 * @author			Tim "Pops" Roberts
 * @version			1.0
 */

package com.topcoder.client.spectatorApp.tcs;

import java.awt.Color;
import java.awt.Font;
import java.awt.Insets;

import org.apache.log4j.Category;

import com.topcoder.client.spectatorApp.FontFactory;
import com.topcoder.client.spectatorApp.bio.NameRenderer;
import com.topcoder.client.spectatorApp.event.AnnounceTCSCoderEvent;
import com.topcoder.client.spectatorApp.widgets.SGridCell;
import com.topcoder.client.spectatorApp.widgets.SGridLayout;
import com.topcoder.client.spectatorApp.widgets.SGridSizingPolicy;
import com.topcoder.client.spectatorApp.widgets.SImage;
import com.topcoder.client.spectatorApp.widgets.SLabelledImage;
import com.topcoder.client.spectatorApp.widgets.SMargin;
import com.topcoder.client.spectatorApp.widgets.SPage;
import com.topcoder.client.spectatorApp.widgets.STextField;


public class TCSBioRenderer extends SPage {

    /** Reference to the logging category */
    private static final Category cat = Category.getInstance(TCSBioRenderer.class.getName());

    /** Font used */
    private static Font titleFont = FontFactory.getInstance().getFont(FontFactory.FUTURA_BOLD, Font.PLAIN, 21);

    /** The string holding the coder stats title */
    private static String coderStatsTitle = "Lifetime Stats:";

    /** The string holding the tournament stats title */
    private static String tournamentStatsTitle = "Tournament Stats:";

    /** The title color */
    private static Color titleColor = Color.white;

    /** The color background to the coder stats */
    private static Color coderColor = new Color(153, 0, 0);

    /** The color background to the collegiate stats */
    private static Color tournamentColor = new Color(51, 51, 51);

    /** Constructs the trailer panel */
    public TCSBioRenderer(AnnounceTCSCoderEvent evt, String roundName, String contestName) {
		// Create the page
		super(new HeaderRenderer(roundName),
			  new StatsPanel(evt, roundName, contestName),
			  new TrailerRenderer(contestName));

    }

	/** The stats panel */
	static class StatsPanel extends SGridLayout {
		public StatsPanel(AnnounceTCSCoderEvent evt, String roundName, String contestName) {
			// Create the renderers
			HandleRenderer handleRenderer = new HandleRenderer(evt.getHandle(), evt.getTcRating(), evt.getTcsRating(), evt.getSeed(), evt.getEarnings());
			SLabelledImage labelledImage = new SLabelledImage(new SImage(evt.getImage()), handleRenderer, (int)((handleRenderer.getHeight() / 2.0) - (handleRenderer.getHeight()*.33)));
			
			NameRenderer nameRenderer = new NameRenderer(evt.getCoderName(), evt.getSchool());
			StatsRenderer coderStatsRenderer = new StatsRenderer(coderColor, evt.getLifetimeNumberSubmissions(), evt.getLifetimeLevel1Average(), evt.getLifetimeLevel2Average(), evt.getLifetimeWins(), 750);
			StatsRenderer tournamentStatsRenderer = new StatsRenderer(tournamentColor, evt.getTournamentNumberSubmissions(), evt.getTournamentLevel1Average(), evt.getTournamentLevel2Average(), evt.getTournamentWins(), 250);
			
			STextField tournamentStatsTitlePanel = new STextField(tournamentStatsTitle, titleColor, titleFont);
			STextField coderStatsTitlePanel = new STextField(coderStatsTitle, titleColor, titleFont);

			SGridLayout statsLayout = new SGridLayout(new SGridCell[][] 
				{
				  {new SGridCell(new SMargin(tournamentStatsTitlePanel, new Insets(20,0,0,0)), SGridCell.WEST, SGridCell.NONE) },
				  {new SGridCell(tournamentStatsRenderer, SGridCell.WEST, SGridCell.NONE) },
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
