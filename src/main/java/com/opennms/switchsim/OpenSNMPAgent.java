package com.opennms.switchsim;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import com.opennms.switchsim.daemon.SNMPDaemon;

@SpringBootApplication
public class OpenSNMPAgent {
	private static final Logger LOG = LoggerFactory.getLogger(OpenSNMPAgent.class);

    @Bean
    public ApplicationRunner runner() {
    	
        return args -> {
        	//SNMPDaemon.exec(new String[] {"-d", "mib2_stub.properties", "-l", "0.0.0.0", "-p", "1691"});
        };
    }
}
