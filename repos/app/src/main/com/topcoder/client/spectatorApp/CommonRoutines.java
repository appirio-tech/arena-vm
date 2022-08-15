/**
 * CommonImageRoutines.java
 *
 * Description:		Common manipulations for images
 * @author			Tim "Pops" Roberts
 * @version			1.0
 */
package com.topcoder.client.spectatorApp;

import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.GraphicsEnvironment;
import java.awt.Image;
import java.awt.MediaTracker;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.Transparency;
import java.awt.font.FontRenderContext;
import java.awt.font.TextLayout;
import java.awt.image.BufferedImage;
import java.awt.image.ConvolveOp;
import java.awt.image.Kernel;
import java.awt.image.VolatileImage;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;
import javax.swing.JLabel;
import org.apache.log4j.Category;

public class CommonRoutines {
	/** reference to the logging category */
	private static final Category cat = Category.getInstance(CommonRoutines.class.getName());

	/**
	 * Creates an offscreen buffer the is compatible with the current display
	 * device and is Volatile
	 * 
	 * @param width
	 *           the width of the offscreen buffer
	 * @param height
	 *           the height of the offscreen buffer
	 * @returns a volatile image
	 */
	public static final VolatileImage createVolatileImage(int width, int height) {
		return GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice().getDefaultConfiguration()
					.createCompatibleVolatileImage(width, height);
	}

	/**
	 * Creates an offscreen buffer the is compatible with the current display
	 * device (the transparency will be Transparency.OPAQUE)
	 * 
	 * @param width
	 *           the width of the offscreen buffer
	 * @param height
	 *           the height of the offscreen buffer
	 * @returns a buffered image
	 */
	public static final BufferedImage createOffScreenBuffer(int width, int height) {
		return createOffScreenBuffer(width, height, Transparency.OPAQUE);
	}

	/**
	 * Creates an offscreen buffer the is compatible with the current display
	 * device
	 * 
	 * @param width
	 *           the width of the offscreen buffer
	 * @param height
	 *           the height of the offscreen buffer
	 * @param type
	 *           the type of transparency (Transparency.OPAQUE ...)
	 * @returns a buffered image
	 */
	public static final BufferedImage createOffScreenBuffer(int width, int height, int type) {
		// Get's the most compatible buffer for the current device
		return GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice().getDefaultConfiguration()
					.createCompatibleImage(width, height, type);
	}

	/**
	 * Creates a buffered image out of an image. The buffered image will have the
	 * same width and height of the passed image and a ARGB color model.
	 * 
	 * @param img
	 *           the image convert (must be fully loaded!)
	 * @returns the buffered image
	 */
	public static final BufferedImage createBufferedImage(Image img) {
		return CommonRoutines.createBufferedImage(img, img.getWidth(null), img.getHeight(null), Transparency.TRANSLUCENT);
	}

	/**
	 * Creates a buffered image out of an image. The buffered image will have the
	 * same width and height of the passed image.
	 * 
	 * @param img
	 *           the image convert (must be fully loaded!)
	 * @param type
	 *           the type of color model to use
	 * @returns the buffered image
	 */
	public static final BufferedImage createBufferedImage(Image img, int type) {
		return CommonRoutines.createBufferedImage(img, img.getWidth(null), img.getHeight(null), type);
	}

	/**
	 * Creates a buffered image out of an image
	 * 
	 * @param img
	 *           the image convert (must be fully loaded!)
	 * @param width
	 *           the width of the buffered image to create
	 * @param height
	 *           the height of the buffered image to create
	 * @param type
	 *           the type of color model to use
	 * @returns the buffered image
	 */
	public static final BufferedImage createBufferedImage(Image img, int width, int height, int type) {
		// Create a buffered image with the same size of the image
		// (Note: image must be fully loaded)
		BufferedImage temp = createOffScreenBuffer(width, height, type);
		// Get a handle to the graphics to draw on
		Graphics2D tempGraphics = temp.createGraphics();
		// Draw the passed image onto the buffered image
		tempGraphics.drawImage(img, 0, 0, width, height, null);
		// Dispose the graphics object
		tempGraphics.dispose();
		// Return the bufferedImage
		return temp;
	}

	/**
	 * Loads the specified images fully
	 * 
	 * @param imgs
	 *           the images to load
	 * @returns whether they were loaded correctly or an error occurred
	 */
	public static final boolean loadImagesFully(Image[] imgs) {
		// Create a media tracker to load images with
		MediaTracker mediaTracker = new MediaTracker(new JLabel());
		// Add each of the images to load
		for (int x = 0; x < imgs.length; x++) {
			if (imgs[x] == null) {
				cat.error("Image file was null!");
				continue;
			}
			mediaTracker.addImage(imgs[x], x);
		}
		// Wait until they are loaded
		try {
			mediaTracker.waitForAll();
		} catch (Throwable t) {
			return false;
		}
		// Verify that they are loaded
		if (mediaTracker.statusAll(false) != MediaTracker.COMPLETE) return false;
		// We loaded them
		return true;
	}

