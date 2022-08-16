package com.topcoder.server.tester;




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
}
