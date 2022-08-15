package com.topcoder.client.spectatorApp;

import java.awt.Color;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Tim "Pops" Roberts
 * @version 1.0
 */
public class ColorProperty {
	private final static Map colorMap = new HashMap();
	
	private ColorProperty() {
		
	}
	
	public final static Color getColor(String name) {
		if(!colorMap.containsKey(name)) loadColor(name);
		return (Color)colorMap.get(name);
	}
	
	private final static void loadColor(String name) {
		String colorName = System.getProperty(name, null);
		if(colorName==null) {
			colorMap.put(name, null);
		} else {
			try {
				int colorInt = Integer.parseInt(colorName);
				colorMap.put(name, new Color(colorInt));
			} catch (NumberFormatException e) {
				Color color=null;
				if(colorName.equalsIgnoreCase("black")) color=Color.BLACK;
				else if(colorName.equalsIgnoreCase("blue")) color=Color.BLUE;
				else if(colorName.equalsIgnoreCase("cyan")) color=Color.CYAN;
				else if(colorName.equalsIgnoreCase("darkgray")) color=Color.DARK_GRAY;
				else if(colorName.equalsIgnoreCase("dark_gray")) color=Color.DARK_GRAY;
				else if(colorName.equalsIgnoreCase("gray")) color=Color.GRAY;
				else if(colorName.equalsIgnoreCase("green")) color=Color.GREEN;
				else if(colorName.equalsIgnoreCase("lightgray")) color=Color.LIGHT_GRAY;
				else if(colorName.equalsIgnoreCase("light_gray")) color=Color.LIGHT_GRAY;
				else if(colorName.equalsIgnoreCase("magenta")) color=Color.MAGENTA;
				else if(colorName.equalsIgnoreCase("orange")) color=Color.ORANGE;
				else if(colorName.equalsIgnoreCase("pink")) color=Color.PINK;
				else if(colorName.equalsIgnoreCase("red")) color=Color.RED;
				else if(colorName.equalsIgnoreCase("white")) color=Color.WHITE;
				else if(colorName.equalsIgnoreCase("yellow")) color=Color.YELLOW;
				colorMap.put(name, color);
			}
				
		}
	}
}
