package com.opennms.switchsim.mib;

import java.io.IOException;
import java.io.InputStream;
import java.util.Map.Entry;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.opennms.switchsim.daemon.SNMPDaemon;

import java.util.Properties;
import java.util.SortedMap;
import java.util.TreeMap;

public class OpenMIBStub {
	private SortedMap<SnmpObjId, Object> m_agentData = new TreeMap<SnmpObjId, Object>();
	private static final Logger LOG = LoggerFactory.getLogger(OpenMIBStub.class);
	private static OpenMIBStub stub;
	
	public static OpenMIBStub getOpenMIBStub() {
		if(stub == null)
			stub = new OpenMIBStub();
		
		return stub;
	}
	
	private OpenMIBStub() {
        try {
			loadSnmpTestData(getClass(), "/mib2_stub.properties");
		} catch (IOException e) {
			e.printStackTrace();
			LOG.error(e.getMessage());
		}
        //SnmpObjId z1 = SnmpObjId.get(zeroInst1Base, "0");
        //assertEquals("TestData", getValueFor(z1).toString());
        
    }
	
	public void loadSnmpTestData(Class<?> clazz, String name) throws IOException {
		LOG.info("loading snmp data...");
        InputStream dataStream = clazz.getResourceAsStream(name);
        Properties mibData = new Properties();
        LOG.info("loading mib...");
        mibData.load(dataStream);
        dataStream.close();
        LOG.info("setting agent data...size:" + mibData.size());
        
        setAgentData(mibData);
    }
	
	public void setAgentData(Properties mibData) {
		LOG.info("setting agent data...");
    	MockSnmpValueFactory factory = new MockSnmpValueFactory();
        m_agentData = new TreeMap<SnmpObjId, Object>();
        for (Entry<Object, Object> entry : mibData.entrySet()) {
            SnmpObjId objId = SnmpObjId.get(entry.getKey().toString());
            
            setAgentValue(objId, factory.parseMibValue(entry.getValue().toString()));
        }
    }
	
	public void setAgentValue(SnmpObjId objId, SnmpValue value) {
		//LOG.info(objId + ":" + value);
        m_agentData.put(objId, value);
    }
	
	public SortedMap<SnmpObjId, Object> getAgentData() {
		return m_agentData;
	}
}
