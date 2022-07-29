package com.topcoder.server.AdminListener.request;

import com.topcoder.server.AdminListener.AdminConstants;
import com.topcoder.shared.netCommon.CSReader;
import com.topcoder.shared.netCommon.CSWriter;
import java.io.IOException;

/**
 * This is a request to be sent to Admin Listener server to get the list
 * of coders registered for specified round.
 *
 * @author  TCSDESIGNER
 * @version 1.0 07/31/2003
 * @since   Admin Tool 2.0
 */
public class GetPrincipalsRequest extends ContestMonitorRequest implements ProcessedAtBackEndRequest {

    /**
     * An int representing a type of principals to get
     */
    private int type = 0;
    
    public GetPrincipalsRequest() {
        
    }
    
    public void customWriteObject(CSWriter writer) throws IOException {
        writer.writeInt(type);
    }
    
    public void customReadObject(CSReader reader) throws IOException {
        type = reader.readInt();
    }


    /**
     * Constructs new GetPrincipalsRequest with specified type of principals
     * to get.
     *
     * @param  type an int representing the type of principals to get. Should
     *         be either AdminConstants.GROUP_PRINCIPALS or AdminConstants.ROLE_PRINCIPALS
     * @throws IllegalArgumentException if given argument contains not valid 
     *         value
     */
    public GetPrincipalsRequest(int type) {
        if( type != AdminConstants.GROUP_PRINCIPALS &&
            type != AdminConstants.ROLE_PRINCIPALS) {
            throw new IllegalArgumentException( "invalid type = " + type );
        }
        this.type = type;
    }

    /**
     * Gets the type of principals to return.
     *
     * @return a type of principals to return, either GROUP_PRINCIPALS or
     *         ROLE_PRINCIPALS
     */
    public int getType() {
        return type;
    }
}
