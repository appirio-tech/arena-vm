/*
 * PostgreSQLDialect
 * 
 * Created 09/11/2006
 */
package com.topcoder.farm.controller.dao.hibernate.dialect;

import java.sql.Types;

import org.hibernate.Hibernate;
import org.hibernate.dialect.function.SQLFunctionTemplate;



/**
 * @author Diego Belfer (mural)
 * @version $Id$
 */
public class PostgreSQLDialect extends org.hibernate.dialect.PostgreSQLDialect {

    public PostgreSQLDialect() {
        super();
        registerColumnType( Types.LONGVARBINARY, "bytea" );
        registerFunction( "ageless", new SQLFunctionTemplate(Hibernate.BOOLEAN, "(date_part('epoch', age(?1,?2)) * 1000 > ?3)"));
    }

}
