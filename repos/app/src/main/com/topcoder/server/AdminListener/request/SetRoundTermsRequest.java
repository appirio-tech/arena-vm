package com.topcoder.server.AdminListener.request;
import com.topcoder.shared.netCommon.CSReader;
import com.topcoder.shared.netCommon.CSWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * This is a request to be sent to Admin Listener server to set the
 * content of terms for specified round.
 *
 * @author  TCSDESIGNER
 * @author  TCSDEVELOPER
 * @version 1.0 07/31/2003
 * @since   Admin Tool 2.0
 */
public class SetRoundTermsRequest extends ContestMonitorRequest implements ProcessedAtBackEndRequest {

    /**
     * An int representing ID of requested round
     */
    private int roundID = 0;

    /**
     * A Hashtable representing property names and values
     */
    private Map props;
    
    public SetRoundTermsRequest() {
        
    }
    
    public void customWriteObject(CSWriter writer) throws IOException {
        writer.writeInt(roundID);
        writer.writeHashMap(new HashMap(props));
    }
    
    public void customReadObject(CSReader reader) throws IOException {
        roundID = reader.readInt();
        props = reader.readHashMap();
    }

    /**
     * Constructs new SetRoundTermsRequest with specified round ID.
     *
     * @param  roundID an int representing ID of requested round
     * @param  props a Hashtable with terms template property names and
     *         values
     */
    public SetRoundTermsRequest(int roundID, Map props) {
        this.roundID = roundID;
        this.props = props;
    }

    /**
     * Gets the ID of requested round.
     *
     * @return an ID of requested round.
     */
    public int getRoundID() {
        return roundID;
    }

    /**
     * Gets the terms template properties that should be used to evaluate a
     * content of terms for specified round. The constructions like
     * "{property_name}" in terms.txt file should be replaced with value
     * corresponding to property name.
     *
     * @return a Hashtable with terms template property names and values
     */
    public Map getProperties() {
        return props;
    }
}
