package com.practice.java8.streams.generalpractice;

import com.practice.cloning.Employee;
import com.practice.java8.streams.reduction.StreamReductionExample1;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class ThirdHighestSalaryEmployee {
    public static void main(String[] args) {
        List<Employee> empLst = StreamReductionExample1.createEmployeeList();

        Map<Integer, List<Employee>> empMap = empLst.stream().collect(Collectors.groupingBy(e -> e.getSalary()));
        System.out.println(empMap);

        //Set<Integer> salarySet = empMap.keySet();
        //System.out.println(salarySet);
        List<Integer> lst = new ArrayList<>(empMap.keySet());
        lst.sort(Comparator.reverseOrder());
        System.out.println(lst);

        System.out.println(empMap.get(lst.get(2)));

//===================================================================================================
        Employee emp1= empLst.stream().max(Comparator.comparing(e -> ((Integer) e.getSalary()))).get();
        System.out.println(emp1);
        Employee emp2= empLst.stream().filter(e -> (e != emp1)).max(Comparator.comparing(e -> ((Integer) e.getSalary()))).get();
        System.out.println(emp2);
        Employee emp3= empLst.stream().filter(e -> (e != emp1) && (e != emp2)  ).max(Comparator.comparing(e -> ((Integer) e.getSalary()))).get();
        System.out.println(emp3);

    }
}
