package com.topcoder.farm.controller.configuration;

/**
 * Environment specific configuration
 * 
 * @author james
 */
public class EnvironmentConfig {
	private String prefix;
	private String appServiceName;

	public String getAppServiceName() {
		return appServiceName;
	}

	public void setAppServiceName(String appServiceName) {
		this.appServiceName = appServiceName;
	}

	public String getPrefix() {
		return prefix;
	}

	public void setPrefix(String prefix) {
		this.prefix = prefix;
	}

}
