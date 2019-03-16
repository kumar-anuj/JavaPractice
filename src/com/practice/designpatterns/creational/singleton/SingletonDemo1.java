package com.practice.designpatterns.creational.singleton;

public class SingletonDemo1 {
    public static void main(String[] args) {
        BillPughSingleton billPughSingleton = BillPughSingleton.getInstance();
        BillPughSingleton billPughSingleton1 = BillPughSingleton.getInstance();
        billPughSingleton.showMessage();
        billPughSingleton1.showMessage();

        EnumSingleton enumSingleton = EnumSingleton.INSTANCE;
        EnumSingleton enumSingleton1 = EnumSingleton.INSTANCE;
        System.out.println(enumSingleton.name());
        System.out.println(enumSingleton1.name());

    }
}
