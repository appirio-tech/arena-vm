/*
 * TestDataPropertiesHelper
 * 
 * Created 08/17/2006
 */
package com.topcoder.farm.test.common;

import com.topcoder.farm.shared.processorproperties.RequerimentsBuilder;

/**
 * @author Diego Belfer (mural)
 * @version $Id$
 */
public class TestDataPropertiesHelper {
    public static final String OS_LINUX = "linux";
    public static final String OS_WINDOWS = "windows";
    public static final Integer[] MEMORY_SIZES = new Integer[] {new Integer(512), new Integer(1024)};
    public static final Integer[] PROC_TYPES = new Integer[] {new Integer(1), new Integer(2)};
    public static final String[] OS_NAMES = new String[] {OS_WINDOWS, OS_LINUX};
    public static final int[] MAX_RUNNABLE_TASKS = new int[] {1,2,1,3};
    
    
    public static final String OS_NAME = RequerimentsBuilder.OS_TYPE_KEY;
    public static final String MEMORY = RequerimentsBuilder.MEM_AVAILABLE_KEY;
    public static final String PROCESSOR_TYPE = "processor-type";
    public static final String PROC_PROP_ID = "properties.id";
}
