/**
 * StandardEditorPanel.java
 *
 * Description:		This is the panel for the standard editor
 *                  It is used to 'glue' all the pieces together
 * @author			Tim "Pops" Roberts
 * @version			1.0
 */

package com.topcoder.client.contestApplet.editors.Standard;

import java.awt.*;
import java.awt.event.*;
import java.net.URL;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.event.*;
import javax.swing.text.*;
import javax.swing.undo.*;

import com.topcoder.util.config.ConfigManager;
import com.topcoder.util.config.ConfigManagerException;
import com.topcoder.util.syntaxhighlighter.ConfigurationException;
import com.topcoder.util.syntaxhighlighter.HighlightingException;
import com.topcoder.util.syntaxhighlighter.RuleMatchException;
import com.topcoder.util.syntaxhighlighter.StatefulJTextPaneOutput;
import com.topcoder.util.syntaxhighlighter.SyntaxHighlighter;
import com.topcoder.util.syntaxhighlighter.TextStyle;
import com.topcoder.client.contestApplet.common.Common;
import com.topcoder.client.contestApplet.common.LocalPreferences;
import java.util.HashMap;
import java.util.Iterator;

import java.util.Observable;
import java.util.Observer;

public class StandardEditorPanel extends JPanel implements CaretListener, Observer {

    private int position;
    
    /**
     * Represents whether the real highlighting should be done. <code>true</code> the real highlighting should be
     * done, otherwise we need only update the starting point, the stored segments in
     * <code>StatefulJTextPaneOutput</code> class under fast mode.  
     * 
     * @since new Added
     */
    private boolean update = true;
    /**
     * <p>
     * SimpleEditorFilter extends the DocumentFilter class in order to provide filtered highlighting capabilities.
     * As a change is made to the document, a portion of the document is rehighlighted.  Unfortunately, due to the
     * complexity of regular expressions, it is not possible to highlight only the visible portion or bits and pieces
     * since we cannot simply extract tokens.
     * </p>
     */
    public final class SimpleEditorFilter extends DocumentFilter {

        /**
         * <p>
         * Invoked prior to removal of the specified region of the specified Document.
         * </p>
         *
         * @param fb FilterBypass that can be used to mutate the Document.
         * @param offset the offset from the beginning (>= 0).
         * @param length the number of characters to remove (>= 0).
         *
         * @throws BadLocationException some portion of the removal range, starting at offset, was not a valid
         * part of the document.
         */
        public void remove(DocumentFilter.FilterBypass fb, int offset, int length) throws BadLocationException {
            super.remove(fb, offset, length);
            rehighlight(offset, length, 0);
        }

        /**
         * <p>
         * Invoked prior to insertion at the specified offset of the specified Document.
         * </p>
         *
         * @param fb FilterBypass that can be used to mutate the Document.
         * @param offset the offset to insert the content (>= 0).
         * @param string the String to insert.
         * @param attr the attributes to associate with the inserted content.  May be null.
         *
         * @throws BadLocationException the insertion position was not a valid part of the document.
         */
        public void insertString(DocumentFilter.FilterBypass fb, int offset, String string, AttributeSet attr)
            throws BadLocationException {

            super.insertString(fb, offset, string, attr);
            rehighlight(offset, 0, string.length());
        }

        /**
         * <p>
         * Invoked prior to replacing a region of text in the specified Document.
         * </p>
         *
         * @param fb FilterBypass that can be used to mutate the Document.
         * @param offset the offset from the beginning (>= 0).
         * @param length the length of text to delete.
         * @param text Text to insert (null indicates no text).
         * @param attrs AttributeSet indicating attributes of inserted text.  May be null.
         *
         * @throws BadLocationException the given insert position was not a valid part of the document.
         */
        public void replace(DocumentFilter.FilterBypass fb, int offset, int length, String text, AttributeSet attrs)
            throws BadLocationException {

            super.replace(fb, offset, length, text, attrs);
            rehighlight(offset, length, text.length());
        }
    }
    
    private JLabel lineLabel;
    private JTextPane textPane;
    private StatefulJTextPaneOutput textPaneOutput;
    private FindDialog findDialog;
    private FindAction findAction;
    private UndoAction undoAction;
    private RedoAction redoAction;
    private FindAgainAction findAgainAction;
    private GotoAction gotoAction;
    
    protected UndoManager undo;
    private LocalPreferences pref = LocalPreferences.getInstance();
    private SyntaxHighlighter highlighter;
    private SimpleEditorFilter editorFilter;
    private String highlightLanguage;
    
    private boolean autoIndent = true;
    
