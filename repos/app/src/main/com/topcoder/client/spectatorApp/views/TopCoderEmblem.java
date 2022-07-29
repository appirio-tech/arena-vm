/**
 * LogoRenderer.java Description: The panel that displays the logo
 * 
 * @author Tim "Pops" Roberts
 * @version 1.0
 */
package com.topcoder.client.spectatorApp.views;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Composite;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.geom.GeneralPath;
import org.apache.log4j.Category;
import com.topcoder.client.spectatorApp.CommonRoutines;
import com.topcoder.client.spectatorApp.FontFactory;
import com.topcoder.client.spectatorApp.views.LayeredPanel.LayeredFilter;

public class TopCoderEmblem extends LayeredPanel implements AnimatePanel {
	/** Reference to the logging category */
	private static final Category cat = Category.getInstance(TopCoderEmblem.class.getName());

	/** Font used */
	private Font titleFont = FontFactory.getInstance().getFont(FontFactory.FUTURA_BOLDOBLIQUE, Font.BOLD, 40);

	/** Font metrics */
	private FontMetrics titleFontFM;

	/** Space from the word */
	private final int BUFFERWORD = 100;

	/** Space between each character */
	private final int BUFFERCHAR = 4;

	/** The brackets */
	private Bracket[] brackets;

	/** The string panels */
	private StringPanel[] stringPanels;

	/** The box panels */
	private BoxPanel[] boxPanels;

	/** The overall layer filter */
	private LayerHandler layerHandler;

	/** Constructs the trailer panel */
	public TopCoderEmblem() {
		resetEmblem(CommonRoutines.calcTimePerSecond(3));
	}

	/** Restarts the panel by just recreating everything */
	public void resetEmblem(long delayMS) {
		// Remove all the layers
		while (this.getLayers().length > 0) this.removeLayer(0);
		
		// Create a new layer filter
		layerHandler = new LayerHandler();
		char[] word = { 'T', 'o', 'p', 'C', 'o', 'd', 'e', 'r' };
		boolean[] red = { false, false, false, true, true, true, true, true };
		stringPanels = new StringPanel[word.length];
		
		// Create the string panels and set their position
		int startX = BUFFERWORD;
		int stringWidth = 0, stringHeight = 0;
		for (int x = 0; x < word.length; x++) {
			stringPanels[x] = new StringPanel(word[x], red[x]);
			stringPanels[x].setPosition(startX, 0);
			stringWidth = Math.max(stringWidth, stringPanels[x].getWidth());
			stringHeight = Math.max(stringHeight, stringPanels[x].getHeight());
			startX += stringPanels[x].getWidth() + BUFFERCHAR;
		}
		
		// Create the boxes that will zoom to the string panel
		startX += BUFFERWORD;
		boxPanels = new BoxPanel[stringPanels.length];
		for (int x = 0; x < stringPanels.length; x++) {
			boxPanels[x] = new BoxPanel(stringPanels[x], (x <= 4 ? startX : 0));
			boxPanels[x].setSize(stringWidth, stringHeight);
			this.addLayer(boxPanels[x], layerHandler);
		}
		
		// Setup which box panel triggers the next one
		boxPanels[0].setNextBoxPanel(boxPanels[7]);
		boxPanels[7].setNextBoxPanel(boxPanels[1]);
		boxPanels[1].setNextBoxPanel(boxPanels[6]);
		boxPanels[6].setNextBoxPanel(boxPanels[2]);
		boxPanels[2].setNextBoxPanel(boxPanels[5]);
		boxPanels[5].setNextBoxPanel(boxPanels[3]);
		boxPanels[3].setNextBoxPanel(boxPanels[4]);
		
		// Add all the string panels LAST (to draw over everything else)
		for (int x = 0; x < stringPanels.length; x++) {
			addLayer(stringPanels[x], layerHandler);
		}
		
		// Create the two brackes to be drawn
		brackets = new Bracket[2];
		for (int x = 0; x < 2; x++) {
			brackets[x] = new Bracket(x % 2 == 0);
			brackets[x].setSize(BUFFERWORD, stringHeight + 20);
			brackets[x].setPosition(x % 2 == 0 ? 0 : startX - BUFFERWORD, -5);
			addLayer(brackets[x], layerHandler);
		}
		
		// Set this size
		this.setSize(startX + stringWidth, stringHeight);
		
		// Start the first box panel in awhile
		boxPanels[0].setDelayed(delayMS);
	}

	class LayerHandler implements LayeredFilter {
		// A temporary composite
		Composite comp;

