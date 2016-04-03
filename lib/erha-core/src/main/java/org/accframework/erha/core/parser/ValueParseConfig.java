package org.accframework.erha.core.parser;

public class ValueParseConfig {

	public String start;
	public String end;
	public String escape;	
	public String type;
	
	public ValueParseConfig(String start, String end, String type) {
		this(start, end, null, type);
	}
	
	public ValueParseConfig(String start, String end, String escape, String type) {
		this.start = start;
		this.end = end;
		this.escape = escape;
		this.type = type;
	}

}