    public void update(Observable o, Object arg) {
        //System.out.println("update");
        autoIndent = pref.isTrue(LocalPreferences.EDSTDINDENT);
        AbstractDocument doc = (AbstractDocument)textPane.getDocument();
        if (pref.isSyntaxHighlight()) {
            doc.setDocumentFilter(editorFilter);
            updateHighlighterStyles();
            
        } else {
            doc.setDocumentFilter(null);
            
            MutableAttributeSet attrSet = new SimpleAttributeSet();

            StyleConstants.setBold(attrSet, false);
            StyleConstants.setItalic(attrSet, false);
            StyleConstants.setForeground(attrSet, pref.getColor(LocalPreferences.EDSTDFORE));
            StyleConstants.setFontFamily(attrSet, pref.getFont(LocalPreferences.EDSTDFONT));
            StyleConstants.setFontSize(attrSet, pref.getFontSize(LocalPreferences.EDSTDFONTSIZE));
            
            // style the necessary segment - update if possible, as this is a hack. Removing this line 
            // will cause code to appear in the syntax highlighter's color at the last cursor position 
            // when syntax highlighting is disabled.
            textPane.getStyledDocument().setCharacterAttributes(35221, 21, attrSet, true);
        }
        Style s = textPane.getLogicalStyle();
        StyleConstants.setTabSet(s, getTabSet(pref.getTabSize()));
    }

    public StandardEditorPanel(JTextPane tp) {        
        // Set the layout as BoxLayout
        this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

        // Save reference to the text area
        textPane = tp;
        textPaneOutput = new StatefulJTextPaneOutput(textPane);
        
        // Create the syntax highlighter
        // mtong: move this to LocalPreferences.java
        AbstractDocument doc = (AbstractDocument)textPane.getDocument();
        editorFilter = new SimpleEditorFilter();
        try {
            ConfigManager configManager = pref.getConfigManager();
            if (!configManager.existsNamespace(SyntaxHighlighter.DEFAULT_NAMESPACE)) {
                URL url = StandardEditorPanel.class.getResource("/syntaxhighlighter/config.xml");
                configManager.add(url);
            }
            
            highlighter = new SyntaxHighlighter();
            updateHighlighterStyles();
        } catch (ConfigurationException e) {
            e.printStackTrace();
        } catch (ConfigManagerException e) {
            e.printStackTrace();
        } 
        if (pref.isSyntaxHighlight()) {
            doc.setDocumentFilter(editorFilter);
        } else {
            doc.setDocumentFilter(null);
        }
        
        Style s = textPane.getLogicalStyle();
        StyleConstants.setTabSet(s, getTabSet(pref.getTabSize()));
        
        //setup undo
        undo = new UndoManager();
        undo.setLimit(5000);
        
        textPane.getDocument().addUndoableEditListener(new StandardUndoableEditListener());

        // Make the panel the listener for caret position changes
        textPane.addCaretListener(this);

        // Set the background color for the panel
        this.setBackground(Common.BG_COLOR);

        // Create the generic actions
        findAction = new FindAction(this);
        findAgainAction = new FindAgainAction(this);
        gotoAction = new GotoAction(this);
        undoAction = new UndoAction(this);
        redoAction = new RedoAction(this);

        // Create the find button
        JButton findButton = Common.getImageButton("g_find_but.gif", this);
        findButton.addActionListener(findAction);
        //findButton.setMnemonic(KeyEvent.VK_F);
        findButton.setToolTipText("Find text");

        // Create the Goto button
        JButton gotoButton = Common.getImageButton("g_goto_but.gif", this);
        gotoButton.addActionListener(gotoAction);
        //gotoButton.setMnemonic(KeyEvent.VK_G);
        gotoButton.setToolTipText("Goto Line");
        
        // Undo Button
        JButton undoButton = Common.getImageButton("g_undo_but.gif", this);
        undoButton.addActionListener(undoAction);
        //undoButton.setMnemonic(KeyEvent.VK_Z);
        undoButton.setToolTipText("Undo");
        
        // Redo Button
        JButton redoButton = Common.getImageButton("g_redo_but.gif", this);
        redoButton.addActionListener(redoAction);
        //redoButton.setMnemonic(KeyEvent.VK_R);
        redoButton.setToolTipText("Redo");

        // Create the line number label;
        lineLabel = new JLabel("Line : 1");
        lineLabel.setForeground(Color.white);
        lineLabel.setMinimumSize(new Dimension(70, 18));
        lineLabel.setPreferredSize(new Dimension(70, 18));

        // Add the key listener
        textPane.addKeyListener(new KeyHandler());

        // Setup a scroll panel for the text area
        JPanel noWrapPanel = new JPanel(new BorderLayout()); 
        noWrapPanel.add(textPane);
        JScrollPane scrollPane = new JScrollPane(noWrapPanel);
        scrollPane.getVerticalScrollBar().setUnitIncrement(13);
        scrollPane.setBorder(new EmptyBorder(0, 0, 0, 0));
        scrollPane.setBackground(Common.BG_COLOR);

        // Layout the panel...

        // Create the horizontal row
        Box status = Box.createHorizontalBox();
        //status.add(Box.createHorizontalStrut(5));
        status.add(findButton);
        status.add(gotoButton);
        status.add(undoButton);
        status.add(redoButton);
        status.add(Box.createHorizontalGlue());
        status.add(lineLabel);

        // Put it all together
        add(scrollPane);
        add(Box.createVerticalStrut(2));
        add(status);

        // Create the find dialog
        findDialog = new FindDialog(this, textPane);
        
        keyPrefs.put(LocalPreferences.EDSTDKEYFIND, pref.getHotKey(LocalPreferences.EDSTDKEYFIND));
        keyPrefs.put(LocalPreferences.EDSTDKEYGOTO, pref.getHotKey(LocalPreferences.EDSTDKEYGOTO));
        keyPrefs.put(LocalPreferences.EDSTDKEYUNDO, pref.getHotKey(LocalPreferences.EDSTDKEYUNDO));
        keyPrefs.put(LocalPreferences.EDSTDKEYREDO, pref.getHotKey(LocalPreferences.EDSTDKEYREDO));
        
        keyButtons.put(LocalPreferences.EDSTDKEYFIND, findButton);
        keyButtons.put(LocalPreferences.EDSTDKEYGOTO, gotoButton);
        keyButtons.put(LocalPreferences.EDSTDKEYUNDO, undoButton);
        keyButtons.put(LocalPreferences.EDSTDKEYREDO, redoButton);
    }
    
    
    HashMap keyPrefs = new HashMap();
    HashMap keyButtons = new HashMap();
    
