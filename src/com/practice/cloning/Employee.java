package com.practice.cloning;

import java.util.Date;

public class Employee implements Cloneable{
	
	private String name;
	private String id;
	private Date dob;
	private Desk desk;
	private int salary;
	
	public Employee (String name, String id, Date dob, Desk desk, int salary) {
		this.name = name;
		this.id = id;
		this.dob = dob;
		this.desk = desk;
		this.salary = salary;
	}

	public Employee (String name, String id, Date dob, Desk desk) {
		this.name = name;
		this.id = id;
		this.dob = dob;
		this.desk = desk;
	}

	public Employee(Employee emp) {
		this.name = emp.getName();
		this.id = emp.getId();
		this.dob = emp.getDob();
		this.desk = new Desk(emp.getDesk());
		this.salary = emp.getSalary();
	}

	@Override
	protected Object clone() throws CloneNotSupportedException {
		//return (Employee)super.clone();
		return new Employee(this);
	}
	
	@Override
	public boolean equals(Object obj) {
		if(obj instanceof Employee) { 
			Employee d1 =(Employee)obj;
			if(d1.getName().equals(this.getName()) && d1.getId().equals(this.getId()) 
					&& d1.getDob().equals(this.getDob()) && d1.getDesk().equals(this.getDesk()))
				return true;
		}
		return false;
	}
	
	@Override
	public String toString() {
		return this.getName() + ": "+ this.getId() + ": "+ this.getDob().toString() + ": " + this.desk.getSeatNo();
	}
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public Date getDob() {
		return dob;
	}
	public void setDob(Date dob) {
		this.dob = dob;
	}

	public Desk getDesk() {
		return desk;
	}

	public void setDesk(Desk desk) {
		this.desk = desk;
	}

	public int getSalary() {
		return salary;
	}

	public void setSalary(int salary) {
		this.salary = salary;
	}
	
}
