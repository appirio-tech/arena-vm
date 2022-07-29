package com.topcoder.client.spectatorApp.views;

import java.awt.Composite;
import java.awt.Graphics2D;
import java.awt.Paint;
import java.awt.RenderingHints;
import java.awt.Stroke;
import java.awt.geom.AffineTransform;
import java.util.ArrayList;
import org.apache.log4j.Logger;

/**
 * A layered panel. This class allows multiple panels to be created and painted
 * in order (providing a full image eventually)
 * 
 * @author Tim 'Pops' Roberts
 */
public class LayeredPanel extends AbstractAnimatePanel {
	/** reference to the logging category */
	private static final Logger cat = Logger.getLogger(LayeredPanel.class.getName());

	/** The layers */
	private ArrayList layers = new ArrayList();

	/** Filters */
	private ArrayList filters = new ArrayList();

	/** Constructor of the panel */
	public LayeredPanel() {}

	/** Constructor of the panel */
	public LayeredPanel(AnimatePanel[] layers) {
		for (int x = 0; x < layers.length; x++)
			this.layers.add(layers[x]);
	}

	/** Adds a panel with no filter */
	public void addLayer(AnimatePanel layer) {
		// if(cat.isDebugEnabled()) cat.debug("Adding Layer " + layer);
		addLayer(layer, null);
	}

	/** Adds a panel with no filter */
	public void addLayer(int pos, AnimatePanel layer) {
		// if(cat.isDebugEnabled()) cat.debug("Adding Layer (" + pos + ") " +
		// layer);
		addLayer(pos, layer, null);
	}

	/** Adds a panel with a filter */
	public void addLayer(AnimatePanel layer, LayeredFilter filter) {
		// if(cat.isDebugEnabled()) cat.debug("Adding Layer " + layer + " with
		// filter " + filter);
		layers.add(layer);
		filters.add(filter);
	}

	/** Adds a panel with a filter */
	public void addLayer(int pos, AnimatePanel layer, LayeredFilter filter) {
		// if(cat.isDebugEnabled()) cat.debug("Adding Layer (" + pos + ") " +
		// layer + " with filter " + filter);
		layers.add(pos, layer);
		filters.add(pos, filter);
	}

	/** Replace a layer with another */
	public AnimatePanel setLayer(int pos, AnimatePanel layer) {
		// if(cat.isDebugEnabled()) cat.debug("Setting Layer (" + pos + ") " +
		// layer);
		return setLayer(pos, layer, null);
	}

	/** Replace a layer with another */
	public AnimatePanel setLayer(int pos, AnimatePanel layer, LayeredFilter filter) {
		// if(cat.isDebugEnabled()) cat.debug("Setting Layer (" + pos + ") " +
		// layer + " with filter " + filter);
		// Create the layers up to the position if they don't exist
		while (layers.size() <= pos) {
			layers.add(null);
			filters.add(null);
		}
		// Set and return
		AnimatePanel panel = (AnimatePanel) layers.set(pos, layer);
		filters.set(pos, filter);
		return panel;
	}

	/** Set the filter for the given layer */
	public void setFilter(AnimatePanel layer, LayeredFilter filter) {
		// if(cat.isDebugEnabled()) cat.debug("Setting Filter " + filter + " for
		// layer " + layer);
		for (int x = layers.size() - 1; x >= 0; x--) {
			if (layer == layers.get(x)) {
				setLayer(x, layer, filter);
				return;
			}
		}
	}

	/** Replace a layer with another */
	public AnimatePanel getLayer(int pos) {
		return (AnimatePanel) layers.get(pos);
	}

	/** Replace a layer with another */
	public AnimatePanel[] getLayers() {
		return (AnimatePanel[]) layers.toArray(new AnimatePanel[0]);
	}

	/** Removes a panel */
	public AnimatePanel removeLayer(int pos) {
		// if(cat.isDebugEnabled()) cat.debug("Removing layer (" + pos + ")");
		filters.remove(pos);
		return (AnimatePanel) layers.remove(pos);
	}

	/** Removes ALL panels */
	public void clear() {
		// if(cat.isDebugEnabled()) cat.debug("clear");
		filters.clear();
		layers.clear();
	}

	/** Disposes of any resources for the panel */
	public void dispose() {
		super.dispose();
		for (int x = layers.size() - 1; x >= 0; x--) {
			AnimatePanel panel = (AnimatePanel) layers.get(x);
			if (panel != null) panel.dispose();
		}
	}

	/** Animates the panel */
	public void animate(long now, long diff) {
		super.animate(now, diff);
		for (int x = layers.size() - 1; x >= 0; x--) {
			AnimatePanel panel = (AnimatePanel) layers.get(x);
			if (panel != null) panel.animate(now, diff);
		}
	}

	/** Renders the panel incrementally or fully - up to the panel */
	public void render(Graphics2D g2D) {
		// Store the original transform
		AffineTransform orig = g2D.getTransform();
		
		// Translate by the position of this panel
		g2D.translate(getX(), getY());
		
		// Draw all the panels in order
		int max = layers.size();
		for (int x = 0; x < max; x++) {
			// Ignore any null layers
			if (layers.get(x) == null) continue;
			
			// Save current setup
			Composite comp = g2D.getComposite();
			AffineTransform trans = g2D.getTransform();
			RenderingHints hints = g2D.getRenderingHints();
			Paint paint = g2D.getPaint();
			Stroke stroke = g2D.getStroke();
			
			// Apply filter
			if (filters.get(x) != null) {
				// //if(cat.isDebugEnabled()) cat.debug("Applying filter " +
				// filters.get(x));
				((LayeredFilter) filters.get(x)).filter(g2D);
			}
			
			// Render the panel
			// //if(cat.isDebugEnabled()) cat.debug("Rendering " + layers.get(x));
			((AnimatePanel) layers.get(x)).render(g2D);
			
			// Restore
			g2D.setComposite(comp);
			g2D.setTransform(trans);
			g2D.setRenderingHints(hints);
			g2D.setPaint(paint);
			g2D.setStroke(stroke);
		}
		// Translate back
		g2D.setTransform(orig);
	}

	public interface LayeredFilter {
		public void filter(Graphics2D g2D);
	}
}
