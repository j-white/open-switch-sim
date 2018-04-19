package com.opennms.switchsim;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties("server") 
public class ConfigProperties {

	private String address;

	public String getOpenNMSServer() {
		return address;
	}
}
