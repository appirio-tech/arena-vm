/*
* Copyright (C) - 2022 TopCoder Inc., All Rights Reserved.
*/

package com.topcoder.client.contestApplet.common;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Point;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Observable;
import java.util.Observer;
import java.util.Vector;

import javax.swing.JOptionPane;

import com.topcoder.client.contestApplet.editors.EditorPlugin;
import com.topcoder.client.ui.UIFactory;
import com.topcoder.client.ui.UIManager;
import com.topcoder.netCommon.contest.ContestConstants;
import com.topcoder.shared.language.CPPLanguage;
import com.topcoder.shared.language.CSharpLanguage;
import com.topcoder.shared.language.JavaLanguage;
import com.topcoder.shared.language.PythonLanguage;
import com.topcoder.shared.language.Python3Language;
import com.topcoder.shared.language.VBLanguage;
import com.topcoder.util.config.ConfigManager;
import com.topcoder.util.config.ConfigManagerException;
import com.topcoder.util.config.Property;
import com.topcoder.util.config.UnknownNamespaceException;

/**
 * LocalPreferences.java
 * Description: Class that reads/writes preferences to a local file.
 *
 * <p>
 * Changes in version 1.1 (Module Assembly - TopCoder Competition Engine - Batch Test):
 * <ol>
 *      <li>Added {@link #KEYBATCHTEST} constant to support batch testing.</li>
 *      <li>Updated {@link #getHotKey(String)} method to handle batch testing hot key.</li>
 * </ol>
 * </p>
 *
 * <p>
 * Changes in version 1.2 (Python3 support):
 * <ol>
 *      <li>Added {@link #SUMMARYPYTHON3POINTS}, {@link #SUMMARYPYTHON3CHLPASSED}, {@link #SUMMARYPYTHON3CHLFAILED},
 *      {@link #SUMMARYPYTHON3SYSPASSED}, {@link #SUMMARYPYTHON3SYSFAILED} fields.</li>
 *      <li>Updated {@link #getColor(String, Color)}, {@link #isTrue(String)}, {@link #resolveStatusPropertyFormat(int, int)} methods.</li>
 * </ol>
 * </p>
 *
 * @author Tim "Pops" Roberts (troberts@bigfoot.com), dexy, liuliquan
 * @version 1.2
 */
public final class LocalPreferences {

    private static LocalPreferences localPref = null;
    private ConfigManager configManager = null;
    //private Properties properties = new Properties();
    private PrefObservable saveObserver = new PrefObservable();
    private UIManager[] managers = null;

    private static final String PLUGINNUMBER = "editor.numplugins";
    private static final String PLUGINDEFAULTNAME = "editor.defaultname";
    private static final String PLUGINCACHED = "editor.cache";
    private static final String PLUGINDEBUG = "editor.debug";
    private static final String PLUGINNAME = "name";
    private static final String PLUGINENTRYPOINT = "entrypoint";
    private static final String PLUGINCLASSPATH = "classpath";
    private static final String PLUGINEAGER = "eager";
    private static final String PLUGINCOMMONPATH = "com.topcoder.client.contestApplet.common.LocalPreferences.commonclasspath";


    // emcee - 4/8/2002 - added boolean for disabling the broadcast popup
    public static final String DISABLEBROADCASTPOPUP = "com.topcoder.jmaContestApplet.ContestApplet.disableBroadcastPopup";
    public static final String DISABLEBROADCASTBEEP = "com.topcoder.jmaContestApplet.ContestApplet.disableBroadcastBeep";
    public static final String ENABLETIMESTAMPS = "com.topcoder.jmaContestApplet.ContestApplet.enableTimestamps";
    public static final String IGNORELIST = "com.topcoder.jmaContestApplet.ContestApplet.ignoreList";
    public static final String IGNORENUMBER = "com.topcoder.jmaContestApplet.ContestApplet.ignoreNumber";
    public static final String ALLOWEDLIST = "com.topcoder.jmaContestApplet.ContestApplet.allowedList";

    public static final String LEADER_TICKER_DISABLED = "com.topcoder.client.contestApplet.panels.main.FaderPanel.enabled";

    public static final String CHAT_SCROLLING = "com.topcoder.jmaContestApplet.ContestApplet.disableChatScrolling";


    // AdamSelene - 5/02/2002 - Universal local preferences: unified ChatPreferences w/ Local
    // I've left the old names from ChatPreferences (although new names are now there)
    // as changing the key strings will lose current chat preferences for everyone.
    // not a major issue, but this'll hopefully save complaints!
    public final static String CHATSYSTEMFORE = "com.topcoder.client.contestApplet.panels.ChatPanel.systemfore";
    public final static String CHATSYSTEMBACK = "com.topcoder.client.contestApplet.panels.ChatPanel.systemback";
    public final static String CHATEMPHSYSTEMFORE = "com.topcoder.client.contestApplet.panels.ChatPanel.emphsystemfore";
    public final static String CHATEMPHSYSTEMBACK = "com.topcoder.client.contestApplet.panels.ChatPanel.emphsystemback";
    public final static String CHATGENERALFORE = "com.topcoder.client.contestApplet.panels.ChatPanel.generalfore";
    public final static String CHATGENERALBACK = "com.topcoder.client.contestApplet.panels.ChatPanel.generalback";
    public final static String CHATGENERALTOFORE = "com.topcoder.client.contestApplet.panels.ChatPanel.generaltofore";
    public final static String CHATGENERALTOBACK = "com.topcoder.client.contestApplet.panels.ChatPanel.generaltoback";
    public final static String CHATMEFORE = "com.topcoder.client.contestApplet.panels.ChatPanel.mefore";
    public final static String CHATMEBACK = "com.topcoder.client.contestApplet.panels.ChatPanel.meback";
    public final static String CHATWHISPERFORE = "com.topcoder.client.contestApplet.panels.ChatPanel.whisperfore";
    public final static String CHATWHISPERBACK = "com.topcoder.client.contestApplet.panels.ChatPanel.whisperback";
    public final static String CHATWHISPERTOFORE = "com.topcoder.client.contestApplet.panels.ChatPanel.whispertofore";
    public final static String CHATWHISPERTOBACK = "com.topcoder.client.contestApplet.panels.ChatPanel.whispertoback";
    public final static String CHATHANDLEBACK = "com.topcoder.client.contestApplet.panels.ChatPanel.handleback";
    // New font/color preferences keys start here
    public final static String CHATPANELBACK = "com.topcoder.client.contestApplet.common.LocalPreferences.chatpanelback";
    public final static String CHATFONT = "com.topcoder.client.contestApplet.common.LocalPreferences.chatfont";
    public final static String CHATFONTSIZE = "com.topcoder.client.contestApplet.common.LocalPreferences.chatfontsize";

    public final static String CHATFINDHIGHLIGHT = "com.topcoder.client.contestApplet.common.LocalPreferences.chatfindhighlight";
    public final static String CHATFINDBACK = "com.topcoder.client.contestApplet.common.LocalPreferences.chatfindback";

    public final static String EDSTDFONT = "com.topcoder.client.contestApplet.common.LocalPreferences.edstdfont";
    public final static String EDSTDFONTSIZE = "com.topcoder.client.contestApplet.common.LocalPreferences.edstdfontsize";
    public final static String EDSTDFORE = "com.topcoder.client.contestApplet.common.LocalPreferences.edstdfore";
    public final static String EDSTDBACK = "com.topcoder.client.contestApplet.common.LocalPreferences.edstdback";
    public final static String EDSTDSELT = "com.topcoder.client.contestApplet.common.LocalPreferences.edstdselt";
    public final static String EDSTDSELB = "com.topcoder.client.contestApplet.common.LocalPreferences.edstdselb";
    public final static String EDSTDINDENT = "com.topcoder.client.contestApplet.common.LocalPreferences.edstdindent";
    public final static String EDSTDSYNTAXHIGHLIGHT = "com.topcoder.client.contestApplet.common.LocalPreferences.edstdsyntaxhighlight";
    public final static String EDSTDTABSIZE = "com.topcoder.client.contestApplet.common.LocalPreferences.edstdtabsize";

