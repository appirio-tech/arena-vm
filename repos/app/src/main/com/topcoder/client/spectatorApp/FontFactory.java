/**

 * FontFactory.java

 *

 * Description:		Factory for fonts.

 * @author			Tim "Pops" Roberts

 * @version			1.0

 */



package com.topcoder.client.spectatorApp;



import java.awt.Font;

import java.util.HashMap;

import java.util.Iterator;



public class FontFactory {



    /** The singleton font factory */

    private static FontFactory fontFactory = null;



    /** The cache of fonts */

    private HashMap fontMap = new HashMap(50);



    public final static String FUTURA_BOLD = "futurabold";

    public final static String FUTURA_BOLDOBLIQUE = "futuraboldoblique";

    public final static String FUTURA_CONDENSED = "futuracondensed";

    public final static String FUTURA_HEAVY = "futuraheavy";

    public final static String FUTURA_HEAVYOBLIQUE = "futuraheavyoblique";

    public final static String FUTURA_BOOK = "futurabook";

    public final static String FUTURA_BOOKOBLIQUE = "futurabookoblique";

    public final static String HANDEL_GOTHIC_BOLD = "handelgothicbold";


    /** Private constructor.  Use getInstance() instead. */

    private FontFactory() {

    }



    /**

     * Gets the instance of the font factory

     *

     * @returns the font factory

     */

    public synchronized static FontFactory getInstance() {

        if (fontFactory == null) fontFactory = new FontFactory();

        return fontFactory;

    }





    public Font getFont(String fontName, int fontStyle, int fontSize) {

        // Create the font key

        FontKey fontKey = new FontKey(fontName, fontStyle, fontSize);



        // Is the font cache'd - then return it

        Font cacheFont = (Font) fontMap.get(fontKey);

        if (cacheFont != null) return cacheFont;



        // Look for the existing font to derive from

        for (Iterator itr = fontMap.keySet().iterator(); itr.hasNext();) {

            // Get the key

            FontKey cacheKey = (FontKey) itr.next();



            // Same font name?

            if (cacheKey.fontName.equals(fontName)) {

                // If so, derive a new font from it

                cacheFont = ((Font) fontMap.get(cacheKey)).deriveFont(fontStyle, fontSize);



                // Put the derived font into the cache (with the original key)

                fontMap.put(fontKey, cacheFont);



                // Return the font

                return cacheFont;

            }

        }



        // Is it a font we load from a file

        String fileName = getFontFileName(fontName);



        // If not - do a new on it

        if (fileName == null) {

            cacheFont = new Font(fontName, fontStyle, fontSize);



            // If so, load it from the filename

        } else {

            try {

                cacheFont = Font.createFont(Font.TRUETYPE_FONT, getClass().getResourceAsStream(fileName)).deriveFont(fontStyle, fontSize);

            } catch (Throwable t) {

                System.out.println("Error loading font: ");

                t.printStackTrace();

                cacheFont = new Font(fontName, fontStyle, fontSize);

            }



        }



        // Put the font into the map

        fontMap.put(fontKey, cacheFont);



        // return it

        return cacheFont;



    }



    public String getFontFileName(String name) {

        if (name.equals(FUTURA_BOLD)) return "FUTUB___.TTF";

        if (name.equals(FUTURA_BOLDOBLIQUE)) return "FUTUBO__.TTF";

        if (name.equals(FUTURA_CONDENSED)) return "FUTUC___.TTF";

        if (name.equals(FUTURA_HEAVY)) return "FUTUH___.TTF";

        if (name.equals(FUTURA_HEAVYOBLIQUE)) return "FUTUHO__.TTF";

        if (name.equals(FUTURA_BOOK)) return "FUTUW___.TTF";

        if (name.equals(FUTURA_BOOKOBLIQUE)) return "FUTUWO__.TTF";
        
        if(name.equals(HANDEL_GOTHIC_BOLD)) return "HANDGOTB.TTF";

        return null;

    }





    /** The class used as a key to the fontMap */

    public class FontKey {



        /** Font name */

        public String fontName;



        /** Font Size */

        public int fontSize;



        /** Font style */

        public int fontStyle;



        /** Constructor */

        public FontKey(String fontName, int fontStyle, int fontSize) {

            this.fontName = fontName;

            this.fontStyle = fontStyle;

            this.fontSize = fontSize;

        }



        /** Equals implementation */

        public boolean equals(Object o) {

            if (o == null || !(o instanceof FontKey)) return false;

            FontKey other = (FontKey) o;

			return this.fontName != null && this.fontName.equals(other.fontName) && other.fontStyle == this.fontStyle
					&& other.fontSize == this.fontSize;

        }



        /** Hashcode implementation */

        public int hashCode() {

            return fontName.hashCode() + fontSize + fontStyle;

        }

    }



}





/* @(#)FontFactory.java */

