package org.accframework.erha.core.model;

import java.util.ArrayList;
import java.util.List;

public class Unit {
	private List<Entity> annotations;
	private Entity entity;
	private List<Unit> children;
	
	public Unit(List<Entity> annotations, Entity entity) {
		this.annotations = annotations;
		this.entity = entity;
		this.children = new ArrayList<Unit>();
	}
	
	
	public List<Entity> getAnnotations() {
		return annotations;
	}

	public Entity getEntity() {
		return entity;
	}

	public List<Unit> getChildren() {
		return children;
	}


	
}
