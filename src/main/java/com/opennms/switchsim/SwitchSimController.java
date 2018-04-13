package com.opennms.switchsim;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.UnknownHostException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class SwitchSimController {
	private SwitchSim sim = new SwitchSim();
	public String ipAddress;// = "127.0.0.1"; //Initialized with default, will be overwritten after (possibly)
	public static int port = 162;
	private static final Logger LOG = LoggerFactory.getLogger(SwitchSimController.class);
	
	@Autowired
    public SwitchSimController(@Value("${opennmsserver.address}") String ipAddress) {
        this.ipAddress = ipAddress;
        LOG.info("OPENNMS server:" + ipAddress);
    }
	
	@RequestMapping("/switchcontroller")
	public String snmpHandler(@RequestParam("status") boolean status, @RequestParam("port") String portNum) {
		String output = "";
		int ifOperStatusInt = 0;
		
		//ipAddress = config.getOpenNMSServer();
		
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
