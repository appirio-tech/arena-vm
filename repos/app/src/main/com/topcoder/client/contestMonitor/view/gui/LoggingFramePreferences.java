package com.topcoder.client.contestMonitor.view.gui;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JColorChooser;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextPane;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.MutableAttributeSet;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GraphicsEnvironment;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.prefs.Preferences;

// Types of conf

public class LoggingFramePreferences {

    private JDialog frame;
    private Preferences pref = Preferences.userNodeForPackage(this.getClass());
    private String font;
    private Integer fontSize;

    private JTextPane logPreview;

    private static final Level[] levels = new Level[]{
        Level.DEBUG,
        Level.INFO,
        Level.WARN,
        Level.ERROR,
        Level.FATAL
    };

    private static final String[] prefices = new String[]{
        "debug.",
        "info.",
        "warn.",
        "error.",
        "fatal."
    };

    private static final Color defaultForeground[] = new Color[]{
        Color.black,
        Color.black,
        new Color(-26368),
        Color.red,
        Color.white
    };

    private static final Color defaultBackground[] = new Color[]{
        Color.white,
        Color.white,
        Color.white,
        Color.white,
        Color.red
    };

    private static final boolean defaultItalic[] = new boolean[]{
        true,
        false,
        true,
        true,
        true
    };

    private static final boolean defaultBold[] = new boolean[]{
        false,
        false,
        true,
        true,
        true
    };

    private static final String[] fonts =
            GraphicsEnvironment.getLocalGraphicsEnvironment().getAvailableFontFamilyNames();

    private static final Integer[] fontSizes =
            new Integer[]{
                new Integer(8),
                new Integer(9),
                new Integer(10),
                new Integer(11),
                new Integer(12),
                new Integer(14),
                new Integer(16),
                new Integer(18),
                new Integer(20),
                new Integer(22),
                new Integer(24)
            };

    private JButton backgroundButtons[] = new JButton[5];
    private JButton foregroundButtons[] = new JButton[5];
    private JCheckBox italicBoxes[] = new JCheckBox[5];
    private JCheckBox boldBoxes[] = new JCheckBox[5];
    private MutableAttributeSet[] attributes = new MutableAttributeSet[5];

    private JComboBox fontBox;
    private JComboBox fontSizesBox;
    private static final Logger logger = Logger.getLogger(LoggingFramePreferences.class);

    private void buildAttributes() {
        font = pref.get("font", "");
        fontSize = new Integer(pref.getInt("size", 12));
        for (int i = 0; i < levels.length; i++) {
            String prefix = prefices[i];
            Color foreground = new Color(
                    pref.getInt(prefix + "foreground", defaultForeground[i].getRGB())
            );
            Color background = new Color(
                    pref.getInt(prefix + "background", defaultBackground[i].getRGB())
            );
            boolean italic = pref.getBoolean(prefix + "italic", defaultItalic[i]);
            boolean bold = pref.getBoolean(prefix + "bold", defaultBold[i]);
            MutableAttributeSet attr = new SimpleAttributeSet();
            StyleConstants.setFontFamily(attr, font);
            StyleConstants.setForeground(attr, foreground);
            StyleConstants.setBackground(attr, background);
            StyleConstants.setFontSize(attr, fontSize.intValue());
            StyleConstants.setItalic(attr, italic);
            StyleConstants.setBold(attr, bold);
            attributes[i] = attr;
        }
    }

    public void showFrame() {
        frame.setVisible(true);
    }

    public AttributeSet getAttributes(Level level) {
        switch (level.toInt()) {
        case Level.DEBUG_INT:
            return attributes[0];
        case Level.INFO_INT:
            return attributes[1];
        case Level.WARN_INT:
            return attributes[2];
        case Level.ERROR_INT:
            return attributes[3];
        case Level.FATAL_INT:
            return attributes[4];
        }
        return null;
    }

    public LoggingFramePreferences(JFrame parent) {
        buildAttributes();
        frame = new JDialog(parent, "Logging Preferences");
        build();
    }

