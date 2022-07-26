/**
 * TCSWinners.java
 *
 * Description:		The panel that displays the winners of a tcs event
 * @author			Tim "Pops" Roberts
 * @version			1.0
 */

package com.topcoder.client.spectatorApp.tcs;

import java.awt.Color;
import java.awt.Font;
import java.awt.Insets;

import com.topcoder.client.spectatorApp.FontFactory;
import com.topcoder.client.spectatorApp.event.AnnounceTCSWinnersEvent;
import com.topcoder.client.spectatorApp.widgets.SBox;
import com.topcoder.client.spectatorApp.widgets.SGridCell;
import com.topcoder.client.spectatorApp.widgets.SGridLayout;
import com.topcoder.client.spectatorApp.widgets.SImage;
import com.topcoder.client.spectatorApp.widgets.SLabelledImage;
import com.topcoder.client.spectatorApp.widgets.SMargin;
import com.topcoder.client.spectatorApp.widgets.SPage;
import com.topcoder.client.spectatorApp.widgets.STextField;
import com.topcoder.client.spectatorApp.Constants;


public class TCSWinners extends SPage {
    /** Font used */
    private static Font titleFont = FontFactory.getInstance().getFont(FontFactory.FUTURA_HEAVYOBLIQUE, Font.PLAIN, 48);

	/** Font used */
	private static Font imageTitleFont = FontFactory.getInstance().getFont(FontFactory.FUTURA_BOLD, Font.PLAIN, 24);

	/** Preferred space between title */
	private static Insets titleMargin = new Insets(0, 50, 0, 50);

    /** The title color */
    private static Color titleColor = Color.white;

	/** The image title color */
	private static Color imageTitleColor = Color.black.brighter().brighter().brighter();

    /** Constructs the trailer panel */
    public TCSWinners(AnnounceTCSWinnersEvent evt, String roundName, String contestName) {
		// Create the page
		super(new HeaderRenderer(roundName),
			  new StatsPanel(evt),
			  new TrailerRenderer(contestName));
    }
    
    static class StatsPanel extends SGridLayout {
    	
    	public StatsPanel(AnnounceTCSWinnersEvent evt) {
			STextField titlePanel = new STextField("WINNERS", titleColor, titleFont);
			
			STextField designTitle = new STextField("DESIGN", titleColor, imageTitleFont);
			designTitle.setJustification(STextField.CENTER);
			SBox designTitleBox = new SBox(designTitle);
			designTitleBox.setResizePanel(true);
			
			STextField developmentTitle = new STextField("DEVELOPMENT", titleColor, imageTitleFont);
			developmentTitle.setJustification(STextField.CENTER);
			SBox developmentTitleBox = new SBox(developmentTitle);
			developmentTitleBox.setResizePanel(true);

			HandleScoreRenderer designHandle = new HandleScoreRenderer(evt.getDesignHandle(), evt.getDesignWinnerAverage(), Constants.getRankColor(evt.getDesignWinnerRating()));
			HandleScoreRenderer develHandle = new HandleScoreRenderer(evt.getDevelopmentHandle(), evt.getDevelopmentWinnerAverage(), Constants.getRankColor(evt.getDevelopmentWinnerRating()));
			
			int width = Math.max(designHandle.getWidth(), develHandle.getWidth());
			int height = Math.max(designHandle.getHeight(), designHandle.getHeight());
			designHandle.setSize(width, height);
			develHandle.setSize(width, height);
			
			SLabelledImage designImage = new SLabelledImage(new SImage(evt.getDesignImage()), designHandle);			
			SLabelledImage develImage = new SLabelledImage(new SImage(evt.getDevelopmentImage()), develHandle);
			
			SGridCell designTitleCell = new SGridCell(designTitleBox, SGridCell.SOUTH, SGridCell.HORIZONTAL);
			SGridCell designImageCell = new SGridCell(designImage, SGridCell.NORTH, SGridCell.NONE);
			SGridLayout designLayout = new SGridLayout(new SGridCell[][] {{designTitleCell},{designImageCell}});
			
			SGridCell develTitleCell = new SGridCell(developmentTitleBox, SGridCell.SOUTH, SGridCell.HORIZONTAL);
			SGridCell develImageCell = new SGridCell(develImage, SGridCell.NORTH, SGridCell.NONE);
			SGridLayout develLayout = new SGridLayout(new SGridCell[][] {{develTitleCell},{develImageCell}});
			
			SGridCell designCell = new SGridCell(designLayout, SGridCell.EAST, SGridCell.NONE);
			SGridCell titleCell = new SGridCell(new SMargin(titlePanel, new Insets(0,50,0,50)), SGridCell.CENTER, SGridCell.NONE);
			SGridCell develCell = new SGridCell(develLayout, SGridCell.WEST, SGridCell.NONE);
			
			this.setGrid(new SGridCell[][] {{designCell, titleCell, develCell}});
    	}
    }

}
