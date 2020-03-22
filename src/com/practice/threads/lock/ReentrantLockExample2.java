package com.practice.threads.lock;

import java.util.concurrent.locks.ReentrantLock;

public class ReentrantLockExample2 {
    public static void main(String[] args) throws InterruptedException {
        ReentrantLock lock = new ReentrantLock();
        Thread t1 = new Thread(new NewThread1(lock), "Thread 1");
        Thread t2 = new Thread(new NewThread1(lock), "Thread 2");

        t1.start(); t2.start();
        Thread.sleep(1000);
        System.out.println("Main t1:" + t1.isAlive());
        System.out.println("Main t2:" + t2.isAlive());
        Thread.sleep(1000);
        t2.interrupt();
        t1.interrupt();
        Thread.sleep(1000);
        System.out.println("Main t1:" + t1.isAlive());
        System.out.println("Main t2:" + t2.isAlive());

    }
}

class NewThread1 implements Runnable {
    ReentrantLock lock;

    public NewThread1(ReentrantLock lock) {
        this.lock = lock;
    }

    @Override
    public void run() {

        try {
            lock.lockInterruptibly();
            System.out.println(Thread.currentThread().getName() + " Inside lock " );
            boolean flag;
            while(!( flag = Thread.currentThread().interrupted())) {}
            System.out.println(Thread.currentThread().getName() + " Interrupted " + flag);
        } catch (InterruptedException e) {
            System.out.println(Thread.currentThread().getName() +" Inside Catch: Interrupted");
        }
        finally {
            if(lock.isHeldByCurrentThread())
                lock.unlock();
            System.out.println(Thread.currentThread().getName() +": finally");

        }

    }

}
