package com.topcoder.client.launcher.common.file;

import java.io.IOException;
import java.io.OutputStream;
import java.net.URL;
import java.security.MessageDigest;

import com.topcoder.client.launcher.common.Utility;

public class UrlApplicationFile extends BaseApplicationFile {
    private URL baseUrl;

    private String filename;

    private byte[] hash;

    private boolean directory;

    public UrlApplicationFile(URL baseUrl, String filename, boolean directory, String hashString) {
        Utility.validateNotNull(baseUrl, "baseUrl");
        Utility.validateNotNull(filename, "filename");
        Utility.validateNotNull(hashString, "hashString");
        
        this.baseUrl = baseUrl;
        this.filename = filename;
        this.directory = directory;

        // Decode the hash string
        hash = Utility.decodeHashString(hashString);
    }

    public String getFilename() {
        return filename;
    }

    public boolean isDirectory() {
        return directory;
    }

    public byte[] getHash() {
        return hash;
    }

    public void writeTo(OutputStream out) throws IOException {
        if (!isDirectory()) {
            Utility.downloadFile(new URL(baseUrl, filename), out);
        }
    }
}
