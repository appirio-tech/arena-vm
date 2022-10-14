package com.topcoder.netCommon.contestantMessages.request;

import java.io.IOException;

import com.topcoder.netCommon.contest.ContestConstants;
import com.topcoder.shared.netCommon.CSReader;
import com.topcoder.shared.netCommon.CSWriter;

/**
 * Defines a request to locate a user in the arena (in which room).<br>
 * Use: The current user may want to go with his friend, but he does not know where his friend is. Here comes this
 * request.<br>
 * Note: The user to be located may not be logged in. It will not cause error.
 * 
 * @author Walter Mundt
 * @version $Id: SearchRequest.java 72292 2008-08-12 09:10:29Z qliu $
 */
public class SearchRequest extends BaseRequest {
    /** Represents the handle of the user to be located. */
    protected String search;

    /**
     * Creates a new instance of <code>SearchRequest</code>. It is required by custom serialization.
     */
    public SearchRequest() {
    }

    /**
     * Creates a new instance of <code>SearchRequest</code>.
     * 
     * @param search the handle of the user to be located.
     */
    public SearchRequest(String search) {
        this.search = search;
    }

    public int getRequestType() {
        return ContestConstants.SEARCH;
    }

    public void customWriteObject(CSWriter writer) throws IOException {
        super.customWriteObject(writer);
        writer.writeString(search);
    }

    public void customReadObject(CSReader reader) throws IOException {
        super.customReadObject(reader);
        search = reader.readString();
    }

    /**
     * Gets the handle of the user to be located.
     * 
     * @return the handle of the user.
     */
    public String getSearch() {
        return search;
    }

    public String toString() {
        StringBuffer ret = new StringBuffer(1000);
        ret.append("(com.topcoder.netCommon.contestantMessages.request.SearchRequest) [");
        ret.append("search = ");
        if (search == null) {
            ret.append("null");
        } else {
            ret.append(search.toString());
        }
        ret.append(", ");
        ret.append("]");
        return ret.toString();
    }
}
