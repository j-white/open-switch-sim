package com.opennms.switchsim.mib;

import java.math.BigInteger;
import java.net.InetAddress;

public interface SnmpValue {
    // These values match the ASN.1 constants
    public static final int SNMP_INT32 = (0x02);

    public static final int SNMP_OCTET_STRING = (0x04);

    public static final int SNMP_NULL = (0x05);

    public static final int SNMP_OBJECT_IDENTIFIER = (0x06);

    public static final int SNMP_IPADDRESS = (0x40); // 64

    public static final int SNMP_COUNTER32 = (0x41); // 65

    public static final int SNMP_GAUGE32 = (0x42); // 66

    public static final int SNMP_TIMETICKS = (0x43); // 67

    public static final int SNMP_OPAQUE = (0x44); // 68

    public static final int SNMP_COUNTER64 = (0x46); // 70
    
    public static final int SNMP_NO_SUCH_OBJECT = (0x80); // 128
    
    public static final int SNMP_NO_SUCH_INSTANCE = (0x81); // 129

    public static final int SNMP_END_OF_MIB = (0x82); // 8*16 + 2 = 130
    
    boolean isEndOfMib();
    
    boolean isError();

    boolean isNull();

    boolean isDisplayable();

    boolean isNumeric();

    int toInt();

    String toDisplayString();

    InetAddress toInetAddress();

    long toLong();
    
    BigInteger toBigInteger();

    String toHexString();
    
    int getType();
    
    byte[] getBytes();

    SnmpObjId toSnmpObjId();
}
