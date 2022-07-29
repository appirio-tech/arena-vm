/*
 * ReaderWriter
 * 
 * Created 09/04/2006
 */
package com.topcoder.farm.shared.commandline;

import java.io.IOException;
import java.io.PrintStream;

/**
 * 
 * @author Diego Belfer (Mural)
 * @version $Id$
 */
public interface ReaderWriter {
    /**
     * Initializes this ReaderWriter 
     * 
     * @throws IOException If this ReaderWriter could not be initialized due to an IO exception
     */
    public void initialize() throws IOException;
    
    /**
     * Releases this ReaderWriter
     */
    public void release();
    
    /**
     * Reads a line
     *  
     * @return The line content
     * @throws IOException If the line could not be read dur to an IO exception
     */
    public String readLine() throws IOException;
    
    /**
     * Returns the printStream for this instance
     * 
     * @return The PrintStream
     */
    public PrintStream getPrintStream();
    
    /**
     * Resets the IO when it is possible. 
     * For instance: a socket close current session an start listening for a new session
     *
     */
    public void reset();
}