package com.opennms.switchsim;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import com.opennms.switchsim.SwitchSimTemplateUtils.SortedProperties;
import com.opennms.switchsim.model.Switch;

public class MibPropertiesTest {

    private static final int PORTS_PER_CARD = 4;

    private static final int TOTAL_CARDS = 2;

    private static final int TOTAL_PORTS = TOTAL_CARDS * PORTS_PER_CARD;

    @Test
    public void test() {
        SwitchSimTemplateUtils utils = new SwitchSimTemplateUtils();
        SortedProperties properties = utils.getMibProperties(new Switch(TOTAL_CARDS, PORTS_PER_CARD));
        validateProperties(properties);
    }

    private void validateProperties(SortedProperties properties) {
        // TODO - these validations only work for <10 ports
        for (int i = 1; i <= TOTAL_PORTS; i++) {
            // validate ifIndex entry
            assertEquals(properties.get(".1.3.6.1.2.1.2.2.1.1." + i), "INTEGER: " + i);
            // validate entPhysicalAlias entry
            assertEquals(properties.get(".1.3.6.1.2.1.47.1.1.1.1.14.300" + i), "STRING: " + i);
            // validate entAliasMapping entry
            assertEquals(properties.get(".1.3.6.1.2.1.47.1.3.2.1.2.300" + i), ".1.3.6.1.2.1.2.2.1.1." + i);
        }
    }

}
