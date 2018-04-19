package com.opennms.switchsim;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class SwitchSimUtils {
	private static final Logger LOG = LoggerFactory.getLogger(SwitchSimUtils.class);
	
	public static String getDefaultIfEmptyOrNull(String ipAddress, String defaultIpAddress) {
		
		if(ipAddress == null || ipAddress.trim().equals(""))
			return defaultIpAddress;
		return ipAddress;
	}

}