    private void build() {
        JPanel panel = new JPanel(new GridBagLayout());
        frame.setContentPane(panel);
        frame.setResizable(false);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridheight = gbc.gridwidth = 1;
        gbc.fill = GridBagConstraints.VERTICAL;
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.weightx = gbc.weighty = .1;

        JPanel fontPanel = new JPanel(new GridBagLayout());
        gbc.anchor = GridBagConstraints.WEST;
        gbc.gridx = gbc.gridy = 0;
        gbc.weightx = 0;
        fontPanel.add(buildFontBox(), gbc);
        gbc.gridx = 1;
        gbc.weightx = .1;
        fontPanel.add(buildFontSizeBox(), gbc);
        fontPanel.setBorder(BorderFactory.createTitledBorder("Font"));

        JPanel colorPanel = new JPanel(new GridBagLayout());
        JLabel label = new JLabel("Background", JLabel.CENTER);
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.weightx = .1;
        gbc.gridx = 1;
        gbc.gridy = 0;
        colorPanel.add(label, gbc);
        label = new JLabel("Foreground", JLabel.CENTER);
        gbc.gridx++;
        colorPanel.add(label, gbc);
        gbc.anchor = GridBagConstraints.WEST;
        label = new JLabel("Italic", JLabel.LEFT);
        gbc.gridx++;
        colorPanel.add(label, gbc);
        label = new JLabel("Bold", JLabel.LEFT);
        gbc.gridx++;
        gbc.weightx = .3;
        colorPanel.add(label, gbc);
        Insets insets1 = new Insets(5, 5, 5, 5);
        Insets insets2 = new Insets(5, 8, 5, 5);
        for (int i = 0; i < levels.length; i++) {
            MutableAttributeSet attr = attributes[i];
            gbc.gridy = i + 1;
            int x = 0;
            JLabel levelLabel = new JLabel(levels[i].toString(), JLabel.RIGHT);
            gbc.insets = insets1;
            gbc.gridx = x++;
            gbc.anchor = GridBagConstraints.EAST;
            gbc.weightx = .1;
            colorPanel.add(levelLabel, gbc);
            gbc.gridx = x++;
            gbc.anchor = GridBagConstraints.CENTER;
            backgroundButtons[i] = buildBackgroundColorButton(attr);
            colorPanel.add(backgroundButtons[i], gbc);
            gbc.gridx = x++;
            foregroundButtons[i] = buildForegroundColorButton(attr);
            colorPanel.add(foregroundButtons[i], gbc);
            gbc.gridx = x++;
            gbc.anchor = GridBagConstraints.WEST;
            gbc.insets = insets2;
            italicBoxes[i] = buildItalicBox(attr);
            colorPanel.add(italicBoxes[i], gbc);
            gbc.gridx = x++;
            gbc.weightx = .3;
            boldBoxes[i] = buildBoldBox(attr);
            colorPanel.add(boldBoxes[i], gbc);
        }
        colorPanel.setBorder(BorderFactory.createTitledBorder("Color"));

        JPanel previewPanel = new JPanel();
        logPreview = new JTextPane();
        logPreview.setBackground(Color.white);
        logPreview.setPreferredSize(new Dimension(480, 100));
        previewPanel.add(logPreview, BorderLayout.CENTER);
        previewPanel.setBorder(BorderFactory.createTitledBorder("Preview"));
        resetPreview();

        JPanel okPanel = new JPanel(new GridBagLayout());
        gbc.anchor = GridBagConstraints.EAST;
        gbc.gridx = gbc.gridy = 0;
        gbc.weightx = .1;
        okPanel.add(buildCancelButton(), gbc);
        gbc.weightx = 0;
        gbc.gridx++;
        okPanel.add(buildOkButton(), gbc);

        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = .1;
        gbc.fill = GridBagConstraints.BOTH;
        panel.add(fontPanel, gbc);
        gbc.gridy++;
        panel.add(colorPanel, gbc);
        gbc.gridy++;
        panel.add(previewPanel, gbc);
        gbc.gridy++;
        panel.add(okPanel, gbc);
        frame.pack();
    }

