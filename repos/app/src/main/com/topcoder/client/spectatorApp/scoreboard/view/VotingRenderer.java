/**
 * SystemTestRenderer.java
 *
 * Description:		Coding Phase renderer
 * @author			Tim "Pops" Roberts
 * @version			1.0
 */

package com.topcoder.client.spectatorApp.scoreboard.view;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Insets;
import java.awt.Rectangle;
import java.awt.TexturePaint;
import java.awt.Toolkit;
import java.awt.Transparency;
import java.awt.image.BufferedImage;

import org.apache.log4j.Category;

import com.topcoder.client.spectatorApp.CommonRoutines;
import com.topcoder.client.spectatorApp.SpectatorApp;
import com.topcoder.client.spectatorApp.scoreboard.model.Team;
import com.topcoder.client.spectatorApp.views.AnimatePanel;
import com.topcoder.client.spectatorApp.views.LayeredPanel;
import com.topcoder.client.spectatorApp.views.VolatileImagePanel;
import com.topcoder.shared.netCommon.messages.spectator.CoderRoomData;


public class VotingRenderer extends LayeredPanel implements AnimatePanel {

    /** Reference to the logging category */
    private static final Category cat = Category.getInstance(VotingRenderer.class.getName());

    /** Reference to the team */
    private Team team;

    /** The margin from the left edge */
    private static final int marginLeft = 10;

    /** The margin from the right edge */
    private static final int marginRight = 15;

    /** The margin the background to the user handle should extend to the right */
    private static final int marginUserRight = 15;

    /** The margin the background to the user handle should extend */
    private static final int marginUserBackVertical = 15;

    /** The user handle column */
    private UserHandlePanel[] userPanels;

    /** The the voting panels */
    private VoteValuePanel[] voteValuePanels;

    /** The column headers */
    private TitlePanel titlePanel;

    /** Minimum row size */
    private int minimumRowHeight;

    /** Minimum col size */
    private int minimumColWidth;

    /** Margin between rows */
    private int marginBetweenRows;

    /** Margin between cols */
    private int marginBetweenCols;

    /** Full row size */
    private int fullRowSize;

    /** Full col size */
    private int fullColSize;

    /** Minimum width */
    private int minWidth;

    /** Minimum height */
    private int minHeight;

    /** Minimum height of the user handle panel */
    private int minimumUserHandleHeight;

    /** Minimum width of the user handle panel */
    private int minimumUserHandleWidth;

    /** Rectangle background color */
    private static final Color backColor = new Color(5, 80, 113);

    /** Rectangle outline color */
    private static final Color outlineColor = new Color(0, 150, 166);

    /** Insets around the title box */
    private Insets titleInsets = new Insets(5, 0, 5, 0);

    /** background texture */
    private TexturePaint backgroundTexture;

