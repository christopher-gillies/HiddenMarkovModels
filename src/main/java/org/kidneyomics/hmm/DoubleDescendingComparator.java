package org.kidneyomics.hmm;

import java.util.Comparator;

class DoubleDescendingComparator implements Comparator<Double> {

	public int compare(Double o1, Double o2) {
		return -1 * o1.compareTo(o2);
	}
	
}
