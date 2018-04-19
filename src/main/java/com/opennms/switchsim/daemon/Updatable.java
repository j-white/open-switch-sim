package com.opennms.switchsim.daemon;


import org.snmp4j.smi.OID;
import org.snmp4j.smi.Variable;

public interface Updatable {
    /**
     * <p>updateValue</p>
     *
     * @param oid a {@link org.snmp4j.smi.OID} object.
     * @param value a {@link org.snmp4j.smi.Variable} object.
     */
    public void updateValue(OID oid, Variable value);

}
