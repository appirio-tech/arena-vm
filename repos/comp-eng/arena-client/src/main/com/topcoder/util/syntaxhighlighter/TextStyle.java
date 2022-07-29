/*
 * Copyright (C) 2005 TopCoder Inc., All Rights Reserved.
 */
package com.topcoder.util.syntaxhighlighter;

import java.awt.Color;
import java.awt.Font;


/**
 * <p>
 * The TextStyle class provides a central style class that encapsulates the font, font color and the
 * background color. This is a simple container class. Each TextStyle instance has a corresponding name. The
 * elements of a TextStyle are not all required. This class corresponds to the style element in the XML
 * Configuration File/schema. Its attributes are a collection of
 * things like font face, size and foreground and background color.</p>
 * <p>Thread Safety: This class provides no mutable state and is thread safe.</p>
 *
 * @author duner, still
 * @version 2.0
 */
public class TextStyle {
    /**
     * <p>
     * Represents the font used to style text. This is set in the constructor and is not changed afterwards.
     * It may be null to signify that no font formatting should be applied. This value is accessible via the
     * getFont() accessor.
     * </p>
     *
     */
    private Font font;

    /**
     * <p>
     * Represents the foreground color used to style text. This is set in the constructor and is not changed
     * afterwards. It may be null to signify that no color formatting should be applied. This value is
     * accessible via the getColor() accessor.
     * </p>
     *
     */
    private Color color;

    /**
     * <p>
     * Represents the foreground background color used to style text. This is set in the constructor and is
     * not changed afterwards. It may be null to signify that no background color formatting should be
     * applied. This value is accessible via the getBGColor() accessor.
     * </p>
     *
     */
    private Color bgColor;

    /**
     * <p>
     * Represents the name of this TextStyle instance. This is set in the constructor and is not changed
     * afterwards. It may not be null.
     * </p>
     *
     */
    private String name;

    /**
     * <p>
     * Initializes a newly created TextStyle object with name, font, color,and bgColor.
     * </p>
     *
     *
     * @param name
     *            The name of the TextStyle instance.
     * @param font
     *            A Font instance used to describe the font. May be null.
     * @param color
     *            A Color instance used to describe the foreground color. May be null.
     * @param bgColor
     *            A Color instance used to describe the background Color. May be null.
     * @throws NullPointerException
     *             if name is null.
     * @throws IllegalArgumentException
     *             if (trimmed) name is an empty string.
     */
    public TextStyle(String name, Font font, Color color, Color bgColor) {
        SHHelper.checkString(name, "name");

        this.name = name;
        this.font = font;
        this.color = color;
        this.bgColor = bgColor;
    }

    /**
     * <p>
     * Get the font attribute.
     * </p>
     *
     *
     * @return the font attribute.
     */
    public Font getFont() {
        return font;
    }

    /**
     * <p>
     * Get the color attribute.
     * </p>
     *
     * @return the color attribute.
     */
    public Color getColor() {
        return color;
    }

    /**
     * <p>
     * Get the bgColor attribute.
     * </p>
     *
     *
     * @return the bgColor attribute.
     */
    public Color getBGColor() {
        return bgColor;
    }

    /**
     * <p>
     * Get the name attribute.
     * </p>
     *
     * @return the name attribute.
     */
    public String getName() {
        return name;
    }
    
    /**
     * Sets the background color of this text style.
     * 
     * @param bgColor the background color.
     */
    public void setBGColor(Color bgColor) {
        this.bgColor = bgColor;
    }
    
    /**
     * Sets the text color of this text style.
     * 
     * @param color the text color.
     */
    public void setColor(Color color) {
        this.color = color;
    }
    
    /**
     * Sets the font of this text style.
     * 
     * @param font the font of this text style.
     */
    public void setFont(Font font) {
        this.font = font;
    }
}
