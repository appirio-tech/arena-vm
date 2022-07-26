/*
 * Author: Michael Cervantes (emcee)
 * Date: Jun 3, 2002
 * Time: 3:57:47 PM
 */
package com.topcoder.client.contestMonitor.view.gui;

import com.topcoder.client.contestMonitor.model.LoggingController;
import com.topcoder.client.contestMonitor.model.LoggingStreamsTableModel;
import com.topcoder.client.contestMonitor.view.LoggingView;
import com.topcoder.server.util.logging.net.StreamID;
import com.topcoder.server.util.logging.net.TCLoggingEvent;
import org.apache.log4j.spi.LoggingEvent;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Vector;

public class LoggingFrameManager implements LoggingView {

    private Map loggingFrames = Collections.synchronizedMap(new HashMap());
    private LoggingStreamsTableModel loggingStreamsModel = new LoggingStreamsTableModel();
    private LoggingStreamsFrame loggingStreamsFrame;
    private LoggingController controller;


    public LoggingFrameManager(LoggingController controller) {
        this.controller = controller;
        controller.setLoggingView(this);
        loggingStreamsFrame = new LoggingStreamsFrame(this);
    }

    public void displayLoggingStreamsFrame() {
        controller.getLoggingStreams();
        loggingStreamsFrame.display();
    }

    public void closeAllLoggingFrames() {
        synchronized (loggingFrames) {
            List frames = new Vector(loggingFrames.values());
            for (Iterator it = frames.iterator(); it.hasNext();) {
                LoggingFrame frame = (LoggingFrame) it.next();
                frame.dispose();
            }
        }
    }

    public void refreshStreams() {
        loggingStreamsModel.clear();
        controller.refreshStreams();
    }

    public void updateStreams(Collection streams) {
        loggingStreamsModel.update(streams);
    }

    public void routeLoggingEvent(StreamID id, TCLoggingEvent event) {
        LoggingFrame frame = (LoggingFrame) loggingFrames.get(id);
        if (frame != null)
            frame.log(event);
    }

    public synchronized void displayLoggingFrame(StreamID id) {
        LoggingFrame frame = (LoggingFrame) loggingFrames.get(id);
        if (frame != null) {
            frame.show();
            return;
        }
        frame = new LoggingFrame(this, id);
        loggingFrames.put(id, frame);
        controller.openStream(id);
        frame.show();
    }

    public synchronized void closeLoggingFrame(StreamID id) {
        loggingFrames.remove(id);
        controller.closeStream(id);
    }

    public LoggingStreamsTableModel getLoggingStreamsModel() {
        return loggingStreamsModel;
    }
}
