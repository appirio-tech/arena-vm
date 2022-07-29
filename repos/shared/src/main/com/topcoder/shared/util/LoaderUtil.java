package com.topcoder.shared.util;

import java.net.URL;

public final class LoaderUtil {

    private LoaderUtil() {
    }

    public static URL getResource(String name) {
        ClassLoader classLoader = LoaderUtil.class.getClassLoader();
        return classLoader.getResource(name);
    }

}
