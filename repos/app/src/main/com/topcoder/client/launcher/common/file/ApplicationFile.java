package com.topcoder.client.launcher.common.file;

import java.io.IOException;
import java.io.OutputStream;

public interface ApplicationFile {
    String getFilename();

    byte[] getHash();

    void writeTo(OutputStream out) throws IOException;
    
    boolean contentSame(ApplicationFile file);
    
    boolean isDirectory();
}
