package com.topcoder.server.util.sql.executor;

import java.util.List;

public final class SimpleResultSet {

    private final List resultSetRowList;

    SimpleResultSet(List resultSetRowList) {
        this.resultSetRowList = resultSetRowList;
    }

    public int size() {
        return resultSetRowList.size();
    }

    public boolean isEmpty() {
        return size() <= 0;
    }

    public ResultSetRow getRow() {
        if (size() != 1) {
            throw new RuntimeException("size() = " + size());
        }
        return get(0);
    }

    public ResultSetRow get(int index) {
        return (ResultSetRow) resultSetRowList.get(index);
    }

}
