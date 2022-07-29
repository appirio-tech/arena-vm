package com.topcoder.client.contestApplet.widgets.ui;

import java.awt.Color;
import javax.swing.border.TitledBorder;

import com.topcoder.client.contestApplet.widgets.RoundBorder;
import com.topcoder.client.contestApplet.widgets.MyTitledBorder;
import com.topcoder.client.ui.UIPage;
import com.topcoder.client.ui.UIPropertyValueParser;

public class MyTitledBorderParser implements UIPropertyValueParser {
    public Object parse(UIPage page, String value, ClassLoader loader) {
        String[] values = value.split(",", -1);

        if (values.length < 7) {
            throw new IllegalArgumentException("This parser requires at least 7 parameters.");
        }

        StringBuffer title = new StringBuffer(values[6]);

        for (int i=7; i < values.length; ++i) {
            title.append(',');
            title.append(values[i]);
        }

        int justification = 0;
        int position = 0;

        if ("left".equalsIgnoreCase(values[3])) {
            justification = TitledBorder.LEFT;
        } else if ("center".equalsIgnoreCase(values[3])) {
            justification = TitledBorder.CENTER;
        } else if ("right".equalsIgnoreCase(values[3])) {
            justification = TitledBorder.RIGHT;
        } else if ("leading".equalsIgnoreCase(values[3])) {
            justification = TitledBorder.LEADING;
        } else if ("trailing".equalsIgnoreCase(values[3])) {
            justification = TitledBorder.TRAILING;
        } else if ("default_justification".equalsIgnoreCase(values[3])) {
            justification = TitledBorder.DEFAULT_JUSTIFICATION;
        } else {
            throw new IllegalArgumentException("Titled border has an invalid justification.");
        }

        if ("above_top".equalsIgnoreCase(values[4])) {
            position = TitledBorder.ABOVE_TOP;
        } else if ("top".equalsIgnoreCase(values[4])) {
            position = TitledBorder.TOP;
        } else if ("below_top".equalsIgnoreCase(values[4])) {
            position = TitledBorder.BELOW_TOP;
        } else if ("above_bottom".equalsIgnoreCase(values[4])) {
            position = TitledBorder.ABOVE_BOTTOM;
        } else if ("bottom".equalsIgnoreCase(values[4])) {
            position = TitledBorder.BOTTOM;
        } else if ("below_bottom".equalsIgnoreCase(values[4])) {
            position = TitledBorder.BELOW_BOTTOM;
        } else if ("default_position".equalsIgnoreCase(values[4])) {
            position = TitledBorder.DEFAULT_POSITION;
        } else {
            throw new IllegalArgumentException("Titled border has an invalid position.");
        }

        MyTitledBorder border = new MyTitledBorder(new RoundBorder(Color.decode(values[0]), Integer.parseInt(values[1]),
                                                                   Boolean.valueOf(values[2]).booleanValue()),
                                                   title.toString(), justification, position);
        border.setTitleColor(Color.decode(values[5]));

        return border;
    }
}
