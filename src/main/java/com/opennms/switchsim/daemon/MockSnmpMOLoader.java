package com.opennms.switchsim.daemon;

import java.net.URL;
import java.util.List;

import org.snmp4j.agent.ManagedObject;

public interface MockSnmpMOLoader {
	public List<ManagedObject> loadMOs(URL moFile);
}

