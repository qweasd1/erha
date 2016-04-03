package org.accframework.erha.core.model;

import java.util.List;

public class Entity {
	private String modifierValue;
	private String modifierType;
	private Value value;
	private List<Property> properties;
	
	public Entity(String modifier,String modifierType, Value value, List<Property> properties) {
		this.modifierValue = modifier;
		this.modifierType = modifierType;
		this.value = value;
		this.properties = properties;
	}
	
	public String getModifierType() {
		return modifierType;
	}

	public String getModifierValue() {
		return modifierValue;
	}

	public Value getValue() {
		return value;
	}

	public List<Property> getProperties() {
		return properties;
	}
	
	@Override
	public String toString() {
		return String.format("[entity(props:%s): value=%s, modiferType=%s]", properties.size(), value.getValue(),modifierType);
	}
	
}


