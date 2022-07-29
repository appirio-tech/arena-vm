package com.topcoder.client.contestApplet.common;

/*
* Common.java
*
* Created on July 10, 2000, 4:08 PM
*/

//import java.net.*;

import java.applet.AppletContext;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Vector;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JEditorPane;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.SwingConstants;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;

import com.topcoder.client.contestApplet.ContestApplet;
import com.topcoder.client.contestApplet.uilogic.frames.ArgInputDialog;
import com.topcoder.client.contestApplet.uilogic.frames.ArrayListInputDialog;
import com.topcoder.client.contestApplet.uilogic.frames.FrameLogic;
import com.topcoder.client.contestApplet.widgets.ContestListCellRenderer;
import com.topcoder.client.contestApplet.widgets.FillingOvalIcon;
import com.topcoder.client.contestApplet.widgets.MyTitledBorder;
import com.topcoder.client.contestApplet.widgets.NoIcon;
import com.topcoder.client.contestApplet.widgets.OvalIcon;
import com.topcoder.client.contestApplet.widgets.RoundBorder;
import com.topcoder.client.contestApplet.widgets.TCIcon;
import com.topcoder.client.contestApplet.widgets.TargetIcon;
import com.topcoder.client.contestant.ProblemComponentModel;
import com.topcoder.client.ui.UIComponent;
import com.topcoder.shared.language.JavaLanguage;
import com.topcoder.shared.problem.DataType;

/**
 *
 * @author  Alex Roman
 * @version 1.0
 */
public class Common {

    // applet frame viewable dimensions
    public static final String COMP_TOPCODER_HS = "TopCoderHS";
    public static final String COMP_TOPCODER = "TopCoder";
    public static final String COMP_GOOGLE = "Google";
    public static final String COMP_VERIZON = "Verizon";
    public static final String COMP_NVIDIA = "Nvidia";
    public static final String COMP_DBLCLK = "DblClk";

    public static final int WIDTH = 795;
    public static final int HEIGHT = 570;

    // competition modes
    public static final int SINGLE = 0;
    public static final int TEAM = 1;

    // user modes
    public static final int SINGLE_USER = 0;
    public static final int TEAM_MEMBER = 1;
    public static final int TEAM_CAPTAIN = 2;

    private static final String SERVER_NAME = "www.topcoder.com";
    private static final String HIGHSCHOOL_SERVER_NAME = "highschool.topcoder.com";
    //public static final String SERVER_NAME = "192.168.1.34";
    public static final String URL_API = "";
    public static final String URL_MAN = "http://" + SERVER_NAME + "/tc?module=Static&d1=help&d2=competitionFaq";
    public static final String URL_PLUGINS = "http://" + SERVER_NAME + "/tc?module=Static&d1=applet&d2=plugins";
    public static final String URL_REG = "http://" + SERVER_NAME + "/reg/";
    public static final String URL_REG_HS = "http://" + HIGHSCHOOL_SERVER_NAME + "?module=Static&d1=registration&d2=registration";
    
    // May 15, 2013 -- all 3 links should point to http://apps.topcoder.com/wiki/display/tc/The+TopCoder+Platform+-+Software+Application+Development+Methodology
    public static final String URL_SOFTWARE = "http://apps.topcoder.com/wiki/display/tc/The+TopCoder+Platform+-+Software+Application+Development+Methodology";
    public static final String URL_SOFTWARE_DESIGN = "http://apps.topcoder.com/wiki/display/tc/The+TopCoder+Platform+-+Software+Application+Development+Methodology";
    public static final String URL_SOFTWARE_DEVELOPMENT = "http://apps.topcoder.com/wiki/display/tc/The+TopCoder+Platform+-+Software+Application+Development+Methodology";
    
    public static final String URL_EMPLOYMENT = "http://" + SERVER_NAME + "/?&t=tces&c=index";
    public static final String URL_TOPCODER = "http://" + SERVER_NAME;
    public static final String URL_CMP_FAQ = "http://" + SERVER_NAME + "/tc?module=Static&d1=help&d2=competitionFaq";

