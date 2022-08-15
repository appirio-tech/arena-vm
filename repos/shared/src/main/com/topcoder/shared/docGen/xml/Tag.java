package com.topcoder.shared.docGen.xml;

abstract class Tag {

    abstract String getXML();

    abstract String getXML(int offSet);

    abstract String getXML(boolean filter);

    abstract String getXML(boolean filter, int offSet);

}
