package org.accframework.erha.core.parser;

public class ModifierPatternTypeConfig extends PatternTypeConfig {

	public ModifierType modifierType;
	
	public ModifierPatternTypeConfig(String pattern, String type, ModifierType modifierType) {
		super(pattern, type);
		this.modifierType = modifierType;
	}
	
	
}
