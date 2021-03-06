package com.practice.java8.streams.map;

import com.practice.cloning.Employee;
import com.practice.java8.streams.reduction.StreamReductionExample1;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import java.util.function.UnaryOperator;
import java.util.stream.Collectors;

public class StreamsMapFunctionExample {

    public static void main(String[] args) {
        List<String> lst = new ArrayList<>();
        lst.add("ONE");lst.add("TWO");lst.add("THREE");lst.add("FOUR");lst.add("FIVE");

        /* Map Examples */
        System.out.println(lst.stream().map(String::toLowerCase).collect(Collectors.toList()));
        // Function Compose
        UnaryOperator<String> func1 = String::toLowerCase;
        System.out.println(lst.stream().map(func1.compose( func -> String.valueOf(func.charAt(0)) )).collect(Collectors.toList()));
        // Function AndThen
        UnaryOperator<String> func2 = func -> func.concat("Z");
        System.out.println(lst.stream().map(func2.andThen( func -> String.valueOf(func.charAt(func.length()-1)) )).collect(Collectors.toList()));
        // Function Identity
        UnaryOperator<String> func3 = UnaryOperator.identity();
        System.out.println(lst.stream().map(func3).collect(Collectors.toList()));

        // Update all Employees salary
        List<Employee> empLst = StreamReductionExample1.createEmployeeList();
        UnaryOperator<Employee> f1 = emp -> {
            emp.setSalary(200);
            return emp;
        };

        empLst.stream().map(f1).collect(Collectors.toList()).forEach( e -> System.out.println(e.getSalary()));

    }
}
