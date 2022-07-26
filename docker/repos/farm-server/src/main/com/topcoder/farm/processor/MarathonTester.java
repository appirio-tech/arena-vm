package com.topcoder.farm.processor;

import java.io.File;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Component;

import com.topcoder.farm.processor.api.CodeProcessingRequest;
import com.topcoder.farm.processor.api.CodeProcessingResult;
import com.topcoder.server.farm.longtester.FarmLongTestRequest;
import com.topcoder.server.farm.longtester.MarathonCodeTestRequest;
import com.topcoder.services.tester.common.LongTestResults;
import com.topcoder.services.tester.type.longtest.FarmLongTester;

/**
 * Processor for testing marathon match code.
 * 
 * @author james
 */
@Component("marathonTester")
public class MarathonTester implements CodeProcessor {

	private static final Logger logger = Logger.getLogger(MarathonTester.class);

	@Override
	public CodeProcessingResult processRequest(CodeProcessingRequest request, File rootFolder, File workFolder) {

		if (logger.isInfoEnabled()) {
			logger.info("Procesing marathon test request " + ToStringBuilder.reflectionToString(request));
		}

		MarathonCodeTestRequest mRequest = (MarathonCodeTestRequest) request.getRequestData();

		FarmLongTestRequest longTestRequestData = mRequest.getLongTestRequest();
		longTestRequestData.setComponentFiles(mRequest.getComponentFiles());
		longTestRequestData.setSolution(mRequest.getSolution());

		CodeProcessingResult res = new CodeProcessingResult(request.getMetadata());
		
		try {
			LongTestResults testResults = new FarmLongTester(rootFolder, workFolder).processLongTest(longTestRequestData);
			res.setResultData(testResults);
		} catch (Throwable t) {
			logger.error("Marathon tester error: " + t.getMessage(), t);
			res.setErrorDetails(ExceptionUtils.getStackTrace(t));
			res.setErrorMessage(t.getMessage());
		}

		return res;
	}

}
