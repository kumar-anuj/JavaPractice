package com.practice.designpatterns.structural.compositepattern;

import java.util.ArrayList;
import java.util.List;

public class CompositeEmployee {
    private String name;
    private String dept;
    private int salary;
    private List<CompositeEmployee> subordinates;

    public CompositeEmployee(String name, String dept, int salary) {
        this.name = name;
        this.dept = dept;
        this.salary = salary;
        this.subordinates = new ArrayList<>();
    }

    public void add(CompositeEmployee ce) {
        subordinates.add(ce);
    }

    public void remove(CompositeEmployee ce) {
        subordinates.remove(ce);
    }

    public List<CompositeEmployee> getSubordinates() {
        return subordinates;
    }

    @Override
    public String toString() {
        return "CompositeEmployee{" +
                "name='" + name + '\'' +
                ", dept='" + dept + '\'' +
                ", salary=" + salary +
                '}';
    }
}
