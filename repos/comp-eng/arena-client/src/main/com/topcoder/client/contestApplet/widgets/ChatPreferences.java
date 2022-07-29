package com.topcoder.client.contestApplet.widgets;

import java.awt.Color;
import java.util.*;

import com.topcoder.client.contestApplet.common.*;
import com.topcoder.client.contestApplet.common.LocalPreferences;

public class ChatPreferences {

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
	public final static String HANDLEBACK = "com.topcoder.client.contestApplet.panels.ChatPanel.handleback";
*/
    /* Da Twink Daddy - 05/12/2002 - New Memebrs */
    /**
     * Properties key for moderated chat question foreground color
     */
//	public static final String MODERATED_CHAT_QUESTION_FOREGROUND = "com.topcoder.client.contentApplet.panels.ChatPanel.moderatedChatQuestionFore";
    /** Properties key for moderated chat question background color */
//	public static final String MODERATED_CHAT_QUESTION_BACKGROUND = "com.topcoder.client.contentApplet.panels.ChatPanel.moderatedChatQuestionBack";
    /** Properties key for moderated chat speaker chat foreground color */
//	public static final String MODERATED_CHAT_SPEAKER_FOREGROUND = "com.topcoder.client.contentApplet.panels.ChatPanel.moderatedChatSpeakerFore";
    /** Properties key for moderated chat speaker chat background color */
//	public static final String MODERATED_CHAT_SPEAKER_BACKGROUND = "com.topcoder.client.contentApplet.panels.ChatPanel.moderatedChatSpeakerBack";

//	private final static LocalPreferences pref = LocalPreferences.getInstance();

/*	private final static LocalPreferences pref = LocalPreferences.getInstance();


	public static final void saveColors(HashMap colors) {
		for(Iterator itr = colors.keySet().iterator();itr.hasNext();) {
			String key = (String)itr.next();
			Color color = (Color)colors.get(key);

			pref.setProperty(key, String.valueOf(color.getRGB()));
		}

		try {
			pref.savePreferences();
		} catch (Throwable t) {}
	}

/*	public static final Color getColor(String type) {

		try {
			String colorValue = pref.getProperty(type);
			if(colorValue!=null) {
				return new Color(Integer.parseInt(colorValue));
			}
		} catch (NumberFormatException e) {}

		// Da Twink Daddy - 05/12/2002 - Added defaults for new members
		if(type.equals(SYSTEMFORE) || type.equals(MODERATED_CHAT_QUESTION_FOREGROUND)) return Color.green;
		if(type.equals(SYSTEMBACK) || type.equals(MODERATED_CHAT_QUESTION_BACKGROUND)) return Color.black;
		if(type.equals(EMPHSYSTEMFORE)) return Color.green;
		if(type.equals(EMPHSYSTEMBACK)) return Color.black;
		if(type.equals(GENERALFORE) || type.equals(MODERATED_CHAT_SPEAKER_FOREGROUND)) return Color.white;
		if(type.equals(GENERALBACK) || type.equals(MODERATED_CHAT_SPEAKER_BACKGROUND)) return Color.black;
		if(type.equals(GENERALTOFORE)) return Color.white;
		if(type.equals(GENERALTOBACK)) return Color.red;
		if(type.equals(MEFORE)) return Common.LIGHT_GREY;
		if(type.equals(MEBACK)) return Color.black;
		if(type.equals(WHISPERFORE)) return Common.LIGHT_GREY;
		if(type.equals(WHISPERBACK)) return Color.black;
		if(type.equals(WHISPERTOFORE)) return Common.LIGHT_GREY;
		if(type.equals(WHISPERTOBACK)) return Color.red;
		if(type.equals(HANDLEBACK)) return Color.black;

		return Color.black;
	}
*/
}