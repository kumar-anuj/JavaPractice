package com.anuj.cloning;

import java.util.Date;

public class TestCloning {

	public static void main(String[] args) throws CloneNotSupportedException {
		Employee emp1 = new Employee("anuj", "1001", new Date(), new Desk(7026, 01206173235L));
		System.out.println(emp1);
		Employee emp2 = (Employee) emp1.clone();
		System.out.println(emp2);
		System.out.println(emp1.equals(emp2));
		System.out.println(emp1.hashCode() == emp2.hashCode());
		
	}

}