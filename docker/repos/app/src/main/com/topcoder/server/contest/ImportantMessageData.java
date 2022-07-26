package com.topcoder.server.contest;

import com.topcoder.shared.netCommon.CSReader;
import com.topcoder.shared.netCommon.CSWriter;
import com.topcoder.shared.netCommon.CustomSerializable;
import java.io.IOException;
import java.io.Serializable;
import java.util.Date;


public class ImportantMessageData implements CustomSerializable, Serializable {

    private int id = 0;
    private String message = "";
    private Date startDate = new Date();
    private Date endDate = new Date();
    private int status = 1;
    private long user_id = 0;
    private long time = 0;

    public ImportantMessageData() {
        
    }
    
    public void customWriteObject(CSWriter writer) throws IOException {
        writer.writeInt(id);
        writer.writeString(message);
        writer.writeLong(startDate.getTime());
        writer.writeLong(endDate.getTime());
        writer.writeInt(status);
        writer.writeLong(user_id);
        writer.writeLong(time);
    }
    
    public void customReadObject(CSReader reader) throws IOException {
        id = reader.readInt();
        message = reader.readString();
        startDate = new Date(reader.readLong());
        endDate = new Date(reader.readLong());
        status = reader.readInt();
        user_id = reader.readLong();
        time = reader.readLong();
    }
    
    public ImportantMessageData(int id, String message, Date startDate, Date endDate, int status) {
        this.endDate = endDate;
        this.id = id;
        this.message = message;
        this.startDate = startDate;
        this.status = status;
    }

    public ImportantMessageData(int id) {
        this.id = id;
    }
    
    public ImportantMessageData(int id, String message) {
        this.id = id;
        this.message = message;
    }
    
    public ImportantMessageData(int id, String message, long time) {
        this.id = id;
        this.message = message;
        this.time = time;
    }
    
    public long getTime() {
        return time;
    }
    
    public void setTime(long l) {
        time = l;
    }

    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(long endDate) {
        this.endDate = new Date(endDate);
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(long startDate) {
        this.startDate = new Date(startDate);
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }
    
    public long getCreateUser() {
        return user_id;
    }
    
    public void setCreateUser(long u) {
        user_id = u;
    }

    public boolean equals(Object obj) {
        if (obj instanceof ImportantMessageData) {
            return id == ((ImportantMessageData) obj).getId();
        }
        return false;
    }
}
