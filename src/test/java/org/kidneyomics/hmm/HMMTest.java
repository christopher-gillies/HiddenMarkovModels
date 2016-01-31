package org.kidneyomics.hmm;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.junit.Test;

public class HMMTest {

	public HMM createBiasedCoinHMM() {
		Symbol heads = Symbol.createSymbol("H");
		Symbol tails = Symbol.createSymbol("T");
		
		State start = State.createStartState();
		
		
		State fair = State.createState("F");
		//set emission probabilities for fair state
		fair.getEmissions().setProbability(heads, 0.5);
		fair.getEmissions().setProbability(tails, 0.5);
		
		
		
		State biased = State.createState("B");
		//set emission probabilities for biased state
		biased.getEmissions().setProbability(heads, 0.9);
		biased.getEmissions().setProbability(tails, 0.1);
		
		//equal chance to be in fair or biased state
		start.getTransitions().setProbability(fair, 0.5);
		start.getTransitions().setProbability(biased, 0.5);
		
		fair.getTransitions().setProbability(fair, 0.9);
		fair.getTransitions().setProbability(biased, 0.1);
		
		biased.getTransitions().setProbability(fair, 0.1);
		biased.getTransitions().setProbability(biased, 0.9);
		
		HMM hmm = HMM.createHMMFromStartState(start);
		
		
		return hmm;
	}
	
	@Test
	public void testIsValid() {
		HMM hmm = createBiasedCoinHMM();
		
		assertTrue(hmm.isValid());
	}
	
	@Test
	public void testDiscoverStates1() {
		
		HMM hmm = createBiasedCoinHMM();
		
		System.err.println("States: " + hmm.getStateNames().size());
		assertTrue(2 == hmm.getStateNames().size());
		assertTrue(hmm.getStateByName("F") != null);
		assertTrue(hmm.getStateByName("B") != null);

		assertTrue(2 == hmm.getSymbolNames().size());
		assertTrue(hmm.getSymbolByName("T") != null);
		assertTrue(hmm.getSymbolByName("H") != null);
		
	}
	
	@Test
	public void testDiscoverStates2() {
		
		State start = State.createStartState();
		State fair = State.createState("F");
		State fair2 = State.createState("F");
		
		start.getTransitions().setProbability(fair, 1);
		
		fair.getTransitions().setProbability(fair2, 1);
		boolean thrown = false;
		try {
			HMM hmm = HMM.createHMMFromStartState(start);
		} catch(RuntimeException e) {
			assertNotNull(e);
			thrown = true;
		}
		assertTrue(thrown);
	}
	
	@Test
	public void testDiscoverStates3() {
		
		State start = State.createStartState();
		State fair = State.createState("F");
		State biased = State.createState("B");
		
		start.getTransitions().setProbability(fair, 1);
		fair.getEmissions().setProbability(Symbol.createSymbol("H"), 1);
		fair.getTransitions().setProbability(biased, 1);
		biased.getEmissions().setProbability(Symbol.createSymbol("H"), 1);
		boolean thrown = false;
		try {
			HMM hmm = HMM.createHMMFromStartState(start);
		} catch(RuntimeException e) {
			assertNotNull(e);
			thrown = true;
		}
		assertTrue(thrown);
	}
	
	@Test
	public void validateGeneratedSequenceLength() {
		
		HMM hmm = createBiasedCoinHMM();
		
		TraversableOrderedSet<StateSymbolPair> orderedSet = hmm.generateSequence(100);
		
		assertEquals(100,orderedSet.size());
		
		System.err.println(orderedSet.toString());
	}
	
	@Test
	public void testCalcProbOfSymbolGivenStateProbs() {
		HMM hmm = createBiasedCoinHMM();
		
		Map<State,Double> probs = new HashMap<State,Double>();
		
		State fair = hmm.getStateByName("F");
		State biased = hmm.getStateByName("B");
		
		Symbol heads = hmm.getSymbolByName("H");
		Symbol tails = hmm.getSymbolByName("T");
		
		probs.put(fair, 0.1);
		probs.put(biased, 0.9);
		
		double resHeads = hmm.calcProbOfSymbolGivenStateProbs(heads, probs);
		
		double expResHeads = 0.1 * 0.5 + 0.9 * 0.9;
		
		System.err.println("Exp Result Heads: " + expResHeads);
		System.err.println("Result Heads: " + resHeads);
		assertEquals(expResHeads,resHeads,0.000001);
		
		
		double resTails = hmm.calcProbOfSymbolGivenStateProbs(tails, probs);
		
		double expResTails = 0.1 * 0.5 + 0.9 * 0.1;
		
		System.err.println("Exp Result Tails: " + expResTails);
		System.err.println("Result Tails: " + resTails);
		assertEquals(expResTails,resTails,0.000001);
		
		assertEquals(1.0,resTails + resHeads, 0.000001);
	}

