/*
 * ConsoleReaderWriter
 * 
 * Created 09/04/2006
 */
package com.topcoder.farm.shared.commandline;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;

/**
 * ReaderWriter that operates using stdin and stdout
 *   
 * @author Diego Belfer (Mural)
 * @version $Id$
 */
public class ConsoleReaderWriter implements ReaderWriter {
    private BufferedReader reader;
    private PrintStream printStream;
    private CommandLineInterpreter interpreter;
    
    public ConsoleReaderWriter(CommandLineInterpreter interpreter) {
        this.interpreter = interpreter;
        reader = new BufferedReader(new InputStreamReader(System.in));
        printStream = System.out;
    }
    
    public void initialize() {
        interpreter.newSessionStarted();
    }
    
    public String readLine() throws IOException {
        return reader.readLine();
    }

    public void release() {
        interpreter.sessionFinished();
        reader = null;
        printStream = null;
    }
    
    public PrintStream getPrintStream() {
        return printStream;
    }
    
    public void reset() {
    }
}