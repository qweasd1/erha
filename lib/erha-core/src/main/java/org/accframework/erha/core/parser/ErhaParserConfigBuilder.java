package org.accframework.erha.core.parser;

public class ErhaParserConfigBuilder {
	
	private ErhaParserConfig config;
	
	public ErhaParserConfigBuilder(){
		config = new ErhaParserConfig();
	}
	
	public void addValueParseConfig(String start, String end, String type) {
		config.valueParseConfigs.add(new ValueParseConfig(start, end, type));
	}
	
	//with escape
	public void addValueParseConfig(String start, String end, String type, String escape) {
		config.valueParseConfigs.add(new ValueParseConfig(start, end,escape, type));
	}
	
	public void addAnnotationModifier(String pattern, String type, ModifierType modifierType) {
		config.annotationModifierConfigs.add(new ModifierPatternTypeConfig(pattern, type, modifierType));
	}
	
	public void addUnitModifier(String pattern, String type, ModifierType modifierType) {
		config.unitModifierConfigs.add(new ModifierPatternTypeConfig(pattern, type, modifierType));
	}
	
	public ErhaParserConfig createConfig() {
		return config;
	}
	
	public static ErhaParserConfigBuilder defaultBuilder() {
		ErhaParserConfigBuilder defaultBuilder = new ErhaParserConfigBuilder();
		ErhaParserConfig config = defaultBuilder.config;
		
		
		// add default annotation modifier '@'
		config.annotationModifierConfigs.add(new ModifierPatternTypeConfig("@", null, ModifierType.TYPE_ONLY));
		// add default key value binding '='
		config.keyValueBindingConfigs.add(new PatternTypeConfig("=", null));
		//skip WS
		config.skipPatterns.add("[ \\t]+");
		//property
		config.propertySetStartBound = "(";
		config.propertySeperator = ",";
		config.propertySetEndBound = ")";
		
		//tabLength, used to calculate indent
		config.tabLength = 4;
		
		return defaultBuilder;
	}
	
}
