package com.opennms.switchsim.mib;

import java.util.StringTokenizer;

public class SnmpInstId extends SnmpObjId {
    
    public static final SnmpInstId INST_ZERO = new SnmpInstId(0);

    public SnmpInstId(int[] instanceIds) {
        super(instanceIds);
    }

    public SnmpInstId(String instance) {
        super(instance);
    }
    
    public SnmpInstId(SnmpObjId instance) {
        super(instance);
    }

    public SnmpInstId(int instance) {
        super(new int[] { instance }, false);
    }
    
    @Override
    protected boolean addPrefixDotInToString() {
        return false;
    }

    public static SnmpInstId[] convertToSnmpInstIds(String instances) {
        StringTokenizer tokenizer = new StringTokenizer(instances, ",");
        SnmpInstId[] insts = new SnmpInstId[tokenizer.countTokens()];
        int index = 0;
        while (tokenizer.hasMoreTokens()) {
            String token = tokenizer.nextToken();
            SnmpInstId inst = new SnmpInstId(token);
            insts[index] = inst;
            index++;
        }
        return insts;
    }

    public int toInt() {
        if (this.length() != 1)
            throw new IllegalStateException("Cannot convert "+this+" to an int");
        
        return getLastSubId();
    }
    
    
}
