package com.practice.java8;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class MyTest {
    public static void main(String[] args) {
        Employee emp1 = new Employee("Anuj", new Dept("FS"));
        Employee emp2 = new Employee("Ankur", new Dept("IT"));
        Employee emp3 = new Employee("Puneet", new Dept("EC"));
        Employee emp4 = new Employee("Mayank", new Dept("FS"));
        Employee emp5 = new Employee("Nikhil", new Dept("IT"));
        Employee emp6 = new Employee("Amit", new Dept("EC"));
        Employee emp7 = new Employee("Sud", new Dept("EC"));

        List<Employee> list = new ArrayList<>();
        list.add(emp1);
        list.add(emp2);
        list.add(emp3);
        list.add(emp4);
        list.add(emp5);
        list.add(emp6);
        list.add(emp7);
        Function<Employee, String> func1 = emp -> emp.getDept();
        //Map<String, Long> map = list.stream().collect(Collectors.groupingBy(func1, Collectors.counting()));
        Map<String, List<Employee>> map = list.stream().collect(Collectors.groupingBy(func1));

        list.forEach((emp) -> System.out.println(emp));
        list.forEach(System.out::println);

        Optional o = Stream.of(1,2,3,4,5).max(Comparator.naturalOrder());
        System.out.println(o.get());



        //System.out.println(map);

    }


}

class Employee {
    String empName;
    Dept dept;

    @Override
    public String toString() {
        return "Employee{" +
                "empName='" + empName + '\'' +
                '}';
    }

    public Employee(String empName, Dept dept) {
        this.empName = empName;
        this.dept = dept;
    }

    public String getEmpName() {
        return empName;
    }

    public void setEmpName(String empName) {
        this.empName = empName;
    }

    public String getDept() {
        return dept.toString();
    }

    public void setDept(Dept dept) {
        this.dept = dept;
    }
}

class Dept {
    String name;

    @Override
    public String toString() {
        return name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Dept(String name) {
        this.name = name;

    }
}
