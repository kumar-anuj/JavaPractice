package com.practice.java8.streams;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.function.BinaryOperator;
import java.util.stream.Collectors;

public class PrimitiveStreams {
    public static void main(String[] args) {
        List<String> lst = new ArrayList<>();
        lst.add("ONE");
        lst.add("TWO");
        lst.add("THREE");
        lst.add("FOUR");
        lst.add("FIVE");

        // IntStream to list
        System.out.println(lst.stream().mapToInt(t -> t.length()).boxed().collect(Collectors.toList()));
        // IntStream max
        System.out.println(lst.stream().mapToInt(t -> t.length()).max().getAsInt());
        // IntStream Summary Statistics
        System.out.println(lst.stream().mapToInt(t -> t.length()).summaryStatistics());



    }
}
