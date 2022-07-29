/*
* Copyright (C) - 2014 TopCoder Inc., All Rights Reserved.
*/
package com.topcoder.client.contestApplet.uilogic.frames;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseListener;
import java.util.ArrayList;
import java.util.Hashtable;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import com.topcoder.client.contestApplet.ContestApplet;
import com.topcoder.client.contestApplet.common.Common;
import com.topcoder.client.contestant.ProblemComponentModel;
import com.topcoder.client.ui.UIComponent;
import com.topcoder.client.ui.UIPage;
import com.topcoder.client.ui.event.UIActionListener;
import com.topcoder.netCommon.contest.Matrix2D;
import com.topcoder.shared.language.JavaLanguage;
import com.topcoder.shared.problem.DataType;
import com.topcoder.shared.problem.TestCase;

/**
 *
 * <p>
 * Changes in version 1.1 (BUGR-10410 - Remove Hardcoded Limit of 50 on Input Data):
 * <ol>
 *      <li>Update {@link #cancelButtonEvent()} method to remove 50 limit.</li>
 *      <li>Update {@link #bracketParse(String text)} method to remove 50 limit.</li>
 * </ol>
 * </p>
 *
 * <p>
 * Changes in version 1.2 (Module Assembly - TopCoder Competition Engine - Batch Test):
 * <ol>
 *      <li>Changed to static {@link #START}, {@link #IN_QUOTE}, {@link #ESCAPE},
 *      {@link #bracketParse(String)} so that can be used in CodingFrame to process the test arguments.</li>
 * </ol>
 * </p>
 *
 * @author savon_cn
 * @version 1.2
 */
public class ArgInputDialog implements FrameLogic {
    private FrameLogic frame;
    private UIComponent dialog;
    private UIPage page;
    private ContestApplet ca;
    private ArrayList originalArgs = null;
    private ArrayList fields = null;
    private ArrayList info = null;
    private DataType[] params = null;
    private Hashtable buttons = null;
    private boolean status = false;
    private boolean confirm = false;
    //
    // Added 3/11/2003 by schveiguy -- to support example testing
    //
    private ProblemComponentModel probComponent = null;

    //
    // Added 3/13/2003 by schveiguy -- to allow buttons to be looked up by
    // index.  This is for when an example is selected, the button text can be
    // changed to "modify"
    //
    private JButton[] buttonsByIndex = null;

    public UIComponent getFrame() {
        return dialog;
    }

    /**
     * Class constructor
     */
    ////////////////////////////////////////////////////////////////////////////////
    public ArgInputDialog(ContestApplet ca, FrameLogic frame, DataType[] params, boolean confirm)
        ////////////////////////////////////////////////////////////////////////////////
    {
        this(ca, frame, params, null, confirm);
    }

    ////////////////////////////////////////////////////////////////////////////////
    public ArgInputDialog(ContestApplet ca, FrameLogic frame, String msg, DataType[] params, boolean confirm)
        ////////////////////////////////////////////////////////////////////////////////
    {
        this(ca, frame, params, msg, null, confirm, null);
    }

    ////////////////////////////////////////////////////////////////////////////////
    public ArgInputDialog(ContestApplet ca, FrameLogic frame, String msg, DataType[] params, boolean confirm, String[] parameterNames)
        ////////////////////////////////////////////////////////////////////////////////
    {
        this(ca, frame, params, msg, null, confirm, null);
    }

    ////////////////////////////////////////////////////////////////////////////////
    public ArgInputDialog(ContestApplet ca, FrameLogic frame, DataType[] params, ArrayList args, boolean confirm)
        ////////////////////////////////////////////////////////////////////////////////
    {
        this(ca, frame, params, null, args, confirm, null);
    }

    public ArgInputDialog(ContestApplet ca, FrameLogic frame, DataType[] params, String msg, ArrayList args, boolean confirm, ProblemComponentModel probComponent) {
        this(ca, frame, params, msg, args, confirm, probComponent, JavaLanguage.ID);
    }

