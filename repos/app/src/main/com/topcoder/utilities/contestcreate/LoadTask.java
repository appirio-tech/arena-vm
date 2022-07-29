package com.topcoder.utilities.contestcreate;

import java.sql.Connection;

public interface LoadTask {

    public void apply(Connection conn);
}
