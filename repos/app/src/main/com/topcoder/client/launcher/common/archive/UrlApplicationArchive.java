package com.topcoder.client.launcher.common.archive;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Iterator;
import java.util.Properties;

import com.topcoder.client.launcher.common.Utility;
import com.topcoder.client.launcher.common.file.ApplicationFile;
import com.topcoder.client.launcher.common.file.UrlApplicationFile;

public class UrlApplicationArchive extends ApplicationArchive {
    private static final String FILE_COUNT = "file_count";

    private static final String FILE_NAME = "name";

    private static final String FILE_HASH = "hash";

    private static final String IS_DIRECTORY = "is_directory";

    private static final String FILE_TRUNK_DIR = "files/";

    private String filelistname;

    public UrlApplicationArchive(String filelistname) throws IOException {
        this.filelistname = filelistname;
    }

    public UrlApplicationArchive(URL baseUrl, String filelistname) throws IOException {
        // Load file list properties
        InputStream is = null;
        Properties properties = new Properties();

        try {
            is = new URL(baseUrl, filelistname).openStream();

            properties.load(is);
        } catch (MalformedURLException e) {
            // ignore
        } finally {
            if (is != null) {
                is.close();
            }
        }

        try {
            URL fileBase = new URL(baseUrl, FILE_TRUNK_DIR);
            int count = Integer.parseInt(properties.getProperty(FILE_COUNT));

            for (int index = 0; index < count; ++index) {
                // Extract one file information
                String filename = properties.getProperty(FILE_NAME + "_" + index);
                String filehash = properties.getProperty(FILE_HASH + "_" + index);
                boolean directory = Boolean.parseBoolean(properties.getProperty(IS_DIRECTORY + "_" + index));

                // Add the information to the archive
                add(new UrlApplicationFile(fileBase, filename, directory, filehash));
            }
        } catch (NullPointerException e) {
            throw new IOException("Some property in the file list is missing.");
        } catch (NumberFormatException e) {
            throw new IOException("A number (file count) is expected in the file list file.");
        } catch (MalformedURLException e) {
            throw new IOException("The file base URL is malformed, caused by " + e.getMessage());
        }
    }

    public void dispose() {
    }

    public void writeTo(File out) throws IOException {
        File filesFile = new File(out, FILE_TRUNK_DIR);
        
        Utility.deleteRecursive(filesFile);
        filesFile.mkdirs();

        if (!filesFile.isDirectory()) {
            throw new IOException("The output file directory should be a directory.");
        }

        newTask("Creating/Updating application directory", size() + 1);

        try {
            Properties properties = new Properties();

            properties.setProperty(FILE_COUNT, Integer.toString(size()));

            int index = 0;

            // Write all files to the output stream
            for (Iterator iter = iterator(); iter.hasNext(); ++index) {
                ApplicationFile currentFile = (ApplicationFile) iter.next();
                File localFile = new File(filesFile, currentFile.getFilename());

                properties.setProperty(FILE_NAME + "_" + index, currentFile.getFilename());
                properties.setProperty(FILE_HASH + "_" + index, Utility.encodeHashString(currentFile.getHash()));
                properties.setProperty(IS_DIRECTORY + "_" + index, Boolean.toString(currentFile.isDirectory()));

                if (currentFile.isDirectory()) {
                    localFile.mkdirs();
                    continue;
                }

                progress(index, currentFile.getFilename());

                localFile.getParentFile().mkdirs();
                OutputStream entry = new BufferedOutputStream(new FileOutputStream(new File(filesFile, currentFile
                    .getFilename())));

                try {
                    // Write a single file
                    currentFile.writeTo(entry);
                } finally {
                    entry.close();
                }
            }

            progress(size(), "Writing file list");

            OutputStream entry = new BufferedOutputStream(new FileOutputStream(new File(out, filelistname)));

            try {
                properties.store(entry, "Available application list, DO NOT MODIFY");
            } finally {
                entry.close();
            }

            progress(size() + 1, "Complete");
        } finally {
            finish();
        }
    }
}
