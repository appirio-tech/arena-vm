/*
 * LongTestScoreId
 * 
 * Created 10/03/2006
 */
package com.topcoder.server.farm.longtester;

import java.io.IOException;
import java.io.ObjectStreamException;
import java.io.Serializable;

import com.topcoder.shared.netCommon.CSReader;
import com.topcoder.shared.netCommon.CSWriter;
import com.topcoder.shared.netCommon.CustomSerializable;

/**
 * Long Test Score recalculation identifier. 
 * 
 * @author Diego Belfer (mural)
 * @version $Id: LongTestScoreId.java 54869 2006-12-01 18:02:46Z thefaxman $
 */
public class LongTestScoreId implements Serializable, CustomSerializable {
    private int testAction;
    private Object id;
    
    public LongTestScoreId() {
        
    }
    
    public LongTestScoreId(int testAction, Object id) {
        this.testAction = testAction;
        this.id = id;
    }

    public int getTestAction() {
        return testAction;
    }

    public void setTestAction(int testAction) {
        this.testAction = testAction;
    }

    public Object getId() {
        return id;
    }

    public void setId(Object id) {
        this.id = id;
    }

    public void customReadObject(CSReader reader) throws IOException, ObjectStreamException {
        this.id = reader.readObject();
        this.testAction = reader.readInt();
    }

    public void customWriteObject(CSWriter writer) throws IOException {
        writer.writeObject(this.id);
        writer.writeInt(this.testAction);
    }
    
    
}