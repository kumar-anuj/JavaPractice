package com.anuj.java8;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.function.Supplier;

public class TestOptionalClass {

	public static void main(String[] args) {
		List<String> strList = Arrays.asList();
		
		Optional<String> o1 = strList.stream().findAny();
		
		System.out.println(o1.orElse("1"));
		System.out.println(o1.orElseGet(() -> "2"));
		
	}

}
