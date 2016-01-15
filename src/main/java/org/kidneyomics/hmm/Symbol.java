package org.kidneyomics.hmm;

public class Symbol {
	
	private final String symbol;
	
	private Symbol(String symbol) {
		this.symbol = symbol;
	}
	
	public static Symbol createSymbol(String symbol) {
		return new Symbol(symbol);
	}
	
	@Override
	public String toString() {
		return this.symbol;
	}
	
}
