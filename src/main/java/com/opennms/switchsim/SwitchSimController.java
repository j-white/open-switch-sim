package com.opennms.switchsim;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.MalformedURLException;
import java.net.UnknownHostException;
import java.util.concurrent.TimeUnit;

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
import com.opennms.switchsim.model.Card;
import com.opennms.switchsim.model.OperStatusEnum;
import com.opennms.switchsim.model.Port;
import com.opennms.switchsim.model.Switch;

@RestController
public class SwitchSimController {
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
			@Value("${opennmsserver.ports}") int numPortsPerCard) throws UnknownHostException, MalformedURLException, IOException, InterruptedException {
		this.ipAddress = ipAddress;
		SwitchSimModel.getInstance().initSwitch(numCards, numPortsPerCard);
		
		//Override default agent port if a new value provided
		if(agentPort != aPort) {
			agentPort = aPort;
			LOG.info("Agent default port is overwritten. New port is " + agentPort);
		}
		
		//Making sure that a dump file is ready
		LOG.info("delaying letting a dum file be ready or updated");
		TimeUnit.SECONDS.sleep(3);
		LOG.info("Assuming it is already ready");
		
		SNMPDaemon.exec(new String[] {"-d", propMibFile, "-l", "0.0.0.0", "-p", agentPort+""});
		LOG.info("OPENNMS server:" + ipAddress + "; switch: (" + numCards + "/" + numPortsPerCard + ")");
	}

	@RequestMapping("/switchcontroller")
	public void snmpHandler(@RequestParam("status") boolean status, @RequestParam("port") String portNum) {
		String output = "";
		int ifOperStatusInt = 0;

		ipAddress = SwitchSimUtils.getDefaultIfEmptyOrNull(ipAddress, "127.0.0.1");

		final InetSocketAddress trapAddr = new InetSocketAddress(ipAddress, port);

		// just simple validation to exclude noisy values
		ifOperStatusInt = (status ? OperStatusEnum.LINK_UP.getValue() : OperStatusEnum.LINK_DOWN.getValue());
		
		for (Card card : SwitchSimModel.getInstance().getSwitch().getCards()) {
			for (Port port : card.getPorts()) {
				if(port.getPortID().intValue() == Integer.parseInt(portNum)) {
					port.setLinkDown(status);
				}
			}
		}

		try {
			output = SwitchSimModel.getInstance().sendTrap(trapAddr, portNum, ifOperStatusInt);
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	@GetMapping(path = "/inventory", produces = MediaType.APPLICATION_JSON_VALUE)
	public Switch loadInventory() {
		return SwitchSimModel.getInstance().getSwitch();
	}
}
