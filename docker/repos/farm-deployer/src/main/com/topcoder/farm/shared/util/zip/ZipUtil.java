/*
 * ZipUtil
 * 
 * Created 08/31/2006
 */
package com.topcoder.farm.shared.util.zip;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * ZipUtil is a Helper class that simplifies zipping/unzipping of multiples 
 * files and folders
 * 
 * @author Diego Belfer (mural)
 * @version $Id$
 */
public class ZipUtil {
    private static Log log = LogFactory.getLog(ZipUtil.class);
    /**
     * Unzip the zip content provided by <code>binaryStream</code> to folder
     * dstFolder
     * 
     * @param binaryStream The InputStream providing zip content
     * @param dstFolder The folder where zip files are going to be unzipped
     * @throws IOException If an IOException is thrown during the process
     */
    public static void unzip(InputStream binaryStream, File dstFolder) throws IOException {
        if (log.isTraceEnabled()) {
            log.trace("Unzipping to: "+dstFolder.getAbsolutePath());
        }
        FileUtils.forceMkdir(dstFolder);
        ZipInputStream resource = new ZipInputStream(new BufferedInputStream(binaryStream));
        ZipEntry entry = resource.getNextEntry();
        while (entry != null) {
            File targetFile = new File(dstFolder, entry.getName());
            if (entry.isDirectory()) {
                if (log.isTraceEnabled()) {
                    log.trace("Unzipping folder: "+targetFile.getAbsolutePath());
                }
                FileUtils.forceMkdir(targetFile);
            } else {
                if (log.isTraceEnabled()) {
                    log.trace("Unzipping file: "+targetFile.getAbsolutePath());
                }
                FileUtils.forceMkdir(targetFile.getParentFile());
                OutputStream os = new BufferedOutputStream(new FileOutputStream(targetFile));
                IOUtils.copy(resource, os);
                os.flush();
                os.close();
            }
            entry = resource.getNextEntry();
        }
    }
    
    /**
     * Creates a new zip file with the name provided in dstFile and add to it
     * the <code>file</code>. If file correspond to a directory, then the contents of the folder 
     * are added. Otherwise, the file is added<p>
     * 
     *  If you call this method with File[]{dir1/filename.txt, dir2/subdir}, zip entries
     *  are going to be: 
     *      <li>filename.txt
     *      <li>subdir/* 
     * 
     * @param file File/directory containg source to add to the zip
     * @param dstFile destination file
     * @throws IOException If an IOException is thrown during the process
     */
    public static void zip(File file, File dstFile) throws IOException {
        zip(new File[] {file}, dstFile);
    }
    
    /**
     * Creates a new zip file with the name provided in <code>dstFile</code> and add to it
     * all the <code>files</code>. When a given file corresponds to a directory, the content of the
     * directory will be added. (without the directory prefix) 
     * 
     * @param files Files/directories containg source to add to the zip
     * @param dstFile destination file
     * @throws IOException If an IOException is thrown during the process
     */
    public static void zip(File[] files, File dstFile) throws IOException {
        if (log.isTraceEnabled()) {
            log.trace("Creating zip file:" + dstFile.getCanonicalPath());
        }
        ZipOutputStream zipOs = new ZipOutputStream(new FileOutputStream(dstFile));
        try {
            List<File> fs = new LinkedList<File>();
            for (int i = 0; i < files.length; i++) {
                File file = files[i];
                if (file.isDirectory()) {
                    if (log.isTraceEnabled()) {
                        log.trace("Adding folder:" + file.getCanonicalPath());
                    }
                    fs.addAll(Arrays.asList(file.listFiles()));
                } else {
                    if (log.isTraceEnabled()) {
                        log.trace("Adding file:" + file.getCanonicalPath());
                    }
                    fs.add(file);
                }
            }
            addFiles(fs.toArray(new File[fs.size()]), "", zipOs);
        } finally {
            IOUtils.closeQuietly(zipOs);
        }
    }

    /**
     * Adds all <code>files</code> to the zip using the file <code>prefix</code> given.
     *  
     * @param files The files to add
     * @param prefix The prefix to use in zip entries
     * @param zipOs The ZipOutputStream of the zipfile.
     * @throws IOException If an IOException is thrown during the process 
     */
    private static void addFiles(File[] files, String prefix, ZipOutputStream zipOs) throws IOException {
        for (int i = 0; i < files.length; i++) {
            File file = files[i];
            addFile(prefix, file, zipOs);
        }
    }

    /**
     * Adds the given <code>file</code> to the zip using the file <code>prefix</code> given.
     * If the file is a directory all files contained in the folder are added using a prefix
     * equals to prefix + file.getName() + '/'
     *  
     * @param file The file to add.
     * @param prefix The prefix to use in zip entries
     * @param zipOs The ZipOutputStream of the zipfile.
     * @throws IOException If an IOException is thrown during the process 
     */
    private static void addFile(String prefix, File file, ZipOutputStream zipOs) throws IOException {
        if (!file.isDirectory()) {
            if (log.isTraceEnabled()) {
                log.trace("Adding file:" + file.getCanonicalPath());
            }
            ZipEntry zipEntry = new ZipEntry(prefix+file.getName());
            zipOs.putNextEntry(zipEntry);
            FileInputStream is = new FileInputStream(file);
            try {
                IOUtils.copy(is, zipOs);
            } finally {
                IOUtils.closeQuietly(is);
            }
            zipOs.closeEntry();
        } else {
            addFiles(file.listFiles(), prefix+file.getName()+"/", zipOs);
        }
    }
}
