package org.kidneyomics.hmm;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.junit.Test;
import org.kidneyomics.hmm.HMM.LEARN_MODE;

public class HMMTest {

	static {
		State.randomService = new DefaultRandomNumberSerivce(0);
	}
	
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
	public void testEvaluate3() {
		HMM hmm = createBiasedCoinHMM();
		
		Symbol heads = hmm.getSymbolByName("H");
		Symbol tails = hmm.getSymbolByName("T");
		State fair = hmm.getStateByName("F");
		State biased = hmm.getStateByName("B");
		
		LinkedList<Symbol> seq = new LinkedList<Symbol>();
		seq.add(tails);
		
		double probOfTails = 0.5 * 0.5 + 0.5 * 0.1;
		double prob = hmm.evaluate(seq, false);
		
		assertEquals(probOfTails, prob, 0.0000001);
		
		
		double probOfHeads = 0.5 * 0.5 + 0.5 * 0.9;
		LinkedList<Symbol> seq2 = new LinkedList<Symbol>();
		seq2.add(heads);
		
		assertEquals(1.0, probOfHeads + probOfTails,0.00001);
		
		double prob2 = hmm.evaluate(seq2, false);
		assertEquals(probOfHeads, prob2, 0.0000001);
		
		assertEquals(1.0, prob2 + prob,0.00001);
	}
	
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
	
	@Test
	public void testLearn1() {
		//TODO: finish test
		HMM hmm = createBiasedCoinHMM();
		
		State start = hmm.getStartState();
		Symbol heads = hmm.getSymbolByName("H");
		Symbol tails = hmm.getSymbolByName("T");
		State fair = hmm.getStateByName("F");
		State biased = hmm.getStateByName("B");
		
		TraversableOrderedSet<StateSymbolPair> pairs = new TraversableOrderedSet<StateSymbolPair>();
		
		StateSymbolPair pair1 = new StateSymbolPair(fair, heads);
		StateSymbolPair pair2 = new StateSymbolPair(fair, tails);
		StateSymbolPair pair3 = new StateSymbolPair(biased, heads);
		StateSymbolPair pair4 = new StateSymbolPair(biased, tails);
		StateSymbolPair pair5 = new StateSymbolPair(biased, tails);
		
		pairs.add(pair1);
		pairs.add(pair2);
		pairs.add(pair3);
		pairs.add(pair4);
		pairs.add(pair5);
		
		hmm.learn(pairs, LEARN_MODE.ZERO_COUNT);
		
		//check counts
		assertEquals(1.0,start.getTransitions().getCount(fair),0.000001);
		assertEquals(0.0,start.getTransitions().getCount(biased),0.000001);
		
		assertEquals(1.0,fair.getTransitions().getCount(fair),0.000001);
		assertEquals(1.0,fair.getTransitions().getCount(biased),0.000001);
		
		assertEquals(0.0,biased.getTransitions().getCount(fair),0.000001);
		assertEquals(2.0,biased.getTransitions().getCount(biased),0.000001);
		
		assertEquals(1.0,fair.getEmissions().getCount(heads),0.000001);
		assertEquals(1.0,fair.getEmissions().getCount(tails),0.000001);
		assertEquals(1.0,biased.getEmissions().getCount(heads),0.000001);
		assertEquals(2.0,biased.getEmissions().getCount(tails),0.000001);
		
		//check probabilities
		
		assertEquals(1.0,start.getTransitions().getProbability(fair),0.000001);
		assertEquals(0.0,start.getTransitions().getProbability(biased),0.000001);
		
		assertEquals(0.5,fair.getTransitions().getProbability(fair),0.000001);
		assertEquals(0.5,fair.getTransitions().getProbability(biased),0.000001);
		
		assertEquals(0.0,biased.getTransitions().getProbability(fair),0.000001);
		assertEquals(1.0,biased.getTransitions().getProbability(biased),0.000001);
		
		assertEquals(0.5,fair.getEmissions().getProbability(heads),0.000001);
		assertEquals(0.5,fair.getEmissions().getProbability(tails),0.000001);
		assertEquals(1.0 / 3.0,biased.getEmissions().getProbability(heads),0.000001);
		assertEquals(2.0 / 3.0,biased.getEmissions().getProbability(tails),0.000001);
		
		hmm.learn(pairs, LEARN_MODE.PSEUDO_COUNT);
		
		//check counts
		assertEquals(2.0,start.getTransitions().getCount(fair),0.000001);
		assertEquals(1.0,start.getTransitions().getCount(biased),0.000001);
		
		assertEquals(2.0,fair.getTransitions().getCount(fair),0.000001);
		assertEquals(2.0,fair.getTransitions().getCount(biased),0.000001);
		
		assertEquals(1.0,biased.getTransitions().getCount(fair),0.000001);
		assertEquals(3.0,biased.getTransitions().getCount(biased),0.000001);
		
		assertEquals(2.0,fair.getEmissions().getCount(heads),0.000001);
		assertEquals(2.0,fair.getEmissions().getCount(tails),0.000001);
		assertEquals(2.0,biased.getEmissions().getCount(heads),0.000001);
		assertEquals(3.0,biased.getEmissions().getCount(tails),0.000001);
		
		//check probs
		assertEquals(2.0 / 3.0,start.getTransitions().getProbability(fair),0.000001);
		assertEquals(1.0 / 3.0,start.getTransitions().getProbability(biased),0.000001);
		
		assertEquals(0.5,fair.getTransitions().getProbability(fair),0.000001);
		assertEquals(0.5,fair.getTransitions().getProbability(biased),0.000001);
		
		assertEquals(1.0 / 4.0,biased.getTransitions().getProbability(fair),0.000001);
		assertEquals(3.0 / 4.0,biased.getTransitions().getProbability(biased),0.000001);
		
		assertEquals(0.5,fair.getEmissions().getProbability(heads),0.000001);
		assertEquals(0.5,fair.getEmissions().getProbability(tails),0.000001);
		assertEquals(2.0 / 5.0,biased.getEmissions().getProbability(heads),0.000001);
		assertEquals(3.0 / 5.0,biased.getEmissions().getProbability(tails),0.000001);
	}
	
