package org.accframework.erha.core.matcher.test;

import static org.junit.Assert.*;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.accframework.erha.core.model.Entity;
import org.accframework.erha.core.model.Root;
import org.accframework.erha.core.model.Unit;
import org.accframework.erha.core.parser.CharStream;
import org.accframework.erha.core.parser.ErhaParser;
import org.accframework.erha.core.parser.ErhaParserConfig;
import org.accframework.erha.core.parser.ErhaSourceCodePreProccessor;
import org.accframework.erha.core.parser.ModifierPatternTypeConfig;
import org.accframework.erha.core.parser.ModifierType;
import org.accframework.erha.core.parser.PatternTypeConfig;
import org.accframework.erha.core.parser.ValueParseConfig;
import org.accframework.erha.core.visitor.ErhaAbstractVisitor;
import org.junit.Test;

public class ErhaVisitorTest {

	private CharStream charStream;
	private ErhaParserConfig config;

	private ErhaParser createParser(String text) {
		config = new ErhaParserConfig();
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
		
		
		
		ErhaSourceCodePreProccessor preProccessor = new ErhaSourceCodePreProccessor();
		
		charStream = new CharStream(preProccessor.preProcess(text),config.skipPatterns);
		return new ErhaParser(charStream, config);
	}
	
	private ErhaParser createParserFromFile(String filePath) throws IOException {
		return createParser(input(filePath));
	}
	
	@Test
	public void test() throws IOException {
		ErhaAbstractVisitor visitor = new ErhaAbstractVisitor() {
			Set<String> annotationNames = new HashSet<String>();
			
			@Override
			public void visitEntity(Entity entity) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void visitAnnotation(Entity entity) {
				annotationNames.add(entity.getValue().getValue());
			}

			@Override
			protected void visitUnit(Unit unit) {
				// TODO Auto-generated method stub
				
			}
		};
		
		ErhaParser parser = createParserFromFile("D:/Projects/accframework/erha/test/samples/playaround.txt");
		config.valueParseConfigs.add(new ValueParseConfig("{", "}", "control"));
		config.unitModifierConfigs.add(new ModifierPatternTypeConfig("(\\w+)\\s*:", "kvp", ModifierType.TYPE_WITH_TEXT));
		
		visitor.visit(parser.parse());
		assertNull(visitor);
	}
	
	@Test
	public void test2() throws IOException {
		ErhaAbstractVisitor visitor = new ErhaAbstractVisitor() {
			List<Unit> units = new ArrayList<Unit>();
			
			@Override
			protected void visitEntity(Entity entity) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			protected void visitAnnotation(Entity entity) {
				
			}

			@Override
			protected void visitUnit(Unit unit) {
				if (unit.getEntity().getValue().getValue().equals("test")) {
					units.add(unit);
				}
			}
		};
		
		ErhaParser parser = createParserFromFile("D:/Projects/accframework/erha/test/samples/playaround.txt");
		
		Root root = parser.parse();
		visitor.visit(root);
		assertNull(visitor);
	}
	
	
	private String input(String filePath) throws IOException {
		return new String(Files.readAllBytes(Paths.get(filePath)));
	}

}
