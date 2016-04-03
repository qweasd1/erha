package org.accframework.erha.core.model;

import java.util.Iterator;
import java.util.List;
import java.util.Map;

public abstract class ErhaModelSet<TKey,TItem> implements Iterable<TItem> {
	private List<TItem> list;
	private Map<Object, TItem> map;
	
	public void add(TItem item) {
		
		TKey key = getKey(item);
		if (key != null) {
			map.put(key, item);
		}
		else {
			map.put(list.size(), item);
		}
		
		list.add(item);
		
	}
	
	public TItem get(Object keyOrIndex) {
		return map.get(keyOrIndex);
	}
	
	public TItem getByKey(TKey key) {
		return map.get(key);
	}
	
	public Iterator<TItem> iterator() {
		return list.iterator();
	}
	
	
	// get the key of the item
	protected abstract TKey getKey(TItem item);
}
