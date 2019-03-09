package com.practice.java8;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.function.Predicate;

public class LambdaExample1 {

	public static void main(String[] args) {
		List<Person> person = Arrays.asList(
				new Person("Anuj", "Kumar", 31),
				new Person("Puneet", "Yadav", 38),
				new Person("Jai", "Singh", 25),
				new Person("Gir", "Joshi", 32)
				);
		
		/*for(Person p: person) {
			System.out.println(p);
		}*/
		
		//System.out.println("Sorted Person List");
		/*Collections.sort(person, new Comparator<Person>() {

			@Override
			public int compare(Person o1, Person o2) {
				return o1.getFirstName().compareTo(o2.getFirstName());
			}
		});*/
		
		Collections.sort(person,(p1,p2) -> p1.getLastName().compareTo(p2.getLastName()));
		//person.forEach((a) -> System.out.println(a));
		//person.forEach(System.out::println);
		//boolean b=true;
		printConditionaly(person, (p) -> p.getLastName().startsWith("Y"));
		
	}
	
	public static void printConditionaly (List<Person> person, Predicate<Person> b) {
		/*for(Person p : person) {
			if(b.test(p))
				System.out.println(p);
		}*/
		
		person.forEach((p) -> {
				if(b.test(p))
					System.out.println(p);
			});
		
	}

}