    // main application colors
    private static final Color BLACK = Color.black;
    private static final Color WHITE = Color.white;
    private static final Color GREEN = Color.decode("0xCCFF99");
    //private static final Color GREEN = Color.decode("0x66BB66");
    //private static final Color GREEN = Color.decode("0x33FF33");
    private static final Color DARK_GREEN = Color.decode("0x003300");
    public static final Color LIGHT_GREEN = Color.decode("0x006600");
    public static final Color RED = Color.decode("0x990000");
    private static final Color GREY = Color.decode("0x333333");
    //private static final Color GREY = Color.decode("0xCCCCCC");
    public static final Color LIGHT_GREY = Color.decode("0x999999");
    private static final Color MAROON = Color.decode("0x330000");
    //private static final Color MAROON = Color.decode("0x470000");

    //protected Color bgColor = Color.decode("0xFFCC99");
    public static final Color MENU_COLOR = RED;
    public static final Color STATUS_COLOR = GREEN;
    //public static final Color RIGHT_ARROWS = GREEN;
    public static final Color TIMER_COLOR = GREEN;
    public static final Color TOP_BACK = MAROON;
    public static final Color COMPS_RN = GREEN;
    public static final Color COMPS_CN = WHITE;
    public static final Color FG_COLOR = WHITE;
    public static final Color BG_COLOR = BLACK;
    public static final Color MF_COLOR = WHITE;
    public static final Color MB_COLOR = BLACK;
    public static final Color TF_COLOR = WHITE;
    public static final Color TB_COLOR = BLACK;
    public static final Color THF_COLOR = GREEN;
    public static final Color THB_COLOR = GREY;
    //public static final Color WPB_COLOR = WHITE;
    public static final Color WPB_COLOR = GREY;
    public static final Color PT_COLOR = GREEN;
    //public static final Color PT_COLOR = WHITE;
    public static final Color PB_COLOR = BLACK;
    //public static final Color PB_COLOR = GREEN;
    public static final Color ID_COLOR = WHITE;
    public static final Color HF_COLOR = WHITE;
    public static final Color HB_COLOR = DARK_GREEN;
    //public static final Color HB_COLOR = GREY;
    //public static final Color SF_COLOR = Color.decode("0x666666");
    //public static final Color ST_COLOR = GREEN;
    private static final Color NE_COLOR = GREEN;
    //public static final Color NX_COLOR = WHITE;
    //public static final Color NP_COLOR = RED;

    // Default font
    public static final String DEFFONT = "Monospaced";

    // Ranking Stuff

    public static final String[] legendLabels = {"2200+", "1500-2199", "1200-1499", "0900-1199", "0001-0899", "NON-RATED", "ADMIN"};
    public static final int[] legendRanks = {2200, 1500, 1200, 900, 1, 0, -1};
    
    //public static final String[] legendLabels = {"ADMIN", "2200+", "1500-2199", "1200-1499", "0900-1199", "0001-0899", "NON-RATED"};
    //public static final int[] legendRanks = {-1, 2200, 1500, 1200, 900, 1, 0};

    private static final int[] rankBreaks = {3000, 2200, 1500, 1200, 900, 1, 0, Integer.MIN_VALUE};

    //public static final Color CODER_RED = Color.decode("0xFF3333");
    //public static final Color CODER_YELLOW = Color.decode("0xFF5555");
    //public static final Color CODER_BLUE = Color.decode("0xFF7777");
    //public static final Color CODER_GREEN = Color.decode("0xFF9999");
    //public static final Color CODER_GREY = Color.decode("0xFFCCCC");
    //public static final Color CODER_WHITE = Color.decode("0xFFFFFF");
    //public static final Color CODER_ADMIN = Color.decode("0xFF0000");
    
    public static final Color CODER_RED = Color.decode("0xee0000");
    public static final Color CODER_YELLOW = Color.decode("0xddcc00");
    public static final Color CODER_BLUE = Color.decode("0x6666ff");
    public static final Color CODER_GREEN = Color.decode("0x00a900");
    public static final Color CODER_GREY = Color.decode("0x999999");
    public static final Color CODER_WHITE = Color.white;
    public static final Color CODER_ADMIN = Color.decode("0xff9900");

    private static final Color[] rankColors = {CODER_RED, // Red
                                               CODER_RED, // Red
                                               CODER_YELLOW, // Yellow
                                               CODER_BLUE, // Blue
                                               CODER_GREEN, // Green
                                               CODER_GREY, // Grey
                                               CODER_WHITE,
                                               CODER_ADMIN};  // Admin orange

