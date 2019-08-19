package com.practice.java8.comparison;

import java.util.Comparator;
import java.util.List;
import java.util.function.Function;
import java.util.function.ToIntFunction;

import com.practice.cloning.Employee;
import com.practice.java8.streams.reduction.StreamReductionExample1;

public class ComparisonTest1 {

	public static void main(String[] args) {
		List<Employee> empLst = StreamReductionExample1.createEmployeeList();
		
		Function<Employee, String> funcName = Employee::getName;
		Function<Employee, String> funcId = Employee::getId;

		/* Sort Employees on the basis of name and then id */
		empLst.sort(Comparator.comparing(funcName).thenComparing(funcId));
		empLst.forEach(System.out::println);
		System.out.println("=============================================");

		/* Sort Employees on the basis of salary*/
		ToIntFunction<Employee> funcSal = Employee::getSalary;
		empLst.sort(Comparator.comparingInt(funcSal));
		empLst.forEach(System.out::println);
	}

}
