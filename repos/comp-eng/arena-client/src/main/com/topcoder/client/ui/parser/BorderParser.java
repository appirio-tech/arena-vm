package com.topcoder.client.ui.parser;

import java.awt.Color;
import java.awt.Font;
import java.util.ArrayList;
import java.util.List;
import javax.swing.BorderFactory;
import javax.swing.border.Border;
import javax.swing.border.BevelBorder;
import javax.swing.border.EtchedBorder;
import javax.swing.border.TitledBorder;

import com.topcoder.client.ui.UIPage;
import com.topcoder.client.ui.UIPropertyValueParser;

public class BorderParser implements UIPropertyValueParser {
    public Object parse(UIPage page, String value, ClassLoader loader) {
        return parse(value);
    }

    private Border parse(String value) {
        List list = new ArrayList();
        int brackets = 0;
        StringBuffer sb = new StringBuffer();

        for (int i=0;i<value.length();++i) {
            char ch = value.charAt(i);
            switch (ch) {
            case ',':
                if (brackets == 0) {
                    list.add(sb.toString());
                    sb.setLength(0);
                } else {
                    sb.append(ch);
                }
                break;
            case '(':
                ++brackets;
                sb.append(ch);
                break;
            case ')':
                --brackets;
                sb.append(ch);
                break;
            default:
                sb.append(ch);
                break;
            }
        }

        list.add(sb.toString());
        String[] values = (String[]) list.toArray(new String[0]);

        if ("empty".equalsIgnoreCase(values[0])) {
            if (values.length == 1) {
                return BorderFactory.createEmptyBorder();
            } else if (values.length != 5) {
                throw new IllegalArgumentException("Empty border needs 0 or 4 numbers.");
            }

            return BorderFactory.createEmptyBorder(Integer.parseInt(values[1]), Integer.parseInt(values[2]),
                                                   Integer.parseInt(values[3]), Integer.parseInt(values[4]));
        } else if ("bevel".equalsIgnoreCase(values[0])) {
            Border border = null;

            if (values.length < 2) {
                throw new IllegalArgumentException("Bevel border needs at least 1 parameter.");
            }

            int type = 0;

            if ("lowered".equalsIgnoreCase(values[1])) {
                type = BevelBorder.LOWERED;
            } else if ("raised".equalsIgnoreCase(values[1])) {
                type = BevelBorder.RAISED;
            } else {
                throw new IllegalArgumentException("Bevel border type is unknown.");
            }

            switch (values.length) {
            case 2:
                border = BorderFactory.createBevelBorder(type);
                break;

            case 4:
                border = BorderFactory.createBevelBorder(type, Color.decode(values[2]), Color.decode(values[3]));
                break;

            case 6:
                border = BorderFactory.createBevelBorder(type, Color.decode(values[2]), Color.decode(values[3]),
                                                         Color.decode(values[4]), Color.decode(values[5]));
                break;

            default:
                throw new IllegalArgumentException("The number of parameters for bevel border must be 1, 3, or 5.");
            }

            return border;
        } else if ("etched".equalsIgnoreCase(values[0])) {
            Border border = null;

            if (values.length < 2) {
                throw new IllegalArgumentException("Etched border needs at least 1 parameter.");
            }

            int type = 0;

            if ("lowered".equalsIgnoreCase(values[1])) {
                type = EtchedBorder.LOWERED;
            } else if ("raised".equalsIgnoreCase(values[1])) {
                type = EtchedBorder.RAISED;
            } else {
                throw new IllegalArgumentException("Etched border type is unknown.");
            }

            switch (values.length) {
            case 2:
                border = BorderFactory.createEtchedBorder(type);
                break;

            case 4:
                border = BorderFactory.createEtchedBorder(type, Color.decode(values[2]), Color.decode(values[3]));
                break;

            default:
                throw new IllegalArgumentException("The number of parameters for etched border must be 1 or 3.");
            }

            return border;
        } else if ("line".equalsIgnoreCase(values[0])) {
            Border border;

            switch (values.length) {
            case 2:
                border = BorderFactory.createLineBorder(Color.decode(values[1]));
                break;

            case 3:
                border = BorderFactory.createLineBorder(Color.decode(values[1]), Integer.parseInt(values[2]));
                break;

            default:
                throw new IllegalArgumentException("The number of parameters for line border must be 2 or 3.");
            }

            return border;
        } else if ("compound".equalsIgnoreCase(values[0])) {
            if (values.length != 3) {
                throw new IllegalArgumentException("Compound border must have two parameters.");
            }

            if (!values[1].startsWith("(") || !values[1].endsWith(")") || !values[2].startsWith("(") || !values[2].endsWith(")")) {
                throw new IllegalArgumentException("Two border definitions must be bracketed.");
            }

            return BorderFactory.createCompoundBorder(parse(values[1].substring(1, values[1].length() - 1)),
                                                        parse(values[2].substring(1, values[2].length() - 1)));
        } else if ("titled".equalsIgnoreCase(values[0])) {
            if (values.length < 2) {
                throw new IllegalArgumentException("Titled border must have at least two parameters.");
            }

            if (values.length > 2 && !(values[1].startsWith("(") && values[1].endsWith(")"))) {
                throw new IllegalArgumentException("Nested border definition must be bracketed.");
            }

            int justification = 0;
            int position = 0;

            if (values.length >= 5) {
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
            }

            Border border;

            switch (values.length) {
            case 2:
                if (values[1].startsWith("(") && values[1].endsWith(")")) {
                    border = BorderFactory.createTitledBorder(parse(values[1].substring(1, values[1].length() - 1)));
                } else {
                    border = BorderFactory.createTitledBorder(values[1]);
                }
                break;
            case 3:
                border = BorderFactory.createTitledBorder(parse(values[1].substring(1, values[1].length() - 1)),
                                                          values[2]);
                break;
            case 5:
                border = BorderFactory.createTitledBorder(parse(values[1].substring(1, values[1].length() - 1)),
                                                          values[2], justification, position);
                break;
            case 6:
                border = BorderFactory.createTitledBorder(parse(values[1].substring(1, values[1].length() - 1)),
                                                          values[2], justification, position, Font.decode(values[5]));
                break;
            case 7:
                border = BorderFactory.createTitledBorder(parse(values[1].substring(1, values[1].length() - 1)),
                                                          values[2], justification, position, Font.decode(values[5]),
                                                          Color.decode(values[6]));
                break;
            default:
                throw new IllegalArgumentException("Titled border must have 1, 2, 4, 5, or 6 parameters.");
            }

            return border;
        } else {
            throw new IllegalArgumentException("Unknown border type.");
        }
    }
}
