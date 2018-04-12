package com.opennms.switchsim;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.UnknownHostException;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class SwitchSimController {
	private SwitchSim sim = new SwitchSim();
	public static String ipAddress = "159.89.115.236";
	public static int port = 162;
	
	@RequestMapping("/switchcontroller")
	public String snmpHandler(@RequestParam("status") boolean status, @RequestParam("port") String portNum) {
		String output = "";
		int ifOperStatusInt = 0;
		
		final InetSocketAddress trapAddr = new InetSocketAddress(ipAddress, port);
		
		//just simple validation to exclude noisy values
		ifOperStatusInt = (status ? 1 : 2);
		
		try {
			output = sim.sendTrap(trapAddr, portNum, ifOperStatusInt);
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		//and display message to client
		return "port " + portNum + " is " + (ifOperStatusInt == 1? "up":"down") + "\n output:\n" + output;
	 }
	 
}
