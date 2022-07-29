/**
 * LogoRenderer.java
 *
 * Description:		The panel that displays the logo
 * @author			Tim "Pops" Roberts
 * @version			1.0
 */

package com.topcoder.client.spectatorApp.views;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.TexturePaint;
import java.awt.Toolkit;
import java.awt.Transparency;
import java.awt.image.BufferedImage;

import org.apache.log4j.Category;

import com.topcoder.client.spectatorApp.CommonRoutines;
import com.topcoder.client.spectatorApp.FontFactory;
import com.topcoder.client.spectatorApp.SpectatorApp;


public class LogoRenderer extends VolatileImagePanel implements AnimatePanel {

    /** Reference to the logging category */
    private static final Category cat = Category.getInstance(LogoRenderer.class.getName());

    /** Font used */
    private Font titleFont = FontFactory.getInstance().getFont(FontFactory.FUTURA_HEAVYOBLIQUE, Font.PLAIN, 48);

    /** Font used */
    private Font trailerFont = FontFactory.getInstance().getFont(FontFactory.FUTURA_CONDENSED, Font.PLAIN, 42);

    /** Font metrics */
    private FontMetrics titleFontFM;

    /** Font metrics */
    private FontMetrics trailerFontFM;

    /** Color of the text */
    private Color titleColor = Color.white;

    /** The logo */
    private Image logo;

    /** The room title */
    private String roomTitle;

    /** The sponsor */
    private String sponsor;

    /** The vertical margin */
    private int marginVertical = 5;

    /** The horizontal margin */
    private int marginHorizontal = 20;

    /** The height of the header */
    private int headerHeight;

    /** The height of the trailer */
    private int trailerHeight;

    /** The logo image */
    private Image sponsorLogo;

	/** The background texture */
	private BufferedImage backImage;

    /** Constructs the trailer panel */
    public LogoRenderer(String roomTitle, String sponsor, Image logo, Image sponsorLogo) {
        this.logo = logo;
        this.sponsorLogo = sponsorLogo;
        this.roomTitle = roomTitle;
        this.sponsor = sponsor;

        // Get the background tile
        Image background = Toolkit.getDefaultToolkit().getImage(SpectatorApp.class.getResource("black.gif"));

        // Load the images
        if (!CommonRoutines.loadImagesFully(new Image[]{background})) {
            System.out.println("Error loading the images");
            return;
        }

        // Get the font metrics
        titleFontFM = CommonRoutines.getFontMetrics(titleFont);
        trailerFontFM = CommonRoutines.getFontMetrics(trailerFont);

        // Calculate the height of the header/trailer
        headerHeight = titleFontFM.getAscent() + titleFontFM.getDescent() + (marginVertical * 2);
        trailerHeight = Math.max(headerHeight, (sponsorLogo==null ? 0 : sponsorLogo.getHeight(null)) + (marginVertical * 2));

		// Convert to Buffered Images
		backImage = CommonRoutines.createBufferedImage(background, Transparency.OPAQUE);

    }

    /**
     * Draw the image
     */
    protected void drawImage(Graphics2D g2D) {
        // Copy the height to a local var
        int rheight = getHeight();

        // Setup antialiasing
        g2D.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

        // Fill the header
        g2D.setPaint(Color.black);
        g2D.fillRect(0, 0, getWidth(), headerHeight);

        g2D.setPaint(titleColor);
        g2D.setFont(titleFont);
        g2D.drawString(roomTitle, marginHorizontal, marginVertical + titleFontFM.getAscent());
        g2D.translate(0, headerHeight);
        rheight -= headerHeight;

        // Fill the trailer
        g2D.translate(0, rheight - trailerHeight);
        g2D.setPaint(Color.black);
        g2D.fillRect(0, 0, getWidth(), trailerHeight);
        g2D.setPaint(titleColor);
        g2D.setFont(trailerFont);

        // Find the centerpoint
        // int padding = (getWidth() / 2) - (((logo==null ? 0 : logo.getWidth(null) + 15 ) + trailerFontFM.stringWidth(sponsor)) / 2);
        int padding = (getWidth() / 2 ) - (trailerFontFM.stringWidth(sponsor) / 2);

        //g2D.drawString(s0ponsor, getWidth() - marginHorizontal - trailerFontFM.stringWidth(sponsor) - (sponsorLogo==null ? 0 : sponsorLogo.getWidth(null)), (trailerHeight / 2) - ((trailerFontFM.getAscent() + trailerFontFM.getDescent()) / 2) + trailerFontFM.getAscent());
        //g2D.drawString(sponsor, padding, (trailerHeight / 2) - ((trailerFontFM.getAscent() + trailerFontFM.getDescent()) / 2) + trailerFontFM.getAscent());
        if(sponsorLogo!=null) g2D.drawImage(sponsorLogo, getWidth() - marginHorizontal - sponsorLogo.getWidth(null), (trailerHeight / 2) - (sponsorLogo.getHeight(null) / 2), null);
        g2D.translate(0, -(rheight - trailerHeight));
        rheight -= trailerHeight;

        // Fill in the body
        g2D.setPaint(new TexturePaint(backImage, new Rectangle(0, 0, backImage.getWidth(), backImage.getHeight())));
        g2D.fillRect(0, 0, getWidth(), rheight);

        // Draw the image centered
        g2D.drawImage(logo, (getWidth() / 2) - (logo.getWidth(null) / 2), (rheight / 2) - (logo.getHeight(null) / 2), null);
    }

}
