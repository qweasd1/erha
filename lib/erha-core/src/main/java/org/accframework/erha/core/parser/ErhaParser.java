package org.accframework.erha.core.parser;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.sound.midi.MidiChannel;

import org.accframework.erha.core.model.Entity;
import org.accframework.erha.core.model.Property;
import org.accframework.erha.core.model.Root;
import org.accframework.erha.core.model.Unit;
import org.accframework.erha.core.model.Value;
import org.accframework.erha.core.parser.ErhaParserConfig;
import org.accframework.erha.core.parser.ValueParseConfig;
import org.accframework.erha.core.parser.ErhaParser.IndentState;

public class ErhaParser {
	private CharStream charStream;
	private ErhaParserConfig config;
	private IndentProcessor indentProcessor;

	
	
	public ErhaParser(CharStream charStream, ErhaParserConfig config) {
		this.charStream = charStream;
		this.config = config;
		this.indentProcessor = new IndentProcessor(charStream, config.tabLength);
	}
	
	
	public Root parse() {
		return new Root(units());
	}
	
	public List<Unit> units() {
		while (!charStream.isEnd()) {
			indentProcessor.updateIndent();
			IndentState indentStatus = indentProcessor.indentStatus;
			
			Unit unit = unit();
			if (unit == null) {
				throw new ErhaParserException();
			}
			indentProcessor.addUnit(indentStatus,unit);
		}
		
		return indentProcessor.getRootUnits();
	}
	
	public Unit unit() {
		charStream.beginTx();
		List<Entity> annotations = annotations();
		Entity entity = entity();
		if (entity == null) {
			charStream.rollback();
			return null;
		}
		
		String newLine = newLine();
		if (newLine == null) {
			charStream.rollback();
			return null;
		}
		
		charStream.commit();
		
		if (annotations == null) {
			annotations =  new ArrayList<Entity>();
		}
		Unit unit = new Unit(annotations, entity);
		return unit;
	}
	
	// after this method being called, we have already have updateIndent for the following entity, no need to call it again
	public List<Entity> annotations() {
		
		List<Entity> result = new ArrayList<Entity>();
		
		charStream.beginTx();
		Entity first_annotation = annotation();
		if (first_annotation == null) {
			charStream.rollback();
			return null;
		}
		else {
			result.add(first_annotation);
		}
		
		while (true) {
			
			String newLine = newLine();
			if (newLine == null) {
				// expected NewLine('\n')
				throw new ErhaParserException();
			}
			
			indentProcessor.updateIndent();
			if (indentProcessor.indentStatus != IndentState.SAME) {
				// indent Error
				throw new ErhaParserException();
			}
			
			Entity annotation = annotation();
			if (annotation == null) {
				charStream.commit();
				return result;
			}
			else {
				result.add(annotation);
				continue;
			}
		}
	}
	
	public Entity annotation() {
		return _entity(config.annotationModifierConfigs,true);
	}
	
	public Entity entity() {
		return _entity(config.unitModifierConfigs,false);
	}
	
	
	private Entity _entity(List<ModifierPatternTypeConfig> modifierConfigs, boolean isModifierRequired) {
		
		
		String modifier = null;
		String modifier_type = null;
		String modifier_value = null;
		Entity entity = null;
		
		
		for (ModifierPatternTypeConfig mConfig : modifierConfigs) {
			modifier = matchRegex(mConfig.bindingPattern);
			if (modifier != null) {
				modifier_type = mConfig.type;
				if (mConfig.modifierType == ModifierType.TYPE_WITH_TEXT) {
					modifier_value = firstGroup(modifier, mConfig.bindingPattern);
				}
				
				break;
			}
			
		}
		
		if (isModifierRequired && modifier == null) {
			// can't parse the annotation check the modifier of the annotation
			return null;
		}
		
//		if (modifier != null) {
//			String whiteSpace = whiteSpace();
//			if (whiteSpace == null) {
//				//expected whiteSpace at ...
//				throw new ErhaParserException();
//			}
//
//		}
				
		Value value = value(config.propertySetStartBound,"\\n");
		
		if (value != null) {
			//has (...)
			List<Property> propertySet = propertySet();
			if (propertySet == null) {
				// can't parse propertySet at ...
				throw new ErhaParserException();
			}
			else {
				
			    return new Entity(modifier_value, modifier_type, value, propertySet);
			}
		}
		else {
			value = value(new String[]{"\r\n", "\n"}, null);
			if (value != null) {
				return new Entity(modifier_value, modifier_type, value, new ArrayList<Property>());
			}
			else {
				// can't parse entity at ...
				throw new ErhaParserException();
			}
		}
		
		
	}
	
	
	public List<Property> propertySet() {
		List<Property> result = emptyPropertySet();
		if (result != null) {
			return result;
		}
		
		result = noneEmptyPropertySet();
		if (result != null) {
			return result;
		}
		
		return null;
	}
	
