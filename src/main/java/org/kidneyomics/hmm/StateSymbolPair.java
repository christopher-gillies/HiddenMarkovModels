package org.kidneyomics.hmm;

public class StateSymbolPair implements Cloneable {
	private State state;
	private Symbol symbol;
	private StateSymbolPair next = null;
	
	
	public StateSymbolPair(State state, Symbol symbol) {
		this.state = state;
		this.symbol = symbol;
	}
	
	public void setNext(StateSymbolPair next) {
		this.next = next;
	}
	
	public StateSymbolPair getNext() {
		return next;
	}
	
	public State getState() {
		return this.state;
	}
	
	public Symbol getEmittedSymbol() {
		return this.symbol;
	}
	
	public Object clone() {
		return new StateSymbolPair(this.state, this.symbol);
	}
}
