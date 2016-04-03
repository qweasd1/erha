package org.accframework.erha.core.parser;

import java.util.ArrayList;
import java.util.List;

public class ErhaParserConfig {
	
public List<ValueParseConfig> valueParseConfigs;
	
	public List<PatternTypeConfig> keyValueBindingConfigs;
	
	public List<ModifierPatternTypeConfig> annotationModifierConfigs;
	
	public List<ModifierPatternTypeConfig> unitModifierConfigs;
	
	public List<String> skipPatterns;

	public String propertySetStartBound;
	public String propertySetEndBound;
	public String propertySeperator;
	
	
	//indent related
	public int tabLength;
	
	
	public ErhaParserConfig(){
		valueParseConfigs = new ArrayList<ValueParseConfig>();
		keyValueBindingConfigs = new ArrayList<PatternTypeConfig>();
		annotationModifierConfigs = new ArrayList<ModifierPatternTypeConfig>();
		unitModifierConfigs = new ArrayList<ModifierPatternTypeConfig>();
		skipPatterns = new ArrayList<String>();
	}

	
}
