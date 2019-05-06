package com.practice.threads.waitnotify;

public class PrintNumberSequenceExample1 {
    public static void main(String[] args) {
        MyObject obj = new MyObject();
        Thread t1 = new Thread(new MyThread1(obj), "Thread 1");
        Thread t2 = new Thread(new MyThread2(obj), "Thread 2");
        t1.start(); t2.start();
    }
}

class MyObject {
    volatile boolean flag = true;
}

class MyThread1 implements Runnable {
    MyObject obj;

    public MyThread1(MyObject obj) {
        this.obj = obj;
    }

    @Override
    public void run() {
        for(int i =1 ; i <11; i = i+1 ) {
            if(( i%2 == 1 ) && obj.flag) {
                System.out.println(Thread.currentThread().getName() +"::"+ i);
                obj.flag = false;
            }
            else {
                while(!obj.flag) {}
            }

        }
    }
}

class MyThread2 implements Runnable {
    MyObject obj;

    public MyThread2(MyObject obj) {
        this.obj = obj;
    }

    @Override
    public void run() {
        for(int i =1 ; i <11; i = i+1 ) {
            if((i%2 == 0 ) && !obj.flag){
                System.out.println(Thread.currentThread().getName() +"::"+ i);
                obj.flag = true;
            }
            else {
                while(obj.flag) {}
            }

        }
    }
}
