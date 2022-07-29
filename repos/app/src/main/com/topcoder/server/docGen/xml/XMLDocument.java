package com.topcoder.server.docGen.xml;

import java.io.*;
import java.util.*;

public final class XMLDocument extends RecordTag {

    private String styleSheet;
    private static final String prependText = "<?xml version=\"1.0\" ?>\n<?cocoon-process type=\"xslt\"?>\n";

    ////////////////////////////////////////////////////////////////////////////////
    public XMLDocument(String name)
            ////////////////////////////////////////////////////////////////////////////////
    {
        super(name);
        this.styleSheet = "";
    }

    ////////////////////////////////////////////////////////////////////////////////
    public void setStyleSheet(String styleSheet)
            ////////////////////////////////////////////////////////////////////////////////
    {
        this.styleSheet = styleSheet;
    }

    ////////////////////////////////////////////////////////////////////////////////
    protected String createXML(int offSet)
            ////////////////////////////////////////////////////////////////////////////////
    {
        StringBuffer preText = new StringBuffer(96);
        preText.append(prependText);

        if (offSet > 0) {
            preText.append(super.createXML(offSet));
        } else {
            preText.append(super.createXML());
        }

        return preText.toString();

    }

    ////////////////////////////////////////////////////////////////////////////////
    public String createXML()
            ////////////////////////////////////////////////////////////////////////////////
    {
        StringBuffer preText = new StringBuffer(96);
        preText.append(prependText);

        preText.append(super.createXML());

        return preText.toString();

    }

}