	public List<Property> emptyPropertySet() {
		charStream.beginTx();
		
		String propertyStartBound = match("(");
		if (propertyStartBound == null) {
			charStream.rollback();
			return null;
		}
		
		String propertyEndBound = match(")");
		if (propertyEndBound == null) {
			charStream.rollback();
			return null;
		}
		
		charStream.commit();
		return new ArrayList<Property>();
	}
	
	public List<Property> noneEmptyPropertySet() {
		List<Property> result = new ArrayList<Property>();
		
		charStream.beginTx();
		
		String propertyStartBound = match(config.propertySetStartBound);
		if (propertyStartBound == null) {
			charStream.rollback();
			return null;
		}
		
		while(true){
			Property property = property(config.propertySeperator);
			if (property == null) {
				break;
			}
			else {
				result.add(property);
			}
		}
		
		Property lastProperty = property(config.propertySetEndBound);
		if (lastProperty == null) {
			charStream.rollback();
			return null;
		}
		else {
			result.add(lastProperty);
			charStream.commit();
			return result;
		}
		
		
	}
	
	public Property property(String propertyEndBound) {
		for (PatternTypeConfig kvbconfig : config.keyValueBindingConfigs) {
			charStream.beginTx();
			
			Value key = value(kvbconfig.bindingPattern, config.propertySeperator + "|\\n");
			if (key == null) {
				charStream.rollback();
				continue;
			}
			
			String kvpBinding = match(kvbconfig.bindingPattern);
			if (kvpBinding == null) {
				charStream.rollback();
				continue;
			}
			
			Value value = value(propertyEndBound, "\\n");
			if (value == null) {
				charStream.rollback();
				continue;
			}
			
			match(propertyEndBound,false);
			
			charStream.commit();
			Property result = new Property(key, value, kvbconfig.type);
			return result;
		}
		
		Value value = value(propertyEndBound, "\\n");
		if (value != null) {
			Property result = new Property(null, value, null);
			match(propertyEndBound);
			return result;
		}
		
		return null;
	}

	private String match(String bindingPattern) {
		return charStream.nextString(bindingPattern);
	}
	
	private String match(String bindingPattern, boolean isSikp) {
		return charStream.nextString(bindingPattern,isSikp);
	}
	
	private String matchRegex(String bindingPattern) {
		return charStream.nextRegex(bindingPattern);
	}
	
	private String firstGroup(String text, String pattern) {
		Matcher matcher = Pattern.compile(pattern).matcher(text);
		matcher.find();
		return matcher.group(1);
	}
	
	
	
	public Value value(String[] endBounds, String excludeString) {
		for (ValueParseConfig config : config.valueParseConfigs) {
			Value result = userDefineValue(config);
			if (result != null) {
				return result;
			}
		}
		
		for (String endBound : endBounds) {
			Value result = rawValue(endBound, excludeString);
			if (result != null) {
				return result;
			}			
		}
		
		return null;
	}
	
