package com.topcoder.client.contestApplet.frames;

/*
 * ArgInputDialog.java
 *
 * Created on July 10, 2000, 4:08 PM
 */

import java.util.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
//import javax.swing.border.*;
//import javax.swing.table.*;
//import javax.swing.event.*;
import com.topcoder.client.contestApplet.common.*;
//import com.topcoder.client.contestApplet.*;
//import com.topcoder.client.contestApplet.listener.*;
import com.topcoder.client.contestApplet.widgets.*;
import com.topcoder.netCommon.contest.Matrix2D;
import com.topcoder.shared.problem.*;
import com.topcoder.client.contestant.*;
import com.topcoder.shared.language.JavaLanguage;
/**
 *
 * @author Alex Roman
 * @version
 */

public final class ArgInputDialog extends JDialog {

    private ArrayList originalArgs = null;
    private ArrayList fields = null;
    private ArrayList info = null;
    private DataType[] params = null;
    private Hashtable buttons = null;
    private boolean status = false;
    private boolean confirm = false;
    private JFrame frame;

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

    /**
     * Class constructor
     */
    ////////////////////////////////////////////////////////////////////////////////
    public ArgInputDialog(JFrame frame, DataType[] params, boolean confirm)
            ////////////////////////////////////////////////////////////////////////////////
    {
        this(frame, params, null, confirm);
    }

    ////////////////////////////////////////////////////////////////////////////////
    public ArgInputDialog(JFrame frame, String msg, DataType[] params, boolean confirm)
            ////////////////////////////////////////////////////////////////////////////////
    {
        this(frame, params, msg, null, confirm, null);
    }

    ////////////////////////////////////////////////////////////////////////////////
    public ArgInputDialog(JFrame frame, String msg, DataType[] params, boolean confirm, String[] parameterNames)
            ////////////////////////////////////////////////////////////////////////////////
    {
        this(frame, params, msg, null, confirm, null);
    }

    ////////////////////////////////////////////////////////////////////////////////
    public ArgInputDialog(JFrame frame, DataType[] params, ArrayList args, boolean confirm)
            ////////////////////////////////////////////////////////////////////////////////
    {
        this(frame, params, null, args, confirm, null);
    }
    
    public ArgInputDialog(JFrame frame, DataType[] params, String msg, ArrayList args, boolean confirm, ProblemComponentModel probComponent) {
        this(frame, params, msg, args, confirm, probComponent, JavaLanguage.ID);
    }

