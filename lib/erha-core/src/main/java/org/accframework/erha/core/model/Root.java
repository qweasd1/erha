package org.accframework.erha.core.model;

import java.util.List;

public class Root {
	private List<Unit> units;

	public Root(List<Unit> units) {
		this.units = units;
	}

	public List<Unit> getUnits() {
		return units;
	}
}
