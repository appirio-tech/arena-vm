package com.appirio.commons;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

import com.amazonaws.auth.DefaultAWSCredentialsProviderChain;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.sqs.AmazonSQS;
import com.amazonaws.services.sqs.AmazonSQSClient;

@Configuration
@ComponentScan
public class DefaultConfiguration {

	@Bean
	AmazonSQS getSqsClient() {
		return new AmazonSQSClient(new DefaultAWSCredentialsProviderChain());
	}
	
	@Bean
	AmazonS3 getS3Client() {
		return new AmazonS3Client(new DefaultAWSCredentialsProviderChain());
	}
	
}
