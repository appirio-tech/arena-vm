/*
 * Copyright (C) - 2022 TopCoder Inc., All Rights Reserved.
 */
package com.topcoder.server.tester;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.topcoder.netCommon.contest.ContestConstants;
import com.topcoder.shared.common.ServicesConstants;

/**
 * Python3 component files.
 *
 * @author liuliquan
 * @version 1.0
 */
public class Python3ComponentFiles extends PythonComponentFiles {

    public Python3ComponentFiles() {
    }
    
    public Python3ComponentFiles(int userId, int contestId, int roundId, int problemId, String problemName) {
        super(userId, contestId, roundId, problemId, problemName);
    }
    
    public Python3ComponentFiles(int componentId, String problemName, String classesDir) {
        super(componentId, problemName, classesDir);
    }

    /**
     * Method to set the full path name where the Java class files or the CPP
     * executable exist.
     *
     * @return the full path name
     */
    protected String buildFullProblemPath() {
        StringBuffer probPathBuf = new StringBuffer();
        probPathBuf.append(ServicesConstants.PYTHON3_SUBMISSIONS);
        probPathBuf.append(getClassesDir());
        return (probPathBuf.toString());
    }

    public boolean storeClasses() {
        return storeClasses(ServicesConstants.PYTHON3_SUBMISSIONS);
    }
    
    @JsonIgnore
    public int getLanguageId() {
        return ContestConstants.PYTHON3;
    }
}
