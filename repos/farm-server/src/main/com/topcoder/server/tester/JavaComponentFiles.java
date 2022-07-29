package com.topcoder.server.tester;

import java.io.File;
import java.io.FileInputStream;
import java.io.FilenameFilter;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.topcoder.netCommon.contest.ContestConstants;
import com.topcoder.shared.common.ServicesConstants;

@SuppressWarnings("serial")
public final class JavaComponentFiles extends ComponentFiles implements Serializable {
    private boolean threadingAllowed = false;

    public JavaComponentFiles() {
    }

    public JavaComponentFiles(int userId, int contestId, int roundId, int problemId, String problemName) {
        super(userId, contestId, roundId, problemId, problemName);
    }

    public JavaComponentFiles(int componentId, String problemName, String classesDir) {
        super(componentId, problemName, classesDir);
    }

    @JsonIgnore
    public String getPackageName() {
        return getClassesDir().replace('/', '.');
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
        ArrayList classFileNames = getClassFileNames(new File(ServicesConstants.JAVA_SUBMISSIONS + "/compile/" + getClassesDir() + "/"));
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

            String errorMsg = "";
            try {
                byte[] b = new byte[fis.available()];
                fis.read(b);
                fis.close();

                System.out.println("SIZE IS: " + b.length);

                dotPath = getClassesDir() + "/" + classFile.getName();

                //strip off ".class" and replace all / with .
                dotPath = dotPath.substring(0, dotPath.length() - 6);
                dotPath = dotPath.replace('/', '.');

                //add the class file name and byte value to the HashMap
                classList.put(dotPath, b);
                System.out.println("now classList is = " + classList);
                StringBuffer tmp = new StringBuffer((int)(b.length*1.1));
                for (int i = 0; i < b.length; i++) {
                    tmp.append((char) b[i]);
                }

                // Do any preliminary code checks specific to each problem
                if(!(sub instanceof LongSubmission) || !( classFile.getName().equals(((LongSubmission)sub).getWrapperClassName() + "$Stopwatch.class") ||
                        classFile.getName().equals(((LongSubmission)sub).getWrapperClassName() + ".class") ||
                        classFile.getName().equals("Wrapper.class") || classFile.getName().equals("Wrapper$Waiter.class"))){
                    //System.out.println("HERE:" + classFile.getName());
                    //System.out.println("HERE:" + tmp);
                    errorMsg = checkCode(sub.getComponentID(), tmp.toString());
                }
                //System.out.println("HERE I AM"+tmp.toString());
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

    ////////////////////////////////////////////////////////////////////////////////
    private String checkCode(int problemId, String programText)
            ////////////////////////////////////////////////////////////////////////////////
    {
        String retVal = "";
        int index;

        // Do any preliminary code checks specific to each problem
        switch (problemId) {
        case 23:
            index = programText.indexOf('%');
            if (index != -1)
                retVal = "Compilation failed because you cannot use the '%' character";
            break;
        case 35:
            index = programText.indexOf("StringTokenizer");
            if (index != -1)
                retVal = "Compilation failed because you cannot use StringTokenizer objects";
            break;
        case 46:
            index = programText.indexOf("import");
            if (index != -1)
                retVal = "Compilation failed because you cannot use any import statements in your program";
            break;
        case 77:
            index = programText.indexOf("Calendar");
            if (index != -1)
                retVal = "Compilation failed because you cannot make any references to class Calendar";
            break;
        default:
            break;
        }

        String[] classes;

        // RuntimeException is OK, but Runtime is not
        int indexRE = programText.indexOf("RuntimeException");
        index = programText.indexOf("Runtime");
        while(index != -1) {
            if (indexRE != index) {
                retVal = "Compilation failed because there is no need to make use of 'Runtime' anywhere in your code, for security purposes.";
                return retVal;
            } else {
                indexRE = programText.indexOf("RuntimeException",index+1);
                index   = programText.indexOf("Runtime",index+1);
            }
        }
        classes = new String[] {"java/beans", "java/net", "java/io/File", "Thread", "Runnable", "SecurityManager", "reflect", "TimerTask", "ClassLoader"};
        if(isThreadingAllowed())
            classes = new String[] {"java/beans", "java/net", "java/io/File", "SecurityManager", "reflect", "TimerTask", "ClassLoader"};

        for (int i = 0; i < classes.length; i++) {
            index = programText.indexOf(classes[i]);
            if (index != -1)
                retVal = "Compilation failed because there is no need to make use of '" + classes[i].replace('/', '.')
                        + "' anywhere in your code, for security purposes.";

        }

        return retVal;
    }

    public boolean storeClasses() {
        return storeClasses(ServicesConstants.JAVA_SUBMISSIONS);
    }
    
    /**
     * This method creates an ArrayList of Java class file names from the given classDir.
     *
     * @param classDir - File - A File object containing a directory where a user's compiled
     *                          class files are located.
     * @return ArrayList - Strings of class file names.
     *
     **/
    @SuppressWarnings("rawtypes")
	private ArrayList getClassFileNames(File classDir) {
        String list[];
        ArrayList classFiles = new ArrayList();

        list = classDir.list(new FilenameFilter() {
            public boolean accept(File dir, String file) {
                String fileName = new File(file).getName().toLowerCase();
                return fileName.indexOf(".class") != -1;
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
        probPathBuf.append(ServicesConstants.JAVA_SUBMISSIONS);
        probPathBuf.append(getClassesDir());
        return (probPathBuf.toString());
    }

    protected String classMapKeyToFileName(String key) {
        return key.replace('.', '/') + ".class";
    }

    protected String fileNameToClassMapKey(String fileName) {
        int idx = fileName.indexOf(".class");
        if (idx == -1) {
        	idx = fileName.length();
//            throw new IllegalArgumentException("Invalid java class file name: " + fileName);
        }
        return fileName.substring(0, idx).replace('/', '.');
    }

    @JsonIgnore
    public int getLanguageId() {
        return ContestConstants.JAVA;
    }

    public boolean isThreadingAllowed() {
        return threadingAllowed;
    }

    public void setThreadingAllowed(boolean allowThreading) {
        this.threadingAllowed = allowThreading;
    }
}