	/**
	 * Gets the screen dimensions
	 * 
	 * @returns the screen dimensions
	 */
	public static final Dimension getScreenDimension() {
		Rectangle r = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice()
					.getDefaultConfiguration().getBounds();
		return new Dimension((int) r.getWidth(), (int) r.getHeight());
	}

	/**
	 * Blurs an image by combining values of a certain radius of pixels
	 * 
	 * @param imageToBlur
	 *           the image to blur
	 * @param radius
	 *           the radius around each pixel to evaluate
	 * @returns the image blurred
	 */
	public static final BufferedImage blurImage(BufferedImage imageToBlur, int radius) {
		Kernel kernel = new Kernel(2 * radius + 1, 2 * radius + 1, getGaussianKernel(radius));
		return new ConvolveOp(kernel).filter(imageToBlur, null);
	}

	/**
	 * Creates a gaussian array pased on the passed radius
	 * 
	 * @returns kernel for a Gaussian blur convolve
	 */
	private static float[] getGaussianKernel(int radius) {
		float kernel[] = new float[(radius * 2 + 1) * (radius * 2 + 1)];
		double sum = 0.;
		int w = 2 * radius + 1;
		double deviation = radius / 3.; // This guarantees non zero values in the
													// kernel
		double devSqr2 = 2 * Math.pow(deviation, 2);
		double piDevSqr2 = Math.PI * devSqr2;
		for (int i = 0; i < w; i++) {
			for (int j = 0; j < w; j++) {
				kernel[i * w + j] = (float) (Math.pow(Math.E, -((j - radius) * (j - radius) + (i - radius) * (i - radius))
							/ devSqr2) / piDevSqr2);
				sum += kernel[i * w + j];
			}
		}
		// Make elements sum to 1
		for (int i = 0; i < w; i++) {
			for (int j = 0; j < w; j++) {
				kernel[i * w + j] /= sum;
			}
		}
		return (kernel);
	}

	/**
	 * Get's the font metrics for a font for the current display device.
	 * 
	 * @param font
	 *           the font to get the font metrics for
	 * @return the font metrics for the font
	 */
	public static final FontMetrics getFontMetrics(Font font) {
		Graphics2D g2d = null;
		try {
			g2d = createOffScreenBuffer(1, 1).createGraphics();
			return g2d.getFontMetrics(font);
		} finally {
			if (g2d != null) g2d.dispose();
		}
	}

	/**
	 * Get's the font metrics for a font for the current display device.
	 * 
	 * @param font
	 *           the font to get the font metrics for
	 * @return the font metrics for the font
	 */
	public static final FontRenderContext getFontRenderContext(boolean antialias) {
		Graphics2D g2d = null;
		try {
			g2d = createOffScreenBuffer(1, 1).createGraphics();
			if (antialias) {
				g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
			}
			return g2d.getFontRenderContext();
		} finally {
			if (g2d != null) g2d.dispose();
		}
	}

	/**
	 * Returns the string version of a rank
	 */
	private static final String[] RANKS = { "", "st", "nd", "rd", "th" };

	public static final String getRank(int rank) {
		if (rank < 0) return "";
		return String.valueOf(rank) + (rank >= RANKS.length ? RANKS[RANKS.length - 1] : RANKS[rank]);
	}

	/** Returns the current time */
	private static Long adj = null; // tracks whether we artificially add a floor to the time
	public static long getCurrentTime()
	{
		long time = System.nanoTime();
		
		if (adj == null) {
			adj = time < 0 ? new Long(-time + 1) : new Long(0);
		}
		
		time += adj.longValue();
		return time;
	}
	
	/** Returns the current time unit (in 1 second) */
	public static long getCurrentTimeUnit()
	{
		return 1000000000;
	}
	
	/** Returns the amount of time per seconds */
	public static long calcTimePerSecond(double seconds)
	{
		return (long) (seconds * getCurrentTimeUnit());
	}
	
	/** Returns the milliseconds in the time unit */
	public static long convertToMillis(long timeUnit)
	{
		return (long)Math.ceil(timeUnit / 1000000);
	}
	
