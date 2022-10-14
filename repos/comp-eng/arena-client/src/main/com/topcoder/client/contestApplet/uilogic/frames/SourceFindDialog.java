package com.topcoder.client.contestApplet.uilogic.frames;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ItemEvent;
import java.awt.event.KeyEvent;

import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.text.DefaultHighlighter;
import javax.swing.text.Highlighter;

import com.topcoder.client.contestApplet.ContestApplet;
import com.topcoder.client.contestApplet.common.LocalPreferences;
import com.topcoder.client.ui.UIComponent;
import com.topcoder.client.ui.UIPage;
import com.topcoder.client.ui.event.UIActionListener;
import com.topcoder.client.ui.event.UIItemListener;
import com.topcoder.client.ui.event.UIKeyAdapter;

public class SourceFindDialog extends UIKeyAdapter implements UIItemListener, UIActionListener, FrameLogic {
    private static final LocalPreferences localPref = LocalPreferences.getInstance();
    private UIPage page;
    private UIComponent frame;
    private UIComponent dial;
    private UIComponent target;
    private String text;
    private boolean backwardDir = false;
    private boolean ignoreCase = true;
    private boolean wrapAround = true;
    private boolean closeAfterFind = true;
    private boolean syntaxHighlight = false;
    private UIComponent findField;
    private UIComponent findOption;
    private UIComponent caseOption;
    private UIComponent wrapOption;
    private UIComponent closeOption;
    private UIComponent findButton;

    public UIComponent getFrame() {
        return dial;
    }

    public SourceFindDialog(ContestApplet ca, UIComponent panel, UIComponent target) {
        page = ca.getCurrentUIManager().getUIPage("source_find_dialog", true);
        frame = panel;
        while (frame.getParent() != null) frame = frame.getParent();
        dial = page.getComponent("root_dialog", false);
        dial.setProperty("Owner", frame.getEventSource());
        this.target = target;
        findField = page.getComponent("find_field");
        findOption = page.getComponent("find_option");
        findOption.setProperty("Selected", Boolean.valueOf(backwardDir));
        caseOption = page.getComponent("case_option");
        caseOption.setProperty("Selected", Boolean.valueOf(ignoreCase));
        wrapOption = page.getComponent("wrap_option");
        wrapOption.setProperty("Selected", Boolean.valueOf(wrapAround));
        closeOption = page.getComponent("close_option");
        closeOption.setProperty("Selected", Boolean.valueOf(closeAfterFind));
        findButton = page.getComponent("find_button");
        findOption.addEventListener("Item", this);
        caseOption.addEventListener("Item", this);
        wrapOption.addEventListener("Item", this);
        closeOption.addEventListener("Item", this);
        findButton.addEventListener("action", this);
        dial.addEventListener("Key", this);
        dial.performAction("pack");
        dial.setProperty("LocationRelativeTo", frame.getEventSource());
    }

    public void setText(String text) {
        this.text = text;
    }
    
    public void setSyntaxHighlight(boolean syntaxHighlight) {
        this.syntaxHighlight = syntaxHighlight;
    }
    
    public void show() {
        // Show the dialog and bring it to the front
        dial.setProperty("Visible", Boolean.TRUE);
        dial.performAction("toFront");
    }

    public void hide() {
        dial.setProperty("Visible", Boolean.FALSE);
    }

    public void findAgain() {
        findButton.performAction("doClick");
    }

    public void keyPressed(KeyEvent e) {

        switch (e.getKeyCode()) {
        case KeyEvent.VK_ENTER:
            findButton.performAction("doClick");
            break;

        case KeyEvent.VK_ESCAPE:
            hide();
            break;
        }
    }

    public void itemStateChanged(ItemEvent e) {
        // Decide which check box made it
        Object source = e.getItemSelectable();
        if (source == findOption.getEventSource()) {
            backwardDir = e.getStateChange() == ItemEvent.SELECTED;
        } else if (source == caseOption.getEventSource()) {
            ignoreCase = e.getStateChange() == ItemEvent.SELECTED;
        } else if (source == wrapOption.getEventSource()) {
            wrapAround = e.getStateChange() == ItemEvent.SELECTED;
        } else if (source == closeOption.getEventSource()) {
            closeAfterFind = e.getStateChange() == ItemEvent.SELECTED;
        } 
    }

    public void actionPerformed(ActionEvent e) {

        int origPos, fromPos;

        // Get the find text stuff
        String findText = (String) findField.getProperty("Text");
        int findTextLen = findText.length();

        // Get the text to look in stuff
        // String lookText = target.getText();
        String lookText = text;
        lookText = lookText.replaceAll("\r", "");
        //if (syntaxHighlight) {
        lookText = lookText.replaceAll("\\t", "    ");
        //}
        int lookTextLen = lookText.length();

        // Decide the direction
        int direction = backwardDir ? -1 : 1;

        // Get the from TO
        origPos = ((Integer) target.getProperty("CaretPosition")).intValue();
        fromPos = origPos + direction;
        //toPos= (backwardDir ? 0: lookText.length()-1);
        //System.out.println("starting fromPos: " + fromPos);

        boolean tempWrapAround = wrapAround;


        while (true) {
            // Look for the text
            for (int pos = fromPos; pos >= 0 && pos < lookTextLen; pos += direction) {
                // Does the text match, if so - highlight it
                if (lookText.regionMatches(ignoreCase, pos, findText, 0, findTextLen)) {
                    
                    try {
                        Highlighter hilite = (Highlighter) target.getProperty("Highlighter");
                        Highlighter.Highlight[] hilites = hilite.getHighlights();

                        for (int i=0; i<hilites.length; i++) {
                            if (hilites[i].getPainter() instanceof MyHighlightPainter) {
                                hilite.removeHighlight(hilites[i]);
                            }
                        }
                        
                        //if (syntaxHighlight) {
                        hilite.addHighlight(pos+1, pos+findTextLen+1, myHighlightPainter);
                        //} else {
                        //    hilite.addHighlight(pos, pos+findTextLen, myHighlightPainter);
                        //}
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                    
                    //target.setCaretPosition(pos + findTextLen);
                    //System.out.println("found at pos: " + pos + " findTextLen: " + findTextLen);
                    //System.out.println("should be highlighted: " + lookText.substring(pos, pos+findTextLen));
                    target.setProperty("CaretPosition", new Integer(pos));
                    if (closeAfterFind) hide();
                    return;
                }
            }

            // Check to see if we should wrap around
            if (tempWrapAround) {
                tempWrapAround = false;
                fromPos = backwardDir ? lookTextLen - 1 : 0;
                //toPos= origPos + direction * -1;
            } else {
                JOptionPane.showMessageDialog((JFrame) frame.getEventSource(), "The search string was not found.", "Find", JOptionPane.WARNING_MESSAGE);
                if (closeAfterFind) hide();
                return;
            }
        }

    }
    
    private Highlighter.HighlightPainter myHighlightPainter = new MyHighlightPainter(localPref.getColor(LocalPreferences.EDSTDSELB)); 
    
    // A private subclass of the default highlight painter
    private class MyHighlightPainter extends DefaultHighlighter.DefaultHighlightPainter {
        public MyHighlightPainter(Color color) {
            super(color);
        }
    }
}
