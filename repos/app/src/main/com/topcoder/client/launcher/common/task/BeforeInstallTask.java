package com.topcoder.client.launcher.common.task;

import java.net.URL;


public interface BeforeInstallTask {
    void beforeInstall(String baseDirectory, URL baseURL) throws ApplicationTaskException;
}
