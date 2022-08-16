/*
 * InformixDialect
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
public class InformixDialect extends org.hibernate.dialect.InformixDialect {

    public InformixDialect() {
        super();
        registerColumnType(Types.CLOB, "text" );
        registerFunction( "ageless", new SQLFunctionTemplate(Hibernate.BOOLEAN, "compare((?1 - ?2), ?3 UNITS FRACTION(3))"));
    }

}
