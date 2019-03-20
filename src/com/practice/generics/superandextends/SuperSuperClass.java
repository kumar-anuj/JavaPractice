package com.practice.generics.superandextends;

public class SuperSuperClass {
	
	private int value;

	public SuperSuperClass(int value) {
		this.value = value;
	}

	public int getValue() {
		return value;
	}

	@Override
	public String toString() {
		return "SuperSuperClass [value=" + value + "]";
	}

}
