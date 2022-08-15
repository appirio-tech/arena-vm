package com.topcoder.server.util;

/**
 * <p>Title: </p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2002</p>
 * <p>Company: TopCoder</p>
 * @author Jeremy Nuanes
 * @version 1.0
 */

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Random;

public final class FileUtil {
    public static final int FILENAME_MAX_LENGTH = 60;


    private FileUtil() {
    }

    public static void removeFile(File file) {
        if (file.isDirectory()) {
            File[] files = file.listFiles();
            for (int i = 0; i != files.length; ++i) {
                if (files[i].isDirectory())
                    removeFile(files[i]);
                else
                    files[i].delete();
            }
        }
        file.delete();
    }

    //Remove a filetype from the directory and all sub directories
    public static void removeFileType(File directory, String type) {
        if (directory.isDirectory()) {
            File[] files = directory.listFiles();
            for (int i = 0; i != files.length; ++i) {
                if (files[i].isDirectory())
                    removeFileType(files[i], type);
                else if (files[i].getName().endsWith(type))
                    files[i].delete();
            }
        }
    }
    
    /**
     * Deletes a directory (including all subdirectories and files),
     * starting at root.
     *
     * @param  root     file or directory to delete
     * @return          true if deletion was successful
     */
    public static boolean deleteRecursive(File root) {
        if (!root.isDirectory()) {
            return root.delete();
        }

        File[] files = root.listFiles();

        boolean success = true;
        boolean succeeded;

        for (int i = 0; i < files.length; i++) {

            if (!files[i].isDirectory()) {
                succeeded = files[i].delete();
            } else {
                if (files[i].delete()) {
                    succeeded = true;
                } else {
                    succeeded = deleteRecursive(files[i]);
                }
            }

            if (succeeded == false) {
                success = false;
            }
        }

        succeeded = root.delete();
        if (succeeded == false) {
            success = false;
        }
        return success;
    }

    public static byte[] getContents(File file) throws IOException {
        BufferedInputStream stream = new BufferedInputStream(new FileInputStream(file));
        byte[] buffer = new byte[(int) file.length()];
        stream.read(buffer, 0, buffer.length);
        stream.close();
        return buffer;
    }
    
    public static String getStringContents(File file) throws IOException {
        return new String(getContents(file));
    }
    
    public static void writeContents(File file, String contents) throws IOException {
        FileWriter out = new FileWriter(file);
        out.write(contents);
        out.close();
    }

    public static void writeContents(File file, byte[] contents) throws IOException {
        FileOutputStream out = new FileOutputStream(file);
        out.write(contents);
        out.close();
    }

    
    // Returns the package name from the given file
    public static String getPackageName(File file) throws IOException {
        BufferedReader reader = new BufferedReader(new FileReader(file));
        int index = 0;
        String currentLine;
        while ((currentLine = reader.readLine()) != null)
            if ((index = currentLine.indexOf("package")) != -1)
                break;
        reader.close();
        return currentLine.substring(index + 8, currentLine.indexOf(';')).trim();
    }

    /**
     * Creates a new file in the given folder and with the given extesion. Filename is generated in order to
     * ensure no other file exists with the same name. The checking and the creation is atomic. 
     * The generated name will be as similar as possible to the preferred one.
     * 
     * @param folder The folder where the file will be created
     * @param preferredName The preferred name for the file
     * @param extension The extension for the file
     * @return An string with the filename created, including the extension
     * 
     * @throws IOException If an IO exception occurs during the process
     */
    public static String generateNewFile(File folder, String preferredName, String extension) throws IOException {
        preferredName = normalize(preferredName);
        if (preferredName.length() == 0) {
            preferredName = "f"+new Random().nextInt();
        } else if (preferredName.length() > FILENAME_MAX_LENGTH) {
            preferredName.substring(0, FILENAME_MAX_LENGTH);
        }
        int i = 0;
        String name = preferredName+"."+extension;
        File file = new File(folder, name);
        while (!file.createNewFile()) {
            name = preferredName+"-"+i+"."+extension;
            file = new File(folder, name);
            i++;
        }
        return name;
    }

    /**
     * Creates a new folder in the given containing folder. The folder name is generated in order to
     * ensure no other folder exists with the same name. The checking and the creation are not atomic
     * but the best attempt is made to ensure no overlapping occurs.
     * 
     * The generated name will be as similar as possible to the preferred one.
     * 
     * @param folder The folder where the folder will be created
     * @param preferredName The preferred name for the folder
     * @return An string with the folder name created
     * 
     * @throws IOException If an IO exception occurs during the process
     */
    public static String generateNewFolder(File folder, String preferredName) throws IOException {
        preferredName = normalize(preferredName);
        if (preferredName.length() == 0) {
            preferredName = "d"+new Random().nextInt();
        } else if (preferredName.length() > FILENAME_MAX_LENGTH) {
            preferredName.substring(0, FILENAME_MAX_LENGTH);
        }
        int i = 0;
        String name = preferredName;
        File file = new File(folder, name);
        while (true) {
            if (!file.exists()) {
                if (file.mkdirs()) {
                    return name;
                }
            }
            name = preferredName+"-"+i;
            file = new File(folder, name);
            i++;
        }
    }
    
    private static String normalize(String preferredName) {
        return preferredName.replaceAll("[^a-zA-Z0-9\\_\\-\\.]","_");
    }
}
