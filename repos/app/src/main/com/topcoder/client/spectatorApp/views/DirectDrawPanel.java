package com.topcoder.client.spectatorApp.views;

import java.awt.Composite;
import java.awt.Graphics2D;
import java.awt.Paint;
import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.Stroke;
import java.awt.geom.AffineTransform;

/**
 * Abstract panel that used for dynamic images that will be 
 * drawn each frame
 * @author Tim 'Pops' Roberts
 */
public abstract class DirectDrawPanel extends AbstractAnimatePanel {

	/** 
	 * Empty constructor 
	 */
	public DirectDrawPanel() {
	}
	
	/** 
	 * Implementing class must provide the drawing for this image
	 * @param g2D the graphics2d to use
	 */
	protected abstract void drawImage(Graphics2D g2D);
	
	/** Renders this panel */
	public void render(Graphics2D g2D) {
		// Save current setup
		Composite comp = g2D.getComposite();
		AffineTransform trans = g2D.getTransform();
		RenderingHints hints = g2D.getRenderingHints();
		Paint paint = g2D.getPaint();
		Stroke stroke = g2D.getStroke();
		Shape clip = g2D.getClip();
		
		// Translate/draw
		g2D.translate(getX(), getY());
		drawImage(g2D);				
		//g2D.translate(-getX(), -getY());

		// Restore
		g2D.setComposite(comp);
		g2D.setTransform(trans);
		g2D.setRenderingHints(hints);
		g2D.setPaint(paint);
		g2D.setStroke(stroke);
		g2D.setClip(clip);
	}
}
