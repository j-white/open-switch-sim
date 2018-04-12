package com.opennms.switchsim;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.UnknownHostException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.snmp4j.CommunityTarget;
import org.snmp4j.PDU;
import org.snmp4j.Snmp;
import org.snmp4j.event.ResponseEvent;
import org.snmp4j.mp.SnmpConstants;
import org.snmp4j.smi.Address;
import org.snmp4j.smi.Integer32;
import org.snmp4j.smi.OID;
import org.snmp4j.smi.OctetString;
import org.snmp4j.smi.TimeTicks;
import org.snmp4j.smi.UdpAddress;
import org.snmp4j.smi.VariableBinding;
import org.snmp4j.transport.DefaultUdpTransportMapping;

public class SwitchSim {
	private static final Logger LOG = LoggerFactory.getLogger(SwitchSim.class);
	
	public String sendTrap(final InetSocketAddress trapAddr, String portNum, int ifOperStatusInt) throws UnknownHostException, IOException {
		LOG.info("Sending trap");

		LOG.info("port " + portNum + " is " + (ifOperStatusInt == 1? "up":"down") );
		
		PDU trap = new PDU();
		trap.setType(PDU.TRAP);
		
		//The first varbind must be sysUpTime.0 and the second snmpTrapOID.0
		OID oid;
		
		if(ifOperStatusInt == 2)
			oid = new OID(".1.3.6.1.6.3.1.1.5.3");
		else
			oid = new OID(".1.3.6.1.6.3.1.1.5.4");
		
		trap.add(new VariableBinding(SnmpConstants.sysUpTime, new TimeTicks(0))); 
		trap.add(new VariableBinding(SnmpConstants.snmpTrapOID, oid));
		//trap.add(new VariableBinding(SnmpConstants.sysDescr, new OctetString("System Description"))); 
		//trap.add(new VariableBinding(SnmpConstants.snmpTrapAddress, new IpAddress(trapAddr.getAddress())));
		//trap.add(new VariableBinding(oid, new OctetString("some string")));     
		
		OID ifIndex = new OID("1.3.6.1.2.1.2.2.1.1");
		OID isAdminStatus = new OID("1.3.6.1.2.1.2.2.1.7");
		OID ifOperStatus = new OID("1.3.6.1.2.1.2.2.1.8");
		
		trap.add(new VariableBinding(ifIndex, new Integer32(Integer.parseInt(portNum))));
		trap.add(new VariableBinding(isAdminStatus, new Integer32(1)));
		trap.add(new VariableBinding(ifOperStatus, new Integer32(ifOperStatusInt)));
	
		

		// Specify receiver
		Address targetaddress = new UdpAddress(trapAddr.getAddress(), trapAddr.getPort());
		LOG.info(targetaddress.toString());
		CommunityTarget target = new CommunityTarget();
		target.setCommunity(new OctetString("public"));
		target.setVersion(SnmpConstants.version2c);
		target.setAddress(targetaddress);
		
		// Send
		Snmp snmp = new Snmp(new DefaultUdpTransportMapping());
		snmp.send(trap, target, null, null);    
		
		ResponseEvent event = snmp.send(trap, target, null);
		
		if(event != null) {
			LOG.info(event.toString());
		}

		LOG.info("Trap has been sent");
        return "success";
	}

}
