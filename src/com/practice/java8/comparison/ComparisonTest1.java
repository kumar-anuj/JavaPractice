package com.practice.java8.comparison;

import java.util.Comparator;
import java.util.List;
import java.util.function.Function;

import com.practice.cloning.Employee;
import com.practice.java8.streams.reduction.StreamReductionExample1;

public class ComparisonTest1 {

	public static void main(String[] args) {
		List<Employee> empLst = StreamReductionExample1.createEmployeeList();
		
		Function<Employee, String> funcName = emp -> emp.getName();
		Function<Employee, String> funcId = emp -> emp.getId();
		
		empLst.sort(Comparator.comparing(funcName).thenComparing(funcId));
		
		empLst.forEach(System.out::println);
	}

}
