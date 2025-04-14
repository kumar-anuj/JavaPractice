package com.practice.java8.streams.generalpractice;

import java.util.ArrayList;
import java.util.List;

public class IntFunctionExample {

	public static void main(String[] args) {
		List<String> str = new ArrayList<>();
		str.add("one");
		str.add("two");
		str.add("three");
		str.add("four");
		
		/*Object[] ss = str.stream().map(s -> s.toUpperCase()).toArray();
		
		for(Object s : ss)
			System.out.println((String)s);*/
		
		String[] zz = str.stream().map(s -> s.toUpperCase()).toArray(len -> new String[len]);
		
		for(String s : zz)
			System.out.println(s);
	}

}
