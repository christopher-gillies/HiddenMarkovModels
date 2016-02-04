package org.kidneyomics.hmm;

import static org.junit.Assert.*;

import java.io.File;
import java.io.IOException;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.junit.Test;
import org.kidneyomics.hmm.HMM.LEARN_MODE;

public class TrainingDataTest {

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
	
	//@Test
	/**
	 * This method generates training data of different sizes and calculates the error in the training data for each size.
	 * The error is calculated as the sum of the absolute differences from the true observations across all states transitions and emissions.
	 * @throws IOException
	 */
	public void testTrainingDataSize() throws IOException {

		
		StringBuilder sb = new StringBuilder();
		
		String outDir = FileUtils.getTempDirectoryPath();
		File out = new File(outDir + "/test.txt");
		
		System.err.println("File: " + out.getAbsolutePath());
		for(int i = 50; i < 20000; i+=50) {
			System.err.println(i);
			HMM hmm = createBiasedCoinHMM();
			Symbol heads = hmm.getSymbolByName("H");
			Symbol tails = hmm.getSymbolByName("T");
			State fair = hmm.getStateByName("F");
			State biased = hmm.getStateByName("B");
			
			TraversableOrderedSet<StateSymbolPair> pairs = hmm.generateSequence(i);
			List<Symbol> seq = StateSymbolPair.createListOfSymbolsFromStateSymbolPair(pairs);
			hmm.initializeStateCounts(LEARN_MODE.PSEUDO_COUNT);
			
			biased.getEmissions().addToCount(heads, 1);
			biased.getTransitions().addToCount(biased, 1);
			fair.getTransitions().addToCount(fair, 1);
			
			hmm.learnEMSingle(seq,LEARN_MODE.CUSTOM);
			
			//transitions
			double diff1 = Math.abs(  0.9 - fair.getTransitions().getProbability(fair)   );
			double diff2 = Math.abs(  0.1 - fair.getTransitions().getProbability(biased)   );
			double diff3 = Math.abs(  0.1 - biased.getTransitions().getProbability(fair)   );
			double diff4 = Math.abs(  0.9 - biased.getTransitions().getProbability(biased)   );
			
			double diff5 = Math.abs(  0.5 - fair.getEmissions().getProbability(heads)   );
			double diff6 = Math.abs(  0.5 - fair.getEmissions().getProbability(tails)   );
			double diff7 = Math.abs(  0.9 - biased.getEmissions().getProbability(heads)   );
			double diff8 = Math.abs(  0.1 - biased.getEmissions().getProbability(tails)   );
			
			double sum = diff1 + diff2 + diff3 + diff4 + diff5 + diff6 + diff7 + diff8;
			sb.append(i + "\t" + sum  + "\t" + diff1 + "\t" + diff2 + "\t" + diff3 +"\t" + diff4 + "\t" + diff5 + "\t" + diff6 + "\t" + diff7 + "\t" + diff8);
			sb.append("\n");
		}
		
		FileUtils.write(out, sb.toString());
		
	}
	
	@Test
	/**
	 * The purpose of this method is to generate a sequence of 2000 points and then generate different lengths of sub sequence
	 * The goal will be to estimate the probability of the last state versus the true state
	 */
	public void testNumberofObservationsToAccuratelyGuessCurrentState() {
		HMM hmm = createBiasedCoinHMM();
		TraversableOrderedSet<StateSymbolPair> pairs = hmm.generateSequence(2000);
		List<Symbol> seq = StateSymbolPair.createListOfSymbolsFromStateSymbolPair(pairs);
		
		for(int length = 1; length <= 100; length+=1) {
			double totalError = 0.0;
			int count = 0;
			Iterator<StateSymbolPair> iter = pairs.iterator();
			while(iter.hasNext()) {
				StateSymbolPair next = iter.next();
				
				//build previous seq
				StateSymbolPair current = next;
				List<Symbol> syms = new LinkedList<Symbol>();
				State lastState = null;
				for(int i = 0; i < length; i++) {
					if(current == null) {
						break;
					}
					
					if(i == length - 1) {
						lastState = current.getState();
					}
					syms.add(current.getEmittedSymbol());
					current = current.getNext();
					
				}
				
				if(syms.size() == length && lastState != null) {
					count++;
					double prob = hmm.probInStateAtPositionGivenSequence(lastState, syms.size() - 1, syms, false);
					double error = 1 - prob;
					totalError += error;
				}
				
			}
			double avgError = totalError / (double) count;
			System.err.println(length + "\t" + avgError);
		}
		
		
		
		
	}

}
