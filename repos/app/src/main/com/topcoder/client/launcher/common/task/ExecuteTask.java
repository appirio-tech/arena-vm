package com.topcoder.client.launcher.common.task;

import java.net.URL;


public interface ExecuteTask {
    void execute(String baseDirectory, URL baseURL) throws ApplicationTaskException;
}
