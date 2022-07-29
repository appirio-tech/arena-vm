/**
 * TSCScoreCard Description: The renderer to show the scorecard
 * 
 * @author Tim "Pops" Roberts
 * @version 1.0
 */
package com.topcoder.client.spectatorApp.tcs;

import java.awt.Color;
import java.awt.Font;
import java.awt.Insets;
import java.util.Arrays;
import com.topcoder.client.spectatorApp.FontFactory;
import com.topcoder.client.spectatorApp.event.AnnounceReviewBoardResultsEvent;
import com.topcoder.client.spectatorApp.messages.TCSCoderInfo;
import com.topcoder.client.spectatorApp.scoreboard.view.UserHandlePanel;
import com.topcoder.client.spectatorApp.views.AnimatePanel;
import com.topcoder.client.spectatorApp.widgets.SDimmerFilter;
import com.topcoder.client.spectatorApp.widgets.SGridCell;
import com.topcoder.client.spectatorApp.widgets.SGridLayout;
import com.topcoder.client.spectatorApp.widgets.SGridSizingPolicy;
import com.topcoder.client.spectatorApp.widgets.SGridSpacingPolicy;
import com.topcoder.client.spectatorApp.widgets.SMargin;
import com.topcoder.client.spectatorApp.widgets.SOutline;
import com.topcoder.client.spectatorApp.widgets.SPage;
import com.topcoder.client.spectatorApp.widgets.STable;
import com.topcoder.client.spectatorApp.widgets.STextField;
import org.apache.log4j.Category;

public class TCSScoreCard extends SPage {
    private static final Category cat = Category.getInstance(TCSScoreCard.class.getName());
    
	/** Constructs the trailer panel */
	public TCSScoreCard(AnnounceReviewBoardResultsEvent evt, String title, String roundName, String contestName, int[] placements) {
		// Add the layers
		super(new HeaderWithTitleRenderer(roundName, title), new BodyPanel(evt,placements), new TrailerRenderer(contestName));
	}

	/** Body panel - does all the work */
	static class BodyPanel extends SGridLayout {
		/** Font for the review board title */
		private Font titleFont = FontFactory.getInstance().getFont(FontFactory.FUTURA_BOLD, Font.PLAIN, 40);

		/** Font for the reviewer average */
		private Font reviewAvgFont = FontFactory.getInstance().getFont(FontFactory.FUTURA_HEAVY, Font.PLAIN, 30);

