package com.topcoder.client.launcher.common.task;

public interface ApplicationTaskProgressListener {
    void newTask(String name, int max);
    void progress(int progress, String comment);
    void finish();
}
