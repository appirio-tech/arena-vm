// POPS - 10/18/2001 - moved jmaContestApplet.JVI to jmaContestApplet.editors.JVI
//   This functionality was split out of the JVI editor
package com.topcoder.client.contestApplet.editors.Standard;

/**
 * StandardEditor.java
 *
 * Created on August 30, 2000
 */

import java.util.Observer;
import java.util.Observable;

import javax.swing.*;

import java.awt.*;
import com.topcoder.client.contestApplet.common.LocalPreferences;
import com.topcoder.shared.language.Language;

/**
 * Standard Editor
 *
 * @author Tim "Pops" Roberts
 * @version 1.0
 */
public class StandardEditor extends JTextPane implements Observer {

    private LocalPreferences localPref = LocalPreferences.getInstance();
    private Language language;
    
    /**
     * Constructor to initialize the editor. (Using this constructor the editor
     * will be resizable).
     *
     * @param string    initialize the buffer with the following string.
     */
    ////////////////////////////////////////////////////////////////////////////////
    public StandardEditor(String string)
    ////////////////////////////////////////////////////////////////////////////////
    {
        super();
        init(string);
    }

    public void init(String text) {
        setText(text);
        setCaret(new StandardCaret());
        //setFont(new Font("Courier", Font.PLAIN, 12));
        setFont(new Font(localPref.getFont(LocalPreferences.EDSTDFONT), Font.PLAIN, localPref.getFontSize(LocalPreferences.EDSTDFONTSIZE)));
        setMargin(new Insets(5, 5, 5, 5));
        setSelectedTextColor(localPref.getColor(LocalPreferences.EDSTDSELT));
        setSelectionColor(localPref.getColor(LocalPreferences.EDSTDSELB));
        //setCaretColor(Common.FG_COLOR);
        //setForeground(Common.FG_COLOR);
        //setBackground(Common.BG_COLOR);
        setCaretColor(localPref.getColor(LocalPreferences.EDSTDFORE));
        setForeground(localPref.getColor(LocalPreferences.EDSTDFORE));
        setBackground(localPref.getColor(LocalPreferences.EDSTDBACK));
    }

    /**
     * Overwrite to set the caret position after setting the text
     *
     * @param text    the user text input to replace the current buffer.
     */
    ////////////////////////////////////////////////////////////////////////////////
    public void setText(String text)
    ////////////////////////////////////////////////////////////////////////////////
    { 
        super.setText(text);
        setCaretPosition(0);
    }
    
    public void setLanguage(Language language) {
        this.language = language;
    }
    
    public Language getLanguage() {
        return language;
    }

    ////////////////////////////////////////////////////////////////////////////////
    public void update(Observable o, Object arg)
            ////////////////////////////////////////////////////////////////////////////////
    {
        // This function will update the color/font scheme as notified by localPref
        setFont(new Font(localPref.getFont(LocalPreferences.EDSTDFONT), Font.PLAIN, localPref.getFontSize(LocalPreferences.EDSTDFONTSIZE)));
        setSelectedTextColor(localPref.getColor(LocalPreferences.EDSTDSELT));
        setSelectionColor(localPref.getColor(LocalPreferences.EDSTDSELB));
        setCaretColor(localPref.getColor(LocalPreferences.EDSTDFORE));
        setForeground(localPref.getColor(LocalPreferences.EDSTDFORE));
        setBackground(localPref.getColor(LocalPreferences.EDSTDBACK));
    }
}

