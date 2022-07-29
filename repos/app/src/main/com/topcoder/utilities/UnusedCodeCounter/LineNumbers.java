/*
 * LineNumbers.java
 *
 * Created on December 1, 2004, 4:23 PM
 */

package com.topcoder.utilities.UnusedCodeCounter;

import java.beans.*;
import java.io.Serializable;

import javax.swing.*;
import javax.swing.event.*;
import javax.swing.text.*;
import java.awt.*;
import java.awt.event.*;
import java.util.*;
import java.beans.*;

/**
 * @author rfairfax
 */
public class LineNumbers extends JComponent implements DocumentListener {
    
    
    private PropertyChangeSupport propertySupport;
    
    private JTextArea textarea;
    
    public LineNumbers(JTextArea textarea) {
        propertySupport = new PropertyChangeSupport(this);
        this.textarea = textarea;
        textarea.getDocument().addDocumentListener(this);
    }
    
    private int lineWidth = 0;
    private int offset = 0;
    private int lines = 0;
    
    public void addPropertyChangeListener(PropertyChangeListener listener) {
        propertySupport.addPropertyChangeListener(listener);
    }
    
    public void removePropertyChangeListener(PropertyChangeListener listener) {
        propertySupport.removePropertyChangeListener(listener);
    }

    public void changedUpdate(DocumentEvent documentEvent) {
        try {
            String text = documentEvent.getDocument().getText(0, documentEvent.getDocument().getLength());

            int newlines = 0;
            int index = -1;
            do
            {
                    newlines++;
                    index = text.indexOf('\n', index+1);
            }
            while (index >= 0);
            if (newlines != lines)
            {
                    lines = newlines;
                    repaint();
            }
        } catch(Exception ex) {
            ex.printStackTrace();
        }
    }

    public void insertUpdate(DocumentEvent documentEvent) {
        try {
            String text = documentEvent.getDocument().getText(0, documentEvent.getDocument().getLength());

            int newlines = 0;
            int index = -1;
            do
            {
                    newlines++;
                    index = text.indexOf('\n', index+1);
            }
            while (index >= 0);
            if (newlines != lines)
            {
                    lines = newlines;
                    repaint();
            }
        } catch(Exception ex) {
            ex.printStackTrace();
        }
    }

    public void removeUpdate(DocumentEvent documentEvent) {
        try {
            String text = documentEvent.getDocument().getText(0, documentEvent.getDocument().getLength());

            int newlines = 0;
            int index = -1;
            do
            {
                    newlines++;
                    index = text.indexOf('\n', index+1);
            }
            while (index >= 0);
            if (newlines != lines)
            {
                    lines = newlines;
                    repaint();
            }
        } catch(Exception ex) {
            ex.printStackTrace();
        }
    }
    
    public void paint(Graphics g)
	{
		g.setFont(textarea.getFont().deriveFont(Font.BOLD));
		FontMetrics fm = g.getFontMetrics();
		if (lineWidth == 0)
		{
			lineWidth = fm.getHeight();
			offset = -fm.getDescent()/2;
		}
		g.setColor(getBackground());
		g.fillRect(0, 0, getWidth(), getHeight());
		g.setColor(textarea.getSelectionColor());
		int maxwidth = fm.stringWidth("000");
		for (int i=1; i<= lines; i++)
		{
			String str = Integer.toString(i);
			maxwidth = Math.max(maxwidth, fm.stringWidth(str));
                        g.setColor(getForeground());
			
                        g.drawString(str, getWidth()-fm.stringWidth(str)-3, i*lineWidth+offset);
		}
		g.setColor(getForeground());
		g.drawLine(maxwidth+2, 0, maxwidth+2, getHeight());
		g.drawLine(maxwidth+1, 0, maxwidth+1, getHeight());
		Dimension dim = getPreferredSize();
		if (dim.height != lineWidth*lines || dim.width != maxwidth+3)
		{
			setPreferredSize(new Dimension(maxwidth+3, lineWidth*lines));
			repaint();
		}
	}
}
