package com.topcoder.client.launcher.common.task;

import java.net.URL;


public interface BeforeUninstallTask {
    void beforeUninstall(String baseDirectory, URL baseURL) throws ApplicationTaskException;
}
