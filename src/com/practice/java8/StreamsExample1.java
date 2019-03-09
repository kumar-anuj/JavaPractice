package com.practice.java8;

import java.util.Arrays;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class StreamsExample1 {

	public static void main(String[] args) {
		List<Person> person = Arrays.asList(
				new Person("Anuj", "Kumar", 31)
				,new Person("Puneet", "Yadav", 38)
				,new Person("Jai", "Singh", 25)
				,new Person("Gir", "Joshi", 32)
				);
		//System.out.println(person.stream().filter(a -> a.getAge() <30).findFirst().get());
		//System.out.println(person.stream().filter(a -> a.getAge() >30).
				//reduce((v,k) -> {v.setAge(k.getAge()); return v;}).get());
		
		List<Integer> intList = Arrays.asList(11,3,9,10,23,1,32,60);
		//System.out.println(intList.stream().filter( t ->  t >10 ? true : false).collect(Collectors.toList()));
		Integer l = intList.stream().filter( t ->  t >10).mapToInt(i -> i).sum();
		//System.out.println(intList.stream().filter( t ->  t >10).map(t -> t +10).collect(Collectors.toList()));
		//System.out.println("Sum :"+l);
		
		Double total = Stream.of(7.2, 1.4, 4.6).reduce(0.0, (a, b) -> a + b);
		System.out.println("Total :" + total);

	}

}
