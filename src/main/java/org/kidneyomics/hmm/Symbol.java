package org.kidneyomics.hmm;

public class Symbol {
	
	private final String name;
	
	private Symbol(String symbol) {
		this.name = symbol;
	}
	
	public static Symbol createSymbol(String symbol) {
		return new Symbol(symbol);
	}
	
	public String getName() {
		return this.name;
	}
	
	@Override
	public String toString() {
		return getName();
	}
	
}
