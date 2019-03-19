package com.practice.datastructures.testpractice;

import java.util.ArrayList;
import java.util.List;

public class FinalListTest {

	public static void main(String[] args) {
		int x = 5;
		
		System.out.println("Outut is :" + ((x>5) ? 3.1 : 2));
		
		final List<String> lst = new ArrayList<>();
		lst.add("one");
		System.out.println(lst);
		
	}

}
