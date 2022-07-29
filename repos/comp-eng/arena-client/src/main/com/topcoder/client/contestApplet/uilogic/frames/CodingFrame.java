/*
 * Copyright (C) - 2022 TopCoder Inc., All Rights Reserved.
 */

package com.topcoder.client.contestApplet.uilogic.frames;

import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.WindowEvent;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollBar;
import javax.swing.JSplitPane;
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
import com.topcoder.client.contestApplet.rooms.CoderRoom;
import com.topcoder.client.contestApplet.rooms.CoderRoomInterface;
import com.topcoder.client.contestApplet.rooms.TeamCoderRoom;
import com.topcoder.client.contestApplet.uilogic.components.BroadcastButton;
import com.topcoder.client.contestApplet.uilogic.panels.CodingTimerPanel;
import com.topcoder.client.contestApplet.uilogic.panels.ContestSponsorPanel;
import com.topcoder.client.contestApplet.uilogic.panels.ProblemInfoComponent;
import com.topcoder.client.contestApplet.uilogic.panels.ProblemInfoPanel;
import com.topcoder.client.contestApplet.uilogic.panels.TimerPanel;
import com.topcoder.client.contestApplet.uilogic.views.ViewerLogic;
import com.topcoder.client.contestApplet.unusedCodeProcessor.UCRProcessor;
import com.topcoder.client.contestApplet.unusedCodeProcessor.UCRProcessorFactory;
import com.topcoder.client.contestApplet.widgets.MoveFocus;
import com.topcoder.client.contestant.ProblemComponentModel;
import com.topcoder.client.contestant.RoomModel;
import com.topcoder.client.render.ProblemComponentRenderer;
import com.topcoder.client.ui.UIComponent;
import com.topcoder.client.ui.UIPage;
import com.topcoder.client.ui.event.UIActionListener;
import com.topcoder.client.ui.event.UIKeyListener;
import com.topcoder.client.ui.event.UIMouseAdapter;
import com.topcoder.client.ui.event.UIWindowAdapter;
import com.topcoder.netCommon.contest.ContestConstants;
import com.topcoder.netCommon.contest.round.RoundProperties;
import com.topcoder.shared.language.BaseLanguage;
import com.topcoder.shared.language.CPPLanguage;
import com.topcoder.shared.language.CSharpLanguage;
import com.topcoder.shared.language.JavaLanguage;
import com.topcoder.shared.language.Language;
import com.topcoder.shared.language.PythonLanguage;
import com.topcoder.shared.language.Python3Language;
import com.topcoder.shared.language.VBLanguage;
import com.topcoder.shared.problem.DataType;
import com.topcoder.shared.problem.TestCase;

/**
 * The coding frame.
 *
 * <p>
 * Changes in version 1.1 (Module Assembly - TopCoder Competition Engine - Batch Test):
 * <ol>
 *      <li>Updated {@link #createButtonDef()} to create add listener for batch testing button.</li>
 *      <li>Added {@link #batchTestButtonEvent()} method to handle batch testing button event.</li>
 * </ol>
 * </p>
 * <p>
 * Changes in version 1.2 (Python3 Support):
 * <ol>
 *      <li>Added {@link #python3RadioButton} field.</li>
 *      <li>Updated {@link #getLanguage()}, {@link #setLanguage(Language)}, {@link #updateLanguageButtonStatus()},
 *      {@link #createEditorTogglePanel()}, {@link #languageToggleEvent(ActionEvent)} methods.</li>
 * </ol>
 * </p>
 *
 * @author dexy, liuliquan
 * @version 1.2
 */
public class CodingFrame implements FrameLogic, ViewerLogic {
    private static final boolean DEBUG = Boolean.getBoolean("com.topcoder.client.contestApplet.frames.CodingFrame");

    // main window
    private ContestApplet parentFrame = null;
    protected UIPage page;
    private CodingTimerPanel timerPanel = null;
    private ProblemInfoComponent problemPanel = null;

    private UIComponent problemPane;
    public UIComponent problemScroll = null;
    private UIComponent editorPanel = null;
    private UIComponent jsp;

    private UIComponent connStatus;

