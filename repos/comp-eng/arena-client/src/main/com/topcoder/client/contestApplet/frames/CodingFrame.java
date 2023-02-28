/*
 * Copyright (C) - 2022 TopCoder Inc., All Rights Reserved.
 */
package com.topcoder.client.contestApplet.frames;

import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JEditorPane;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.text.html.HTMLDocument;

import com.topcoder.client.contestApplet.ContestApplet;
import com.topcoder.client.contestApplet.common.Common;
import com.topcoder.client.contestApplet.common.CommonData;
import com.topcoder.client.contestApplet.common.HyperLinkLoader;
import com.topcoder.client.contestApplet.common.LocalPreferences;
import com.topcoder.client.contestApplet.editors.DynamicEditor;
import com.topcoder.client.contestApplet.editors.EditorPlugin;
import com.topcoder.client.contestApplet.editors.PluginManager;
import com.topcoder.client.contestApplet.editors.StandardPlugins;
import com.topcoder.client.contestApplet.panels.ContestSponsorPanel;
import com.topcoder.client.contestApplet.panels.coding.CodingTimerPanel;
import com.topcoder.client.contestApplet.panels.coding.ProblemInfoComponent;
import com.topcoder.client.contestApplet.panels.coding.ProblemInfoPanel;
import com.topcoder.client.contestApplet.panels.room.TimerPanel;
import com.topcoder.client.contestApplet.rooms.CoderRoom;
import com.topcoder.client.contestApplet.rooms.CoderRoomInterface;
import com.topcoder.client.contestApplet.rooms.TeamCoderRoom;
import com.topcoder.client.contestApplet.unusedCodeProcessor.UCRProcessor;
import com.topcoder.client.contestApplet.unusedCodeProcessor.UCRProcessorFactory;
import com.topcoder.client.contestApplet.widgets.BroadcastButton;
import com.topcoder.client.contestApplet.widgets.MoveFocus;
import com.topcoder.client.contestant.ProblemComponentModel;
import com.topcoder.client.contestant.RoomModel;
import com.topcoder.client.render.ProblemComponentRenderer;
import com.topcoder.netCommon.contest.ContestConstants;
import com.topcoder.netCommon.contest.round.RoundProperties;
import com.topcoder.shared.language.BaseLanguage;
import com.topcoder.shared.language.CPPLanguage;
import com.topcoder.shared.language.CSharpLanguage;
import com.topcoder.shared.language.JavaLanguage;
import com.topcoder.shared.language.Language;
import com.topcoder.shared.language.PythonLanguage;
import com.topcoder.shared.language.Python3Language;
import com.topcoder.shared.language.JavaScriptLanguage;
import com.topcoder.shared.language.VBLanguage;
import com.topcoder.shared.problem.DataType;

import java.awt.BorderLayout;

/**
 * <p>
 * Changes in version 1.1 (Python3 Support):
 * <ol>
 *      <li>Added {@link #python3RadioButton} field.</li>
 *      <li>Updated {@link #getLanguage()}, {@link #setLanguage(Language)}, {@link #updateLanguageButtonStatus()},
 *      {@link #createEditorTogglePanel()}, {@link #languageToggleEvent(ActionEvent)} methods.</li>
 * </ol>
 * </p>
 *
 * @author  Alex Roman, liuliquan
 * @version 1.1
 */
public class CodingFrame extends JFrame {

    private static final boolean DEBUG = Boolean.getBoolean("com.topcoder.client.contestApplet.frames.CodingFrame");
    
    // main window
    private ContestApplet parentFrame = null;
    // panels
    private CodingTimerPanel timerPanel = null;
    private ProblemInfoComponent problemPanel = null;
//    private JPanel problemDescPanel = null;
//    private JPanel sourcePanel = null;

    // SourceCode panel variables
    private JEditorPane problemPane;
    public JScrollPane problemScroll = null;
    private JPanel editorPanel = null;
    private JSplitPane jsp;
    
    private JLabel connStatus;

    private ButtonDef[] buttonDefs;
    private JButton[] buttons;
    private BroadcastButton broadcastButton;
    private JRadioButton javaRadioButton, cplusplusRadioButton, csharpRadioButton, vbRadioButton,
            pythonRadioButton, python3RadioButton, javascriptRadioButton;
    private JComboBox editorList;
    private boolean isSaved = true;
    private Language currentLanguage = JavaLanguage.JAVA_LANGUAGE;
    private boolean enabled = true;
    private RoomModel roomModel;

    // POPS 12/22/2001 - added keys to store sizes in local preferences
    private LocalPreferences pref = LocalPreferences.getInstance();
    private static final String FRAMELOCATION = "com.topcoder.jmaContestApplet.frames.codingframe.location";
    private static final String FRAMESIZE = "com.topcoder.jmaContestApplet.frames.codingframe.size";
    private static final String DIVIDERLOC = "com.topcoder.jmaContestApplet.frames.codingframe.dividerloc";
    private static final String NOCOPYPASTE = "com.topcoder.client.contestApplet.frames.codingframe.nocopypaste";

    private DynamicEditor dynamicEditor;
    private HashMap editorDefs = new HashMap();

    private boolean ignoreToggleEvent = false;
    
    // POPS 5/2/2002 - added for support of source changes prior to submission
    private String compiledSource = "";
    private ProblemComponentModel component;
    
    private CoderRoomInterface cr;
    
    public void setCR(CoderRoomInterface c) {
        cr = c;
    }