	@Test
	public void testCalculateJointProbabilityOfSequencesAndStates1() {
		HMM hmm = createBiasedCoinHMM();
		State fair = hmm.getStateByName("F");
		State biased = hmm.getStateByName("B");
		Symbol heads = hmm.getSymbolByName("H");
		Symbol tails = hmm.getSymbolByName("T");
		
		StateSymbolPair pair1 = new StateSymbolPair(fair, heads);
		StateSymbolPair pair2 = new StateSymbolPair(biased, tails);
		StateSymbolPair pair3 = new StateSymbolPair(biased, heads);
		
		TraversableOrderedSet<StateSymbolPair> set = new TraversableOrderedSet<StateSymbolPair>();
		set.add(pair1);
		set.add(pair2);
		set.add(pair3);
		
		double prob = hmm.calculateJointProbabilityOfSequencesAndStates(set, false);
		double expRes = 0.5 * 0.5 * 0.1 * 0.1 * 0.9 * 0.9;
		
		assertEquals(expRes,prob,0.0001);
	}
	
	@Test
	public void testCalculateJointProbabilityOfSequencesAndStates2() {
		//TODO: finish this unit test
		HMM hmm = createBiasedCoinHMM();
		State fair = hmm.getStateByName("F");
		State biased = hmm.getStateByName("B");
		Symbol heads = hmm.getSymbolByName("H");
		Symbol tails = hmm.getSymbolByName("T");
		
		State[] states = { fair, biased };
		Symbol[] symbols = { heads, tails };
		
		
		List<StateSymbolPair> allPairs = new ArrayList<StateSymbolPair>( 4 );
		for(int j = 0; j < states.length; j++) {
			for(int k = 0; k < symbols.length; k++) {
				allPairs.add(new StateSymbolPair(states[j], symbols[k]));
			}
		}
		
		double sum = 0;
		double max = 0;
		TraversableOrderedSet<StateSymbolPair> best = null; 
		for(int i = 0; i < allPairs.size(); i++) {
			for(int j = 0; j < allPairs.size(); j++) {
				for(int k = 0; k < allPairs.size(); k++) {
					StateSymbolPair pair1 = (StateSymbolPair) allPairs.get(i).clone();
					StateSymbolPair pair2 = (StateSymbolPair) allPairs.get(j).clone();
					StateSymbolPair pair3 = (StateSymbolPair) allPairs.get(k).clone();
					
					TraversableOrderedSet<StateSymbolPair> set = new TraversableOrderedSet<StateSymbolPair>();
					set.add(pair1);
					set.add(pair2);
					set.add(pair3);
					
					double prob = hmm.calculateJointProbabilityOfSequencesAndStates(set, false);
					
					if(prob > max) {
						max = prob;
						best = set;
					}
					sum += prob;
				}
			}
		}
		
		assertEquals(1.0,sum,0.00001);
		
		
		System.err.println("Best prob: " + max);
		System.err.println(best.toString());
		
		assertEquals(0.5 * Math.pow(0.9,5),max,0.0001);
	}
	
