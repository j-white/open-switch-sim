package com.opennms.switchsim;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.MalformedURLException;
import java.net.UnknownHostException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.opennms.switchsim.daemon.SNMPDaemon;
import com.opennms.switchsim.model.Switch;

@RestController
public class SwitchSimController {
	private SwitchSimModel model;
	public String ipAddress;// = "127.0.0.1"; //Initialized with default, will be overwritten after
	public static int port = 162;
	public static int agentPort = 161;
	public static String propMibFile = "mib2_tmp.properties";
	private static final Logger LOG = LoggerFactory.getLogger(SwitchSimController.class);

	@Autowired
	public SwitchSimController(SwitchSimModel model, 
			@Value("${opennmsserver.address}") String ipAddress,
			@Value("${opennmsserver.agentport}") int aPort,
			@Value("${opennmsserver.cards}") int numCards, 
			@Value("${opennmsserver.ports}") int numPortsPerCard) throws UnknownHostException, MalformedURLException, IOException {
		this.ipAddress = ipAddress;
		this.model = model;
		model.initSwitch(numCards, numPortsPerCard);
		
		//Override default agent port if a new value provided
		if(agentPort != aPort) {
			agentPort = aPort;
			LOG.info("Agent default port is overwritten. New port is " + agentPort);
		}
		
		SNMPDaemon.exec(new String[] {"-d", propMibFile, "-l", "0.0.0.0", "-p", agentPort+""});
		LOG.info("OPENNMS server:" + ipAddress + "; switch: (" + numCards + "/" + numPortsPerCard + ")");
	}

	@RequestMapping("/switchcontroller")
	public String snmpHandler(@RequestParam("status") boolean status, @RequestParam("port") String portNum) {
		String output = "";
		int ifOperStatusInt = 0;

		ipAddress = SwitchSimUtils.getDefaultIfEmptyOrNull(ipAddress, "127.0.0.1");

		final InetSocketAddress trapAddr = new InetSocketAddress(ipAddress, port);

		// just simple validation to exclude noisy values
		ifOperStatusInt = (status ? 1 : 2);

		try {
			output = model.sendTrap(trapAddr, portNum, ifOperStatusInt);
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		// and display message to client
		return "port " + portNum + " is " + (ifOperStatusInt == 1 ? "up" : "down") + "\n output:\n" + output;
	}

	@GetMapping(path = "/inventory", produces = MediaType.APPLICATION_JSON_VALUE)
	public Switch loadInventory() {
		return model.getSwitch();
	}
}
