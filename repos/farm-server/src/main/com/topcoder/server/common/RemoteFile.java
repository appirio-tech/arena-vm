package com.topcoder.server.common;

/**
 * <p>Title: </p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2002</p>
 * <p>Company: TopCoder</p>
 * @author Jeremy Nuanes
 * @version 1.0
 */

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectStreamException;
import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.topcoder.server.util.FileUtil;
import com.topcoder.shared.netCommon.CSReader;
import com.topcoder.shared.netCommon.CSWriter;
import com.topcoder.shared.netCommon.CustomSerializable;


@SuppressWarnings("serial")
public class RemoteFile implements Serializable, CustomSerializable {

    private String _fileName;
    private String _relativePath;
    private byte[] _fileContents;

    public RemoteFile() {
    }
    
    //EX C:/topcoder/file.java, C:/ Relative path = topCoder
    //EX C:/topcoder/file.java, C:
    public RemoteFile(File localFile, String basePath) throws FileNotFoundException, IOException {
        _fileName = localFile.getName();
//        System.out.println(basePath);
//        System.out.println(localFile.getParent());
        if (basePath.endsWith("/") || basePath.endsWith("\\"))
            _relativePath = localFile.getParent().substring(basePath.length() - 1);
        //add 1 to the index if the \ or / isn't in the basePath
        else
            _relativePath = localFile.getParent().substring(basePath.length());
        _fileContents = FileUtil.getContents(localFile);
//        System.out.println(_relativePath);
//        System.out.println(_fileName);
    }

    public RemoteFile(String path, byte[] fileContents) {
        int idx = path.lastIndexOf('/');
        if (idx >= 0) {
            _fileName = path.substring(idx + 1);
            _relativePath = path.substring(0, idx);
        } else {
            _fileName = path;
            _relativePath = "";
        }
        _fileContents = fileContents;
    }


    public final File reconstruct(File localDirectory) throws FileNotFoundException, IOException {
        if (!localDirectory.exists()) {
            if (!localDirectory.mkdirs())
                throw new IOException("Could not construct directory " + localDirectory.getPath());
        }
        File fileDirectory = new File(localDirectory, _relativePath);
        if (!fileDirectory.exists()) {
            if (!fileDirectory.mkdirs())
                throw new IOException("Could not construct directory " + fileDirectory.getPath());
        }
        File file = new File(fileDirectory, _fileName);
        BufferedOutputStream stream = new BufferedOutputStream(new FileOutputStream(file));
        stream.write(_fileContents);
        stream.flush();
        stream.close();
        return file;
    }

    public final String getName() {
        return _fileName;
    }
    
    public void setName(String name) {
    	_fileName = name;
    }

    public final String getBasePath() {
        return _relativePath;
    }
    
    public void setBasePath(String path) {
    	_relativePath = path;
    }

    public void setContents(byte[] fileContents) {
        _fileContents = fileContents;
    }

    public final byte[] getContents() {
        return _fileContents;
    }

    @JsonIgnore
    public final String getPath() {
        if (_relativePath.length() > 0)
            return _relativePath + '/' + _fileName;
        else
            return _fileName;
    }

    /**
     * @see com.topcoder.shared.netCommon.CustomSerializable#customReadObject(com.topcoder.shared.netCommon.CSReader)
     */
    public void customReadObject(CSReader reader) throws IOException, ObjectStreamException {
        this._fileName =  reader.readString();
        this._relativePath = reader.readString();
        this._fileContents = reader.readByteArray();
    }

    /**
     * @see com.topcoder.shared.netCommon.CustomSerializable#customWriteObject(com.topcoder.shared.netCommon.CSWriter)
     */
    public void customWriteObject(CSWriter writer) throws IOException {
        writer.writeString(this._fileName );
        writer.writeString(this._relativePath);
        writer.writeByteArray(this._fileContents);
    }
}