    public CodingFrame(ContestApplet parent) {
        super("TopCoder Competition Arena - Coding Phase");

        // Disallow copy/cut/paste when specified
       	if(System.getProperty(NOCOPYPASTE)!=null) {
       		problemPane = new NoClipboardEditorPane("text/html", "");
       	} else {
       		problemPane = new JEditorPane("text/html", "");
       	}
       	
        parentFrame = parent;
        if(DEBUG) System.out.println("Creating");
        // POPS - 12/22/2001 - added new windowclosing listener to save the sizes to the local preferences
        //this.addWindowListener(new wl("windowClosing", "closeCodingWindow", this));
        this.addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                unsetupKeyListener(kl, getContentPane());
                closeCodingWindow();
            }
        });
        problemPane.addHyperlinkListener(new HyperLinkLoader(parent.getAppletContext()));
        
    }
    
    private void unsetupKeyListener(KeyListener kl, Container comp) {
        comp.removeKeyListener(kl);
        for(int i = 0; i < comp.getComponentCount(); i++) {
            if(comp instanceof Container)
                unsetupKeyListener(kl, (Container)comp.getComponent(i));
            else
                unsetupKeyListener(kl, comp.getComponent(i));
        }
    }
    
    private void unsetupKeyListener(KeyListener kl, Component comp) {
        comp.removeKeyListener(kl);
    }
    
    public int testComponentID;
    
    public void doTest(ArrayList info) {
        ArrayList tempArgs = null;
         try {
            tempArgs = (ArrayList) info.get(1);
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        if(cr instanceof CoderRoom) {
            ((CoderRoom)cr).setArgs(tempArgs);
            cr = null;
        } else if(cr instanceof TeamCoderRoom) {
            ((TeamCoderRoom)cr).setArgs(tempArgs);
            cr = null;
        }

        if (((Boolean) info.get(0)).booleanValue()) {
            test(info, testComponentID); 
        } else {
            this.setButtons(true, true, true, false, false, true, true, true);
        }
    }
    
    private void test(ArrayList info, int componentID) {
        if(enabled) {
            parentFrame.getInterFrame().showMessage("Testing...", this, ContestConstants.TEST);
            parentFrame.getRequester().requestTest((ArrayList) info.get(1), componentID);
        }
    }

    public void dispose() {
        if(DEBUG) System.out.println("dispose: " + (dynamicEditor==null ? "null" : dynamicEditor.getPlugin().getName()));
        if(dynamicEditor!=null) {
            PluginManager.getInstance().disposeEditor(dynamicEditor);
            dynamicEditor=null;
        }
        super.dispose();
    }
    
    public void hide() {
        if(DEBUG) System.out.println("hide: " + (dynamicEditor==null ? "null" : dynamicEditor.getPlugin().getName()));
        if(dynamicEditor!=null) {
            PluginManager.getInstance().disposeEditor(dynamicEditor);
            dynamicEditor=null;
        }
        super.hide();
    }
    
    public void setVisible(boolean visible) {
        if(DEBUG) System.out.println("setVisible: " + (dynamicEditor==null ? "null" : dynamicEditor.getPlugin().getName()));
        if(dynamicEditor!=null) {
            PluginManager.getInstance().disposeEditor(dynamicEditor);
            dynamicEditor=null;
        }
        super.setVisible(visible);
    }

    public void clear() {
        problemPane.setText("");
        problemScroll.getVerticalScrollBar().setValue(0);
        if(dynamicEditor!=null) dynamicEditor.clear();
    }

    public void resetFocus() {
        // show is threaded so grab focus doesn't always work
        MoveFocus.moveFocus(editorList);
        MoveFocus.moveFocus(editorPanel);
        
        
        SwingUtilities.invokeLater(new Runnable() {
            public void run() { 
                editorPanel.requestFocus();
            }
        });
    }

    public void showFrame(boolean enabled) {
        if(DEBUG) System.out.println("showFrame: " + (dynamicEditor==null ? "null" : dynamicEditor.getPlugin().getName()));
        if(dynamicEditor==null) setEditor(getDefaultEditor(), null);
        updateLanguageButtonStatus();
        enableText(enabled);
        show();
        resetFocus();
    }

    public String getCode() {
        if(dynamicEditor==null) return "";
        String code = getSourceCode();
        return code == null ? "" : code;
    }


    public TimerPanel getTimerPanel() {
        return (timerPanel);
    }


    public void updateComponentInfo(ProblemComponentModel info) {
        component = info;
        // Set the language and update everything
        setLanguage(getDefaultLanguage());
        
        if(DEBUG) System.out.println("updateComponentInfo: " + (dynamicEditor==null ? "null" : dynamicEditor.getPlugin().getName()));
        if(dynamicEditor==null) {
            setEditor(getDefaultEditor(), component.getDefaultSolution());
        } else {
            setEditor((String)editorList.getSelectedItem(), component.getDefaultSolution());
        }
    }


    //------------------------------------------------------------------------------
    // Create the room
    //------------------------------------------------------------------------------

    public void create() {
        GridBagConstraints gbc = Common.getDefaultConstraints();

        CodingTimerPanel tp = new CodingTimerPanel(parentFrame);
        this.timerPanel = tp;
        ProblemInfoComponent pp = newProblemInfoPanel();
        this.problemPanel = pp;

        // create all the panels/panes
        JPanel ps = Common.createMessagePanel("Problem Statement", problemPane, 0, 0, Common.BG_COLOR);
//        problemDescPanel = ps;
        JPanel ep = createEditorTogglePanel();
        JPanel sc = createSourceCodePanel("Coding Area", 0, 0);
//        sourcePanel = sc;
        JPanel bt = createButtonPanel();

        // Combine the source code and the button panels
        JPanel combined = new JPanel(new BorderLayout());
        combined.add(sc, BorderLayout.CENTER);
        combined.add(bt, BorderLayout.SOUTH);
        JSplitPane sp = new JSplitPane(JSplitPane.VERTICAL_SPLIT, true, ps, combined);
        jsp = sp;

        //sp.setPreferredSize(new Dimension(675, 310));
        //sp.setPreferredSize(new Dimension(760, 360));
        //sp.setDividerLocation(80);

        // set the preferred font on the problem statement, and set the color.
        this.problemPane.setFont(new Font(pref.getFont(LocalPreferences.PROBLEMFONT), Font.PLAIN, pref.getFontSize(LocalPreferences.PROBLEMFONTSIZE)));
        
        //this.problemPane.setEditorKit(new HTMLEditorKit());
        ((HTMLDocument) this.problemPane.getDocument()).getStyleSheet().addRule("body {font-family: " + pref.getFont(LocalPreferences.PROBLEMFONT) + ";}");
        ((HTMLDocument) this.problemPane.getDocument()).getStyleSheet().addRule("body {font-size: " + pref.getFontSize(LocalPreferences.PROBLEMFONTSIZE) + "pt;}");
        
        ((HTMLDocument) this.problemPane.getDocument()).getStyleSheet().addRule("pre {font-family: " + pref.getFont(LocalPreferences.PROBLEMFIXEDFONT) + ";}");
        ((HTMLDocument) this.problemPane.getDocument()).getStyleSheet().addRule("pre {font-size: " + pref.getFontSize(LocalPreferences.PROBLEMFIXEDFONTSIZE) + "pt;}");
        
        this.problemPane.setForeground(pref.getColor(LocalPreferences.PROBLEMFORE));
        this.problemPane.setBackground(pref.getColor(LocalPreferences.PROBLEMBACK));

        // Get the scroll area
        this.problemScroll = (JScrollPane) ps.getComponent(0);

        // Initially, disable all of them until a key is pressed
        setButtons(false, false, false, false, false, false, false, false);

        getContentPane().setLayout(new GridBagLayout());
        getContentPane().setBackground(Common.WPB_COLOR);

        gbc.fill = GridBagConstraints.BOTH;
        gbc.insets = new Insets(0, 15, 5, 5);
        Common.insertInPanel((JComponent) pp, getContentPane(), gbc, 0, 1, 2, 1, 0.0, 0.0);
        
        gbc.insets = new Insets(0, 0, 0, 0);
        connStatus = new JLabel(Common.getImage("grey_connected.gif", parentFrame));
        JPanel stp = new JPanel(new GridBagLayout());
        sp.setBackground(Common.BG_COLOR);
        gbc.fill = GridBagConstraints.NONE;
        gbc.anchor = GridBagConstraints.NORTHEAST;
        JLabel connText = new JLabel("Connection Status: ");
        connText.setForeground(Common.TIMER_COLOR);
        connText.setFont(new Font("SansSerif", Font.PLAIN, 10));
        Common.insertInPanel(connText, stp, gbc, 0, 0, 1, 1, 0.1, 0.1);
        gbc.fill = GridBagConstraints.NONE;
        gbc.anchor = GridBagConstraints.NORTHEAST;
        Common.insertInPanel(connStatus, stp, gbc, 1, 0, 1, 1, 0.1, 0.1);
        
        //Common.insertInPanel(tp, stp, gbc, 0, 1, 2, 1, 0.0, 0.0);
        
        gbc.fill = GridBagConstraints.NONE;
        gbc.anchor = GridBagConstraints.EAST;
        //gbc.insets = new Insets(5, 5, 5, 15);
        gbc.insets = new Insets(5, 0, 0, 25);
        Common.insertInPanel(stp, getContentPane(), gbc, 2, 0, 1, 1, 0.0, 0.0);
        
        gbc.insets = new Insets(0, 5, 5, 15);
        Common.insertInPanel(tp, getContentPane(), gbc, 2, 1, 1, 1, 0.0, 0.0);

        gbc.fill = GridBagConstraints.NONE;
        gbc.anchor = GridBagConstraints.NORTHWEST;
        gbc.insets = new Insets(5, 15, 5, 15);
        Common.insertInPanel(newUnderProblemInfoPanel(), getContentPane(), gbc, 0, 2, 1, 1, 0.0, 0.0);

        gbc.fill = GridBagConstraints.BOTH;
        gbc.anchor = GridBagConstraints.NORTHEAST;
        gbc.insets = new Insets(5, 15, 5, 15);
        Common.insertInPanel(ep, getContentPane(), gbc, 1, 2, 2, 1, 0.0, 0.0);

        gbc.fill = GridBagConstraints.BOTH;
        gbc.anchor = GridBagConstraints.NORTH;
        gbc.insets = new Insets(5, 15, 15, 15);
        Common.insertInPanel(sp, getContentPane(), gbc, 0, 3, 3, 1, 0.1, 0.1);

        pack();
        
        //setup key listener
        setupKeyListener(kl, this.getContentPane());
        
        // Get the frame location
        Point frameLocation = pref.getLocation(FRAMELOCATION);
        if (frameLocation == null) {
            JFrame currFrame = parentFrame.getCurrentFrame();
            if (currFrame == null) {
                frameLocation = new Point(0, 0);
            } else {
                frameLocation = currFrame.getLocation();
            }
        }

        // Adjust the frame location (to be possible) and set the location
        frameLocation = Common.adjustWindowLocation(frameLocation);
        setLocation(frameLocation);

        // Restore the frame size (adjusted for the constraints of the window)
        Dimension frameSize = pref.getSize(FRAMESIZE);
        if (frameSize == null) frameSize = new Dimension(760, 360);
        setSize(Common.adjustWindowSize(frameLocation, frameSize));

        // Restore the divider location
        String dividerLocation = pref.getProperty(DIVIDERLOC);
        if (dividerLocation == null) dividerLocation = "80";
        sp.setDividerLocation(Integer.parseInt(dividerLocation));
        
        
        for (int i = 0; i < buttonDefs.length; i++) {
            ButtonDef def = buttonDefs[i];
            if (def.hotKeyPrefKey != null) {
                keyPrefs.put(def.hotKeyPrefKey, pref.getHotKey(def.hotKeyPrefKey));
                keyButtons.put(def.hotKeyPrefKey, buttons[i]);
            }
            
        }
    }

    protected JPanel newUnderProblemInfoPanel() {
        return new ContestSponsorPanel(parentFrame, CommonData.getSponsorCodingFrameImageAddr(parentFrame.getSponsorName(), getRoomModel()));
    }

    protected ProblemInfoComponent newProblemInfoPanel() {
        return new ProblemInfoPanel(parentFrame);
    }
    
    HashMap keyPrefs = new HashMap();
    HashMap keyButtons = new HashMap();
    
    //load hotkey from local preferences
    private KeyListener kl = new KeyListener() {
            long lastTime = 0;
            int lastKey = 0;
            
            public void keyPressed(KeyEvent keyEvent) {
                //check each of the buttons at the bottom
                if(lastKey == keyEvent.getKeyCode() && System.currentTimeMillis() - lastTime < 500)
                    return;
                    
                lastTime = System.currentTimeMillis();
                lastKey = keyEvent.getKeyCode();
                
                for(Iterator i = keyPrefs.keySet().iterator(); i.hasNext(); ) {
                    String key = (String)i.next();
                    if(checkValue(keyEvent, (String)keyPrefs.get(key))) {
                        keyEvent.consume();
                        JButton btn = (JButton)keyButtons.get(key);
                        
                        btn.doClick();
                    }
                }
                
            }
            public void keyReleased(KeyEvent keyEvent) {
            }
            public void keyTyped(KeyEvent keyEvent) {
                
            }
        };
        
    private boolean checkValue(KeyEvent evt, String val) {
        String[] keys = val.split("\\+");
        //check for extra modifiers
        boolean alt = false, ctrl = false, shift = false;
        if((evt.getModifiers() & KeyEvent.CTRL_MASK) != 0) {
            ctrl = true;
//            System.out.println("CTRL");
        }
        if((evt.getModifiers() & KeyEvent.ALT_MASK) != 0) {
            alt = true;
//            System.out.println("ALT");
        }
        if((evt.getModifiers() & KeyEvent.SHIFT_MASK) != 0) {
            shift = true;
//            System.out.println("SHIFT");
        }
        
        for(int i = 0; i < keys.length-1; i++) {
            if(keys[i].equals("Ctrl")) {
                ctrl = false;
                if((evt.getModifiers() & KeyEvent.CTRL_MASK) == 0)
                    return false;
            } else if(keys[i].equals("Alt")) {
                alt = false;
                if((evt.getModifiers() & KeyEvent.ALT_MASK) == 0)
                    return false;
            } else if(keys[i].equals("Shift")) {
                shift = false;
                if((evt.getModifiers() & KeyEvent.SHIFT_MASK) == 0)
                    return false;
            }
        }

        if(ctrl || alt || shift)
            return false;
        
        if(!keys[keys.length-1].equals(KeyEvent.getKeyText(evt.getKeyCode()))) {
            return false;
        }
        
        return true;
    }
    
    private void setupKeyListener(KeyListener kl, Container comp) {
        comp.removeKeyListener(kl);
        comp.addKeyListener(kl);
        for(int i = 0; i < comp.getComponentCount(); i++) {
            if(comp instanceof Container)
                setupKeyListener(kl, (Container)comp.getComponent(i));
            else
                setupKeyListener(kl, comp.getComponent(i));
        }
    }
    
    private void setupKeyListener(KeyListener kl, Component comp) {
        comp.removeKeyListener(kl);
        comp.addKeyListener(kl);
    }

    private JPanel createEditorTogglePanel() {
        // Get the plugins
        java.util.List fullList = Arrays.asList(PluginManager.getInstance().getEditorPlugins());

        // Sort them based on name
        String[] names = new String[fullList.size()];

        // Add them to both the internal cache and the combo box
        int max = fullList.size();
        for (int x = 0; x < max; x++) {
            EditorPlugin edit = (EditorPlugin) fullList.get(x);
            editorDefs.put(edit.getName(), edit);
            names[x] = edit.getName();
        }

        // Sort the names
        Arrays.sort(names, java.text.Collator.getInstance());

        // Create the combo box
        editorList = Common.createComboBox();
        editorList.setPreferredSize(new Dimension(175, 21));
        editorList.setMaximumSize(new Dimension(175, 21));

        for (int x = 0; x < names.length; x++) editorList.addItem(names[x]);

        // Set the default
        editorList.setSelectedItem(getDefaultEditor());
        
        // Create the various objects
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setOpaque(false);

        // Create weapon label
        JLabel jl1 = new JLabel("Choose your editor: ", SwingConstants.LEFT);
        jl1.setForeground(Common.FG_COLOR);

        // Create attack label
        JLabel jl2 = new JLabel("Choose your language: ", SwingConstants.LEFT);
        jl2.setForeground(Common.FG_COLOR);
        JComponent radioButtonsPanel = new JPanel();

        final ButtonGroup group2 = new ButtonGroup();
        ActionListener actionListener = new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                languageToggleEvent(e);
            }
        };

        // Create Java radiobutton
        JRadioButton javaRadioButton = new JRadioButton("Java", true);
        javaRadioButton.setBackground(Common.WPB_COLOR);
        javaRadioButton.setForeground(Common.FG_COLOR);
        javaRadioButton.setOpaque(false);
        javaRadioButton.setActionCommand("Java");
        radioButtonsPanel.add(javaRadioButton);
        group2.add(javaRadioButton);
        javaRadioButton.addActionListener(actionListener);
        this.javaRadioButton = javaRadioButton;

        // Create C++ radiobutton
        JRadioButton cplusplusRadioButton = new JRadioButton("C++", false);
        cplusplusRadioButton.setBackground(Common.WPB_COLOR);
        cplusplusRadioButton.setForeground(Common.FG_COLOR);
        cplusplusRadioButton.setOpaque(false);
        cplusplusRadioButton.setActionCommand("C++");
        radioButtonsPanel.add(cplusplusRadioButton);
        group2.add(cplusplusRadioButton);
        cplusplusRadioButton.addActionListener(actionListener);
        this.cplusplusRadioButton = cplusplusRadioButton;

        // Create C# radiobutton
        JRadioButton csharpRadioButton = new JRadioButton("C#", false);
        csharpRadioButton.setBackground(Common.WPB_COLOR);
        csharpRadioButton.setForeground(Common.FG_COLOR);
        csharpRadioButton.setOpaque(false);
        csharpRadioButton.setActionCommand("C#");
        radioButtonsPanel.add(csharpRadioButton);
        group2.add(csharpRadioButton);
        csharpRadioButton.addActionListener(actionListener);
        this.csharpRadioButton = csharpRadioButton;

        // Create VB radio button
        JRadioButton vbRadioButton = new JRadioButton("VB", false);
        vbRadioButton.setBackground(Common.WPB_COLOR);
        vbRadioButton.setForeground(Common.FG_COLOR);
        vbRadioButton.setOpaque(false);
        vbRadioButton.setActionCommand("VB");
        radioButtonsPanel.add(vbRadioButton);
        group2.add(vbRadioButton);
        vbRadioButton.addActionListener(actionListener);
        this.vbRadioButton = vbRadioButton;
        
        // Create Python radio button
        JRadioButton pythonRadioButton = new JRadioButton("Python", false);
        pythonRadioButton.setBackground(Common.WPB_COLOR);
        pythonRadioButton.setForeground(Common.FG_COLOR);
        pythonRadioButton.setOpaque(false);
        pythonRadioButton.setActionCommand("Python");
        radioButtonsPanel.add(pythonRadioButton);
        group2.add(pythonRadioButton);
        pythonRadioButton.addActionListener(actionListener);
        this.pythonRadioButton = pythonRadioButton;
        
        // Create Python3 radio button
        JRadioButton python3RadioButton = new JRadioButton("Python3", false);
        python3RadioButton.setBackground(Common.WPB_COLOR);
        python3RadioButton.setForeground(Common.FG_COLOR);
        python3RadioButton.setOpaque(false);
        python3RadioButton.setActionCommand("Python3");
        radioButtonsPanel.add(python3RadioButton);
        group2.add(python3RadioButton);
        python3RadioButton.addActionListener(actionListener);
        this.python3RadioButton = python3RadioButton;

        // Create Javascript radio button
        JRadioButton javascriptRadioButton = new JRadioButton("JavaScript", false);
        javascriptRadioButton.setBackground(Common.WPB_COLOR);
        javascriptRadioButton.setForeground(Common.FG_COLOR);
        javascriptRadioButton.setOpaque(false);
        javascriptRadioButton.setActionCommand("JavaScript");
        radioButtonsPanel.add(javascriptRadioButton);
        group2.add(javascriptRadioButton);
        javascriptRadioButton.addActionListener(actionListener);
        this.javascriptRadioButton = javascriptRadioButton;
        GridBagConstraints gbc = Common.getDefaultConstraints();

        gbc.fill = GridBagConstraints.NONE;
        gbc.anchor = GridBagConstraints.EAST;
        gbc.insets = new Insets(0, 0, 0, 10);
        Common.insertInPanel(jl1, panel, gbc, 0, 0, 1, 1, 1.0, 1.0);

        gbc.insets = new Insets(0, 0, 0, 0);
        gbc.anchor = GridBagConstraints.NORTHEAST;
        Common.insertInPanel(editorList, panel, gbc, 1, 0, 2, 1, 0.0, 0.0);

        gbc.fill = GridBagConstraints.NONE;
        gbc.anchor = GridBagConstraints.EAST;
        gbc.insets = new Insets(0, 0, 0, 10);
        Common.insertInPanel(jl2, panel, gbc, 0, 1, 1, 1, 1.0, 1.0);

        gbc.fill = GridBagConstraints.BOTH;
        gbc.anchor = GridBagConstraints.NORTHEAST;
        gbc.insets = new Insets(0, 0, 0, 0);


        Common.insertInPanel(radioButtonsPanel, panel, gbc, 1, 1, 1, 1, 0.0, 0.0);

        // Add the listeners
        //editorList.addActionListener(new al("actionPerformed", "editorToggleEvent", this));
        editorList.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if(!ignoreToggleEvent) editorToggleEvent();
            }
        });

        // Return the panel
        return (panel);
    }

    private JPanel createSourceCodePanel(String title, int width, int height) {

        // Create the panel
        editorPanel = new JPanel();
        editorPanel.setLayout(new BoxLayout(editorPanel, BoxLayout.Y_AXIS));

        // Wrap it in a title
        editorPanel.setBorder(Common.getTitledBorder(title));
        editorPanel.setPreferredSize(new Dimension(width, height));
        editorPanel.setBackground(Common.WPB_COLOR);
        editorPanel.setRequestFocusEnabled(true);


        // Setup the default editor
        //setEditor(getDefaultEditor(), null);
        //setEditor("Standard");

        return editorPanel;

    }
    
    protected boolean isPanelEnabled() {
        return enabled;
    }
    
    public void setPanelEnabled(boolean on) {
        enabled = on;
        if(on) {
            connStatus.setIcon(Common.getImage("grey_connected.gif", parentFrame));
        } else {
            connStatus.setIcon(Common.getImage("grey_disconnected.gif", parentFrame));
        }
        broadcastButton.setButtonEnabled(on);
        updateButtons(on);
    }

    protected void updateButtons(boolean on) {
        if(on) {
            for (int i = 0; i < buttonDefs.length; i++) {
                buttons[i].setIcon(Common.getImage(buttonDefs[i].image, parentFrame));
            }
        } else {
            for (int i = 0; i < buttonDefs.length; i++) {
                buttons[i].setIcon(Common.getImage(buttonDefs[i].disabledImage, parentFrame));
            }
        }
    }

    private JPanel createButtonPanel() {
        buttonDefs = createButtonDefs();
        buttons = new JButton[buttonDefs.length];
        for (int i = 0; i < buttonDefs.length; i++) {
            buttons[i] = createButton(buttonDefs[i]);
            
        }

        broadcastButton = new BroadcastButton(parentFrame);

        // Create the panel to hold them and add the buttons
        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new GridBagLayout());
        GridBagConstraints gbc = Common.getDefaultConstraints();
        gbc.fill = GridBagConstraints.NONE;
        gbc.insets = new Insets(0, 5, 5, 0);
        gbc.anchor = GridBagConstraints.WEST;
        Common.insertInPanel(broadcastButton, buttonPanel, gbc, 0, 0, 1, 1, .5, 0);
        gbc.insets = new Insets(0, 1, 5, 1);
        gbc.anchor = GridBagConstraints.EAST;
        for (int i = 0; i < buttons.length; i++) {
            if (i == buttons.length - 1) {
                gbc.insets = new Insets(0, 1, 5, 5);
            }
            Common.insertInPanel(buttons[i], buttonPanel, gbc, i+1, 0, 1, 1, 0, 0);
        }
        return buttonPanel;
    }

    protected JButton createButton(ButtonDef def) {
        JButton button = Common.getImageButton(def.image, parentFrame);
        button.setToolTipText(def.toolTipText);
        //button5.addActionListener(new al("actionPerformed", "submitButtonEvent", this));
        button.addActionListener(def.actionListener);
        return button;
    }

    protected ButtonDef[] createButtonDefs() {
        ButtonDef[] buttonDefs = new ButtonDef[5];
        buttonDefs[0] = new ButtonDef("g_save_but.gif", "no_g_save_but.gif", "Save your source code", LocalPreferences.KEYSAVE, 
                new ActionListener() {
                    public void actionPerformed(ActionEvent e) {
                        saveButtonEvent();
                    }
                });
        buttonDefs[1] = new ButtonDef("g_clear_but.gif", "no_g_clear_but.gif", "Clears your source code", null, 
                new ActionListener() {
                    public void actionPerformed(ActionEvent e) {
                        clearButtonEvent();
                    }
                });

        buttonDefs[2] = new ButtonDef("g_compile_but.gif", "no_g_compile_but.gif", "Compile source code", LocalPreferences.KEYCOMPILE, 
                new ActionListener() {
                    public void actionPerformed(ActionEvent e) {
                        compileButtonEvent();
                    }
                });
        
        buttonDefs[3] = new ButtonDef("g_test_but.gif", "no_g_test_but.gif", "Test your source code", LocalPreferences.KEYTEST, 
                new ActionListener() {
                    public void actionPerformed(ActionEvent e) {
                        testButtonEvent();
                    }
                });
        buttonDefs[4] = new ButtonDef("g_submit_but.gif", "no_g_submit_but.gif", "Final submission of source code", LocalPreferences.KEYSUBMIT, 
                new ActionListener() {
                    public void actionPerformed(ActionEvent e) {
                        submitButtonEvent();
                    }
                });
        return buttonDefs;
    }

    ////////////////////////////////////////////////////////////////////////////////
    // Called when we get to intermission to disable the text view
    public void enableText(boolean enabled)
            ////////////////////////////////////////////////////////////////////////////////
    {
        if(dynamicEditor==null) return;
        dynamicEditor.setTextEnabled(new Boolean(enabled));
        this.editorList.setEnabled(enabled && isEditorAllowed());
    }

    protected boolean isEditorAllowed() {
        return true;
    }

    ////////////////////////////////////////////////////////////////////////////////
    // called when the results of a compile have finished
    public void updateCompile(boolean success, String message) {

        if(dynamicEditor==null) return;

        // Let the dynamic editor have first crack..
        if (!dynamicEditor.setCompileResults(success, message)) {
            // If not - do legacy code
            if (success) {
                Common.showMessage("Compile Results", message, this);
                setButtons(true, true, true, false, false, true, true, true);
            } else {
                MessageDialog md = new MessageDialog(this, "Compiler Results", message);
                md.show();
                setButtons(true, true, true, false, false, false, true, false);
            }
        }
    }

    public void setButtons(boolean findup, boolean finddown, boolean gto, boolean save,
            boolean compile, boolean test, boolean clear, boolean submit) {
        this.isSaved = !save;
    }

    private Language getDefaultLanguage() {
        Integer lang = new Integer(ContestConstants.LANGUAGE);
        Map prefs = parentFrame.getModel().getUserInfo().getPreferences();
        if (prefs.containsKey(lang)) {
	    if(((Integer)prefs.get(lang)).intValue() == 0)
		return JavaLanguage.JAVA_LANGUAGE;
            return BaseLanguage.getLanguage(((Integer) prefs.get(lang)).intValue());
	} else
            return JavaLanguage.JAVA_LANGUAGE;
    }

    protected String getDefaultEditor() {
        String r = PluginManager.getInstance().getDefaultEditorName();
        if (!r.equals("")) return r;
        return "Standard";
    }


    private void setLanguage(Language lang) {
        if (lang.getId() == ContestConstants.DEFAULT_LANG) {
            lang = getDefaultLanguage();
        }

        if (lang.getId() == JavaLanguage.ID && javaRadioButton.isVisible()) {
            javaRadioButton.setSelected(true);
        } else if (lang.getId() == CPPLanguage.ID && cplusplusRadioButton.isVisible()) {
            cplusplusRadioButton.setSelected(true);
        } else if (lang.getId() == CSharpLanguage.ID && csharpRadioButton.isVisible()) {
            csharpRadioButton.setSelected(true);
        } else if (lang.getId() == VBLanguage.ID && vbRadioButton.isVisible()) {
            vbRadioButton.setSelected(true);
        } else if (lang.getId() == PythonLanguage.ID && pythonRadioButton.isVisible()) {
            pythonRadioButton.setSelected(true);
        } else if (lang.getId() == Python3Language.ID && python3RadioButton.isVisible()) {
            python3RadioButton.setSelected(true);
        } else if (lang.getId() == JavaScriptLanguage.ID && JavaScriptRadioButton.isVisible()) {
            JavaScriptRadioButton.setSelected(true);
        } else {
            // POPS - 9/12/2002 -
            // If we made it here - the chose language is not a selectable type - choose the first one that is
            if (javaRadioButton.isVisible()) {
                lang = JavaLanguage.JAVA_LANGUAGE;
                javaRadioButton.setSelected(true);
            } else if (cplusplusRadioButton.isVisible()) {
                lang = CPPLanguage.CPP_LANGUAGE;
                cplusplusRadioButton.setSelected(true);
            } else if (csharpRadioButton.isVisible()) {
                lang = CSharpLanguage.CSHARP_LANGUAGE;
                csharpRadioButton.setSelected(true);
            } else {
                // Yikes - nothing is available
                lang = JavaLanguage.JAVA_LANGUAGE;
            }
        }

        currentLanguage = lang;
    }


    public int getLanguage() {
        int language = ContestConstants.JAVA;

        if (javaRadioButton.isSelected()) {
            language = ContestConstants.JAVA;
        } else if (cplusplusRadioButton.isSelected()) {
            language = ContestConstants.CPP;
        } else if (csharpRadioButton.isSelected()) {
            language = ContestConstants.CSHARP;
        } else if (vbRadioButton.isSelected()) {
            language = ContestConstants.VB;
        } else if (pythonRadioButton.isSelected()) {
            language = ContestConstants.PYTHON;
        } else if (python3RadioButton.isSelected()) {
            language = ContestConstants.PYTHON3;
        } else if (javascriptRadioButton.isSelected()) {
            language = ContestConstants.JAVASCRIPT;
        }
        return (language);
    }

    public void updateLanguageButtonStatus() {
        RoundProperties properties = getRoomModel().getRoundModel().getRoundProperties();
        javaRadioButton.setEnabled(properties.allowsLanguage(JavaLanguage.JAVA_LANGUAGE));
        cplusplusRadioButton.setEnabled(properties.allowsLanguage(CPPLanguage.CPP_LANGUAGE));
        csharpRadioButton.setEnabled(properties.allowsLanguage(CSharpLanguage.CSHARP_LANGUAGE));
        vbRadioButton.setEnabled(properties.allowsLanguage(VBLanguage.VB_LANGUAGE));
        pythonRadioButton.setEnabled(properties.allowsLanguage(PythonLanguage.PYTHON_LANGUAGE));
        python3RadioButton.setEnabled(properties.allowsLanguage(Python3Language.PYTHON3_LANGUAGE));
        javascriptRadioButton.setEnabled(properties.allowsLanguage(JavaScriptLanguage.JAVASCRIPT_LANGUAGE));
    }

    public void setEditor(String pluginName, String source) {

        // Save prior source (if it exists)
        if(source==null) source = dynamicEditor==null ? "" : getSourceCode();

        // Dispose of the current one if it exists
        if(dynamicEditor!=null) PluginManager.getInstance().disposeEditor(dynamicEditor);
        
        //remove bindings if needed
        if(dynamicEditor!=null) unsetupKeyListener(kl, dynamicEditor.getEditorPanel());
        
        // Set the item in the selection
        EditorPlugin editor = (EditorPlugin) editorDefs.get(pluginName);

        // Did we get one?
        if (editor == null) {
            Common.showMessage("Unknown Editor", "Your default editor points to a non existing editor.  Switching to 'Standard' editor instead", this);
            //System.err.println("Your default editor points to a non existing editor.  Switching to 'Standard' editor instead");
            pluginName = StandardPlugins.STANDARD;
        }
        
        // Create the new dynamic editor
        if(DEBUG) System.out.println("SetEditor: " + pluginName);
        createDynamicEditor(pluginName);
        
        // Get the panel
        editorPanel.removeAll();
        JPanel panel = dynamicEditor.getEditorPanel();
        if (panel != null) panel.setRequestFocusEnabled(true);
        if (panel != null) editorPanel.add(panel);
        
        setupKeyListener(kl, panel);

        // Make it repaint the panel
        editorPanel.invalidate();
        editorPanel.repaint();

        ProblemComponentRenderer pcRenderer =
                new ProblemComponentRenderer(component.getComponent());

        pcRenderer.setForegroundColor(pref.getColor(LocalPreferences.PROBLEMFORE));
        pcRenderer.setBackgroundColor(pref.getColor(LocalPreferences.PROBLEMBACK));
        String htmlProblemText = null;
        String plainProblemText = null;
        try {
            htmlProblemText = pcRenderer.toHTML(currentLanguage);
            plainProblemText = pcRenderer.toPlainText(currentLanguage);
        } catch (Exception e) {
            e.printStackTrace();
        }

        String className = component.getClassName();
        String methodName = component.getMethodName();
        DataType[] parms = component.getParamTypes();
        DataType rcType = component.getReturnType();

        problemPane.setText(htmlProblemText);
        problemPane.setCaretPosition(0);
        problemPanel.updateComponentInfo(component, getCurrentLanguageId());

        // TODO this is just so we don't break plugins
        ArrayList paramLabels = new ArrayList();
        for (int i = 0; i < parms.length; i++) {
            paramLabels.add(parms[i].getDescriptor(getCurrentLanguageId()));
        }

        // Set the dyanmic editor
        dynamicEditor.setLanguage(new Integer(getCurrentLanguageId()));
        dynamicEditor.setProblem(plainProblemText);
        dynamicEditor.setSignature(className, methodName, paramLabels, rcType.getDescriptor(currentLanguage));

        // POPS - updated API
        dynamicEditor.setProblemComponent(component, currentLanguage, pcRenderer);

        // Set the source
        dynamicEditor.setSource(source);
        //boolean isEnabled = this.isSaved;
    }

    private void createDynamicEditor(final String pluginName) {

        if(DEBUG) System.out.println("CreateDynamicEditor: " + pluginName);
        // Select the proper one if not selected
        // (do it later to avoid callbacks issues 
        //  from the change [ie the listener calling
        //   createDynamicEditor on the change])
        if(!editorList.getSelectedItem().equals(pluginName)) {
            SwingUtilities.invokeLater(new Runnable() {
                public void run() { 
                    ignoreToggleEvent=true;
                    editorList.setSelectedItem(pluginName); 
                    ignoreToggleEvent=false;
                }
            });
        }

        // Create the new dynamic editor
        try {
            dynamicEditor = PluginManager.getInstance().getEditor(pluginName);
        } catch (InstantiationError e) {
            if(pluginName.equals(StandardPlugins.STANDARD)) {
                Common.showMessage("Major Error", "Could not instantiate the STANDARD editor - Something is definately whacko", this);
            } else {
                Common.showMessage("Instantiation Error", "Could not instantiate the editor " + pluginName + " (see the java console for details).  Switching to 'Standard' editor instead", CodingFrame.this);
                createDynamicEditor(StandardPlugins.STANDARD);
            }
            
        } catch (NoSuchMethodError e) {
            if(pluginName.equals(StandardPlugins.STANDARD)) {
                Common.showMessage("Major Error", "Could not instantiate the STANDARD editor - Something is definately whacko", this);
            } else {
                Common.showMessage("Editor Plugin Error", "The editor " + pluginName + " does not implement the required methods for an editor plugin.  Switching to 'Standard' editor instead", CodingFrame.this);
                createDynamicEditor(StandardPlugins.STANDARD);
            }
        }

    }

    public boolean isSaved() {
        return (isSaved);
    }

    private void editorToggleEvent() {
        if(DEBUG) System.out.println("editorToggleEvent: " + (dynamicEditor==null ? "null" : dynamicEditor.getPlugin().getName()));
        setEditor((String) editorList.getSelectedItem(), null);
    }

    private void languageToggleEvent(ActionEvent e) {
        if(DEBUG) System.out.println("languageToggleEvent: " + (dynamicEditor==null ? "null" : dynamicEditor.getPlugin().getName()));
        String actionCommand = e.getActionCommand();
        int id = getCurrentLanguageId();
        Language newLanguage = null; 
        if (actionCommand.equals("Java")) {
            newLanguage = JavaLanguage.JAVA_LANGUAGE;
        } else if (actionCommand.equals("C++")) {
            newLanguage =CPPLanguage.CPP_LANGUAGE;
        } else if (actionCommand.equals("C#")) {
            newLanguage = CSharpLanguage.CSHARP_LANGUAGE;
        } else if (actionCommand.equals("VB")) {
            newLanguage=VBLanguage.VB_LANGUAGE;
        } else if (actionCommand.equals("Python")) {
            newLanguage=PythonLanguage.PYTHON_LANGUAGE;
        } else if (actionCommand.equals("Python3")) {
            newLanguage=Python3Language.PYTHON3_LANGUAGE;
        } else if (actionCommand.equals("JavaScript")) {
            newLanguage=JavaScriptLanguage.JAVASCRIPT_LANGUAGE;
        } else {
            throw new RuntimeException("unknown language: " + actionCommand);
        }
        if (newLanguage.getId() == id) {
            return;
        }
        setLanguage(newLanguage);
        setEditor((String) editorList.getSelectedItem(), null);
    }

    private void saveButtonEvent() {
        //button1.setEnabled(false);
        isSaved = true;

        /*parentFrame.setCurrentIndex(currentIndex);
        parentFrame.setCurrentIndex2(-1);*/
        parentFrame.setCurrentFrame(this);
        save();
    }

    private void save() {
        if(enabled) {
            parentFrame.getInterFrame().showMessage("Saving code...", this, ContestConstants.SAVE);
            parentFrame.getRequester().requestSave(getComponentId(), getSourceCode(), getCurrentLanguageId());
        }
    }

    protected long getComponentId() {
        return component.getID().longValue();
    }

    protected int getCurrentLanguageId() {
        return currentLanguage.getId();
    }

    private void clearButtonEvent() {
        if(enabled) {
            if (Common.confirm("Warning", "Are you sure you want to delete your source code?", this)) {
                dynamicEditor.clear();
                setButtons(false, false, false, false, false, false, false, false);
            }
        }
    }

    private void compileButtonEvent() {
        if(enabled) {
            setButtons(false, false, false, false, false, false, false, false);
            parentFrame.setCurrentFrame(this);
            // POPS 5/2/2002 - added for support of source changes prior to submission
            compiledSource = getSourceCode();
            parentFrame.getInterFrame().showMessage("Compiling...", this, ContestConstants.COMPILE);
            parentFrame.getRequester().requestCompile(compiledSource, getLanguage(), getComponentId());
        }
    }

    protected String getSourceCode() {
        return dynamicEditor.getSource();
    }

    private void testButtonEvent() {
        if(enabled) {
            setButtons(false, false, false, false, false, false, false, false);
            /*parentFrame.setCurrentIndex(currentIndex);
            parentFrame.setCurrentIndex2(-1);*/
            
            parentFrame.setCurrentFrame(this);
            parentFrame.getRequester().requestTestInfo(getComponentId());
        }
    }

    private void submitButtonEvent() {
        // POPS 5/2/2002 - added for support of source changes prior to submission
        if(!enabled)
            return;
        String submittedSource = getSourceCode();
        if (!submittedSource.equals(compiledSource)) {
            if (!Common.confirm("Warning", "You have made a change to your code since the last time you compiled.  Do you want to continue with the submit?", this)) {
                return;
            }
        }
        
        if (Common.confirm("Warning", "Would you like to submit your code ?", this)) {
            //perform check for unused code here.  Check preference first to see if we should
            if(pref.isTrue(LocalPreferences.UNUSEDCODECHECK) && !parentFrame.getPoweredByView()) {
                try {
                    UCRProcessor proc = UCRProcessorFactory.getProcessor(getCurrentLanguageId());
                    proc.initialize(component.getClassName(), component.getMethodName(), submittedSource);
                    String msg = proc.checkCode();
                    if(!msg.equals("")) {
                        if (!Common.confirm("Warning", msg, this)) {
                            return;
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            parentFrame.setCurrentFrame(this);
            submit();
        }
    }

    private void submit() {
        if(enabled) {
            parentFrame.getInterFrame().showMessage("Submitting...", this, ContestConstants.SUBMIT_PROBLEM);
            parentFrame.getRequester().requestSubmitCode(getComponentId());
        }
    }

    // POPS - 12/22/2001 - added window close event to save sizes
    ////////////////////////////////////////////////////////////////////////////////
    private void closeCodingWindow() {
        if(enabled)
            parentFrame.getRequester().requestCloseComponent(getComponentId(), parentFrame.getModel().getCurrentUser());
        
        pref.setLocation(FRAMELOCATION, this.getLocation());
        pref.setSize(FRAMESIZE, getSize());
        pref.setProperty(DIVIDERLOC, String.valueOf(jsp.getDividerLocation()));

        // Try to save the sizes - catch all errors (we probably don't have authority to save)
        try {
            pref.savePreferences();
        } catch (Throwable t) {
            t.printStackTrace();
        }
    }

    public void updateComponentSource(String code, Language lang) {
        if(DEBUG) System.out.println("updateComponentSource: " + (dynamicEditor==null ? "null" : dynamicEditor.getPlugin().getName()));
        setLanguage(lang);
        if(dynamicEditor==null) {
            setEditor(getDefaultEditor(), code);
        } else {
            setEditor((String)editorList.getSelectedItem(), code);
        }
    }
    
    protected static class ButtonDef {
        String image;
        String disabledImage;
        String toolTipText;
        String hotKeyPrefKey;
        ActionListener actionListener;
        
        public ButtonDef(String image, String disabledImage, String toolTipText, String hotKeyPrefKey, ActionListener actionListener) {
            this.image = image;
            this.disabledImage = disabledImage;
            this.toolTipText = toolTipText;
            this.hotKeyPrefKey = hotKeyPrefKey;
            this.actionListener = actionListener;
        }

    }

    /**
     * Accessor for the component model of this CodingFrame.  This was added
     * to allow the CoderRoom to pass the model to the ArgInputDialog.
     *
     * Added 3/11/2003
     *
     * author Steven Schveighoffer (schveiguy)
     *
     * @return The component member
     */
    public ProblemComponentModel getComponentModel()
    {
        return component;
    }
    
    /** Editor pane to disallow selection/copy/paste'ing */
    class NoClipboardEditorPane extends JEditorPane {
    	public NoClipboardEditorPane(String a, String b) {
    		super(a,b);
    		setHighlighter(null);
    	}
    	public void copy() {  }
    	public void cut() { }
    	public void paste() { }
    	public void replaceSelection(String n) {}
    	public String getSelectedText() { return ""; }
    	public void setSelectionStart(int s) {}
    	public void setSelectionEnd(int e) {}
    	public void select(int s, int e) {}
    	public void selectAll() {}
    }

    protected ContestApplet getParentFrame() {
        return parentFrame;
    }

    public void setRoomModel(RoomModel roomModel) {
        this.roomModel = roomModel;
    }

    public RoomModel getRoomModel() {
        return roomModel;
    }

}