		public BodyPanel(AnnounceReviewBoardResultsEvent evt, int[] placements) {
			// Setup our table
			STable table = new STable();
			
			// Standard width of the score columns (to be calculated)
			int standardWidth = new TCSPointValueRenderer(999.99, Color.white).getWidth();
			
			// Setup the coder names
			TCSCoderInfo[] coders = evt.getCoders();
			String[] reviewerNames = evt.getReviewerNames();
                        
                        int[] placesToShow;
                        if(placements == null) {
                            //show everyone
                            cat.info("LENGTH IS NULL");
                            placesToShow = new int[coders.length];
                            for(int i = 0; i < coders.length; i++) {
                                placesToShow[i] = i+1;
                            }
                        } else {
                            placesToShow = placements;
                        }
                        
                        cat.info("LENGTH IS " + placesToShow.length);
                        	
			// Create the headings - first column will be null
			AnimatePanel[] headings = new AnimatePanel[reviewerNames.length + 2];
			
			// Put in the reviewers (starting in the second column)
			for (int x = 0; x < reviewerNames.length; x++) {
				headings[x + 1] = new TCSReviewerTitleRenderer(reviewerNames[x]);
				standardWidth = Math.max(standardWidth, headings[x + 1].getWidth());
			}
			
			// Add the last column
			headings[headings.length - 1] = new STextField("REVIEWER AVG", reviewAvgFont);
			
			// Set the column headings
			table.setColumnHeadings(headings);
			
			int rowHeight = 0;
			// Create array
			// 1. Reserve the first row for the headers
			// 2. Reserver the first column for coders (except first row which is
			// null)
			// 3. Reserve the last column for final scores
			SGridCell[][] cells = new SGridCell[placesToShow.length][reviewerNames.length + 2];
			
			// Put in the coders in the first column
                        int idx = 0;
			for (int x = 0; x < coders.length; x++) {
                                if(idx < placesToShow.length && placesToShow[idx] == (x+1)) {
                                    cells[idx][0] = new SGridCell(new UserHandlePanel(coders[x].getHandle(), coders[x].getSeed(), coders[x].getTcRating()), SGridCell.VERTICAL_ALIGNMENT_WEST, SGridCell.HORIZONTAL);
                                    rowHeight = Math.max(rowHeight, cells[idx][0].getHeight());
                                    idx++;
                                }
			}
			
			// Convience to get scores
			double[][] scores = evt.getScores();
			double[] finalScores = evt.getFinalScores();
			
			// Figure out the maximum rows/column scores
			double maxFinalScore = -1;
			for (int x = 0; x < finalScores.length; x++) maxFinalScore = Math.max(maxFinalScore, finalScores[x]);
			double[] maxReviewerScore = new double[evt.getReviewerNames().length];
			Arrays.fill(maxReviewerScore, Double.MIN_VALUE);
			for (int y = 0; y < maxReviewerScore.length; y++) {
				for (int x = 0; x < evt.getCoders().length; x++) {
					maxReviewerScore[y] = Math.max(maxReviewerScore[y], scores[x][y]);
				}
			}
			
			// Setup the scores
                        idx = 0;
			for (int x = 0; x < scores.length; x++) {
                            if(idx < placesToShow.length && placesToShow[idx] == (x+1)) {
				for (int y = 0; y < scores[x].length; y++) {
                                        cells[idx][y + 1] = new SGridCell(new TCSPointValueRenderer(scores[x][y], (scores[x][y] == maxReviewerScore[y] && scores[x][y] >= 0.0 ? Color.green : new Color(5, 80, 113))),
                                                                SGridCell.VERTICAL_ALIGNMENT, SGridCell.HORIZONTAL);
                                        standardWidth = Math.max(standardWidth, cells[idx][y + 1].getPanel().getWidth());
                                        rowHeight = Math.max(rowHeight, cells[idx][y+1].getHeight());
				}
                                idx++; 
                            }
			}
			// Setup the total scores
                        idx = 0;
			for (int x = 0; x < finalScores.length; x++) {
                            if(idx < placesToShow.length && placesToShow[idx] == (x+1)) {
				cells[idx][cells[idx].length - 1] = new SGridCell(new TCSTotalValueRenderer(finalScores[x]), SGridCell.VERTICAL_ALIGNMENT, SGridCell.NONE);
				rowHeight = Math.max(rowHeight, cells[idx][cells[idx].length - 1].getHeight());
                                idx++;
                            }
			}
			
			// Add each row
			for (int x = 0; x < cells.length; x++) {
				// Add the rows
				table.addRow(cells[x]);
				// Dim the row if no the winning score
				if (maxFinalScore > 0 && finalScores[placesToShow[x]-1] != maxFinalScore) {
					table.setRowFilter(x, SDimmerFilter.INSTANCE);
				}
			}
			
			// Make all the score columns the same size
			final int columnWidth = standardWidth;
			table.setColumnSizingPolicy(new SGridSizingPolicy() {
				public void assignSizes(int[] values, int total) {
					for (int x = 1; x < values.length - 1; x++) {
						values[x] = columnWidth;
					}
				}
			});
			table.setRowSizingPolicy(new SGridSizingPolicy.FixedSizingPolicy(rowHeight));
			
			// Create an outline for the handles
			// (note: insets match the margin around the table)
			SOutline outline = new SOutline(null, new Insets(20, 20, 20, 20));
			outline.setFillBox(true);
			table.setColumnOutline(0, outline);
			
			// Allow the interspacing to take up the excess width
			table.setRowSpacingPolicy(new SGridSpacingPolicy.HeaderSpacingPolicy(0, new SGridSpacingPolicy.EdgePercentageSpacingPolicy(1.0, .5)));
			table.setColumnSpacingPolicy(new SGridSpacingPolicy.EdgePercentageSpacingPolicy(1.0, .0));
			
			// Create the review board text font
			STextField reviewBoard = new STextField("REVIEW BOARD", titleFont);
			
			// Setup the overall stuff
			this.setRowSizingPolicy(SGridSizingPolicy.RIGHT_SIZING);
			this.setColumnSizingPolicy(SGridSizingPolicy.EVEN_SIZING);
			
			SGridCell titleCell = new SGridCell(new SMargin(reviewBoard, new Insets(50, 0, 25, 0)), SGridCell.CENTER, SGridCell.NONE);
			SGridCell tableCell = new SGridCell(new SMargin(table, new Insets(0, 20, 0, 20)), SGridCell.CENTER, SGridCell.BOTH);
			//tableCell.setDebugColor(Color.yellow);
			this.setGrid(new SGridCell[][] { { titleCell }, { tableCell } });
		}		
	}
}