    ////////////////////////////////////////////////////////////////////////////////
    //
    // Modified 3/13/2003 by schveiguy -- this constructor used to be private,
    // but it is now public to allow creation of a message AND have current
    // arguments.  All other constructors call this one anyways.  Also added
    // the ProblemComponentModel argument to allow example testing
    //
    public ArgInputDialog(ContestApplet _ca, FrameLogic frame, DataType[] params, String msg, ArrayList args, boolean confirm, ProblemComponentModel probComponent, int language)
        ////////////////////////////////////////////////////////////////////////////////
    {
        this.ca = _ca;
        page = ca.getCurrentUIManager().getUIPage("arg_input_dialog", true);
        dialog = page.getComponent("root_dialog", false);
        dialog.setProperty("owner", frame.getFrame().getEventSource());
        dialog.create();

        this.confirm = confirm;
        this.frame = frame;
        this.params = params;
        this.fields = new ArrayList(params.length);
        this.info = new ArrayList(2);
        this.buttons = new Hashtable();
        this.originalArgs = args;
        this.probComponent = probComponent;
        String[] parameterNames = probComponent.getParamNames();

        if (msg != null) {
            Component mouseless = (Component) page.getComponent("instruction_pane").getEventSource();
            MouseListener[] listeners = mouseless.getMouseListeners();
            for (int i=0;i<listeners.length;++i) {
                mouseless.removeMouseListener(listeners[i]);
            }
            page.getComponent("instruction_pane").setProperty("text", msg);
        } else {
            page.getComponent("instruction_panel").setProperty("visible", Boolean.FALSE);
        }

        // just in case something goes wrong with the cache.
        if ((args != null) && (args.size() != params.length)) {
            args = null;
        }

        UIComponent textFieldTemplate = page.getComponent("arg_textfield_template");
        JPanel argPanel = (JPanel) page.getComponent("arg_panel").getEventSource();
        UIComponent labelTemplate = page.getComponent("arg_label_template");
        UIComponent buttonTemplate = page.getComponent("arg_button_template");
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1;
        gbc.weighty = 1;
        buttonsByIndex = new JButton[params.length];
        for (int i = 0; i < params.length; i++) {
            JLabel label = (JLabel) labelTemplate.performAction("clone");
            label.setText("(" + (i + 1) + ") " + params[i].getDescriptor(language) + " "+parameterNames[i]);
            gbc.gridx=0; gbc.gridy=i;
            argPanel.add(label, gbc);

            if (params[i].getDescription().equals("ArrayList") ||
                params[i].getDescription().startsWith("vector") ||
                params[i].getDescription().endsWith("[]")) {
                JButton b = (JButton) buttonTemplate.performAction("clone");
                b.setText(args == null ? "create" : "modify");
                //b.addActionListener(new al("actionPerformed", "alButtonEvent", this));
                b.addActionListener(new ActionListener() {
                        public void actionPerformed(ActionEvent e) {
                            alButtonEvent(e);
                        }
                    });
                b.setActionCommand(params[i].getDescriptor(language));
                buttons.put(b, new Integer(i));
                buttonsByIndex[i] = b;
                fields.add(args == null ? new ArrayList() : (ArrayList) args.get(i));
                gbc.gridx=1;
                argPanel.add(b, gbc);
            } else {
                JTextField tf = (JTextField) textFieldTemplate.performAction("Clone");
                tf.setText(args == null ? "" : (String) args.get(i));
                fields.add(tf);
                gbc.gridx=1;
                argPanel.add(tf, gbc);
            }
        }

        argPanel.setPreferredSize(new Dimension(0, 33 * params.length));
        argPanel.setMinimumSize(new Dimension(0, 33 * params.length));

        page.getComponent("ok_button").addEventListener("action", new UIActionListener() {
                public void actionPerformed(ActionEvent e) {
                    okButtonEvent();
                }
            });

        page.getComponent("cancel_button").addEventListener("action", new UIActionListener() {
                public void actionPerformed(ActionEvent e) {
                    cancelButtonEvent();
                }
            });

        if(probComponent != null && probComponent.hasTestCases()) {
            TestCase[] examples = probComponent.getTestCases();
            UIComponent exampleList = page.getComponent("example_list");
            exampleList.performAction("addItem", new Object[] {"Select Example..."});
            for(int x = 0; x < examples.length; x++)
                exampleList.performAction("addItem", new Object[] {"Example " + x});
            exampleList.setProperty("UI", exampleList.getProperty("UI"));

            //
            // see if any of the examples are the current arguments
            //
            if(args != null) {
                example_outer:
                for(int i = 0; i < examples.length; i++) {
                    String[] curex = examples[i].getInput();
                    for(int j = 0; j < params.length; j++) {
                        if (params[j].getDescription().equals("ArrayList") ||
                            params[j].getDescription().startsWith("vector") ||
                            params[j].getDescription().endsWith("[]")) {
                            ArrayList a = (ArrayList)args.get(j);
                            if(!a.equals(bracketParse(curex[j])))
                                continue example_outer;
                        }
                        else {
                            String a = (String)args.get(j);
                            String ca = null;
                            if(params[j].getDescription().equalsIgnoreCase("string") ||
                               params[j].getDescription().equals("char"))
                                ca = curex[j].substring(1, curex[j].length() - 1);
                            else
                                ca = curex[j];
                            if(!a.equals(ca))
                                continue example_outer;
                        }
                    }
                    //
                    // this is the example, set the selection up in the
                    // JList
                    //
                    exampleList.setProperty("SelectedIndex", new Integer(i + 1));
                    break;
                }
            }
            exampleList.addEventListener("action", new UIActionListener() {
                    public void actionPerformed(ActionEvent e) {
                        //
                        // load the example data from the example test case
                        //
                        JComboBox cb = (JComboBox)e.getSource();
                        loadExample(cb.getSelectedIndex() - 1);
                    }
                });
        } else {
            page.getComponent("example_list").setProperty("visible", Boolean.FALSE);
        }

        dialog.performAction("pack");
        Common.setLocationRelativeTo(frame.getFrame(), dialog);
    }