    protected UIComponent[] buttons;
    private BroadcastButton broadcastButton;
    private UIComponent javaRadioButton, cplusplusRadioButton, csharpRadioButton, vbRadioButton, pythonRadioButton, python3RadioButton;
    private UIComponent editorList;
    private UIComponent frame;
    private UIComponent splitToggleButton;
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

    public int testComponentID;

    private ContestSponsorPanel sponsorPanel;

    public CodingFrame(ContestApplet ca) {
        this(ca, ca.getCurrentUIManager().getUIPage("coding_frame", true));
    }

    protected CodingFrame(ContestApplet ca, UIPage page) {
        ca.closeOtherCodingViewingFrame(this);

        this.page = page;
        parentFrame = ca;
        problemPane = page.getComponent("problem_pane");
        frame = page.getComponent("root_frame");

        if(System.getProperty(NOCOPYPASTE)!=null) {
            problemPane.setProperty("ClipboardEnabled", Boolean.FALSE);
        } else {
            problemPane.setProperty("ClipboardEnabled", Boolean.TRUE);
        }

        if(DEBUG) System.out.println("Creating");
        // POPS - 12/22/2001 - added new windowclosing listener to save the sizes to the local preferences
        //this.addWindowListener(new wl("windowClosing", "closeCodingWindow", this));
        frame.addEventListener("window", new UIWindowAdapter() {
                public void windowClosing(WindowEvent e) {
                    unsetupKeyListener(kl, (Container) frame.getProperty("ContentPane"));
                    closeCodingWindow();
                }
            });
        problemPane.addEventListener("hyperlink", new HyperLinkLoader(parentFrame.getAppletContext()));
        splitToggleButton = page.getComponent("problem_split_toggle_button");
        splitToggleButton.addEventListener("mouse", new UIMouseAdapter() {
                public void mouseClicked(MouseEvent e) {
                    splitToggle();
                }
            });
    }

    private void splitToggle() {
        if (((Integer) jsp.getProperty("Orientation")).intValue() == JSplitPane.VERTICAL_SPLIT) {
            jsp.setProperty("Orientation", new Integer(JSplitPane.HORIZONTAL_SPLIT));
            splitToggleButton.setProperty("Enabled", Boolean.FALSE);
        } else {
            jsp.setProperty("Orientation", new Integer(JSplitPane.VERTICAL_SPLIT));
            splitToggleButton.setProperty("Enabled", Boolean.TRUE);
        }
    }

    public UIComponent getFrame() {
        return frame;
    }

    public void setCR(CoderRoomInterface c) {
        cr = c;
    }

    private void unsetupKeyListener(UIKeyListener kl, Container comp) {
        comp.removeKeyListener(kl);
        for(int i = 0; i < comp.getComponentCount(); i++) {
            if(comp instanceof Container)
                unsetupKeyListener(kl, (Container)comp.getComponent(i));
            else
                unsetupKeyListener(kl, comp.getComponent(i));
        }
    }

