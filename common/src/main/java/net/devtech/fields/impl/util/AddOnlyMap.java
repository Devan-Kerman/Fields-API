package net.devtech.fields.impl.util;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.Set;

import com.google.common.collect.ForwardingMap;

public class AddOnlyMap<K, V> extends ForwardingMap<K, V> {
	public final Map<K, V> delegate;

	public AddOnlyMap(Map<K, V> delegate) {
		this.delegate = delegate;
	}

	@Override
	protected Map<K, V> delegate() {
		return this.delegate;
	}

	@Override
	public V remove(Object object) {
		throw new UnsupportedOperationException("remove");
	}

	@Override
	public V put(K key, V value) {
		if(this.containsKey(key)) {
			throw new UnsupportedOperationException("put null (remove)");
		}
		return super.put(key, value);
	}

	@Override
	public void putAll(Map<? extends K, ? extends V> map) {
		this.standardPutAll(map);
	}

	@Override
	public Set<K> keySet() {
		return Collections.unmodifiableSet(super.keySet());
	}

	@Override
	public Collection<V> values() {
		return Collections.unmodifiableCollection(super.values());
	}

	@Override
	public Set<Entry<K, V>> entrySet() {
		return Collections.unmodifiableSet(super.entrySet());
	}
}
