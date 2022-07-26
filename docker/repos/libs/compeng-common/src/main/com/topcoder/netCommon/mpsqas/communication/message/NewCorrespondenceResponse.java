package com.topcoder.netCommon.mpsqas.communication.message;

import com.topcoder.netCommon.mpsqas.*;

/**
 *
 * @author Logan Hanks
 */
public class NewCorrespondenceResponse
        extends CorrespondenceMessage {

    public NewCorrespondenceResponse() {
    }

    public NewCorrespondenceResponse(Correspondence correspondence) {
        super(correspondence);
    }
}

