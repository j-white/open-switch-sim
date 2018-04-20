package com.opennms.switchsim.model;

import java.util.ArrayList;
import java.util.List;

public class Switch {
	private List<Card> cards = new ArrayList<>();
	public static final String switchId = "123456";
	
	public Switch(int numCards, int numPortsPerCard) {
		initDefaultSwitch(numCards, numPortsPerCard);
	}
	
	public void initDefaultSwitch(int numCards, int numPortsPerCard) {
		int count = 1;
		
		//Clear default settings
		cards.clear();
		
		for(int i = 0; i < numCards; i++) {
			Card card = new Card();
			List<Port> ports = new ArrayList<>();
			
			for(int j = 0; j < numPortsPerCard; j++) {
				ports.add(new Port(count));
				count++;
			}
			
			card.setPorts(ports);
			cards.add(card);
		}
	}
	
	public List<Card> getCards() {
		return cards;
	}
}