    public final static String EDSTDSYNTAXCOMMENTS = "com.topcoder.client.contestApplet.common.LocalPreferences.edstdsyntaxcomments";
    public final static String EDSTDSYNTAXLITERALS = "com.topcoder.client.contestApplet.common.LocalPreferences.edstdsyntaxliterals";
    public final static String EDSTDSYNTAXKEYWORDS = "com.topcoder.client.contestApplet.common.LocalPreferences.edstdsyntaxkeywords";
    public final static String EDSTDSYNTAXDEFAULT = "com.topcoder.client.contestApplet.common.LocalPreferences.edstdsyntaxdefault";
    public final static String EDSTDSYNTAXCOMMENTSSTYLE = "com.topcoder.client.contestApplet.common.LocalPreferences.edstdsyntaxcommentsstyle";
    public final static String EDSTDSYNTAXLITERALSSTYLE = "com.topcoder.client.contestApplet.common.LocalPreferences.edstdsyntaxliteralsstyle";
    public final static String EDSTDSYNTAXKEYWORDSSTYLE = "com.topcoder.client.contestApplet.common.LocalPreferences.edstdsyntaxkeywordsstyle";
    public final static String EDSTDSYNTAXDEFAULTSTYLE = "com.topcoder.client.contestApplet.common.LocalPreferences.edstdsyntaxdefaultstyle";

    public final static String EDSTDKEYFIND = "com.topcoder.client.contestApplet.common.LocalPreferences.edstdkeyfind";
    public final static String EDSTDKEYGOTO = "com.topcoder.client.contestApplet.common.LocalPreferences.edstdkeygoto";
    public final static String EDSTDKEYUNDO = "com.topcoder.client.contestApplet.common.LocalPreferences.edstdkeyundo";
    public final static String EDSTDKEYREDO = "com.topcoder.client.contestApplet.common.LocalPreferences.edstdkeyredo";

    public final static String KEYSAVE = "com.topcoder.client.contestApplet.common.LocalPreferences.keysave";
    public final static String KEYCOMPILE = "com.topcoder.client.contestApplet.common.LocalPreferences.keycompile";
    public final static String KEYTEST = "com.topcoder.client.contestApplet.common.LocalPreferences.keytest";
    /**
     * Constant to support batch testing.
     * @since 1.1
     */
    public final static String KEYBATCHTEST = "com.topcoder.client.contestApplet.common.LocalPreferences.keybatchtest";
    public final static String KEYSUBMIT = "com.topcoder.client.contestApplet.common.LocalPreferences.keysubmit";

    public final static String PROBLEMFONT = "com.topcoder.client.contestApplet.common.LocalPreferences.problemfont";
    public final static String PROBLEMFONTSIZE = "com.topcoder.client.contestApplet.common.LocalPreferences.problemfontsize";
    public final static String PROBLEMFORE = "com.topcoder.client.contestApplet.common.LocalPreferences.problemfore";
    public final static String PROBLEMBACK = "com.topcoder.client.contestApplet.common.LocalPreferences.problemback";
    public final static String PROBLEMFIXEDFONT = "com.topcoder.client.contestApplet.common.LocalPreferences.problemfixedfont";
    public final static String PROBLEMFIXEDFONTSIZE = "com.topcoder.client.contestApplet.common.LocalPreferences.problemfixedfontsize";
    public final static String PROBLEMFIXEDFORE = "com.topcoder.client.contestApplet.common.LocalPreferences.problemfixedfore";
    public final static String PROBLEMFIXEDBACK = "com.topcoder.client.contestApplet.common.LocalPreferences.problemfixedback";

    public final static String MESSAGEFONT = "com.topcoder.client.contestApplet.common.LocalPreferences.messagefont";
    public final static String MESSAGEFONTSIZE = "com.topcoder.client.contestApplet.common.LocalPreferences.messagefontsize";
    public final static String MESSAGEFORE = "com.topcoder.client.contestApplet.common.LocalPreferences.messagefore";
    public final static String MESSAGEBACK = "com.topcoder.client.contestApplet.common.LocalPreferences.messageback";

    public final static String CHALSRCFONT = "com.topcoder.client.contestApplet.common.LocalPreferences.chalsrcfont";
    public final static String CHALSRCFONTSIZE = "com.topcoder.client.contestApplet.common.LocalPreferences.chalsrcfontsize";
    public final static String CHALSRCFORE = "com.topcoder.client.contestApplet.common.LocalPreferences.chalsrcfore";
    public final static String CHALSRCBACK = "com.topcoder.client.contestApplet.common.LocalPreferences.chalsrcback";
    public final static String CHALSRCSYNTAXHIGHLIGHT = "com.topcoder.client.contestApplet.common.LocalPreferences.chalsrcsyntaxhighlight";

    public final static String CHALPROBFONT = "com.topcoder.client.contestApplet.common.LocalPreferences.chalprobfont";
    public final static String CHALPROBFONTSIZE = "com.topcoder.client.contestApplet.common.LocalPreferences.chalprobontsize";
    public final static String CHALPROBFIXEDFONT = "com.topcoder.client.contestApplet.common.LocalPreferences.chalprobfixedfont";
    public final static String CHALPROBFIXEDFONTSIZE = "com.topcoder.client.contestApplet.common.LocalPreferences.chalprobfixedfontsize";
    public final static String CHALPROBFORE = "com.topcoder.client.contestApplet.common.LocalPreferences.chalprobfore";
    public final static String CHALPROBBACK = "com.topcoder.client.contestApplet.common.LocalPreferences.chalprobback";

    public final static String MENUFONT = "com.topcoder.client.contestApplet.common.LocalPreferences.menufont";
    public final static String MENUFONTSIZE = "com.topcoder.client.contestApplet.common.LocalPreferences.menufontsize";

    public final static String USERTABLEFONT = "com.topcoder.client.contestApplet.common.LocalPreferences.usertablefont";
    public final static String USERTABLEFONTSIZE = "com.topcoder.client.contestApplet.common.LocalPreferences.usertablefontsize";

    public final static String SUMMARYFONT = "com.topcoder.client.contestApplet.common.LocalPreferences.summaryfont";
    public final static String SUMMARYFONTSIZE = "com.topcoder.client.contestApplet.common.LocalPreferences.summaryfontsize";

    // AdamSelene - merge - 05/24/2002 - Moved these in from ChatPreferences (obsolete)
    //				also modified GetColor below.
    /* Da Twink Daddy - 05/12/2002 - New Members */
    /**
     * Properties key for moderated chat question foreground color
     */
    public static final String MODERATED_CHAT_QUESTION_FOREGROUND = "com.topcoder.client.contentApplet.panels.ChatPanel.moderatedChatQuestionFore";
    /** Properties key for moderated chat question background color */
    public static final String MODERATED_CHAT_QUESTION_BACKGROUND = "com.topcoder.client.contentApplet.panels.ChatPanel.moderatedChatQuestionBack";
    /** Properties key for moderated chat speaker chat foreground color */
    public static final String MODERATED_CHAT_SPEAKER_FOREGROUND = "com.topcoder.client.contentApplet.panels.ChatPanel.moderatedChatSpeakerFore";
    /** Properties key for moderated chat speaker chat background color */
    public static final String MODERATED_CHAT_SPEAKER_BACKGROUND = "com.topcoder.client.contentApplet.panels.ChatPanel.moderatedChatSpeakerBack";

