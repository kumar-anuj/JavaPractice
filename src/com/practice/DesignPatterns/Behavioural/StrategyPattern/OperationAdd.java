package com.practice.DesignPatterns.StrategyPattern;

public class OperationAdd implements Strategy {

	@Override
	public int doOperation(int i1, int i2) {
		return i1 + i2;
	}

}
