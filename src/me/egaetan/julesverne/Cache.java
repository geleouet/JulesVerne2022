package me.egaetan.julesverne;

import java.util.HashMap;
import java.util.Map;

public class Cache<T> {
	private final Map<T, Integer> cache = new HashMap<>();

	public int get(T p) {
		return cache.get(p);
	}

	public boolean contains(T p) {
		return cache.containsKey(p);
	}

	public int memo(T p, int i) {
		cache.put(p,  i);
		return i;
	}

	public boolean doesntContains(T p) {
		return !contains(p);
	}
}