	@Test
	public void testLearn2() {
		//TODO: finish test
		HMM hmm = createBiasedCoinHMM();
		
		State start = hmm.getStartState();
		Symbol heads = hmm.getSymbolByName("H");
		Symbol tails = hmm.getSymbolByName("T");
		State fair = hmm.getStateByName("F");
		State biased = hmm.getStateByName("B");
		
		TraversableOrderedSet<StateSymbolPair> pairs1 = new TraversableOrderedSet<StateSymbolPair>();
		TraversableOrderedSet<StateSymbolPair> pairs2 = new TraversableOrderedSet<StateSymbolPair>();
		
		
		StateSymbolPair pair1 = new StateSymbolPair(fair, heads);
		
		StateSymbolPair pair2 = new StateSymbolPair(biased, heads);
		
		pairs1.add(pair1);
		pairs2.add(pair2);
		
		LinkedList<TraversableOrderedSet<StateSymbolPair>> list = new LinkedList<TraversableOrderedSet<StateSymbolPair>>();
		list.add(pairs1);
		list.add(pairs2);
		
		hmm.learn(list, LEARN_MODE.ZERO_COUNT);
		
		//check counts
		assertEquals(1.0,start.getTransitions().getCount(fair),0.000001);
		assertEquals(1.0,start.getTransitions().getCount(biased),0.000001);
		
		assertEquals(1.0,fair.getEmissions().getCount(heads),0.000001);
		assertEquals(1.0,biased.getEmissions().getCount(heads),0.000001);
		
		hmm.learn(list, LEARN_MODE.PSEUDO_COUNT);
		
		//check counts
		assertEquals(2.0,start.getTransitions().getCount(fair),0.000001);
		assertEquals(2.0,start.getTransitions().getCount(biased),0.000001);
		
		assertEquals(2.0,fair.getEmissions().getCount(heads),0.000001);
		assertEquals(2.0,biased.getEmissions().getCount(heads),0.000001);
		
	}
	
	@Test
	public void testLikelihood() {
		HMM hmm = createBiasedCoinHMM();
		
		Symbol heads = hmm.getSymbolByName("H");
		Symbol tails = hmm.getSymbolByName("T");
		State fair = hmm.getStateByName("F");
		State biased = hmm.getStateByName("B");
		
		List<List<Symbol>> seqs = new LinkedList<List<Symbol>>();
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
		
		seqs.add(seq);
		seqs.add(seq);
		
		double prob = hmm.evaluate(seq, false);
		
		assertEquals(Math.pow(prob, 2), hmm.likelihood(seqs, false),0.0001);
		
	}
	
