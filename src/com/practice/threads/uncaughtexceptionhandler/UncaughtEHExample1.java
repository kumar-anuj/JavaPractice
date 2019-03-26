package com.practice.threads.uncaughtexceptionhandler;

public class UncaughtEHExample1 {
    public static void main(String[] args) {
        Thread t1 = new Thread(new NewThread(),"Thread 1");
        t1.setUncaughtExceptionHandler(new ExceptionHandler());
        t1.start();
        t1.interrupt();

    }
}

class NewThread implements  Runnable {

    @Override
    public void run() {
        System.out.println("Run");

        if(Thread.interrupted()) {
            throw new NumberFormatException("Bad Number");
        }
    }
}

class ExceptionHandler  implements Thread.UncaughtExceptionHandler {

    @Override
    public void uncaughtException(Thread t, Throwable e) {
        System.out.println("Exception occurred in " + t.getName());
        System.out.println("Exception :" + e.getClass().getName() );
        System.out.println("Exception Message :" + e.getMessage());
        System.out.println("Thread state :" + t.getState());

        new Thread(new NewThread(),"Thread 1").start();

    }
}

