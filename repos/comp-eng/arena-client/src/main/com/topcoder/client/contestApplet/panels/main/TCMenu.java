/*
 * User: Michael Cervantes
 * Date: Aug 8, 2002
 * Time: 4:57:22 PM
 */
package com.topcoder.client.contestApplet.panels.main;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionListener;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Observable;
import java.util.Observer;

import javax.swing.JMenu;
import javax.swing.UIManager;
import javax.swing.border.BevelBorder;

import com.topcoder.client.contestApplet.common.Common;
import com.topcoder.client.contestApplet.common.LocalPreferences;
import com.topcoder.client.contestant.RoundModel;
import com.topcoder.netCommon.contestantMessages.response.data.CategoryData;

public class TCMenu extends JMenu {

    private LocalPreferences pref = LocalPreferences.getInstance();

    public TCMenu(String s, int width, int height, char mnemonic) {
        this(s);
        // set size
        Dimension dim = new Dimension(width, height);
        setMinimumSize(dim);
        setPreferredSize(dim);
        setMnemonic(mnemonic);

        addObserver();
    }

    private void addObserver() {
        pref.addSaveObserver(new Observer() {
            public void update(Observable o, Object arg) {
                setFontFromPreferences();
            }
        });
    }

    TCMenu(String s) {
        super(s);
        setMenuProperties();
        addObserver();
    }

    TCMenu(String s, char mnemonic) {
        super(s);
        setMnemonic(mnemonic);
        setMenuProperties();
        addObserver();
    }


    private void setFontFromPreferences() {
        String font = pref.getFont(LocalPreferences.MENUFONT, "");
        int fontSize = pref.getFontSize(LocalPreferences.MENUFONTSIZE, 10);
        setFont(new Font(font, 0, fontSize));
        revalidate();
        repaint();
    }


    private void setMenuProperties() {
        // set menu properties
        setFontFromPreferences();
        setBorder(new BevelBorder(BevelBorder.RAISED));
        setBackground(Common.TOP_BACK);
        setForeground(Color.white);

        UIManager.put("SubMenu.selectionBackground", Color.decode("0x660000"));
    }

    /** SHOULD NOT BE NECESSARY **/
    public void buildIndexedCascadingMenu(String[] names, Object[] userData, ActionListener actionListener) {
//        // clear out the menu first
//        removeAll();
        UIManager.put("SubMenu.selectionBackground", Color.decode("0x660000"));
        String size = "" + names.length;
        int ndigits = size.length();
        buildMenu(this, names, 0, names.length, ndigits - 1, actionListener, userData);
    }
    
    // MAXIMUM SIZE FOR CASCADING PRACTICE ROOM MENU
    private static final int MENU_SIZE = 25;
    
    // gdorman - 5/26/2006 - added buildCategorizedCascadingMenu
    public void buildCategorizedCascadingMenu(RoundModel[] rounds, CategoryData[] categories, ActionListener listener) {
        UIManager.put("SubMenu.selectionBackground", Color.decode("0x660000"));
        Map categoryMap = getCategoryMap(categories);
        
        // Cycle through each category ID
        Iterator iterator = categoryMap.keySet().iterator();
        int categoryNumber = 0;
        while(iterator.hasNext()) {
            Integer currentID = (Integer) iterator.next();
            java.util.List currentRounds = new ArrayList();
            
            // Get all rounds associated with current category ID
            for(int currentRound = 0; currentRound < rounds.length; currentRound++) {
                if (rounds[currentRound].getRoundCategoryID() == currentID.intValue()) {
                    currentRounds.add(rounds[currentRound]);
                }
            }
            
            // Only create menu if there is at least 1 round in it
            if (!currentRounds.isEmpty()) {
                
                // Add menu for category
                String str = "" + ++categoryNumber;
                JMenu menu = new TCMenu(str + " - " + categoryMap.get(currentID), 120, 14, str.charAt(0));
                this.add(menu);
                
                Object[] roundsArray = currentRounds.toArray();
                
                // Build sub-menus if there are too many items
                if (roundsArray.length > MENU_SIZE) {
                    for(int subCount = 0; subCount*MENU_SIZE < roundsArray.length; subCount++) {
                        int start = subCount*MENU_SIZE;
                        int end = (subCount+1)*MENU_SIZE;
                        if (roundsArray.length < end) {
                            end = roundsArray.length;
                        }
                        str = (start + 1) + " - " + end;
                        JMenu subMenu = new TCMenu(str,120,14,str.charAt(0));
                        menu.add(subMenu);
                        RoundModel[] roundData = new RoundModel[end-start];
                        for(int i = start; i < end; i++) {
                            if (roundsArray[i] instanceof RoundModel) {
                                roundData[i-start] = (RoundModel) roundsArray[i];
                            }
                        }
                        buildSubMenu(subMenu,roundData,start,listener);
                    }
                    
                // Put menu items directly on category menu
                } else {
                    RoundModel[] roundData = new RoundModel[roundsArray.length];
                    for(int i = 0; i < roundsArray.length; i++) {
                        if (roundsArray[i] instanceof RoundModel) {
                            roundData[i] = (RoundModel) roundsArray[i];
                        }
                    }
                    buildSubMenu(menu,roundData,0,listener);
                }
            }
        }

        this.revalidate();
        this.repaint();
    }
    
