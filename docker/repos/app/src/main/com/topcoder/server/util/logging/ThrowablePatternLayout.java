/*
 * Author: Michael Cervantes (emcee)
 * Date: Jul 21, 2002
 * Time: 7:48:15 PM
 */
package com.topcoder.server.util.logging;

import org.apache.log4j.*;
import org.apache.log4j.spi.*;

import java.io.*;

public class ThrowablePatternLayout extends PatternLayout {

    boolean fullTrace = true;

    public ThrowablePatternLayout() {
    }

    public ThrowablePatternLayout(String pattern) {
        super(pattern);
    }

    /**
     * Set the <b>ShowStackTrace</b> option.
     *
     * This controls if the class should format the entire stack trace,
     * or just the Throwable's toString() information (class name and
     * message usually, no trace information).  Default is <code>true</code>,
     * which will display full stack trace information.
     */
    public void setShowStackTrace(boolean showTrace) {
        this.fullTrace = showTrace;
    }

    /**
     * This class uses, and does not ignore, the information contained within the
     * throwable of {@link org.apache.log4j.spi.LoggingEvent}s.
     * @return false.
     */
    public boolean ignoresThrowable() {
        return false;
    }

    public String format(LoggingEvent event) {
        ThrowableInformation ti = event.getThrowableInformation();

        if (ti != null) {
            StringWriter sw = new StringWriter();
            PrintWriter pw = new PrintWriter(sw);

            pw.print(super.format(event));
            pw.print("  ");
            if (fullTrace)
                ti.getThrowable().printStackTrace(pw);
            else
                pw.println(ti.getThrowable().toString());
            pw.close();

            return sw.toString();
        }

        return super.format(event);
    }
}
