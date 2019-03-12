package com.practice.java8.streams.reduction;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class StreamReductionExample2 {
    public static void main(String[] args) {
        List<String> lst = createStringList();

        Map<String, Long> str = lst.stream().collect(Collectors.groupingBy(Function.identity(),Collectors.counting()));
        System.out.println(str);
    }

    public static List<String> createStringList() {
        List<String> lst = new ArrayList<>();
        lst.add("ONE");lst.add("TWO");
        lst.add("TWO");lst.add("ONE");lst.add("ONE");
        lst.add("FOUR");lst.add("FOUR");

        return lst;
    }
}
