package com.topcoder.client.spectatorApp.views;
import java.awt.Graphics2D;

public class BlankPanel extends BufferedImagePanel {
    public BlankPanel() {
    }
    
    public BlankPanel(int width, int height) {
        setSize(width, height);
    }
    
    protected void drawImage(Graphics2D g2d) {
    }
}
