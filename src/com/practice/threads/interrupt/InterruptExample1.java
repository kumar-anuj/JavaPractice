package com.practice.threads.interrupt;

public class InterruptExample1 {
    public static void main(String[] args) throws InterruptedException {
        Thread t1 = new Thread(new NewThread(),"Thread 1");
        t1.start();
        Thread.sleep(1);
        t1.interrupt();
        System.out.println("1 ::" + t1.isInterrupted());

        System.out.println("Alive ::" +t1.isAlive());
    }
}

class NewThread implements  Runnable {

    @Override
    public void run() {
            while(true) {
                boolean flag = Thread.interrupted();    //return the interrupted status and set as false
                System.out.println("run : "+flag + "::" + Thread.interrupted() + "::" + Thread.currentThread().isInterrupted());
                if(flag)
                    break;

            }

        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println("end");

    }
}
