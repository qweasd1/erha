package org.accframework.erha.core.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class PropertySet extends ErhaModelSet<String, Property> {

	@Override
	protected String getKey(Property item) {
		if (item.getKey() == null) {
			return null;
		}
		else {
			return item.getKey().getValue();
		}
		
				
	}
	
}
