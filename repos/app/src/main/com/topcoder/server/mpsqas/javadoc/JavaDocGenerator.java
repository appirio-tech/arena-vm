package com.topcoder.server.mpsqas.javadoc;

import com.topcoder.server.common.RemoteFile;

import java.util.*;
import java.io.*;

import org.apache.log4j.Logger;


/**
 * Constructs the java docs for java source files.
 */
public class JavaDocGenerator {

    private static String RESOURCE = "JavaDocGeneration";
    private static ResourceBundle rb = ResourceBundle.getBundle(RESOURCE);

    private static String SOURCE_BASE = rb.getString("sourceBaseDir");
    private static String OUTPUT = rb.getString("outputDir");

    private static Logger logger = Logger.getLogger(JavaDocGenerator.class);

    private static int sequence = 1;

    /**
     * Returns a unique id for naming directories.
     */
    private synchronized static int getSeqVal() {
        return sequence++;
    }

    /**
     * Using the java doc tool, creates the javadocs for the passed source files.
     */
    public static RemoteFile[]
            generateJavaDocs(RemoteFile[] sourceFiles)
            throws JavaDocException {

        try {
            logger.info("In generateJavaDocs.");
            String unique = "jd" + getSeqVal();
            String sourceBase = SOURCE_BASE + unique + "/";
            String htmlBase = OUTPUT + unique + "/";

            //write out the source files
            File directory;
            File file;
            FileOutputStream output;

            logger.info("About to write out the files.");
            for (int i = 0; i < sourceFiles.length; i++) {
                directory = new File(sourceBase + sourceFiles[i].getBasePath());
                if (!directory.exists()) {
                    logger.info("Creating directory: " + directory.getPath());
                    directory.mkdirs();
                }

                file = new File(sourceBase + sourceFiles[i].getPath());
                logger.info("Writing file: " + file.getPath());
                output = new FileOutputStream(file);
                output.write(sourceFiles[i].getContents());
                output.close();
            }

            StringBuffer command = new StringBuffer();
            command.append("javadoc ");
            command.append("-nodeprecatedlist ");
            command.append("-noindex ");
            command.append("-nohelp ");
            command.append("-nonavbar ");
            command.append("-d ");
            command.append(htmlBase);
            command.append(" ");
            for (int i = 0; i < sourceFiles.length; i++) {
                command.append(sourceBase + sourceFiles[i].getPath());
                command.append(" ");
            }

            logger.info("Running: " + command.toString());
            //run the java doc tool
            Process process = Runtime.getRuntime().exec(command.toString());
            int result = process.waitFor();
            logger.info("Done running, exit value = " + result);

            //store the streams
            StringBuffer outputText = new StringBuffer();
            InputStream std = process.getInputStream();
            InputStream err = process.getInputStream();
            byte[] bytes = new byte[std.available()];
            int read = std.read(bytes);

            if (read >= 0) {
                outputText.append("STANDARD OUTPUT: \n");
                outputText.append(new String(bytes, 0, read));
            }

            bytes = new byte[err.available()];
            read = err.read(bytes);

            if (read >= 0) {
                outputText.append("STANDARD ERROR: \n");
                outputText.append(new String(bytes, 0, read));
            }

            std.close();
            err.close();

            logger.info("Output: \n" + outputText.toString());

            if (result != 0) {
                throw new JavaDocException("javadoc returned with non-zero exit status:"
                        + outputText.toString());
            }

            //read in the files, then delete them
            ArrayList al_files = new ArrayList();
            readFilesAndDelete(new File(htmlBase), al_files, htmlBase);

            //convert them to RemoteFile[] and return
            RemoteFile[] files = new RemoteFile[al_files.size()];
            for (int i = 0; i < al_files.size(); i++) {
                files[i] = (RemoteFile) al_files.get(i);
            }
            return files;
        } catch (JavaDocException jde) {
            throw jde;
        } catch (Exception e) {
            logger.error("Error generating javadocs.", e);
            throw new JavaDocException(e.getMessage());
        }
    }

    /**
     * Recursively adds all non-directory files to allFiles.  Deletes files
     * after adding them.
     */
    private static void readFilesAndDelete(File file, ArrayList allFiles, String htmlBase)
            throws Exception {
        if (file.isDirectory()) {
            logger.info("Entering " + file.getPath());
            File[] dirContents = file.listFiles();
            for (int i = 0; i < dirContents.length; i++) {
                readFilesAndDelete(dirContents[i], allFiles, htmlBase);
            }
        } else {
            logger.info("Reading " + file.getPath());
            allFiles.add(new RemoteFile(file, htmlBase));
        }

        file.delete();
    }
}