    public void showDialog() {
        dialog.performAction("show");
    }


    ////////////////////////////////////////////////////////////////////////////////
    private void alButtonEvent(ActionEvent e)
        ////////////////////////////////////////////////////////////////////////////////
    {
        JButton b = (JButton) e.getSource();
        ArrayList info = null;

        if (b.getText().equals("create")) {
            info = Common.showArrayListInput(ca, dialog, e.getActionCommand());
        } else {
            ArrayList al = (ArrayList) fields.get(((Integer) buttons.get(b)).intValue());
            info = Common.showArrayListInput(ca, dialog, al, e.getActionCommand());
        }

        if (((Boolean) info.get(0)).booleanValue()) {
            b.setText("modify");
            fields.set(((Integer) buttons.get(b)).intValue(), info.get(1));
        } else {
            //System.out.println("cancel");
        }
    }

    ////////////////////////////////////////////////////////////////////////////////
    private void okButtonEvent()
        ////////////////////////////////////////////////////////////////////////////////
    {
        ArrayList args = new ArrayList(params.length);

        // Fill in the array of user inputs and do some type checking
        for (int i = 0; i < fields.size(); i++) {
            Object arg = fields.get(i);

            if (arg instanceof JTextField) {
                args.add(i, ((JTextField) arg).getText());
            } else if (arg instanceof ArrayList) {
                args.add(i, arg);
            } else if (arg instanceof Matrix2D) {
                args.add(i, arg);
            } else {
                //System.out.println("not sure");
            }
        }

        // Confirm the input
        if (confirm) {
            ConfirmInputDialog confirm = new ConfirmInputDialog(ca, frame.getFrame(), params, args);
            if (!confirm.showDialog()) return;
        }

        //info.add(params);
        //info.add(args);
        info = args;
        status = true;

        //doChallenge here
        ArrayList total = new ArrayList(2);

        total.add(new Boolean(status));
        total.add(info);

        if(this.frame instanceof SourceViewer)
            {
                ((SourceViewer)this.frame).doChallengeRequest(total);
            } else if(this.frame instanceof CodingFrame) {
                ((CodingFrame)this.frame).doTest(total);
            }

        dialog.performAction("dispose");  // frees up the show() -- must be last
    }

    ////////////////////////////////////////////////////////////////////////////////
    private void cancelButtonEvent()
        ////////////////////////////////////////////////////////////////////////////////
    {
        status = false;

        ArrayList args = new ArrayList(params.length);

        // Fill in the array of user inputs and do some type checking
        for (int i = 0; i < fields.size(); i++) {
            Object arg = fields.get(i);

            if (arg instanceof JTextField) {
                args.add(i, ((JTextField) arg).getText());
            } else if (arg instanceof ArrayList) {
                args.add(i, arg);
            } else if (arg instanceof Matrix2D) {
                args.add(i, arg);
            } else {
                //System.out.println("not sure");
            }
        }

        info = args;

        //doChallenge here
        ArrayList total = new ArrayList(2);

        total.add(new Boolean(status));
        total.add(info);

        if(this.frame instanceof SourceViewer)
            {
                ((SourceViewer)this.frame).doChallengeRequest(total);
            } else if(this.frame instanceof CodingFrame) {
                ((CodingFrame)this.frame).doTest(total);
            }

        dialog.performAction("dispose");  // frees up the show() -- must be last
    }

