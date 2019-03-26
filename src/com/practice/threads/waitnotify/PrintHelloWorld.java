package com.practice.threads.waitnotify;

public class PrintHelloWorld {
    private static volatile Boolean flag = false;

    public static Boolean getFlag() {
        return flag;
    }

    public static void setFlag(Boolean flag) {
        PrintHelloWorld.flag = flag;
    }

    public static void main(String[] args) {
        Object obj = new Object();

        Thread t1 = new Thread(new Thread1(obj), "Thread1");
        Thread t2 = new Thread(new Thread2(obj), "Thread2");

        t1.start(); t2.start();
    }

}

class Thread1 implements Runnable {
    Object obj;

    public Thread1(Object obj) {
        this.obj = obj;
    }

    @Override
    public void run() {
        synchronized (obj) {
            System.out.print("Hello ");
            PrintHelloWorld.setFlag(true);
            obj.notify();
            try {
                obj.wait();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}

class Thread2 implements Runnable {
    Object obj;

    public Thread2(Object obj) {
        this.obj = obj;
    }
    @Override
    public void run() {
        synchronized (obj) {
            try {
                while (!PrintHelloWorld.getFlag()) {
                    obj.wait();
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            System.out.print("World ");
            obj.notify();
            PrintHelloWorld.setFlag(false);
        }
    }
}
