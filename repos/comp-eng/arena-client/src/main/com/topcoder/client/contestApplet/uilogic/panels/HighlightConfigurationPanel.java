package com.topcoder.client.contestApplet.uilogic.panels;

import java.awt.Color;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.Iterator;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JColorChooser;
import javax.swing.JComboBox;
import javax.swing.JTextPane;
import javax.swing.text.AbstractDocument;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.DocumentFilter;
import javax.swing.text.MutableAttributeSet;
import javax.swing.text.SimpleAttributeSet;

import com.topcoder.client.contestApplet.common.LocalPreferences;
import com.topcoder.client.contestApplet.uilogic.frames.FrameLogic;
import com.topcoder.client.ui.UIComponent;
import com.topcoder.client.ui.UIPage;
import com.topcoder.client.ui.event.UIActionListener;
import com.topcoder.util.config.ConfigManager;
import com.topcoder.util.config.ConfigManagerException;
import com.topcoder.util.syntaxhighlighter.ConfigurationException;
import com.topcoder.util.syntaxhighlighter.HighlightingException;
import com.topcoder.util.syntaxhighlighter.RuleMatchException;
import com.topcoder.util.syntaxhighlighter.StatefulJTextPaneOutput;
import com.topcoder.util.syntaxhighlighter.SyntaxHighlighter;
import com.topcoder.util.syntaxhighlighter.TextStyle;

public class HighlightConfigurationPanel {
    private UIPage page;
    private FrameLogic parent;
    private UIComponent sourcePreview;
    private StatefulJTextPaneOutput sourcePreviewOutput;
    private String sourcePreviewStr = "/* a comment */\npublic int testMethod() {\n  String s = \"123\";\n  return 0;\n}";

    private SyntaxHighlighter highlighter;
    private SimpleEditorFilter editorFilter;
    
    private MutableAttributeSet attr = new SimpleAttributeSet();

    private ActionHandler handler = new ActionHandler();

    private UIComponent stdCommentsStyle, stdLiteralsStyle, stdKeywordsStyle, stdDefaultStyle;
    private UIComponent highlightEditorBox, highlightViewerBox;

    // Did not think I would have enough buttons to need this, but I did.
    private HashMap map = new HashMap();

    int r;
    private boolean changesPending = false;
    private LocalPreferences localPref = LocalPreferences.getInstance();

    public HighlightConfigurationPanel(FrameLogic parent, UIPage page) {
        this.parent = parent;
        this.page = page;

        sourcePreview = page.getComponent("highlight_preview_pane");
        sourcePreviewOutput = new StatefulJTextPaneOutput((JTextPane) sourcePreview.getEventSource());

        map.put(LocalPreferences.EDSTDSYNTAXCOMMENTS, createJButton(localPref.getColor(LocalPreferences.EDSTDSYNTAXCOMMENTS), "highlight_comments_color"));
        map.put(LocalPreferences.EDSTDSYNTAXLITERALS, createJButton(localPref.getColor(LocalPreferences.EDSTDSYNTAXLITERALS), "highlight_literals_color"));
        map.put(LocalPreferences.EDSTDSYNTAXKEYWORDS, createJButton(localPref.getColor(LocalPreferences.EDSTDSYNTAXKEYWORDS), "highlight_keywords_color"));
        map.put(LocalPreferences.EDSTDSYNTAXDEFAULT, createJButton(localPref.getColor(LocalPreferences.EDSTDSYNTAXDEFAULT), "highlight_default_color"));

        Object[] fontStyles = new Object[]{"Normal", "Bold", "Italic", "Bold Italic"};

        highlightEditorBox = createJCheckBox("highlight_editor_checkbox", localPref.isSyntaxHighlight());
        highlightViewerBox = createJCheckBox("highlight_viewer_checkbox", localPref.isViewerSyntaxHighlight());

        stdCommentsStyle = createJComboBox(LocalPreferences.EDSTDSYNTAXCOMMENTSSTYLE, fontStyles, "highlight_comments_style");
        stdLiteralsStyle = createJComboBox(LocalPreferences.EDSTDSYNTAXLITERALSSTYLE, fontStyles, "highlight_literals_style");
        stdKeywordsStyle = createJComboBox(LocalPreferences.EDSTDSYNTAXKEYWORDSSTYLE, fontStyles, "highlight_keywords_style");
        stdDefaultStyle = createJComboBox(LocalPreferences.EDSTDSYNTAXDEFAULTSTYLE, fontStyles, "highlight_default_style");
        
        // Create the syntax highlighter
        // mtong: move this to LocalPreferences.java
        AbstractDocument doc = (AbstractDocument)sourcePreview.getProperty("Document");
        editorFilter = new SimpleEditorFilter();
        try {
            ConfigManager configManager = localPref.getConfigManager();
            if (!configManager.existsNamespace(SyntaxHighlighter.DEFAULT_NAMESPACE)) {
                URL url = this.getClass().getResource("/syntaxhighlighter/config.xml");
                configManager.add(url);
            }
            
            highlighter = new SyntaxHighlighter();
            updateHighlighterStyles();
        } catch (ConfigurationException e) {
            e.printStackTrace();
        } catch (ConfigManagerException e) {
            e.printStackTrace();
        } 
        if (localPref.isSyntaxHighlight()) {
            doc.setDocumentFilter(editorFilter);
        } else {
            doc.setDocumentFilter(null);
        }
        
        resetPreview();
    }