	@Test
	public void testComputeProbOfTransitionFromStateToState() {

		HMM hmm = createBiasedCoinHMM();
		State start = hmm.getStartState();
		State end = hmm.getEndState();
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
		
		TraversableOrderedSet<TraversableSymbol> emSeq = TraversableOrderedSetUtil.symbolListToTraverseable(seq);
		ViterbiGraph graph = ViterbiGraph.createViterbiGraphFromHmmAndEmittedSymbols(hmm, emSeq);
		
		hmm.calcBackward(graph);
		hmm.calcForward(graph);
		
		Iterator<ViterbiColumn> iter = graph.getColumns().iterator();
		//start
		
		int count = 0;
		ViterbiColumn startCol = iter.next();
		
		double startFair = Math.exp(hmm.computeProbOfTransitionFromStateToState(graph,startCol,start,fair));
		double startBiased = Math.exp(hmm.computeProbOfTransitionFromStateToState(graph,startCol,start,biased));
		assertEquals(1.0, startFair + startBiased, 0.0001);
		count++;
		while(iter.hasNext()) {
			ViterbiColumn column = iter.next();
			//if the next column the end state
			//then there is nothing to do since the last column is contains the end state and there are no transitions to the end state from any states
			if(column.getNext().containsNode(end)) {
				break;
			}
			System.err.println(count);
			double res1 = Math.exp(hmm.computeProbOfTransitionFromStateToState(graph,column,fair,fair));
			double res2 = Math.exp(hmm.computeProbOfTransitionFromStateToState(graph,column,fair,biased));
			double res3 = Math.exp(hmm.computeProbOfTransitionFromStateToState(graph,column,biased,biased));
			double res4 = Math.exp(hmm.computeProbOfTransitionFromStateToState(graph,column,biased,fair));
			
			//for start state
			double fairProb = hmm.probInStateAtPositionGivenSequence(graph,fair,count - 1,false);
			double biasedProb = hmm.probInStateAtPositionGivenSequence(graph,biased,count - 1,false);
			assertEquals(1.0, fairProb + biasedProb, 0.0001);
			
			//double fairProbCheck = column.getNode(fair).getBackward() * column.getNode(fair).getBackward(;
			
			assertEquals(fairProb, res1 + res2, 0.0001);
			assertEquals(biasedProb, res3 + res4, 0.0001);
			
			assertEquals(1.0, res1 + res2 + res3 + res4, 0.0001);
			count++;
		}
		assertTrue(count == seq.size());
		
		//still has end state
		assertTrue(iter.hasNext());
		
	}
	