    private static void buildSubMenu(JMenu parent, RoundModel[] rounds, int start, ActionListener listener) {
        for(int i = 0; i < rounds.length; i++) {
            String str = "" + (i + start + 1);
            TCMenuItem jmi = new TCMenuItem((i + start + 1) + " - " + rounds[i].getSingleName(), str.charAt(str.length() - 1));
            jmi.setUserData(rounds[i]);
            jmi.addActionListener(listener);
            parent.add(jmi);
        }
    }
    
    private static Map getCategoryMap(CategoryData[] categories) {
        Map map = new HashMap();
        for(int i = 0; i < categories.length; i++) {
            map.put(new Integer(categories[i].getCategoryID()),categories[i].getCategoryName());
        }
        return map;
    }
    
    private static DecimalFormat fmt = new DecimalFormat();

    /** SHOULD NOT BE NECESSARY **/
    private static void buildMenu(JMenu parent, String[] names, int start, int end, int power, ActionListener listener, Object[] userData) {
        if (power > 0) {
            fmt.setMaximumFractionDigits(0);
            fmt.setMinimumIntegerDigits(power + 1);
            int factor = (int) Math.pow(10, power);
            for (int i = start + factor; i <= end; i += factor) {
                String s = fmt.format(i - factor + 1);
                JMenu menu = new TCMenu(s + "-" + fmt.format(i), 120, 14, s.charAt(s.length() - power - 1));
                buildMenu(menu, names, i - factor, i, power - 1, listener, userData);
                parent.add(menu);
            }
            int residue = (end - start) % factor;
            if (residue > 0) {
                String s = fmt.format((end - residue + 1));
                JMenu menu = new TCMenu(s + "-" + fmt.format(end), 120, 14, s.charAt(s.length() - power - 1));
                buildMenu(menu, names, end - residue, end, power - 1, listener, userData);
                parent.add(menu);
            }
        } else {
            for (int i = start; i < end; i++) {
                String s = "" + (i + 1);
                TCMenuItem jmi = new TCMenuItem(names[i], s.charAt(s.length() - 1));
                jmi.setUserData(userData[i]);
                jmi.addActionListener(listener);
                parent.add(jmi);
            }
        }
        parent.revalidate();
        parent.repaint();
    }


    /**
     * Generically creates a new menu.
     *
     * First, clears <code>menu</code>.  Then, fills <code>menu</code> with JMenuItems
     * with text drawn from <code>contests</code>
     * and enabled / disabled state determined by <code>contestStati</code> and adds
     * <code>actionListener</code> to each created menu item.
     *
     * @param	names	ArrayList of String labels for the items
     * @param	enabled	ArrayList of Strings.  An "A" indicates the item should be enabled;
     *				anything else indicates the item should be disabled.
     * @param	actionListener	ActionListener that will be added to each menu item.
     */
    public void addToMenu(String[] names, boolean[] enabled, Object[] userData, ActionListener actionListener) {
        // clear out the menu first
        removeAll();
        boolean[] usedMnemonics = new boolean[26];

        UIManager.put("SubMenu.selectionBackground", Color.decode("0x660000"));

        // add menu items and events
        for (int i = 0; i < names.length; i++) {
            String name = names[i];
            String lower = name.toLowerCase();
            char mnemonic = '\0';
            for (int j = 0; j < lower.length(); j++) {
                char c = lower.charAt(j);
                if (Character.isLetter(c) && !usedMnemonics[c - 'a']) {
                    usedMnemonics[c - 'a'] = true;
                    mnemonic = c;
                    break;
                }
            }
            TCMenuItem jmi = new TCMenuItem(name, mnemonic);
            jmi.setForeground(Color.white);
            jmi.setBackground(Common.MENU_COLOR);
            jmi.setBorder(new BevelBorder(BevelBorder.RAISED));
            jmi.addActionListener(actionListener);
            jmi.setEnabled(enabled[i]);
            jmi.setMnemonic(mnemonic);
            jmi.setUserData(userData[i]);
            add(jmi);
        }
    }
}