    public final static String SUMMARYUNOPENED = "com.topcoder.client.contestApplet.common.LocalPreferences.summaryunopened";
    public final static String SUMMARYOPENED = "com.topcoder.client.contestApplet.common.LocalPreferences.summaryopened";
    public final static String SUMMARYCOMPILED = "com.topcoder.client.contestApplet.common.LocalPreferences.summarycompiled";
    public final static String SUMMARYJAVAPOINTS = "com.topcoder.client.contestApplet.common.LocalPreferences.summaryjavapoints";
    public final static String SUMMARYJAVACHLPASSED = "com.topcoder.client.contestApplet.common.LocalPreferences.summaryjavachlpassed";
    public final static String SUMMARYJAVACHLFAILED = "com.topcoder.client.contestApplet.common.LocalPreferences.summaryjavachlfailed";
    public final static String SUMMARYJAVASYSPASSED = "com.topcoder.client.contestApplet.common.LocalPreferences.summaryjavasyspassed";
    public final static String SUMMARYJAVASYSFAILED = "com.topcoder.client.contestApplet.common.LocalPreferences.summaryjavasysfailed";
    public final static String SUMMARYCPPPOINTS = "com.topcoder.client.contestApplet.common.LocalPreferences.summarycpppoints";
    public final static String SUMMARYCPPCHLPASSED = "com.topcoder.client.contestApplet.common.LocalPreferences.summarycppchlpassed";
    public final static String SUMMARYCPPCHLFAILED = "com.topcoder.client.contestApplet.common.LocalPreferences.summarycppchlfailed";
    public final static String SUMMARYCPPSYSPASSED = "com.topcoder.client.contestApplet.common.LocalPreferences.summarycppsyspassed";
    public final static String SUMMARYCPPSYSFAILED = "com.topcoder.client.contestApplet.common.LocalPreferences.summarycppsysfailed";

    public final static String SUMMARYCSHARPPOINTS = "com.topcoder.client.contestApplet.common.LocalPreferences.summarycsharppoints";
    public final static String SUMMARYCSHARPCHLPASSED = "com.topcoder.client.contestApplet.common.LocalPreferences.summarycsharpchlpassed";
    public final static String SUMMARYCSHARPCHLFAILED = "com.topcoder.client.contestApplet.common.LocalPreferences.summarycsharpchlfailed";
    public final static String SUMMARYCSHARPSYSPASSED = "com.topcoder.client.contestApplet.common.LocalPreferences.summarycsharpsyspassed";
    public final static String SUMMARYCSHARPSYSFAILED = "com.topcoder.client.contestApplet.common.LocalPreferences.summarycsharpsysfailed";

    public final static String SUMMARYVBPOINTS = "com.topcoder.client.contestApplet.common.LocalPreferences.summaryvbpoints";
    public final static String SUMMARYVBCHLPASSED = "com.topcoder.client.contestApplet.common.LocalPreferences.summaryvbchlpassed";
    public final static String SUMMARYVBCHLFAILED = "com.topcoder.client.contestApplet.common.LocalPreferences.summaryvbchlfailed";
    public final static String SUMMARYVBSYSPASSED = "com.topcoder.client.contestApplet.common.LocalPreferences.summaryvbsyspassed";
    public final static String SUMMARYVBSYSFAILED = "com.topcoder.client.contestApplet.common.LocalPreferences.summaryvbsysfailed";

    public final static String SUMMARYPYTHONPOINTS = "com.topcoder.client.contestApplet.common.LocalPreferences.summarypythonpoints";
    public final static String SUMMARYPYTHONCHLPASSED = "com.topcoder.client.contestApplet.common.LocalPreferences.summarypythonchlpassed";
    public final static String SUMMARYPYTHONCHLFAILED = "com.topcoder.client.contestApplet.common.LocalPreferences.summarypythonchlfailed";
    public final static String SUMMARYPYTHONSYSPASSED = "com.topcoder.client.contestApplet.common.LocalPreferences.summarypythonsyspassed";
    public final static String SUMMARYPYTHONSYSFAILED = "com.topcoder.client.contestApplet.common.LocalPreferences.summarypythonsysfailed";

    public final static String SUMMARYPYTHON3POINTS = "com.topcoder.client.contestApplet.common.LocalPreferences.summarypython3points";
    public final static String SUMMARYPYTHON3CHLPASSED = "com.topcoder.client.contestApplet.common.LocalPreferences.summarypython3chlpassed";
    public final static String SUMMARYPYTHON3CHLFAILED = "com.topcoder.client.contestApplet.common.LocalPreferences.summarypython3chlfailed";
    public final static String SUMMARYPYTHON3SYSPASSED = "com.topcoder.client.contestApplet.common.LocalPreferences.summarypython3syspassed";
    public final static String SUMMARYPYTHON3SYSFAILED = "com.topcoder.client.contestApplet.common.LocalPreferences.summarypython3sysfailed";

    public final static String UNUSEDCODECHECK = "com.topcoder.client.contestApplet.common.LocalPreferences.UnusedCodeCheckEnabled";

    public final static String ATTRIBUTECOLOR = "color";
    public final static String ATTRIBUTEBOLD = "bold";
    public final static String ATTRIBUTEITALIC = "italic";

    public final static String BROWSERLOCATION = "com.topcoder.client.contestApplet.common.LocalPreferences.browserlocation";

	public final static String FILELOCATION = "com.topcoder.client.contestApplet.common.LocalPreferences.filelocation";
	public final static String FILENAME = "com.topcoder.client.contestApplet.common.LocalPreferences.filename";

    public final static String CONFIG_NAMESPACE = "contestapplet.conf";
    public final static String CONFIG_USER = "user";

    public static final String CONNECTION_TYPE = "com.topcoder.client.contestApplet.LocalPreferences.connectionType";
    public static final String CONNECTION_USERNAME = "com.topcoder.client.contestApplet.LocalPreferences.connection.username";
    public static final String CONNECTION_PASSWORD = "com.topcoder.client.contestApplet.LocalPreferences.connection.password";
    public static final String CONNECTION_PROXY_TYPE = "com.topcoder.client.contestApplet.LocalPreferences.connection.type";
    public static final String CONNECTION_HOST = "com.topcoder.client.contestApplet.LocalPreferences.connection.host";
    public static final String CONNECTION_PORT = "com.topcoder.client.contestApplet.LocalPreferences.connection.port";
    public static final String CONNECTION_SSL = "com.topcoder.client.contestApplet.LocalPreferences.connection.ssl";

    public static final String UI_THEME = "ui.theme";
    private static final String UI_THEME_DIRECTORY = "arenaui";

    private static final String[] UI_THEME_EMBEDDED = { "/ui/default.xml" };

    private static void copyFile(File org, File dst) throws IOException {
        InputStream is = null;
        OutputStream os = null;
        try {
            is = new BufferedInputStream(new FileInputStream(org));
            os = new BufferedOutputStream(new FileOutputStream(dst));
            byte[] buffer = new byte[1024];
            int bytes;
            while ((bytes = is.read(buffer)) != -1) {
                os.write(buffer, 0, bytes);
            }
        } finally {
            if (is != null) {
                try {
                    is.close();
                } catch (IOException e) {
                }
            }
            if (os != null) {
                try {
                    os.close();
                } catch (IOException e) {
                }
            }
        }
    }