	@Test
	public void testComputeExpectedTransitionCountsFromStateToState() {
		
		HMM hmm = createBiasedCoinHMM();
		State start = hmm.getStartState();
		State end = hmm.getEndState();
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
		
		TraversableOrderedSet<TraversableSymbol> emSeq = TraversableOrderedSetUtil.symbolListToTraverseable(seq);
		ViterbiGraph graph = ViterbiGraph.createViterbiGraphFromHmmAndEmittedSymbols(hmm, emSeq);
		
		hmm.calcBackward(graph);
		hmm.calcForward(graph);
		
		LinkedList<ViterbiGraph> graphs = new LinkedList<ViterbiGraph>();
		graphs.add(graph);
		
		hmm.initializeStateCounts(LEARN_MODE.ZERO_COUNT);
		
		hmm.computeExpectedTransitionCounts(graphs);
		double startToFair = start.getTransitions().getCount(fair);
		double startToBiased = start.getTransitions().getCount(biased);
		double fairToFair = fair.getTransitions().getCount(fair);
		double fairToBiased = fair.getTransitions().getCount(biased);
		double biasedToFair = biased.getTransitions().getCount(fair);
		double biasedToBiased = biased.getTransitions().getCount(biased);
		
		double sum = startToFair + startToBiased + fairToFair + fairToBiased + biasedToFair + biasedToBiased;
		
		System.err.println("Start to fair: " + startToFair);
		System.err.println("Start to biased: " + startToBiased);
		System.err.println("Fair to fair: " + fairToFair);
		System.err.println("Fair to biased: " + fairToBiased);
		System.err.println("Biased to fair: " + biasedToFair);
		System.err.println("Biased to biased: " + biasedToBiased);
		System.err.println("Sum: " + sum);
		
		//assertEquals(14.0,sum,0.0001);
		assertEquals(14.0,sum,0.0001);
	}
	
	
	@Test
	public void testComputeExpectedEmissionCounts() {
		HMM hmm = createBiasedCoinHMM();
		State start = hmm.getStartState();
		State end = hmm.getEndState();
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

		ViterbiGraph graph = hmm.getViterbiGraph(seq);
		
		hmm.calcBackward(graph);
		hmm.calcForward(graph);
		
		LinkedList<ViterbiGraph> graphs = new LinkedList<ViterbiGraph>();
		graphs.add(graph);
		
		hmm.initializeStateCounts(LEARN_MODE.ZERO_COUNT);
		
		hmm.computeExpectedEmissionCounts(graphs);
		
		double sum = fair.getEmissions().getCount(heads) + fair.getEmissions().getCount(tails) +
				biased.getEmissions().getCount(heads) + biased.getEmissions().getCount(tails);
		
		assertEquals(14,sum,0.00001);
		
	}
	
	
	@Test
	public void testLearnEM1() {
		System.err.println("\ntestLearnEM1\n");
		HMM hmm = createBiasedCoinHMM();
		State start = hmm.getStartState();
		State end = hmm.getEndState();
		Symbol heads = hmm.getSymbolByName("H");
		Symbol tails = hmm.getSymbolByName("T");
		State fair = hmm.getStateByName("F");
		State biased = hmm.getStateByName("B");
		
		TraversableOrderedSet<StateSymbolPair> pairs = hmm.generateSequence(2000);
		List<Symbol> seq = StateSymbolPair.createListOfSymbolsFromStateSymbolPair(pairs);
		
		double before = hmm.evaluate(seq, true);
		
		hmm.initializeStateCounts(LEARN_MODE.PSEUDO_COUNT);
		
		//give a little bias to heads in biased state
		biased.getEmissions().addToCount(heads, 1);
		//give biased to stay in biased state
		biased.getTransitions().addToCount(biased, 1);
		//give biase to stay in fair state
		fair.getTransitions().addToCount(fair, 1);
		
		hmm.setStateProbsFromCounts();
		double startingLikelihood = hmm.evaluate(seq, true);
		hmm.learnEMSingle(seq,LEARN_MODE.CUSTOM);
		
		
		
		;
		System.err.println("Known log likelihood: " + before);
		System.err.println("Starting log likelihood: " + startingLikelihood);
		System.err.println("After log likelihood: " + hmm.evaluate(seq, true));
		
		
		System.err.println("fair to fair: " + fair.getTransitions().getProbability(fair));
		System.err.println("fair to biased: " + fair.getTransitions().getProbability(biased));
		
		System.err.println("fair -- heads: " + fair.getEmissions().getProbability(heads));
		System.err.println("fair -- tails: " + fair.getEmissions().getProbability(tails));
		
		System.err.println("biased to fair: " + biased.getTransitions().getProbability(fair));
		System.err.println("biased to biased: " + biased.getTransitions().getProbability(biased));
		
		System.err.println("biased -- heads: " + biased.getEmissions().getProbability(heads));
		System.err.println("biased -- tails: " + biased.getEmissions().getProbability(tails));
		
		assertEquals(0.9, fair.getTransitions().getProbability(fair), 0.1);
		assertEquals(0.1, fair.getTransitions().getProbability(biased), 0.1);
		assertEquals(0.5, fair.getEmissions().getProbability(heads), 0.1);
		assertEquals(0.5, fair.getEmissions().getProbability(tails), 0.1);
		
		assertEquals(0.9, biased.getTransitions().getProbability(biased), 0.1);
		assertEquals(0.1, biased.getTransitions().getProbability(fair), 0.1);
		assertEquals(0.9, biased.getEmissions().getProbability(heads), 0.1);
		assertEquals(0.1, biased.getEmissions().getProbability(tails), 0.1);
		System.err.println("\ntestLearnEM1End\n");
	}

}
