package com.java8;

public class MyEmp {
	
	private int id;
	private String name;
	private int age;
	private String gender;
	
	@Override
	public String toString() {
		return "MyEmp [id=" + id + ", name=" + name + ", age=" + age + ", gender=" + gender + "]";
	}
	
	public MyEmp(int id, String name, int age, String gender) {
		super();
		this.id = id;
		this.name = name;
		this.age = age;
		this.gender = gender;
	}

	public MyEmp() {
		super();
	}

	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public int getAge() {
		return age;
	}
	public void setAge(int age) {
		this.age = age;
	}
	public String getGender() {
		return gender;
	}
	public void setGender(String gender) {
		this.gender = gender;
	}
	
	

}
