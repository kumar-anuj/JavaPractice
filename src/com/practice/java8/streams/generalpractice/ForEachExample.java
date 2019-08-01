package com.practice.java8.streams.generalpractice;

import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;

@FunctionalInterface
interface ABC {
	boolean equals(Object o);
	boolean equals1(Object o);
}

@FunctionalInterface
interface X { Iterable m(Iterable<String> arg); }

@FunctionalInterface
interface Y { Iterable<String> m(Iterable arg); }

@FunctionalInterface
interface Z extends X, Y {}

public class ForEachExample {

	public static void main(String[] args) {
		List<String> lst = Arrays.asList("Anuj", "Kumar", "Chaudhary");
		List<String> lst1 =null;
		ABC a = new ABC() {
			
			@Override
			public boolean equals1(Object o) {
				// TODO Auto-generated method stub
				return false;
			}
		};
		System.out.println(a instanceof Object);
		/*for(String s :lst1) {
			System.out.println(s);
		}*/
		
		System.out.println("===FOR IN===");
		for(String s :lst) {
			System.out.println(s);
		}
		
		System.out.println("===FOR EACH===");
		lst.forEach(new Consumer<String>() {

			@Override
			public void accept(String t) {
				System.out.println(t);
			}
		});
		
		System.out.println("===Lambda===");
		lst.forEach((t) -> System.out.println(t));
		
		
		System.out.println("===Method Reference===");
		lst.forEach(System.out::println);
	}

}