    private static final Class[] rankIcons = {TargetIcon.class,
                                              FillingOvalIcon.class,
                                              FillingOvalIcon.class,
                                              FillingOvalIcon.class,
                                              FillingOvalIcon.class,
                                              FillingOvalIcon.class,
                                              NoIcon.class,
                                              NoIcon.class};

    /*private static final Class[] rankIcons = {FillingOvalIcon.class,
                                              FillingOvalIcon.class,
                                              FillingOvalIcon.class,
                                              FillingOvalIcon.class,
                                              FillingOvalIcon.class,
                                              FillingOvalIcon.class,
                                              NoIcon.class,
                                              TargetIcon.class};*/
    // chat settings
    public static final int MAX_CHAT = 200;
    public static final int MAX_NOSCROLL_CHAT = 2000;

    ////////////////////////////////////////////////////////////////////////////////
    public static String replaceAll(String from, String to, String buf)
            ////////////////////////////////////////////////////////////////////////////////
    {
        int index = 0;
        int position = 0;
        String result = buf;

        while (index != -1) {
            index = result.indexOf(from, position);
            if (index != -1) {
                if ((index + from.length() == result.length()) || !Character.isLetter(result.charAt(index + from.length()))) {
                    result = result.substring(0, index) +
                            to +
                            result.substring(index + from.length());
                    position = index + to.length();
                } else {
                    position = index + from.length();
                }
            }
        }

        return (result);
    }


    ////////////////////////////////////////////////////////////////////////////////
    public static JLabel getCoderLabel(String username, int rank) {
        Color c = getRankColor(rank);
        JLabel l = new JLabel(username, getRankIcon(rank), SwingConstants.LEFT);
        l.setForeground(c);

        if (rank == -1) {
            l.setFont(new Font("", (Font.BOLD | Font.ITALIC), 13));
        }

        return (l);
    }

    ////////////////////////////////////////////////////////////////////////////////
    public static Color getRankColor(int rank) {
        // Loop the the ranks looking for the matching rank
        // If found - return the color related to it
        for (int x = 0; x < rankBreaks.length; x++) {
            if (rank >= rankBreaks[x]) return rankColors[x];
        }

        // Unknown - really not possible but just in case
        return Color.white;
    }

    ////////////////////////////////////////////////////////////////////////////////
    public static Icon getRankIcon(int rank) {
        // Loop the the ranks looking for the matching rank
        // If found - return the icon related to it
        for (int x = 0; x < rankBreaks.length; x++) {
            if (rank >= rankBreaks[x]) {
                try {
                    // Create an instance of the icon for this rank
                    Icon rankIcon = (Icon) rankIcons[x].newInstance();

                    // If its a TCIcon - set it's foreground color to the rank's color
                    if (rankIcon instanceof TCIcon) ((TCIcon) rankIcon).setForeground(getRankColor(rank));

                    // If its a fillingovalicon, set it's percentage filled
                    if (rankIcon instanceof FillingOvalIcon) {
                        // Calculate the percentage within the category
                        double perc = (double) (rank - rankBreaks[x]) / (double) (((x == 0) ? rankBreaks[x] * 2 : rankBreaks[x - 1]) - rankBreaks[x]);
                        ((FillingOvalIcon) rankIcon).setPercentage(perc);
                    }

                    // Return the icon
                    return rankIcon;

                } catch (Exception e) {
                    // Problem - just return a white oval icon
                    return new OvalIcon(Color.white);
                }
            }

        }

        // Unknown - really not possible but just in case...
        return new OvalIcon(Color.white);
    }

    ////////////////////////////////////////////////////////////////////////////////
    public static ImageIcon getImage(String name, Object myclass)
            ////////////////////////////////////////////////////////////////////////////////
    {
        return getImage(name, myclass.getClass());
    }

    ////////////////////////////////////////////////////////////////////////////////
    public static ImageIcon getImage(String name, Class myclass)
            ////////////////////////////////////////////////////////////////////////////////
    {
        ImageIcon image = null;
        try {
            image = new ImageIcon(Toolkit.getDefaultToolkit().getImage(myclass.getResource("images/" + name)));
        } catch (Exception e) {
            //System.out.println("Error Loading image: images/" + name);
            image = new ImageIcon();
        }

        return (image);
    }