    /** transparency */
    private static final AlphaComposite semiTransparent = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, .5f);

    /** Preferred Alignment */
    private int alignment;

    /** Background Image (to the body) */
    private BufferedImage backImage;

    /** Reference to the coderdata array */
    CoderRoomData[] coderData;

	/** Layer for the background panel */
	protected static final int BACKGROUNDLAYER = 0;
	
	/** Layer for the background panel */
	protected static final int HEADERLAYER = 1;
	
	/** Layer for the background panel */
	protected static final int TRAILERLAYER = 2;
	
	/** Layer for the background panel */
	protected static final int VOTINGLAYER = 3;
	
    /** Constructs the panel */
    public VotingRenderer(Team team, String contestName) {

        // Save reference to the team
        this.team = team;
        coderData = team.getCoderData();

        // Get the background tile
        Image background = Toolkit.getDefaultToolkit().getImage(SpectatorApp.class.getResource("dots.gif"));

        // Load the images
        if (!CommonRoutines.loadImagesFully(new Image[]{background})) {
            System.out.println("Error loading the images");
            return;
        }

        // Convert to Buffered Images
        backImage = CommonRoutines.createBufferedImage(background, Transparency.OPAQUE);

        // Get the sizes of things
        int rowSize = coderData.length;

        // Create the array items
        userPanels = new UserHandlePanel[rowSize];
        voteValuePanels = new VoteValuePanel[rowSize];


        // Reset the minimums
        minimumUserHandleWidth = -1;
        minimumUserHandleHeight = -1;
        minimumRowHeight = -1;
        minimumColWidth = -1;

        // Create the user panels and the total panels
        for (int r = 0; r < rowSize; r++) {
            userPanels[r] = new UserHandlePanel(coderData[r].getHandle(), coderData[r].getSeed(), coderData[r].getRank());
            minimumUserHandleWidth = Math.max(userPanels[r].getWidth(), minimumUserHandleWidth);
            minimumUserHandleHeight = Math.max(userPanels[r].getHeight(), minimumUserHandleHeight);

            if (userPanels[r].getHeight() > rowSize) alignment = userPanels[r].getVerticalAlignment();
            minimumRowHeight = Math.max(userPanels[r].getHeight(), minimumRowHeight);

            voteValuePanels[r] = new VoteValuePanel();

            if (voteValuePanels[r].getHeight() > rowSize) alignment = voteValuePanels[r].getVerticalAlignment();
            minimumRowHeight = Math.max(voteValuePanels[r].getHeight(), minimumRowHeight);
            minimumColWidth = Math.max(voteValuePanels[r].getWidth(), minimumColWidth);
        }

        // Create the title panel
        titlePanel = new TitlePanel("Votes", minimumColWidth);

		// Create the header and trailer panels
		addLayer(new BackgroundPanel());
		addLayer(new HeaderPanel(team.getTeamName()));
		addLayer(new TrailerPanel(contestName));
		addLayer(new VotingPanel());


        // Calculate the minimum width
        minWidth = marginLeft + minimumUserHandleWidth + marginUserRight + minimumColWidth + marginRight;
        minWidth = Math.max(minWidth, getLayer(HEADERLAYER).getWidth());
        minWidth = Math.max(minWidth, getLayer(TRAILERLAYER).getWidth());

        // Calculate the minimum height
        minHeight = getLayer(HEADERLAYER).getHeight() + titleInsets.top + titlePanel.getHeight() + titleInsets.bottom + (team.getCoderCount() * minimumRowHeight) + getLayer(TRAILERLAYER).getHeight();

        // Make the current width/height equal to the minimum
        setSize(minWidth, minHeight);
    }


	public void setSize(int width, int height) {
		// Record the new size
		super.setSize(width, height);
    	
		// Get the various panels
		HeaderPanel headerPanel = (HeaderPanel)getLayer(HEADERLAYER);
		TrailerPanel trailerPanel = (TrailerPanel)getLayer(TRAILERLAYER);
		BackgroundPanel backgroundPanel = (BackgroundPanel)getLayer(BACKGROUNDLAYER);
		VotingPanel votingPanel = (VotingPanel)getLayer(VOTINGLAYER);

		// Set the pos/size of the header panel
		headerPanel.setPosition(0,0);
		headerPanel.setSize(width, headerPanel.getHeight());    	

		// Set the pos/size of the trailer panel
		trailerPanel.setPosition(0, height - trailerPanel.getHeight());
		trailerPanel.setSize(width, trailerPanel.getHeight());    	

		// Set the pos/size of the background panel
		backgroundPanel.setPosition(0, headerPanel.getHeight());
		backgroundPanel.setSize(width, height-headerPanel.getHeight()-trailerPanel.getHeight());

		// Set the pos/size of the message panel
		votingPanel.setPosition(0, backgroundPanel.getY());
		votingPanel.setSize(width, backgroundPanel.getHeight());
		
	}
	
	class BackgroundPanel extends VolatileImagePanel {
	    /**
	     * Creates the back buffer which contains all the non-changeable items
	     */
	    public void drawImage(Graphics2D g2D) {
	
	        // Figure out the padding involved
	        marginBetweenRows = (getHeight() - minHeight) / userPanels.length;
	        marginBetweenCols = (getWidth() - minWidth) / 2;
	        fullRowSize = marginBetweenRows + minimumRowHeight;
	
	        // Paint the background image
	        g2D.setPaint(new TexturePaint(backImage, new Rectangle(0, 0, backImage.getWidth(), backImage.getHeight())));
	        g2D.fillRect(0, 0, getWidth(), getHeight());
	
	        // Translate over to where the point value title should be
	        g2D.translate(marginLeft + minimumUserHandleWidth + marginBetweenCols, titleInsets.top);
	
	        // Draw the title
	        titlePanel.render(g2D);
	
	        // Translate back to where the user handle box will appear
	        g2D.translate(-(marginLeft + minimumUserHandleWidth + marginBetweenCols), titlePanel.getHeight() + titleInsets.bottom);
	
	        // Draw the background for theuser panel
	        int bWidth = minimumUserHandleWidth + marginLeft + marginUserRight;
	        int bHeight = (marginUserBackVertical * 2) + ((userPanels.length - 1) * fullRowSize) + minimumUserHandleHeight;
	        g2D.setPaint(backColor);
	        g2D.setComposite(semiTransparent);
	        g2D.fillRect(0, 0, bWidth, bHeight);
	
	        // Draw the outline
	        g2D.setPaint(outlineColor);
	        g2D.drawRect(0, 0, bWidth - 1, bHeight - 1);
	    }
	}

	class VotingPanel extends LayeredPanel {
		
		public VotingPanel() {
			// Setup the panel
	        for (int r = 0; r < coderData.length; r++) {
				
				// The row layer
				LayeredPanel rowLayer = new LayeredPanel();

	            // Render the user panel
	            rowLayer.addLayer(userPanels[r]);
	
	            // Render the points
	            rowLayer.addLayer(voteValuePanels[r]);

				// Add the row to the layer
				addLayer(rowLayer);
	        }
	
		}	

		/**
		 * Disposes of resources used
		 */
		public void dispose() {
			super.dispose();
			for (int r = userPanels.length - 1; r >= 0; r--) userPanels[r].dispose();
			for (int r = voteValuePanels.length - 1; r >= 0; r--) voteValuePanels[r].dispose();
			titlePanel.dispose();
		}

		/**
		 * Animates the panel
		 */
		public final void animate(long now, long diff) {
			super.animate(now, diff);
			for (int r = userPanels.length - 1; r >= 0; r--) userPanels[r].animate(now, diff);
			for (int r = voteValuePanels.length - 1; r >= 0; r--) voteValuePanels[r].animate(now, diff);
			titlePanel.animate(now, diff);

	        // Create an array for votes against
	        int[] votesAgainst = new int[coderData.length];
	        for (int x = coderData.length - 1; x >= 0; x--) {
	            votesAgainst[x] = team.getVotesAgainst(coderData[x].getCoderID());
	        }
	
	        // Create a crossxref in vote against order
	        int[] xref = new int[votesAgainst.length];
	        boolean[] seen = new boolean[votesAgainst.length];
	        for (int x = 0; x < xref.length; x++) {
	            int max = -1;
	            for (int y = 0; y < xref.length; y++) {
	                if ((max == -1 || votesAgainst[y] > votesAgainst[max]) && !seen[y]) {
	                    max = y;
	                }
	            }
	            seen[max] = true;
	            xref[x] = max;
	        }
	
	        // Translate past the header and the title
	        int currX = marginLeft;
			int currY = titleInsets.top + titlePanel.getHeight() + titleInsets.bottom + marginUserBackVertical;
	
	        for (int r = 0; r < xref.length; r++) {
	            // Translate to where the user panel is
	            currY += alignment - userPanels[xref[r]].getVerticalAlignment();
	
	            // Render the user panel
	            userPanels[xref[r]].setPosition(currX, currY);
	
	            // To where the point value goes
	            currX += minimumUserHandleWidth + marginBetweenCols;
				currY += -(alignment - userPanels[xref[r]].getVerticalAlignment());
	
	            // Align it
	            currY += alignment - voteValuePanels[xref[r]].getVerticalAlignment();
	
	            // Render the points
	            voteValuePanels[xref[r]].setPointValue(votesAgainst[xref[r]]);
	            voteValuePanels[xref[r]].setPosition(currX, currY);
	
	            // Translate back to origin plus one row
	            currX += -(minimumUserHandleWidth + marginBetweenCols);
				currY += -(alignment - voteValuePanels[xref[r]].getVerticalAlignment()) + fullRowSize;
	        }
	
		}


	}	
}
