package com.practice.general.basic;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

public class StringTest1 {

	public static void main(String[] args) {
		
		String s1="a,b,c";
		
		String[] s = s1.split(",");
		
		for(int i =0; i<s.length; i++) {
			s[i] = "'" + s[i] + "'";
		}
		
		System.out.println(String.join(",", s));
		
		System.out.println(Arrays.stream(s1.split(",")).map(x -> "'" + x + "'").collect(Collectors.joining(",")));
		System.out.println(String.join(",", Arrays.stream(s1.split(",")).map(x -> "'" + x + "'").collect(Collectors.toList())));

		String[] str1 = {"1", "2"};
		String[] str2 = {"3", "4"};
		List<String[]> ll = new ArrayList<>();
		ll.add(str1);
		ll.add(str2);

		StringBuilder sb = new StringBuilder();
		//ll.forEach(arr -> System.out.println(Arrays.toString(arr)));
		for(String[] l: ll)
			sb.append(Arrays.toString(l));

		System.out.println(sb);

		int size =1;

		for(int i=0; i<size; i++){
			System.out.println("Hello");
		}

		System.out.println(Arrays.toString(str1));

		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");

		sdf.setTimeZone(TimeZone.getTimeZone("America/New_York"));
		System.out.println(Timestamp.valueOf(sdf.format(new Date())));

		String ss = "hello:  hello1:     hello2:   hello3";
		System.out.println(Arrays.stream(ss.split(":")).map(String::trim).collect(Collectors.toList()));

		String ss1 = "1:  2:     3:   4";
		System.out.println(Arrays.stream(ss1.split(":")).map(String::trim).map(Integer::parseInt).collect(Collectors.toList()));
		
		
	}

}
