/*
 * Author: Michael Cervantes (emcee)
 * Date: Jun 12, 2002
 * Time: 4:58:42 AM
 */
package com.topcoder.client.contestMonitor.view;

import com.topcoder.server.util.logging.net.StreamID;
import com.topcoder.server.util.logging.net.TCLoggingEvent;
import org.apache.log4j.spi.LoggingEvent;

import java.util.Collection;

public interface LoggingView {

    void updateStreams(Collection streams);

    void routeLoggingEvent(StreamID id, TCLoggingEvent event);
}
