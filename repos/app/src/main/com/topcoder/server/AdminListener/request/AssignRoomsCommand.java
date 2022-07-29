package com.topcoder.server.AdminListener.request;

import com.topcoder.shared.netCommon.*;

import java.io.IOException;
import java.io.ObjectStreamException;


public final class AssignRoomsCommand extends RoundIDCommand implements CustomSerializable {

    private int codersPerRoom;
    private int type;
    private boolean isByDivision;
    private boolean isFinal;
    private boolean isByRegion;
    double p;

    public AssignRoomsCommand() {
    }

    public AssignRoomsCommand(int roundID, int codersPerRoom, int type, boolean byDivision,
            boolean aFinal, boolean byRegion, double p) {
        super(roundID);
        this.codersPerRoom = codersPerRoom;
        this.type = type;
        isByDivision = byDivision;
        isFinal = aFinal;
        isByRegion = byRegion;
        this.p = p;
    }

    public void customWriteObject(CSWriter writer) throws IOException {
        super.customWriteObject(writer);
        writer.writeInt(codersPerRoom);
        writer.writeInt(type);
        writer.writeBoolean(isByDivision);
        writer.writeBoolean(isFinal);
        writer.writeBoolean(isByRegion);
        writer.writeDouble(p);
    }

    public void customReadObject(CSReader reader) throws IOException, ObjectStreamException {
        super.customReadObject(reader);
        codersPerRoom = reader.readInt();
        type = reader.readInt();
        isByDivision = reader.readBoolean();
        isFinal = reader.readBoolean();
        isByRegion = reader.readBoolean();
        p = reader.readDouble();
    }

    public int getCodersPerRoom() {
        return codersPerRoom;
    }

    public boolean isByDivision() {
        return isByDivision;
    }

    public int getType() {
        return type;
    }

    public double getP() {
        return p;
    }

    public boolean isFinal() {
        return isFinal;
    }

    public boolean isByRegion() {
        return isByRegion;
    }

}
