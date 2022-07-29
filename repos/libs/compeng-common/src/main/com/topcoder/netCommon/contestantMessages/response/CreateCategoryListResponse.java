package com.topcoder.netCommon.contestantMessages.response;

import java.io.IOException;
import java.io.ObjectStreamException;

import com.topcoder.netCommon.contestantMessages.response.data.CategoryData;
import com.topcoder.shared.netCommon.CSReader;
import com.topcoder.shared.netCommon.CSWriter;

/**
 * Defines a response to tell the client to replace the practice room categories by the categories in this response.<br>
 * Use: After logging in or refreshing the practice room list, this response is sent containing the most recent practice
 * room categories. The client should replaces the existing practice room categories by the categories in this response.
 * 
 * @author Griffin Dorman
 * @version $Id: CreateCategoryListResponse.java 72300 2008-08-13 08:33:29Z qliu $
 */
public class CreateCategoryListResponse extends BaseResponse {
    /** Represents the practice room categories. */
    private CategoryData[] categories;

    /**
     * Creates a new instance of <code>CreateCategoryListResponse<code>. It is required by custom serialization.
     */
    public CreateCategoryListResponse() {
    }

    /**
     * Creates a new instance of <code>CreateCategoryListResponse</code>. There is no copy.
     * 
     * @param categories the current practice room categories.
     */
    public CreateCategoryListResponse(CategoryData[] categories) {
        this.categories = categories;
    }

    /**
     * Gets the practice room categories used by the client. There is no copy.
     * 
     * @return the practice room categories.
     */
    public CategoryData[] getCategories() {
        return categories;
    }

    public void customReadObject(CSReader csReader) throws IOException, ObjectStreamException {
        super.customReadObject(csReader);
        categories = (CategoryData[]) csReader.readObjectArray(CategoryData.class);
    }

    public void customWriteObject(CSWriter csWriter) throws IOException {
        super.customWriteObject(csWriter);
        csWriter.writeObjectArray(getCategories());
    }

    public String toString() {
        StringBuffer ret = new StringBuffer(1000);
        ret.append("(com.topcoder.netCommon.contestantMessages.response.CreateCategoryListResponse) [");
        ret.append("roundCategories = ");
        if (categories == null) {
            ret.append("null");
        } else {
            ret.append("{");
            for (int i = 0; i < categories.length; i++) {
                ret.append(categories[i].toString() + ",");
            }
            ret.append("}");
        }
        ret.append(", ");
        ret.append("]");
        return ret.toString();
    }
}
