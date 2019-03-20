package com.practice.generics.superandextends;

public class SuperClass extends SuperSuperClass{
	private int value;

	public SuperClass(int value) {
		super(value);
		this.value = value;
	}

	public int getValue() {
		return value;
	}

	@Override
	public String toString() {
		return "SuperClass [value=" + value + "]";
	}

}
