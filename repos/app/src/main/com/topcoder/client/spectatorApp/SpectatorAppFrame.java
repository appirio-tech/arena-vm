/**

 * SpectatorAppFrame.java

 *

 * Description:		The frame for spectator application

 * @author			Tim "Pops" Roberts

 * @version			2.0

 */
package com.topcoder.client.spectatorApp;

import java.awt.Color;
import java.awt.DisplayMode;
import java.awt.Frame;
import java.awt.Graphics2D;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.image.BufferStrategy;
import java.awt.image.BufferedImage;
import org.apache.log4j.Category;
import org.apache.log4j.Priority;
import com.topcoder.client.spectatorApp.controller.GUIController;
import com.topcoder.client.spectatorApp.views.AnimatePanel;
import com.topcoder.client.spectatorApp.views.InitialRenderer;

public class SpectatorAppFrame extends Frame {
	/** reference to the logging category */
	private static final Category cat = Category.getInstance(SpectatorAppFrame.class.getName());

	/** Target frames per second */
	public static final long FRAMESPERSECOND = Long.getLong("com.topcoder.client.spectatorApp.fps", 85).longValue();
	
	/** The amount of time we can spend per frame */
	private static final long MAXFRAMETIME = CommonRoutines.getCurrentTimeUnit() / FRAMESPERSECOND;
	
	/** Create the canvas to draw on */
	private ActiveRenderer renderer;

	/** The GUI Controller */
	private GUIController guiController = GUIController.getInstance();

	/** The static inital renderer */
	private AnimatePanel initialRenderer = new InitialRenderer();

	// private static AnimatePanel initialRenderer;
	// static {
	// String filename = "C:\\armageddon
	// software\\projects\\TopCoder\\MainTrunk\\app\\images\\com\\topcoder\\client\\spectatorApp\\finalist\\ambrose.jpg";
	// Image coderImage = null;
	// try {
	// // Create the file
	// File file = new File(filename);
	// if (!file.exists()) {
	// System.out.println("File " + filename + " does not exist");
	// }
	//
	// // Create an array to hold it
	// byte[] imageData = new byte[(int) file.length()];
	//
	//
	// // Read it into the array
	// new FileInputStream(file).read(imageData);
	//
	// // Verify its a good file
	// coderImage = Toolkit.getDefaultToolkit().createImage(imageData);
	// if (coderImage == null) {
	// System.out.println("The image " + filename + " does not appear to be a
	// valid image format (jpg, gif, png)");
	// }
	//
	// // Make double sure we loaded it
	// if(!CommonImageRoutines.loadImagesFully(new Image[] { coderImage })) {
	// System.out.println("The image " + filename + " could not be loaded for who
	// knows what reason");
	// }
	// } catch (IOException e) {
	// System.out.println(e);
	// }
	// AnnounceTCSCoderEvent evt = new AnnounceTCSCoderEvent(new Object(),
	// 1,
	// "CoderName",
	// "Student",
	// coderImage,
	// "handle",
	// 1547,
	// 1958,
	// 5,
	// 2934.44d,
	// 80,
	// .80d,
	// 10.2d,
	// 50,
	// 23,
	// 50.2d,
	// .23d,
	// 22);
	// initialRenderer = new TCSBioRenderer(evt, "Round1", "Contest 1");
	// }
	/** The graphics device */
	GraphicsDevice graphicsDevice;

	/** Default constructor */
	public SpectatorAppFrame() {
		// super(new Frame());
		super("Spectator Application");
		
		// Setup the frame
		setUndecorated(true);
		setResizable(false);
		setIgnoreRepaint(true);
		
		// Get the graphics environment
		graphicsDevice = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice();
		
		// Swap into full screen mode
		if (graphicsDevice.isFullScreenSupported()
					&& !Boolean.getBoolean("com.topcoder.client.spectatorApp.EmulatePlasma")) {
			graphicsDevice.setFullScreenWindow(this);
		} else {
			DisplayMode disp = graphicsDevice.getDisplayMode();
			setLocation(0, 0);
			setSize(disp.getWidth(), disp.getHeight());
			setSize(1365, 768);
		}
		
		// Create a transparent cursor
		setCursor(Toolkit.getDefaultToolkit().createCustomCursor(new BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB),
					new Point(0, 0), "Transparent"));
		
		// Create the active renderer
		renderer = new ActiveRenderer();
		
		// Show the window
		setVisible(true);
		
		// Create the buffer strategy
		int numBuffers = Integer.getInteger("com.topcoder.client.spectatorApp.NumBuffers", 2).intValue();
		createBufferStrategy(numBuffers);
		
