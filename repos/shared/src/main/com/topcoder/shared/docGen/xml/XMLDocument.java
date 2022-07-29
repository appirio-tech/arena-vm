package com.topcoder.shared.docGen.xml;


/**
 * @author Steve Burrows
 * @version  $Revision$
 */
public final class XMLDocument extends RecordTag {

    private String styleSheet;
    private static final String prependText = "<?xml version=\"1.0\" encoding=\"UTF-8\" ?>\n<?cocoon-process type=\"xslt\"?>\n";


    /**
     *
     * @param name
     */
    public XMLDocument(String name) {
        super(name);
        this.styleSheet = "";
    }

    /**
     *
     * @param styleSheet
     */
    public void setStyleSheet(String styleSheet) {
        this.styleSheet = styleSheet;
    }


    public String getXML() {
        return prependText+super.getXML();
    }

    public String getXML(int offSet) {
        return prependText+super.getXML(offSet);
    }

    public String getXML(boolean filter) {
        return prependText+super.getXML(filter);
    }

    public String getXML(boolean filter, int offSet) {
        return prependText+super.getXML(filter, offSet);
    }


    /**
     *
     * @param offSet
     * @return
     * @deprecated
     */
    protected String createXML(int offSet) {
        StringBuffer preText = new StringBuffer(96);
        preText.append(prependText);

        if (offSet > 0) {
            preText.append(super.createXML(true, offSet));
        } else {
            preText.append(super.createXML(true));
        }

        return preText.toString();

    }

    /**
     *
     * @return
     * @deprecated
     */
    public String createXML() {
        StringBuffer preText = new StringBuffer(96);
        preText.append(prependText);

        preText.append(super.createXML(true));

        return preText.toString();

    }

}
