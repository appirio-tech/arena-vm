/*
 * Author: Michael Cervantes (emcee)
 * Date: Jun 3, 2002
 * Time: 3:57:47 PM
 */
package com.topcoder.client.contestMonitor.model;

import com.topcoder.client.contestMonitor.view.LoggingView;
import com.topcoder.server.util.logging.net.StreamID;
import com.topcoder.server.util.logging.net.TCLoggingEvent;
import org.apache.log4j.spi.LoggingEvent;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

public class LoggingController {

    private Set loggingStreams = Collections.synchronizedSet(new HashSet());
    private CommandSender sender;
    private LoggingView view;

    public LoggingController(CommandSender sender) {
        this.sender = sender;
    }

    public void setLoggingView(LoggingView view) {
        this.view = view;
    }

    public void getLoggingStreams() {
        sender.sendGetLoggingStreams();
    }

    public void refreshStreams() {
        loggingStreams.clear();
        sender.sendGetLoggingStreams();
    }

    public void updateStreams(Collection streams) {
        loggingStreams.addAll(streams);
        view.updateStreams(streams);
    }

    public void routeLoggingEvent(StreamID id, TCLoggingEvent event) {
        view.routeLoggingEvent(id, event);
    }

    void reconnectEvent() {
        synchronized (loggingStreams) {
            for (Iterator it = loggingStreams.iterator(); it.hasNext();) {
                StreamID id = (StreamID) it.next();
                sender.sendLoggingStreamSubscribe(id);
            }
        }
    }

    public void openStream(StreamID id) {
        sender.sendLoggingStreamSubscribe(id);
    }

    public void closeStream(StreamID id) {
        sender.sendLoggingStreamUnsubscribe(id);
    }
}
