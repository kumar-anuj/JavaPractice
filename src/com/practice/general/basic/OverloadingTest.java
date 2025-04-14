package com.practice.general.basic;

public class OverloadingTest {
	public static final String STR ="Value :";

	public static void main(String[] args) {
		OverloadingTest o1 = new OverloadingTest();
		o1.display(null);
	}
	
	public void display(String s) {
		System.out.println(STR + "String" + s);
	}
	
	public void display(boolean s) {
		System.out.println(STR + "boolean" +  s);
	}
	
	public void display(int s) {
		System.out.println(STR + "int" + s);
	}

	public void display(Object s) {
		System.out.println(STR + "Object" + s);
	}
}