    private boolean checkValue(KeyEvent evt, String val) {
        String[] keys = val.split("\\+");
        for(int i = 0; i < keys.length-1; i++) {
            if(keys[i].equals("Ctrl")) {
                if((evt.getModifiers() & evt.CTRL_MASK) == 0)
                    return false;
            } else if(keys[i].equals("Alt")) {
                if((evt.getModifiers() & evt.ALT_MASK) == 0)
                    return false;
            } else if(keys[i].equals("Shift")) {
                if((evt.getModifiers() & evt.SHIFT_MASK) == 0)
                    return false;
            }
        }
        
        if(!keys[keys.length-1].equals(evt.getKeyText(evt.getKeyCode()))) {
            return false;
        }
        
        return true;
    }

    private class KeyHandler extends KeyAdapter {

        public void keyPressed(KeyEvent evt) {
            
            for(Iterator i = keyPrefs.keySet().iterator(); i.hasNext(); ) {
                String key = (String)i.next();
                if(checkValue(evt, (String)keyPrefs.get(key))) {
                    evt.consume();
                    JButton btn = (JButton)keyButtons.get(key);

                    btn.doClick();
                    return;
                }
            }
            
            switch (evt.getKeyCode()) {
            case KeyEvent.VK_ENTER:
                {
                    evt.consume();                   
                    textPane.replaceSelection("\n");

                    if(textPane.getSelectionEnd() - textPane.getSelectionStart() > 0) {
                        return;
                    }
                    
                    if(!autoIndent)
                        return;
                    
                    try {
                        //get the last line
                        int caret = textPane.getCaretPosition();
                        int pos = caret - 2; //ignore the last newline
                        while(pos >= 0) {

                            String s = textPane.getDocument().getText(pos,1);
                            if(s.equals("\n"))
                                break;
                            pos--;
                        }
                        
                        if(pos > 0) {
                            String lastLine = textPane.getDocument().getText(pos+1,caret-pos-2);
                            //System.out.println("LAST LINE IS: " + lastLine);
                            
                            String insertText = "";
                            for(int i = 0; i < lastLine.length(); i++) {
                                if(lastLine.charAt(i) == ' ' || lastLine.charAt(i) == '\t')
                                    insertText += lastLine.charAt(i);
                                else
                                    break;
                            }
                            
                            textPane.replaceSelection(insertText);
                            
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }                    
                    
                    break;
                }

            case KeyEvent.VK_F3:
                {
                    evt.consume();
                    findAgainAction.actionPerformed(new ActionEvent(this, evt.getID(), ""));
                    return;
                }
           
            }            
        }
    }
    