    ////////////////////////////////////////////////////////////////////////////////
    //
    // Modified 3/13/2003 by schveiguy -- this constructor used to be private,
    // but it is now public to allow creation of a message AND have current
    // arguments.  All other constructors call this one anyways.  Also added
    // the ProblemComponentModel argument to allow example testing
    //
    public ArgInputDialog(JFrame frame, DataType[] params, String msg, ArrayList args, boolean confirm, ProblemComponentModel probComponent, int language)
            ////////////////////////////////////////////////////////////////////////////////
    {
        super(frame, "Problem Arguments", false);

        this.confirm = confirm;
        this.frame = frame;
        this.params = params;
        this.fields = new ArrayList(params.length);
        this.info = new ArrayList(2);
        this.buttons = new Hashtable();
        this.originalArgs = args;
        this.probComponent = probComponent;


        String[] parameterNames = probComponent.getParamNames();

        MouseLessTextArea mesg = null;
        JPanel mp = null;
        int msgSize = 0;

        if (msg != null) {
            msgSize = 100;
            mesg = new MouseLessTextArea(msg);
            mp = Common.createMessagePanel("Instructions", mesg, 0, msgSize, Common.BG_COLOR);
        }

        JPanel p1 = new JPanel();
        p1.setBackground(Common.WPB_COLOR);
        p1.setLayout(new GridLayout(params.length, 2));

        Font fixed = new Font("Courier", Font.PLAIN, 10);

        // just in case something goes wrong with the cache.
        if ((args != null) && (args.size() != params.length)) {
            args = null;
        }

        buttonsByIndex = new JButton[params.length];
        for (int i = 0; i < params.length; i++) {
            JLabel label = new JLabel("(" + (i + 1) + ") " + params[i].getDescriptor(language) + " "+parameterNames[i]);

            label.setForeground(Color.white);
            label.setBackground(Common.WPB_COLOR);
            label.setFont(fixed);
            p1.add(label);

            if (params[i].getDescription().equals("ArrayList") ||
                    params[i].getDescription().startsWith("vector") ||
                    params[i].getDescription().endsWith("[]")) {
                JButton b = new JButton(args == null ? "create" : "modify");
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
                p1.add(b);
                /*
            } else if ( ((String) params.get(i)).equals("Matrix2D") ) {
              JButton b = new JButton(args == null ? "create" : "modify");
              //b.addActionListener(new al("actionPerformed", "mxButtonEvent", this));
              b.addActionListener(new ActionListener(){
                  public void actionPerformed(ActionEvent e) {
                      mxButtonEvent(e);
                  }
              });
              buttons.put(b, new Integer(i));
              fields.add(args == null ? new Matrix2D() : (Matrix2D)args.get(i));
              p1.add(b);
              */
            } else {
                JTextField tf = new JTextField(args == null ? "" : (String) args.get(i));
                fields.add(tf);
                p1.add(tf);
            }
        }

        JPanel p2 = new JPanel(new FlowLayout());
        p2.setBackground(Common.WPB_COLOR);
        JButton okButton = new JButton("OK");
        //okButton.addActionListener(new al("actionPerformed", "okButtonEvent", this));
        okButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                okButtonEvent();
            }
        });

        p2.add(okButton);
        okButton.setDefaultCapable(true);
        getRootPane().setDefaultButton(okButton);
        JButton cancelButton = new JButton("Cancel");
        //cancelButton.addActionListener(new al("actionPerformed", "cancelButtonEvent", this));
        cancelButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                cancelButtonEvent();
            }
        });
        p2.add(cancelButton);

        //
        // Added 3/11/2003 by schveiguy to support easy example testing.
        //
        JComboBox exampleList = null;
        if(probComponent != null && probComponent.hasTestCases())
        {
            TestCase[] examples = probComponent.getTestCases();
            String[] exampleNames = new String[examples.length + 1];
            exampleNames[0] = "Select Example...";
            for(int x = 1; x < exampleNames.length; x++)
                exampleNames[x] = "Example " + (x - 1);
            exampleList = new JComboBox(exampleNames);
            exampleList.setBackground(Common.WPB_COLOR);
            exampleList.setForeground(Color.white);

            //
            // see if any of the examples are the current arguments
            //
            if(args != null)
            {
example_outer:
                for(int i = 0; i < examples.length; i++)
                {
                    String[] curex = examples[i].getInput();
                    for(int j = 0; j < params.length; j++)
                    {
                        if (params[j].getDescription().equals("ArrayList") ||
                                params[j].getDescription().startsWith("vector") ||
                                params[j].getDescription().endsWith("[]"))
                        {
                            ArrayList a = (ArrayList)args.get(j);
                            if(!a.equals(bracketParse(curex[j])))
                                continue example_outer;
                        }
                        else
                        {
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
                    exampleList.setSelectedIndex(i + 1);
                    break;
                }
            }
            exampleList.addActionListener(new ActionListener() {
                    public void actionPerformed(ActionEvent e)
                    {
                    //
                    // load the example data from the example test case
                    //
                    JComboBox cb = (JComboBox)e.getSource();
                    loadExample(cb.getSelectedIndex() - 1);
                    }
                    });
        }



        int i = 0;
        GridBagConstraints gbc = Common.getDefaultConstraints();
        getContentPane().setLayout(new GridBagLayout());

        if (msg != null) {
            gbc.insets = new Insets(5, 15, 0, 15);
            gbc.fill = GridBagConstraints.BOTH;
            Common.insertInPanel(mp, getContentPane(), gbc, 0, i++, 1, 1, 0.1, 1.0);
        }

        gbc.insets = new Insets(15, 15, 15, 15);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        Common.insertInPanel(p1, getContentPane(), gbc, 0, i++, 1, 1, 0.1, 0.1);
        gbc.fill = GridBagConstraints.NONE;

        //
        // Added 3/11/2003 by schveiguy -- insert the list panel into the
        // global panel
        //
        if(exampleList != null)
            Common.insertInPanel(exampleList, getContentPane(), gbc, 0, i++, 1, 1, 0.1, 0.1);

        Common.insertInPanel(p2, getContentPane(), gbc, 0, i++, 1, 1, 0.1, 0.1);

        getRootPane().setPreferredSize(new Dimension(350 - 10, (msgSize + 60 + 65 + 33 * params.length + (probComponent != null ? 60 : 0)) - 50));
        getRootPane().setMinimumSize(new Dimension(350 - 10, (msgSize + 60 + 65 + 33 * params.length + (probComponent != null ? 60 : 0)) - 50));

        pack();

        setResizable(false);

        Common.setLocationRelativeTo(frame, this);
    }

    ////////////////////////////////////////////////////////////////////////////////
    public void showDialog()
            ////////////////////////////////////////////////////////////////////////////////
    {
        show();

        
    }

    /*
  ////////////////////////////////////////////////////////////////////////////////
  private void mxButtonEvent(ActionEvent e)
  ////////////////////////////////////////////////////////////////////////////////
  {
    JButton b = (JButton) e.getSource();
    ArrayList info = null;

    if ( b.getText().equals("create") ) {
      info = Common.showMatrixInput(this);
    } else {
      ArrayList al = ((Matrix2D)fields.get(((Integer)buttons.get(b)).intValue())).toArrayList();
      info = Common.showMatrixInput(this, al);
    }

    if ( ((Boolean)info.get(0)).booleanValue() ) {
       b.setText("modify");
       fields.set(((Integer)buttons.get(b)).intValue(), (Matrix2D)info.get(1));
     } else {
       //System.out.println("cancel");
     }
  }
  */

    ////////////////////////////////////////////////////////////////////////////////
    private void alButtonEvent(ActionEvent e)
            ////////////////////////////////////////////////////////////////////////////////
    {
        JButton b = (JButton) e.getSource();
        ArrayList info = null;

        if (b.getText().equals("create")) {
            //info = Common.showArrayListInput(this, e.getActionCommand());
        } else {
            ArrayList al = (ArrayList) fields.get(((Integer) buttons.get(b)).intValue());
            //info = Common.showArrayListInput(this, al, e.getActionCommand());
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
            ConfirmInputDialog confirm = new ConfirmInputDialog(frame, params, args);
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
        
        dispose();  // frees up the show() -- must be last
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
                if (((JTextField) arg).getText().length() > 50) {
                    Common.showMessage("Error", "You have entered " + ((JTextField) arg).getText().length() +
                            " characters. Please limit your argument size to 50 characters.", this);
                    MoveFocus.moveFocus((JTextField) arg);
                    return;
                }
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
        
        dispose();  // frees up the show() -- must be last
    }

    /*
  public static final void main(String[] a) {
  	JFrame f = new JFrame();
  	f.pack();
  	f.show();
  	f.setLocation(100,100);

  	ArrayList l = new ArrayList();
  	l.add("ArrayList");
  	l.add("String[]");
  	l.add("int");
  	ArgInputDialog d = new ArgInputDialog(f, l, false);
  	System.out.println(d.showDialog());
  }
  */

    /**
     * Load an example case into the argument fields.  Called from the
     * JComboBox ActionEvent handler.
     * 
     * Added 3/11/2003
     *
     * @author Steven Schveighoffer (schveiguy)
     * @param idx Which example to load
     */
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
    private final int START = 0;
    private final int IN_QUOTE = 1;
    private final int ESCAPE = 2;
    private ArrayList bracketParse(String text)
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
                    if (param.length() > 50) {
                        continue;
                    }
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
                        if (param.length() > 50) {
                            continue;
                        }
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
                        if (param.length() > 50) {
                            continue;
                        }
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
            if (param.length() > 50) {
            } else {
                result.add(param);
            }
        }
        //
        // return the array list containing the values
        //
        return result;
    }

}
