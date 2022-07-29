/*
 * CustomSerializableUserType
 * 
 * Created 11/02/2006
 */
package com.topcoder.farm.controller.dao.hibernate;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.Serializable;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.Properties;

import org.hibernate.HibernateException;
import org.hibernate.usertype.ParameterizedType;
import org.hibernate.usertype.UserType;

import com.topcoder.farm.shared.serialization.FarmCSHandlerFactory;
import com.topcoder.io.serialization.basictype.impl.BasicTypeDataInputImpl;
import com.topcoder.io.serialization.basictype.impl.BasicTypeDataOutputImpl;
import com.topcoder.shared.netCommon.CSHandler;

/**
 * @author Diego Belfer (Mural)
 * @version $Id$
 */
public class CustomSerializableUserType implements UserType, ParameterizedType {
    private static FarmCSHandlerFactory handlerFactory = new FarmCSHandlerFactory();
    private Class classType;

    public int[] sqlTypes() {
        return new int[] { Types.LONGVARBINARY };
    }

    public Class returnedClass() {
        return classType;
    }

    public boolean equals(Object x, Object y) throws HibernateException {
        if (x == y) {
            return true;
        }
        if (x == null || y == null) {
            return false;
        }
        return x.equals(y);
    }

    public Object nullSafeGet(ResultSet rs, String[] names, Object owner) throws HibernateException, SQLException {
        byte[] value = rs.getBytes(names[0]);
        if (value == null) {
            return null;
        }
        return fromByteArray(value);
    }

    

    public void nullSafeSet(PreparedStatement st, Object value, int index) throws HibernateException, SQLException {
        if (value == null) {
            st.setNull(index, Types.LONGVARBINARY);
        } else {
            byte[] bytesArray = toByteArray(value);
            st.setBytes(index, bytesArray);
        }
    }

    private byte[] toByteArray(Object value) {
        CSHandler handler = getHandler();
        ByteArrayOutputStream bytes = new ByteArrayOutputStream(128);
        handler.setDataOutput(new BasicTypeDataOutputImpl(bytes));
        try {
            handler.writeObject(value);
        } catch (IOException e) {
            throw new HibernateException("Cannot serialize custom serialized type "+value.getClass().getName(), e);
        }
        byte[] bytesArray = bytes.toByteArray();
        return bytesArray;
    }

    private Object fromByteArray(byte[] value) {
        CSHandler handler = getHandler();
        handler.setDataInput(new BasicTypeDataInputImpl(new ByteArrayInputStream(value), value.length));
        try {
            return handler.readObject();
        } catch (IOException e) {
            throw new HibernateException("Cannot deserialize custom serialized type "+classType.getName(), e);
        }
    }
    
    public Object deepCopy(Object o) throws HibernateException {
        if (o == null) return null;
        return fromByteArray(toByteArray(o));
    }

    public boolean isMutable() {
        return true;
    }

    public int hashCode(Object x) throws HibernateException {
        return x.hashCode();
    }

    public Serializable disassemble(Object value) throws HibernateException {
        if (value == null) {
            return null;
        }
        return toByteArray(value);
    }

    public Object assemble(Serializable cached, Object owner) throws HibernateException {
        return fromByteArray((byte[]) cached);
    }

    public Object replace(Object original, Object target, Object owner) throws HibernateException {
        return deepCopy(original);
    }

    public void setParameterValues(Properties parameters) {
        if (parameters == null || parameters.getProperty("class") == null) {
            classType = Object.class;
        } else {
            try {
                classType = Class.forName(parameters.getProperty("class"));
            } catch (Exception e) {
                classType = Object.class;
            }
        }
    }
    
    private CSHandler getHandler() {
        return handlerFactory.newInstance();
    }
}
