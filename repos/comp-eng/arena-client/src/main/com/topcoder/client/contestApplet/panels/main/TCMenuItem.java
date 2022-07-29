/*
* User: Michael Cervantes
* Date: Aug 8, 2002
* Time: 4:57:33 PM
*/
package com.topcoder.client.contestApplet.panels.main;

import com.topcoder.client.contestApplet.common.*;

import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.util.*;

class TCMenuItem extends JMenuItem {

    private Object userData;
    private LocalPreferences pref = LocalPreferences.getInstance();

    public TCMenuItem(MenuItemInfo info) {
        super(info.getText());
        addActionListener(info.getActionListener());
        setMenuItemProperties();
        if (info.getHasMnemonic()) {
            setMnemonic(info.getMnemonic());
        }
        addObserver();
    }

    public TCMenuItem(String text, char mnemonic) {
        this(text);
        setMnemonic(mnemonic);
        addObserver();
    }

    public TCMenuItem(String text) {
        super(text);
        setMenuItemProperties();
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
        String font = pref.getFont(LocalPreferences.MENUFONT, "Arial");
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

    public boolean hasUserData() {
        return userData != null;
    }

    public Object getUserData() {
        return userData;
    }

    public void setUserData(Object userData) {
        this.userData = userData;
    }
}
