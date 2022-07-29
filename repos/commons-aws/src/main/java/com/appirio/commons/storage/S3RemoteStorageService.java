package com.appirio.commons.storage;

import java.io.File;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.GetObjectRequest;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectResult;

/**
 * S3 implementation of StorageService
 * 
 * @author james
 *
 */
@Service
public class S3RemoteStorageService implements RemoteStorageService {
	private static final Logger logger = Logger.getLogger(S3RemoteStorageService.class);
	
	private AmazonS3 s3;
	
	@Autowired
	public S3RemoteStorageService(AmazonS3 s3) {
		this.s3 = s3;
	}
	
	@Override
	public void storeFileRemotely(File localFile, String bucket, String path) {
		logger.info("Storing file " + localFile.getAbsolutePath() + " to " + bucket + '/' + path);
		
		PutObjectResult result = s3.putObject(bucket, path, localFile);
		logger.info("Stored file " + result.getETag());
	}

	@Override
	public void retrieveRemoteFile(File localFile, String bucket, String path) {
		logger.info("Retrieving file " + bucket + '/' + path + " to " + localFile.getAbsolutePath());
		
		ObjectMetadata meta = s3.getObject(new GetObjectRequest(bucket, path), localFile);
		
		logger.info("Retrieved file " + meta.getETag());
		
	}

	@Override
	public void deleteRemoteFile(String bucket, String path) {
		logger.info("Deleting file " + bucket + '/' + path);
		
		s3.deleteObject(bucket, path);
		
		logger.info("File deleted");
	}

}