    private void loadExample(int idx)
    {
        //
        // make sure there actually are examples
        //
        if(probComponent == null || idx < 0 || idx >= probComponent.getTestCases().length)
            return;

        //
        // load the specified example
        //
        String[] input = probComponent.getTestCases()[idx].getInput();

        for(int i = 0; i < input.length; i++)
            {
                //
                // check to see what the input is
                //
                if (params[i].getDescription().equals("ArrayList") ||
                    params[i].getDescription().startsWith("vector") ||
                    params[i].getDescription().endsWith("[]"))
                    {
                        //
                        // an arraylist datatype
                        //
                        buttonsByIndex[i].setText("modify");
                        fields.set(i, bracketParse(input[i]));
                    }
                else
                    {
                        //
                        // a string/integer datatype
                        //
                        JTextField jtf = (JTextField)fields.get(i);
                        if(params[i].getDescription().equalsIgnoreCase("string"))
                            {
                                //
                                // use the bracketParse method to parse out any escape
                                // characters and wrapping quotes.
                                //
                                ArrayList al = bracketParse(input[i]);
                                jtf.setText((String)al.get(0));
                            }
                        else if(params[i].getDescription().equals("char"))
                            {
                                //
                                // erase the single quotes around the character
                                //
                                jtf.setText(input[i].substring(1, input[i].length() - 1));
                            }
                        else
                            {
                                //
                                // integer
                                //
                                jtf.setText(input[i]);
                            }
                    }
            }
    }

    /**
     * Parse an array-type argument that is enclosed with braces.  Each
     * element of the array is separated by a comma.
     *
     * Note: this is copied from ArrayListInputdialog.java.
     *
     * @param text The input string to be parsed.
     *
     * @return An array list containing all the values parsed from the string.
     */
    private static final int START = 0;
    private static final int IN_QUOTE = 1;
    private static final int ESCAPE = 2;
    /**
     * parse the text.
     * @param text the string text
     * @return the parse result of array list.
     */
    public static ArrayList bracketParse(String text)
    {
        ArrayList result = new ArrayList();
        text = text.trim();
        //
        // modified 4/9/2003 by schveiguy
        //
        // fix bug where empty array causes exception
        //
    if(text.length() > 0 && text.charAt(0) == '{') text = text.substring(1);
        if (text.length() > 0 && text.charAt(text.length() - 1) == '}') text = text.substring(0, text.length() - 1);
    if(text.length() == 0)
        return result;
        int state = START;
        StringBuffer buf = new StringBuffer(50);
        for (int i = 0; i < text.length(); i++) {
            char ch = text.charAt(i);
            switch (state) {
            case ESCAPE:
                switch (ch) {
                case '\\':
                    buf.append('\\');
                    break;
                case '"':
                    buf.append('"');
                    break;
                default: //we'll just assume it was a mistake, problems really should not use tabs, line feeds, etc.
                    buf.append('\\');
                    buf.append(ch);
                }
                state = IN_QUOTE;
                break;
            case IN_QUOTE:
                switch (ch) {
                case '\\':
                    state = ESCAPE;
                    break;
                case '"':
                    String param = buf.toString();
                    buf.delete(0, buf.length());
                    state = START;
                    result.add(param);
                    break;
                default:
                    buf.append(ch);
                    break;
                }
                break;
            case START:
                if (Character.isWhitespace(ch)) {
                    if (buf.length() > 0) {
                        String param = buf.toString().trim();
                        buf.delete(0, buf.length());
                        result.add(param);
                    }
                    continue;
                }
                switch (ch) {
                case '"':
                    if (buf.length() > 0) {
                        buf.append('"');
                    } else {
                        state = IN_QUOTE;
                    }
                    break;
                case ',':
                    if (buf.length() > 0 || (i == 0) || (i > 0 && text.charAt(i - 1) == ',')) {
                        String param = buf.toString().trim();
                        buf.delete(0, buf.length());
                        result.add(param);
                    }
                    break;
                default:
                    buf.append(ch);
                }
            }
        }
        if (buf.length() > 0 || text.charAt(text.length() - 1) == ',') {
            String param = buf.toString().trim();
            buf.delete(0, buf.length());
            result.add(param);
        }
        //
        // return the array list containing the values
        //
        return result;
    }
}
