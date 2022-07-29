/*
 * Copyright (C) 2013 TopCoder Inc., All Rights Reserved.
 */
package com.topcoder.client.mpsqasApplet.view.defaultimpl;

import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;

import com.topcoder.client.mpsqasApplet.object.MainObjectFactory;
import com.topcoder.netCommon.mpsqas.ApplicationConstants;
import com.topcoder.netCommon.mpsqas.CustomBuildSetting;
import com.topcoder.netCommon.mpsqas.ProblemRoundType;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * This class is an UI helper class which provides some static helper methods
 * and static helper classes used to build UI.
 *
 * <p>
 * <strong>Change log:</strong>
 * </p>
 *
 * <p>
 * Version 1.1 (Release Assembly - Dynamic Round Type List For Long and Individual Problems):
 * <ol>
 * <li>
 * Added {@link #createProblemRoundTypeComboBox(int, int)}.
 * </li>
 * </ol>
 * </p>
 *
 * <p>
 * Version 1.2 (Release Assembly - TopCoder Competition Engine Improvement Series 2 v1.0):
 * <ol>
 * <li>
 * Updated inner class {@link com.topcoder.client.mpsqasApplet.view.defaultimpl.UIHelper.ComboItem}
 * to make ID to be of {@link java.lang.String} type.
 * </li>
 * <li>
 * Updated {@link #createProblemRoundTypeComboBox(int, int)} to work with updated
 * {@link com.topcoder.client.mpsqasApplet.view.defaultimpl.UIHelper.ComboItem}.
 * </li>
 * <li>
 * Added {@link #createCustomBuildSettingComboBox(int, String)}.
 * </li>
 * </ol>
 * </p>
 *
 * <p>
 * <strong>Thread Safety: </strong><br/>
 * This class is static and immutable. It's thread-safe.
 * </p>
 *
 * @author gevak, TCSASSEMBLER
 * @version 1.2
 */
public final class UIHelper {
    /**
     * Private empty constructor.
     */
    private UIHelper() {
    }

    /**
     * Build a new component which contains the existing component, and the width of the new
     * component will be fixed.
     *
     * @param component the existing component.
     * @param percentWidth the fixed width in percentage.
     * @return the new component.
     */
    public static JComponent sizeComponent(JComponent component, double percentWidth) {
        int width = (int) (100 * percentWidth);
        JPanel panel = new JPanel();

        GridBagLayout layout = new GridBagLayout();
        panel.setLayout(layout);
        GridBagConstraints gbc = new GridBagConstraints();

        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        GUIConstants.buildConstraints(gbc, 0, 0, 1, 1, width, 1);
        layout.setConstraints(component, gbc);
        panel.add(component);

        if (percentWidth < 1) {
            JComponent spacer = new JLabel();
            GUIConstants.buildConstraints(gbc, 1, 0, 1, 1, 100 - width, 0);
            layout.setConstraints(spacer, gbc);
            panel.add(spacer);
        }
        return panel;
    }

    /**
     * This class contains the ID-Value pairs which can be used by combo box.
     *
     * @author TCSASSEMBLER
     * @version 1.2
     */
    public static final class ComboItem {
        /**
         * The id of the combo box item.
         */
        private String id;
        /**
         * The value of the combo box item.
         */
        private String val;

        /**
         * Constructor with the id and the value.
         *
         * @param id the id of the combo box item.
         * @param val the value of the combo box item.
         */
        public ComboItem(String id, String val) {
            this.id = id;
            this.val = val;
        }

        /**
         * Gets the string represents of this object.
         *
         * @return the string represents of this object.
         */
        public String toString() {
            return val;
        }

        /**
         * Gets the id of this combo box item.
         *
         * @return the id of this combo box item.
         */
        public String getId() {
            return id;
        }

        /**
         * Compares whether two <code>ComboItem</code>s are equal.
         *
         * @param o another <code>ComboItem</code> to compare to.
         * @return true if two instances are equal, false otherwise.
         */
        public boolean equals(Object o) {
            ComboItem o2 = (ComboItem)o;
            return o2 != null  && o2.getId().equals(id);
        }
    }

    /**
     * Creates problem round type combo box.
     *
     * @param problemType Problem type ID.
     * @param roundType ID of round type to be selected by default (-1 if not specified).
     * @return Constructed combo box.
     * @since 1.1
     */
    public static JComboBox createProblemRoundTypeComboBox(int problemType, int roundType) {
        JComboBox roundTypeCombo = new JComboBox();
        ComboItem selected =  new ComboItem("-1", "Not specified");
        roundTypeCombo.addItem(selected);

        // incase the LookupValues is not set during the Login.
        if(MainObjectFactory.getLookupValues() != null) {
            List<ProblemRoundType> problemRoundTypes =
                    MainObjectFactory.getLookupValues().getProblemRoundTypes();
            for (ProblemRoundType problemRoundType : problemRoundTypes) {
                if (problemRoundType.getProblemType() == problemType) {
                    ComboItem comboItem = new ComboItem(problemRoundType.getId() + "",
                            problemRoundType.getDescription());
                    roundTypeCombo.addItem(comboItem);
                    if (problemRoundType.getId() == roundType) {
                        selected = comboItem;
                    }
                }
            }
        }
        roundTypeCombo.setSelectedItem(selected);
        return roundTypeCombo;
    }

    /**
     * Creates custom build settings combo box.
     *
     * @param customBuildSettingType Build settings type.
     *      See {@link com.topcoder.netCommon.mpsqas.CustomBuildSetting} constants for possible values.
     * @param currentValue Current value of the custom build setting.
     * @return Constructed combo box.
     * @since 1.2
     */
    public static JComboBox createCustomBuildSettingComboBox(int customBuildSettingType, String currentValue) {
        JComboBox combo = new JComboBox();
        ComboItem selected =  new ComboItem("", "Not specified");
        combo.addItem(selected);

        if(MainObjectFactory.getLookupValues() != null &&
                MainObjectFactory.getLookupValues().getCustomBuildSettings().containsKey(customBuildSettingType)) {
            ArrayList<CustomBuildSetting> customBuildSettings =
                    MainObjectFactory.getLookupValues().getCustomBuildSettings().get(customBuildSettingType);
            for (CustomBuildSetting customBuildSetting : customBuildSettings) {
                ComboItem comboItem = new ComboItem(customBuildSetting.getValue(),
                        customBuildSetting.getDescription());
                combo.addItem(comboItem);
                if (customBuildSetting.getValue().equals(currentValue)) {
                    selected = comboItem;
                }
            }
        }
        combo.setSelectedItem(selected);
        return combo;
    }
}
