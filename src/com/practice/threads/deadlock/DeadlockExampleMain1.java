package com.practice.threads.deadlock;

public class DeadlockExampleMain1 {
    public static void main(String[] args) throws InterruptedException {
        System.out.println("Main Start");
        MyObject obj1 = new MyObject(10);
        MyObject obj2 = new MyObject(11);
        Thread t1 = new Thread(new NewThread(obj1, obj2), "ONE");
        Thread t2 = new Thread(new NewThread(obj1, obj2), "TWO");
        t1.start(); t2.start();
        t1.join(); t2.join();
        System.out.println("Main Finish");
    }
}

class NewThread implements  Runnable {

    MyObject obj1;
    MyObject obj2;

    public NewThread(MyObject obj1, MyObject obj2) {
        this.obj1 = obj1;
        this.obj2 = obj2;
    }

    @Override
    public void run() {
        System.out.println("1::"+ Thread.currentThread().getName());
        if(Thread.currentThread().getName().equals("ONE")) {
            synchronized (obj1) {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                System.out.println("ONE ::" + obj1.getValue());
                synchronized (obj2) {
                    System.out.println("ONE ::" + obj2.getValue());
                }
            }
        }

        System.out.println("2::"+ Thread.currentThread().getName());
        if(Thread.currentThread().getName().equals("TWO")) {
            synchronized (obj2) {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                System.out.println("TWO ::" + obj2.getValue());
                synchronized (obj1) {
                    System.out.println("TWO ::" + obj1.getValue());
                }
            }
        }
        System.out.println("3::"+ Thread.currentThread().getName());
    }
}
