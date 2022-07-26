package com.topcoder.client.contestApplet.listener;

import java.lang.reflect.*;

final class Invokator {

    private Invokator() {
    }

    ////////////////////////////////////////////////////////////////////////////////
    static void invoke(Object c, String m, Class t, Object p)
            ////////////////////////////////////////////////////////////////////////////////
    {
        Class[] ta = {t};
        Object[] pa = {p};

        // get a handle to the method
        try {
            Method method = c.getClass().getMethod(m, ta);
            method.invoke(c, pa);
        } catch (Exception e) {
            System.out.println("c=" + c + ", m=" + m + ", t=" + t + ", p=" + p);
            e.printStackTrace();
            //System.out.println("Invokation Exception : " + m);
            //System.out.println("Method "+m+" with parameter "+p+" could not be found");
        }
    }

}