	public Value value(String endBound, String excludeString) {
		for (ValueParseConfig config : config.valueParseConfigs) {
			Value result = userDefineValue(config);
			if (result != null) {
				return result;
			}
		}
		
		Value result = rawValue(endBound, excludeString);
		return result;
	}
	
	public Value userDefineValue(ValueParseConfig config) {
		String value = charStream.nextBlock(config.start, config.end, config.escape, false);
		if (value == null) {
			return null;
		}
		else {
			Value result = new Value(config.type,value);
			
			return result;
		}
	}
	
	public Value rawValue(String endBound, String excludeString) {
		
		String value = charStream.nextUntil(endBound, excludeString);
	    if (value == null) {
			return null;	
		}
	    else {
	    	// trim the raw Value
			String trim_value = value.trim();
			Value result = new Value(null,trim_value);
			return result;
		}
	}
	
	public String whiteSpaceOptional(){
		return charStream.nextRegex("\\s*");
	}
	
	public String whiteSpace(){
		return charStream.nextRegex("\\s+");
	}
	
	public String newLine() {
		return charStream.nextRegex("(\\r)?\\n",false);
	}
	
	class IndentProcessor{
		private CharStream charStream;
		private int tabLength;
		

		private Stack<Integer> indents;
		private Stack<List<Unit>> unitListStack; 
		private int dedentCount;
		
		public IndentState indentStatus;
		
		public IndentProcessor(CharStream charStream, int tabLength){
			this.charStream = charStream;
			this.tabLength = tabLength;
			
			indents = new Stack<Integer>();
			unitListStack = new Stack<List<Unit>>();
			unitListStack.push(new ArrayList<Unit>());
			
			indentStatus = IndentState.SAME;
			
			dedentCount = 0;
		}
		
		public void updateIndent() {
			String whitespaceString = charStream.nextRegex("[ \\t]*");
			int newIndent = indent(whitespaceString);
			if(indents.isEmpty()){
				indents.push(newIndent);
				if (newIndent == 0) {
					indentStatus = IndentState.SAME;
				}
				else {
					indentStatus = IndentState.INDENT;
				}
				
			} else if (indents.peek() < newIndent) {
				indents.push(newIndent);
				indentStatus = IndentState.INDENT;
			}
			else if (indents.peek() == newIndent) {
				indentStatus = IndentState.SAME;
			}
			// (indents.peek() > newIndent
			else {
				dedentCount = 0;
				while(!indents.isEmpty()){
					Integer poppedIndent = indents.pop();
					if (poppedIndent > newIndent) {
						dedentCount += 1;
						continue;
					}
					else if (poppedIndent == newIndent) {
						indentStatus = IndentState.DEDENT;
						return;
					}
					else {
						//dedent error at ...
						indentStatus = IndentState.ERROR;
					}
				}
			}
		}
		
		
		
		public void addUnit(IndentState indentState,Unit unit) {
			List<Unit> lastUnitList = unitListStack.peek();
			if (indentState == IndentState.SAME) {
				lastUnitList.add(unit);
			}
			else if (indentState == IndentState.INDENT) {
				Unit newAddedUnit = lastUnitList.get(lastUnitList.size() -1);
				newAddedUnit.getChildren().add(unit);
				unitListStack.push(newAddedUnit.getChildren());
			}
			else if (indentState == IndentState.DEDENT) {
				for (int i = 0; i < dedentCount; i++) {
					unitListStack.pop();
				}
				unitListStack.peek().add(unit);
			}
		}
		
		public List<Unit> getRootUnits() {
			return unitListStack.get(0);
		}
		
		private int indent(String whitespaces){
			int sum = 0;
			for (char ws : whitespaces.toCharArray()) {
				if (ws == ' ') {
					sum += 1;
				}
				else {
					sum += tabLength;
				}
			}
			return sum;
		}
	}
	
	enum IndentState {
		INDENT,
		DEDENT,
		SAME,
		ERROR, 
	}
	
}