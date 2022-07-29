/*
 * Copyright (C) 2014 TopCoder Inc., All Rights Reserved.
 */

package com.topcoder.netCommon.contestantMessages.request;

import java.io.IOException;

import com.topcoder.shared.netCommon.CSReader;
import com.topcoder.shared.netCommon.CSWriter;


/**
 * Defines a request of the list of rounds the user is registered to.<br>
 * Use: This request is sent when the current user wants to get the list of
 * all rounds he is registered to.<br>
 * Note: Only active rounds will be returned.
 *
 * @author dexy
 * @version 1.0
 */
public class RegisteredRoundListRequest extends BaseRequest {
    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = -5499185516665022164L;

    /** The Constant TOSTRING_BUFFER_LENGTH. */
    private static final int TOSTRING_BUFFER_LENGTH = 1000;

    /**
     * Creates a new instance of <code>RegisteredRoundListRequest</code>.
     * It is required by custom serialization.
     */
    public RegisteredRoundListRequest() {
    }

    /* (non-Javadoc)
     * @see com.topcoder.shared.netCommon.messages.Message#customWriteObject(com.topcoder.shared.netCommon.CSWriter)
     */
    @Override
    public void customWriteObject(CSWriter writer) throws IOException {

    }

    /* (non-Javadoc)
     * @see com.topcoder.shared.netCommon.messages.Message#customReadObject(com.topcoder.shared.netCommon.CSReader)
     */
    @Override
    public void customReadObject(CSReader reader) throws IOException {

    }

    /* (non-Javadoc)
     * @see com.topcoder.netCommon.contestantMessages.request.BaseRequest#toString()
     */
    @Override
    public String toString() {
        StringBuffer ret = new StringBuffer(TOSTRING_BUFFER_LENGTH);
        ret.append("(");
        ret.append(getClass().getName());
        ret.append(") [");
        ret.append("]");
        return ret.toString();
    }

    /* (non-Javadoc)
     * @see com.topcoder.netCommon.contestantMessages.request.BaseRequest#getRequestType()
     */
    @Override
    public int getRequestType() {
        return 0;
    }
}