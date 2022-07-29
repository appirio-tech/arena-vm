package com.topcoder.server.webservice;

/**
 * Title:        WebServiceProblem
 * Description:  Holds all of the information needed to generate a Web
 *               Servie.
 * Copyright:    Copyright (c) 2002
 * Company:      TopCoder
 * @author       Jeremy Nuanes
 * @version 1.0
 */

import com.topcoder.server.common.RemoteFile;

import java.io.*;

public class WebServiceProblem implements Serializable {

    private int _problemId; // Reference to the _problemId for the competition
    private String _serviceName; // What the service is called
    private RemoteFile _interfaceFile; // Interface RemoteFile
    private RemoteFile _implementationFile; // Implementation of the Interface RemoteFile
    private RemoteFile[] _helperFiles; // These are other java RemoteFiles that are needed
    // by the Implementation or Interface RemoteFile.

    public int getProblemId() {
        return _problemId;
    }

    public String getServiceName() {
        return _serviceName;
    }

    public RemoteFile getInterfaceFile() {
        return _interfaceFile;
    }

    public RemoteFile getImplementationFile() {
        return _implementationFile;
    }

    public RemoteFile[] getHelperFiles() {
        return _helperFiles;
    }

    public WebServiceProblem(int problemId, String serviceName, RemoteFile interfaceFile,
            RemoteFile implementationFile, RemoteFile[] helperFiles) {
        _problemId = problemId;
        _serviceName = serviceName;
        _interfaceFile = interfaceFile;
        _implementationFile = implementationFile;
        _helperFiles = helperFiles;
    }
}
