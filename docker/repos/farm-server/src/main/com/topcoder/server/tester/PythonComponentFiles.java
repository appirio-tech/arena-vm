package com.topcoder.server.tester;

import java.io.File;
import java.io.FileInputStream;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.HashMap;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.topcoder.netCommon.contest.ContestConstants;
import com.topcoder.shared.common.ServicesConstants;

public final class PythonComponentFiles extends ComponentFiles {
    
    public PythonComponentFiles() {
    }
    
    public PythonComponentFiles(int userId, int contestId, int roundId, int problemId, String problemName) {
        super(userId, contestId, roundId, problemId, problemName);
    }
    
    public PythonComponentFiles(int componentId, String problemName, String classesDir) {
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
     * @author ademich
     */
    public boolean setClasses(CodeCompilation sub) {
        System.out.println("In setClasses(Submission) ...");

        boolean valid = true;
        ArrayList classFileNames = getClassFileNames(new File(getFullComponentPath() + "/compile"));
        System.out.println("classFileNames = " + classFileNames);

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

            String tmp = "", errorMsg = "";
            try {
                byte[] b = new byte[fis.available()];
                fis.read(b);
                fis.close();

                dotPath = getClassesDir() + "/" + classFile.getName();

                //strip off ".pyc" and replace all / with .
                dotPath = dotPath.substring(0, dotPath.length() - 4);
                dotPath = dotPath.replace('/', '.');

                //add the class file name and byte value to the HashMap
                classList.put(dotPath, b);
                System.out.println("now classList is = " + classList);

                for (int i = 0; i < b.length; i++) {
                    tmp += (char) b[i];
                }
            } catch (Exception e) {
                e.printStackTrace();
            }


            if (!(errorMsg.equals(""))) {
                valid = false;
                sub.setCompileError(errorMsg);
                System.out.println("WE HAVE A POSSIBLE CHEATER ON OUR HANDS!!! (Coder " + sub.getCoderID() + ")");
                System.out.println("HERE IS THEIR ERROR MSG:" + errorMsg);
                System.out.println("...AND HERE IS THEIR STINKIN' CODE...");
                System.out.println(sub.getProgramText());
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
                return fileName.indexOf(".pyc") != -1;
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
        probPathBuf.append(ServicesConstants.PYTHON_SUBMISSIONS);
        probPathBuf.append(getClassesDir());
        return (probPathBuf.toString());
    }

    protected String classMapKeyToFileName(String key) {
        return key.replace('.', '/') + ".pyc";
    }

    protected String fileNameToClassMapKey(String fileName) {
        int idx = fileName.indexOf(".pyc");
        if (idx == -1) {
        	idx = fileName.length();
//            throw new IllegalArgumentException("Invalid python class file name: " + fileName);
        }
        return fileName.substring(0, idx).replace('/', '.');
    }

    public boolean storeClasses() {
        return storeClasses(ServicesConstants.PYTHON_SUBMISSIONS);
    }
    
    @JsonIgnore
    public int getLanguageId() {
        return ContestConstants.PYTHON;
    }
}
