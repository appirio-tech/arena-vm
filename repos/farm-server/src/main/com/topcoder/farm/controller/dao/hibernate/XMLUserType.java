/*
 * XMLUserType
 * 
 * Created 08/14/2006
 */
package com.topcoder.farm.controller.dao.hibernate;

import java.io.Serializable;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.Properties;

import org.hibernate.Hibernate;
import org.hibernate.HibernateException;
import org.hibernate.usertype.ParameterizedType;
import org.hibernate.usertype.UserType;

import com.topcoder.farm.shared.xml.XMLHelper;

/**
 * XML type mapping. This parametrized user type
 * persists Object instances as XML.
 * 
 * The class mapped by this UserType can be defined 
 * using the &lt;code&gt;param&lt;/code&gt; tag with "class" as name.  
 *  &lt;property ...&gt;
 *      &lt;type ...&gt;
 *          &lt;param name="class"&gt;....&lt;/param&gt;
 *      &lt;/type&gt;
 *  &lt;/property&gt;
 *
 * @author Diego Belfer (Mural)
 * @version $Id$
 */
public class XMLUserType implements UserType, ParameterizedType {
    private static final XMLHelper xmlSerializer = XMLHelper.getInstance();
    private Class classType; 

    private XMLHelper getXMLSerializer() {
        return xmlSerializer;
    }

    public int[] sqlTypes() {
        return new int[] { Types.CLOB };
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
        String key = (String) Hibernate.STRING.nullSafeGet(rs, names[0]);
        return (key == null) ? null : getXMLSerializer().fromXML(key);
    }

    public void nullSafeSet(PreparedStatement st, Object value, int index) throws HibernateException, SQLException {
        String key = (value == null) ? null : getXMLSerializer().toXML(value);
        Hibernate.STRING.nullSafeSet(st, key, index);
    }

    public Object deepCopy(Object o) throws HibernateException {
        if (o == null) return null;
        return getXMLSerializer().fromXML(getXMLSerializer().toXML(o));
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
        return getXMLSerializer().toXML(value);
    }

    public Object assemble(Serializable cached, Object owner) throws HibernateException {
        return getXMLSerializer().fromXML((String) cached);
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
}