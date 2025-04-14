package com.practice.general.basic;

public class ExceptionTest {

	public static void main(String[] args) {
		String str = "new";
		
		try {
			str="new1";
			throw new Exception();
			
		} catch (Exception e) {
			str="new2";
		} finally {
			
			System.out.println("1 : " + str);
			str="new3";
		}
		
		System.out.println("2 : " + str);

	}

}
