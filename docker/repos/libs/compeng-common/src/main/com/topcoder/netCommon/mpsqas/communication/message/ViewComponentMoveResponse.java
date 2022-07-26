package com.topcoder.netCommon.mpsqas.communication.message;

import com.topcoder.netCommon.mpsqas.*;
import com.topcoder.shared.netCommon.*;

import java.io.*;

/**
 *
 * @author mitalub
 */
public class ViewComponentMoveResponse
    extends MoveResponse {

        private ComponentInformation component;
        private boolean statementEditable;

        public ViewComponentMoveResponse() {
        }

    public ViewComponentMoveResponse(ComponentInformation component,
            boolean statementEditable) {
        this.component = component;
        this.statementEditable = statementEditable;
    }

    public ComponentInformation getComponent() {
        return component;
    }

    public boolean isStatementEditable() {
        return statementEditable;
    }

    public void customWriteObject(CSWriter writer)
            throws IOException {
        writer.writeObject(component);
        writer.writeBoolean(statementEditable);
    }

    public void customReadObject(CSReader reader)
            throws IOException, ObjectStreamException {
        component = (ComponentInformation) reader.readObject();
        statementEditable = reader.readBoolean();
    }
}
