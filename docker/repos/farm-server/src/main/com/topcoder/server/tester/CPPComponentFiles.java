package com.topcoder.server.tester;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.topcoder.netCommon.contest.ContestConstants;
import com.topcoder.shared.util.logging.*;
import com.topcoder.shared.common.ServicesConstants;

public final class CPPComponentFiles extends ComponentFiles {
    
    private static Logger logger = Logger.getLogger(CPPComponentFiles.class);

    public CPPComponentFiles() {
    }
    
    public CPPComponentFiles(int userId, int contestId, int roundId, int problemId, String problemName) {
        super(userId, contestId, roundId, problemId, problemName);
    }

    public CPPComponentFiles(int componentId, String problemName, String classesDir) {
        super(componentId, problemName, classesDir);
    }

    /**
     * Will retrieve the executable name and create a byte array out of the executable file.
     * The byte array will then be put in the classList.
     */
    public boolean setClasses(CodeCompilation sub) {
        ArrayList classFileNames;
        String fileName;
        String className;
        FileInputStream fis;
        byte[] b;
        
        long sz = 0;

        try {
            HashMap classList = new HashMap();
            classFileNames = getClassFileNames(new File(getFullComponentPath()));
            fileName = (String) classFileNames.get(0);
            
            String bckFileName;
            String bckClassName;

            /* fileName is an absolute path, but we need a relative path for the map */
            className = getClassesDir() + "/" + getComponentName();
            
            bckFileName = fileName;
            bckClassName = className;

            fis = new FileInputStream(fileName);
            
            sz += fis.available();
            
            if(sz > 40000000)
            {
                logger.debug("RYAN:" + sz);
                return false;
            }
            
            b = new byte[fis.available()];
            fis.read(b);
            fis.close();

            //add the class file name and byte value to the HashMap
            classList.put(className, b);
            try{
                fileName += ".o";
                className += ".o";
                fis = new FileInputStream(fileName);

                sz += fis.available();

                if(sz > 40000000)
                {
                    logger.debug("RYAN:" + sz);
                    return false;
                }

                b = new byte[fis.available()];
                fis.read(b);
                fis.close();
                // add the object file name and byte value to the HashMap
                classList.put(className, b);

                //lookup team object
            } catch(FileNotFoundException fe) {
                //ignore (long problems skip object files)
            }
            fileName = bckFileName + ".to";
            className = bckClassName + ".to";
            
            try {
                fis = new FileInputStream(fileName);

                sz += fis.available();

                if(sz > 40000000)
                {
                    logger.debug("RYAN:" + sz);
                    return false;
                }

                b = new byte[fis.available()];
                fis.read(b);
                fis.close();
                // add the object file name and byte value to the HashMap
                classList.put(className, b);
                
                fileName = bckFileName + ".h";
                className = bckClassName + ".h";
                
                fis = new FileInputStream(fileName);

                sz += fis.available();

                if(sz > 40000000)
                {
                    logger.debug("RYAN:" + sz);
                    return false;
                }

                b = new byte[fis.available()];
                fis.read(b);
                fis.close();
                // add the object file name and byte value to the HashMap
                classList.put(className, b);
            } catch(FileNotFoundException fe) {
                //ignore
            }
            setClassMap(classList);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }


    /**
     * Method to write the C++ executable to the file system exactly as it was
     * compiled.
     * If the directory does not exist, it will be created.
     * The sandbox tries to chmod +x the file, so we needn't do that here.
     *
     * @author ademich
     */
    ///////////////////////////////////////////////////////////////////////////////
    public boolean storeClasses() {
        return storeClasses(ServicesConstants.CPP_SUBMISSIONS);
    }


    /**
     * This method creates an ArrayList containing one element: The CPP executable name
     * from the given classDir.
     *
     * @param classDir - File - A File object containing a directory where a user's compiled
     *                          class files are located.
     * @return ArrayList - String of CPP executable file name.
     *
     **/
    private ArrayList getClassFileNames(File classDir) {
        ArrayList classFiles = new ArrayList();
        StringBuffer fullClassName = new StringBuffer();
        fullClassName.append(classDir.getAbsolutePath()).
                append("/").
                append(getComponentName());
        classFiles.add(fullClassName.toString());
        return classFiles;
    }

    /**
     * Method to set the full path name where the Java class files or the CPP
     * executable exist.
     *
     * @return the full path name
     * @author ademich
     */
    protected String buildFullProblemPath() {
        StringBuffer probPathBuf = new StringBuffer();
        probPathBuf.append(ServicesConstants.CPP_SUBMISSIONS);
        probPathBuf.append(getClassesDir());
        return (probPathBuf.toString());
    }
    
    @JsonIgnore
    public int getLanguageId() {
        return ContestConstants.CPP;
    }
}
