package com.practice.generics.superandextends;

import java.util.ArrayList;
import java.util.List;

public class TestExtendsAndSuperExample1 {

	public static void main(String[] args) {
		// Extends Example - Can only Read
		List<SubClassA> lstA = new ArrayList<>();
		lstA.add(new SubClassA(10));
		readList(lstA);
		
		List<SubClassB> lstB = new ArrayList<>();
		lstB.add(new SubClassB(9));
		readList(lstB);
		
		// Super Example - Can only Add
		List<SuperSuperClass> lstSS = new ArrayList<>();
		lstSS.add(new SuperSuperClass(11));
		lstSS.add(new SuperClass(12));
		lstSS.add(new SubClassA(20));
		writeList(lstSS);
		
		// Read and Write both
		List<SuperClass> lstS = new ArrayList<>();
		lstS.add(new SuperClass(20));
		readList(lstS);
		writeList(lstS);
		
	}

	private static void readList(List<? extends SuperClass> lst) {
		SuperClass s = lst.get(0);
		System.out.println(s.getValue());
	}
	
	private static void writeList(List<? super SuperClass> lst) {
		lst.add(new SuperClass(13));
		lst.add(new SubClassA(11));
		lst.add(new SubClassB(12));
		System.out.println(lst);
	}

}
