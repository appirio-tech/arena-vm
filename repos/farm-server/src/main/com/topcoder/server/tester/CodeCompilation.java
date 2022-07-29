/*
* Copyright (C) - 2014 TopCoder Inc., All Rights Reserved.
*/
package com.topcoder.server.tester;

/**
 * <p>
 * Changes in version 1.1 (Return Time Infos When Opening Problems v1.0) :
 * <ol>
 *      <li>Add {@link #getCompileStatus()} method.</li>
 * </ol>
 * </p>
 * @author TCSASSMEBLER
 * @since 1.1
 */
public interface CodeCompilation {
    public void setCompileError(String s);
    public int getComponentID();
    public int getCoderID();
    public String getProgramText();
    public void setClassFiles(ComponentFiles in);
    public ComponentFiles getClassFiles();
    public void setCompileStatus(boolean b);
    public int getRoundID();
    public int getLanguage();
    /**
     * Get the compilation status.
     * @return the compilation status.
     * @since 1.1
     */
    public boolean getCompileStatus();
}
