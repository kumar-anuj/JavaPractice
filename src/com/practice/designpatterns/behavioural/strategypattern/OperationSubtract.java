package com.practice.designpatterns.behavioural.strategypattern;

public class OperationSubtract implements Strategy {

	@Override
	public int doOperation(int i1, int i2) {
		return Math.abs(i1 - i2);
	}

}
