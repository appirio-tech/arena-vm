package com.topcoder.client.launcher.common.file;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import com.topcoder.client.launcher.common.Utility;

public class ZipApplicationFile extends BaseApplicationFile {
    private ZipEntry entry;

    private ZipFile file;

    private byte[] hash;

    public ZipApplicationFile(ZipEntry entry, ZipFile file) {
        Utility.validateNotNull(entry, "entry");
        Utility.validateNotNull(file, "file");

        this.entry = entry;
        this.file = file;
    }

    private InputStream getInputStream() throws IOException {
        return file.getInputStream(entry);
    }

    public String getFilename() {
        return entry.getName();
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
        return entry.isDirectory();
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
