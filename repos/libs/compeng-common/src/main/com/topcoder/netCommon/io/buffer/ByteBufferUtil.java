package com.topcoder.netCommon.io.buffer;

import java.nio.ByteBuffer;

/**
 * Defines a utility class which can copy the content from a <code>ByteBuffer</code> to another. It automatically
 * takes care of the buffer limit.
 * 
 * @author Qi Liu
 */
public final class ByteBufferUtil {
    /**
     * Creates a new instance of <code>ByteBufferUtil</code> class. This private constructor prevents the creation
     * of a new instance.
     */
    private ByteBufferUtil() {
    }

    /**
     * Copy the content of a source <code>ByteBuffer</code> to another <code>ByteBuffer</code>. When the size
     * of the destination buffer is enough, all the content of the source buffer will be copied. Otherwise,
     * only part of the source buffer which can fit in the destination buffer will be copied.
     * 
     * @param srcBuffer the content of the buffer to be copied.
     * @param dstBuffer the buffer where the content should be copied to.
     */
    public static void copy(ByteBuffer srcBuffer, ByteBuffer dstBuffer) {
        //limit things first, just in case the new limit is above our current
        //limit
        int newLimit = Math.min(srcBuffer.limit(), dstBuffer.capacity());
        dstBuffer.limit(newLimit);

        dstBuffer.position(srcBuffer.position());

        byte[] srcArray = srcBuffer.array();
        byte[] dstArray = dstBuffer.array();
        int minArrayLength = Math.min(srcArray.length, dstArray.length);
        System.arraycopy(srcArray, 0, dstArray, 0, minArrayLength);
    }

}