    public void doUndo() {
        //System.out.println("doUndo begins.");
        
        while (undo.canUndo() && undo.getUndoPresentationName().equals("Undo style change")) {
            //System.out.println("undo:" + undo.getUndoPresentationName());
            undo.undo();
        }
        if (undo.canUndo()) {
            //System.out.println("undo:" + undo.getUndoPresentationName());
            String undoPresentationName = undo.getUndoPresentationName();
            int prePosition = this.position;
            undo.undo();
            int nowPosition = this.position;

            // we just need to update the starting point of StatefulJTextPaneOutput, and the real highlighting
            // should not be done. So we set the value of update to false.
            update = false;
            if (undoPresentationName.equals("Undo addition")) {
                this.rehighlight(nowPosition, Math.abs(prePosition - nowPosition), 0);
            }
            if (undoPresentationName.equals("Undo deletion")) {
                this.rehighlight(prePosition, 0, Math.abs(nowPosition - prePosition));
            }
            // restore back the value of update
            update = true;

            this.textPane.requestFocusInWindow();
            this.textPane.setCaretPosition(nowPosition);
        }
        //System.out.println("doUndo ends.");
    }
    
    public void doRedo() {
        //System.out.println("doRedo begins.");
//        while (undo.canRedo() && undo.getRedoPresentationName().equals("Redo style change")) {
//            System.out.println("undo:" + undo.getUndoPresentationName());
//            undo.redo();
//        }

        int nowPosition = -1;
        
        if (undo.canRedo()) {
            //System.out.println("redo:" + undo.getRedoPresentationName());
            String redoPresentationName = undo.getRedoPresentationName();

            int prePosition = this.position;
            undo.redo();
            nowPosition = this.position;

            // we just need to update the starting point of StatefulJTextPaneOutput, and the real highlighting
            // should not be done. So we set the value of update to false.
            update = false;
            if (redoPresentationName.equals("Redo addition")) {
                this.rehighlight(prePosition, 0, Math.abs(nowPosition - prePosition));
            }
            if (redoPresentationName.equals("Redo deletion")) {
                this.rehighlight(nowPosition, Math.abs(prePosition - nowPosition), 0);  
            }

            //System.out.println("prePosition: " + prePosition + " nowPosition:" + nowPosition);
            // restore back the value of update
            update = true;
        }
        while (undo.canRedo() && undo.getRedoPresentationName().equals("Redo style change")) {
            //System.out.println("undo:" + undo.getUndoPresentationName());
            undo.redo();
        }
        if (nowPosition >= 0) {
            this.textPane.requestFocusInWindow();
            this.textPane.setCaretPosition(nowPosition);
        }
        //System.out.println("doRedo ends.");
    }

    public FindDialog getFindDialog() {
        return findDialog;
    }

    public JTextPane getTextPane() {
        return textPane;
    }

    public void caretUpdate(CaretEvent e) {
        // Update the line counter
        try {
            int offset = e.getDot();
            position = e.getDot();
            //System.out.println("Caret position: " + offset);
            Document doc = textPane.getDocument();
            if (offset < doc.getStartPosition().getOffset() || offset >= doc.getEndPosition().getOffset())
                throw new BadLocationException("offset outside of document", offset);
            int lineNum = doc.getDefaultRootElement().getElementIndex(offset)+1;
            lineLabel.setText("Line : " + lineNum);
        } catch (BadLocationException bade) {
        }
    }

	public static void main(String s[]) {
		JFrame frame = new JFrame();
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		StandardEditor se = new StandardEditor("ababab");
		StandardEditorPanel p = new StandardEditorPanel(se);
		frame.getContentPane().add(p);
		frame.pack();
		frame.show();
	}
    
    protected class StandardUndoableEditListener implements UndoableEditListener
    {
        public void undoableEditHappened(UndoableEditEvent e) {
//            if (e.getEdit() instanceof AbstractDocument.DefaultDocumentEvent &&
//                    ((AbstractDocument.DefaultDocumentEvent)e.getEdit()).getType() == 
//                    AbstractDocument.DefaultDocumentEvent.EventType.CHANGE) {
//                    return;
//            }

            // if it is not the real highlighting, we should not add the event into the undoManager.
            if (!update) {
                return;
            }
            //System.out.println("undoableEditHappened begins");
            //System.out.println(e.getEdit().getPresentationName());
            undo.addEdit(e.getEdit());
            undoAction.updateUndoState();
            redoAction.updateRedoState(); 
            //System.out.println("undoableEditHappened ends");     
        }
    }
    
