package com.opennms.switchsim.model;

public enum OperStatusEnum {
	LINK_UP(1), LINK_DOWN(2);
	
	private final int value;

	OperStatusEnum(final int newValue) {
        value = newValue;
    }

    public int getValue() { return value; }
}
