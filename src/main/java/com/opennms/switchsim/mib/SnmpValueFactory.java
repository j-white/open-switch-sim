package com.opennms.switchsim.mib;


import java.math.BigInteger;
import java.net.InetAddress;


public interface SnmpValueFactory {

    SnmpValue getOctetString(byte[] bytes);

    SnmpValue getCounter32(long val);

    SnmpValue getCounter64(BigInteger val);

    SnmpValue getGauge32(long val);

    SnmpValue getInt32(int val);

    SnmpValue getIpAddress(InetAddress val);

    SnmpValue getObjectId(SnmpObjId objId);

    SnmpValue getTimeTicks(long val);

    SnmpValue getValue(int type, byte[] bytes);

    SnmpValue getNull();

    SnmpValue getOpaque(byte[] bs);
}

