package com.practice.java8.streams.reduction;

import com.practice.cloning.Desk;
import com.practice.cloning.Employee;

import java.util.Arrays;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

public class StreamReductionExample1 {
    public static void main(String[] args) {
        List<Employee> empLst = createEmployeeList();

        // Get Max Emp ID
        System.out.println(empLst.stream().map(Employee::getId).max(Comparator.naturalOrder()).get());
        System.out.println(empLst.stream().map(Employee::getId).reduce("1000", (s1, s2) -> s2.compareTo(s1) > 0 ? s2 : s1));

        // Grouping example
        System.out.println(empLst.stream().collect(Collectors.groupingBy( t -> t.getDesk().getDeskPhone())));
        System.out.println(empLst.stream().collect(Collectors.groupingBy( t -> t.getDesk().getDeskPhone(), Collectors.counting())));


    }

    public static List<Employee> createEmployeeList() {
        Employee emp1 = new Employee("anuj", "1001",
                new Date(), new Desk(7026, 6173235L), 10000);
        Employee emp2 = new Employee("abhi", "1002",
                new Date(), new Desk(7027, 6173233L), 20000);
        Employee emp3 = new Employee("mayank", "1003",
                new Date(), new Desk(7028, 6173233L), 15000);
        Employee emp4 = new Employee("vishal", "1004",
                new Date(), new Desk(7029, 6173232L), 100000);
        Employee emp5 = new Employee("rahul", "1005",
                new Date(), new Desk(7024, 6173231L), 20000);
        Employee emp6 = new Employee("anuj", "1006",
                new Date(), new Desk(8026, 6173235L), 50000);
        Employee emp7 = new Employee("mayank", "1007",
                new Date(), new Desk(8028, 6173233L), 55000);

        return Arrays.asList(emp6, emp7, emp1, emp2, emp3, emp4, emp5 );
    }
}