    private JComboBox buildFontBox() {
        fontBox = new JComboBox(fonts);
        fontBox.setSelectedItem(font);
        fontBox.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                resetPreview();
            }
        });
        return fontBox;
    }

    private JComboBox buildFontSizeBox() {
        fontSizesBox = new JComboBox(fontSizes);
        fontSizesBox.setSelectedItem(fontSize);
        fontSizesBox.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                resetPreview();
            }
        });
        return fontSizesBox;
    }

    private JButton buildBackgroundColorButton(final MutableAttributeSet attr) {
        final JButton colorButton = new JButton();
        colorButton.setBackground(StyleConstants.getBackground(attr));
        colorButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                Color c = colorButton.getBackground();
                c = JColorChooser.showDialog(frame, "Choose Background", c);
                colorButton.setBackground(c);
                colorButton.repaint();
                resetPreview();
            }
        });
        return colorButton;
    }

    private JButton buildForegroundColorButton(final MutableAttributeSet attr) {
        final JButton colorButton = new JButton();
        colorButton.setBackground(StyleConstants.getForeground(attr));
        colorButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                Color c = colorButton.getBackground();
                c = JColorChooser.showDialog(frame, "Choose Foreground", c);
                colorButton.setBackground(c);
                colorButton.repaint();
                resetPreview();
            }
        });
        return colorButton;
    }

    private JCheckBox buildBoldBox(MutableAttributeSet attr) {
        JCheckBox boldBox = new JCheckBox();
        boldBox.setSelected(StyleConstants.isBold(attr));
        boldBox.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                resetPreview();
            }
        });
        return boldBox;
    }

    private JCheckBox buildItalicBox(MutableAttributeSet attr) {
        JCheckBox italicBox = new JCheckBox();
        italicBox.setSelected(StyleConstants.isItalic(attr));
        italicBox.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                resetPreview();
            }
        });
        return italicBox;
    }


    private JButton buildOkButton() {
        JButton okButton = new JButton("OK");
        okButton.setMnemonic('o');
        okButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                font = (String) fontBox.getSelectedItem();
                fontSize = (Integer) fontSizesBox.getSelectedItem();
                for (int i = 0; i < levels.length; i++) {
                    MutableAttributeSet attr = attributes[i];
                    StyleConstants.setFontFamily(attr, font);
                    StyleConstants.setFontSize(attr, fontSize.intValue());
                    StyleConstants.setForeground(attr, backgroundButtons[i].getBackground());
                    StyleConstants.setForeground(attr, foregroundButtons[i].getBackground());
                    StyleConstants.setForeground(attr, backgroundButtons[i].getBackground());
                    StyleConstants.setItalic(attr, italicBoxes[i].isSelected());
                    StyleConstants.setBold(attr, boldBoxes[i].isSelected());
                }

                int response = JOptionPane.showConfirmDialog(frame, "Remember these preferences?", "", JOptionPane.YES_NO_OPTION);
                if (response == JOptionPane.YES_OPTION) {
                    pref.put("font", font);
                    pref.putInt("size", fontSize.intValue());
                    for (int i = 0; i < levels.length; i++) {
                        String prefix = prefices[i];
                        pref.putInt(prefix + "foreground", foregroundButtons[i].getBackground().getRGB());
                        pref.putInt(prefix + "background", backgroundButtons[i].getBackground().getRGB());
                        pref.putBoolean(prefix + "italic", italicBoxes[i].isSelected());
                        pref.putBoolean(prefix + "bold", boldBoxes[i].isSelected());
                    }
                }
                frame.hide();
            }
        });
        return okButton;
    }

    private JButton buildCancelButton() {
        JButton cancelButton = new JButton("Cancel");
        cancelButton.setMnemonic('c');
        cancelButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                reset();
                frame.hide();
            }
        });
        return cancelButton;
    }

    private void reset() {
        fontBox.setSelectedItem(font);
        fontSizesBox.setSelectedItem(fontSize);
        for (int i = 0; i < levels.length; i++) {
            backgroundButtons[i].setBackground(StyleConstants.getBackground(attributes[i]));
            foregroundButtons[i].setBackground(StyleConstants.getForeground(attributes[i]));
            italicBoxes[i].setSelected(StyleConstants.isItalic(attributes[i]));
            boldBoxes[i].setSelected(StyleConstants.isBold(attributes[i]));
        }
        resetPreview();
    }


    private static final String previewMessages[] = new String[]{
        "2002-06-09 11:04:34,325 [main] com.acme.Kitchen - servicing request TurnOnOven\n",
        "2002-06-09 11:06:17,326 [main] com.acme.Kitchen - oven.temp = 300 F\n",
        "2002-06-09 21:06:30,154 [main] com.acme.Kitchen - oven left on for ten hours!\n",
        "2002-06-09 21:06:45,300 [main] com.acme.Kitchen - the house is on fire!\n",
        "2002-06-09 21:07:00,120 [main] com.acme.Kitchen - we're dead!\n",
    };

    private void resetPreview() {
        logPreview.setEditable(false);
        logPreview.setText("");
        String font = (String) fontBox.getSelectedItem();
        Integer size = (Integer) fontSizesBox.getSelectedItem();
        MutableAttributeSet attr = new SimpleAttributeSet();
        Document doc = logPreview.getDocument();

        try {
            StyleConstants.setFontFamily(attr, font);
            StyleConstants.setFontSize(attr, size.intValue());
            for (int i = 0; i < levels.length; i++) {
                StyleConstants.setForeground(attr, foregroundButtons[i].getBackground());
                StyleConstants.setBackground(attr, backgroundButtons[i].getBackground());
                StyleConstants.setItalic(attr, italicBoxes[i].isSelected());
                StyleConstants.setBold(attr, boldBoxes[i].isSelected());
                doc.insertString(doc.getLength(), previewMessages[i], attr);
            }
        } catch (BadLocationException e) {
            logger.error(e);
        }
    }
}
