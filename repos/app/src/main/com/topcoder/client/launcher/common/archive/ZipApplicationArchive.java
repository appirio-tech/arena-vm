package com.topcoder.client.launcher.common.archive;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipOutputStream;

import com.topcoder.client.launcher.common.file.ApplicationFile;
import com.topcoder.client.launcher.common.file.ZipApplicationFile;

public class ZipApplicationArchive extends ApplicationArchive {
    private ZipFile zipFile;

    public ZipApplicationArchive() {
    }

    public ZipApplicationArchive(File file) throws IOException {
        zipFile = new ZipFile(file);

        // Extract all files/directories in zip file
        for (Enumeration iter = zipFile.entries(); iter.hasMoreElements();) {
            ZipEntry entry = (ZipEntry) iter.nextElement();

            add(new ZipApplicationFile(entry, zipFile));
        }
    }

    public synchronized void dispose() {
        try {
            if (zipFile != null) {
                zipFile.close();
            }
        } catch (IOException e) {
            // ignore
        }
    }

    public void writeTo(File out) throws IOException {
        ZipOutputStream zos = new ZipOutputStream(new BufferedOutputStream(new FileOutputStream(out)));

        newTask("Creating/Updating Zip file", size());

        try {
            int index = 0;

            // Write all files to the output stream
            for (Iterator iter = iterator(); iter.hasNext(); ++index) {
                ApplicationFile currentFile = (ApplicationFile) iter.next();
                ZipEntry entry = new ZipEntry(currentFile.getFilename());

                progress(index, currentFile.getFilename());

                // Write a single file
                zos.putNextEntry(entry);
                try {
                    currentFile.writeTo(zos);
                } finally {
                    zos.closeEntry();
                }
            }

            progress(size(), "Complete");
        } finally {
            finish();
            try {
                zos.finish();
            } finally {
                zos.close();
            }
        }
    }
}
