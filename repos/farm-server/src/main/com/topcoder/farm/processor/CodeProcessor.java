package com.topcoder.farm.processor;

import java.io.File;

import com.topcoder.farm.processor.api.CodeProcessingRequest;
import com.topcoder.farm.processor.api.CodeProcessingResult;

/**
 * Interface for a local code processor
 * 
 * @author james
 */
public interface CodeProcessor {

	/**
	 * Processes the given request.
	 * 
	 * @param request
	 *            The request to process
	 * @param rootFolder
	 *            A local root folder
	 * @param workFolder
	 *            A local work folder
	 * @return The result of the processing
	 */
	public CodeProcessingResult processRequest(CodeProcessingRequest request, File rootFolder, File workFolder);

}
