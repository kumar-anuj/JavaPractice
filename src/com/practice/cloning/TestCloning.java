package com.practice.cloning;

import java.util.Date;

public class TestCloning {

	public static void main(String[] args) throws CloneNotSupportedException {
		Employee emp1 = new Employee("anuj", "1001", new Date(), new Desk(7026, 01206173235L));
		System.out.println("0:"+emp1);
		//Employee emp2 = (Employee) emp1.clone();
		Employee emp2 = new Employee(emp1);
		System.out.println("1:"+emp2);
		System.out.println("2:"+emp1.equals(emp2));
		System.out.println("3:"+ (emp1 == emp2));
		System.out.println("4:"+ (emp1.hashCode() == emp2.hashCode() ));
		
	}

}