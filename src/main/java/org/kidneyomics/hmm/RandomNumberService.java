package org.kidneyomics.hmm;

import java.util.Map;

public interface RandomNumberService {
	double getNextRandomNumber();
	<T> T emit(Map<T,Double> map);
}
