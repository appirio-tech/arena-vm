package com.topcoder.server.listener.monitor;

import com.topcoder.server.AdminListener.AdminConstants;
import com.topcoder.server.util.TCLinkedQueue;
import com.topcoder.shared.util.StoppableThread;

public final class MonitorChatHandler implements StoppableThread.Client {

    private final MonitorProcessor processor;
    private final TCLinkedQueue queue = new TCLinkedQueue();
    private final StoppableThread thread = new StoppableThread(this, "MonitorChatHandler");
    //private final String[] tabooWords=MonitorProperties.getTabooWords();/* SYHAAS 2002-05-09 dont need this anymore */

    public MonitorChatHandler(MonitorProcessor processor) {
        this.processor = processor;
    }

    void start() {
        thread.start();
    }

    void stop() {
        try {
            thread.stopThread();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    /* SYHAAS 2002-05-09 removed this method becuz since the addition of Item and
          moved this method into that class to differentiate between ChatItems and
          QuestionItems
    private boolean isBad(ChatItem item) {
        String message=item.getMessage();
        message=message.toLowerCase();
        for (int i=0; i<tabooWords.length; i++) {
            String word=tabooWords[i];
            if (message.indexOf(word)>=0) {
                return true;
            }
        }
        return false;
    }*/

    public void cycle() throws InterruptedException {
        /* SYHAAS 2002-05-09 changed this to work with the Item
ChatItem item=(ChatItem) queue.take();
if (isBad(item)) {
send(item);
}
*/
        Item item = (Item) queue.take();
        if (item.isBad()) send(item);
    }

    private void send(Item object) { /* SYHAAS 2002-05-09 changed arg from ChatItem to Item */
        processor.send(object, MonitorProcessor.ALL_ADMIN_LISTENERS, AdminConstants.RECIPIENT_ALL);
    }

    void chat(int roomID, String username, String message) {
        queue.put(new ChatItem(roomID, username, message));
    }

    /* SYHAAS 2002-05-09 add this method */
    public void question(int roomID, String username, String message) {
        queue.put(new QuestionItem(roomID, username, message));
    }

}