	private LocalPreferences()
	{
        try {
            reload();

            try {
                File backupFile = getPreferencesBackupFile();
                File localFile = getPreferencesFile();

                copyFile(localFile, backupFile);
            } catch (IOException ee) {
                // ignore the backup IO exceptions
            }
        } catch (IOException e) {
            // Error occurred, try to restore
            File backupFile = null;
            File localFile = null;

            try {
                backupFile = getPreferencesBackupFile();
                localFile = getPreferencesFile();

                if (backupFile.isFile() && JOptionPane.showConfirmDialog(null, "The preference file is corrupted. Do you want to restore the backup file?",
                                                                     "Confirm", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
                    copyFile(backupFile, localFile);
                    reload();
                }
            } catch (IOException ee) {
                // restore or reload fails, clean up the backup file and the local file
                if (localFile != null) {
                    localFile.delete();
                }
                if (backupFile != null) {
                    backupFile.delete();
                }
            }
            printError(e.toString());
        }
    }

    public final void reload() throws IOException {
        // Clear properties
        //properties.clear();

        // Get the preferences file
        File localFile = getPreferencesFile();

        configManager = ConfigManager.getInstance();
        if (!localFile.exists()) {  // create a new file
            OutputStream out = new BufferedOutputStream(new FileOutputStream(localFile));
            PrintWriter writer = new PrintWriter(new OutputStreamWriter(out, "ISO-8859-1"));
            writer.println("#TopCoder ContestApplet Preferences File");
            writer.println("#" + Calendar.getInstance().getTime());
            // menu properties hack - remove when fixed
            writer.println("com.topcoder.client.contestApplet.common.LocalPreferences.menufont=Arial");
            writer.println("com.topcoder.client.contestApplet.common.LocalPreferences.menufontsize=10");
            writer.flush();
        }
        configManager.add(CONFIG_NAMESPACE, localFile.getPath(), ConfigManager.CONFIG_PROPERTIES_FORMAT);

        // If it exists, reload it
        //if (localFile.exists()) properties.load(new BufferedInputStream(new FileInputStream(localFile)));
    }

    private Vector propertiesList;
    private final void setNestedProperties(String key) {
        try {
            Property p = configManager.getPropertyObject(CONFIG_NAMESPACE, key);
            if (p.getValue() != null) {
                //System.out.println(key+"="+p.getValue());
                propertiesList.add(key);
            }
            Enumeration e = p.propertyNames();
            while (e.hasMoreElements()) {
                String suffix = (String)e.nextElement();
                String newKey = key+"."+suffix;
                if(key.equals("")) {
                    newKey = suffix;
                }
                setNestedProperties(newKey);
            }
        } catch (UnknownNamespaceException une) {
            une.printStackTrace();
        }
    }
    private final Vector getNestedProperties(String key) {
        propertiesList = new Vector();
        setNestedProperties(key);
        return propertiesList;
    }

    public final static synchronized LocalPreferences getInstance() {
        // Has the instance already been created - if so, return it
        if (localPref != null) return localPref;

        // Create an instance and then return it
        localPref = new LocalPreferences();
        return localPref;
    }

    public final ConfigManager getConfigManager() {
        return configManager;
    }

    public final void addSaveObserver(Observer observer) {
        // Adds a save observer
        saveObserver.addObserver(observer);
    }

    public final void removeSaveObserver(Observer observer) {
        // Deletes a save observer
        saveObserver.deleteObserver(observer);
    }


    public final void savePreferences() throws IOException {
        // Get the preferences file
        File localFile = getPreferencesFile();

        // Store it
        OutputStream out = new BufferedOutputStream(new FileOutputStream(localFile));
        PrintWriter writer = new PrintWriter(new OutputStreamWriter(out, "ISO-8859-1"));
        writer.println("#TopCoder ContestApplet Preferences File");
        writer.println("#" + Calendar.getInstance().getTime());
        Enumeration e = getNestedProperties("").elements();
        StringBuffer s = new StringBuffer();
        while (e.hasMoreElements()) {
            String key = (String)e.nextElement();
            String val = configManager.getString(CONFIG_NAMESPACE, key);
            //System.out.println(key+"="+val);
            if (val==null) continue;
            formatForOutput(key, s, true);
            s.append('=');
            formatForOutput(val, s, false);
            writer.println(s);
        }
        writer.flush();

        // remove - mktong
        //properties.store(new BufferedOutputStream(new FileOutputStream(localFile)), "TopCoder ContestApplet Preferences File");

        // Notify all the observers
        saveObserver.fireIt();
    }

    public final void saveColors(HashMap colors) {
        String key;
        Color color;
        for (Iterator itr = colors.keySet().iterator(); itr.hasNext();) {
            key = (String) itr.next();
            color = (Color) colors.get(key);
            setProperty(key, String.valueOf(color.getRGB()));
        }

        try {
            savePreferences();
        } catch (Throwable t) {
        }
    }

    public final void setColor(String key, Color color) {
        setProperty(key, String.valueOf(color.getRGB()));
    }

    public final String getFont(String keyName) {
        // This should hopefully make the default font everywhere Monospaced
        // which should be implemented in every Java distribution.
        // the only way the property can be set differently should be via
        // the preferences window, which should only allow fonts that
        // exist in the system.
        if(keyName.equals(USERTABLEFONT)) {
            return getFont(keyName, "Arial");
        } else if (keyName.equals(SUMMARYFONT)) {
            return getFont(keyName, "Arial");
        }
        return getFont(keyName, "Monospaced");
    }

    public final String getFont(String keyName, String d) {
        // This should hopefully make the default font everywhere Monospaced
        // which should be implemented in every Java distribution.
        // the only way the property can be set differently should be via
        // the preferences window, which should only allow fonts that
        // exist in the system.
        String prop = getProperty(keyName, d);
        if (prop == null || prop.equals("")) return d; else return (prop);
    }

    public final void setFont(String keyName, String fontname) {
        // for set/get symmetry
        setProperty(keyName, fontname);
    }

    public final int getFontSize(String keyName) {
        if(keyName.equals(USERTABLEFONTSIZE)) {
            return getFontSize(keyName, 12);
        } else if(keyName.equals(SUMMARYFONTSIZE)) {
            return getFontSize(keyName, 12);
        }
        return getFontSize(keyName, 12);
    }

    public final int getFontSize(String keyName, int d) {
        String prop = getProperty(keyName);
        if (prop == null || prop.equals("")) return d;
        else return Integer.parseInt(prop);
    }

    public final void setFontSize(String keyName, String fontname) {
        // for set/get symmetry
        setProperty(keyName, fontname);
    }

    public final Color getColor(String type) {
        return getColor(type, Color.black);
    }

    // Color function
    public final Color getColor(String type, Color defaultColor) {
        try {
            String colorValue = getProperty(type);
            if (colorValue != null) {
                return new Color(Integer.parseInt(colorValue));
            }
        } catch (NumberFormatException e) {
        }

        if (type.equals(CHATSYSTEMFORE)) return Color.green;
        if (type.equals(CHATSYSTEMBACK)) return Color.black;
        if (type.equals(CHATEMPHSYSTEMFORE)) return Color.green;
        if (type.equals(CHATEMPHSYSTEMBACK)) return Color.black;
        if (type.equals(CHATGENERALFORE)) return Color.white;
        if (type.equals(CHATGENERALBACK)) return Color.black;
        if (type.equals(CHATGENERALTOFORE)) return Color.white;
        if (type.equals(CHATGENERALTOBACK)) return Color.red;
        if (type.equals(CHATMEFORE)) return Common.LIGHT_GREY;
        if (type.equals(CHATMEBACK)) return Color.black;
        if (type.equals(CHATWHISPERFORE)) return Common.LIGHT_GREY;
        if (type.equals(CHATWHISPERBACK)) return Color.black;
        if (type.equals(CHATWHISPERTOFORE)) return Common.LIGHT_GREY;
        if (type.equals(CHATWHISPERTOBACK)) return Color.red;
        if (type.equals(CHATHANDLEBACK)) return Color.black;

        if (type.equals(CHATFINDBACK)) return Color.BLUE;
        if (type.equals(CHATFINDHIGHLIGHT)) return Color.GRAY;

        if (type.equals(EDSTDFORE)) return Color.white;
        if (type.equals(EDSTDBACK)) return Color.black;

        if (type.equals(EDSTDSELT)) return Common.HF_COLOR;
        if (type.equals(EDSTDSELB)) return Common.LIGHT_GREEN;
        if (type.equals(EDSTDSYNTAXCOMMENTS)) return Color.decode("0x00CC00");
        if (type.equals(EDSTDSYNTAXLITERALS)) return Color.decode("0xFF00FF");
        if (type.equals(EDSTDSYNTAXKEYWORDS)) return Color.decode("0x9999FF");
        if (type.equals(EDSTDSYNTAXDEFAULT)) return Color.decode("0xFFFFFF");

        if (type.equals(PROBLEMFORE)) return Color.white;
        if (type.equals(PROBLEMBACK)) return Color.black;
        if (type.equals(PROBLEMFIXEDFORE)) return Color.white;
        if (type.equals(PROBLEMFIXEDBACK)) return Color.black;

        if (type.equals(MESSAGEFORE)) return Color.white;
        if (type.equals(MESSAGEBACK)) return Color.black;

        if (type.equals(CHALSRCFORE)) return Color.white;
        if (type.equals(CHALSRCBACK)) return Color.black;
        if (type.equals(CHALPROBFORE)) return Color.white;
        if (type.equals(CHALPROBBACK)) return Color.black;

        if (type.equals(MODERATED_CHAT_QUESTION_FOREGROUND)) return Color.green;
        if (type.equals(MODERATED_CHAT_QUESTION_BACKGROUND)) return Color.black;
        if (type.equals(MODERATED_CHAT_SPEAKER_FOREGROUND)) return Color.white;
        if (type.equals(MODERATED_CHAT_SPEAKER_BACKGROUND)) return Color.black;

        if (type.startsWith(SUMMARYUNOPENED)) return new Color(255,255,255);
        if (type.startsWith(SUMMARYOPENED)) return new Color(255,255,255);
        if (type.startsWith(SUMMARYCOMPILED)) return new Color(255,255,255);
        if (type.startsWith(SUMMARYJAVAPOINTS)) return new Color(0,255,0);
        if (type.startsWith(SUMMARYJAVACHLPASSED)) return new Color(0,255,0);
        if (type.startsWith(SUMMARYJAVACHLFAILED)) return new Color(0,255,0);
        if (type.startsWith(SUMMARYJAVASYSPASSED)) return new Color(0,255,0);
        if (type.startsWith(SUMMARYJAVASYSFAILED)) return new Color(255,0,51);
        if (type.startsWith(SUMMARYCPPPOINTS)) return new  Color(255,255,153);
        if (type.startsWith(SUMMARYCPPCHLPASSED)) return new Color(255,255,153);
        if (type.startsWith(SUMMARYCPPCHLFAILED)) return new Color(255,255,153);
        if (type.startsWith(SUMMARYCPPSYSPASSED)) return new Color(255,255,153);
        if (type.startsWith(SUMMARYCPPSYSFAILED)) return new Color(255,0,51);

        if (type.startsWith(SUMMARYCSHARPPOINTS)) return new Color(102,102,255);
        if (type.startsWith(SUMMARYCSHARPCHLPASSED)) return new Color(102,102,255);
        if (type.startsWith(SUMMARYCSHARPCHLFAILED)) return new Color(102,102,255);
        if (type.startsWith(SUMMARYCSHARPSYSPASSED)) return new Color(102,102,255);
        if (type.startsWith(SUMMARYCSHARPSYSFAILED)) return new Color(255,0,51);

        Color vbColor = new Color(129,217,255);
        if (type.startsWith(SUMMARYVBPOINTS)) return vbColor;
        if (type.startsWith(SUMMARYVBCHLPASSED)) return vbColor;
        if (type.startsWith(SUMMARYVBCHLFAILED)) return vbColor;
        if (type.startsWith(SUMMARYVBSYSPASSED)) return vbColor;
        if (type.startsWith(SUMMARYVBSYSFAILED)) return new Color(255,0,51);

        Color pythonColor = new Color(255,102,255);
        if (type.startsWith(SUMMARYPYTHONPOINTS)) return pythonColor;
        if (type.startsWith(SUMMARYPYTHONCHLPASSED)) return pythonColor;
        if (type.startsWith(SUMMARYPYTHONCHLFAILED)) return pythonColor;
        if (type.startsWith(SUMMARYPYTHONSYSPASSED)) return pythonColor;
        if (type.startsWith(SUMMARYPYTHONSYSFAILED)) return new Color(255,0,51);

        Color python3Color = new Color(255,150,50);
        if (type.startsWith(SUMMARYPYTHON3POINTS)) return python3Color;
        if (type.startsWith(SUMMARYPYTHON3CHLPASSED)) return python3Color;
        if (type.startsWith(SUMMARYPYTHON3CHLFAILED)) return python3Color;
        if (type.startsWith(SUMMARYPYTHON3SYSPASSED)) return python3Color;
        if (type.startsWith(SUMMARYPYTHON3SYSFAILED)) return new Color(255,0,51);

        return defaultColor;
    }

    /** Gets a boolean value from the properties */
    public final boolean isTrue(String key) {
        // Get the property
        String propValue = getProperty(key);

        // If found - return the value
        if(propValue!=null) return new Boolean(propValue).booleanValue();

        // Defaults
        if(key.equals(LocalPreferences.getKeyAttribute(SUMMARYJAVACHLPASSED, LocalPreferences.ATTRIBUTEBOLD))) return true;
        if(key.equals(LocalPreferences.getKeyAttribute(SUMMARYJAVASYSPASSED, LocalPreferences.ATTRIBUTEBOLD))) return true;
        if(key.equals(LocalPreferences.getKeyAttribute(SUMMARYCPPCHLPASSED, LocalPreferences.ATTRIBUTEBOLD))) return true;
        if(key.equals(LocalPreferences.getKeyAttribute(SUMMARYCPPSYSPASSED, LocalPreferences.ATTRIBUTEBOLD))) return true;
        if(key.equals(LocalPreferences.getKeyAttribute(SUMMARYCSHARPCHLPASSED, LocalPreferences.ATTRIBUTEBOLD))) return true;
        if(key.equals(LocalPreferences.getKeyAttribute(SUMMARYCSHARPSYSPASSED, LocalPreferences.ATTRIBUTEBOLD))) return true;

        if(key.equals(LocalPreferences.getKeyAttribute(SUMMARYJAVACHLPASSED, LocalPreferences.ATTRIBUTEITALIC))) return true;
        if(key.equals(LocalPreferences.getKeyAttribute(SUMMARYJAVASYSPASSED, LocalPreferences.ATTRIBUTEITALIC))) return true;
        if(key.equals(LocalPreferences.getKeyAttribute(SUMMARYJAVASYSFAILED, LocalPreferences.ATTRIBUTEITALIC))) return true;
        if(key.equals(LocalPreferences.getKeyAttribute(SUMMARYCPPCHLPASSED, LocalPreferences.ATTRIBUTEITALIC))) return true;
        if(key.equals(LocalPreferences.getKeyAttribute(SUMMARYCPPSYSPASSED, LocalPreferences.ATTRIBUTEITALIC))) return true;
        if(key.equals(LocalPreferences.getKeyAttribute(SUMMARYCPPSYSFAILED, LocalPreferences.ATTRIBUTEITALIC))) return true;
        if(key.equals(LocalPreferences.getKeyAttribute(SUMMARYCSHARPCHLPASSED, LocalPreferences.ATTRIBUTEITALIC))) return true;
        if(key.equals(LocalPreferences.getKeyAttribute(SUMMARYCSHARPSYSPASSED, LocalPreferences.ATTRIBUTEITALIC))) return true;
        if(key.equals(LocalPreferences.getKeyAttribute(SUMMARYCSHARPSYSFAILED, LocalPreferences.ATTRIBUTEITALIC))) return true;

        if(key.equals(LocalPreferences.getKeyAttribute(SUMMARYVBCHLPASSED, LocalPreferences.ATTRIBUTEBOLD))) return true;
        if(key.equals(LocalPreferences.getKeyAttribute(SUMMARYVBSYSPASSED, LocalPreferences.ATTRIBUTEBOLD))) return true;
        if(key.equals(LocalPreferences.getKeyAttribute(SUMMARYVBCHLPASSED, LocalPreferences.ATTRIBUTEITALIC))) return true;
        if(key.equals(LocalPreferences.getKeyAttribute(SUMMARYVBSYSPASSED, LocalPreferences.ATTRIBUTEITALIC))) return true;
        if(key.equals(LocalPreferences.getKeyAttribute(SUMMARYVBSYSFAILED, LocalPreferences.ATTRIBUTEITALIC))) return true;

        if(key.equals(LocalPreferences.getKeyAttribute(SUMMARYPYTHONCHLPASSED, LocalPreferences.ATTRIBUTEBOLD))) return true;
        if(key.equals(LocalPreferences.getKeyAttribute(SUMMARYPYTHONSYSPASSED, LocalPreferences.ATTRIBUTEBOLD))) return true;
        if(key.equals(LocalPreferences.getKeyAttribute(SUMMARYPYTHONCHLPASSED, LocalPreferences.ATTRIBUTEITALIC))) return true;
        if(key.equals(LocalPreferences.getKeyAttribute(SUMMARYPYTHONSYSPASSED, LocalPreferences.ATTRIBUTEITALIC))) return true;
        if(key.equals(LocalPreferences.getKeyAttribute(SUMMARYPYTHONSYSFAILED, LocalPreferences.ATTRIBUTEITALIC))) return true;

        if(key.equals(LocalPreferences.getKeyAttribute(SUMMARYPYTHON3CHLPASSED, LocalPreferences.ATTRIBUTEBOLD))) return true;
        if(key.equals(LocalPreferences.getKeyAttribute(SUMMARYPYTHON3SYSPASSED, LocalPreferences.ATTRIBUTEBOLD))) return true;
        if(key.equals(LocalPreferences.getKeyAttribute(SUMMARYPYTHON3CHLPASSED, LocalPreferences.ATTRIBUTEITALIC))) return true;
        if(key.equals(LocalPreferences.getKeyAttribute(SUMMARYPYTHON3SYSPASSED, LocalPreferences.ATTRIBUTEITALIC))) return true;
        if(key.equals(LocalPreferences.getKeyAttribute(SUMMARYPYTHON3SYSFAILED, LocalPreferences.ATTRIBUTEITALIC))) return true;

        if(key.equals(LocalPreferences.EDSTDINDENT)) return true;

        if(key.equals(LocalPreferences.UNUSEDCODECHECK)) return true;

        // Return a default
        return false;

    }

    /** Sets a boolean value in the properties */
    public final void setTrue(String key, boolean value) {
        setProperty(key, value ? "true" : "false");
    }

    public final List getPlugins() {
        // Create the return list
        ArrayList list = new ArrayList();

        // Find out how many plugins there are
        int numPlugins = 0;
        try {
            String pluginNumber = getProperty(PLUGINNUMBER);
            if (pluginNumber != null) numPlugins = Integer.parseInt(pluginNumber);
        } catch (NumberFormatException e) {
            printError("editor.numplugins is not a valid number");
        }

        // Loop through each plugin
        for (int x = numPlugins; x > 0; x--) {
            // Get the Attributes of the editor
            String name = getProperty(getPluginKey(x, PLUGINNAME));
            String entryPoint = getProperty(getPluginKey(x, PLUGINENTRYPOINT));
            String classPath = getProperty(getPluginKey(x, PLUGINCLASSPATH));
            String eager = getProperty(getPluginKey(x, PLUGINEAGER));

            // Validate them
            if (name == null) {
                printError(getPluginKey(x, PLUGINNAME) + " is not defined");
                continue;
            }
            if (entryPoint == null) {
                printError(getPluginKey(x, PLUGINENTRYPOINT) + " is not defined");
                continue;
            }
            if (classPath == null) {
                printError(getPluginKey(x, PLUGINCLASSPATH) + " is not defined");
                continue;
            }
            if(eager==null) eager = "0";

            // Create the editor plugins
            list.add(new EditorPlugin(name, entryPoint, classPath, (eager.equals("1") ? true : false)));
        }

        // Return the resulting list
        return list;
    }

    public final void setPluginCommonPath(String commonPath) {
        setProperty(PLUGINCOMMONPATH, commonPath);
    }

    public final String getPluginCommonPath() {
        return getProperty(PLUGINCOMMONPATH);
    }


    public final void setPlugins(List plugins) {

        // Loop through the plugin list
        int pluginNum = 0;
        for (Iterator itr = plugins.iterator(); itr.hasNext();) {

            // Get the plugin
            EditorPlugin plugin;
            try {
                plugin = (EditorPlugin) itr.next();
            } catch (ClassCastException e) {
                printError("Plugins list contained a non-plugin!");
                continue;
            }

            // Bump the plugin number up
            pluginNum++;

            // Set the properties for the plugin
            setProperty(getPluginKey(pluginNum, PLUGINNAME), plugin.getName());
            setProperty(getPluginKey(pluginNum, PLUGINENTRYPOINT), plugin.getEntryPoint());
            setProperty(getPluginKey(pluginNum, PLUGINCLASSPATH), plugin.getClassPath());
            setProperty(getPluginKey(pluginNum, PLUGINEAGER), (plugin.getEager() ? "1" : "0"));

        }

        // Set the number of plugins
        setProperty(PLUGINNUMBER, String.valueOf(pluginNum));
    }

    public final HashSet getAllowedList() {
        HashSet ret = new HashSet();
        String userList = getProperty(ALLOWEDLIST);

        if (null != userList) {
            String[] userHandles = userList.split(",");

            for (int i = 0; i < userHandles.length; i++) {
                String handle = userHandles[i].trim();
                if(handle.length() > 0) {
                    ret.add(handle);
                }
            }
        }
        return ret;
    }

    public final HashSet getIgnoreList() {
        HashSet ret = new HashSet();
        String number = getProperty(IGNORENUMBER);
        if (number == null) number = "0";
        int total = Integer.parseInt(number);
        for (int i = 0; i < total; i++) {
            String name = getProperty(IGNORELIST + "." + i);
            if (name != null)
                ret.add(name);
        }
        return ret;
    }

    public final void addToIgnoreList(String s) {
        String number = getProperty(IGNORENUMBER);
        if (number == null) number = "0";
        int total = Integer.parseInt(number);
        setProperty(IGNORELIST + "." + (total++), s);
        setProperty(IGNORENUMBER, "" + total);
        try {
            savePreferences();
        } catch (Exception e) {
            System.err.println("Error saving preferences file");
            e.printStackTrace();
        }
    }

    public final void clearIgnore() {
        setProperty(IGNORENUMBER, "0");
        try {
            savePreferences();
        } catch (Exception e) {
            System.err.println("Error saving preferences file");
            e.printStackTrace();
        }
    }

    public final void removeFromIgnoreList(String s) {
        //HashSet ret = new HashSet();
        String number = getProperty(IGNORENUMBER);
        if (number == null) number = "0";
        int total = Integer.parseInt(number);
        boolean found = false;
        for (int i = 0; i < total; i++) {
            String name = getProperty(IGNORELIST + "." + i);
            if (found) {
                setProperty(IGNORELIST + "." + (i - 1), name);
            }
            if (name.equals(s)) {
                found = true;
            }
        }
        setProperty(IGNORENUMBER, "" + (total - (found ? 1 : 0)));
        try {
            savePreferences();
        } catch (Exception e) {
            System.err.println("Error saving preferences file");
            e.printStackTrace();
        }
    }

    public void setProperty(String keyName, String value) {
        //properties.setProperty(keyName, value);
        if(keyName.equals("popsedit.config.javatemplate"))
            System.out.println(value);
        try {
            configManager.createTemporaryProperties(CONFIG_NAMESPACE);
            configManager.setProperty(CONFIG_NAMESPACE, keyName, value);
            configManager.commit(CONFIG_NAMESPACE, CONFIG_USER);
        } catch (UnknownNamespaceException une) {
            une.printStackTrace();
        } catch (ConfigManagerException cme) {
            cme.printStackTrace();
        }
    }

    public String getProperty(String keyName) {
        //return properties.getProperty(keyName);
        try {
            String s =configManager.getString(CONFIG_NAMESPACE, keyName);
            if(s == null)
                return null;
            return s.replaceAll("\\\\;",";");
        } catch (UnknownNamespaceException une) {
            une.printStackTrace();
            return "";
        }
    }

    /**
     * Gets the hot key (shortcut) for the given key name.
     *
     * @param keyName the key name
     * @return the key shortcut for given key name
     */
    public String getHotKey(String keyName) {
        if (keyName == EDSTDKEYFIND) {
            return getProperty(keyName, "Alt+F");
        } else if (keyName == EDSTDKEYGOTO) {
            return getProperty(keyName, "Alt+G");
        } else if (keyName == EDSTDKEYUNDO) {
            return getProperty(keyName, "Ctrl+Z");
        } else if (keyName == EDSTDKEYREDO) {
            return getProperty(keyName, "Ctrl+Y");
        } else if (keyName == KEYSAVE) {
            return getProperty(keyName, "Alt+S");
        } else if (keyName == KEYCOMPILE) {
            return getProperty(keyName, "Alt+C");
        } else if (keyName == KEYTEST) {
            return getProperty(keyName, "Alt+T");
        } else if (keyName == KEYBATCHTEST) {
            return getProperty(keyName, "Alt+B");
        } else if (keyName == KEYSUBMIT) {
            return getProperty(keyName, "Alt+U");
        }
        return getProperty(keyName);
    }

    public String getProperty(String keyName, String dval) {
        //return properties.getProperty(keyName, dval);
        try {
            String val = configManager.getString(CONFIG_NAMESPACE, keyName);
            if (val == null || val.equals("")) {
                return dval;
            } else {
                return val.replaceAll("\\\\;",";");
            }
        } catch (UnknownNamespaceException une) {
            une.printStackTrace();
            return dval;
        }
    }

    public boolean getBoolean(String keyName, boolean dval) {
        return new Boolean(this.getProperty(keyName, "" + dval)).booleanValue();
    }

    public final Enumeration getKeys() {
        //return properties.propertyNames();
        return getNestedProperties("com").elements();
    }

    public final String removeProperty(String propertyKey) {
        String val = "";
        try {
            configManager.createTemporaryProperties(CONFIG_NAMESPACE);
            val = getProperty(propertyKey);
            configManager.removeProperty(CONFIG_NAMESPACE, propertyKey);
            configManager.commit(CONFIG_NAMESPACE, CONFIG_USER);
        } catch (UnknownNamespaceException une) {
            une.printStackTrace();
        } catch (ConfigManagerException cme) {
            cme.printStackTrace();
        }
        return val;
    }

    private final static String getPluginKey(int num, String name) {
        StringBuffer str = new StringBuffer("editor.");
        str.append(num);
        str.append(".");
        str.append(name);
        return str.toString();
    }

    public final int getTabSize() {
        String size = getProperty(EDSTDTABSIZE);
        return size == null ? 4 : Integer.parseInt(size);
    }

    public final boolean isEditorCache() {
        String cache = getProperty(PLUGINCACHED);
        return cache == null ? true : !cache.equals("false");
    }

    public final boolean isEditorDebug() {
        String debug = getProperty(PLUGINDEBUG);
        return debug == null ? false : debug.equals("true");
    }

    public final boolean isSyntaxHighlight() {
        String syntaxHighlighted = getProperty(EDSTDSYNTAXHIGHLIGHT);
        return syntaxHighlighted == null ? true : !syntaxHighlighted.equals("false");
    }

    public final void setSyntaxHighlight(boolean b) {
        setProperty(EDSTDSYNTAXHIGHLIGHT, String.valueOf(b));
    }

    public final boolean isViewerSyntaxHighlight() {
        String syntaxHighlighted = getProperty(CHALSRCSYNTAXHIGHLIGHT);
        return syntaxHighlighted == null ? true : !syntaxHighlighted.equals("false");
    }

    public final void setViewerSyntaxHighlight(boolean b) {
        setProperty(CHALSRCSYNTAXHIGHLIGHT, String.valueOf(b));
    }

    public final String getDefaultEditorName() {
        String name = getProperty(PLUGINDEFAULTNAME);
        return name == null ? "" : name;
    }

    public final void setDefaultEditorName(String name) {
        setProperty(PLUGINDEFAULTNAME, name);
    }

    public final Dimension getSize(String name) {
        String prop = getProperty(name);
        if (prop == null) return null;

        int pos = prop.indexOf(":");
        if (pos < 0) return null;

        try {
            return new Dimension(Integer.parseInt(prop.substring(0, pos)), Integer.parseInt(prop.substring(pos + 1)));
        } catch (NumberFormatException e) {
            return null;
        }
    }


    public final void setSize(String name, Dimension dim) {
        setProperty(name, (int) dim.getWidth() + ":" + (int) dim.getHeight());
    }

    public final Point getLocation(String name) {
        String prop = getProperty(name);
        if (prop == null) return null;

        int pos = prop.indexOf(":");
        if (pos < 0) return null;

        try {
            return new Point(Integer.parseInt(prop.substring(0, pos)), Integer.parseInt(prop.substring(pos + 1)));
        } catch (NumberFormatException e) {
            return null;
        }
    }


    public final void setLocation(String name, Point pt) {
        setProperty(name, (int) pt.getX() + ":" + (int) pt.getY());
    }

    private final static void printError(String errorMsg) {
        System.err.println("LocalPreferences: " + errorMsg);
    }

    public final UIManager[] getAllUIManagers() {
        if (managers != null) {
            return managers;
        }

        synchronized(this) {
            if (managers == null) {
                List list = new ArrayList();
                for (int i = 0; i < UI_THEME_EMBEDDED.length; ++i) {
                    list.add(UIFactory.getUIManagerFromResource(getClass(), UI_THEME_EMBEDDED[i]));
                }

                try {
                    File themeDir = new File(LocalPreferences.getPreferencesFile().getParent(), UI_THEME_DIRECTORY);
                    list.addAll(Arrays.asList(UIFactory.getAllUIManagers(themeDir)));
                } catch (IOException e) {
                }

                managers = (UIManager[]) list.toArray(new UIManager[list.size()]);
            }
        }
        return managers;
    }

    private final static File getPreferencesFile() throws IOException {

	    try {
			// Get the local directory
			String localDir = System.getProperty(FILELOCATION);
            if (localDir == null) localDir = System.getProperty("user.home");
            if (localDir == null) localDir = System.getProperty("java.home");

			// Get the preferences file
			String fileName = System.getProperty(FILENAME);
			if (fileName == null) fileName = "contestapplet.conf";

			// Return the preferences file
            return new File(localDir, fileName);
        } catch (Exception e) {
            throw new IOException("Cannot access local properties file");
        }

    }

    private final static File getPreferencesBackupFile() throws IOException {

	    try {
			// Get the local directory
			String localDir = System.getProperty(FILELOCATION);
            if (localDir == null) localDir = System.getProperty("user.home");
            if (localDir == null) localDir = System.getProperty("java.home");

			// Get the preferences file
			String fileName = System.getProperty(FILENAME);
			if (fileName == null) fileName = "contestapplet.conf";
            fileName = fileName + ".bak";

			// Return the preferences file
            return new File(localDir, fileName);
        } catch (Exception e) {
            throw new IOException("Cannot access local properties file");
        }

    }

    //
    // Flip the setting for the broadcast popup.
    //
    public void toggleBroadcastPopup() {
        String val = getProperty(DISABLEBROADCASTPOPUP);
        if (val == null)
            val = "false";
        setProperty(DISABLEBROADCASTPOPUP, (val.equals("true")) ? "false" : "true");
        try {
            savePreferences();
        } catch (Exception e) {
            System.err.println("Error saving preferences file");
            e.printStackTrace();
        }
    }


    //
    // Flip the setting for the broadcast popup.
    //
    public void toggleLeaderTicker() {
        String val = getProperty(LEADER_TICKER_DISABLED);
        if (val == null) {
            val = "false";
        }
        setProperty(LEADER_TICKER_DISABLED, (val.equals("true")) ? "false" : "true");
        try {
            savePreferences();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //
    // Flip the setting for the broadcast beep.
    //
    public void toggleBroadcastBeep() {
        String val = getProperty(DISABLEBROADCASTBEEP);
        if (val == null)
            val = "false";
        setProperty(DISABLEBROADCASTBEEP, (val.equals("true")) ? "false" : "true");
        try {
            savePreferences();
        } catch (Exception e) {
            System.err.println("Error saving preferences file");
            e.printStackTrace();
        }
    }

    //
    // Flip the setting for timestamps
    //
    public void toggleEnableTimestamps() {
        String val = getProperty(ENABLETIMESTAMPS);
        if (val == null)
            val = "false";
        setProperty(ENABLETIMESTAMPS, (val.equals("true")) ? "false" : "true");
        try {
            savePreferences();
        } catch (Exception e) {
            System.err.println("Error saving preferences file");
            e.printStackTrace();
        }
    }
    public void toggleEnableUnusedCodeCheck() {
        String val = getProperty(UNUSEDCODECHECK);
        if (val == null)
            val = "true";
        setProperty(UNUSEDCODECHECK, (val.equals("true")) ? "false" : "true");
        try {
            savePreferences();
        } catch (Exception e) {
            System.err.println("Error saving preferences file");
            e.printStackTrace();
        }
    }
    public void toggleDisableChatScrolling() {
        String val = getProperty(CHAT_SCROLLING);
        if (val == null)
            val = "false";
        setProperty(CHAT_SCROLLING, (val.equals("true")) ? "false" : "true");
        try {
            savePreferences();
        } catch (Exception e) {
            System.err.println("Error saving preferences file");
            e.printStackTrace();
        }
    }

    /** Helper function to concatenate the key and attribute */
    public static String getKeyAttribute(String key, String attribute) {
        return key + "." + attribute;
    }

    private static class PrefObservable extends Observable {

        public void fireIt() {
            setChanged();
            notifyObservers();
        }
    }

    /**
     * Formats a key or value for output in a properties file.
     * See store for a description of the format.
     *
     * @param str the string to format
     * @param buffer the buffer to add it to
     * @param key true if all ' ' must be escaped for the key, false if only
     *        leading spaces must be escaped for the value
     * @see #store(OutputStream, String)
     */
     private void formatForOutput(String str, StringBuffer buffer, boolean key)
     {
         str = str.replaceAll("\\\\;",";");
         if (key)
         {
             buffer.setLength(0);
             buffer.ensureCapacity(str.length());
         }
         else
             buffer.ensureCapacity(buffer.length() + str.length());
         boolean head = true;
         int size = str.length();
         for (int i = 0; i < size; i++)
         {
             char c = str.charAt(i);
             switch (c)
             {
                 case '\n':
                     buffer.append("\\n");
                     break;
                 case '\r':
                     buffer.append("\\r");
                     break;
                 case '\t':
                     buffer.append("\\t");
                     break;
                 case ' ':
                     buffer.append(head ? "\\ " : " ");
                     break;
                 case '\\':
                 case '!':
                 case '#':
                 case '=':
                 case ':':
                     buffer.append('\\').append(c);
                     break;
                 default:
                     if (c < ' ' || c > '~')
                     {
                         String hex = Integer.toHexString(c);
                         buffer.append("\\u0000".substring(0, 6 - hex.length()));
                         buffer.append(hex);
                     }
                     else
                         buffer.append(c);
             }
             if (c != ' ')
                 head = key;
         }
     }


     public static String resolveStatusPropertyFormat(int languageId, int componentStatus) {
         String language;
         switch (languageId) {
             case JavaLanguage.ID :
                 language = "java";
                 break;
             case CPPLanguage.ID :
                 language = "cpp";
                 break;
             case CSharpLanguage.ID :
                 language = "csharp";
                 break;
             case VBLanguage.ID :
                 language = "vb";
                 break;
             case PythonLanguage.ID :
                 language = "python";
                 break;
             case Python3Language.ID :
                 language = "python3";
                 break;
             default :
                 language = "";
         }
         String status;
         switch (componentStatus) {
             case ContestConstants.NOT_OPENED:
                 language = "";
                 status = "unopened";
                 break;
             case ContestConstants.LOOKED_AT:
                 status = "opened";
                 language = "";
                 break;
             case ContestConstants.COMPILED_UNSUBMITTED:
                 status = "compiled";
                 language = "";
                 break;
             case ContestConstants.NOT_CHALLENGED:
                 status = "";
                 language = language + "points";
                 break;
             case ContestConstants.CHALLENGE_FAILED:
                 status = "chlfailed";
                 break;
             case ContestConstants.CHALLENGE_SUCCEEDED:
                 status = "chlpassed";
                 break;
             case ContestConstants.SYSTEM_TEST_SUCCEEDED:
                 status = "syspassed";
                 break;
             case ContestConstants.SYSTEM_TEST_FAILED:
                 status = "sysfailed";
                 break;
             default:
                 throw new IllegalArgumentException("Unknown component status "+componentStatus);
         }
         String prop = "com.topcoder.client.contestApplet.common.LocalPreferences.summary"+language+status;
         return prop;
     }
}

/*	public final static String SYSTEMFORE = "com.topcoder.client.contestApplet.panels.ChatPanel.systemfore";
	public final static String SYSTEMBACK = "com.topcoder.client.contestApplet.panels.ChatPanel.systemback";
	public final static String EMPHSYSTEMFORE = "com.topcoder.client.contestApplet.panels.ChatPanel.emphsystemfore";
	public final static String EMPHSYSTEMBACK = "com.topcoder.client.contestApplet.panels.ChatPanel.emphsystemback";
	public final static String GENERALFORE = "com.topcoder.client.contestApplet.panels.ChatPanel.generalfore";
	public final static String GENERALBACK = "com.topcoder.client.contestApplet.panels.ChatPanel.generalback";
	public final static String GENERALTOFORE = "com.topcoder.client.contestApplet.panels.ChatPanel.generaltofore";
	public final static String GENERALTOBACK = "com.topcoder.client.contestApplet.panels.ChatPanel.generaltoback";
	public final static String MEFORE = "com.topcoder.client.contestApplet.panels.ChatPanel.mefore";
	public final static String MEBACK = "com.topcoder.client.contestApplet.panels.ChatPanel.meback";
	public final static String WHISPERFORE = "com.topcoder.client.contestApplet.panels.ChatPanel.whisperfore";
	public final static String WHISPERBACK = "com.topcoder.client.contestApplet.panels.ChatPanel.whisperback";
	public final static String WHISPERTOFORE = "com.topcoder.client.contestApplet.panels.ChatPanel.whispertofore";
	public final static String WHISPERTOBACK = "com.topcoder.client.contestApplet.panels.ChatPanel.whispertoback";
	public final static String HANDLEBACK = "com.topcoder.client.contestApplet.panels.ChatPanel.handleback";	*/


/* @(#)StandardPlugins.java */
