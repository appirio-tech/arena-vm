/*
 * Copyright (C) 2013 TopCoder Inc., All Rights Reserved.
 */
package com.topcoder.server.tester;

import java.io.File;
import java.io.FileInputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import com.topcoder.netCommon.contest.ContestConstants;
import com.topcoder.services.compiler.util.RLongCodeGenerator;
import com.topcoder.shared.common.ServicesConstants;
import com.topcoder.shared.util.logging.Logger;

/**
 * <p>
 * the R language component files.
 * </p>
 * @author TCSASSEMBLER
 * @version 1.0
 */
public final class RComponentFiles extends ComponentFiles {
    /**
     * <p>the default constructor</p>
     */
    public RComponentFiles() {
        //empty.
    }
    
    /**
     * <p>
     * construct the R language component files.
     * </p>
     * @param userId the user id.
     * @param contestId the contest id.
     * @param roundId the contest round id.
     * @param problemId the problem id.
     * @param problemName the problem name.
     */
    public RComponentFiles(int userId, int contestId, int roundId, int problemId, String problemName) {
        super(userId, contestId, roundId, problemId, problemName);
    }
    
    /**
     * <p>
     * construct the R language component files.
     * </p>
     * @param componentId the component id.
     * @param problemName the problem name.
     * @param classesDir the class directory.
     */
    public RComponentFiles(int componentId, String problemName, String classesDir) {
        super(componentId, problemName, classesDir);
    }

    /**
     * Process each generated class file. The classList is populated here with the key
     * being the path to a class file and the value being the byte array representation
     * of the class file.
     * CodeCompiler.checkCode is called to perform preliminary code checks specific to
     * each problem.
     *
     * @param sub - Submission
     * @return boolean - False is there were any errors encountered or if the code was
     *                    deemed possibly malicious. True otherwise.
     */
    public boolean setClasses(CodeCompilation sub) {
        boolean valid = true;
        ArrayList classFileNames = getClassFileNames(new File(getFullComponentPath() + "/compile"));

        File classFile;
        FileInputStream fis;
        String dotPath;
        String fileName;
        HashMap classList = new HashMap();
        setClassMap(classList);

        for (int j = 0; j < classFileNames.size(); j++) {
            try {
                fileName = (String) classFileNames.get(j);
                classFile = new File(fileName);
                fis = new FileInputStream(fileName);
            } catch (Exception e) {
                sub.setCompileError("Compilation failed because no class file was created.");
                valid = false;
                break;
            }

            String errorMsg = "";
            try {
                byte[] b = new byte[fis.available()];
                fis.read(b);
                dotPath = getClassesDir() + "/" + classFile.getName();

                //strip off ".rlc" and replace all / with .
                dotPath = dotPath.substring(0, dotPath.length() - 4);
                dotPath = dotPath.replace('/', '.');

                //add the class file name and byte value to the HashMap
                classList.put(dotPath, b);
            } catch (Exception e) {
                errorMsg = e.getMessage();
            } finally {
                try {
                    if (fis != null)
                        fis.close();
                } catch(IOException e1) {
                   
                }
            }


            if (!(errorMsg.equals(""))) {
                valid = false;
                sub.setCompileError(errorMsg);
                break;
            }
        }
        return valid;
    }

    /**
     * This method creates an ArrayList of Java class file names from the given classDir.
     *
     * @param classDir - File - A File object containing a directory where a user's compiled
     *                          class files are located.
     * @return ArrayList - Strings of class file names.
     *
     **/
    private static ArrayList getClassFileNames(File classDir) {
        String list[];
        ArrayList classFiles = new ArrayList();

        list = classDir.list(new FilenameFilter() {
            public boolean accept(File dir, String file) {
                String fileName = new File(file).getName().toLowerCase();
                return fileName.indexOf(".rlc") != -1;
            }
        }
        );

        StringBuffer fullClassName;
        if (list != null) {
            for (int i = 0; i < list.length; i++) {
                fullClassName = new StringBuffer();
                fullClassName.append(classDir.getAbsolutePath()).
                        append("/").
                        append(list[i]);
                classFiles.add(fullClassName.toString());
            }
        }

        return classFiles;
    }

    /**
     * Method to set the full path name where the Java class files or the CPP
     * executable exist.
     *
     * @return the full path name
     */
    protected String buildFullProblemPath() {
        StringBuffer probPathBuf = new StringBuffer();
        probPathBuf.append(ServicesConstants.R_SUBMISSIONS);
        probPathBuf.append(getClassesDir());
        return (probPathBuf.toString());
    }

    /**
     * <p>
     * map the class file key.
     * </p>
     */
    protected String classMapKeyToFileName(String key) {
        return key.replace('.', '/') + ".rlc";
    }

    /**
     * <p>
     * convert the file extension of R language.
     * </p>
     */
    protected String fileNameToClassMapKey(String fileName) {
        int idx = fileName.indexOf(".rlc");
        if (idx == -1) {
            throw new IllegalArgumentException("Invalid R class file name: " + fileName);
        }
        return fileName.substring(0, idx).replace('/', '.');
    }

    /**
     * <p>
     * store the class file maps.
     * </p>
     */
    public boolean storeClasses() {
        return storeClasses(ServicesConstants.R_SUBMISSIONS);
    }
    
    /**
     * <p>
     * get the current language id.
     * </p>
     */
    public int getLanguageId() {
        return ContestConstants.R;
    }
}
