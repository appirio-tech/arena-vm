package com.topcoder.farm.processor;

import java.io.File;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.topcoder.arena.exception.ArenaRuntimeException;
import com.topcoder.farm.controller.configuration.ApplicationContextProvider;
import com.topcoder.farm.processor.api.CodeProcessingRequest;
import com.topcoder.farm.processor.api.CodeProcessingResult;

/**
 * Service for processing code requests locally by invoking CodeProcessor
 * implementations to do the work.
 * 
 * @author james
 */
@Service
public class ProcessorService {

	private static final Logger logger = Logger.getLogger(ProcessorService.class);

	private ExecutorService executor;
	private ProcessorConfig config;

	@Autowired
	public ProcessorService(@Qualifier("processorServiceExecutor") ExecutorService executor, ProcessorConfig config) {
		this.executor = executor;
		this.config = config;
	}

	/**
	 * Process the request.
	 * 
	 * @param request
	 *            The request to process
	 * @param timeout
	 *            The time limit for processing
	 * @param timeoutUnit
	 *            The associated time limit for processing
	 * @return The result of the processing request
	 */
	public CodeProcessingResult process(final CodeProcessingRequest request, long timeout, TimeUnit timeoutUnit) {
		try {
			final CodeProcessor processor = (CodeProcessor) ApplicationContextProvider.getContext().getBean(
					request.getMetadata().getProcessorName());

			CodeProcessingResult result;
			try {
				Future<CodeProcessingResult> resultPromise = executor.submit(new Callable<CodeProcessingResult>() {

					@Override
					public CodeProcessingResult call() throws Exception {
						File rootFolder = new File(config.getRootDir());
						File workFolder = new File(config.getWorkDir());

						return processor.processRequest(request, rootFolder, workFolder);
					}
				});

				result = resultPromise.get(timeout, timeoutUnit);

			} catch (TimeoutException te) {
				logger.warn("Processor timeout:" + te.getMessage());
				result = new CodeProcessingResult(request.getMetadata(), "Processor timeout: " + te.getMessage(),
						ExceptionUtils.getStackTrace(te));
			} catch (Exception e) {
				logger.error("Processor error: " + e.getMessage(), e);
				result = new CodeProcessingResult(request.getMetadata(), e.getMessage(),
						ExceptionUtils.getStackTrace(e));
			}

			return result;

		} catch (Throwable t) {
			logger.error("An error occurred while processing: " + t.getMessage(), t);
			throw new ArenaRuntimeException(t);
		}
	}

}
