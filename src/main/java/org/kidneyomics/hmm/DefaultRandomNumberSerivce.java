package org.kidneyomics.hmm;

import java.util.Random;

public class DefaultRandomNumberSerivce implements RandomNumberService {

	
	
	public double getNextRandomNumber() {
		return Math.random();
	}

}
