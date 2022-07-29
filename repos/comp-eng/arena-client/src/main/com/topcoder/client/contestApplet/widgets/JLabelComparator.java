package com.topcoder.client.contestApplet.widgets;

/**
 * JLabelComparator.java
 *
 * Description:		Comparator to compare the text of two JLabels
 * @author			Tim "Pops" Roberts (troberts@bigfoot.com)
 * @version			1.0
 */

import java.util.Comparator;
import javax.swing.JLabel;

public class JLabelComparator implements Comparator {

    boolean caseSensitive;

    public JLabelComparator(boolean caseSensitive) {
        this.caseSensitive = caseSensitive;
    }

    public int compare(Object a, Object b) {
        if (caseSensitive) {
            return ((JLabel) a).getText().compareTo(((JLabel) b).getText());
        } else {
            return ((JLabel) a).getText().compareToIgnoreCase(((JLabel) b).getText());
        }
    }

    public boolean equals(Object b) {
        return b instanceof JLabelComparator;
    }

}
