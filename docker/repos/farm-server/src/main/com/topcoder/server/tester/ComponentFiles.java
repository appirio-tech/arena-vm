/*
* Copyright (C) - 2013 TopCoder Inc., All Rights Reserved.
*/

/*
 * Author: Michael Cervantes (emcee)
 * Date: Jul 22, 2002
 * Time: 1:38:50 AM
 */
package com.topcoder.server.tester;

import java.io.Externalizable;
import java.io.File;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.io.ObjectStreamException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonTypeInfo.As;
import com.fasterxml.jackson.annotation.JsonTypeInfo.Id;
import com.topcoder.netCommon.contest.ContestConstants;
import com.topcoder.server.common.RemoteFile;
import com.topcoder.server.util.FileUtil;
import com.topcoder.shared.netCommon.CSReader;
import com.topcoder.shared.netCommon.CSWriter;
import com.topcoder.shared.netCommon.CustomSerializable;
import com.topcoder.shared.netCommon.ExternalizableHelper;

/**
 * <p>
 * the component file with the specific language type.
 * </p>
 *
 * <p>
 * Changes in version 1.0 (TC Competition Engine - R Language Test Support v1.0):
 * <ol>
 *      <li>Update {@link #getInstance(int, int, int, int, int,String)} method.</li>
 *      <li>Update {@link #getInstance(int, int, String, String)} method.</li>
 * </ol>
 * </p>
 * @author TCSASSEMBLER
 * @version 1.0
 */
@JsonTypeInfo(use = Id.CLASS, include = As.PROPERTY, property = "@class")
public abstract class ComponentFiles implements Externalizable, CustomSerializable {
//public abstract class ComponentFiles {
    private String classesDir;
    private Map<String, byte[]> classMap;
    private String problemName;
    private int componentId;

    public ComponentFiles() {

    }
    ///////////////////////////////////////////////////////////////////////////////
    public ComponentFiles(int userId, int contestId, int roundId, int componentId, String problemName)
            ///////////////////////////////////////////////////////////////////////////////
    {
        this.classMap = new HashMap<String, byte[]>();
        this.componentId = componentId;
        this.classesDir = getClassesPath(userId, contestId, roundId, componentId);
        this.problemName = problemName;
    }

    /**
     * Creates a new ComponentFiles
     *
     * @param componentId Id of the component
     * @param problemName Name of the component
     * @param classesDir String containing the path for the classes directory of this
     *                   ComponentFiles
     */
    public ComponentFiles(int componentId, String problemName, String classesDir) {
        this.classMap = new HashMap<String, byte[]>();
        this.componentId = componentId;
        this.classesDir = classesDir;
        this.problemName = problemName;
    }

    public int getComponentId() {
        return componentId;
    }
    
    public void setComponentId(int componentId) {
    	this.componentId = componentId;
    }

    protected abstract String buildFullProblemPath();

    public abstract boolean setClasses(CodeCompilation sub);

    public abstract boolean storeClasses();

    /**
     * Returns the language the components files belong
     * @return id of the language
     */
    public abstract int getLanguageId();



    public final boolean storeClasses(String path) {
        boolean retVal = true;
        try {
            // create the directory, if it does not exist
            File dir = new File(path + "/" + classesDir);

            if (!dir.exists()) {
                dir.mkdirs();
            }

            //Iterator<Entry<String, byte[]>> iter = classMap.entrySet().iterator();
            //while (iter.hasNext()) {
            for (Entry<String, byte[]> entry : classMap.entrySet()) {
                //Map.Entry entry = (Map.Entry) iter.next();
                File fileName = new File(path, classMapKeyToFileNameForStorage(entry.getKey()));
                FileUtil.writeContents(fileName, entry.getValue());
            }
        } catch (Exception e) {
            e.printStackTrace();
            retVal = false;
        }

        return retVal;
    }

    public final String getClassesDir() {
        return this.classesDir;
    }
    
    public void setClassesDir(String classesDir) {
    	this.classesDir = classesDir;
    }

    @JsonIgnore
    public final String getFullComponentPath() {
        return buildFullProblemPath();
    }

    public final String getComponentName() {
        return this.problemName;
    }
    
    public void setComponentName(String problemName) {
    	this.problemName = problemName;
    }

    public final void setClassMap(Map<String, byte[]> classMap) {
        this.classMap = classMap;
    }

    public final Map<String, byte[]> getClassMap() {
        return classMap;
    }

    /**
     * This method creates a String representing a path name to a user's compiled class files.
     *
     * @param coder_id - int - unique id identifying a specific user.
     * @param contest_id - int - unique id identifying a specific contest.
     * @param round_id - int - unique id identifying a specific round.
     * @param component_id - int - unique id identifying a specific problem.
     * @return String - The full path name to the user's compiled class files.
     *
     * author ademich
     **/
    ////////////////////////////////////////////////////////////////////////////////
    private static String getClassesPath(int coder_id, int contest_id, int round_id, int component_id) {
        ////////////////////////////////////////////////////////////////////////////////
        StringBuffer classesPathBuf = new StringBuffer();
        classesPathBuf.append("u").append(coder_id).
                append("/c").append(contest_id).
                append("/r").append(round_id).
                append("/p").append(component_id);


        return classesPathBuf.toString();
    }
    
