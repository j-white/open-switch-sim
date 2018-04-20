package com.opennms.switchsim.daemon.responder;

import org.snmp4j.smi.Integer32;
import org.snmp4j.smi.Variable;

import com.opennms.switchsim.SwitchSimModel;
import com.opennms.switchsim.model.Card;
import com.opennms.switchsim.model.OperStatusEnum;
import com.opennms.switchsim.model.Port;

public class OperStatus implements DynamicVariable{
	
	@Override
	public Variable getVariableForOID(String oidStr) throws SnmpErrorStatusException {
		String[] oids = oidStr.split("\\.");
		Integer instance = Integer.parseInt(oids[oids.length-1]);
		
		//find appropriated port
		for (Card card : SwitchSimModel.getInstance().getSwitch().getCards()) {
			for (Port port : card.getPorts()) {
				if(port.getPortID().intValue() == instance.intValue()) {
					return new Integer32(port.isLinkDown()? OperStatusEnum.LINK_DOWN.getValue() : OperStatusEnum.LINK_UP.getValue());
				}
			}
		}
		
		return new Integer32(OperStatusEnum.LINK_DOWN.getValue());
	}
}
