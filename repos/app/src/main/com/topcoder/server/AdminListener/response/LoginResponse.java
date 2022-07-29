package com.topcoder.server.AdminListener.response;
import com.topcoder.security.policy.GenericPermission;
import com.topcoder.shared.netCommon.CSReader;
import com.topcoder.shared.netCommon.CSWriter;
import com.topcoder.shared.netCommon.CustomSerializable;
import java.io.IOException;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;


/**
 * Modifications for AdminTool 2.0 are : 
 * <p>Private variable <code>userId</code> becomes deprecated due to modifications
 * of existing security schema.
 *
 * <p>New private instance variable of type TCSubject is added.
 * 
 * <p>The content of <code>allowedFunctions</code> Set is changed.
 *
 * <p>Existing constructor becomes deprecated. New constructor accepting TCSubject
 * is added.
 *
 * <p>New method <code>getTCSubject()</code> added.
 * 
 * @author TCDEVELOPER
 */
public class LoginResponse implements CustomSerializable, Serializable {

    /**
     * Starting from Admin Tool 2.0 this variable becomes deprecated. An ID
     * of user should be obtained with TCSubject.getUserId() method.
     *
     * @deprecated
     * @see  TCSubject#getuserId()
     */
    private long userId;
    
    /**
     * true if login was sucessful, false if not
     */
    private boolean succeeded;
    
    /**
     * as of AdminTool 2.0, the content of returned Set is changed. The returned 
     * Set contains TCPermission objects representing permissions granted to 
     * TCSubject that was successfully authenticated instead of Integers 
     * representing IDs of requests.
     */
    private Set allowedFunctions;
    
    public void customWriteObject(CSWriter writer) throws IOException {
        writer.writeLong(userId);
        writer.writeBoolean(succeeded);
        //the array is of generic permission objects.  We can't make a override
        //for these, so we do it manually
        writer.writeInt(allowedFunctions.size());
        for(Iterator i = allowedFunctions.iterator(); i.hasNext();) {
            GenericPermission gp = (GenericPermission)i.next();
            writer.writeString(gp.getName());
        }       
    }
    
    public void customReadObject(CSReader reader) throws IOException {
        userId = reader.readLong();
        succeeded = reader.readBoolean();
        int sz = reader.readInt();
        allowedFunctions = new HashSet();
        for(int i = 0; i < sz; i++) {
            String h = reader.readString();
            allowedFunctions.add(new GenericPermission(h));
        }
        
    }
    
    public LoginResponse() {
        
    }
    
    /**
     * Starting from Admin Tool 2.0 new constructor accepting TCSubject should
     * be used to construct instances of LoginResponse.
     *
     * 
     */
    public LoginResponse(long userId, boolean succeeded, Set allowedFunctions) {
        this.userId = userId;
        this.succeeded = succeeded;
        this.allowedFunctions = allowedFunctions;
    }

    /**
     * Starting from Admin Tool 2.0 this method should return a user ID 
     * obtained from TCSubject object that this LoginResponse is constructed 
     * with.
     *
     * @see TCSubject#getUserId()
     */
    public long getUserId() {
        return userId;
    }

    /**
     * This method will return true if this response represents a successful
     * login, false otherwise.
     * @return the status of the login request
     */
    public boolean getSucceeded() {
        return succeeded;
    }
    
   /**
    * The content of returned Set is changed. Starting from Admin Tool 2.0
    * returned Set contains TCPermission objects representing permissions
    * granted to TCSubject that was successfully authenticated instead of
    * Integers representing IDs of requests.
    */
    public Set getAllowedFunctions() {
        return allowedFunctions;
    }
}
