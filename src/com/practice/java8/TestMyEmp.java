package com.practice.java8;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class TestMyEmp {
	static int count = 0;

	public static void main(String[] args) {

		List<MyEmp> myList = TestMyEmp.createMyEmp();
		
		List<MyEmp> lst = myList.stream().filter(t -> t.getGender().equalsIgnoreCase("MALE")).sorted((o1, o2) -> 
		-((Integer.valueOf(o1.getAge())).compareTo(Integer.valueOf(o2.getAge())))).limit(3).collect(Collectors.toList());
		
		lst.forEach(t -> System.out.println(t));
		
		 System.out.println("Female Emp Count::" +myList.stream().filter(t -> t.getGender().equalsIgnoreCase("FEMALE")).count());
		 
		 lst.forEach(t -> count = count + t.getAge());
		 
		 System.out.println(count/4);
		 
		 System.out.println("Name ::"+ myList.stream().filter(t -> t.getName().contains("ad")).collect(Collectors.toList()));
		
		
	}

	
	private static List<MyEmp> createMyEmp() {
		MyEmp e1 =  new MyEmp(1001, "abc", 20, "Male");
		MyEmp e2 =  new MyEmp(1002, "pqr", 60, "Female");
		MyEmp e3 =  new MyEmp(1003, "xyz", 45, "Male");
		MyEmp e4 =  new MyEmp(1004, "mno", 62, "Female");
		MyEmp e5 =  new MyEmp(1005, "mno", 21, "Female");
		MyEmp e6 =  new MyEmp(1006, "rst", 40, "Female");
		MyEmp e7 =  new MyEmp(1007, "cde", 32, "Female");
		MyEmp e8 =  new MyEmp(1008, "wxy", 42, "Male");
		MyEmp e9 =  new MyEmp(1009, "efg", 54, "Male");
		MyEmp e10 =  new MyEmp(1010, "lmn", 32, "Male");
		
		List<MyEmp> myList = Arrays.asList(e1,e2,e3,e4,e5,e6,e7,e8,e9,e10);
		
		return myList;
	}

}
