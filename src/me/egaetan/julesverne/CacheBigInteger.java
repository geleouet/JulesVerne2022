package me.egaetan.julesverne;

import java.math.BigInteger;
import java.util.HashMap;
import java.util.Map;

public class CacheBigInteger<T> {
	private final Map<T, BigInteger> cache = new HashMap<>();

	public BigInteger get(T p) {
		return cache.get(p);
	}

	public boolean contains(T p) {
		return cache.containsKey(p);
	}

	public BigInteger memo(T p, BigInteger i) {
		cache.put(p,  i);
		return i;
	}

	public boolean doesntContains(T p) {
		return !contains(p);
	}
}