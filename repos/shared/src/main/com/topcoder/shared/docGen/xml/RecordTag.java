package com.topcoder.shared.docGen.xml;

import java.util.*;

/**
 * @author Steve Burrows
 * @version  $Revision$
 */
public class RecordTag extends Tag {
    private String name;
    private ArrayList elementList;

    /**
     *
     */
    public RecordTag() {
        elementList = new ArrayList();
    }

    /**
     *
     * @param name
     */
    public RecordTag(String name) {
        elementList = new ArrayList();
        this.name = name;
    }

    /**
     *
     * @param name
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     *
     * @return
     */
    public String getName() {
        return this.name;
    }

    /**
     *
     */
    public void reset() {
        this.name = null;
        elementList.clear();
    }

    /**
     *
     * @param tag
     */
    public void addTag(Tag tag) {
        elementList.add(tag);
    }

    /**
     *
     * @return
     */
    public String getXML() {
        return createXML(true);
    }

    /**
     *
     * @param offSet
     * @return
     */
    public String getXML(int offSet) {
        String result = null;
        try {
            if (offSet > 0) {
                result = createXML(true, offSet);
            } else {
                result = createXML(true);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    public String getXML(boolean filter) {
        return createXML(filter);
    }

    public String getXML(boolean filter, int offSet) {
        return createXML(filter, offSet);
    }

    /**
     *
     * @param offSet
     * @return
     */
    protected String createXML(boolean filter, int offSet) {

        StringBuffer retVal = new StringBuffer(1000);
        try {
            String offSetString = createOffset(offSet);

            if (offSet > 0) {
                retVal.append(offSetString);
            }
            retVal.append("<");
            retVal.append(this.name);
            retVal.append(">\n");

            //Iterate through elementList appending the getXML outputs.
            for (int i = 0; i < elementList.size(); i++) {
                Tag tempTag = (Tag) elementList.get(i);
                retVal.append(tempTag.getXML(filter, offSet + 2));
            }

            if (offSet > 0) {
                retVal.append(offSetString);
            }
            retVal.append("</");
            retVal.append(this.name);
            retVal.append(">\n");
            offSetString = null;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return retVal.toString();
    }

    /**
     *
     * @return
     */
    protected String createXML(boolean filter) {

        StringBuffer retVal = new StringBuffer(1000);
        try {
            retVal.append("<");
            retVal.append(this.name);
            retVal.append(">\n");

            //Iterate through elementList appending the getXML outputs.
            for (int i = 0; i < elementList.size(); i++) {
                Tag tempTag = (Tag) elementList.get(i);
                retVal.append(tempTag.getXML(filter));
            }
            retVal.append("</");
            retVal.append(this.name);
            retVal.append(">\n");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return retVal.toString();
    }

    private String createOffset(int offSet) {
        StringBuffer offSetString = new StringBuffer();
        try {
            for (int i = 0; i < offSet; i++) {
                offSetString.append(" ");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return offSetString.toString();
    }


    /**
     **********************************************************************************
     * createErrorTag()
     * Creates a record tag for a generic error page.
     * @param message - the message
     * @return RecordTag - the tag we've created
     **********************************************************************************
     **/
    public static RecordTag createErrorTag(String message) {

        RecordTag result = new RecordTag("ERROR");

        if (message == null)
            result.addTag(new ValueTag("ErrorMessage", ""));
        else
            result.addTag(new ValueTag("ErrorMessage", message));

        return result;
    }


    /**
     *
     * @param tagName
     * @param attrList
     * @return
     * @throws Exception
     */
    public static final RecordTag getListXML(String tagName, List attrList)
            throws Exception {
        RecordTag result = null;
        try {
            result = new RecordTag(tagName);
            if (attrList != null) {
                for (int i = 0; i < attrList.size(); i++) {
                    TagRenderer attrObj = (TagRenderer) attrList.get(i);
                    try {
                        result.addTag(attrObj.getXML());
                    } catch (Exception e) {
                        StringBuffer msg = new StringBuffer(150);
                        msg.append("common.web.xml.RecordTag:getListXML:");
                        msg.append(tagName);
                        msg.append(":ERROR:\n");
                        msg.append(e);
                        throw new Exception(msg.toString());
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new Exception("common.web.xml.XMl:getListXML:ERROR:\n" + e);
        }
        return result;
    }

    /**
     *
     * @param tagName
     * @param attrCollection
     * @return
     * @throws Exception
     */
    public static final RecordTag getCollectionXML(String tagName, Collection attrCollection)
            throws Exception {
        RecordTag result = null;
        try {
            result = new RecordTag(tagName);
            if (attrCollection != null) {
                for (Iterator i = attrCollection.iterator(); i.hasNext();) {
                    TagRenderer attrObj = (TagRenderer) i.next();
                    try {
                        result.addTag(attrObj.getXML());
                    } catch (Exception e) {
                        StringBuffer msg = new StringBuffer(150);
                        msg.append("common.web.xml.RecordTag:getListXML:");
                        msg.append(tagName);
                        msg.append(":ERROR:\n");
                        msg.append(e);
                        throw new Exception(msg.toString());
                    }
                }
            }
        } catch (Exception e) {
            throw new Exception("common.web.xml.XMl:getCollectionXML:ERROR:\n" + e);
        }
        return result;
    }

}
