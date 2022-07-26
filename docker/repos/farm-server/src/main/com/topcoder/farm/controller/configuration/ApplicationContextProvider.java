package com.topcoder.farm.controller.configuration;

import org.springframework.context.ApplicationContext;

/**
 * Singleton that enables legacy code access to a spring application context.
 * 
 * @author james
 *
 */
public final class ApplicationContextProvider {
	private static ApplicationContext applicationContext;
	
	public static ApplicationContext getContext() {
		return applicationContext;
	}
	
	public static void setContext(ApplicationContext context) {
		applicationContext = context;
	}
}
