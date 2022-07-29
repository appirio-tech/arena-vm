package com.topcoder.server.util.logging.net;

import com.topcoder.shared.netCommon.CSReader;
import com.topcoder.shared.netCommon.CSWriter;
import com.topcoder.shared.netCommon.CustomSerializable;
import java.io.IOException;
import java.io.Serializable;

import org.apache.log4j.Level;

public class TCLoggingEvent implements CustomSerializable, Serializable {
    
    private String message;
    private Level level;
    
    public TCLoggingEvent() {
        
    }
    
    public TCLoggingEvent(String message, Level level) {
        this.level = level;
        this.message = message;
    }
    
    public void customWriteObject(CSWriter writer) throws IOException {
        writer.writeString(message);
        writer.writeInt(level.toInt());
    }

    public void customReadObject(CSReader reader) throws IOException {
        message = reader.readString();
        level = Level.toLevel(reader.readInt());
    }    
    
    public String getMessage() {
        return message;
    }
    
    public void setMessage(String message) {
        this.message = message;
    }
    
    public Level getLevel() {
        return level;
    }
    
    public void setLevel(Level level) {
        this.level = level;
    }
}
