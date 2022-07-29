package com.topcoder.netCommon.contestantMessages.response.data;

import java.io.IOException;
import java.io.ObjectStreamException;
import java.io.Serializable;

import com.topcoder.netCommon.contestantMessages.response.CreateCategoryListResponse;
import com.topcoder.shared.netCommon.CSReader;
import com.topcoder.shared.netCommon.CSWriter;
import com.topcoder.shared.netCommon.CustomSerializable;

/**
 * Defines a practice room category, such as 'SRM', 'Tournaments', 'Marathon', etc. The practice room category contains
 * an ID and a name. It is used in <code>CreateCategoryListResponse</code>.
 * 
 * @author Griffin Dorman
 * @version $Id: CategoryData.java 72424 2008-08-20 08:06:01Z qliu $
 * @see CreateCategoryListResponse
 */
public class CategoryData implements Serializable, CustomSerializable, Cloneable {
    /** Represents the ID of the practice room category. */
    int categoryID;

    /** Represents the name of the practice room category. */
    String categoryName;

    /**
     * Creates a new instance of <code>CategoryData</code>. It is required by custom serialization.
     */
    public CategoryData() {
    }

    /**
     * Creates a new instance of <code>CategoryData</code>.
     * 
     * @param categoryID the ID of the practice room category.
     * @param categoryName the name of the practice room category.
     */
    public CategoryData(int categoryID, String categoryName) {
        this.categoryID = categoryID;
        this.categoryName = categoryName;
    }

    public void customWriteObject(CSWriter csWriter) throws IOException {
        csWriter.writeInt(getCategoryID());
        csWriter.writeString(getCategoryName());
    }

    public void customReadObject(CSReader csReader) throws IOException, ObjectStreamException {
        setCategoryID(csReader.readInt());
        setCategoryName(csReader.readString());
    }

    /**
     * Gets the ID of the practice room category.
     * 
     * @return the category ID.
     */
    public int getCategoryID() {
        return categoryID;
    }

    /**
     * Gets the name of the practice room category.
     * 
     * @return the category name.
     */
    public String getCategoryName() {
        return categoryName;
    }

    /**
     * Sets the ID of the practice room category.
     * 
     * @param categoryID the category ID.
     */
    public void setCategoryID(int categoryID) {
        this.categoryID = categoryID;
    }

    /**
     * Sets the name of the practice room category.
     * 
     * @param categoryName the category name.
     */
    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    public String toString() {
        StringBuffer ret = new StringBuffer(1000);
        ret.append("(com.topcoder.netCommon.contestantMessages.response.data.CategoryData) [");
        ret.append("categoryID = ");
        ret.append(categoryID);
        ret.append(", ");
        ret.append("categoryName = ");
        ret.append(categoryName);
        ret.append("]");
        return ret.toString();
    }

}