    ////////////////////////////////////////////////////////////////////////////////
    public static JButton getTextButton(String name)
            ////////////////////////////////////////////////////////////////////////////////
    {
        JButton button = new JButton(name);
        button.setForeground(Common.NE_COLOR);
        button.setBackground(Color.black);
        button.setBorder(new EmptyBorder(0, 0, 0, 0));
        button.setFocusPainted(false);
        button.setContentAreaFilled(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return (button);
    }

    /**
     * @author  Matthew P. Suhocki
     */
    ////////////////////////////////////////////////////////////////////////////////
    public static JButton getButton(String name)
            ////////////////////////////////////////////////////////////////////////////////
    {
        JButton button = new JButton(name);
        button.setForeground(Color.white);
        button.setBackground(Color.black);

        return (button);
    }

    ////////////////////////////////////////////////////////////////////////////////
    public static JButton getImageButton(String imageName, Object myclass)
            ////////////////////////////////////////////////////////////////////////////////
    {
        JButton button = new JButton(getImage(imageName, myclass));
        button.setBorder(new EmptyBorder(0, 0, 0, 0));
        button.setFocusPainted(false);
        button.setContentAreaFilled(false);

        return (button);
    }

    private static final String prepend = "<HTML><BODY BGCOLOR=\"#000000\" TEXT=\"#FFFFFF\" LINK=\"#D00000\" ALINK=\"#FF0000\" VLINK=\"#900000\">";
    private static final String append = "</BODY></HTML>";

    public static String htmlEncode(String s) {
        StringBuffer sb = htmlEncodeNoHeaders(s);
        return prepend + sb.toString() + append;
    }


    public static StringBuffer htmlEncodeNoHeaders(String s) {
        StringBuffer sb = new StringBuffer();
        char ch = ' ';
        boolean escape = false;
        for (int i = 0; i < s.length(); i++) {
            if ((ch = s.charAt(i)) == '\\' && !escape) {
                escape = true;
                ch = s.charAt(++i);
            }
            if (!escape) {
                sb.append(ch);
            } else if (ch == '\\') {
                sb.append('\\');
            } else if (ch == '>') {
                sb.append("&gt;");
            } else if (ch == 9) {  //we'll go with 2 spaces for a tab
                sb.append("  ");
            } else if (ch == '<') {
                sb.append("&lt;");
            } else if (ch == 10 || ch == 13) {  // let line feed and carriage return go
                sb.append(ch);
            } else if (((int) ch) < 32) {
                //anything less than a "space" character is technically unprintable
                sb.append("[\\u" + (int) ch + "]");
            } else if ((((int) ch) > 126) && (((int) ch) < 160)) {
                //anything in this range is unprintable per latin-1
                sb.append("[\\u" + (int) ch + "]");
            } else if ((((int) ch) >= 160) && (((int) ch) <= 255)) {
                //anything in this range is printable per latin-1 with a little massaging
                sb.append("&#" + (int) ch + ";");
            } else if ((int) ch > 255) {
                //anything in this range is unprintable per latin-1
                //html4.0 has some support but it isn't worth picking out a few cases that
                //some browsers won't display properly.
                sb.append("[\\u" + (int) ch + "]");
            } else
                sb.append(ch);
            escape = false;
        }
        return sb;
    }

    public static void showURL(AppletContext arenaContext, URL url) {
        boolean shown = false;
        try {
            shown = URLLoader.showURL(url);
        } catch (Throwable t) {
//            t.printStackTrace();
        }
        //System.out.println(shown);
        try {
            if (!shown) {
                arenaContext.showDocument(url, "_blank");
            }
        } catch (Throwable t) {
//            t.printStackTrace();
        }
    }

    /**
     */
    public static JCheckBox getCheckBox(String title) {
        JCheckBox jcb = new JCheckBox(title);
        jcb.setBackground(Color.black);
        jcb.setForeground(Color.white);
        jcb.setContentAreaFilled(false);
        jcb.setOpaque(false);
        return jcb;
    }

    // ------------------------------------------------------------
    // Retrieve custom titled border
    // ------------------------------------------------------------
    ////////////////////////////////////////////////////////////////////////////////
    public static TitledBorder getTitledBorder(String title) {
        Border border = new RoundBorder(PB_COLOR, 5, true);
        MyTitledBorder tb = new MyTitledBorder(border, title, TitledBorder.LEFT, TitledBorder.ABOVE_TOP);
        tb.setTitleColor(PT_COLOR);

        return (tb);
    }

    // ------------------------------------------------------------
    // Add a specific element in a specific position in a panel
    // ------------------------------------------------------------
    ////////////////////////////////////////////////////////////////////////////////
    public static void insertInPanel(JComponent c, Container p, GridBagConstraints g, int x, int y, int cs, int rs) {
        g.gridx = x;
        g.gridy = y;
        g.gridwidth = cs;
        g.gridheight = rs;
        p.add(c, g);
    }


    // ------------------------------------------------------------
    // Add a specific element in a specific position in a panel w/weight
    // ------------------------------------------------------------
    public static void insertInPanel(JComponent c, Container p, GridBagConstraints g,
            int x, int y, int cs, int rs, double cw, double rw) {
        g.weightx = cw;
        g.weighty = rw;
        insertInPanel(c, p, g, x, y, cs, rs);
    }

    // ------------------------------------------------------------
    // Set default parameters for the GridBagLayout
    // ------------------------------------------------------------
    ////////////////////////////////////////////////////////////////////////////////
    public static GridBagConstraints getDefaultConstraints()
            ////////////////////////////////////////////////////////////////////////////////
    {
        GridBagConstraints gbc = new GridBagConstraints();

        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 1;
        gbc.gridheight = 1;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.anchor = GridBagConstraints.NORTH;
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.ipadx = 0;
        gbc.ipady = 0;
        gbc.weightx = 0.1;
        gbc.weighty = 0.1;

        return gbc;
    }

    ////////////////////////////////////////////////////////////////////////////////
    public static void setLocationRelativeTo(Component baseComp, Component popup)
            ////////////////////////////////////////////////////////////////////////////////
    {
        popup.setLocation(baseComp.getX() + (baseComp.getWidth() / 2 - popup.getWidth() / 2),
                baseComp.getY() + (baseComp.getHeight() / 2 - popup.getHeight() / 2));
    }

    public static void setLocationRelativeTo(UIComponent baseComp, UIComponent popup) {
        popup.setProperty("Location", new Point(((Integer) baseComp.getProperty("X")).intValue() + (((Integer) baseComp.getProperty("Width")).intValue()/2 - ((Integer) popup.getProperty("Width")).intValue()/2), ((Integer) baseComp.getProperty("Y")).intValue() + (((Integer) baseComp.getProperty("Height")).intValue()/2 - ((Integer) popup.getProperty("Height")).intValue()/2)));
    }

    public static void setLocationRelativeTo(Component baseComp, UIComponent popup) {
        popup.setProperty("Location", new Point(baseComp.getX() + baseComp.getWidth()/2 - ((Integer) popup.getProperty("Width")).intValue()/2, baseComp.getY() + baseComp.getHeight()/2 - ((Integer) popup.getProperty("Height")).intValue()/2));
    }

    // ------------------------------------------------------------
    // Display popup window
    // ------------------------------------------------------------

    ////////////////////////////////////////////////////////////////////////////////
    public static void showMessage(String title, String msg, Component comp)
            ////////////////////////////////////////////////////////////////////////////////
    {
        JOptionPane.showMessageDialog(comp, msg, title, JOptionPane.INFORMATION_MESSAGE);
    }

    // ------------------------------------------------------------
    // Confirm action through popup window
    // ------------------------------------------------------------
    ////////////////////////////////////////////////////////////////////////////////
    public static boolean confirm(String title, String msg, Component comp)
            ////////////////////////////////////////////////////////////////////////////////
    {
        int choice = JOptionPane.showConfirmDialog(comp, msg, title,
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE);

        if (choice == JOptionPane.YES_OPTION) {
            return (true);
        }

        return (false);
    }


    private static final SimpleDateFormat dateFormatter =
            new SimpleDateFormat("MM/dd/yy hh:mm aaa z");

    public static String formatTime(long time) {
        return dateFormatter.format(new Date(time));
    }


    ////////////////////////////////////////////////////////////////////////////////
    public static ArrayList showArrayListInput(ContestApplet ca, UIComponent comp, String title)
            ////////////////////////////////////////////////////////////////////////////////
    {
        return (showArrayListInput(ca, comp, new ArrayList(), title));
    }

    ////////////////////////////////////////////////////////////////////////////////
    public static ArrayList showArrayListInput(ContestApplet ca, UIComponent comp, ArrayList al, String title)
            ////////////////////////////////////////////////////////////////////////////////
    {
        ArrayListInputDialog dialog = new ArrayListInputDialog(ca, comp, al, title);
        ArrayList info = dialog.showDialog();
        return (info);
    }

    ////////////////////////////////////////////////////////////////////////////////
    public static void showArgInput(ContestApplet ca, DataType[] params, ArrayList args, FrameLogic comp, boolean confirm)
            ////////////////////////////////////////////////////////////////////////////////
    {
        ArgInputDialog dialog = new ArgInputDialog(ca, comp, params, args, confirm);
        dialog.showDialog();
    }

    ////////////////////////////////////////////////////////////////////////////////
    public static void showArgInput(ContestApplet ca, String msg, DataType[] params, FrameLogic comp, boolean confirm)
    ////////////////////////////////////////////////////////////////////////////////
    {
        ArgInputDialog dialog = new ArgInputDialog(ca, comp, msg, params, confirm);
        dialog.showDialog();
    }

    ////////////////////////////////////////////////////////////////////////////////
    public static void showArgInput(ContestApplet ca, String msg, DataType[] params, FrameLogic comp, boolean confirm, String[] parameterNames)
    ////////////////////////////////////////////////////////////////////////////////
    {
        ArgInputDialog dialog = new ArgInputDialog(ca, comp, msg, params, confirm, parameterNames);
        dialog.showDialog();
    }


    public static void showArgInput(ContestApplet ca, DataType[] params, FrameLogic comp, boolean confirm) {
        ArgInputDialog dialog = new ArgInputDialog(ca, comp, params, confirm);
        dialog.showDialog();
    }

		/**
		 * Show argument input dialog with optional example cases and message.
		 * This simply creates an ArgInputDialog instance with the given
		 * parameters and returns the result of that dialog.
		 *
		 * Added 3/13/2003
		 *
		 * author Steven Schveighoffer (schveiguy)
		 *
		 * @param msg The message to display as instructions to the coder
		 * @param params The data types of all the parameters to the particular
		 * problem
		 * @param args The default args to use
		 * @param comp The parent JFrame to build this dialog under
		 * @param confirm Whether the arguments should be confirmed before
		 * accepting
		 * @param component The ProblemModelComponent instance that represents
		 * this problem.  The ArgInputDialog will use this to query any example
		 * cases that exist.
		 *
		 * @return An array list representing the arguments that were entered.
		 */
    public static void showArgInput(ContestApplet ca, String msg, DataType[] params, ArrayList args, FrameLogic comp, boolean confirm, ProblemComponentModel component, int language)
    {
        ArgInputDialog dialog = new ArgInputDialog(ca, comp, params, msg, args, confirm, component, language);
        dialog.showDialog();
    }
    
    public static void showArgInput(ContestApplet ca, String msg, DataType[] params, ArrayList args, FrameLogic comp, boolean confirm, ProblemComponentModel component) {
        showArgInput(ca, msg, params, args, comp, confirm, component, JavaLanguage.ID);
    }


    // ------------------------------------------------------------
    // Get one input value
    // ------------------------------------------------------------
    ////////////////////////////////////////////////////////////////////////////////
    public static String input(String title, String msg, Component comp)
            ////////////////////////////////////////////////////////////////////////////////
    {
        String value = JOptionPane.showInputDialog(comp, msg, title,
                JOptionPane.QUESTION_MESSAGE);
        return (value);
    }


    // ------------------------------------------------------------
    // Creates a message panel
    // ------------------------------------------------------------
    ////////////////////////////////////////////////////////////////////////////////
    public static JPanel createMessagePanel(String title, JTextArea msg, int width, int height, Color color)
            ////////////////////////////////////////////////////////////////////////////////
    {
        JScrollPane pane = new JScrollPane(msg);
        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = getDefaultConstraints();

        gbc.insets = new Insets(0, 0, 0, 0);
        msg.setEditable(false);
        msg.setLineWrap(true);
        msg.setWrapStyleWord(true);
        msg.setMargin(new Insets(5, 5, 5, 5));
        msg.setSelectedTextColor(HF_COLOR);
        msg.setSelectionColor(HB_COLOR);
        msg.setBackground(MB_COLOR);
        msg.setForeground(MF_COLOR);
        msg.setCaretPosition(0);
        pane.setBorder(new EmptyBorder(-1, -1, -1, -1));
        panel.setBorder(getTitledBorder(title));
        panel.setPreferredSize(new Dimension(width, height));
        panel.setBackground(color);

        insertInPanel(pane, panel, gbc, 0, 0, 1, 1);

        // new workspace variables
        panel.setBackground(Common.WPB_COLOR);
        panel.setOpaque(true);

        return (panel);
    }

    public static JPanel createMessagePanel(String title, JEditorPane msg, int width, int height, Color color)
            ////////////////////////////////////////////////////////////////////////////////
    {
        JScrollPane pane = new JScrollPane(msg);
        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = getDefaultConstraints();

        gbc.insets = new Insets(0, 0, 0, 0);
        msg.setEditable(false);
        msg.setMargin(new Insets(5, 5, 5, 5));
        msg.setSelectedTextColor(HF_COLOR);
        msg.setSelectionColor(HB_COLOR);
        msg.setBackground(MB_COLOR);
        msg.setForeground(MF_COLOR);
        pane.setBorder(new EmptyBorder(-1, -1, -1, -1));
        panel.setBorder(getTitledBorder(title));
        panel.setPreferredSize(new Dimension(width, height));
        panel.setBackground(color);

        insertInPanel(pane, panel, gbc, 0, 0, 1, 1);

        // new workspace variables
        panel.setBackground(Common.WPB_COLOR);
        panel.setOpaque(true);

        return (panel);
    }
    
    public static JPanel createMessagePanel(String title, JScrollPane pane, int width, int height, Color color)
    ////////////////////////////////////////////////////////////////////////////////
    {
        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = getDefaultConstraints();

        gbc.insets = new Insets(0, 0, 0, 0);
        pane.setBorder(new EmptyBorder(-1, -1, -1, -1));
        panel.setBorder(getTitledBorder(title));
        panel.setPreferredSize(new Dimension(width, height));
        panel.setBackground(color);

        insertInPanel(pane, panel, gbc, 0, 0, 1, 1);

        // new workspace variables
        panel.setBackground(Common.WPB_COLOR);
        panel.setOpaque(true);

        return (panel);
    }

    // ------------------------------------------------------------
    // Creates a message panel
    // ------------------------------------------------------------
    ////////////////////////////////////////////////////////////////////////////////
    public static JPanel createColumnarMessagePanel(String title, JTextArea msgs[], int widthPerMsg, int heightPerMsg)
            ////////////////////////////////////////////////////////////////////////////////
    {
        JPanel p1 = new JPanel(new GridBagLayout());
        JPanel p2 = new JPanel(new GridBagLayout());
        JScrollPane pane = new JScrollPane(p2);
        GridBagConstraints gbc = getDefaultConstraints();

        gbc.insets = new Insets(0, 0, 0, 0);
        for (int i = 0; i < msgs.length; i++) {
            msgs[i].setEditable(false);
            msgs[i].setLineWrap(true);
            msgs[i].setWrapStyleWord(true);
            msgs[i].setMargin(new Insets(5, 5, 5, 5));
            msgs[i].setSelectedTextColor(HF_COLOR);
            msgs[i].setSelectionColor(HB_COLOR);
            msgs[i].setBackground(MB_COLOR);
            msgs[i].setForeground(MF_COLOR);
            msgs[i].setPreferredSize(new Dimension(widthPerMsg, heightPerMsg));
            Common.insertInPanel(msgs[i], p2, gbc, i, 0, 1, 1, .1, .1);
        }

        p2.setPreferredSize(new Dimension(msgs.length * widthPerMsg, heightPerMsg));
        p2.setBorder(new EmptyBorder(-1, -1, -1, -1));
        pane.setBorder(new EmptyBorder(-1, -1, -1, -1));
        p1.setBorder(getTitledBorder(title));

        insertInPanel(pane, p1, gbc, 0, 0, 1, 1);
        p1.setBackground(Common.WPB_COLOR);
        return (p1);
    }


    ////////////////////////////////////////////////////////////////////////////////
    public static JComboBox createComboBox()
            ////////////////////////////////////////////////////////////////////////////////
    {
        JComboBox jcb = new JComboBox();
        jcb.setBackground(Common.WPB_COLOR);
        jcb.setForeground(Color.white);
        jcb.setBorder(new EmptyBorder(-1, -1, -1, -1));
        jcb.setRenderer(new ContestListCellRenderer());

        return (jcb);
    }


    // POPS - 12/27/2001 - added adjustWindowxxx methods
    ////////////////////////////////////////////////////////////////////////////////
    public static Point adjustWindowLocation(Point location)
            ////////////////////////////////////////////////////////////////////////////////
    {
        // Get the screen dimensions
        boolean found = false;
        Rectangle screenSize = new Rectangle();
        GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
        GraphicsDevice[] gs = ge.getScreenDevices();
        for (int j = 0; j < gs.length; j++) { 
            GraphicsDevice gd = gs[j];
            try {
                if(gd.getType() != GraphicsDevice.TYPE_RASTER_SCREEN)continue;
                if(gd.getDisplayMode() != null) {
                    found = true;
                    screenSize.width = gd.getDisplayMode().getWidth();
                    screenSize.height = gd.getDisplayMode().getHeight();
                }
            } catch(Exception ignore) {
                
            } catch(Error ignore) {
                
            }
        }
        
        if(!found) return location;
        // Make sure the location is reasonable
        if (location.x < screenSize.x) location.x = screenSize.x;
        if (location.x > screenSize.width + screenSize.x - 125) location.x = screenSize.width - 125;

        if (location.y < screenSize.y) location.y = screenSize.y;
        if (location.y > screenSize.height + screenSize.y - 125) location.y = screenSize.height - 125;

        // Return the location
        return location;
    }

    ////////////////////////////////////////////////////////////////////////////////
    public static Dimension adjustWindowSize(Point location, Dimension size)
            ////////////////////////////////////////////////////////////////////////////////
    {
        // Get the screen dimensions
        boolean found = false;
        Rectangle screenSize = new Rectangle();
        GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
        GraphicsDevice[] gs = ge.getScreenDevices();
        for (int j = 0; j < gs.length; j++) { 
            GraphicsDevice gd = gs[j];
            try {
                if(gd.getType() != GraphicsDevice.TYPE_RASTER_SCREEN)continue;
                if(gd.getDisplayMode() != null) {
                    found = true;
                    screenSize.width = gd.getDisplayMode().getWidth();
                    screenSize.height = gd.getDisplayMode().getHeight();
                }
            } catch (Exception ignore) {
            }catch(Error ignore) {
                
            }
        }

        if(!found) return size;
        // Make sure the size is reasonable
        if (size.width < 0) size.width = 600;
        if (size.width + location.x > screenSize.width + screenSize.x) size.width = screenSize.width + screenSize.x - location.x;

        if (size.height < 0) size.height = 400;
        if (size.height + location.y > screenSize.height + screenSize.y) size.height = screenSize.height + screenSize.y - location.y;

        return size;
    }


    // AdamSelene - 5/02/2002
    public static Vector enumerateFonts() {
        // get our env
        GraphicsEnvironment theEnv = GraphicsEnvironment.getLocalGraphicsEnvironment();

        // grab all them fonts
        String fonts[] = theEnv.getAvailableFontFamilyNames();

        Vector ret = new Vector();
        int i;

        // dump it into a vector so we can make a combobox ultra easily
        // and for some reason JComboBox doesn't like a String[]
        for (i = 0; i < fonts.length; i++) {
            ret.add(fonts[i]);
        }
        return ret;
    }

    public static boolean isAdmin(int rank) {
        return rank < 0;
    }

    private static NumberFormat scoreFormat = new DecimalFormat("####0.00");

    public static NumberFormat newScoreFormat() {
        return new DecimalFormat("####0.00");
    }

    public static String formatScore(double score) {
        return scoreFormat.format(score);
    }

    private static NumberFormat noFractionsFormat = new DecimalFormat("####0");


    public static String formatNoFractions(Double score) {
        return noFractionsFormat.format(score);
    }

    public static String formatNoFractions(double score) {
        return noFractionsFormat.format(score);
    }


    public static String getURLContent(URL url) throws IOException {
        BufferedReader in = new BufferedReader(new InputStreamReader(url.openStream()));
        StringBuffer r = new StringBuffer(1000);
        try {
            char[] buf = new char[1000];
            int bytesRead = 0;
            while ((bytesRead = in.read(buf, 0, buf.length)) > 0)
                r.append(buf, 0, bytesRead);
        } finally {
            in.close();
        }
        return r.toString();
    }
}
