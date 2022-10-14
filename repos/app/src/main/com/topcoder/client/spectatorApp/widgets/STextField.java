package com.topcoder.client.spectatorApp.widgets;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import com.topcoder.client.spectatorApp.CommonRoutines;
import com.topcoder.client.spectatorApp.FontFactory;
import com.topcoder.client.spectatorApp.views.DirectDrawPanel;

/**
 * Spectator Text Field. This field will display (with a transparent background)
 * a text field with the given font and color. Note: the size of this field is
 * based on the text & font and cannot be overridden
 * 
 * @author Tim "Pops" Roberts
 * @version 1.0
 */
public class STextField extends DirectDrawPanel {
	/** Reference to the logging category */
	//private static final Category cat = Category.getInstance(STextField.class.getName());

	/** Font used for the descriptions */
	private Font font;

	/** Color used for the text */
	private Color textColor;

	/** The text */
	private String text = "";

	/** The justification */
	private int justification;

	/** Left Justification */
	public static final int LEFT = 1;

	/** Center justification */
	public static final int CENTER = 2;

	/** Right justification */
	public static final int RIGHT = 3;

	/** Text layout of the text*/
	//private TextLayout textLayout;
	
	/**
	 * Contructs the text field with the given text with the default font
	 * (FUTURA_HEAVYOBLIQUE, 48pt) and white text color
	 * 
	 * @param text
	 *           the text to use
	 */
	public STextField(String text) {
		this(text, FontFactory.getInstance().getFont(FontFactory.FUTURA_HEAVYOBLIQUE, Font.PLAIN, 48));
	}

	/**
	 * Contructs the text field with the given font but no text and white text
	 * color
	 * 
	 * @param font
	 *           the font to use
	 */
	public STextField(Font font) {
		this("unknown", font);
	}

	/**
	 * Contructs the text field with the given font and color but no text
	 * 
	 * @param font
	 *           the font to use
	 * @param color
	 *           the color to use
	 */
	public STextField(Color color, Font font) {
		this("unknown", color, font);
	}

	/**
	 * Contructs the text field with the given text with the default font
	 * (FUTURA_HEAVYOBLIQUE, 48pt) and the specified color
	 * 
	 * @param text
	 *           the text to use
	 * @param color
	 *           the text color
	 */
	public STextField(String text, Color color) {
		this(text, color, FontFactory.getInstance().getFont(FontFactory.FUTURA_HEAVYOBLIQUE, Font.PLAIN, 48));
	}

	/**
	 * Contructs the text field with the given text, the given font, and the
	 * given color
	 * 
	 * @param text
	 *           the text to use
	 * @param font
	 *           the font to use
	 */
	public STextField(String text, Font font) {
		this(text, Color.white, font);
	}

	/**
	 * Contructs the text field with the given text and the given font and given
	 * color
	 * 
	 * @param text
	 *           the text to use
	 * @param font
	 *           the font to use
	 */
	public STextField(String text, Color color, Font font) {
		this.text = text;
		this.textColor = color;
		setFont(font);
		resize();
	}

	/**
	 * Sets the text and resizes the field
	 * 
	 * @param text
	 *           the text to use
	 */
	public void setText(String text) {
		this.text = text;
		resize();
		dispose();
	}

	/**
	 * Resizes the image to fit the current text, and font size
	 * 
	 * @return
	 */
	public void resize() {
//		textLayout = new TextLayout(text.length() == 0  ? " " : text, font, new FontRenderContext(null, true, false));
//		super.setSize((int) Math.ceil(textLayout.getBounds().getWidth()), (int) Math.ceil(textLayout.getAscent() + textLayout.getDescent()));
		FontMetrics fm = CommonRoutines.getFontMetrics(font);
		//super.setSize((int) Math.ceil(fm.stringWidth(text)), (int) Math.ceil(fm.getAscent() + fm.getDescent()));
		super.setSize((int) Math.ceil(fm.stringWidth(text)), (int) Math.ceil(fm.getAscent() + fm.getDescent()));
		setVerticalAlignment(fm.getAscent());
	}

	/** Returns the vertical alignment */
//	public int getVerticalAlignment() {
//		FontMetrics fm = CommonRoutines.getFontMetrics(font);
//		return (int) Math.ceil(fm.getAscent());
//		//return (int) Math.ceil(textLayout.getAscent());
//	}

	/**
	 * Returns the current text
	 * 
	 * @return the current text
	 */
	public String getText() {
		return text;
	}

	/**
	 * Returns the current text color
	 * 
	 * @return the current text color
	 */
	public Color getColor() {
		return textColor;
	}

	/**
	 * Sets the current text color
	 * 
	 * @param textColor
	 *           the text color
	 */
	public void setColor(Color textColor) {
		this.textColor = textColor;
		dispose();
	}

	/**
	 * Returns the current font
	 * 
	 * @return the current font
	 */
	public Font getFont() {
		return font;
	}

	/**
	 * Sets the current font
	 * 
	 * @param font
	 *           the current font
	 */
	public void setFont(Font font) {
		this.font = font;
		resize();
		dispose();
	}

	/**
	 * Returns the current justification
	 * 
	 * @return the current justification
	 */
	public int getJustification() {
		return justification;
	}

	/**
	 * Sets the justification (if unknown - defaults to LEFT)
	 * 
	 * @param justification
	 *           the justification
	 */
	public void setJustification(int justification) {
		this.justification = justification;
		dispose();
	}

	/** Override to do the text */
	public String toString() {
		return this.text;
	}

	/**
	 * Draws the image of the component
	 */
	protected void drawImage(Graphics2D g2D) {
		if (!isVisible()) return;
		
		// Setup antialiasing
		g2D.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
		
		// Draw the room title
		g2D.setPaint(textColor);
		g2D.setFont(font);
		g2D.setClip(0, 0, getWidth(), getHeight());
		
		//int textWidth = (int) Math.ceil(textLayout.getBounds().getWidth());
		int textWidth = (int) Math.ceil(g2D.getFontMetrics().stringWidth(text));
		
		switch (justification) {
		case CENTER:
			g2D.drawString(text, (int) Math.floor(getWidth() / 2) - (textWidth / 2) - 1, getVerticalAlignment());
			break;
		case RIGHT:
			g2D.drawString(text, getWidth() - textWidth - 1, getVerticalAlignment());
			break;
		default:
			g2D.drawString(text, 0, getVerticalAlignment());
			break;
		}
		// If we have a debug color, highlight our area
		if (getDebugColor() != null) {
			g2D.setPaint(getDebugColor());
			g2D.drawRect(0, 0, getWidth() - 1, getHeight() - 1);
		}
	}
}
