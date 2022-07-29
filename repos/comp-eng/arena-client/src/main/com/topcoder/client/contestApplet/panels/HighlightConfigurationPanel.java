package com.topcoder.client.contestApplet.panels;

import java.util.*;
import java.awt.*;
import java.awt.event.*;

import javax.swing.*;
import javax.swing.text.*;
import javax.swing.border.*;
import com.topcoder.client.contestApplet.widgets.*;
import com.topcoder.client.contestApplet.common.*;
import com.topcoder.client.contestApplet.editors.Standard.StandardEditorPanel;
import com.topcoder.util.config.ConfigManager;
import com.topcoder.util.config.ConfigManagerException;
import com.topcoder.util.syntaxhighlighter.ConfigurationException;
import com.topcoder.util.syntaxhighlighter.HighlightingException;
import com.topcoder.util.syntaxhighlighter.RuleMatchException;
import com.topcoder.util.syntaxhighlighter.StatefulJTextPaneOutput;
import com.topcoder.util.syntaxhighlighter.SyntaxHighlighter;
import com.topcoder.util.syntaxhighlighter.TextStyle;

import java.io.IOException;
import java.net.URL;

public class HighlightConfigurationPanel extends JPanel {

    private JDialog parent;

    private JTextPane sourcePreview;
    private StatefulJTextPaneOutput sourcePreviewOutput;
    private String sourcePreviewStr = "/* a comment */\npublic int testMethod() {\n  String s = \"123\";\n  return 0;\n}";

    private SyntaxHighlighter highlighter;
    private SimpleEditorFilter editorFilter;
    
    private MutableAttributeSet attr = new SimpleAttributeSet();

    private ActionHandler handler = new ActionHandler();

    private JComboBox stdCommentsStyle, stdLiteralsStyle, stdKeywordsStyle, stdDefaultStyle;
    private JCheckBox highlightEditorBox, highlightViewerBox;

    // Did not think I would have enough buttons to need this, but I did.
    private HashMap map = new HashMap();

    int r;
    private boolean changesPending = false;
    private boolean needsNewWindow = false;
    private LocalPreferences localPref = LocalPreferences.getInstance();


