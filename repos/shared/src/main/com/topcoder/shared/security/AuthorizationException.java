package com.topcoder.shared.security;

import java.io.PrintWriter;
import java.io.PrintStream;

/**
 *
 * @author Fred Wang (silentmobius)
 * @author Grimicus
 * @version $Revision$
 * Jan 15, 2003 6:30:49 PM
 */

public class AuthorizationException extends Exception {

   /** 
     * Line separator for the local OS
     */
    private static final String SEPARATOR = 
        System.getProperty("line.separator");

    /** 
     * String constant for delimiting this exception and its nested one. 
     */
    private static final String NESTED_STRING = 
        SEPARATOR + SEPARATOR + " NESTED WITHIN: ";

    /**
     * The nested exception.  The exception that this class is wrapped around.
     */
    private Throwable nestedException;

    /**
     * Default Constructor
     */
    public AuthorizationException()
    {
        super();
    }
    
    /**
     * <p>
     * Constructor taking a string message
     * </p>
     *
     * @param message - the message of the exception
     */
    public AuthorizationException(String message)
    {
        super(message);
    }

    /**
     * <p>
     * Constructor taking a nested exception
     * </p>
     *
     * @param nestedException the nested exception
     */
    public AuthorizationException(Throwable nestedException)
    {
        super();
        this.nestedException = nestedException;
    }

    /**
     * <p>
     * Constructor taking a nested exception and a string
     * </p>
     *
     * @param message the message of this exception
     * @param nestedException the nested exception
     */
    public AuthorizationException(String message, Throwable nestedException)
    {
        super(message);
        this.nestedException = nestedException;
    }

    /** 
     *  Set to an exception that should be nested
     *  
     * @param val The throwable to be nested
     */
    protected void setNestedException(Throwable val)
    {
        nestedException = val;
    }
    
    /**
     * <p>
     * Gets the nested exception
     * </p>
     *
     * @return Throwable the nested exception
     */    
    public Throwable getNestedException()
    {
        return nestedException;
    }

    /**
     * <p>
     * Prints the message for this exception including any nested exceptions.
     * Nested Exception messages are printed at the top to show the deepest
     * error first.
     * </p>
     * 
     * @return The string holding the messages of all nested exceptions and 
     *         this one.
     */
    public String getNestedMessage()
    {
        String output = null;
        String msg = null;
        String msg2 = super.getMessage();
        
        if (nestedException != null)
        {
            msg = nestedException.getMessage();
        }

        if (msg != null)
        {
            if (msg2 != null)
            {
                output = msg + NESTED_STRING + msg2;
            }
            else
            {
                output = msg;
            }
        }
        else
        {
            if (msg2 != null)
            {
                output = msg2;
            }
            else
            {
                output = null;
            }
        }

        return output;
    }

    /**
     * Prints the stack trace to standard error
     */
    public void printStackTrace()
    {
        printStackTrace(System.err);
    }

    /**
     * Prints the given stack trace to a given PrintWriter object.
     * Nested Exception's stack trace is printed first so that the
     * deepest exception (i.e. the actual error), is shown first.
     *
     * @param writer PrintWriter containing stack trace
     */
    public void printStackTrace(PrintWriter writer)
    {
        if (nestedException != null)
        {
            nestedException.printStackTrace(writer);
            writer.println(NESTED_STRING);
        }
        super.printStackTrace(writer);
    }

    /**
     * Prints the given stack trace to a PrintStream object.
     * Nested Exception's stack trace is printed first so that the
     * deepest exception (i.e. the actual error), is shown first.
     *
     * @param stream PrintStream containing stack trace
     */
    public void printStackTrace(PrintStream stream)
    {
        if (nestedException != null)
        {
            nestedException.printStackTrace(stream);
            stream.println(NESTED_STRING);
        }
        super.printStackTrace(stream);
    }

}
