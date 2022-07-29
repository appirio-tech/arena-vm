/*
 * Copyright (C) 2005 TopCoder Inc., All Rights Reserved.
 */
package com.topcoder.util.syntaxhighlighter;

import com.topcoder.util.log.Level;
import com.topcoder.util.log.Log;

import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXParseException;


/**
 * <p>
 * This class is used internally by SyntaxHighlighter to log errors to a Log instance specified by SyntaxHighlighter.
 * If no Log is provided, this simply does nothing (similar in behavior to DefaultErrorHandler).
 * </p>
 * @author duner, still
 * @version 2.0
 */
class LoggingErrorHandler implements ErrorHandler {
    /**
     * <p>
     * The Log instance used to perform logging of errors. This is set in the constructor and may be null to
     * indicate no logging.
     * </p>
     *
     */
    private Log log;

    /**
     * <p>
     * Simply assigns log to its corresponding attribute. log may be null to indicate that no logging need to be done.
     * </p>
     *
     *
     * @param log
     *            The Log instance used to perform logging of errors.
     */
    public LoggingErrorHandler(Log log) {
        this.log = log;
    }

    /**
     * <p>
     * Logs the warning to the log attribute using the WARN error level if log is not null.
     * log.log(Level.WARN, message); where message should contain the line number, column and the underlying
     * message. For instance: String message = "Line: " + exception.getLineNumber() + " Column: " +
     * exception.getColumnNumber() + " Message: " + exception.getMessage());
     * </p>
     *
     *
     * @param exception
     *            the SAXParseException that describes the parsing error that occured.
     * @throws NullPointerException
     *             if exception is null.
     */
    public void warning(SAXParseException exception) {
        LogException(log, Level.WARN, exception);
    }

    /**
     * <p>
     * Logs the error to the log attribute using the ERROR logging level if log is not null.
     * log.log(Level.ERROR, message); where message should contain the line number, column and the underlying
     * message. For instance: String message = "Line: " + exception.getLineNumber() + " Column: " +
     * exception.getColumnNumber() + " Message: " + exception.getMessage());
     * </p>
     *
     *
     * @param exception
     *            the SAXParseException that describes the parsing error that occured.
     * @throws NullPointerException
     *             if exception is null.
     */
    public void error(SAXParseException exception) {
        LogException(log, Level.ERROR, exception);
    }

    /**
     * <p>
     * Logs the warning to the log attribute using the FATAL logging level if log is not null.
     * log.log(Level.FATAL, message); where message should contain the line number, column and the underlying
     * message. For instance: String message = "Line: " + exception.getLineNumber() + " Column: " +
     * exception.getColumnNumber() + " Message: " + exception.getMessage());
     * </p>
     *
     *
     * @param exception
     *            the SAXParseException that describes the parsing error that occured.
     * @throws NullPointerException
     *             if exception is null.
     */
    public void fatalError(SAXParseException exception) {
        LogException(log, Level.FATAL, exception);
    }

    /**
         * <p>This is private helper method to log, it logs exception accoring to level if log is not null.
         * </p>
         * @param log
         *                         The Log instance used to perform logging of errors.
         * @param level indicates the level of log: waring error or fatel error.
         * @param exception the exception to be loged.
         */
    private static void LogException(Log log, Level level, SAXParseException exception) {
        SHHelper.checkNull(exception, "exception");

        if (log != null) {
            log.log(level, "Line: " + exception.getLineNumber() + " Column: " + exception.getColumnNumber()
                + " Message: " + exception.getMessage());
        }
    }
}
