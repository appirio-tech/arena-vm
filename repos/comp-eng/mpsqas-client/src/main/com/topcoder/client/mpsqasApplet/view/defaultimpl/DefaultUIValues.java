package com.topcoder.client.mpsqasApplet.view.defaultimpl;

import javax.swing.plaf.*;
import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;

/**
 * Responsible for setting the defaults in the UIManager.
 *
 * @author mitalub
 */
public class DefaultUIValues {

    public static final Font FIXED_WIDTH_FONT =
            new Font("Monospaced", Font.PLAIN, 11);
    public static final Font NORMAL_FONT =
            new Font("SansSerif", Font.PLAIN, 12);
    public static final Font BOLD_FONT =
            new Font("SansSerif", Font.BOLD, 12);
    public static final Font HEADER_FONT =
            new Font("SansSerif", Font.BOLD, 16);

    public static final Border LOWERED_BORDER =
            new BevelBorder(BevelBorder.LOWERED);
    public static final Border RAISED_BORDER =
            new BevelBorder(BevelBorder.RAISED);
    public static final Border RAISED_BUTTON_BORDER =
            new CompoundBorder(new BevelBorder(BevelBorder.RAISED),
                    new EmptyBorder(2, 12, 2, 12));

    public static final Insets NORMAL_INSETS = new Insets(5, 5, 5, 5);

