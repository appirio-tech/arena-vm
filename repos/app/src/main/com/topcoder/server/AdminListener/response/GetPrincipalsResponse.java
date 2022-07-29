package com.topcoder.server.AdminListener.response;
import com.topcoder.security.GroupPrincipal;
import com.topcoder.security.RolePrincipal;
import com.topcoder.shared.netCommon.CSReader;
import com.topcoder.shared.netCommon.CSWriter;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;

/**
 * A response to request for list of coders registered for specified round
 * Instances of this class are created by 
 * <code>com.topcoder.server.AdminListener.ContestManagementProcessor</code>
 * in response to <code>GetPrincipalsRequest</code> and contain the 
 * <code>Collection</code> containing CoderHandleIDPair objects representing 
 * the coders registered for specified round.
 *
 * @author  TCSDESIGNER
 * @version 1.0 07/31/2003
 * @since   Admin Tool 2.0
 */
public class GetPrincipalsResponse extends ContestManagementAck {

    /**
     * A Collection of TCPrincipals of requested type.
     * 
     * @see TCPrincipal
     * @see GroupPrincipal
     * @see RolePrincipal
     */
    private Collection principals = null;                               

    /**
     * An int representing the type of returned principals.
     */
    private int type = 0;
    
    public GetPrincipalsResponse() {
        
    }
    
    public void customWriteObject(CSWriter writer) throws IOException {
        super.customWriteObject(writer);
        writer.writeInt(type);
        writer.writeInt(principals.size());
        for(Iterator i = principals.iterator(); i.hasNext(); ) {
            Object o = i.next();
            if(o instanceof GroupPrincipal) {         
                writer.writeInt(0);
                GroupPrincipal gp = (GroupPrincipal)o;
                writer.writeString(gp.getName());
                writer.writeLong(gp.getId());
            } else if (o instanceof RolePrincipal) {
                writer.writeInt(1);
                RolePrincipal rp = (RolePrincipal)o;
                writer.writeString(rp.getName());
                writer.writeLong(rp.getId());                
            }
        }
    }
    
    public void customReadObject(CSReader reader) throws IOException {
        super.customReadObject(reader);
        type = reader.readInt();
        int sz = reader.readInt();
        Object[] gp = new Object[sz];
        for(int i = 0; i < gp.length; i++) {
            int type = reader.readInt();
            if(type == 0) {
                gp[i] = new GroupPrincipal(reader.readString(), reader.readLong());
            } else {
                gp[i] = new RolePrincipal(reader.readString(), reader.readLong());
            }
        }
        principals = Arrays.asList(gp);
    }

    /**
     * Constructs new GetPrincipalsResponse with specified Collection of
     * TCPrincipals corresponding to specified type.
     *
     * @param  type an int representing the type of returned principals
     * @param  principals a Collection of TCPrincipals of requested type
     * @throws IllegalArgumentException if given Collection is null
     */
    public GetPrincipalsResponse(int type, Collection principals) {
        if( principals == null ) 
            throw new IllegalArgumentException("principals cannot be null");
        this.type = type;
        this.principals = principals;
    }

    /**
     * Constructs new instance of GetPrincipalsResponse specifying the
     * Throwable representing the exception or error occured preventing
     * the successful fulfilment of request.
     *
     * @param errorDetails a Throwable occured during the fulfilment of 
     *        request
     */
    public GetPrincipalsResponse(Throwable errorDetails) {
        super(errorDetails);
    }

    /**
     * Gets the Collection of TCPrincipals of requested type.
     *
     * @return a Collection of TCPrincipals (either GroupPrincipals or 
     *         RolePrincipals)
     */
    public Collection getPrincipals() {
        return principals;
    }

    /**
     * Gets the type of returned principals.
     *
     * @return an int representing the type of returned principals, either
     *         AdminConstants.GROUP_PRINCIPALS or AdminConstants.ROLE_PRINCIPALS
     */
    public int getType() {
        return type;
    }
}
