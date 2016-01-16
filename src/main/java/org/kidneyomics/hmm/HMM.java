package org.kidneyomics.hmm;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;

import org.kidneyomics.hmm.State.VISIT_LEVEL;

public class HMM {
	
	private final State startState;
	private final HashMap<String,State> states;
	private final HashMap<String,Symbol> symbols;
	
	private HMM(State startState) {
		this.startState = startState;
		this.states = new HashMap<String,State>();
		this.symbols = new HashMap<String,Symbol>();
		discoverStatesAndSymbols(this.startState);
	}
	
	
	public static HMM createHMMFromStartState(State startState) {
		if(!startState.isStartState()) {
			throw new IllegalArgumentException("Please input a start state");
		}
		return new HMM(startState);
	}
	
	/**
	 * 
	 * @param n
	 * @return returns the first element in the sequence
	 */
	public StateSymbolPair generateSequence(int n) {
		//TODO: Change to return StateSymbolPairCollection
		//List<StateSymbolPair> sequence = new ArrayList<StateSymbolPair>(n);
		
		//set the current state to be the start state
		State current = startState;
		
		//generate a sequence of length n by getting the next state and generating a symbol from it
		//then storing the result in the sequence result
		StateSymbolPair last = null;
		StateSymbolPair first = null;
		for(int i = 0; i < n; i++) {
			
			current = current.emitNextState();
			Symbol emittedSymbol = current.emitSymbol();
						
			StateSymbolPair pair = new StateSymbolPair(current, emittedSymbol);
			
			//store pointer to next state
			if(last != null) {
				last.setNext(pair);
			}
			last = pair;
			
			//we set the first state if it is null
			if(first == null) {
				first = pair;
			}
		}
		
		return first;
	}
	
	public double calcProbOfSymbolGivenStateProbs(Symbol symbol, Map<State,Double> map) {
		if(symbol == null) {
			throw new IllegalArgumentException("Symbol cannot be null");
		}
		/*
		 * Marginalize the joint distribution of p(symbol,state)
		 * 
		 * --> conditional probability <--
		 * p(symbol,state) = p(symbol|state) * p(state);
		 * sum_{states} p(symbol,state) = p(symbol) by the law of total probability
		 * 
		 */
		double prob = 0;
		for(Map.Entry<State, Double> entry : map.entrySet()) {
			if(entry.getKey() == null) {
				throw new IllegalArgumentException("Key cannot be null in prob map");
			}
			
			if(entry.getValue() == null) {
				throw new IllegalArgumentException("Value cannot be null in prob map");
			}
			
			double probOfState = entry.getValue();
			double probOfSymbol = entry.getKey().getEmissions().getProbability(symbol);
			prob += probOfSymbol * probOfState;
		}
		return prob;
	}
	
	public double calculateJointProbabilityOfSequencesAndStates(StateSymbolPairCollection sequence, boolean log) {
		//TODO: finish this function
//		double res = 0.0;
//		if(sequence.size() == 0) {
//			return 0.0;
//		}
//		
//		//compute the prob(x,pi) = a_0k * [e_k(b) * akl * e_l(b) ...]
//		State firstSate = sequence.get(0).getState();
//		double startProb = Math.log(this.startState.getTransitions().getProbability(firstSate));
//		res = startProb;
//		
//		if(sequence.size() == 1) {
//			StateSymbolPair pair = sequence.get(0);
//			Symbol symbol = pair.getEmittedSymbol();
//			State state = pair.getState();
//			double emitProb = Math.log(state.getEmissions().getProbability(symbol));
//			res = res + emitProb;
//		} else {
//			
//		}
//		
//		if(!log) {
//			return res;
//		} else {
//			return Math.exp(res);
//		}
		return 0.0;
	}
	
	private void discoverStatesAndSymbols(State state) {
		Queue<State> queue = new LinkedList<State>();
		
		state.setVisitLevel(VISIT_LEVEL.VISITED);
		queue.add(state);
		
		while(queue.peek() != null) {
			State next = queue.poll();
			if(!next.isStartState()) {
				//Store state
				this.states.put(next.getName(), next);
				
				//Store symbols
				for(Symbol symbol : next.getEmissions().getKeys()) {
					this.symbols.put(symbol.getSymbolName(), symbol);
				}
			}
			
			if(next.getVisitLevel() != VISIT_LEVEL.CLOSED) {
				for(State s : next.getTransitions().getKeys()) {
					if(s.getVisitLevel() == VISIT_LEVEL.NOT_VISITED) {
						s.setVisitLevel(VISIT_LEVEL.VISITED);
						queue.add(s);
					}
				}
			}
			
			next.setVisitLevel(VISIT_LEVEL.CLOSED);
		}
		
	}
	
	public State getStateByName(String name) {
		return this.states.get(name);
	}
	
	public Set<String> getStateNames() {
		return this.states.keySet();
	}
	
	public Set<String> getSymbolNames() {
		return this.symbols.keySet();
	}
	
	public Symbol getSymbolByName(String name) {
		return this.symbols.get(name);
	}
	
	
}
