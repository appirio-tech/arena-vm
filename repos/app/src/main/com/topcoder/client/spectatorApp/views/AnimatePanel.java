/**
 * AnimatePanel.java
 *
 * Description:		Animation panel interface
 * @author			Tim "Pops" Roberts
 * @version			1.0
 */

package com.topcoder.client.spectatorApp.views;

import java.awt.Graphics2D;

public interface AnimatePanel {

    /** 
     * Returns the width of the panel
     * @return the width of the panel 
     */
    public int getWidth();

    /** 
     * Returns the height of the panel
     * @return the width of the panel 
     */
    public int getHeight();

    /** 
     * Returns the vertical alignment for the panel.  
     * @return the vertical alignment value for the panel.  Returns -1 if not applicable 
     */
    public int getVerticalAlignment();

    /** 
     * Sets the size of the panel
     * @param width the width of the panel
     * @param height the height of the panel 
     */
    public void setSize(int width, int height);

	/** 
	 * Returns the horizontal position of the panel
	 * @return the horizontal position of the panel 
	 */
	public int getX();

	/** 
	 * Returns the vertical position of the panel
	 * @return the vertical position of the panel 
	 */
	public int getY();

	/** 
	 * Sets the position of the panel
	 * @param x the horizontal position of the panel
	 * @param y the vertical position of the panel 
	 */
	public void setPosition(int x, int y);

	/** 
	 * Sets the position/size of this panel like anothers
	 * @param panel the panel to copy
	 */
	public void setLike(AnimatePanel panel);

    /** 
     * Disposes of any resources for the panel 
     */
    public void dispose();

    /** 
     * Animates the panel
     * @param newTime the new time point of the animation 
     */
    public void animate(long now, long diff);

    /** 
     * Renders the panel 
     * @param g2D the graphics2D object to render to
     */
    public void render(Graphics2D g2D);
}
