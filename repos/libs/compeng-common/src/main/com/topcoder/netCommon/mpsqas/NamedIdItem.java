package com.topcoder.netCommon.mpsqas;

import com.topcoder.shared.netCommon.*;

import java.io.*;

/**
 * Contains a bit of information on an item with a name and id.
 *
 * @author mitalub
 */
public class NamedIdItem
        implements CustomSerializable, Cloneable, Serializable {

    public static final int COMPONENT = 1;
    public static final int SINGLE_PROBLEM = 2;
    public static final int TEAM_PROBLEM = 3;
    public static final int LONG_PROBLEM = 4;
    public static final int WEB_SERVICE = 5;
    public static final int USER = 6;

    private int id;
    private int type;
    private String name;

    public NamedIdItem() {
    }

    public NamedIdItem(String name, int id, int type) {
        this.name = name;
        this.type = type;
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public int getType() {
        return type;
    }

    public String getName() {
        return name;
    }

    public void customWriteObject(CSWriter writer) throws IOException {
        writer.writeString(name);
        writer.writeInt(type);
        writer.writeInt(id);
    }

    public void customReadObject(CSReader reader) throws IOException,
            ObjectStreamException {
        name = reader.readString();
        type = reader.readInt();
        id = reader.readInt();
    }
}
