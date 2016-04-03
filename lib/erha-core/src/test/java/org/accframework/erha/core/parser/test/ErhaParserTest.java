package org.accframework.erha.core.parser.test;

import static org.junit.Assert.*;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;

import org.accframework.erha.core.model.Entity;
import org.accframework.erha.core.model.Property;
import org.accframework.erha.core.model.Unit;
import org.accframework.erha.core.model.Value;
import org.accframework.erha.core.parser.CharStream;
import org.accframework.erha.core.parser.ErhaParser;
import org.accframework.erha.core.parser.ErhaParserConfig;
import org.accframework.erha.core.parser.ErhaParserException;
import org.accframework.erha.core.parser.ErhaSourceCodePreProccessor;
import org.accframework.erha.core.parser.ModifierMode;
import org.accframework.erha.core.parser.ModifierPatternTypeConfig;
import org.accframework.erha.core.parser.PatternTypeConfig;
import org.accframework.erha.core.parser.ValueParseConfig;
import org.junit.Test;

public class ErhaParserTest {

	
	private CharStream charStream;
	private ErhaParserConfig config;

	private ErhaParser createParser(String text) {
		config = new ErhaParserConfig();
		// add default annotation modifier '@'
		config.annotationModifierConfigs.add(new ModifierPatternTypeConfig("@", null, ModifierMode.TYPE_ONLY));
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
		
		config.valueParseConfigs.add(new ValueParseConfig("{{", "}}", "test"));
		
		ErhaSourceCodePreProccessor preProccessor = new ErhaSourceCodePreProccessor();
		
		charStream = new CharStream(preProccessor.preProcess(text),config.skipPatterns);
		return new ErhaParser(charStream, config);
	}
	
	
	
	
	@Test
	public void testProperty() {
		
		ErhaParser parser = createParser(" abc = test ,");
		Property property = parser.property(",");
		assertEquals("abc", property.getKey().getValue());
		assertEquals("test", property.getValue().getValue());
		
	}
	
	@Test
	public void testNoneEmptyPropertySet() {
		
		ErhaParser parser = createParser("( abc = test , bb)");
		List<Property> propertSet = parser.noneEmptyPropertySet();
		assertEquals(2, propertSet.size());
		
	}
	
	@Test
	public void testEmptyPropertySet() {
		
		ErhaParser parser = createParser("( )");
		List<Property> propertSet = parser.emptyPropertySet();
		assertNotNull(propertSet);
		
	}
	
	@Test
	public void testPropertySet() {
		
		ErhaParser parser = createParser("( a,{{test}},a=b)");
		List<Property> propertSet = parser.propertySet();
		assertNotNull(propertSet);
		
	}
	
	
	@Test
	public void testEntity_WithoutPropertySet() {
		
		ErhaParser parser = createParser("* ast\\r\\n");
		Entity entity = parser.entity();
		assertNotNull(entity);
		
	}
	
	@Test
	public void testEntity_WithoutModifier() {
		
		ErhaParser parser = createParser("ast(1,2,3)");
		Entity entity = parser.entity();
		assertNotNull(entity);
		
	}
	
	@Test
	public void testEntity_withModifier() {
		
		ErhaParser parser = createParser("1. ast(1,2,3,{{test}}=asd)");
		config.unitModifierConfigs.add(new ModifierPatternTypeConfig("(\\d+)\\.*", "oi",ModifierMode.TYPE_WITH_TEXT));
		Entity entity = parser.entity();
		assertNotNull(entity);
		
	}
	
	@Test
	public void testAnnotation_withModifier() {
		
		ErhaParser parser = createParser("@ ast(1,2,3,{{test}}=asd)");
		config.unitModifierConfigs.add(new ModifierPatternTypeConfig("\\d+\\.*", "oi", ModifierMode.TYPE_ONLY));
		Entity entity = parser.annotation();
		assertNotNull(entity);
		
	}
	
	
	
	//@Test(expected=ErhaParserException.class)
	public void testAnnotation_withoutModifier() {
		
		ErhaParser parser = createParser("ast(1,2,3,{{test}}=asd)");
		config.unitModifierConfigs.add(new ModifierPatternTypeConfig("\\d+\\.*", "oi",ModifierMode.TYPE_ONLY));
		Entity entity = parser.annotation();
		assertNotNull(entity);
		
	}
	
	@Test
	public void testAnnotations() {
		String input = "@t1\r\n@t2\r\nentity";
		ErhaParser parser = createParser(input);		
		List<Entity> annotations = parser.annotations();
		assertNotNull(annotations);
		
	}
	
	@Test
	public void testUnit() {
		String input = "@t1(1,k=v,3)\r\n"
				     + "@t2\r\n"
				     + "entity(a,b)";
		ErhaParser parser = createParser(input);		
		Unit unit = parser.unit();
		assertNotNull(unit);
		
	}
	
	@Test
	public void testUnits() {
		String input = "@t1(1,k=v,3)\r\n"
				     + "@t2\r\n"
				     + "entity(a,b)\r\n"
				     + "  test({{ttt}})\r\n"
				     + "@controller(GET)\r\n"
					 + "test(b)\r\n";
		ErhaParser parser = createParser(input);		
		List<Unit> units = parser.units();
		assertNotNull(units);
		assertEquals(2, units.size());
		
	}
	
	
	@Test
	public void testt() throws Exception {
		
		String input = input("C:\\Users\\Lee\\Desktop\\tttt.txt");
		ErhaParser parser = createParser(input);
		config.valueParseConfigs.add(new ValueParseConfig("{", "}", "control"));
		List<Unit> units = parser.units();
		assertNotNull(units);
		assertEquals(1, units.size());
		
	}




	private String input(String filePath) throws IOException {
		return new String(Files.readAllBytes(Paths.get(filePath)));
	}
	
	
}
