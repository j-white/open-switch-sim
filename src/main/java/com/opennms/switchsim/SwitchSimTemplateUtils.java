package com.opennms.switchsim;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Vector;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

import com.google.common.collect.ImmutableMap;
import com.opennms.switchsim.model.Card;
import com.opennms.switchsim.model.Port;
import com.opennms.switchsim.model.Switch;

import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import freemarker.template.TemplateExceptionHandler;

@Component
public class SwitchSimTemplateUtils {
	private static final Logger LOG = LoggerFactory.getLogger(SwitchSimTemplateUtils.class);
	private final Configuration cfg = new Configuration(Configuration.VERSION_2_3_23);
	private static final String propFileUrl = SwitchSimController.propMibFile;
	
	@Autowired
	public SwitchSimTemplateUtils() {
		// Setup Freemarker
		cfg.setClassForTemplateLoading(getClass(), "");
		cfg.setDefaultEncoding(StandardCharsets.UTF_8.name());
		cfg.setTemplateExceptionHandler(TemplateExceptionHandler.RETHROW_HANDLER);
	}

	public String generateMib(Switch device) {
		List<Card> cards = device.getCards();
		List<Port> ports = new ArrayList<>();
		for (Card card : cards) {
			ports.addAll((Collection<? extends Port>) card.getPorts());
		}

		return render("mib.ftl", ImmutableMap.builder().put("ports", ports).build());
	}

	public String storeMib(Switch device) {
		SortedProperties prop = getMibProperties(device);

		OutputStream output = null;
		ClassPathResource resource = new ClassPathResource(propFileUrl);
        // Initialize at least with something
        String resourceURL = propFileUrl;

        // save properties to project root folder
        try {
            resourceURL = resource.getURL().getPath();
            output = new FileOutputStream(resourceURL);
			prop.store(output, null);
			// the colon is escaped with a back slash so we just need to overwrite it
			PrintWriter pw = new PrintWriter(resourceURL);
			for (Enumeration<?> e = prop.propertyNames(); e.hasMoreElements();) {
				String key = (String) e.nextElement();
				pw.println(key + " = " + prop.getProperty(key));
			}
			pw.close();

		} catch (IOException io) {
			io.printStackTrace();
			LOG.error(io.getMessage());
		} finally {
			if (output != null) {
				try {
					output.close();
				} catch (IOException e) {
					e.printStackTrace();
					LOG.error(e.getMessage());
				}
			}
		}
		
		return resourceURL;
	}

	SortedProperties getMibProperties(Switch sw) {
	    SortedProperties properties = new SortedProperties();
        for (Card card : sw.getCards()) {
            for (Port port : card.getPorts()) {
                properties.setProperty(".1.3.6.1.2.1.2.2.1.1." + port.getPortID(), "INTEGER: " + port.getPortID());
                properties.setProperty(".1.3.6.1.2.1.2.2.1.2." + port.getPortID(), "STRING: 10/100 utp ethernet (cat 3/5)");
                properties.setProperty(".1.3.6.1.2.1.2.2.1.3." + port.getPortID(), "INTEGER: 6");
                properties.setProperty(".1.3.6.1.2.1.2.2.1.4." + port.getPortID(), "INTEGER: 1500");
                properties.setProperty(".1.3.6.1.2.1.2.2.1.5." + port.getPortID(), "Gauge32: 100000000");
                properties.setProperty(".1.3.6.1.2.1.2.2.1.7." + port.getPortID(), "INTEGER: 1");
                properties.setProperty(".1.3.6.1.2.1.2.2.1.8." + port.getPortID(), "Responder: com.opennms.switchsim.daemon.responder.OperStatus");
                properties.setProperty(".1.3.6.1.2.1.2.2.1.9." + port.getPortID(), "Timeticks: (1620595854) 187 days, 13:39:18.54");
            }
        }

        getEntityPhysicalTable(sw, properties);

        return properties;
    }

    private void getEntityPhysicalTable(Switch sw, SortedProperties properties) {
        String entPhysicalDescr = ".1.3.6.1.2.1.47.1.1.1.1.2.";
        String entPhysicalContainedIn = ".1.3.6.1.2.1.47.1.1.1.1.4.";
        String entPhysicalAlias = ".1.3.6.1.2.1.47.1.1.1.1.14.";
        String entAliasMappingEntry = ".1.3.6.1.2.1.47.1.3.2.1.2.";
        String ifIndex = ".1.3.6.1.2.1.2.2.1.1.";

        List<Card> cards = sw.getCards();
        int numberCards = cards.size();

        // arbitrary starting indecies for the physical table and ports
	    int index = 1001; 
        int physicalPortId = 3001;
	    
	    // The 'chassis'
	    int chassisIndex = index;
        properties.setProperty(entPhysicalDescr + chassisIndex, "OpenNMS Switch Simulator");
        properties.setProperty(entPhysicalContainedIn + chassisIndex, "INTEGER: 0");
	            
	    // Add 'slots' and 'cards'
        List<Integer> slotIndexes = IntStream.rangeClosed(1, numberCards).mapToObj(i -> chassisIndex + i).collect(Collectors.toList());
        List<Integer> cardIndexes = IntStream.rangeClosed(1, numberCards).mapToObj(i -> chassisIndex + numberCards + i).collect(Collectors.toList());
        for (int idx = 0; idx < numberCards; idx++) {
            int slot = slotIndexes.get(idx);
            int card = cardIndexes.get(idx);
            properties.setProperty(entPhysicalDescr + slot, "OpenNMS Switch Simulator Slot " + idx + "/" + numberCards);
            properties.setProperty(entPhysicalContainedIn + slot, "INTEGER: " + chassisIndex);
            properties.setProperty(entPhysicalDescr + card, "OpenNMS Switch Simulator Card " + idx + "/" + numberCards);
            properties.setProperty(entPhysicalContainedIn + card, "INTEGER: " + slot);
            // Map the ports in the 3000 range
            for (Port port : cards.get(idx).getPorts()) {
                // 'physically' add the port to the cards.
                properties.setProperty(entPhysicalContainedIn + physicalPortId, "INTEGER: " + card);
                properties.setProperty(entPhysicalAlias + physicalPortId, "STRING: " + port.getPortID());
                // provide alias mapping to ifIndex
                properties.setProperty(entAliasMappingEntry + physicalPortId, ifIndex + port.getPortID());
                physicalPortId++;
            }
        }
	}

    private String render(String templateName, Map<Object, Object> context) {
		try {
			final StringWriter writer = new StringWriter();
			final Template template = cfg.getTemplate(templateName);
			template.process(context, writer);
			return writer.toString();
		} catch (IOException | TemplateException e) {
			LOG.error(e.getMessage());
			throw new RuntimeException();
		}
	}
	
	@SuppressWarnings("serial")
	class SortedProperties extends Properties {
		@SuppressWarnings("unchecked")
		public Enumeration keys() {
			Enumeration<?> keysEnum = super.keys();
			Vector<String> keyList = new Vector<String>();
			while (keysEnum.hasMoreElements()) {
				keyList.add((String) keysEnum.nextElement());
			}
			Collections.sort(keyList);
			return keyList.elements();
		}

	}
}


