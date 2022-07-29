/*
* Copyright (C) - 2014 TopCoder Inc., All Rights Reserved.
*/
package com.topcoder.client.launcher.common;

import java.awt.Component;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.Map;
import java.util.TreeMap;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import javax.swing.JOptionPane;

import com.topcoder.client.launcher.common.task.ApplicationTaskProgressListener;

/**
 * the utility class.
 *
 * <p>
 * Changes in version 1.0 (Release Assembly - TopCoder Competition Engine Improvement Series 4):
 * <ol>
 *      <li>Add {@link #input(String title, String msg, Component comp)} method.</li>
 *      <li>Add {@link #showMessage(String title, String msg, Component comp)} method.</li>
 * </ol>
 * </p>
 * @author TCSASSEMBLER
 * @version 1.0
 */
public class Utility {
    private static final int BUFFER_SIZE = 4 * 1024;

    private static final String DIGEST_ALGORITHM = "SHA1";

    private static final Runtime RUNTIME = Runtime.getRuntime();

    private static final int MAX_RETRY = 5;

    private static MessageDigest getHashAlgorithm() {
        try {
            return MessageDigest.getInstance(DIGEST_ALGORITHM);
        } catch (NoSuchAlgorithmException e) {
            // ignore, should never happen
            return null;
        }
    }
    
    public static void debug(String message) {
        if (Boolean.getBoolean("DEBUG")) {
            System.out.println(message);
        }
    }

    /**
     * <p>show the input dialog.</p>
     * @param title the dialog title.
     * @param msg the dialog message
     * @param comp the component.
     * @return dialog return text.
     */
    public static String input(String title, String msg, Component comp) {
        String value = JOptionPane.showInputDialog(comp, msg, title,
                JOptionPane.QUESTION_MESSAGE);
        return (value);
    }
    
    /**
     * <p>display the popup window.</p>
     * @param title the windows title.
     * @param msg the windows message.
     * @param comp the windows component.
     */
    public static void showMessage(String title, String msg, Component comp) {
        JOptionPane.showMessageDialog(comp, msg, title, JOptionPane.INFORMATION_MESSAGE);
    }
    public static void gc() {
        long nowFree = RUNTIME.freeMemory();
        long free;

        do {
            Thread.yield();
            free = nowFree;
            RUNTIME.gc();
            nowFree = RUNTIME.freeMemory();
        } while (nowFree > free);
    }

    public static void validateNotNull(Object obj, String name) {
        if (obj == null) {
            throw new NullPointerException("Parameter '" + name + "' cannot be null.");
        }
    }

    public static String encodeHashString(byte[] hash) {
        StringBuffer sb = new StringBuffer();

        for (int i = 0; i < hash.length; ++i) {
            int b = hash[i];

            if (b < 0) {
                b += 256;
            }

            if (b < 16) {
                sb.append("0");
            }

            sb.append(Integer.toHexString(b));
        }

        return sb.toString();
    }

    public static byte[] decodeHashString(String hashString) {
        // Decode the hash string
        byte[] hash = new byte[hashString.length() / 2];

        try {
            for (int i = 0; i < hashString.length(); i += 2) {
                hash[i / 2] = (byte) Integer.parseInt(hashString.substring(i, i + 2), 16);
            }
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("The hash string is malformed.", e);
        }

        return hash;
    }

    public static void downloadFile(URL url, OutputStream dest) throws IOException {
        InputStream is = null;

        try {
            is = url.openStream();

            copyStream(is, dest);
        } finally {
            if (is != null) {
                is.close();
            }
        }
    }
    
    public static void downloadFileProgress(URL url, OutputStream dest, ApplicationTaskProgressListener listener) throws IOException {
        InputStream is = null;
        URLConnection conn = null;
        int len = 0;

        try {
            conn = url.openConnection();
            is = url.openStream();

            listener.newTask("Downloading " + url.getFile(), conn.getContentLength());
            
            byte[] buffer = new byte[BUFFER_SIZE];
            int read;

            while ((read = is.read(buffer)) >= 0) {
                dest.write(buffer, 0, read);
                len += read;
                listener.progress(len, Integer.toString(len / 1024) + " kb");
            }

            dest.flush();
        } finally {
            listener.finish();
            if (is != null) {
                is.close();
            }
        }
    }

    public static byte[] computeHash(InputStream is) throws IOException {
        MessageDigest digest = getHashAlgorithm();

        byte[] buffer = new byte[BUFFER_SIZE];
        int read;

        while ((read = is.read(buffer)) >= 0) {
            digest.update(buffer, 0, read);
        }

        return digest.digest();
    }

    public static byte[] computeZipHash(ZipFile zipFile) throws IOException {
        Map map = new TreeMap();
        MessageDigest digest = getHashAlgorithm();

        // Sort all entries according to their names
        for (Enumeration iter = zipFile.entries(); iter.hasMoreElements();) {
            ZipEntry entry = (ZipEntry) iter.nextElement();

            map.put(entry.getName(), entry);
        }

        byte[] buffer = new byte[BUFFER_SIZE];

        // Build the hash
        for (Iterator iter = map.values().iterator(); iter.hasNext();) {
            ZipEntry entry = (ZipEntry) iter.next();
            
            // Add the name to the digested buffer
            digest.update(entry.getName().getBytes("UTF-8"));

            if (!entry.isDirectory()) {
                // Only files are digested.
                InputStream is = null;

                try {
                    is = zipFile.getInputStream(entry);
                    int read;

                    while ((read = is.read(buffer)) >= 0) {
                        digest.update(buffer, 0, read);
                    }
                } finally {
                    if (is != null) {
                        is.close();
                    }
                }
            }
        }

        return digest.digest();
    }

    public static void copyStream(InputStream from, OutputStream to) throws IOException {
        byte[] buffer = new byte[BUFFER_SIZE];
        int read;

        while ((read = from.read(buffer)) >= 0) {
            to.write(buffer, 0, read);
        }

        to.flush();
    }

    public static boolean deleteRecursive(File base) {
        File[] files = base.listFiles();

        boolean success = true;

        if (files != null) {
            for (int i = 0; i < files.length; ++i) {
                if (!deleteRecursive(files[i])) {
                    success = false;
                }
            }
        }

        boolean localsuccess = false;

        for (int i = 0; i < MAX_RETRY; ++i) {
            if (base.delete()) {
                localsuccess = true;
                break;
            }

            System.gc();
        }

        return localsuccess && success;
    }
}
