package com.topcoder.netCommon.io.buffer;

import java.nio.ByteBuffer;

import junit.framework.TestCase;

import com.topcoder.netCommon.io.buffer.ByteBufferUtil;


public final class ByteBufferUtilTest extends TestCase {

    public static void testCopy() {
        java.nio.ByteBuffer srcBuffer = ByteBuffer.allocate(10);
        srcBuffer.put((byte) 1);
        srcBuffer.put((byte) 2);
        srcBuffer.flip();
        java.nio.ByteBuffer dstBuffer = ByteBuffer.allocate(20);
        ByteBufferUtil.copy(srcBuffer, dstBuffer);
        assertEquals(srcBuffer.position(), dstBuffer.position());
        byte[] srcArray = srcBuffer.array();
        byte[] dstArray = dstBuffer.array();
        int length = Math.min(srcArray.length, dstArray.length);
        for (int i = 0; i < length; i++) {
            assertEquals(srcArray[i], dstArray[i]);
        }
        assertEquals(srcBuffer.limit(), dstBuffer.limit());
    }

    public static void testCopyBigToSmall() {
        java.nio.ByteBuffer srcBuffer = ByteBuffer.allocate(20);
        java.nio.ByteBuffer dstBuffer = ByteBuffer.allocate(10);
        ByteBufferUtil.copy(srcBuffer, dstBuffer);
    }

}
