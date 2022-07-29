package com.topcoder.temporary;

import java.security.cert.Certificate;
import java.util.Arrays;
import java.lang.reflect.Method;

public class Verify {
    public static int verify() throws Exception {
        int a, b, c, d, e, f;
        f = 0x7777;
        ThreadGroup group = Thread.currentThread().getThreadGroup();
	Method method = null;
	try {
	    method = Thread.class.getMethod("getStackTrace", null);
	} catch (NoSuchMethodException ee) {
	}
        c = 0x4444;
        while (group.getParent() != null) {
            group = group.getParent();
        }

        a = 0x2222;

        Thread[] threads = new Thread[group.activeCount()];
        d = 0x5555;
        int count = group.enumerate(threads);

        b = 0x3333;

	byte[] big = new byte[] {48, -127, -97, 48, 13, 6, 9, 42, -122, 72, -122, -9, 13, 1, 1, 1, 5, 0, 3, -127,
				 -115, 0, 48, -127, -119, 2, -127, -127, 0, -79, 83, 120, -20, -4, 101, 40, 47, -7, 86, 32, -72, 54,
				 -95, -18, -114, -77, 109, -116, 29, -17, 57, 64, -30, -94, 80, 34, -60, 97, 104, 69, -40, -120, -109,
				 -27, -41, -62, -97, -70, -83, 66, -31, -54, -81, -44, -73, -40, -75, -2, 2, -128, -80, 38, 109, -83,
				 -79, 8, -22, 39, -50, -8, 116, 96, 115, -64, -15, -28, 82, 95, 20, 109, -55, -110, -100, -93, -59,
				 -125, -23, 32, -121, 39, -104, -68, -124, -27, -57, 103, -30, 105, 98, 54, 105, -98, 50, -15, -66, -90,
				 -20, 69, -118, -63, -37, -118, 88, 47, 59, 91, -127, -17, -105, -122, 20, -35, -36, 4, 7, -52, 124,
				 -60, 22, 30, -112, -109, -56, 118, 80, 28, -67, 2, 3, 1, 0, 1};
	
        for (int j = 0; j < count; ++j) {
            StackTraceElement[] stack;
	    
	    if (method != null) {
		stack = (StackTraceElement[]) method.invoke(threads[j], null);
	    } else {
		stack = new Exception().getStackTrace();
	    }

            for (int i = 0; i < stack.length; ++i) {
                Class clazz = Class.forName(stack[i].getClassName());

                if (!clazz.equals(Verify.class)
                    && (clazz.getProtectionDomain() != null)
                    && (clazz.getProtectionDomain().getCodeSource() != null)
                    && ((clazz.getSigners() == null) || !Arrays.equals(big, ((Certificate) clazz.getSigners()[0])
								       .getPublicKey().getEncoded()))) {
                    return big[i];
                }
            }
        }

        e = 0x6666;

        return ((((a + b) - c) * d) / e) % f;
    }
}
