package org.kidneyomics.hmm;

import java.util.Iterator;
import java.util.List;

public class StateSymbolPair {
	private State state;
	private Symbol symbol;
	
	public StateSymbolPair(State state, Symbol symbol) {
		this.state = state;
		this.symbol = symbol;
	}
	
	public State getState() {
		return this.state;
	}
	
	public Symbol getEmittedSymbol() {
		return this.symbol;
	}
	
	public static String createVisualRepresentation(List<StateSymbolPair> pairs, String delimiter) {
		StringBuilder seqSb = new StringBuilder();
		seqSb.append("Sequence:\t");
		StringBuilder stateSb = new StringBuilder();
		stateSb.append("States:\t\t");
		
		Iterator<StateSymbolPair> iter = pairs.iterator();
		
		while(iter.hasNext()) {
			StateSymbolPair next = iter.next();
			seqSb.append(next.getEmittedSymbol());
			stateSb.append(next.getState());
			
			if(iter.hasNext()) {
				seqSb.append(delimiter);
				stateSb.append(delimiter);
			}
			
		}
		
		seqSb.append("\n");
		seqSb.append(stateSb.toString());
		
		return seqSb.toString();
	}
}
