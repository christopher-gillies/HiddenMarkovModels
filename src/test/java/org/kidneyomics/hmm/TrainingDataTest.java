package org.kidneyomics.hmm;

import static org.junit.Assert.*;

import java.io.File;
import java.io.IOException;
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
	public void test() throws IOException {

		
		StringBuilder sb = new StringBuilder();
		
		String outDir = FileUtils.getTempDirectoryPath();
		File out = new File(outDir + "/test.txt");
		
		System.err.println("File: " + out.getAbsolutePath());
		for(int i = 50; i < 20000; i+=50) {
			if(i > 2000) {
				i+= 1000;
			}
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

}
