package com.topcoder.client.contestMonitor.view.gui;

import com.topcoder.client.contestMonitor.model.CommandSender;
import com.topcoder.server.listener.monitor.CachedItem;
import org.apache.log4j.Category;

final class CachedObjectsPanel extends MonitorBasePanel {

    private final Category cat = Category.getInstance(getClass());

    private final boolean isGuiOutput;

    CachedObjectsPanel(CommandSender sender, boolean isGuiOutput) {
        super(sender);
        this.isGuiOutput = isGuiOutput;
    }

    public void cycle() throws InterruptedException {
        CachedItem chatItem = getSender().dequeueCachedItem();
        String message = chatItem.getMessage();
        String date = chatItem.getTime();
        String s = date + "> " + message + "\n";
        info(s);
        if (isGuiOutput) {
            insertString(s);
        }
    }

    private void info(Object message) {
        cat.info(message);
    }

}
