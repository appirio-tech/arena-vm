package com.topcoder.utilities;

import java.io.*;
import java.util.*;
import java.net.*;

import com.topcoder.shared.util.DBMS;
import com.topcoder.server.common.ServerContestConstants;
import com.topcoder.netCommon.contest.*;

//import com.topcoder.common.contest.attr.*;

public class ObjectGetter {

    String table, field, whereClause;
    String type;
    Object obj;

    public ObjectGetter(String field, String table, String where) {
        this.field = field;
        this.table = table;
        this.whereClause = where;
    }

    public static void main(String[] args) {
        if (args.length != 3) {
            System.out.println("This program is used to retrieve a BLOB object from the database");
            System.out.println("Usage: java com.topcoder.utilities.ObjectGetter <field> <table> <whereClause>");
            System.out.println("Example: java com.topcoder.utilities.ObjectGetter expected_result system_test_cases test_case_id=2");
            return;
        }

        ObjectGetter x = new ObjectGetter(args[0], args[1], args[2]);
        System.out.println("The returned object is " + x.getObject());

    }

    public Object getObject() {
        Object obj = new Object();

        try {
            obj = DBMS.getBlobObject(this.table, this.field, this.whereClause);
        } catch (Exception e) {
            System.out.println("EXCEPTION IN GET OBJECT");
            e.printStackTrace();
        }

        return ServerContestConstants.makePretty(obj);
    }
}