    /**
     * <p>
     * Private method used to highlight the document.  Called from the public methods of this class.
     * </p>
     *
     * @param position An integer representing the position in the document where the change occurred.  The caller
     *                 should ensure this is valid within the scope of the document.
     */
    private void rehighlight(int position, int remove, int insert) {
        if (((AbstractDocument) textPane.getDocument()).getLength() == 0) {
            return;
        }

        try {
            // highlights only from the segment prior to the position.  If the position falls at the segment
            // start, it will highlight one segment previous to the one it falls on.  This will break on
            // complex regular expressions but is probably sufficient for the expected usage.
            String text = textPaneOutput.getHighlightString(position, remove, insert, 50, 0);
            if (text != null && !text.trim().equals("")) {
                //System.out.println("rehighlight context begins");
                if (update == false) {
                    textPaneOutput.setUpdate(false);
                }
                highlighter.highlightText(text, ((StandardEditor)textPane).getLanguage().getName(), textPaneOutput);
                //highlighter.highlightText(text, "Java", textPaneOutput);
                if (update == false) {
                    textPaneOutput.setUpdate(true);
                }
                //System.out.println("rehighlight context ends");
            }

            // the below can be uncommented (and the above commented) to do accurate (slow) matching.
//             highlighter.highlightText(textPane.getDocument().getText(0, textPane.getDocument().getLength()),
//             "Java", textPaneOutput);
        } catch (RuleMatchException e) {
            e.printStackTrace();
            textPaneOutput.resetState();
        } catch (HighlightingException e) {
            e.printStackTrace();
            textPaneOutput.resetState();
        } catch (BadLocationException e) {
            e.printStackTrace();
            textPaneOutput.resetState();
        }
    }
    
    private static final int TAB_COUNT = 20;
    
    private TabSet getTabSet(int count) {
        
        String fontName = pref.getFont(LocalPreferences.EDSTDFONT);
        int fontStyle = 0;
        int fontSize = pref.getFontSize(LocalPreferences.EDSTDFONTSIZE);
        Font f = new Font(fontName, fontStyle, fontSize);
        int spaceSize = StyleContext.getDefaultStyleContext().getFontMetrics(f).stringWidth(" ");

        TabStop[] ts = new TabStop[TAB_COUNT];
        for(int i = 0; i < TAB_COUNT; i++) {
            ts[i] = new TabStop(count*spaceSize*(i+1), TabStop.ALIGN_LEFT, TabStop.LEAD_DOTS);
        }
        return new TabSet(ts);
    }
    
    private void updateHighlighterStyles() {
        String fontName = pref.getFont(LocalPreferences.EDSTDFONT);
        int fontStyle = 0;
        int fontSize = pref.getFontSize(LocalPreferences.EDSTDFONTSIZE);
        for (int i=0; i<highlighter.getLanguages().length; i++) {
            TextStyle[] styles = highlighter.getLanguages()[i].getStyles();
            for (int j=0; j<styles.length; j++) {
                if (styles[j].getName().equals("KEYWORD_STYLE")) {
                    styles[j].setColor(pref.getColor(LocalPreferences.EDSTDSYNTAXKEYWORDS));
                    fontStyle = Integer.parseInt(pref.getProperty(LocalPreferences.EDSTDSYNTAXKEYWORDSSTYLE, "0"));
                } else if (styles[j].getName().equals("BLOCK_STYLE")) {
                    styles[j].setColor(pref.getColor(LocalPreferences.EDSTDSYNTAXCOMMENTS));
                    fontStyle = Integer.parseInt(pref.getProperty(LocalPreferences.EDSTDSYNTAXCOMMENTSSTYLE, "0"));
                } else if (styles[j].getName().equals("LITERAL_STYLE")) {
                    styles[j].setColor(pref.getColor(LocalPreferences.EDSTDSYNTAXLITERALS));
                    fontStyle = Integer.parseInt(pref.getProperty(LocalPreferences.EDSTDSYNTAXLITERALSSTYLE, "0"));
                } else if (styles[j].getName().equals("DEFAULT_STYLE")) {
                    styles[j].setColor(pref.getColor(LocalPreferences.EDSTDSYNTAXDEFAULT));
                    fontStyle = Integer.parseInt(pref.getProperty(LocalPreferences.EDSTDSYNTAXDEFAULTSTYLE, "0"));
                }
                styles[j].setBGColor(pref.getColor(LocalPreferences.EDSTDBACK));
                styles[j].setFont(new Font(fontName, fontStyle, fontSize));
            }
        }
    }
}
