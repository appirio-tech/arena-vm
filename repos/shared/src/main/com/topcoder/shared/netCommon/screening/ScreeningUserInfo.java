/**
 * @author Michael Cervantes (emcee)
 * @since Apr 25, 2002
 */
package com.topcoder.shared.netCommon.screening;

import com.topcoder.shared.netCommon.CustomSerializable;
import com.topcoder.shared.netCommon.CSReader;
import com.topcoder.shared.netCommon.CSWriter;

import java.io.Serializable;
import java.io.IOException;
import java.util.*;

public final class ScreeningUserInfo implements Serializable, CustomSerializable, Cloneable {

    private long lastLogin;
    private String handle = "";
    private HashMap preferences = new HashMap();

    public ScreeningUserInfo() {
    }

    public ScreeningUserInfo(String handle) {
        this.handle = handle;
    }

    public ScreeningUserInfo(String handle, long lastLogin) {
        this.handle = handle;
        this.lastLogin = lastLogin;
    }

    public void clear() {
        handle = "";
        lastLogin = 0;
    }

    public boolean equals(Object r) {
        if (r instanceof ScreeningUserInfo) {
            ScreeningUserInfo rhs = (ScreeningUserInfo) r;
            return rhs.handle.equals(handle) && (rhs.lastLogin == lastLogin);
        }
        return false;
    }

    public String toString() {
        return "handle=" + handle + "," + "lastLogin=" + lastLogin;
    }

    public void customReadObject(CSReader reader) throws IOException {
        lastLogin = reader.readLong();
        handle = reader.readString();
        preferences = reader.readHashMap();
    }

    public void customWriteObject(CSWriter writer) throws IOException {
        writer.writeLong(lastLogin);
        writer.writeString(handle);
        writer.writeHashMap(preferences);
    }


    public String getHandle() {
        return handle;
    }

    public void setHandle(String h) {
        handle = h;
    }

    public boolean isFirstTimeUser() {
        return lastLogin == 0;
    }

    public long getLastLogin() {
        return lastLogin;
    }

    public void setLastLogin(long lastLogin) {
        this.lastLogin = lastLogin;
    }

    public HashMap getPreferences() {
        return preferences;
    }

    public void setPreferences(HashMap preferences) {
        this.preferences = preferences;
    }
}
