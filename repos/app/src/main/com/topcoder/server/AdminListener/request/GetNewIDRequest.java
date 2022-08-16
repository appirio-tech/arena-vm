package com.topcoder.server.AdminListener.request;

import com.topcoder.shared.netCommon.CSReader;
import com.topcoder.shared.netCommon.CSWriter;
import com.topcoder.shared.util.DBMS;
import java.io.IOException;

/**
 * This is a request to be sent to Admin Listener server to get new ID from
 * specified sequence. Such requests should be sent from any requestor that
 * wants to create new round or contest, for example. Starting from Admin Tool 
 * 2.0 IDs for contest and rounds are maintained by new sequences added to 
 * COMMON_OLTP.SEQUENCE_OBJECT table named CONTEST_SEQ and ROUND_SEQ 
 * and with IDs corresponding to AdminConstants.CONTEST_SEQ and
 * AdminConstants.ROUND_SEQ variables respectively.
 *
 * Copyright (c) 2003 TopCoder, Inc.  All rights reserved.
 * 
 * @author  TCSDESIGNER
 * @version 1.0 11/03/2003
 * @since   Admin Tool 2.0
 */
public class GetNewIDRequest extends ContestManagementRequest {

    /**
     * An ID of sequence that should be used to generate new ID.
     */
    private String sequence;
    
    public GetNewIDRequest() {
        
    }
    
    public void customWriteObject(CSWriter writer) throws IOException {
        writer.writeString(sequence);
    }
    
    public void customReadObject(CSReader reader) throws IOException {
        sequence = reader.readString();
    }

    /**
     * Constructs new GetNewIDRequest with specified ID of sequence that 
     * should be used to generate new ID.
     *
     * @param  sequence an ID of sequence that should be used to generate new
     *         ID.
     * @throws IllegalArgumentException if given sequence is not equal to any
     *         of DBMS.*_SEQ constants.
     * @see    DBMS.*_SEQ
     */
    public GetNewIDRequest(String sequence) {
    	String[] validSeq = {		
			DBMS.JMA_SEQ, DBMS.PROBLEM_SEQ, DBMS.CHALLENGE_SEQ, 
            DBMS.COMPONENT_STATE_SEQ, DBMS.SURVEY_SEQ, DBMS.SERVER_SEQ,
            DBMS.ROOM_SEQ, DBMS.REQUEST_SEQ, DBMS.BROADCAST_SEQ, 
            DBMS.PARAMETER_SEQ, DBMS.COMPONENT_SEQ, 
            DBMS.CONTEST_SEQ, DBMS.ROUND_SEQ, DBMS.PAYMENT_SEQ, 
			DBMS.WEB_SERVICE_SEQ, DBMS.WEB_SERVICE_SOURCE_FILE_SEQ, 
			DBMS.WEB_SERVICE_JAVA_DOC_SEQ, DBMS.MAIN_SEQ, DBMS.MESSAGE_SEQ };
		boolean found = false;
		for( int i=0; !found && i < validSeq.length; i++ ) if( sequence.equals(validSeq[i]) ) found = true;
		if( !found ) {
			throw new IllegalArgumentException("Sequence is invalid: " + sequence );
		}
    	this.sequence = sequence;
    }

    /**
     * Gets the ID of sequence that should be used to generate new ID. Returned
     * value will be one of DBMS.*_SEQ constants.
     *
     * @return an ID of sequence to be used to generate new ID.
     */
    public String getSequenceID() {
        return sequence;
    }
}
