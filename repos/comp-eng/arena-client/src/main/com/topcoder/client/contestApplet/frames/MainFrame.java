package com.topcoder.client.contestApplet.frames;

/**
 * MainFrame.java
 *
 * Created on June 8, 2001
 */

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Toolkit;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.BoxLayout;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.WindowConstants;
import javax.swing.plaf.metal.MetalLookAndFeel;
import javax.swing.plaf.metal.MetalTheme;

import com.topcoder.client.contestApplet.ContestApplet;
import com.topcoder.client.contestApplet.common.Common;
import com.topcoder.client.contestApplet.common.CommonData;
import com.topcoder.client.contestApplet.panels.ContestSponsorPanel;
import com.topcoder.client.contestApplet.panels.SoftwareLogoPanel;
import com.topcoder.client.contestApplet.panels.TopCoderLogoPanel;
import com.topcoder.client.contestApplet.panels.main.MainMenuPanel;
import com.topcoder.client.contestApplet.panels.main.MainStatusPanel;
import com.topcoder.client.contestant.RoomModel;
import com.topcoder.client.contestant.view.RoomViewManager;


/**
 *
 * @author Alex Roman
 * @version
 */
public final class MainFrame extends JFrame {

    protected ContestApplet ca = null;
    protected JPanel mainPanel = null;
    protected JPanel navPanel = new JPanel();
    protected MainMenuPanel menuPanel = null;
    protected MainStatusPanel statusPanel = null;
    protected TopCoderLogoPanel topcoderlogoPanel = null;
    //protected RoomListFrame rlf = null;
    //protected String companyName = null;

    /**
     * Default Constructor
     */
    public MainFrame(ContestApplet ca) {
        super("TopCoder");
        this.ca = ca;
        try {
            //UIManager.setLookAndFeel("javax.swing.plaf.metal.MetalLookAndFeel");
        	//this.changeMetalTheme(STEEL_THEME_CLASS);
        } catch (Exception e) {
            // Should not happen as the Metal Look and Feel is cross platform
            e.printStackTrace();
        }
        create();
    }
    
    public static final String STEEL_THEME_CLASS = "javax.swing.plaf.metal.DefaultMetalTheme";
    public static final String OCEAN_THEME_CLASS = "javax.swing.plaf.metal.OceanTheme";
    private ContestSponsorPanel sponsorPanel;
    
    public void changeMetalTheme(String themeName) {
       try {
          MetalTheme theme = (MetalTheme)Class.forName(themeName).newInstance();
          MetalLookAndFeel.setCurrentTheme(theme);
       }
       catch (Exception e) { e.printStackTrace(); }
    }
    
    public void setMenuEnabled(boolean on) {
        menuPanel.setMenuEnabled(on);
    }

    /**
     * Create the main frame and all its components.
     */
    private void create() {
        //interFrame = new MessageFrame("", mainFrame, ca);
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();

        init();

        //addWindowListener(new wl("windowClosing", "mainFrameEvent", ca));
        //addWindowListener(new wl("windowClosed", "mainFrameEvent", ca));
        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                ca.mainFrameEvent();
            }

