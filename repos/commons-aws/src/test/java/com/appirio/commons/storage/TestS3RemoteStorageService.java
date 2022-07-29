package com.appirio.commons.storage;

import java.io.File;
import java.io.FileOutputStream;
import java.util.UUID;

import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.stereotype.Component;

import com.appirio.commons.DefaultConfiguration;

@Component
public class TestS3RemoteStorageService {

	private static final String TEST_BUCKET = "commons-aws-test";
	private static ApplicationContext context;

	private RemoteStorageService service;

	@BeforeClass
	public static void setupClass() throws Exception {
		context = new AnnotationConfigApplicationContext(DefaultConfiguration.class);
	}

	@Before
	public void setUp() throws Exception {
		service = context.getBean(S3RemoteStorageService.class);
	}

	@Test
	public void testService() throws Exception {
		File temp = File.createTempFile("s3-unit-test", ".txt");
		try {
			FileOutputStream fos = new FileOutputStream(temp);
			fos.write("this is only a test".getBytes("UTF-8"));
			fos.flush();
			fos.close();
			
			String path = UUID.randomUUID().toString() + "/s3-unit-test.txt";
			service.storeFileRemotely(temp, TEST_BUCKET, path);
			
			temp.delete();
			Assert.assertTrue(!temp.exists());
			
			service.retrieveRemoteFile(temp, TEST_BUCKET, path);
			Assert.assertTrue(temp.exists() && temp.length() > 0);
			
			service.deleteRemoteFile(TEST_BUCKET, path);
		} finally {
			temp.delete();
		}
	}

}
