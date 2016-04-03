package org.accframework.erha.core.visitor;

import org.accframework.erha.core.model.Entity;
import org.accframework.erha.core.model.Root;
import org.accframework.erha.core.model.Unit;

public abstract class ErhaAbstractVisitor {

	public void visit(Root root) {
		for (Unit unit : root.getUnits()) {
			_visitUnit(unit);
		}
	}
	
	
	private void _visitUnit(Unit unit) {
		for (Entity annotation : unit.getAnnotations()) {
			visitAnnotation(annotation);			
		}
		
		visitEntity(unit.getEntity());
		
		visitUnit(unit);
		
		for (Unit subUnit : unit.getChildren()) {
			_visitUnit(subUnit);
		}
	}
	
	protected abstract void visitUnit(Unit unit);
	
	protected abstract void visitAnnotation(Entity entity);
	
	protected abstract void visitEntity(Entity entity);
}