            public void windowClosed(WindowEvent e) {
                ca.mainFrameEvent();
            }
        });

        setBackground(Common.BG_COLOR);
        getContentPane().add(ca);
        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);

        pack();
        setLocation(screenSize.width / 2 - getWidth() / 2,
                screenSize.height / 2 - getHeight() / 2);
    }

    public void init() {
        GridBagConstraints gbc = Common.getDefaultConstraints();

        getRootPane().setMinimumSize(new Dimension(Common.WIDTH, Common.HEIGHT));
        getRootPane().setPreferredSize(new Dimension(Common.WIDTH, Common.HEIGHT));

        getContentPane().setLayout(new GridBagLayout());
        getContentPane().setBackground(Common.BG_COLOR);

        mainPanel = new JPanel();
        //addFocusListener(new fl());

        JPanel bp = new JPanel(new GridBagLayout());
        JPanel holder = new JPanel(new GridBagLayout());

        //MainStatusPanel sp = new MainStatusPanel(ca);
        MainMenuPanel menu = new MainMenuPanel(ca);
        JComponent topcoderlogoPanel = new TopCoderLogoPanel(ca);
        SoftwareLogoPanel softwarelogoPanel = new SoftwareLogoPanel(ca);
        //EmploymentServicesLogoPanel employmentlogoPanel = new EmploymentServicesLogoPanel(ca);

        bp.setBackground(Common.TOP_BACK);
        //bp.setPreferredSize(new Dimension(640, 60));
        //bp.setMinimumSize(new Dimension(640, 60));
        holder.setBackground(Common.BG_COLOR);
        //holder.setPreferredSize(new Dimension(640, 60));
        //holder.setMinimumSize(new Dimension(640, 60));
        holder.setRequestFocusEnabled(false);

        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.X_AXIS));
        mainPanel.setBackground(Common.BG_COLOR);
        mainPanel.setRequestFocusEnabled(false);

        //JButton goRL = Common.getTextButton("");
        //goRL.setIcon(Common.getImage("go_but.gif",ca));
        //goRL.setPressedIcon(Common.getImage("go_but_in.gif",ca));
        //goRL.setDisabledIcon(Common.getImage("go_but_gray.gif",ca));
        //goRL.setBackground(Color.white);
        //goRL.setForeground(Color.white);
        //goRL.setMnemonic('w');
        //goRL.addActionListener(new al("actionPerformed", "goRLButtonEvent", this));
        //goRL.addActionListener(new ActionListener(){
        //    public void actionPerformed(ActionEvent e) {
        //        goRLButtonEvent();
        //    }
        //});
        //goRL.setToolTipText("View complete RoomLeaders list");

        // start with the logon screen
        bp.setVisible(false);

        String companyName = ca.getCompanyName();
        boolean poweredByView = ca.getPoweredByView();
        sponsorPanel = new ContestSponsorPanel(ca, CommonData.getSponsorLobbyImageAddr(companyName, null));
        ca.getModel().getRoomViewManagerManager().addListener(new RoomViewManager() {
        
            public void setCurrentRoom(RoomModel room) {
                sponsorPanel.updateURL(CommonData.getSponsorLobbyImageAddr(ca.getSponsorName(), room));
            }
        
            public void removeRoom(RoomModel room) {
            }
        
            public void clearRooms() {
            }
        
            public void addRoom(RoomModel room) {
            }
        
        });
        JComponent leftPanel;
        JComponent rightPanel;
        if (poweredByView) {
            leftPanel = sponsorPanel;
            rightPanel = topcoderlogoPanel;
        } else {
            leftPanel = topcoderlogoPanel;
            rightPanel = sponsorPanel;
        }

        gbc.insets = new Insets(0, 0, 0, 0);
        gbc.fill = GridBagConstraints.NONE;

        //gbc.anchor = GridBagConstraints.NORTHWEST;
        //Common.insertInPanel(sp, bp, gbc, 0, 0, 1, 1, 0.0, 0.0);

        //gbc.insets = new Insets(11,0,0,0);
        //gbc.anchor = GridBagConstraints.NORTHWEST;
        //Common.insertInPanel(goRL, bp, gbc, 1, 0, 1, 1, 0.0, 0.0);
        gbc.insets = new Insets(0, 0, 0, 0);
        gbc.anchor = GridBagConstraints.NORTHEAST;
        Common.insertInPanel(leftPanel, bp, gbc, 0, 0, 1, 1, 0.0, 0.0);

        if (companyName.equals(Common.COMP_TOPCODER)) {
            gbc.insets = new Insets(0, 0, 0, 0);
            gbc.anchor = GridBagConstraints.NORTHEAST;
            Common.insertInPanel(softwarelogoPanel, bp, gbc, 1, 0, 1, 1, 0.0, 0.0);

            gbc.insets = new Insets(0, 0, 0, 0);
            gbc.anchor = GridBagConstraints.NORTHEAST;
            //Common.insertInPanel(employmentlogoPanel, bp, gbc, 2, 0, 1, 1, 0.0, 0.0);
        }

        gbc.insets = new Insets(0, 0, 0, 0);
        gbc.anchor = GridBagConstraints.NORTHEAST;
        //Common.insertInPanel(logo, bp, gbc, 1, 0, 1, 1, 0.1, 0.1);
        Common.insertInPanel(rightPanel, bp, gbc, 3, 0, 1, 1, 0.1, 0.1);
        gbc.anchor = GridBagConstraints.NORTHWEST;
        gbc.fill = GridBagConstraints.BOTH;
        Common.insertInPanel(menu, bp, gbc, 0, 1, 4, 1, 0.1, 0.1);
        Common.insertInPanel(bp, holder, gbc, 0, 0, 1, 1, 0.1, 0.1);

        gbc.fill = GridBagConstraints.BOTH;
        Common.insertInPanel(holder, getContentPane(), gbc, 0, 0, 1, 1, 0.0, 0.0);
        Common.insertInPanel(mainPanel, getContentPane(), gbc, 0, 1, 1, 1, 1.0, 1.0);


        this.navPanel = bp;
        //this.statusPanel = sp;
        this.menuPanel = menu;

    }

    public void leave() {
        //if (rlf != null) {
        //   rlf.hide();
        //   rlf.reset();
        //}
//        statusPanel.getFaderPanel().setTickerEnabled(false);
        //statusPanel.getFaderPanel().clear();
    }

    //private void goRLButtonEvent() {
    //    if (rlf == null) {
    //        rlf = new RoomListFrame(ca);
    //    }

    //    rlf.show();
    //    ca.getRequester().requestGetLeaderBoard();
    //}

//    public void updateRoomLeaderBoard(ArrayList rows, boolean show) {
//        if ( rlf == null ) {
//            rlf = new RoomListFrame(ca);
//        }
//
//        statusPanel.getFaderPanel().clear();
//        statusPanel.getFaderPanel().setEnabled(!ca.isLeaderTickerDisabled());
//
//        rlf.updateRoomInfoTable(rows);
//
//        if (show) {
//            rlf.show();
//        }
//    }
//
//
//    public void updateLeaderboard(long roundID, LeaderboardItem item) {
//        rlf.modifyRoomInfoTable(roundID,item);
//    }

    public JPanel getMainPanel() {
        return (mainPanel);
    }

    public JPanel getNavPanel() {
        return (navPanel);
    }

    public MainMenuPanel getMenuPanel() {
        return (menuPanel);
    }

    public ContestApplet getContestApplet() {
        return ca;
    }
    //public MainStatusPanel getStatusPanel() {
    //    return(statusPanel);
    //}
}
