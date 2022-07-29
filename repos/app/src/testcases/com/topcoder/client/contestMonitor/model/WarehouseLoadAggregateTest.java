package com.topcoder.client.contestMonitor.model;

import com.topcoder.server.AdminListener.AdminConstants;

/**
 * Run a warehouse load aggregate test
 */
public class WarehouseLoadAggregateTest extends WarehouseLoadTest {
    /**
     * Set the appropriate id for the test we want to run
     * @throws Exception
     */
    protected void setUp() throws Exception {
        super.setUp();
        setWarehouseLoadRequestId(AdminConstants.REQUEST_WAREHOUSE_LOAD_AGGREGATE);
    }
}
