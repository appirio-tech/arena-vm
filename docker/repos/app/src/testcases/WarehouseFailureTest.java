/**
 * WarehouseFailureTest.java 
 *
 * This test class provides a series of failure tests for Warehouse loading functionality
 * (WarehouseLoadRequest class)
 *
 * @author aksonov
 * 
 * Copyright é 2003, TopCoder, Inc. All rights reserved
 */

import java.util.Hashtable;

import junit.framework.TestCase;

import com.topcoder.server.AdminListener.AdminConstants;
import com.topcoder.server.AdminListener.request.WarehouseLoadRequest;

public class WarehouseFailureTest extends TestCase {

	/**
	 *  Tests WarehouseLoadRequest(in requestID:int, params:Hashtable) with null params 
	 */
	public void testWarehouseLoadRequestParamsNull() throws Exception {
		try {
			new WarehouseLoadRequest(AdminConstants.REQUEST_WAREHOUSE_LOAD_ROUND, null);
		} catch (IllegalArgumentException e){
			return;
		}
		fail("IllegalArgumentException should be thrown!");
	}


}
