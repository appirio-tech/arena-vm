package com.topcoder.client.contestMonitor.view.gui;

import com.topcoder.client.contestMonitor.model.CommandSender;
import com.topcoder.server.listener.monitor.ChatItem;

final class MonitorChatPanel extends MonitorBasePanel {

    MonitorChatPanel(CommandSender sender) {
        super(sender);
    }

    public void cycle() throws InterruptedException {
        ChatItem chatItem = getSender().dequeueChatItem();
        int roomID = chatItem.getRoomID();
        String username = chatItem.getUsername();
        String message = chatItem.getMessage();
        String date = chatItem.getTime();
        String s = date + ", roomID=" + roomID + ", " + username + "> " + message;
        insertString(s);
    }

}
