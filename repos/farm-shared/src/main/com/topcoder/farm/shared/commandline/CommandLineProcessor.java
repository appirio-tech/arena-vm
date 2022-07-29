/*
 * CommandLineProcessor
 * 
 * Created 09/04/2006
 */
package com.topcoder.farm.shared.commandline;

import java.io.PrintStream;

/**
 * Plugable command line processor for the CommandLineInterpreter
 * 
 * 
 * @author Diego Belfer (mural)
 * @version $Id$
 */
public interface CommandLineProcessor {

    /**
     * Process the give command <code>cmd</code>
     * 
     * @param cmd The command string to process
     * @param writer The PrintStream to report information to the user requesting the cmd
     * @return true if the command was processed
     * @throws Exception If an exception was thrown during command processing
     */
    boolean process(String cmd, PrintStream writer) throws Exception;

    /**
     * Display the list of all available commands
     * 
     * @param printStream the printStream where to write information
     */
    void displayCommands(PrintStream printStream);

}
