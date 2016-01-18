package org.kidneyomics.hmm;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
	public void validateGeneratedSequenceLength() {
		
		HMM hmm = createBiasedCoinHMM();
		
		NextableOrderedSet orderedSet = hmm.generateSequence(100);
		
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
		
		NextableOrderedSet set = new NextableOrderedSet();
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
		NextableOrderedSet best = null; 
		for(int i = 0; i < allPairs.size(); i++) {
			for(int j = 0; j < allPairs.size(); j++) {
				for(int k = 0; k < allPairs.size(); k++) {
					StateSymbolPair pair1 = (StateSymbolPair) allPairs.get(i).clone();
					StateSymbolPair pair2 = (StateSymbolPair) allPairs.get(j).clone();
					StateSymbolPair pair3 = (StateSymbolPair) allPairs.get(k).clone();
					
					NextableOrderedSet set = new NextableOrderedSet();
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
	
}
