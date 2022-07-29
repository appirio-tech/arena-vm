package com.topcoder.netCommon.mpsqas;

import com.topcoder.shared.netCommon.*;

import java.io.*;
import java.util.*;

/**
 *
 * @author Logan Hanks
 */
public class UserInformation
        implements CustomSerializable, Cloneable, Serializable {

    private boolean writer = false;
    private boolean tester = false;
    private String handle = "";
    private int userId = -1;
    private String firstName = "";
    private String lastName = "";
    private String email = "";
    private double paid = 0;
    private double pending = 0;
    private ArrayList problems = new ArrayList();

    public void customWriteObject(CSWriter writer) throws IOException {
        writer.writeBoolean(this.writer);
        writer.writeBoolean(tester);
        writer.writeString(handle);
        writer.writeInt(userId);
        writer.writeString(firstName);
        writer.writeString(lastName);
        writer.writeString(email);
        writer.writeDouble(paid);
        writer.writeDouble(pending);
        writer.writeArrayList(problems);
    }

    public void customReadObject(CSReader reader) throws IOException, ObjectStreamException {
        writer = reader.readBoolean();
        tester = reader.readBoolean();
        handle = reader.readString();
        userId = reader.readInt();
        firstName = reader.readString();
        lastName = reader.readString();
        email = reader.readString();
        paid = reader.readDouble();
        pending = reader.readDouble();
        problems = reader.readArrayList();
    }

    public UserInformation() {
    }

    public UserInformation(String handle, int userId) {
        this.handle = handle;
        this.userId = userId;
    }

    public UserInformation(String handle) {
        this.handle = handle;
        userId = -1;
    }

    public String getHandle() {
        return handle;
    }

    public int getUserId() {
        return userId;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setPaid(double paid) {
        this.paid = paid;
    }

    public double getPaid() {
        return paid;
    }

    public void setPending(double pending) {
        this.pending = pending;
    }

    public double getPending() {
        return pending;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getEmail() {
        return email;
    }

    public void setWriter(boolean writer) {
        this.writer = writer;
    }

    public boolean isWriter() {
        return writer;
    }

    public void setTester(boolean tester) {
        this.tester = tester;
    }

    public boolean isTester() {
        return tester;
    }

    public void setProblems(ArrayList problems) {
        this.problems = problems;
    }

    public ArrayList getProblems() {
        return problems;
    }

    public boolean equals(Object o) {
        boolean result = true;
        if (o == null) {
            result = false;
        } else if (!(o instanceof UserInformation)) {
            result = false;
        } else {
            UserInformation u = (UserInformation) o;
            if (userId != u.getUserId()) {
                result = false;
            } else if (!handle.equals(u.getHandle())) {
                result = false;
            }
        }
        return result;
    }
}
