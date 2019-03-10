package com.practice.java8.streams;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class PrimitiveStreams {
    public static void main(String[] args) {
        List<String> lst = new ArrayList<>();
        lst.add("ONE");lst.add("TWO");lst.add("THREE");lst.add("FOUR");lst.add("FIVE");

        System.out.println(lst.stream().mapToInt( t -> t.length()).boxed().collect(Collectors.toList()));
        System.out.println(lst.stream().mapToInt( t -> t.length()).max().getAsInt());

    }
}
