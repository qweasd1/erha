package org.accframework.erha.core.model;

public class Value {
	private String type;
	private String value;
	
	public Value(String type){
		this(type, null);
	}
	
	public Value(String type, String value) {
		this.type = type;
		this.value = value;
	}
	
	public String getType() {
		return type;
	}



	public void setType(String type) {
		this.type = type;
	}


	public String getValue() {
		return value;
	}


	public void setValue(String value) {
		this.value = value;
	}

	@Override
	public String toString() {
		return String.format("[value: type=%s, value=%s]",type, value);
	}

	
}
