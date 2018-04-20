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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

import com.opennms.switchsim.model.Card;
import com.opennms.switchsim.model.Port;
import com.opennms.switchsim.model.Switch;
import com.google.common.collect.ImmutableMap;
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
		SortedProperties prop = new SortedProperties();
		OutputStream output = null;
		ClassPathResource resource = new ClassPathResource(propFileUrl);
		
		//Initialize at least with something
		String resourceURL = propFileUrl;

		try {
			resourceURL = resource.getURL().getPath();
			output = new FileOutputStream(resourceURL);

			// set the properties value
			List<Card> cards = device.getCards();

			for (Card card : cards) {
				for (Port port : card.getPorts()) {
					prop.setProperty(".1.3.6.1.2.1.2.2.1.1." + port.getPortID(), "INTEGER: " + port.getPortID());
				}
			}

			for (Card card : cards) {
				for (Port port : card.getPorts()) {
					prop.setProperty(".1.3.6.1.2.1.2.2.1.2." + port.getPortID(),
							"STRING: 10/100 utp ethernet (cat 3/5)");
				}
			}

			for (Card card : cards) {
				for (Port port : card.getPorts()) {
					prop.setProperty(".1.3.6.1.2.1.2.2.1.3." + port.getPortID(), "INTEGER: 6");
				}
			}

			for (Card card : cards) {
				for (Port port : card.getPorts()) {
					prop.setProperty(".1.3.6.1.2.1.2.2.1.4." + port.getPortID(), "INTEGER: 1500");
				}
			}

			for (Card card : cards) {
				for (Port port : card.getPorts()) {
					prop.setProperty(".1.3.6.1.2.1.2.2.1.5." + port.getPortID(), "Gauge32: 100000000");
				}
			}

			for (Card card : cards) {
				for (Port port : card.getPorts()) {
					prop.setProperty(".1.3.6.1.2.1.2.2.1.7." + port.getPortID(), "INTEGER: 1");
				}
			}

			for (Card card : cards) {
				for (Port port : card.getPorts()) {
					prop.setProperty(".1.3.6.1.2.1.2.2.1.8." + port.getPortID(), "Responder: com.opennms.switchsim.daemon.responder.OperStatus");
				}
			}

			for (Card card : cards) {
				for (Port port : card.getPorts()) {
					prop.setProperty(".1.3.6.1.2.1.2.2.1.9." + port.getPortID(),
							"Timeticks: (1620595854) 187 days, 13:39:18.54");
				}
			}
			// save properties to project root folder
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


