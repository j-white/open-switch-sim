package com.opennms.switchsim.model;

import java.util.ArrayList;
import java.util.List;

public class Card {
	private List<Port> ports = new ArrayList<>();
	
	public Card() {
		
	}
	
	public Card(int numPorts) {
		for(int i = 0; i < numPorts; i++) {
			ports.add(new Port());
		}
	}
	
	public List<Port> getPorts() {
		return ports;
	}

	public void setPorts(List<Port> ports) {
		this.ports = ports;
	}

	public Card(List<Port> ports) {
		this.ports = ports;
	}
}