    private UIComponent createJButton(Color color, String name) {
        UIComponent temp = page.getComponent(name);
        temp.setProperty("Background", color);
        temp.addEventListener("Action", handler);
        return temp;
    }
    
    private UIComponent createJCheckBox(String name, boolean checked) {
        UIComponent temp = page.getComponent(name);
        temp.setProperty("Selected", Boolean.valueOf(checked));
        temp.addEventListener("Action", handler);
        return temp;
    }

    private UIComponent createJComboBox(String syntaxStylePref, Object[] fontStyles, String name) {
        UIComponent temp = page.getComponent(name);
        temp.setProperty("items", fontStyles);
        temp.setProperty("SelectedIndex", Integer.valueOf(localPref.getProperty(syntaxStylePref, "0")));
        temp.addEventListener("Action", handler);
        return temp;
    }

    // replace lines commented out with lines relevant to syntax highlighting
    private void resetPreview() {
        sourcePreview.setProperty("Text", "");
        sourcePreview.setProperty("SelectedTextColor", localPref.getColor(LocalPreferences.EDSTDSELT));
        sourcePreview.setProperty("SelectionColor", localPref.getColor(LocalPreferences.EDSTDSELB));
        sourcePreview.setProperty("Background", localPref.getColor(LocalPreferences.EDSTDBACK));
        updateHighlighterStyles();

        try {
            //StyleConstants.setForeground(attr, ((JButton) map.get(LocalPreferences.EDSTDFORE)).getBackground());
            //StyleConstants.setFontFamily(attr, (String) stdFonts.getSelectedItem());
            //StyleConstants.setFontSize(attr, Integer.parseInt((String) stdFontSizes.getSelectedItem()));
            Document doc = (Document) sourcePreview.getProperty("Document");
            doc.insertString(doc.getLength(), sourcePreviewStr, attr);
        } catch (BadLocationException e) {
        }
    }

    private class ActionHandler implements UIActionListener {

        public void actionPerformed(ActionEvent e) {
            Object src = e.getSource();

            if (src instanceof JButton) {
                // Get the foreground color
                Color col = ((JButton) e.getSource()).getBackground();

                // Choose a new one
                Color newCol = JColorChooser.showDialog(null, "Choose color", col);
                if (newCol == null) return;

                // Set our changes pending color
                if (!col.equals(newCol)) changesPending = true;

                // Reset the color and view
                ((JButton) e.getSource()).setBackground(newCol);
                resetPreview();
            } else if (src instanceof JComboBox) {
                changesPending = true;
                resetPreview();
            } else if (src instanceof JCheckBox) {
                changesPending = true;
                resetPreview();
            }
        }
    }

    public boolean areChangesPending() {
        return changesPending;
    }

    public void saveHighlightPreferences() {
        HashMap colors = new HashMap();
        for (Iterator itr = map.keySet().iterator(); itr.hasNext();) {
            String key = (String) itr.next();
            UIComponent button = (UIComponent) map.get(key);
            colors.put(key, button.getProperty("Background"));
        }

        localPref.saveColors(colors);

        localPref.setProperty(LocalPreferences.EDSTDSYNTAXCOMMENTSSTYLE, String.valueOf(stdCommentsStyle.getProperty("SelectedIndex")));
        localPref.setProperty(LocalPreferences.EDSTDSYNTAXLITERALSSTYLE, String.valueOf(stdLiteralsStyle.getProperty("SelectedIndex")));
        localPref.setProperty(LocalPreferences.EDSTDSYNTAXKEYWORDSSTYLE, String.valueOf(stdKeywordsStyle.getProperty("SelectedIndex")));
        localPref.setProperty(LocalPreferences.EDSTDSYNTAXDEFAULTSTYLE, String.valueOf(stdDefaultStyle.getProperty("SelectedIndex")));
        localPref.setSyntaxHighlight(((Boolean) highlightEditorBox.getProperty("Selected")).booleanValue());
        localPref.setViewerSyntaxHighlight(((Boolean) highlightViewerBox.getProperty("Selected")).booleanValue());
        
        try {
            localPref.savePreferences();
        } catch (IOException e) {
        }
        changesPending = false;
    }
    
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
    
