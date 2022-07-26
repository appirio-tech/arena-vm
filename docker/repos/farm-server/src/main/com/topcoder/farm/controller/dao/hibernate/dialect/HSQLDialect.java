/*
 * HSQLDialect
 *
 * Created 09/11/2006
 */
package com.topcoder.farm.controller.dao.hibernate.dialect;

import org.hibernate.Hibernate;
import org.hibernate.dialect.function.SQLFunctionTemplate;
import org.hibernate.dialect.function.StandardSQLFunction;


/**
 * @author Diego Belfer (mural)
 * @version $Id$
 */
public class HSQLDialect extends org.hibernate.dialect.HSQLDialect {

    public HSQLDialect() {
        registerFunction( "ageless", new SQLFunctionTemplate(Hibernate.BOOLEAN, "(DATEDIFF('ms', ?1, ?2) > ?3)"));
        registerFunction("substring_index", new StandardSQLFunction("substring_index") );
    }
}
