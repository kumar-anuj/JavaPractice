package com.practice.threads.waitnotify;

import com.practice.threads.deadlock.MyObject;

public class WaitNotifyExample1 {
    public static void main(String[] args) throws InterruptedException {
        System.out.println("Main Start");
        MyObject obj = new MyObject(10);
        Thread t1 = new Thread(new Waiter(obj), "Waiter");
        Thread t2 = new Thread(new Notifier(obj), "Notifier");
        t1.start(); t2.start();
        t1.join(); t2.join();
        System.out.println("Main Finish");
    }
}

class Waiter implements Runnable{

    MyObject obj;

    public Waiter(MyObject obj) {
        this.obj = obj;
    }

    @Override
    public void run() {
        System.out.println(Thread.currentThread().getName() + ": Inside run");
        synchronized (obj) {
            System.out.println(Thread.currentThread().getName() + ": synchronized");
            if (obj.getValue() < 50) {
                try {
                    System.out.println(Thread.currentThread().getName() + ": waiting");
                    obj.wait();
                    System.out.println(Thread.currentThread().getName() + ":" + obj.getValue());
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }

    }
}

class Notifier implements Runnable{

    MyObject obj;

    public Notifier(MyObject obj) {
        this.obj = obj;
    }

    @Override
    public void run() {
        try {
            System.out.println(Thread.currentThread().getName() + ": Inside run");
            Thread.sleep(2000);
            synchronized (obj) {
                System.out.println(Thread.currentThread().getName() + ": synchronized");
                while(obj.getValue() <= 50) {
                    obj.setValue(obj.getValue() + 2);
                }
                System.out.println(Thread.currentThread().getName() + ": notify");
                obj.notify();
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }
}