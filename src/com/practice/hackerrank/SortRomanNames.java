package com.practice.hackerrank;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.function.ToIntFunction;

public class SortRomanNames {
	
	public static void main(String[] args) {
		Map<String, Integer> valMap = createValueMap();
		String[] nameArr = {"Anuj L", "ANUJ IV", "ANKUR XI", "ANUJ V", "ANKUR II", "ANUJ IX", 
				"ANUJ X", "ANUJ XX", "ANUJ I"};
		List<Name> nameList = new ArrayList<>();
		
		for(String s : nameArr) {
			int sum = 0;
			String[] sArr = s.split(" ");
			char[] chArr = sArr[1].toCharArray();
			
			for(int i =0; i< chArr.length; i++) {
				if(i == 0) {
					sum = sum + valMap.get(String.valueOf(chArr[i]));
				}
				else {
					if(valMap.get(String.valueOf(chArr[i])) <= valMap.get(String.valueOf(chArr[i-1]))) {
						sum = sum + valMap.get(String.valueOf(chArr[i]));
					}
					else {
						sum = Math.abs(sum - valMap.get(String.valueOf(chArr[i])));
					}
				}
			}
			
			Name name = new Name(sArr[0], sArr[1], sum);
			nameList.add(name);
		}
		
		Function<Name, String> f1 = Name::getFirstName;
		ToIntFunction<Name> f2 = Name::getValue;
		nameList.sort(Comparator.comparing(f1).thenComparingInt(f2));
		
		System.out.println("Names ::" + nameList);
		
	}

	private static Map<String, Integer> createValueMap() {
		HashMap<String, Integer> valMap = new HashMap<>();
		valMap.put("I", 1);
		valMap.put("V", 5);
		valMap.put("X", 10);
		valMap.put("L", 50);
		valMap.put("C", 100);
		valMap.put("D", 500);
		valMap.put("M", 1000);
		
		return valMap;
	}

}

class Name {
	private String firstName;
	private String lastName;
	private Integer value;
	
	public Name(String firstName, String lastName, Integer value) {
		super();
		this.firstName = firstName;
		this.lastName = lastName;
		this.value = value;
	}
	
	public String getFirstName() {
		return firstName;
	}
	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}
	public String getSecondName() {
		return lastName;
	}
	public void setSecondName(String lastName) {
		this.lastName = lastName;
	}
	public Integer getValue() {
		return value;
	}
	public void setValue(Integer value) {
		this.value = value;
	}

	@Override
	public String toString() {
		return  firstName  + " " + lastName;
	}
	
	
}
