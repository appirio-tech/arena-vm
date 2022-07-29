/*
 * DirectMethodInvocation
 * 
 * Created 06/24/2006
 */
package com.topcoder.farm.shared.invocation;

import java.io.IOException;
import java.io.ObjectStreamException;
import java.lang.reflect.Method;

import com.topcoder.shared.netCommon.CSReader;
import com.topcoder.shared.netCommon.CSWriter;

/**
 * @author Diego Belfer (mural)
 * @version $Id$
 */
public class DirectMethodInvocation implements Invocation {
    public String className;
    public String methodName;
    public Class[] argTypes;
    public Object[] argValues;
    
    
    public DirectMethodInvocation(String className, String methodName, Class[] argTypes, Object[] argValues) {
        this.className = className;
        this.methodName = methodName;
        this.argTypes = argTypes;
        this.argValues = argValues;
    }


    public Object run(InvocationContext context) throws InvocationException {
        try {
            Class classInstance = Class.forName(className);
            Object object = classInstance.newInstance();
            Method method = classInstance.getMethod(methodName, argTypes);
            return method.invoke(object, argValues);
        } catch (Exception e) {
            throw new InvocationException(e);
        }
    }
    
    /**
     * @see com.topcoder.shared.netCommon.CustomSerializable#customReadObject(com.topcoder.shared.netCommon.CSReader)
     */
    public void customReadObject(CSReader cs) throws IOException, ObjectStreamException {
        this.className = cs.readString();
        this.methodName = cs.readString();
        this.argTypes = (Class[]) cs.readObjectArray(Class.class);
        this.argValues = cs.readObjectArray(Object.class);
    }

    /**
     * @see com.topcoder.shared.netCommon.CustomSerializable#customWriteObject(com.topcoder.shared.netCommon.CSWriter)
     */
    public void customWriteObject(CSWriter cs) throws IOException {
        cs.writeString(this.className);
        cs.writeString(this.methodName);
        cs.writeObjectArray(this.argTypes);
        cs.writeObjectArray(this.argValues);
    }
}