	@Test
	public void testDecode1() {
		HMM hmm = createBiasedCoinHMM();
		
		Symbol heads = hmm.getSymbolByName("H");
		Symbol tails = hmm.getSymbolByName("T");
		State fair = hmm.getStateByName("F");
		State biased = hmm.getStateByName("B");
		
		LinkedList<Symbol> seq = new LinkedList<Symbol>();
		seq.add(tails);
		seq.add(heads);
		seq.add(tails);
		seq.add(heads);
		seq.add(heads);
		seq.add(heads);
		seq.add(heads);
		seq.add(heads);
		seq.add(heads);
		seq.add(heads);
		seq.add(heads);
		seq.add(tails);
		seq.add(heads);
		seq.add(tails);
		TraversableOrderedSet<StateSymbolPair> bestSeq = hmm.decode(seq);
		double prob = hmm.calculateJointProbabilityOfSequencesAndStates(bestSeq, false);
		
		System.err.println("Viterbi Path");
		System.err.println("BestSeq: " + bestSeq.toString());
		System.err.println("BestProb: " + prob);
		
		//check sizes match
		assertEquals(seq.size(), bestSeq.size());
		
		Iterator<Symbol> seqIter = seq.iterator();
		Iterator<StateSymbolPair> bestIter = bestSeq.iterator();
		
		//check symbols match
		while(seqIter.hasNext()) {
			Symbol nextSym = seqIter.next();
			StateSymbolPair nextPair = bestIter.next();
			assertEquals(nextSym,nextPair.getEmittedSymbol());
		}
		
		//check that is max
		
		//get enumerations of all possible states
		Set<State> states = new HashSet<State>();
		states.add(fair);
		states.add(biased);
		Enumerator<State> enumerator = Enumerator.getEnumeratorForSymbolsAndLength(states, 14);
		Iterator<List<State>> iter = enumerator.iterator();
		int count = 0;
		while(iter.hasNext()) {
			List<State> path = iter.next();
			TraversableOrderedSet<StateSymbolPair> seqPairToCompare = StateSymbolPair.createFromListOfSymbolsAndStates(seq,path);
			double compareProb = hmm.calculateJointProbabilityOfSequencesAndStates(seqPairToCompare, false);
			assertTrue(compareProb <= prob);
			count++;
		}
		
		assertEquals(16384,count);
		
	}
	
	@Test
	public void testEvaluate1() {
		HMM hmm = createBiasedCoinHMM();
		
		Symbol heads = hmm.getSymbolByName("H");
		Symbol tails = hmm.getSymbolByName("T");
		State fair = hmm.getStateByName("F");
		State biased = hmm.getStateByName("B");
		
		LinkedList<Symbol> seq = new LinkedList<Symbol>();
		seq.add(tails);
		seq.add(heads);
		seq.add(tails);
		seq.add(heads);
		seq.add(heads);
		seq.add(heads);
		seq.add(heads);
		seq.add(heads);
		seq.add(heads);
		seq.add(heads);
		seq.add(heads);
		seq.add(tails);
		seq.add(heads);
		seq.add(tails);
		double prob = hmm.evaluate(seq, false);
		
		System.err.println("Probability of x: " + prob);
		
				
		//compute sum for seq
		
		//get enumerations of all possible states
		Set<State> states = new HashSet<State>();
		states.add(fair);
		states.add(biased);
		Enumerator<State> enumerator = Enumerator.getEnumeratorForSymbolsAndLength(states, 14);
		Iterator<List<State>> iter = enumerator.iterator();
		int count = 0;
		double sumOfProbs = 0;
		while(iter.hasNext()) {
			List<State> path = iter.next();
			TraversableOrderedSet<StateSymbolPair> seqPairToCompare = StateSymbolPair.createFromListOfSymbolsAndStates(seq,path);
			double probOfPath = hmm.calculateJointProbabilityOfSequencesAndStates(seqPairToCompare, false);
			sumOfProbs += probOfPath;
			count++;
		}
		
		assertEquals(16384,count);
		assertEquals(sumOfProbs,prob,0.00001);
		
	}
	
	@Test
	public void testEvaluate2() {
		HMM hmm = createBiasedCoinHMM();
		
		Symbol heads = hmm.getSymbolByName("H");
		Symbol tails = hmm.getSymbolByName("T");
		State fair = hmm.getStateByName("F");
		State biased = hmm.getStateByName("B");
		Set<Symbol> symbols = new HashSet<Symbol>();
		symbols.add(heads);
		symbols.add(tails);
		Enumerator<Symbol> enumerator = Enumerator.getEnumeratorForSymbolsAndLength(symbols, 14);
		Iterator<List<Symbol>> iter = enumerator.iterator();
		
		double sumOfProbs = 0;
		int count = 0;
		while(iter.hasNext()) {
			List<Symbol> seq = iter.next();
			double prob = hmm.evaluate(seq, false);
			sumOfProbs += prob;
			count++;
		}
		
		assertEquals(16384,count);
		assertEquals(1.0,sumOfProbs,0.00001);
	}

	//TODO: add more tests for connected end state hmm
	
	
	
