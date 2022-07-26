/*
 * User: Mike Cervantes (emcee)
 * Date: May 17, 2002
 * Time: 3:12:08 PM
 */
package com.topcoder.server.AdminListener.response;
import com.topcoder.shared.netCommon.CSReader;
import com.topcoder.shared.netCommon.CSWriter;
import com.topcoder.shared.netCommon.CustomSerializable;
import java.io.IOException;
import java.io.Serializable;
import java.util.*;

public class RoundAccessItem implements CustomSerializable, Serializable {

    private int id = 0;
    private String name = "";
    private Date startDate = null;
    
    public RoundAccessItem() {
        
    }
    
    public void customWriteObject(CSWriter writer) throws IOException {
        writer.writeInt(id);
        writer.writeString(name);
        if(startDate == null)
            writer.writeLong(0);
        else
            writer.writeLong(startDate.getTime());
    }
    
    public void customReadObject(CSReader reader) throws IOException {
        id = reader.readInt();
        name = reader.readString();
        long l = reader.readLong();
        if(l == 0)
            startDate = null;
        else
            startDate = new Date(l);
    }

    public RoundAccessItem(int id, String name, Date startDate) {
        this.id = id;
        this.name = name;
        this.startDate = startDate;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public boolean equals(Object obj) {
        if (obj instanceof RoundAccessItem) {
            return id == ((RoundAccessItem) obj).getId();
        }
        return false;
    }
}
