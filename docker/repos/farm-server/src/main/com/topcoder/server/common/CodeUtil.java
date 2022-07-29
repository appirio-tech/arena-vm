/*
 * Copyright (C) - 2022 TopCoder Inc., All Rights Reserved.
 */
package com.topcoder.server.common;

import com.topcoder.farm.processor.api.CodeProcessingRequestMetadata.LanguageType;
import com.topcoder.netCommon.contest.ContestConstants;

/**
 * Various code processing utilities.
 *
 * <p>
 * Changes in version 1.1 (Python3 Support):
 * <ol>
 *      <li>Updated {@link #toLanguageType(int)} method to support Python3 language.</li>
 * </ol>
 * </p>
 *
 * @author james, liuliquan
 * @version 1.1
 */
public class CodeUtil {

	/**
	 * Converts from the old lang int to the enum
	 */
	public static LanguageType toLanguageType(int langCode) {
		switch (langCode) {
			case ContestConstants.JAVA:
				return LanguageType.JAVA;
			case ContestConstants.VB:
			case ContestConstants.CSHARP:
				return LanguageType.DOTNET;
			case ContestConstants.CPP:
				return LanguageType.CPP;
			case ContestConstants.PYTHON:
				return LanguageType.PYTHON;
      case ContestConstants.PYTHON3:
          return LanguageType.PYTHON3;
			case ContestConstants.R:
				return LanguageType.R;
		}
		
		throw new IllegalArgumentException("Invalid language id: " + langCode);
	}
	
}
