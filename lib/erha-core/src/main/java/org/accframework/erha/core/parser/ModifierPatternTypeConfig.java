package org.accframework.erha.core.parser;

public class ModifierPatternTypeConfig extends PatternTypeConfig {

	public ModifierMode modifierType;
	
	public ModifierPatternTypeConfig(String bindingPattern, String type, ModifierMode modifierType) {
		super(bindingPattern, type);
		this.modifierType = modifierType;
	}
	
	
}