	@Test
	public void testEvaluateBacward11() {
		HMM hmm = createBiasedCoinHMM();
		
		Symbol heads = hmm.getSymbolByName("H");
		Symbol tails = hmm.getSymbolByName("T");
		State fair = hmm.getStateByName("F");
		State biased = hmm.getStateByName("B");
		
		LinkedList<Symbol> seq = new LinkedList<Symbol>();
		seq.add(tails);
		seq.add(heads);
		seq.add(tails);
		seq.add(heads);
		seq.add(heads);
		seq.add(heads);
		seq.add(heads);
		seq.add(heads);
		seq.add(heads);
		seq.add(heads);
		seq.add(heads);
		seq.add(tails);
		seq.add(heads);
		seq.add(tails);
		double probForward = hmm.evaluate(seq, false);
		double prob = hmm.evaluateBackward(seq, false);
		
	
		System.err.println("Probability of x: " + prob);
		
		assertEquals(probForward,prob,0.001);
				
		//compute sum for seq
		
		//get enumerations of all possible states
		Set<State> states = new HashSet<State>();
		states.add(fair);
		states.add(biased);
		Enumerator<State> enumerator = Enumerator.getEnumeratorForSymbolsAndLength(states, 14);
		Iterator<List<State>> iter = enumerator.iterator();
		int count = 0;
		double sumOfProbs = 0;
		while(iter.hasNext()) {
			List<State> path = iter.next();
			TraversableOrderedSet<StateSymbolPair> seqPairToCompare = StateSymbolPair.createFromListOfSymbolsAndStates(seq,path);
			double probOfPath = hmm.calculateJointProbabilityOfSequencesAndStates(seqPairToCompare, false);
			sumOfProbs += probOfPath;
			count++;
		}
		
		assertEquals(16384,count);
		assertEquals(sumOfProbs,prob,0.00001);
		
	}
	
	@Test
	public void testEvaluateBackward2() {
		HMM hmm = createBiasedCoinHMM();
		
		Symbol heads = hmm.getSymbolByName("H");
		Symbol tails = hmm.getSymbolByName("T");
		Set<Symbol> symbols = new HashSet<Symbol>();
		symbols.add(heads);
		symbols.add(tails);
		Enumerator<Symbol> enumerator = Enumerator.getEnumeratorForSymbolsAndLength(symbols, 14);
		Iterator<List<Symbol>> iter = enumerator.iterator();
		
		double sumOfProbs = 0;
		int count = 0;
		while(iter.hasNext()) {
			List<Symbol> seq = iter.next();
			double prob = hmm.evaluateBackward(seq, false);
			sumOfProbs += prob;
			count++;
		}
		
		assertEquals(16384,count);
		assertEquals(1.0,sumOfProbs,0.00001);
	}
	
	@Test 
	public void testProbInStateAtPositionGivenSequence() {
		HMM hmm = createBiasedCoinHMM();
		
		Symbol heads = hmm.getSymbolByName("H");
		Symbol tails = hmm.getSymbolByName("T");
		State fair = hmm.getStateByName("F");
		State biased = hmm.getStateByName("B");
		
		LinkedList<Symbol> seq = new LinkedList<Symbol>();
		seq.add(tails);
		seq.add(heads);
		seq.add(tails);
		seq.add(heads);
		seq.add(heads);
		seq.add(heads);
		seq.add(heads);
		seq.add(heads);
		seq.add(heads);
		seq.add(heads);
		seq.add(heads);
		seq.add(tails);
		seq.add(heads);
		seq.add(tails);
		
		TraversableOrderedSet<StateSymbolPair> list = hmm.decode(seq);
		
		Iterator<StateSymbolPair> iter = list.iterator();
		
		
		//probInStateAtPositionGivenSequence(State state, int pos, List<Symbol> x, boolean log) {
		int count = 0;
		for(int i = 0; i < seq.size(); i++) {
			double biasedProb = hmm.probInStateAtPositionGivenSequence(biased, i, seq, false);
			double fairProb = hmm.probInStateAtPositionGivenSequence(fair, i, seq, false);
			
			StateSymbolPair next = iter.next();
			State max = null;
			if(biasedProb > fairProb) {
				assertEquals(next.getState(),biased);
				max = biased;
			} else {
				assertEquals(next.getState(),fair);
				max = fair;
			}
			
			double sum = biasedProb + fairProb;
			System.err.println("Position: " + i);
			System.err.println("Viterbi State: " + next.getState());
			System.err.println("Posterior State: " + max);
			System.err.println("Prob of fair: " + fairProb);
			System.err.println("Prob of biased: " + biasedProb);
			System.err.println("Sum of probs per state: " + sum);
			assertEquals(1.0,sum,0.001);
			count++;
		}
		assertEquals(seq.size(), count);
		
	}

}
