/*
 * MySQLDialect
 *
 * Created 10/12/2006
 */
package com.topcoder.farm.controller.dao.hibernate.dialect;

import java.sql.Types;

import org.hibernate.Hibernate;
import org.hibernate.dialect.function.SQLFunctionTemplate;
import org.hibernate.dialect.function.StandardSQLFunction;

/**
 * @author Diego Belfer (mural)
 * @version $Id$
 */
public class MySQLDialect extends org.hibernate.dialect.MySQL5Dialect {

    public MySQLDialect() {
        super();
        registerColumnType( Types.LONGVARBINARY, "longblob" );
        registerColumnType( Types.CLOB, 65535, "longtext" );
        registerFunction( "ageless", new SQLFunctionTemplate(Hibernate.BOOLEAN, "((?2 + INTERVAL ?3 * 1000 MICROSECOND) < ?1)"));
        registerFunction("substring_index", new StandardSQLFunction("substring_index") );
    }

    /**
     * @see org.hibernate.dialect.MySQLDialect#supportsCascadeDelete()
     */
    public boolean supportsCascadeDelete() {
        return true;
    }

}
