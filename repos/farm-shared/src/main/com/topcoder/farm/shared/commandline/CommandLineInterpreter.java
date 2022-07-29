/*
 * CommandLineInterpreter
 * 
 * Created 08/18/2006
 */
package com.topcoder.farm.shared.commandline;


import java.io.FilterOutputStream;
import java.io.PrintStream;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.log4j.Logger;
import org.apache.log4j.PatternLayout;
import org.apache.log4j.WriterAppender;


/**
 * Reads commands using a ReaderWriter object and processes
 * them using the CommandLineProcessor given during creation.<p>
 * 
 * The ReaderWriter used is determined by system properties: <p>
 * {@link CommandLineInterpreter#IO_MODE_KEY}, values allowed are:
 * <code>console</code>, <code>socket</code>, <code>auto</code>. 
 * If <code>socket </code>is specified the IO_MODE_PORT_KEY system property
 * should contain the port on which the CommandLineInterpreter will listen. 
 * If  the port is not set DEFAULT_PORT will be used.
 * When <code>console</code> is specified as mode, stdin and std out are used.
 * 
 * If none mode is specified <code>auto</code> is assumed, <code>console</code> mode will be selected
 * if System.in is available, otherwise socket will be chosen. 
 *  
 * @author Diego Belfer (Mural)
 * @version $Id$
 */
public class CommandLineInterpreter extends Thread {
    public static final String IO_MODE_KEY = "com.topcoder.commandline.io";
    public static final String IO_MODE_PORT_KEY  = "com.topcoder.commandline.io.port";
    public static final String IO_MODE_CONSOLE = "console";
    public static final String IO_MODE_SOCKET  = "socket";
    public static final String IO_MODE_AUTO    = "auto";
    public static final int DEFAULT_PORT = 15658;
    private Log log = LogFactory.getLog(CommandLineInterpreter.class);
    
    private final CommandLineProcessor processor;
    private ReaderWriter io;
    
    public CommandLineInterpreter(CommandLineProcessor processor) {
        super("cmd-reader");
        this.processor = processor;
        setDaemon(true);
        configureReaderWriter();
    }

    private void configureReaderWriter() {
        String mode = System.getProperty(IO_MODE_KEY, IO_MODE_AUTO);
        if (IO_MODE_AUTO.equals(mode)) {
            mode = detectMode();
        }
        if (IO_MODE_CONSOLE.equals(mode)) {
            log.info("Commmand line listening using stdio");
            io = new ConsoleReaderWriter(this);
        } else {
            io = new SocketReaderWriter(this, resolvePort());
        }
        try {
            io.initialize();
        } catch (Exception e) {
            io = null;
        }
    }
    

    private int resolvePort() {
        String portStr = System.getProperty(IO_MODE_PORT_KEY);
        int port = DEFAULT_PORT;
        if (portStr == null) {
            log.info("Port is not configured, using default");
            port = DEFAULT_PORT;
        } else {
            try {
                port = Integer.parseInt(portStr);
            } catch (NumberFormatException e) {
                log.info("Invalid port configured, using default");
                port = DEFAULT_PORT;
            }
        }
        log.info("Commmand line listening on port: "+port);
        return port;
    }

    private String detectMode() {
        if (System.getProperty("javawebstart.version") == null) {
            return "console";
        } else {
            return "socket";
        }
    }

    public void run() {
        if (io != null) {
            while (true) {
                try {
                    display("command>");
                    String line = io.readLine();
                    WriterAppender appender = buildAppender();
                    Logger.getRootLogger().addAppender(appender);
                    try {
                        if (line != null) {
                            final String cmd = line.trim();
                            if ("?".equals(cmd)) {
                                processor.displayCommands(io.getPrintStream());
                            } else if ("quit".equals(cmd)) {
                                display("bye. have a nice day");
                                io.reset();
                            } else {
                                try {
                                    if (!processor.process(cmd, io.getPrintStream())) {
                                        display("Unknown command: "+cmd);
                                    }
                                } catch (Exception e) {
                                    io.getPrintStream().println("Exception while processing command: "+ cmd);
                                    e.printStackTrace(io.getPrintStream());
                                }
                            }
                        }
                    } finally {
                        Logger.getRootLogger().removeAppender(appender);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private WriterAppender buildAppender() {
        return new WriterAppender(new PatternLayout("%m%n"), new FilterOutputStream(io.getPrintStream()) {
            public void close() {
            }
        });
    }
    
    private void displayCommands() {
        try {
            PrintStream printStream = io.getPrintStream();
            if (printStream != null) {
                processor.displayCommands(printStream);
            }
        } catch (Exception e) {
        }
    }
    
    private void display(String text) {
        try {
            PrintStream printStream = io.getPrintStream();
            if (printStream != null) {
                printStream.println(text);
            }
        } catch (Exception e) {
        }
    }
    
    
    public void newSessionStarted() {
        displayCommands();
    }

    public void sessionFinished() {
    }
}