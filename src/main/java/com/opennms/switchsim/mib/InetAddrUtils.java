package com.opennms.switchsim.mib;

import java.net.InetAddress;
import java.net.UnknownHostException;

/**
 * @deprecated Use {@link InetAddressUtils} instead.
 */
@Deprecated
public abstract class InetAddrUtils {

	public static String str(InetAddress address) {
		return address == null ? null : address.getHostAddress();
	}

	public static InetAddress addr(String value) {
		try {
			return value == null ? null : InetAddress.getByName(value);
		} catch (UnknownHostException e) {
			throw new RuntimeException("Unable to turn " + value + " into an inet address");
		}
	}

	public static InetAddress getLocalHostAddress() {
		return addr("127.0.0.1");
	}
	
	

}
