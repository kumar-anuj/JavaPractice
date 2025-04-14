package com.practice.general.basic;

import java.util.Arrays;
import java.util.stream.Collectors;

public class StringTest1 {

	public static void main(String[] args) {
		
		String s1="a,b,c";
		
		String[] s = s1.split(",");
		
		for(int i =0; i<s.length; i++) {
			s[i] = "'" + s[i] + "'";
		}
		
		System.out.println(String.join(",", s));
		
		System.out.println(String.join(",", Arrays.stream(s1.split(",")).map(x -> "'" + x + "'").collect(Collectors.toList())));
		
		
	}

}