		// Start up the animation
		renderer.startAnimation();
	}

	/**
	 * Disposes of any resources used
	 */
	public void dispose() {
		cat.debug("Disposing Frame");
		// Stop the animation thread
		renderer.stopAnimation();
		
		// Dispose of any
		renderer.dispose();
		
		// Swap out of full screen mode
		if (graphicsDevice.isFullScreenSupported()) graphicsDevice.setFullScreenWindow(null);
		
		// Dispose of the window
		super.dispose();
	}

	/** The canvas used to paint upon */
	public class ActiveRenderer extends Thread {
		/** Whether to keep going or not */
		boolean keepAlive = true;

		/** Constructor */
		public ActiveRenderer() {
			super("Active Rendering Thread");
			setDaemon(true);
			setPriority(MIN_PRIORITY);
		}

		/** Start the active rendering */
		public void startAnimation() {
			cat.debug("Starting animation");
			this.start();
		}

		/** Stop the active rendering */
		public void stopAnimation() {
			cat.debug("Stopping animation");
			keepAlive = false;
		}

		/** Dispose of any resources used */
		public void dispose() {
			keepAlive = false;
		}

		/**
		 * Active rendering loop
		 */
		public void run() {
			// Count of the frames that have been rendered
			int totalFramesRendered = 0;
			
			// The on screen graphics
			Graphics2D onScreenGraphics = null;
			
			// The time the frame was started
			long frameStartTime = CommonRoutines.getCurrentTime(), oldFrameStartTime = 0;
			long appStartTime = CommonRoutines.getCurrentTime();
			
			// Get the buffer strategy
			BufferStrategy buf = getBufferStrategy();
			
			// Get the animation manager
			AnimationManager animationManager = AnimationManager.getInstance();
		
			// Whether to show FPS or not
			boolean showFPS = Boolean.getBoolean("com.topcoder.client.spectatorApp.showFPS");
			while (keepAlive) {
				try {
					// Get the current start time
					oldFrameStartTime = frameStartTime;
					frameStartTime = CommonRoutines.getCurrentTime();

					// If painting is enabled..
					if (guiController.isPaintEnabled()) {
						// Get the panel to paint
						AnimatePanel currPanel = guiController.getPanel();
						if (currPanel == null)
							currPanel = initialRenderer;
						
						// If the panel is not the same size -
						if (getWidth() != currPanel.getWidth() || getHeight() != currPanel.getHeight()) {
							// Resize the panel
							currPanel.setSize(getWidth(), getHeight());
						}
						
						// Animate everything
						animationManager.animate(frameStartTime, frameStartTime - oldFrameStartTime);
						currPanel.animate(frameStartTime, frameStartTime - oldFrameStartTime);
						
						if (cat.isDebugEnabled()) {
							long now = CommonRoutines.getCurrentTime();
							cat.log(Priority.DEBUG, "Time from last frame: " + (frameStartTime-oldFrameStartTime));
							cat.log(Priority.DEBUG, "Frame Start Time: " + frameStartTime);
							cat.log(Priority.DEBUG, "Frame Render Time: " + (now - frameStartTime));
						}
						
						try {
							// Get the graphics2d
							onScreenGraphics = (Graphics2D) buf.getDrawGraphics();
							if (onScreenGraphics == null) {
								continue;
							}
							
							// Clear out the last representation
							onScreenGraphics.clearRect(0, 0, getWidth(), getHeight());
							
							// Render the graphics
							currPanel.render(onScreenGraphics);
							
							// Add to the frames that have been rendered
							totalFramesRendered++;
							
							// Output the frames per second
							if (showFPS && frameStartTime - appStartTime >= CommonRoutines.getCurrentTimeUnit()) {
								onScreenGraphics.setPaint(Color.blue);
								onScreenGraphics.setFont(getFont());
								onScreenGraphics.drawString("FPS: "
											+ (int) (totalFramesRendered / ((frameStartTime - appStartTime) / CommonRoutines.getCurrentTimeUnit())), 0,
											getFontMetrics(getFont()).getAscent());
							}
						} catch (Throwable t) {
							cat.error("Error in", t);
						} finally {
							if (onScreenGraphics != null)	onScreenGraphics.dispose();
							if (!buf.contentsLost()) buf.show();
						}
						
						// Find out how long it took
						long deltatime = CommonRoutines.getCurrentTime() - frameStartTime;
						while (deltatime < MAXFRAMETIME) {
							try {
								final long waitTime = CommonRoutines.convertToMillis(MAXFRAMETIME - deltatime);
								if (waitTime > 0 && waitTime < 1000) { // ensure we must wait and that wait isn't over 1 second
									Thread.sleep(waitTime);
								} else {
									if (waitTime > 0) { // zero wait is fine - means it was nano wait - we don't care!
										cat.info("Wait time is unacceptable: " + waitTime);
									}
									break;
								}
							} catch (Exception e) {
								cat.info("Exception occurred during sleep", e);
							}
							deltatime = CommonRoutines.getCurrentTime() - frameStartTime;
						}
					}
				} catch (OutOfMemoryError e) {
					cat.info("OutofMemory - running a full GC");
					System.gc();
				} catch (Throwable t) {
					cat.error("Error in rendering", t);
				}
			}
		}
	}
}
