package com.appirio.commons.mq;

import java.util.Arrays;
import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.stereotype.Component;

import com.appirio.commons.DefaultConfiguration;

@Component
public class TestSqsMessageQueueService {

	private static final String TEST_QUEUE = "james-arena-dev";
	private static final int DEFAULT_WAIT_TIME = 4;
	private static ApplicationContext context;

	private MessageQueueService service;

	@BeforeClass
	public static void setupClass() throws Exception {
		context = new AnnotationConfigApplicationContext(
				DefaultConfiguration.class);
	}

	@Before
	public void setUp() throws Exception {
		service = context.getBean(SqsMessageQueueService.class);
	}

	@Test
	public void testQueueService() {
		
		Assert.assertTrue(service.isQueueAvailable(TEST_QUEUE));
		
		Assert.assertFalse(service.isQueueAvailable("doesnotexist"));
		
		service.sendMessages(TEST_QUEUE,
				Arrays.asList(new Message("m1"), new Message("m2")));

		List<Message> msgs = service.retrieveMessages(TEST_QUEUE, 2, DEFAULT_WAIT_TIME, false);

		Assert.assertEquals(2, msgs.size());

		// messages should not be visible now that we've retrieved them
		Assert.assertEquals(0,
				service.retrieveMessages(TEST_QUEUE, 10, DEFAULT_WAIT_TIME, false).size());

		// return the messages to the queue
		service.returnMessageToQueue(TEST_QUEUE, msgs.get(0).getMetadata());
		service.returnMessageToQueue(TEST_QUEUE, msgs.get(1).getMetadata());

		// retrieve again but peek
		msgs = service.retrieveMessages(TEST_QUEUE, 2, DEFAULT_WAIT_TIME, true);

		Assert.assertEquals(2, msgs.size());
		
		// retrieve again and verify the messages are available since they should be returned w/peek=true on the
		// previous call. note - we need to retrieve w/out peek prior to delete
		msgs = service.retrieveMessages(TEST_QUEUE, 2, DEFAULT_WAIT_TIME, false);
		Assert.assertEquals(2, msgs.size());

		service.deleteMessage(TEST_QUEUE, msgs.get(0).getMetadata());
		service.deleteMessage(TEST_QUEUE, msgs.get(1).getMetadata());

	}

}
