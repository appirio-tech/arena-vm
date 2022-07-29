package com.appirio.commons.storage;

import java.io.File;

/**
 * Interface for a simple remote storage service
 * 
 * @author james
 *
 */
public interface RemoteStorageService {
	public void storeFileRemotely(File localFile, String bucket, String path);
	
	public void retrieveRemoteFile(File localFile, String bucket, String path);
	
	public void deleteRemoteFile(String bucket, String path);
}
