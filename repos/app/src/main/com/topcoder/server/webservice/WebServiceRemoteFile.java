package com.topcoder.server.webservice;

/**
 * <p>Title: </p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2002</p>
 * <p>Company: TopCoder</p>
 * @author Jeremy Nuanes
 * @version 1.0
 */

import com.topcoder.server.common.RemoteFile;
import com.topcoder.shared.language.*;

import java.io.Serializable;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.*;

public class WebServiceRemoteFile extends RemoteFile implements Serializable {

    public static final int WEB_SERVICE_INTERFACE = 1;
    public static final int WEB_SERVICE_IMPLEMENTATION = 2;
    public static final int WEB_SERVICE_HELPER = 3;
    public static final int WEB_SERVICE_CLIENT_HEADER = 4;
    public static final int WEB_SERVICE_CLIENT_SOURCE = 5;
    public static final int WEB_SERVICE_CLIENT_OBJECT = 6;
    public static final int WEB_SERVICE_USER_HELPER = 7;

    private int _type = -1;
    private Integer languageID = null;
    private Long sourceFileID = null;
    private WebServiceRemoteFile[] compiledObjectFiles;


    public int getType() {
        return _type;
    }


    public WebServiceRemoteFile(File localFile, String basePath, int type, int languageID) throws FileNotFoundException, IOException {
        super(localFile, basePath);
        validateType(type);
        _type = type;
        validateLanguageID(languageID);
        this.languageID = new Integer(languageID);
    }

    public WebServiceRemoteFile(String path, byte[] fileContents, int type, int languageID) {
        super(path, fileContents);
        validateType(type);
        _type = type;
        validateLanguageID(languageID);
        this.languageID = new Integer(languageID);
    }

    public WebServiceRemoteFile(long sourceFileID, String path, byte[] fileContents, int type, WebServiceRemoteFile[] compiledObjectFiles, int languageID) {
        this(path, fileContents, type, languageID);
        this.sourceFileID = new Long(sourceFileID);
        this.compiledObjectFiles = compiledObjectFiles;
    }

    public Integer getLanguageID() {
        return languageID;
    }

    public boolean hasSourceFileID() {
        return sourceFileID != null;
    }

    public Long getSourceFileID() {
        return sourceFileID;
    }

    public boolean hasCompiledObjectFiles() {
        return compiledObjectFiles != null && compiledObjectFiles.length > 0;
    }

    public WebServiceRemoteFile[] getCompiledObjectFiles() {
        return compiledObjectFiles;
    }

    private static void validateLanguageID(int languageID) {
        if (languageID != CPPLanguage.ID && languageID != CSharpLanguage.ID && languageID != JavaLanguage.ID) {
            throw new IllegalArgumentException("Bad language ID: " + languageID);
        }
    }

    private static void validateType(int type) {
        switch (type) {
        case WEB_SERVICE_CLIENT_HEADER:
        case WEB_SERVICE_CLIENT_OBJECT:
        case WEB_SERVICE_CLIENT_SOURCE:
        case WEB_SERVICE_HELPER:
        case WEB_SERVICE_IMPLEMENTATION:
        case WEB_SERVICE_INTERFACE:
        case WEB_SERVICE_USER_HELPER:
            return;
        default:
            throw new IllegalArgumentException("Bad web service file type: " + type);
        }
    }

    public static WebServiceRemoteFile[] toArray(Collection c) {
        return (WebServiceRemoteFile[]) c.toArray(new WebServiceRemoteFile[c.size()]);
    }
}
