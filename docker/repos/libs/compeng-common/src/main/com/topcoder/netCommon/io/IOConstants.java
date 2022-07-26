package com.topcoder.netCommon.io;

/**
 * The class contains public constants of the com.topcoder.netCommon.io package (buffer sizes).
 *
 * @author  Timur Zambalayev
 */
public final class IOConstants {

    private IOConstants() {
    }
    
    /**
     * The initial request buffer size
     */
    public static final int REQUEST_INITIAL_BUFFER_SIZE = 100 * 1024; //100k
    
    /**
     * The request buffer size increment
     */
    public static final int REQUEST_BUFFER_INCREMENT = 350 * 1024; //350k more
    
    /**
     * The maximum request buffer size
     */
    public static final int REQUEST_MAXIMUM_BUFFER_SIZE = 25 * 1024 * 1024; //25 MB

    /**
     * The initial request buffer size
     */
    public static final int RESPONSE_INITIAL_BUFFER_SIZE = 100 * 1024; //100k
    
    /**
     * The request buffer size increment
     */
    public static final int RESPONSE_BUFFER_INCREMENT = 350 * 1024; //350k more
    
    /**
     * The maximum request buffer size
     */
    public static final int RESPONSE_MAXIMUM_BUFFER_SIZE = 25 * 1024 * 1024; //25 MB

    
    /**
     * DEPRECATED - rfairfax
     *
     * A constant holding the value for the size of the request buffer.
     */
    //public static final int REQUEST_BUFFER_SIZE = 100000;

    //public static final int REQUEST_BIG_BUFFER_SIZE = 5000000;

    /**
     * A constant holding the value for the size of the response buffer.
     */
    //public static final int RESPONSE_BUFFER_SIZE = 100000;

    //public static final int RESPONSE_BIG_BUFFER_SIZE = 5000000;

}
