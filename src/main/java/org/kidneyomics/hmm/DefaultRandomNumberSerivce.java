package org.kidneyomics.hmm;

import java.util.Map;
import java.util.Random;

public class DefaultRandomNumberSerivce implements RandomNumberService {

	private final Random random;
	
	public DefaultRandomNumberSerivce(long seed) {
		random = new Random(seed);
	}
	
	public DefaultRandomNumberSerivce() {
		random = new Random();
	}
	
	public double getNextRandomNumber() {
		return random.nextDouble();
	}


	public <T> T emit(Map<T,Double> map) {
		
		//based on the probabilities of each symbol/sate emit a symbol/state
		double current = 0;
		double rand = this.getNextRandomNumber();
		
		T ret = null;
		
		//e.g. suppose rand = 0.4
		for(Map.Entry<T, Double> entry : map.entrySet()) {
			double val = entry.getValue();
			current += val;
			if(rand <= current) {
				ret = entry.getKey();
				break;
			}
		}
		
		return ret;
	}
}
