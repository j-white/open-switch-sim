package com.opennms.switchsim.model;

public class Port {
	private Number portID;
	private Boolean isLinkDown;
	
	public Port() {
		
	}
	
	public Port(int portID) {
		this.portID = portID;
	}

	public Number getPortID() {
		return portID;
	}

	public void setPortID(Number portID) {
		this.portID = portID;
	}

	public Boolean isLinkDown() {
		return isLinkDown;
	}
}
