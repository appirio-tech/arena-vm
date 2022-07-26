/*
 * User: Michael Cervantes
 * Date: Sep 17, 2002
 * Time: 8:30:27 PM
 */
package com.topcoder.client.contestApplet.panels.main;

import com.topcoder.client.contestApplet.common.*;

import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.util.*;

public class TCCheckBoxMenuItem extends JCheckBoxMenuItem {

    private LocalPreferences pref = LocalPreferences.getInstance();

    public TCCheckBoxMenuItem(MenuItemInfo info) {
        super(info.getText());
        addActionListener(info.getActionListener());
        setMenuItemProperties();

        // POPS - 12/21/2001 - added restoration of check mark
        String propertyName = info.getPropertyName();
        if (propertyName != null) {
            setState(pref.isTrue(propertyName));
        }

        if (info.getHasMnemonic()) {
            setMnemonic(info.getMnemonic());
        }

        addObserver();
    }

    private void addObserver() {
        pref.addSaveObserver(new Observer() {
            public void update(Observable o, Object arg) {
                setFontFromPreferences();
            }
        });
    }

    private void setFontFromPreferences() {
        String font = pref.getFont(LocalPreferences.MENUFONT, "");
        int fontSize = pref.getFontSize(LocalPreferences.MENUFONTSIZE, 10);
        setFont(new Font(font, 0, fontSize));
        revalidate();
        repaint();
    }

    private void setMenuItemProperties() {
        setFontFromPreferences();
        setForeground(Color.white);
        setBackground(Common.MENU_COLOR);
        setBorder(new BevelBorder(BevelBorder.RAISED));
    }
}
