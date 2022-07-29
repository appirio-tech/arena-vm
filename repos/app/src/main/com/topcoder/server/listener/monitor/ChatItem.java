package com.topcoder.server.listener.monitor;

/**
 * A General purpose chat message. Used for the lobby, IMs, and
 * coding rooms.
 *
 * Class rewritten by Sylvan Haas IV (syhaas) for implementation
 * of the moderated chat session project
 */
public class ChatItem extends Item {

    private final String[] tabooWords = MonitorProperties.getTabooWords();

    public ChatItem() {
        super();
    }

    public ChatItem(int roomID, String username, String message) {
        super(roomID, username, message);
    }

    /**
     * Should return TRUE if message has "taboo" words, FALSE if it is clean
     */
    public boolean isBad() {
        /* SYHAAS 2002-05-09 code taken exactly from MonitorChatHandler.isBad() */
        String msg = message.toLowerCase();
        for (int i = 0; i < tabooWords.length; i++) {
            String word = tabooWords[i];
            if (msg.indexOf(word) >= 0) {
                return true;
            }
        }
        return false;
    }
}