    /**
     * <p>
     * Private method used to highlight the document.  Called from the public methods of this class.
     * </p>
     *
     * @param position An integer representing the position in the document where the change occurred.  The caller
     *                 should ensure this is valid within the scope of the document.
     */
    private void rehighlight(int position, int remove, int insert) {
        if (((Document) sourcePreview.getProperty("Document")).getLength() == 0) {
            return;
        }

        try {
            // highlights only from the segment prior to the position.  If the position falls at the segment
            // start, it will highlight one segment previous to the one it falls on.  This will break on
            // complex regular expressions but is probably sufficient for the expected usage.
            String text = sourcePreviewOutput.getHighlightString(position, remove, insert, 50, 0);
            if (text != null && !text.trim().equals("")) {
                //System.out.println("rehighlight context begins");
                if (update == false) {
                    sourcePreviewOutput.setUpdate(false);
                }
                //highlighter.highlightText(text, ((StandardEditor)sourcePreview).getLanguage().getName(), sourcePreviewOutput);
                highlighter.highlightText(text, "Java", sourcePreviewOutput);
                if (update == false) {
                    sourcePreviewOutput.setUpdate(true);
                }
                //System.out.println("rehighlight context ends");
            }

            // the below can be uncommented (and the above commented) to do accurate (slow) matching.
            //             highlighter.highlightText(sourcePreview.getDocument().getText(0, sourcePreview.getDocument().getLength()),
            //             "Java", sourcePreviewOutput);
        } catch (RuleMatchException e) {
            e.printStackTrace();
            sourcePreviewOutput.resetState();
        } catch (HighlightingException e) {
            e.printStackTrace();
            sourcePreviewOutput.resetState();
        } catch (BadLocationException e) {
            e.printStackTrace();
            sourcePreviewOutput.resetState();
        }
    }
    
    private void updateHighlighterStyles() {
        String fontName = localPref.getFont(LocalPreferences.EDSTDFONT);
        int fontStyle = 0;
        int fontSize = localPref.getFontSize(LocalPreferences.EDSTDFONTSIZE);
        for (int i=0; i<highlighter.getLanguages().length; i++) {
            TextStyle[] styles = highlighter.getLanguages()[i].getStyles();
            for (int j=0; j<styles.length; j++) {
                if (styles[j].getName().equals("KEYWORD_STYLE")) {
                    styles[j].setColor((Color)((UIComponent)map.get(LocalPreferences.EDSTDSYNTAXKEYWORDS)).getProperty("Background"));
                    fontStyle = ((Integer) stdKeywordsStyle.getProperty("SelectedIndex")).intValue();
                } else if (styles[j].getName().equals("BLOCK_STYLE")) {
                    styles[j].setColor((Color)((UIComponent)map.get(LocalPreferences.EDSTDSYNTAXCOMMENTS)).getProperty("Background"));
                    fontStyle = ((Integer) stdCommentsStyle.getProperty("SelectedIndex")).intValue();
                } else if (styles[j].getName().equals("LITERAL_STYLE")) {
                    styles[j].setColor((Color)((UIComponent)map.get(LocalPreferences.EDSTDSYNTAXLITERALS)).getProperty("Background"));
                    fontStyle = ((Integer) stdLiteralsStyle.getProperty("SelectedIndex")).intValue();
                } else if (styles[j].getName().equals("DEFAULT_STYLE")) {
                    styles[j].setColor((Color)((UIComponent)map.get(LocalPreferences.EDSTDSYNTAXDEFAULT)).getProperty("Background"));
                    fontStyle = ((Integer) stdDefaultStyle.getProperty("SelectedIndex")).intValue();
                }
                styles[j].setBGColor(localPref.getColor(LocalPreferences.EDSTDBACK));
                styles[j].setFont(new Font(fontName, fontStyle, fontSize));
            }
        }
    }
}