		// Current phase
		int phase = 0;

		// Time when phase became active
		long phaseTime = 0;

		// Not active phase
		final int NOTACTIVE = 0;

		// Waiting to fade
		final int WAITTOFADE = 1;

		// Fade away phase
		final int FADE = 2;

		// Time to wait until fade
		final double TIMETOWAIT = CommonRoutines.calcTimePerSecond(3);

		// Time to take to fade
		final double TIMETOFADE = CommonRoutines.calcTimePerSecond(1);

		// Start
		public void start() {
			phaseTime = CommonRoutines.getCurrentTime();
			phase = WAITTOFADE;
		}

		// Set the next phase
		public void setNextPhase() {
			phaseTime = CommonRoutines.getCurrentTime();
			phase++;
		}

		public void filter(Graphics2D g2D) {
			long now = CommonRoutines.getCurrentTime();
			switch (phase) {
			case NOTACTIVE:
				return;
			case WAITTOFADE: {
				if (phaseTime < now - TIMETOFADE) {
					setNextPhase();
					return;
				}
				break;
			}
			case FADE: {
				double clr = (now - phaseTime) / TIMETOFADE;
				if (clr >= 1.0d) {
					comp = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0f);
					setNextPhase();
					// Reset!
					resetEmblem(CommonRoutines.calcTimePerSecond(1));
					break;
				} else {
					comp = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1f - (float) clr);
				}
				g2D.setComposite(comp);
				break;
			}
			default:
				return;
			}
		}
	}

	/** The bracket panel */
	class Bracket extends DirectDrawPanel {
		// Thickness of the line
		int lineSize = 7;

		// Shape of the bracket (including tail)
		GeneralPath shape;

		// Whether this is the left or right side
		boolean reverse;

		// Current phase
		int phase = 0;

		// The time phase started
		long phaseTime = 0;

		// Time when we start drawing up
		long upTime = 0;

		// Not active phase status
		final int NOTACTIVE = 0;

		// Draw phase status
		final int DRAW = 1;

		// Time taken to draw
		final double TIMETODRAW = CommonRoutines.calcTimePerSecond(.25);

		// Clipping rectangle
		Rectangle clipRect;

		// Create a left or right bracked
		public Bracket(boolean reverse) {
			this.reverse = reverse;
		}

		// Set the next phase
		public void setNextPhase() {
			phaseTime = CommonRoutines.getCurrentTime();
			phase++;
		}

		// Animage
		public void animate(long now, long diff) {
			switch (phase) {
			// Do nothing if not active
			case NOTACTIVE: {
				break;
			}
			// Start drawing - the bracket is draw
			// as a full image (including tail)
			// and a clipping rectangle is used to
			// simulate the movement of it
			case DRAW: {
				// How far into the draw are we?
				int pos = (int) (((now - phaseTime) / TIMETODRAW) * getWidth());
				// Do we start going up?
				if (upTime == 0 && pos >= getWidth() - lineSize * 2 - (double) clipRect.getWidth()) {
					upTime = now;
				}
				// Figure out where the clip rectangle should begin
				if (reverse) {
					if (pos >= getWidth() - lineSize * 2) {
						clipRect.x = getWidth() - lineSize * 2;
					} else {
						clipRect.x = pos;
					}
				} else {
					if (pos > getWidth() - lineSize * 2) {
						clipRect.x = (int) -clipRect.getWidth() + lineSize * 2;
					} else {
						clipRect.x = getWidth() - (int) clipRect.getWidth() - pos;
					}
				}
				// Figure out the height of the clip
				if (upTime > 0) {
					int mid = (getHeight() / 2) - (lineSize / 2);
					int vpos = (int) (((now - upTime) / TIMETODRAW) * mid);
					if (clipRect.height > getHeight()) {
						clipRect.height = getHeight() * 2;
						setNextPhase();
						layerHandler.start();
						break;
					}
					clipRect.y = mid - vpos;
					clipRect.height = vpos * 2 + lineSize;
					break;
				}
			}
			}
		}

		/** Resets the size of every thing */
		public void setSize(int width, int height) {
			super.setSize(width, height);
			int mid = (height / 2) - (lineSize / 2);
			shape = new GeneralPath();
			if (reverse) {
				clipRect = new Rectangle(0, mid, 50, lineSize);
				shape.moveTo(width - lineSize * 2, 0);
				shape.lineTo(width, 0);
				shape.lineTo(width, lineSize);
				shape.lineTo(width - lineSize, lineSize);
				shape.lineTo(width - lineSize, height - lineSize);
				shape.lineTo(width, height - lineSize);
				shape.lineTo(width, height);
				shape.lineTo(width - lineSize * 2, height);
				shape.lineTo(width - lineSize * 2, mid + lineSize);
				shape.lineTo(0, mid + lineSize);
				shape.lineTo(0, mid);
				shape.lineTo(width - lineSize * 2, mid);
				shape.closePath();
			} else {
				clipRect = new Rectangle(width - 50, mid, 50, lineSize);
				shape.moveTo(lineSize * 2, 0);
				shape.lineTo(0, 0);
				shape.lineTo(0, lineSize);
				shape.lineTo(lineSize, lineSize);
				shape.lineTo(lineSize, height - lineSize);
				shape.lineTo(0, height - lineSize);
				shape.lineTo(0, height);
				shape.lineTo(lineSize * 2, height);
				shape.lineTo(lineSize * 2, mid + lineSize);
				shape.lineTo(width, mid + lineSize);
				shape.lineTo(width, mid);
				shape.lineTo(lineSize * 2, mid);
				shape.closePath();
			}
		}

		/** Draw the bracking clipping when necessary */
		public void drawImage(Graphics2D g2D) {
			if (phase == NOTACTIVE) return;
			g2D.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
			g2D.setPaint(Color.white);
			g2D.setClip(clipRect);
			g2D.fill(shape);
			g2D.setClip(null);
		}
	}

	/** Box that zooms out and away from a string */
	class BoxPanel extends DirectDrawPanel {
		// The good composite
		Composite GOOD = AlphaComposite.SrcOver;

		// A temporary composite
		Composite comp = GOOD;

		// The associated string panel
		StringPanel stringPanel;

		// The next box panel to trigger
		BoxPanel nextBoxPanel = null;

		// Initial starting point
		int startX;

		// Current phase
		int phase = 0;

		// Time to delay until
		long delayTime = 0;

		// Time when phase became active
		long phaseTime = 0;

		// Not active phase
		final int NOTACTIVE = 0;

		// Box is moving to the character phase
		final int TOCHAR = 1;

		// Box is at the character phase
		final int ATCHAR = 2;

		// Box is moving away from character phase
		final int FROMCHAR = 3;

		// Time taken to get to the character
		final double TIMETOCHAR = CommonRoutines.calcTimePerSecond(.333333333);

		// Horizontal line size
		final int HORIZONTAL = 5;

		// Vertical line size
		final int VERTICAL = 2;

		// Horizontal buffer around string
		final int HORIZONTALBUFFER = 1;

		// Vertical buffer around string
		final int VERTICALBUFFER = 0;

		// The goal X
		int goalX;

		/** Construct it */
		public BoxPanel(StringPanel stringPanel, int startX) {
			this.stringPanel = stringPanel;
			this.startX = startX;
			this.goalX = stringPanel.getX() - VERTICAL - VERTICALBUFFER;
			stringPanel.setBoxPanel(this);
			this.setPosition(startX, stringPanel.getY() - HORIZONTAL - HORIZONTALBUFFER);
		}

		/** Set the size */
		public void setSize(int width, int height) {
			width += VERTICAL + HORIZONTAL;
			height += HORIZONTAL + HORIZONTAL;
			super.setSize(width, height);
		}

		/** Set the next box panel to trigger */
		public void setNextBoxPanel(BoxPanel nextBoxPanel) {
			this.nextBoxPanel = nextBoxPanel;
		}

		/** Start delay (3 seconds) */
		public void setDelayed(long delayMS) {
			delayTime = CommonRoutines.getCurrentTime() + delayMS;
		}

		/** Set the next phase */
		public void setNextPhase() {
			phaseTime = CommonRoutines.getCurrentTime();
			phase++;
		}

		/** Animate */
		public void animate(long now, long diff) {
			switch (phase) {
			// Not Active - set active when over the delay
			case NOTACTIVE: {
				if (delayTime > 0 && delayTime < now) {
					setNextPhase();
					delayTime = 0;
				}
				break;
			}
			// Box moving to the character
			case TOCHAR: {
				int pos = (int) (((now - phaseTime) / TIMETOCHAR) * (Math.abs(startX - goalX)));
				if ((startX < goalX && startX + pos > goalX) || (startX > goalX && startX - pos < goalX)) {
					setPosition(goalX, getY());
					setNextPhase();
					stringPanel.setNextPhase();
					int temp = startX;
					startX = goalX;
					goalX = temp;
					if (nextBoxPanel != null) nextBoxPanel.setNextPhase();
					break;
				}
				setPosition(startX + (startX < goalX ? pos : -pos), getY());
				break;
			}
			// Box at the character
			case ATCHAR: {
				// Waiting
				break;
			}
			// Box moving away from the character
			case FROMCHAR: {
				int pos = (int) (((now - phaseTime) / TIMETOCHAR) * (Math.abs(startX - goalX)));
				setPosition(startX + (goalX > startX ? pos : -pos), getY());
				double clr = (now - phaseTime) / TIMETOCHAR;
				if (clr >= 1.0d) {
					comp = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0f);
					phase++;
					if (nextBoxPanel == null) {
						for (int x = 0; x < 2; x++)
							brackets[x].setNextPhase();
					}
					break;
				} else {
					if (pos > 0) {
						comp = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1f - (float) clr);
					}
				}
			}
			}
		}

		// Draw the box
		public void drawImage(Graphics2D g2D) {
			if (phase == NOTACTIVE) return;
			g2D.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
			g2D.setPaint(Color.white);
			Composite saved = g2D.getComposite();
			g2D.setComposite(comp);
			g2D.fillRect(0, 0, VERTICAL, getHeight());
			g2D.fillRect(0 + getWidth() - VERTICAL, 0, VERTICAL, getHeight());
			g2D.fillRect(0, 0, getWidth(), HORIZONTAL);
			g2D.fillRect(0, 0 + getHeight() - HORIZONTAL, getWidth(), HORIZONTAL);
			g2D.setComposite(saved);
		}
	}

	/** String panel */
	class StringPanel extends DirectDrawPanel {
		// Character to eventually reach
		char goalCh;

		// Character we start at
		char startingChar;

		// Current character
		char[] currChar = new char[1];

		// Current color of character
		Color currentColor = Color.white;

		// Whether character should turn red
		boolean red;

		// Associated box panel
		BoxPanel boxPanel;

		// Current phase
		int phase = 0;

		// Current phase time
		long phaseTime = 0;

		// Phase doing nothing
		final int NOTACTIVE = 0;

		// Character up'ing to the goal character
		final int UPCHAR = 1;

		// Character is to red
		final int FADE = 2;

		// Time to up to the goal character
		final double TIMETOCHAR = CommonRoutines.calcTimePerSecond(.333333333);

		// Time to fade to red
		final double TIMETOFADE = CommonRoutines.calcTimePerSecond(.333333333);

		public StringPanel(char ch, boolean red) {
			if (Character.isUpperCase(ch)) {
				currChar[0] = 'A';
				startingChar = currChar[0];
			} else {
				currChar[0] = 'a';
				startingChar = currChar[0];
			}
			this.goalCh = ch;
			this.red = red;
			this.setSize(CommonRoutines.getFontMetrics(titleFont).charWidth(ch), CommonRoutines.getFontMetrics(titleFont).getHeight());
		}

		/** Get the goal character */
		public char getGoalChar() {
			return goalCh;
		}

		/** Set the associated box panel */
		public void setBoxPanel(BoxPanel boxPanel) {
			this.boxPanel = boxPanel;
		}

		/** Set the next phase */
		public void setNextPhase() {
			phaseTime = CommonRoutines.getCurrentTime();
			phase++;
		}

		/** Animate */
		public void animate(long now, long diff) {
			switch (phase) {
			// Not doing anything
			case NOTACTIVE: {
				break;
			}
			// Go up to our goal character
			case UPCHAR: {
				int pos = (int) (((now - phaseTime) / TIMETOCHAR) * 26);
				if (pos > 30 || (char) (startingChar + pos) > goalCh) {
					currChar[0] = goalCh;
					setNextPhase();
					boxPanel.setNextPhase();
					break;
				}
				currChar[0] = (char) (startingChar + pos);
				break;
			}
			// Fade to our red color
			case FADE: {
				int pos = (int) (((now - phaseTime) / TIMETOFADE) * 255);
				if (pos > 255) {
					if (red) currentColor = Color.red;
					setNextPhase();
					break;
				}
				if (red) currentColor = new Color(255, 255 - pos, 255 - pos);
			}
			}
		}

		/** Draw the image */
		public void drawImage(Graphics2D g2D) {
			if (phase == NOTACTIVE) return;
			g2D.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
			g2D.setFont(titleFont);
			g2D.setPaint(currentColor);
			g2D.drawChars(currChar, 0, 1, 0, g2D.getFontMetrics(titleFont).getAscent());
		}
	}
}
