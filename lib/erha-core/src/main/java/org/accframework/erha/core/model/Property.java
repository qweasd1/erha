package org.accframework.erha.core.model;

import javax.security.auth.kerberos.KerberosTicket;

public class Property {
	private Value key;
	private Value value;
	private String type;
	
	
	public Property(Value key, Value value, String type) {
		this.key = key;
		this.value = value;
		this.type = type;
	}
	
	public Value getKey() {
		return key;
	}


	public Value getValue() {
		return value;
	}


	public String getType() {
		return type;
	}

	@Override
	public String toString() {
		return String.format("[property: key=%s, value=%s]", key == null ? null: key.getValue(), value.getValue());
	}
	
	
}