    private void unsetupKeyListener(UIKeyListener kl, Component comp) {
        comp.removeKeyListener(kl);
    }

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
            parentFrame.getInterFrame().showMessage("Testing...", (JFrame) frame.getEventSource(), ContestConstants.TEST);
            parentFrame.getRequester().requestTest((ArrayList) info.get(1), componentID);
        }
    }

    public void dispose() {
        if(DEBUG) System.out.println("dispose: " + (dynamicEditor==null ? "null" : dynamicEditor.getPlugin().getName()));
        if(dynamicEditor!=null) {
            PluginManager.getInstance().disposeEditor(dynamicEditor);
            dynamicEditor=null;
        }
        frame.performAction("dispose");
    }

    public void hide() {
        if(DEBUG) System.out.println("hide: " + (dynamicEditor==null ? "null" : dynamicEditor.getPlugin().getName()));
        if(dynamicEditor!=null) {
            PluginManager.getInstance().disposeEditor(dynamicEditor);
            dynamicEditor=null;
        }
        frame.performAction("hide");
    }

    public void setVisible(boolean visible) {
        if(DEBUG) System.out.println("setVisible: " + (dynamicEditor==null ? "null" : dynamicEditor.getPlugin().getName()));
        if(dynamicEditor!=null) {
            PluginManager.getInstance().disposeEditor(dynamicEditor);
            dynamicEditor=null;
        }
        frame.setProperty("Visible", Boolean.valueOf(visible));
    }

    public void clear() {
        problemPane.setProperty("Text", "");
        ((JScrollBar) problemScroll.getProperty("VerticalScrollBar")).setValue(0);
        if(dynamicEditor!=null) dynamicEditor.clear();
    }

    public void resetFocus() {
        // show is threaded so grab focus doesn't always work
        MoveFocus.moveFocus(editorList);
        MoveFocus.moveFocus(editorPanel);


        SwingUtilities.invokeLater(new Runnable() {
                public void run() {
                    editorPanel.performAction("requestFocus");
                }
            });
    }

    public void showFrame(boolean enabled) {
        if(DEBUG) System.out.println("showFrame: " + (dynamicEditor==null ? "null" : dynamicEditor.getPlugin().getName()));
        if(dynamicEditor==null) setEditor(getDefaultEditor(), null);
        updateLanguageButtonStatus();
        enableText(enabled);
        frame.performAction("show");
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
            setEditor((String)editorList.getProperty("SelectedItem"), component.getDefaultSolution());
        }
    }


    protected ProblemInfoComponent newProblemInfoPanel() {
        return new ProblemInfoPanel(parentFrame, page);
    }

    //------------------------------------------------------------------------------
    // Create the room
    //------------------------------------------------------------------------------

    public void create() {
        timerPanel = new CodingTimerPanel(parentFrame, page);
        problemPanel = newProblemInfoPanel();

        // create all the panels/panes
        createSourceCodePanel();
        createButtonPanel();
        createEditorTogglePanel();
        //        sourcePanel = sc;

        // Combine the source code and the button panels
        jsp = page.getComponent("problem_editor_split_pane");

        //sp.setPreferredSize(new Dimension(675, 310));
        //sp.setPreferredSize(new Dimension(760, 360));
        //sp.setDividerLocation(80);

        // set the preferred font on the problem statement, and set the color.
        this.problemPane.setProperty("Font", new Font(pref.getFont(LocalPreferences.PROBLEMFONT), Font.PLAIN, pref.getFontSize(LocalPreferences.PROBLEMFONTSIZE)));

        //this.problemPane.setEditorKit(new HTMLEditorKit());
        ((HTMLDocument) this.problemPane.getProperty("Document")).getStyleSheet().addRule("body {font-family: " + pref.getFont(LocalPreferences.PROBLEMFONT) + ";}");
        ((HTMLDocument) this.problemPane.getProperty("Document")).getStyleSheet().addRule("body {font-size: " + pref.getFontSize(LocalPreferences.PROBLEMFONTSIZE) + "pt;}");

        ((HTMLDocument) this.problemPane.getProperty("Document")).getStyleSheet().addRule("pre {font-family: " + pref.getFont(LocalPreferences.PROBLEMFIXEDFONT) + ";}");
        ((HTMLDocument) this.problemPane.getProperty("Document")).getStyleSheet().addRule("pre {font-size: " + pref.getFontSize(LocalPreferences.PROBLEMFIXEDFONTSIZE) + "pt;}");

        this.problemPane.setProperty("Foreground", pref.getColor(LocalPreferences.PROBLEMFORE));
        this.problemPane.setProperty("Background", pref.getColor(LocalPreferences.PROBLEMBACK));

        // Get the scroll area
        this.problemScroll = page.getComponent("problem_description_scroll_pane");

        // Initially, disable all of them until a key is pressed
        setButtons(false, false, false, false, false, false, false, false);

        connStatus = page.getComponent("connection_status");
        //Common.insertInPanel(tp, stp, gbc, 0, 1, 2, 1, 0.0, 0.0);

        sponsorPanel = newUnderProblemInfoPanel();

        frame.performAction("pack");

        //setup key listener
        setupKeyListener(kl, (Container) frame.getProperty("ContentPane"));

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
        frame.setProperty("Location", frameLocation);

        // Restore the frame size (adjusted for the constraints of the window)
        Dimension frameSize = pref.getSize(FRAMESIZE);
        if (frameSize == null) frameSize = new Dimension(760, 360);
        frame.setProperty("Size", Common.adjustWindowSize(frameLocation, frameSize));

        // Restore the divider location
        String dividerLocation = pref.getProperty(DIVIDERLOC);
        if (dividerLocation == null) dividerLocation = "80";
        jsp.setProperty("DividerLocation", Integer.valueOf(dividerLocation));
    }

    protected ContestSponsorPanel newUnderProblemInfoPanel() {
        return new ContestSponsorPanel(page.getComponent("sponsor_logo"), getCodingFrameSponsorURLString());
    }

    protected String getCodingFrameSponsorURLString() {
        return CommonData.getSponsorCodingFrameImageAddr(parentFrame.getSponsorName(), getRoomModel());
    }

    HashMap keyPrefs = new HashMap();
    HashMap keyButtons = new HashMap();

    //load hotkey from local preferences
    private UIKeyListener kl = new UIKeyListener() {
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
                        UIComponent btn = (UIComponent)keyButtons.get(key);

                        btn.performAction("doClick");
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
        }
        if((evt.getModifiers() & KeyEvent.ALT_MASK) != 0) {
            alt = true;
        }
        if((evt.getModifiers() & KeyEvent.SHIFT_MASK) != 0) {
            shift = true;
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

    private void setupKeyListener(UIKeyListener kl, Container comp) {
        comp.removeKeyListener(kl);
        comp.addKeyListener(kl);
        for(int i = 0; i < comp.getComponentCount(); i++) {
            if(comp instanceof Container)
                setupKeyListener(kl, (Container)comp.getComponent(i));
            else
                setupKeyListener(kl, comp.getComponent(i));
        }
    }

    private void setupKeyListener(UIKeyListener kl, Component comp) {
        comp.removeKeyListener(kl);
        comp.addKeyListener(kl);
    }

    private void createEditorTogglePanel() {
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
        editorList = page.getComponent("editor_list");

        editorList.performAction("removeAllItems");
        for (int x = 0; x < names.length; x++) editorList.performAction("addItem", new Object[] {names[x]});
        editorList.setProperty("UI", editorList.getProperty("UI"));

        // Set the default
        editorList.setProperty("SelectedItem", getDefaultEditor());

        // Create the various objects
        UIActionListener actionListener = new UIActionListener() {
                public void actionPerformed(ActionEvent e) {
                    languageToggleEvent(e);
                }
            };

        // Create Java radiobutton
        javaRadioButton = page.getComponent("java_radio_button");
        javaRadioButton.addEventListener("action", actionListener);

        // Create C++ radiobutton
        cplusplusRadioButton = page.getComponent("c++_radio_button");
        cplusplusRadioButton.addEventListener("action", actionListener);

        // Create C# radiobutton
        csharpRadioButton = page.getComponent("c#_radio_button");
        csharpRadioButton.addEventListener("action", actionListener);

        // Create VB radio button
        vbRadioButton = page.getComponent("vb_radio_button");
        vbRadioButton.addEventListener("action", actionListener);

        // Create Python radio button
        pythonRadioButton = page.getComponent("python_radio_button");
        pythonRadioButton.addEventListener("action", actionListener);

        // Create Python3 radio button
        python3RadioButton = page.getComponent("python3_radio_button");
        python3RadioButton.addEventListener("action", actionListener);

        // Add the listeners
        //editorList.addActionListener(new al("actionPerformed", "editorToggleEvent", this));
        editorList.addEventListener("action", new UIActionListener() {
                public void actionPerformed(ActionEvent e) {
                    if(!ignoreToggleEvent) editorToggleEvent();
                }
            });
    }

    private void createSourceCodePanel() {
        // Create the panel
        editorPanel = page.getComponent("editor_panel");
    }

    public boolean isPanelEnabled() {
        return enabled;
    }

    public void setPanelEnabled(boolean on) {
        enabled = on;

        connStatus.setProperty("Enabled", Boolean.valueOf(on));
        for (int i=0;i<buttons.length;++i) {
            buttons[i].setProperty("Enabled", Boolean.valueOf(on));
        }
        broadcastButton.setEnabled(on);
    }

    protected static class ButtonDef {
        public UIActionListener listener;
        public String hotKey;

        public ButtonDef(UIActionListener listener, String hotKey) {
            this.listener = listener;;
            this.hotKey = hotKey;
        }
    }

    /**
     * Creates button definitions of the coding frame.
     *
     * @return the map of the button titles and button definitions
     */
    protected Map createButtonDef() {
        Map map = new HashMap();
        map.put("save_button", new ButtonDef(new UIActionListener() {
                public void actionPerformed(ActionEvent e) {
                    saveButtonEvent();
                }
            }, LocalPreferences.KEYSAVE));
        map.put("clear_button", new ButtonDef(new UIActionListener() {
                public void actionPerformed(ActionEvent e) {
                    clearButtonEvent();
                }
            }, null));
        map.put("compile_button", new ButtonDef(new UIActionListener() {
                public void actionPerformed(ActionEvent e) {
                    compileButtonEvent();
                }
            }, LocalPreferences.KEYCOMPILE));
        map.put("test_button", new ButtonDef(new UIActionListener() {
                public void actionPerformed(ActionEvent e) {
                    testButtonEvent();
                }
            }, LocalPreferences.KEYTEST));
        map.put("batch_test_button", new ButtonDef(new UIActionListener() {
            public void actionPerformed(ActionEvent e) {
                batchTestButtonEvent();
            }
        }, LocalPreferences.KEYBATCHTEST));
        map.put("submit_button", new ButtonDef(new UIActionListener() {
                public void actionPerformed(ActionEvent e) {
                    submitButtonEvent();
                }
            }, LocalPreferences.KEYSUBMIT));
        return map;
    }

    private void createButtonPanel() {
        Map map = createButtonDef();
        buttons = new UIComponent[map.size()];
        int i=0;
        for (Iterator iter = map.entrySet().iterator(); iter.hasNext(); ++i) {
            Map.Entry entry = (Map.Entry) iter.next();
            ButtonDef def = (ButtonDef) entry.getValue();
            buttons[i] = page.getComponent((String) entry.getKey());
            buttons[i].addEventListener("action", def.listener);
            if (def.hotKey != null) {
                keyPrefs.put(def.hotKey, pref.getHotKey(def.hotKey));
                keyButtons.put(def.hotKey, buttons[i]);
            }
        }
        broadcastButton = new BroadcastButton(parentFrame, page.getComponent("broadcast_button"));
    }

    ////////////////////////////////////////////////////////////////////////////////
    // Called when we get to intermission to disable the text view
    public void enableText(boolean enabled)
        ////////////////////////////////////////////////////////////////////////////////
    {
        if(dynamicEditor==null) return;
        dynamicEditor.setTextEnabled(new Boolean(enabled));
        this.editorList.setProperty("Enabled", Boolean.valueOf(enabled && isEditorAllowed()));
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
                Common.showMessage("Compile Results", message, (JFrame) frame.getEventSource());
                setButtons(true, true, true, false, false, true, true, true);
            } else {
                MessageDialog md = new MessageDialog(parentFrame, frame, "Compiler Results", message);
                md.showDialog();
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

        if (lang.getId() == JavaLanguage.ID && ((Boolean) javaRadioButton.getProperty("Visible")).booleanValue()) {
            javaRadioButton.setProperty("Selected", Boolean.TRUE);
        } else if (lang.getId() == CPPLanguage.ID && ((Boolean) cplusplusRadioButton.getProperty("Visible")).booleanValue()) {
            cplusplusRadioButton.setProperty("Selected", Boolean.TRUE);
        } else if (lang.getId() == CSharpLanguage.ID && ((Boolean) csharpRadioButton.getProperty("Visible")).booleanValue()) {
            csharpRadioButton.setProperty("Selected", Boolean.TRUE);
        } else if (lang.getId() == VBLanguage.ID && ((Boolean) vbRadioButton.getProperty("Visible")).booleanValue()) {
            vbRadioButton.setProperty("Selected", Boolean.TRUE);
        } else if (lang.getId() == PythonLanguage.ID && ((Boolean) pythonRadioButton.getProperty("Visible")).booleanValue()) {
            pythonRadioButton.setProperty("Selected", Boolean.TRUE);
        } else if (lang.getId() == Python3Language.ID && ((Boolean) python3RadioButton.getProperty("Visible")).booleanValue()) {
            python3RadioButton.setProperty("Selected", Boolean.TRUE);
        } else {
            // POPS - 9/12/2002 -
            // If we made it here - the chose language is not a selectable type - choose the first one that is
            if (((Boolean) javaRadioButton.getProperty("Visible")).booleanValue()) {
                lang = JavaLanguage.JAVA_LANGUAGE;
                javaRadioButton.setProperty("Selected", Boolean.TRUE);
            } else if (((Boolean) cplusplusRadioButton.getProperty("Visible")).booleanValue()) {
                lang = CPPLanguage.CPP_LANGUAGE;
                cplusplusRadioButton.setProperty("Selected", Boolean.TRUE);
            } else if (((Boolean) csharpRadioButton.getProperty("Visible")).booleanValue()) {
                lang = CSharpLanguage.CSHARP_LANGUAGE;
                csharpRadioButton.setProperty("Selected", Boolean.TRUE);
            } else {
                // Yikes - nothing is available
                lang = JavaLanguage.JAVA_LANGUAGE;
            }
        }

        currentLanguage = lang;
    }


    public int getLanguage() {
        int language = ContestConstants.JAVA;

        if (((Boolean) javaRadioButton.getProperty("Selected")).booleanValue()) {
            language = ContestConstants.JAVA;
        } else if (((Boolean) cplusplusRadioButton.getProperty("Selected")).booleanValue()) {
            language = ContestConstants.CPP;
        } else if (((Boolean) csharpRadioButton.getProperty("Selected")).booleanValue()) {
            language = ContestConstants.CSHARP;
        } else if (((Boolean) vbRadioButton.getProperty("Selected")).booleanValue()) {
            language = ContestConstants.VB;
        } else if (((Boolean) pythonRadioButton.getProperty("Selected")).booleanValue()) {
            language = ContestConstants.PYTHON;
        } else if (((Boolean) python3RadioButton.getProperty("Selected")).booleanValue()) {
            language = ContestConstants.PYTHON3;
        }
        return (language);
    }

    public void updateLanguageButtonStatus() {
        RoundProperties properties = getRoomModel().getRoundModel().getRoundProperties();
        javaRadioButton.setProperty("enabled", Boolean.valueOf(properties.allowsLanguage(JavaLanguage.JAVA_LANGUAGE)));
        cplusplusRadioButton.setProperty("enabled", Boolean.valueOf(properties.allowsLanguage(CPPLanguage.CPP_LANGUAGE)));
        csharpRadioButton.setProperty("enabled", Boolean.valueOf(properties.allowsLanguage(CSharpLanguage.CSHARP_LANGUAGE)));
        vbRadioButton.setProperty("enabled", Boolean.valueOf(properties.allowsLanguage(VBLanguage.VB_LANGUAGE)));
        pythonRadioButton.setProperty("enabled", Boolean.valueOf(properties.allowsLanguage(PythonLanguage.PYTHON_LANGUAGE)));
        python3RadioButton.setProperty("enabled", Boolean.valueOf(properties.allowsLanguage(Python3Language.PYTHON3_LANGUAGE)));
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
            Common.showMessage("Unknown Editor", "Your default editor points to a non existing editor.  Switching to 'Standard' editor instead", (JFrame) frame.getEventSource());
            //System.err.println("Your default editor points to a non existing editor.  Switching to 'Standard' editor instead");
            pluginName = StandardPlugins.STANDARD;
        }

        // Create the new dynamic editor
        if(DEBUG) System.out.println("SetEditor: " + pluginName);
        createDynamicEditor(pluginName);

        // Get the panel
        editorPanel.performAction("removeAll");
        JPanel panel = dynamicEditor.getEditorPanel();
        if (panel != null) panel.setRequestFocusEnabled(true);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.BOTH;
        gbc.weightx=1;
        gbc.weighty=1;
        gbc.gridx=0;
        gbc.gridy=0;
        if (panel != null) editorPanel.performAction("add", new Object[] {panel, gbc});

        setupKeyListener(kl, panel);

        // Make it repaint the panel
        editorPanel.performAction("invalidate");
        editorPanel.performAction("repaint");

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

        problemPane.setProperty("Text", htmlProblemText);
        problemPane.setProperty("CaretPosition", new Integer(0));
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
        if(!editorList.getProperty("SelectedItem").equals(pluginName)) {
            SwingUtilities.invokeLater(new Runnable() {
                    public void run() {
                        ignoreToggleEvent=true;
                        editorList.setProperty("SelectedItem", pluginName);
                        ignoreToggleEvent=false;
                    }
                });
        }

        // Create the new dynamic editor
        try {
            dynamicEditor = PluginManager.getInstance().getEditor(pluginName);
        } catch (InstantiationError e) {
            if(pluginName.equals(StandardPlugins.STANDARD)) {
                Common.showMessage("Major Error", "Could not instantiate the STANDARD editor - Something is definately whacko", (JFrame) frame.getEventSource());
            } else {
                Common.showMessage("Instantiation Error", "Could not instantiate the editor " + pluginName + " (see the java console for details).  Switching to 'Standard' editor instead", (JFrame) frame.getEventSource());
                createDynamicEditor(StandardPlugins.STANDARD);
            }

        } catch (NoSuchMethodError e) {
            if(pluginName.equals(StandardPlugins.STANDARD)) {
                Common.showMessage("Major Error", "Could not instantiate the STANDARD editor - Something is definately whacko", (JFrame) frame.getEventSource());
            } else {
                Common.showMessage("Editor Plugin Error", "The editor " + pluginName + " does not implement the required methods for an editor plugin.  Switching to 'Standard' editor instead", (JFrame) frame.getEventSource());
                createDynamicEditor(StandardPlugins.STANDARD);
            }
        }

    }

    public boolean isSaved() {
        return (isSaved);
    }

    private void editorToggleEvent() {
        if(DEBUG) System.out.println("editorToggleEvent: " + (dynamicEditor==null ? "null" : dynamicEditor.getPlugin().getName()));
        setEditor((String) editorList.getProperty("SelectedItem"), null);
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
        } else {
            throw new RuntimeException("unknown language: " + actionCommand);
        }
        if (newLanguage.getId() == id) {
            return;
        }
        setLanguage(newLanguage);
        setEditor((String) editorList.getProperty("SelectedItem"), null);
    }

    private void saveButtonEvent() {
        //saveButton.setEnabled(false);
        isSaved = true;

        /*parentFrame.setCurrentIndex(currentIndex);
          parentFrame.setCurrentIndex2(-1);*/
        parentFrame.setCurrentFrame((JFrame) frame.getEventSource());
        save();
    }

    private void save() {
        if(enabled) {
            parentFrame.getInterFrame().showMessage("Saving code...", (JFrame) frame.getEventSource(), ContestConstants.SAVE);
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
            if (Common.confirm("Warning", "Are you sure you want to delete your source code?", (JFrame) frame.getEventSource())) {
                dynamicEditor.clear();
                setButtons(false, false, false, false, false, false, false, false);
            }
        }
    }

    private void compileButtonEvent() {
        if(enabled) {
            setButtons(false, false, false, false, false, false, false, false);
            parentFrame.setCurrentFrame((JFrame) frame.getEventSource());
            // POPS 5/2/2002 - added for support of source changes prior to submission
            compiledSource = getSourceCode();
            parentFrame.getInterFrame().showMessage("Compiling...", (JFrame) frame.getEventSource(), ContestConstants.COMPILE);
            parentFrame.getRequester().requestCompile(compiledSource, getLanguage(), getComponentId());
        }
    }


    private void testButtonEvent() {
        System.out.println(enabled);
        if(enabled) {
            setButtons(false, false, false, false, false, false, false, false);
            /*parentFrame.setCurrentIndex(currentIndex);
              parentFrame.setCurrentIndex2(-1);*/

            parentFrame.setCurrentFrame((JFrame) frame.getEventSource());
            parentFrame.getRequester().requestTestInfo(getComponentId());
        }
    }

    /**
     * Handles batch test button click event.
     * @since 1.1
     */
    private void batchTestButtonEvent() {
        if (enabled) {
            setButtons(false, false, false, false, false, false, false, false);
            parentFrame.setCurrentFrame((JFrame) frame.getEventSource());

            TestCase[] testCases = component.getTestCases();
            DataType[] params = component.getParamTypes();
            ArrayList tests = new ArrayList(testCases.length);
            for (int itest = 0; itest < testCases.length; itest++) {
                ArrayList test = new ArrayList(params.length);
                String[] input = testCases [itest].getInput();

                for (int iparam = 0; iparam < params.length; iparam++) {
                    //
                    // check to see what the input is
                    //
                    if (params[iparam].getDescription().equals("ArrayList")
                            || params[iparam].getDescription().startsWith("vector")
                            || params[iparam].getDescription().endsWith("[]")) {
                        //
                        // an arraylist datatype
                        //
                        test.add(iparam, ArgInputDialog.bracketParse(input[iparam]));
                    } else {
                        //
                        // a string/integer datatype
                        //
                        if (params[iparam].getDescription().equalsIgnoreCase("string")) {
                            test.add(iparam, ((ArgInputDialog.bracketParse(input[iparam]).get(0))));
                        } else if (params[iparam].getDescription().equalsIgnoreCase("char")) {
                            test.add(iparam, input[iparam].substring(1, input[iparam].length() - 1));
                        } else {
                            test.add(iparam, input[iparam]);
                        }
                    }
                }
                tests.add(itest, test);
            }
            parentFrame.getInterFrame().showMessage("Batch testing...", (JFrame) frame.getEventSource(),
                    ContestConstants.BATCH_TEST);
            parentFrame.getRequester().requestBatchTest(tests, getComponentId());
        }
    }

    private void submitButtonEvent() {
        // POPS 5/2/2002 - added for support of source changes prior to submission
        if(!enabled)
            return;
        String submittedSource = getSourceCode();
        if (!submittedSource.equals(compiledSource)) {
            if (!Common.confirm("Warning", "You have made a change to your code since the last time you compiled.  Do you want to continue with the submit?", (JFrame) frame.getEventSource())) {
                return;
            }
        }

        if (Common.confirm("Warning", "Would you like to submit your code ?", (JFrame) frame.getEventSource())) {
            //perform check for unused code here.  Check preference first to see if we should
            if(pref.isTrue(LocalPreferences.UNUSEDCODECHECK) && !parentFrame.getPoweredByView()) {
                try {
                    UCRProcessor proc = UCRProcessorFactory.getProcessor(getCurrentLanguageId());
                    proc.initialize(component.getClassName(), component.getMethodName(), submittedSource);
                    String msg = proc.checkCode();
                    if(!msg.equals("")) {
                        if (!Common.confirm("Warning", msg, (JFrame) frame.getEventSource())) {
                            return;
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            parentFrame.setCurrentFrame((JFrame) frame.getEventSource());
            submit();
        }
    }

    private void submit() {
        if(enabled) {
            parentFrame.getInterFrame().showMessage("Submitting...", (JFrame) frame.getEventSource(), ContestConstants.SUBMIT_PROBLEM);
            parentFrame.getRequester().requestSubmitCode(getComponentId());
        }
    }

    public void closeWindow() {
        if (cr != null) {
            cr.closeCodingWindow();
        }
        closeCodingWindow();
    }

    // POPS - 12/22/2001 - added window close event to save sizes
    ////////////////////////////////////////////////////////////////////////////////
    private void closeCodingWindow() {
        if(enabled && component != null)
            parentFrame.getRequester().requestCloseComponent(getComponentId(), parentFrame.getModel().getCurrentUser());

        pref.setLocation(FRAMELOCATION, (Point) frame.getProperty("Location"));
        pref.setSize(FRAMESIZE, (Dimension) frame.getProperty("Size"));
        pref.setProperty(DIVIDERLOC, String.valueOf(jsp.getProperty("DividerLocation")));

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
            setEditor((String)editorList.getProperty("SelectedItem"), code);
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

    public boolean isShowing() {
        return ((Boolean) frame.getProperty("showing")).booleanValue();
    }

    protected String getSourceCode() {
        return dynamicEditor.getSource();
    }

    protected ContestApplet getParentFrame() {
        return parentFrame;
    }

    public void setRoomModel(RoomModel roomModel) {
        this.roomModel = roomModel;
        if (sponsorPanel != null) {
            sponsorPanel.updateURL(getCodingFrameSponsorURLString());
        }
    }

    public RoomModel getRoomModel() {
        return roomModel;
    }
}
