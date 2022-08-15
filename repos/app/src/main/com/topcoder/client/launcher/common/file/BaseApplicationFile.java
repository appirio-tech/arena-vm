package com.topcoder.client.launcher.common.file;

import java.io.IOException;
import java.io.OutputStream;
import java.security.MessageDigest;

public abstract class BaseApplicationFile implements ApplicationFile {
    public boolean equals(Object obj) {
        if (!(obj instanceof ApplicationFile)) {
            return false;
        }

        return getFilename().equals(((ApplicationFile) obj).getFilename());
    }

    public int hashCode() {
        return getFilename().hashCode();
    }

    public boolean contentSame(ApplicationFile file) {
        return (isDirectory() && file.isDirectory()) || MessageDigest.isEqual(getHash(), file.getHash());
    }
}
