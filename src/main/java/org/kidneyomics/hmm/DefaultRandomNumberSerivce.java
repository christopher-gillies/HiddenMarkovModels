package org.kidneyomics.hmm;

public class DefaultRandomNumberSerivce implements RandomNumberService {

	public double getNextRandomNumber() {
		return Math.random();
	}

}
