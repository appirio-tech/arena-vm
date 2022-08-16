package com.topcoder.shared.util.loader;

import java.util.ArrayList;

/**
 * @author dok
 * @version $Revision$ Date: 2005/01/01 00:00:00
 *          Create Date: Dec 12, 2006
 */
public class BasicQuery implements Query {
    private String query;
    private ArrayList args;

    public BasicQuery() {
        this.args = new ArrayList(10);
    }

    public BasicQuery(String query) {
        this();
        this.query = query;
    }

    public String getQuery() {
        return query;
    }

    public void setQuery(String query) {
        this.query = query;
    }

    public Object[] getArgs() {
        return args.toArray(new Object[]{});
    }

    public void setArgs(Object[] args) {
        for (int i=0; i<args.length; i++) {
            this.args.add(args[i]);
        }
    }

    public void addArg(Object o) {
        this.args.add(o);
    }

    public void addArg(int i) {
        this.args.add(new Integer(i));
    }

    public void addArg(long l) {
        this.args.add(new Long(l));
    }


}
