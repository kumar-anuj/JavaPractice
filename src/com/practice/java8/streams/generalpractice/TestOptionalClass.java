package com.practice.java8.streams.generalpractice;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

public class TestOptionalClass {

	public static void main(String[] args) {
		List<String> strList = Arrays.asList();
		
		Optional<String> o1 = strList.stream().findAny();
		
		System.out.println(o1.orElse("1"));
		System.out.println(o1.orElseGet(() -> "2"));
		
	}

}