	/** Creates an (int) rectangle from a text layout boundry (width/height only) */
	public static Rectangle getIntRectangle(TextLayout t)
	{
		//int x = (int) Math.ceil(t.getBounds().getX());
		//int y = (int) Math.ceil(t.getBounds().getY());
		int w = (int) Math.ceil(t.getBounds().getWidth());
		int h = (int) Math.ceil(t.getBounds().getHeight());
		return new Rectangle(0, 0, w, h);
	}	
	
	public static int[] insertElement(int[] array, int idx, int value)
	{
		int[] newArray = new int[array.length+1];
		idx = Math.min(idx, newArray.length); 
		
		if (idx > 0) {
			System.arraycopy(array, 0, newArray, 0, idx);
		}
		
		newArray[idx] = value;
		
		if (idx < array.length) {
			System.arraycopy(array, idx, newArray, idx+1, array.length-idx);
		}
		return newArray;
	}
	
	public static String[] insertElement(String[] array, int idx, String value)
	{
		String[] newArray = new String[array.length+1];
		idx = Math.min(idx, newArray.length); 
		
		if (idx > 0) {
			System.arraycopy(array, 0, newArray, 0, idx);
		}
		
		newArray[idx] = value;
		
		if (idx < array.length) {
			System.arraycopy(array, idx, newArray, idx+1, array.length-idx);
		}
		return newArray;
	}
	
	/** Pretty print an array - assumes array is sorted */
	public static String prettyPrint(int[] a)
	{
		StringBuffer b = new StringBuffer();
		if (a.length > 0) {
			int start = a[0];
			int temp = start;
			for(int x = 1; x <= a.length; x++) {
				int curr = x == a.length ? Integer.MAX_VALUE : a[x];
				if (curr == temp) {
					continue;
				} else if (curr == temp + 1) {
					temp = curr;
					continue;
				} else {
					if (temp == start) {
						if (b.length() > 0) b.append(",");
						b.append(start);
					} else if (temp == start + 1) {
						if (b.length() > 0) b.append(",");
						b.append(start);						
						b.append(",");						
						b.append(temp);						
					} else {
						if (b.length() > 0) b.append(",");
						b.append(start);
						b.append('-');
						b.append(temp);
					}
					start = temp = curr;
				}
			}
		}	
		return b.toString();
	}
	
	/** Pretty print an array - assumes array is sorted */
	public static String prettyPrint(long[] a)
	{
		StringBuffer b = new StringBuffer();
		if (a.length > 0) {
			long start = a[0];
			long temp = start;
			for(int x = 1; x <= a.length; x++) {
				long curr = x == a.length ? Long.MAX_VALUE : a[x];
				if (curr == temp) {
					continue;
				} else if (curr == temp + 1) {
					temp = curr;
					continue;
				} else {
					if (temp == start) {
						if (b.length() > 0) b.append(",");
						b.append(start);
					} else if (temp == start + 1) {
						if (b.length() > 0) b.append(",");
						b.append(start);						
						b.append(",");						
						b.append(temp);						
					} else {
						if (b.length() > 0) b.append(",");
						b.append(start);
						b.append('-');
						b.append(temp);
					}
					start = temp = curr;
				}
			}
		}	
		return b.toString();
	}

	public static int[] parseToIntArray(String text) throws NumberFormatException {
		List<Integer> placements = new ArrayList<Integer>();
		StringTokenizer str = new StringTokenizer(text, ", ");
		
		while(str.hasMoreTokens()) {
			String token = str.nextToken();
			int idx = token.indexOf('-');
			if (idx < 0) {
				placements.add(new Integer(token));
			} else {
				int left = new Integer(token.substring(0, idx));
				int right = new Integer(token.substring(idx+1));
				for(int x = Math.min(left, right); x<=Math.max(left,right); x++) {
					placements.add(x);
				}
			}
		}
		
		int[] p = new int[placements.size()];
		int idx = 0;
		for(Integer i : placements) {
			p[idx++] = i;
		}

		return p;
	}
	
	public static long[] parseToLongArray(String text) throws NumberFormatException {
		List<Long> placements = new ArrayList<Long>();
		StringTokenizer str = new StringTokenizer(text, ", ");
		
		while(str.hasMoreTokens()) {
			String token = str.nextToken();
			int idx = token.indexOf('-');
			if (idx < 0) {
				placements.add(new Long(token));
			} else {
				long left = new Long(token.substring(0, idx));
				long right = new Long(token.substring(idx+1));
				for(long x = Math.min(left, right); x<=Math.max(left,right); x++) {
					placements.add(x);
				}
			}
		}
		
		long[] p = new long[placements.size()];
		int idx = 0;
		for(Long i : placements) {
			p[idx++] = i;
		}

		return p;
	}

}
