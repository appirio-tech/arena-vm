package com.topcoder.netCommon.mpsqas;

import com.topcoder.shared.netCommon.*;

import java.io.*;

/**
 *
 * @author Logan Hanks
 */
public class ApplicationInformation
        implements Serializable, Cloneable, CustomSerializable {

    private int id;
    private String handle;
    private int rating;
    private int events;
    private String name;
    private String email;
    private String message;
    private String applicationType;

    public void customWriteObject(CSWriter writer) throws IOException {
        writer.writeInt(id);
        writer.writeString(handle);
        writer.writeInt(rating);
        writer.writeInt(events);
        writer.writeString(name);
        writer.writeString(email);
        writer.writeString(message);
        writer.writeString(applicationType);
    }

    public void customReadObject(CSReader reader) throws IOException, ObjectStreamException {
        id = reader.readInt();
        handle = reader.readString();
        rating = reader.readInt();
        events = reader.readInt();
        name = reader.readString();
        email = reader.readString();
        message = reader.readString();
        applicationType = reader.readString();
    }

    public String getHandle() {
        return handle;
    }

    public int getRating() {
        return rating;
    }

    public int getEvents() {
        return events;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public String getMessage() {
        return message;
    }

    public String getApplicationType() {
        return applicationType;
    }

    public void setHandle(String in) {
        handle = in;
    }

    public void setRating(int in) {
        rating = in;
    }

    public void setEvents(int in) {
        events = in;
    }

    public void setName(String in) {
        name = in;
    }

    public void setMessage(String in) {
        message = in;
    }

    public void setEmail(String in) {
        email = in;
    }

    public void setApplicationType(String in) {
        applicationType = in;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public String toString() {
        return "ApplicationInformation[id=" + id + ", handle=" + handle + ", rating=" + rating + ", events=" + events +
                ", name=" + name + ", email=" + email + ", message=\"" + message + "\", applicationType=" + applicationType;
    }
}
