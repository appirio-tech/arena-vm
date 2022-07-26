package com.topcoder.client.spectatorApp.widgets;

import java.awt.Color;
import java.awt.Graphics2D;
import com.topcoder.client.spectatorApp.views.VolatileImagePanel;

/**
 * Simply draws a line along either the TOP of the width
 * or the LEFT of the height
 * 
 * @author Tim Roberts
 * @version 1.0
 */
public class SLine extends VolatileImagePanel {

	/** Whether it's a vertical line or not */
	private boolean vertical;
	
	/** The color to draw it in */
	private Color color;
	
	/** Constructs a white vertical line */
	public SLine() {
		this(true);
	}
	
	/** 
	 * Constructs a vertical line
	 * @param color the color to draw the line in
	 */
	public SLine(Color color) {
		this(true, color);
	}
	
	/**
	 * Constructs a white line
	 * @param vertical whether it's vertical or not
	 */
	public SLine(boolean vertical) {
		this(vertical, Color.white);
	}
	
	/**
	 * Constructs a line
	 * @param vertical whether it's vertical or not
	 * @param color the color to draw the line in
	 */
	public SLine(boolean vertical, Color color) {
		this.vertical = vertical;
		this.color = color;
		setSize(1,1);
	}
	
	/** Overridden to keep the widht/height at one */
	public void setSize(int width, int height) {
		if(vertical) {
			super.setSize(1, height);
		} else {
			super.setSize(width, 1);
		}
		super.setVerticalAlignment(getWidth() / 2);
	}
	
	/** Draws the image */
	protected void drawImage(Graphics2D g2D) {
		g2D.setPaint(color);
		if(vertical) {
			g2D.drawLine(0,0,0,getHeight()-1);
		} else {
			g2D.drawLine(0,0,getWidth()-1,0);
		}
	}

}
