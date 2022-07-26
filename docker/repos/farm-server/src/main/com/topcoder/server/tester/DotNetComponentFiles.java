package com.topcoder.server.tester;
import java.io.File;
import java.io.FileInputStream;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.io.IOUtils;

import com.topcoder.shared.common.ServicesConstants;
import com.topcoder.shared.language.CSharpLanguage;
import com.topcoder.shared.util.logging.Logger;

/**
 * Renamed from CSharpComponentFiles 
 */
public class DotNetComponentFiles extends ComponentFiles {
    private static Logger logger = Logger.getLogger(DotNetComponentFiles.class);
    private int languageId = CSharpLanguage.ID;
    private static final String DLL = "dll";
    private static final String PDB = "pdb";
//    private static final String LANGUAGE = "language";

    public DotNetComponentFiles() {
    }
    
    protected DotNetComponentFiles(int userId, int contestId, int roundId, int problemId, String problemName, int languageId) {
        super(userId, contestId, roundId, problemId, problemName);
        this.languageId = languageId;
    }

    public DotNetComponentFiles(int componentId, String problemName, String classesDir, int languageId) {
        super(componentId, problemName, classesDir);
        this.languageId = languageId;
    }

    public boolean storeClasses() {
        return storeClasses(ServicesConstants.CSHARP_SUBMISSIONS);
    }
     
    public boolean setClasses(CodeCompilation sub) {
        String fileName;
        String className;
        FileInputStream fis = null;
        byte[] b;
        
        long sz = 0;

        try {
            Map<String, byte[]> classList = new HashMap<String, byte[]>();
            if (sub instanceof LongSubmission) {
                // Load long submission compilation files
                fileName = getClassFileNames(new File(getFullComponentPath()));
                
                /* fileName is an absolute path, but we need a relative path for the map */
                className = getClassesDir() + '/' + getComponentName() + ".exe";
                fis = new FileInputStream(fileName);
                sz += fis.available();
                if(sz > 40000000) {
                    logger.debug("RYAN:" + sz);
                    return false;
                }
                b = new byte[fis.available()];
                fis.read(b);
                fis.close();
                classList.put(className.replace('/',File.separatorChar), b);
          
                className = className.substring(0,className.length()-4)+".netmodule";
                fileName = fileName.substring(0,fileName.length()-4)+".netmodule";

                fis = new FileInputStream(fileName);
                sz += fis.available();
                if(sz > 40000000) {
                    logger.debug("RYAN:" + sz);
                    return false;
                }
                b = new byte[fis.available()];
                fis.read(b);
                fis.close();
                classList.put(className.replace('/',File.separatorChar), b);
            
                className = getClassesDir() + '/' + ((LongSubmission)sub).getWrapperClassName() + ".netmodule";
                fileName = new File(getFullComponentPath()).getAbsolutePath() + File.separator + 
                    ((LongSubmission)sub).getWrapperClassName() + ".netmodule";

                fis = new FileInputStream(fileName);
                sz += fis.available();
                if(sz > 40000000) {
                    logger.debug("RYAN:" + sz);
                    return false;
                }
                b = new byte[fis.available()];
                fis.read(b);
                fis.close();
                classList.put(className.replace('/',File.separatorChar), b);
            } else {
                // Algo submission
                fileName = new File(getFullComponentPath(), getComponentName() + ".dll").getAbsolutePath();
                fis = new FileInputStream(fileName);
                sz += fis.available();
                if(sz > 40000000) {
                    logger.debug("RYAN:" + sz);
                    return false;
                }
                b = new byte[fis.available()];
                fis.read(b);
                fis.close();
                classList.put(DLL, b);

                // Algo submission
                fileName = new File(getFullComponentPath(), getComponentName() + ".pdb").getAbsolutePath();
                fis = new FileInputStream(fileName);
                sz += fis.available();
                if(sz > 40000000) {
                    logger.debug("RYAN:" + sz);
                    return false;
                }
                b = new byte[fis.available()];
                fis.read(b);
                fis.close();
                classList.put(PDB, b);
                languageId = sub.getLanguage();
                //classList.put(LANGUAGE, new Integer(sub.getLanguage()));
            }

            setClassMap(classList);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        } finally {
        	IOUtils.closeQuietly(fis);
        }
    }

    protected String buildFullProblemPath() {
        StringBuffer probPathBuf = new StringBuffer();
        probPathBuf.append(ServicesConstants.CSHARP_SUBMISSIONS);
        probPathBuf.append(getClassesDir());
        return (probPathBuf.toString());
    }

    private String getClassFileNames(File classDir) {
        StringBuffer fullClassName = new StringBuffer();
        fullClassName.append(classDir.getAbsolutePath()).
                append(File.separatorChar).
                append(getComponentName()).append(".exe");
        return fullClassName.toString();
    }

    public int getLanguageId() {
        return languageId;
    }
    
    public void setLanguageId(int languageId) {
    	this.languageId = languageId;
    }
    
    protected String classMapKeyToFileNameForStorage(String key) {
        /*
         * SRM map contains "dll" and "pdb" without the path.  
         */
        if ("dll".equals(key) || "pdb".equals(key)) {
            return getClassesDir()+"/"+getComponentName()+"."+key;
        } else {
            return super.classMapKeyToFileNameForStorage(key);
        }
    }
    
//    /**
//     * @see com.topcoder.server.tester.ComponentFiles#customReadObject(com.topcoder.shared.netCommon.CSReader)
//     */
//    public void customReadObject(CSReader reader) throws IOException, ObjectStreamException {
//        super.customReadObject(reader);
//        languageId = reader.readInt();
//    }
//    
//    /**
//     * @see com.topcoder.server.tester.ComponentFiles#customWriteObject(com.topcoder.shared.netCommon.CSWriter)
//     */
//    public void customWriteObject(CSWriter writer) throws IOException {
//        super.customWriteObject(writer);
//        writer.writeInt(languageId);
//    }
}