    public HighlightConfigurationPanel(JDialog iparent) {
        //super(new GridLayout(1,2),false);
        super(false);
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        JPanel mainWin = new JPanel();
        mainWin.setLayout(new BoxLayout(mainWin, BoxLayout.X_AXIS));

        parent = iparent;
        this.setBackground(Common.BG_COLOR);

        GridBagConstraints gbc = Common.getDefaultConstraints();
        gbc.fill = GridBagConstraints.VERTICAL;
        gbc.anchor = GridBagConstraints.NORTHEAST;

        JPanel edsettings = new JPanel(new GridBagLayout(), false);
        JPanel preview = new JPanel(new GridLayout(2, 1), false);

        edsettings.setBackground(Common.BG_COLOR);
        preview.setBackground(Common.BG_COLOR);

        JPanel stdSettingsPanel = new JPanel(new GridBagLayout(), false);
        stdSettingsPanel.setBackground(Common.BG_COLOR);

        JPanel sourcePreviewPanel = new JPanel(new BorderLayout(), false);
        sourcePreviewPanel.setBackground(Common.BG_COLOR);

        Border border = new RoundBorder(Common.LIGHT_GREY, 5, true);
        MyTitledBorder tb = new MyTitledBorder(border, "Source Preview", TitledBorder.LEFT, TitledBorder.ABOVE_TOP);
        tb.setTitleColor(Common.PT_COLOR);
        sourcePreviewPanel.setBorder(tb);
        sourcePreview = new JTextPane();
        sourcePreview.setEditable(false);
        sourcePreviewPanel.add(sourcePreview);

        preview.add(sourcePreviewPanel);
        sourcePreviewOutput = new StatefulJTextPaneOutput(sourcePreview);

        map.put(LocalPreferences.EDSTDSYNTAXCOMMENTS, createJButton(localPref.getColor(LocalPreferences.EDSTDSYNTAXCOMMENTS)));
        map.put(LocalPreferences.EDSTDSYNTAXLITERALS, createJButton(localPref.getColor(LocalPreferences.EDSTDSYNTAXLITERALS)));
        map.put(LocalPreferences.EDSTDSYNTAXKEYWORDS, createJButton(localPref.getColor(LocalPreferences.EDSTDSYNTAXKEYWORDS)));
        map.put(LocalPreferences.EDSTDSYNTAXDEFAULT, createJButton(localPref.getColor(LocalPreferences.EDSTDSYNTAXDEFAULT)));
        
        Object[] fontStyles = new Object[]{"Normal", "Bold", "Italic", "Bold Italic"};
        
        gbc.fill = GridBagConstraints.NONE;
        gbc.anchor = GridBagConstraints.WEST;
        Common.insertInPanel(createHeaderJLabel("Syntax Highlighting"), stdSettingsPanel, gbc, 0, 0, 1, 1, 0, 0);
        
        JComponent checkboxHighlightPanel = new JPanel();
        checkboxHighlightPanel.setBackground(Common.BG_COLOR);

        highlightEditorBox = createJCheckBox("Standard Editor", localPref.isSyntaxHighlight());        
        checkboxHighlightPanel.add(highlightEditorBox);
        
        highlightViewerBox = createJCheckBox("Source Viewer", localPref.isViewerSyntaxHighlight());        
        checkboxHighlightPanel.add(highlightViewerBox);
        
        gbc.fill = GridBagConstraints.NONE;
        gbc.anchor = GridBagConstraints.WEST;
        Common.insertInPanel(createJLabel("Apply highlighting to: "), stdSettingsPanel, gbc, 0, 1, 1, 1, 0, 0);
        Common.insertInPanel(checkboxHighlightPanel, stdSettingsPanel, gbc, 1, 1, 3, 1, 0.0, 0.0);
        
        gbc.fill = GridBagConstraints.NONE;
        gbc.anchor = GridBagConstraints.WEST;
        Common.insertInPanel(createJLabel("Comments: "), stdSettingsPanel, gbc, 0, 2, 1, 1, 0, 0);
        stdCommentsStyle = new JComboBox(fontStyles);
        JPanel syntaxCommentsPanel = createSyntaxPanel(LocalPreferences.EDSTDSYNTAXCOMMENTS,
                LocalPreferences.EDSTDSYNTAXCOMMENTSSTYLE, stdCommentsStyle, fontStyles, gbc);
        gbc.fill = GridBagConstraints.NONE;
        gbc.anchor = GridBagConstraints.NORTHWEST;
        Common.insertInPanel(syntaxCommentsPanel, stdSettingsPanel, gbc, 1, 2, 1, 1, 0, 0);
        
        gbc.fill = GridBagConstraints.NONE;
        gbc.anchor = GridBagConstraints.WEST;
        Common.insertInPanel(createJLabel("Literals: "), stdSettingsPanel, gbc, 2, 2, 1, 1, 0, 0);
        stdLiteralsStyle = new JComboBox(fontStyles);
        JPanel syntaxLiteralsPanel = createSyntaxPanel(LocalPreferences.EDSTDSYNTAXLITERALS,
                LocalPreferences.EDSTDSYNTAXLITERALSSTYLE, stdLiteralsStyle, fontStyles, gbc);
        gbc.fill = GridBagConstraints.NONE;
        gbc.anchor = GridBagConstraints.NORTHWEST;
        Common.insertInPanel(syntaxLiteralsPanel, stdSettingsPanel, gbc, 3, 2, 1, 1, 0, 0);

        gbc.fill = GridBagConstraints.NONE;
        gbc.anchor = GridBagConstraints.WEST;
        Common.insertInPanel(createJLabel("Keywords: "), stdSettingsPanel, gbc, 0, 3, 1, 1, 0, 0);
        stdKeywordsStyle = new JComboBox(fontStyles);
        JPanel syntaxKeywordsPanel = createSyntaxPanel(LocalPreferences.EDSTDSYNTAXKEYWORDS,
                LocalPreferences.EDSTDSYNTAXKEYWORDSSTYLE, stdKeywordsStyle, fontStyles, gbc);
        gbc.fill = GridBagConstraints.NONE;
        gbc.anchor = GridBagConstraints.NORTHWEST;
        Common.insertInPanel(syntaxKeywordsPanel, stdSettingsPanel, gbc, 1, 3, 1, 1, 0, 0);

        gbc.fill = GridBagConstraints.NONE;
        gbc.anchor = GridBagConstraints.WEST;
        Common.insertInPanel(createJLabel("Default: "), stdSettingsPanel, gbc, 2, 3, 1, 1, 0, 0);
        stdDefaultStyle = new JComboBox(fontStyles);
        JPanel syntaxDefaultPanel = createSyntaxPanel(LocalPreferences.EDSTDSYNTAXDEFAULT,
                LocalPreferences.EDSTDSYNTAXDEFAULTSTYLE, stdDefaultStyle, fontStyles, gbc);
        gbc.fill = GridBagConstraints.NONE;
        gbc.anchor = GridBagConstraints.NORTHWEST;
        Common.insertInPanel(syntaxDefaultPanel, stdSettingsPanel, gbc, 3, 3, 1, 1, 0, 0);
        
        gbc.fill = GridBagConstraints.NONE;
        gbc.anchor = GridBagConstraints.NORTHWEST;
        Common.insertInPanel(createJLabel(""), stdSettingsPanel, gbc, 0, 4, 1, 1, 1, 1);

        Common.insertInPanel(stdSettingsPanel, edsettings, gbc, 1, 1, 1, 1);
        
        // was going to add problem settings to this window, may later, leaving ability to add footer
        mainWin.add(edsettings);
        mainWin.add(preview);
        add(mainWin);
        
        // Create the syntax highlighter
        // mtong: move this to LocalPreferences.java
        AbstractDocument doc = (AbstractDocument)sourcePreview.getDocument();
        editorFilter = new SimpleEditorFilter();
        try {
            ConfigManager configManager = localPref.getConfigManager();
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
        if (localPref.isSyntaxHighlight()) {
            doc.setDocumentFilter(editorFilter);
        } else {
            doc.setDocumentFilter(null);
        }
        
        resetPreview();
        needsNewWindow = true;
    }

    private JLabel createHeaderJLabel(String text) {
        JLabel temp = new JLabel(text);
        temp.setForeground(Common.PT_COLOR);
        temp.setBackground(Common.BG_COLOR);
        temp.setFont(new Font(temp.getFont().getFontName(), Font.BOLD, temp.getFont().getSize()));
        return temp;
    }

    private JLabel createJLabel(String text) {
        JLabel temp = new JLabel(text);
        temp.setForeground(Common.FG_COLOR);
        temp.setBackground(Common.BG_COLOR);
        return temp;
    }

    private JButton createJButton(Color color) {
        JButton temp = new JButton();
        temp.setBackground(color);
        temp.addActionListener(handler);
        temp.setPreferredSize(new Dimension(35, 20));
        return temp;
    }
    
    private JCheckBox createJCheckBox(String text, boolean checked) {
        JCheckBox temp = new JCheckBox(text, checked);
        temp.setBackground(Common.BG_COLOR);
        temp.setForeground(Common.FG_COLOR);
        temp.setOpaque(false);
        temp.addActionListener(handler);
        return temp;
    }
    
    private JPanel createSyntaxPanel(String syntaxPref, String syntaxStylePref, JComboBox styleList, 
            Object[] fontStyles, GridBagConstraints gbc) {
        JPanel syntaxPanel = new JPanel(new GridBagLayout(), false);
        syntaxPanel.setBackground(Common.BG_COLOR);
        
        Common.insertInPanel((JButton) map.get(syntaxPref), syntaxPanel, gbc, 0, 0, 1, 1, 0, 0);
        styleList.setSelectedIndex(Integer.parseInt(localPref.getProperty(syntaxStylePref, "0")));
        styleList.setEditable(false);
        styleList.addActionListener(handler);
        Common.insertInPanel(styleList, syntaxPanel, gbc, 1, 0, 1, 1, 0, 0);
        return syntaxPanel;
    }

    // replace lines commented out with lines relevant to syntax highlighting
    private void resetPreview() {

        sourcePreview.setText("");
        sourcePreview.setSelectedTextColor(localPref.getColor(LocalPreferences.EDSTDSELT));
        sourcePreview.setSelectionColor(localPref.getColor(LocalPreferences.EDSTDSELB));
        sourcePreview.setBackground(localPref.getColor(LocalPreferences.EDSTDBACK));
        updateHighlighterStyles();

        try {
            //StyleConstants.setForeground(attr, ((JButton) map.get(LocalPreferences.EDSTDFORE)).getBackground());
            //StyleConstants.setFontFamily(attr, (String) stdFonts.getSelectedItem());
            //StyleConstants.setFontSize(attr, Integer.parseInt((String) stdFontSizes.getSelectedItem()));

            sourcePreview.getDocument().insertString(sourcePreview.getDocument().getLength(), sourcePreviewStr, attr);
        } catch (BadLocationException e) {
        }
        if (needsNewWindow) parent.pack();
    }

    private class ActionHandler implements ActionListener {

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
            JButton button = (JButton) map.get(key);
            colors.put(key, button.getBackground());
        }

        localPref.saveColors(colors);

        localPref.setProperty(LocalPreferences.EDSTDSYNTAXCOMMENTSSTYLE, String.valueOf(stdCommentsStyle.getSelectedIndex()));
        localPref.setProperty(LocalPreferences.EDSTDSYNTAXLITERALSSTYLE, String.valueOf(stdLiteralsStyle.getSelectedIndex()));
        localPref.setProperty(LocalPreferences.EDSTDSYNTAXKEYWORDSSTYLE, String.valueOf(stdKeywordsStyle.getSelectedIndex()));
        localPref.setProperty(LocalPreferences.EDSTDSYNTAXDEFAULTSTYLE, String.valueOf(stdDefaultStyle.getSelectedIndex()));
        localPref.setSyntaxHighlight(highlightEditorBox.isSelected());
        localPref.setViewerSyntaxHighlight(highlightViewerBox.isSelected());
        
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
        if (((AbstractDocument) sourcePreview.getDocument()).getLength() == 0) {
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
                    styles[j].setColor(((JButton)map.get(LocalPreferences.EDSTDSYNTAXKEYWORDS)).getBackground());
                    fontStyle = stdKeywordsStyle.getSelectedIndex();                        
                } else if (styles[j].getName().equals("BLOCK_STYLE")) {
                    styles[j].setColor(((JButton)map.get(LocalPreferences.EDSTDSYNTAXCOMMENTS)).getBackground());
                    fontStyle = stdCommentsStyle.getSelectedIndex(); 
                } else if (styles[j].getName().equals("LITERAL_STYLE")) {
                    styles[j].setColor(((JButton)map.get(LocalPreferences.EDSTDSYNTAXLITERALS)).getBackground());
                    fontStyle = stdLiteralsStyle.getSelectedIndex(); 
                } else if (styles[j].getName().equals("DEFAULT_STYLE")) {
                    styles[j].setColor(((JButton)map.get(LocalPreferences.EDSTDSYNTAXDEFAULT)).getBackground());
                    fontStyle = stdDefaultStyle.getSelectedIndex();
                }
                styles[j].setBGColor(localPref.getColor(LocalPreferences.EDSTDBACK));
                styles[j].setFont(new Font(fontName, fontStyle, fontSize));
            }
        }
    }
}
