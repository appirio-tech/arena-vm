package com.topcoder.client.launcher.common.file;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import com.topcoder.client.launcher.common.Utility;

public class LocalApplicationFile extends BaseApplicationFile {
    private File file;

    private String filename;

    private byte[] hash;
    
    public LocalApplicationFile(File file) {
        this(file, file.getName());
    }

    public LocalApplicationFile(File file, String nameInArchive) {
        Utility.validateNotNull(file, "file");
        Utility.validateNotNull(nameInArchive, "nameInArchive");

        this.filename = nameInArchive;
        this.file = file;
    }

    private InputStream getInputStream() throws IOException {
        return new FileInputStream(file);
    }

    public String getFilename() {
        return filename;
    }

    public byte[] getHash() {
        if (hash == null) {
            if (isDirectory()) {
                hash = new byte[0];
            } else {

                InputStream is = null;

                try {
                    hash = Utility.computeHash(getInputStream());
                } catch (IOException e) {
                    hash = new byte[0];
                } finally {
                    if (is != null) {
                        try {
                            is.close();
                        } catch (IOException e) {
                            // ignore
                        }
                    }
                }
            }
        }
        
        return hash;
    }

    public boolean isDirectory() {
        return file.isDirectory();
    }

    public void writeTo(OutputStream out) throws IOException {
        if (isDirectory()) {
            return;
        }

        InputStream is = null;

        try {
            is = getInputStream();
            Utility.copyStream(is, out);
        } finally {
            if (is != null) {
                is.close();
            }
        }
    }
}