    /**
     * Sets look and feel, and defaults.
     */
    public static void set() {
        try {
            UIManager.setLookAndFeel(
                    UIManager.getCrossPlatformLookAndFeelClassName());
        } catch (Exception e) {
            System.out.println("Error setting cross platform look and feel:");
            e.printStackTrace();
        }

        //Fonts:
        UIManager.put("TextArea.font", new FontUIResource(FIXED_WIDTH_FONT));
        UIManager.put("TextField.font", new FontUIResource(FIXED_WIDTH_FONT));
        UIManager.put("PasswordField.font", new FontUIResource(FIXED_WIDTH_FONT));
        UIManager.put("Label.font", new FontUIResource(NORMAL_FONT));
        UIManager.put("Button.font", new FontUIResource(NORMAL_FONT));
        UIManager.put("Menu.font", new FontUIResource(NORMAL_FONT));
        UIManager.put("MenuItem.font", new FontUIResource(NORMAL_FONT));
        UIManager.put("MenuBar.font", new FontUIResource(NORMAL_FONT));
        UIManager.put("ComboBox.font", new FontUIResource(NORMAL_FONT));
        UIManager.put("Table.font", new FontUIResource(NORMAL_FONT));
        UIManager.put("Tree.font", new FontUIResource(NORMAL_FONT));
        UIManager.put("List.font", new FontUIResource(NORMAL_FONT));
        UIManager.put("CheckBox.font", new FontUIResource(NORMAL_FONT));
        UIManager.put("CheckBox.font", new FontUIResource(NORMAL_FONT));
        UIManager.put("CheckBoxMenuItem.font", new FontUIResource(NORMAL_FONT));
        UIManager.put("ColorChooser.font", new FontUIResource(NORMAL_FONT));
        UIManager.put("TabbedPane.font", new FontUIResource(NORMAL_FONT));
        UIManager.put("DesktopIcon.font", new FontUIResource(NORMAL_FONT));
        UIManager.put("EditorPane.font", new FontUIResource(NORMAL_FONT));
        UIManager.put("FormattedTextField.font", new FontUIResource(NORMAL_FONT));
        UIManager.put("OptionPane.font", new FontUIResource(NORMAL_FONT));
        UIManager.put("Panel.font", new FontUIResource(NORMAL_FONT));
        UIManager.put("PopupMenu.font", new FontUIResource(NORMAL_FONT));
        UIManager.put("ProgressBar.font", new FontUIResource(NORMAL_FONT));
        UIManager.put("RadioButton.font", new FontUIResource(NORMAL_FONT));
        UIManager.put("RadioButtonMenuItem.font", new FontUIResource(NORMAL_FONT));
        UIManager.put("ScrollPane.font", new FontUIResource(NORMAL_FONT));
        UIManager.put("TableHeader.font", new FontUIResource(NORMAL_FONT));
        UIManager.put("TextPane.font", new FontUIResource(FIXED_WIDTH_FONT));
        UIManager.put("TitledBorder.font", new FontUIResource(NORMAL_FONT));
        UIManager.put("ToggleButton.font", new FontUIResource(NORMAL_FONT));
        UIManager.put("ToolBar.font", new FontUIResource(NORMAL_FONT));
        UIManager.put("ToolTip.font", new FontUIResource(NORMAL_FONT));
        UIManager.put("Viewport.font", new FontUIResource(NORMAL_FONT));

        //Borders:
        UIManager.put("ScrollPane.border", new BorderUIResource(LOWERED_BORDER));
        UIManager.put("TextField.border", new BorderUIResource(LOWERED_BORDER));
        UIManager.put("PasswordField.border", new BorderUIResource(LOWERED_BORDER));
        UIManager.put("Button.border", new BorderUIResource(RAISED_BUTTON_BORDER));


        //Colors:
        UIManager.put("TextArea.foreground", new ColorUIResource(Color.black));
        UIManager.put("TextField.foreground", new ColorUIResource(Color.black));
        UIManager.put("PasswordField.foreground", new ColorUIResource(Color.black));
        UIManager.put("Label.foreground", new ColorUIResource(Color.black));
        UIManager.put("Button.foreground", new ColorUIResource(Color.black));
        UIManager.put("Menu.foreground", new ColorUIResource(Color.black));
        UIManager.put("MenuItem.foreground", new ColorUIResource(Color.black));
        UIManager.put("MenuBar.foreground", new ColorUIResource(Color.black));
        UIManager.put("ComboBox.foreground", new ColorUIResource(Color.black));
        UIManager.put("Table.foreground", new ColorUIResource(Color.black));
        UIManager.put("Tree.foreground", new ColorUIResource(Color.black));
        UIManager.put("CheckBox.foreground", new ColorUIResource(Color.black));
        UIManager.put("CheckBox.foreground", new ColorUIResource(Color.black));
        UIManager.put("CheckBoxMenuItem.foreground",
                new ColorUIResource(Color.black));
        UIManager.put("ColorChooser.foreground", new ColorUIResource(Color.black));
        UIManager.put("TabbedPane.foreground", new ColorUIResource(Color.black));
        UIManager.put("DesktopIcon.foreground", new ColorUIResource(Color.black));
        UIManager.put("EditorPane.foreground", new ColorUIResource(Color.black));
        UIManager.put("FormattedTextField.foreground",
                new ColorUIResource(Color.black));
        UIManager.put("OptionPane.foreground", new ColorUIResource(Color.black));
        UIManager.put("Panel.foreground", new ColorUIResource(Color.black));
        UIManager.put("PopupMenu.foreground", new ColorUIResource(Color.black));
        UIManager.put("ProgressBar.foreground", new ColorUIResource(Color.black));
        UIManager.put("RadioButton.foreground", new ColorUIResource(Color.black));
        UIManager.put("RadioButtonMenuItem.foreground",
                new ColorUIResource(Color.black));
        UIManager.put("ScrollPane.foreground", new ColorUIResource(Color.black));
        UIManager.put("TableHeader.foreground", new ColorUIResource(Color.black));
        UIManager.put("TextPane.foreground", new ColorUIResource(Color.black));
        UIManager.put("TitledBorder.foreground", new ColorUIResource(Color.black));
        UIManager.put("ToggleButton.foreground", new ColorUIResource(Color.black));
        UIManager.put("ToolBar.foreground", new ColorUIResource(Color.black));
        UIManager.put("ToolTip.foreground", new ColorUIResource(Color.black));
        UIManager.put("Viewport.foreground", new ColorUIResource(Color.black));

        UIManager.put("TextArea.background", new ColorUIResource(Color.white));
        UIManager.put("TextField.background", new ColorUIResource(Color.white));
        UIManager.put("PasswordField.background", new ColorUIResource(Color.white));
        UIManager.put("Table.background", new ColorUIResource(Color.white));
        UIManager.put("Tree.background", new ColorUIResource(Color.white));
        UIManager.put("EditorPane.background", new ColorUIResource(Color.white));
        UIManager.put("FormattedTextField.background",
                new ColorUIResource(Color.white));
        UIManager.put("TextPane.background", new ColorUIResource(Color.white));
        UIManager.put("List.background", new ColorUIResource(Color.white));
        UIManager.put("Table.background", new ColorUIResource(Color.white));
        UIManager.put("Tree.background", new ColorUIResource(Color.white));

        UIManager.put("TextField.inactiveBackground",
                new ColorUIResource(Color.lightGray));
        UIManager.put("TextArea.inactiveForeground",
                new ColorUIResource(Color.black));
        UIManager.put("TextArea.inactiveBackground",
                new ColorUIResource(Color.white));
        UIManager.put("TextArea.inactiveForeground",
                new ColorUIResource(Color.black));
        UIManager.put("PasswordField.inactiveBackground",
                new ColorUIResource(Color.lightGray));
        UIManager.put("PasswordField.inactiveForeground",
                new ColorUIResource(Color.black));
    }
}