    /**
     * <p>
     * get the instance of component file with the specific language id.
     * </p>
     * @param languageID the language id.
     * @param coderId the coder id.
     * @param contestId the contest id
     * @param roundId the round id.
     * @param componentId the component id.
     * @param className the class name of the solution.
     * @return the component file entity.
     */
    public static ComponentFiles getInstance(int languageID, int coderId, int contestId, int roundId, int componentId,
            String className) {
        ComponentFiles files;
        switch (languageID) {
        case ContestConstants.JAVA:
            files = new JavaComponentFiles(coderId, contestId, roundId, componentId, className);
            break;
        case ContestConstants.CPP:
            files = new CPPComponentFiles(coderId, contestId, roundId, componentId, className);
            break;
        case ContestConstants.CSHARP:
        case ContestConstants.VB:
            files = new DotNetComponentFiles(coderId, contestId, roundId, componentId, className, languageID);
            break;
        case ContestConstants.PYTHON:
            files = new PythonComponentFiles(coderId, contestId, roundId, componentId, className);
            break;
        case ContestConstants.R:
            files = new RComponentFiles(coderId, contestId, roundId, componentId, className);
            break;
        default:
            throw new IllegalArgumentException("Invalid language id: " + languageID);
        }
        return files;
    }

    /**
     * <p>
     * get the instance of component file with the specific language id.
     * </p>
     * @param languageID the language id.
     * @param componentId the component id.
     * @param className the class name of the solution.
     * @param classesDir the class dir.
     * @return the component file entity.
     */
    public static ComponentFiles getInstance(int languageID, int componentId, String className, String classesDir) {
        ComponentFiles files;
        switch (languageID) {
        case ContestConstants.JAVA:
            files = new JavaComponentFiles(componentId, className, classesDir);
            break;
        case ContestConstants.CPP:
            files = new CPPComponentFiles(componentId, className, classesDir);
            break;
        case ContestConstants.CSHARP:
        case ContestConstants.VB:
            files = new DotNetComponentFiles(componentId, className, classesDir, languageID);
            break;
        case ContestConstants.PYTHON:
            files = new PythonComponentFiles(componentId, className, classesDir);
            break;
        case ContestConstants.R:
            files = new RComponentFiles(componentId, className, classesDir);
            break;
        default:
            throw new IllegalArgumentException("Invalid language id: " + languageID);
        }
        return files;
    }

    /**
     * New method for handling non PATH storage in SRM NET component files.
     *
     * @param key The key stored in the map
     *
     * @return The filename containing required path prefix
     */
    protected String classMapKeyToFileNameForStorage(String key) {
        return classMapKeyToFileName(key);
    }

    protected String classMapKeyToFileName(String key) {
        return key;
    }

    protected String fileNameToClassMapKey(String fileName) {
        return fileName;
    }

    public final void addClassFile(String path, byte[] bytes) {
        classMap.put(fileNameToClassMapKey(path), bytes);
    }

    @JsonIgnore
    public final List<RemoteFile> getClassFiles() {
        ArrayList<RemoteFile> r = new ArrayList<RemoteFile>(classMap.size());
        for (Entry<String, byte[]> entry : classMap.entrySet()) {
            //Map.Entry entry = (Map.Entry) it.next();
        	r.add(new RemoteFile(entry.getKey(), entry.getValue()));
        }
        return r;
    }

    /**
     * @see com.topcoder.shared.netCommon.CustomSerializable#customReadObject(com.topcoder.shared.netCommon.CSReader)
     */
    public void customReadObject(CSReader reader) throws IOException, ObjectStreamException {
        this.classMap = reader.readHashMap();
        this.componentId = reader.readInt();
        this.classesDir = reader.readString();
        this.problemName = reader.readString();
    }

    /**
     * @see com.topcoder.shared.netCommon.CustomSerializable#customWriteObject(com.topcoder.shared.netCommon.CSWriter)
     */
    @SuppressWarnings("rawtypes")
	public void customWriteObject(CSWriter writer) throws IOException {
        writer.writeHashMap((HashMap) this.classMap);
        writer.writeInt(this.componentId);
        writer.writeString(this.classesDir);
        writer.writeString(this.problemName);
    }

    public void writeExternal(ObjectOutput out) throws IOException {
        ExternalizableHelper.writeExternal(out, this);
    }

    public void readExternal(ObjectInput in) throws IOException {
        ExternalizableHelper.readExternal(in, this);
    }
}
