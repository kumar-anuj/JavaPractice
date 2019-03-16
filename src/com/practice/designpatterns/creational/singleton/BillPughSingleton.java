package com.practice.designpatterns.creational.singleton;

public class BillPughSingleton {

    private BillPughSingleton() { }

    public static BillPughSingleton getInstance()  {
        return SingletonHelper.instance;
    }

    public void showMessage() {
        System.out.println("BillPughSingleton :" + this);
    }

    private static class SingletonHelper {
        static final BillPughSingleton instance = new BillPughSingleton();
    }
}
