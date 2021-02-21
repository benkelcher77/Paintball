package com.ben.paintball.util;

public class Pair<A, B> {

	public A first;
	public B second;
	
	public Pair(A first, B second) {
		this.first = first;
		this.second = second;
	}
	
	@Override
	public boolean equals(Object other) {
		if (!(other instanceof Pair<?, ?>))
			return false;
		
		Pair<?, ?> otherPair = (Pair<?, ?>)other;
		return otherPair.first.equals(first) && otherPair.second.equals(second);
	}
	
}
