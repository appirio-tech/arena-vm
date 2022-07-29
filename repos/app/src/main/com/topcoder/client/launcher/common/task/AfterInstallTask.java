package com.topcoder.client.launcher.common.task;

import java.net.URL;


public interface AfterInstallTask {
    void afterInstall(String baseDirectory, URL baseURL) throws ApplicationTaskException;
}
