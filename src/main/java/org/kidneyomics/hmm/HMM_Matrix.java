package org.kidneyomics.hmm;

import java.util.HashMap;

class HMM_Matrix {
	
	//a_kl = P(pi_{i} = l | pi_{i-1} = k)
	//transition probability from state k to state l
	private final double[][] transitions;
	
	//e_{k}(b) = P(x_{i} = b | pi_{i} = k)
	//probability of emitting symbol b given we are in state k 
	private final double[][] emissions;
	
	//symbols used for the sequences x
	private final String[] symbols;
	
	//states for the hidden markov model plus a start state
	private final String[] states;
	
	//useful map to convert from a string symbol to its integer location in the array
	private final HashMap<String,Integer> state2Int;
	private final HashMap<String,Integer> symbol2Int;
	
	
	HMM_Matrix(String[] symbols, String[] states) {
		this.symbols = symbols;
		//add one for start state
		this.states = new String[states.length + 1];
		this.states[0] = "START_STATE";
		for(int i = 0; i < states.length; i++) {
			this.states[i + 1] = states[i];
		}
		
		transitions = new double[this.states.length][this.states.length];
		emissions = new double[this.states.length][this.symbols.length];
		
		//create state to integers map
		state2Int = new HashMap<String,Integer>();
		
		for(int i = 0; i < this.states.length; i++) {
			state2Int.put(this.states[i], i);
		}
		
		//create symbols to integers map
		symbol2Int = new HashMap<String,Integer>();
		
		for(int i = 0; i < this.symbols.length; i++) {
			symbol2Int.put(this.symbols[i], i);
		}
	}
	
	void setTransition(int state1, int state2, double value) {
		transitions[state1][state2] = value;
	}
	
	void setTransition(String state1, String state2, double value) {
		Integer state1Int = state2Int(state1);
		Integer state2Int = state2Int(state2);
		if(state1Int == null || state2Int == null) {
			throw new IllegalArgumentException(state1 + " or " + state2 + " not found!");
		}
		
		transitions[state1Int][state2Int] = value;
	}
	
	void setEmission(int state, int symbol, double value) {
		emissions[state][symbol] = value;
	}
	
	void setEmission(String state, String symbol, double value) {
		Integer stateInt = state2Int(state);
		Integer symbolInt = symbol2Int(symbol);
		
		if(stateInt == null || symbolInt == null) {
			throw new IllegalArgumentException(state + " or " + symbol + " not found!");
		}
		
		emissions[stateInt][symbolInt] = value;
	}
	
	int state2Int(String state) {
		return state2Int.get(state);
	}
	
	int symbol2Int(String symbol) {
		return symbol2Int.get(symbol);
	}
	
}
