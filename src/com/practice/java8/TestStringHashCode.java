package com.practice.java8;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class TestStringHashCode {
	
	public String a1, a2;
	public static List<Character> lowerLst = null;
	public static List<Character> totalLst = new ArrayList<>();
	public static Map<String, Integer> charMap = null;

	public static void main(String[] args) {
		lowerLst = createCharList();
		
		lowerLst.forEach(t -> { totalLst.add(Character.toUpperCase(t));
		totalLst.add(t);
		});
		
		charMap = findHashCodeLogic(totalLst);
		
		List<String> charKeys = new LinkedList<>();
		charKeys.addAll(charMap.keySet());
		Collections.sort(charKeys);
		
		Map<Integer, List<String>> reverseMap = findCommonStrings(charKeys);
		
		reverseMap.forEach((t, u) -> {
			if(u.size() >1)
				System.out.println("Value : " + t + ":: List" + u);
		});
		
	}

	private static Map<Integer, List<String>> findCommonStrings(List<String> charKeys) {
		Map<Integer, List<String>> reverseMap = new LinkedHashMap<>();

		charKeys.forEach(t -> {
			Integer i = charMap.get(t);
			if(reverseMap.get(i) == null) {
				List<String> lst = new LinkedList<>();
				lst.add(t);
				reverseMap.put(i,lst);
			}
			else {
				List<String> lst = null;
				lst = reverseMap.get(i);
				if(lst != null) 
					lst.add(t);
				reverseMap.put(i,lst);
			}
		});

		return reverseMap;
		
	}

	private static Map<String, Integer> findHashCodeLogic(List<Character> totalLst) {
		
		Map<String, Integer> charMap = new LinkedHashMap<>();
		
		totalLst.forEach(t -> {
			
			totalLst.forEach( p -> {
				
				charMap.put((char)t + "" + (char)p, (t*31 + p));
			});
		});
		
		return charMap;
		
	}

	private static List<Character> createCharList() {

		String s = "abcdefghijklmnopqrstuvwxyz";
		List<Character> lst = new ArrayList<>();
		
		 char[] alp = s.toCharArray();
		 
		 for(char c : alp){
			lst.add(c);
		 }
		 
		 return lst;
	}
	
	

}
