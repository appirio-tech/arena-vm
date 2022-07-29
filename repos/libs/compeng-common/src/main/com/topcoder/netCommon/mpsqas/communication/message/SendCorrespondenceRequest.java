package com.topcoder.netCommon.mpsqas.communication.message;

import com.topcoder.netCommon.mpsqas.*;

/**
 *
 * @author Logan Hanks
 */
public class SendCorrespondenceRequest
        extends CorrespondenceMessage {

    public SendCorrespondenceRequest(Correspondence correspondence) {
        super(correspondence);
    }

    public SendCorrespondenceRequest() {
    }
}
