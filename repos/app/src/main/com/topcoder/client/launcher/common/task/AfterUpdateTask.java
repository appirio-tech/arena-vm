package com.topcoder.client.launcher.common.task;

import java.net.URL;


public interface AfterUpdateTask {
    void afterUpdate(String baseDirectory, URL baseURL) throws ApplicationTaskException;
}
