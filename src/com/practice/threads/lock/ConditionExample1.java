package com.practice.threads.lock;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

public class ConditionExample1 implements Runnable{
    ReentrantLock lock = new ReentrantLock();
    List<String> lst = new ArrayList<>(5);

    Condition isFull = lock.newCondition();
    Condition isEmpty = lock.newCondition();

    public static void main(String[] args) {
        ConditionExample1 ex1 = new ConditionExample1();
        Thread t1 = new Thread(ex1, "Thread 1");
        Thread t2 = new Thread(ex1, "Thread 2");
        t1.start(); t2.start();

    }

    private void addElement(String value)  {
        lock.lock();
        System.out.println("1)" +Thread.currentThread().getName() + " lock ::"+lock.isHeldByCurrentThread());
            try {
                if(lst.size() == 5) {
                    System.out.println(Thread.currentThread().getName() + ":: List Full::Wait");
                    isFull.await();
                }
                lst.add(value);
                System.out.println(Thread.currentThread().getName() +" :: Add  : " + value);

                isEmpty.signal();

                //System.out.println(Thread.currentThread().getName() + "::List Not Empty::Signal");
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            finally {
                lock.unlock();
                System.out.println("2)" +Thread.currentThread().getName() + " lock ::"+lock.isHeldByCurrentThread());
            }
        }

    private void removeElement(int index) {
        String s="";
        lock.lock();
        System.out.println("1)" +Thread.currentThread().getName() + " lock ::"+lock.isHeldByCurrentThread());
        try {
            if(lst.size() == 0) {
                System.out.println(Thread.currentThread().getName() + ":: List Empty Wait");
                isEmpty.await();
            }
            s = lst.remove(lst.size() - 1);
            System.out.println(Thread.currentThread().getName() + ":: Remove :" + s);

            isFull.signal();
            //System.out.println(Thread.currentThread().getName() + "::List Not Full::Signal");
            Thread.sleep(100);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        finally {
            lock.unlock();
            System.out.println("2)" +Thread.currentThread().getName() + " lock ::"+lock.isHeldByCurrentThread());
        }
    }

    @Override
    public void run() {
        for(int i =0; i<10; i++) {
            if(Thread.currentThread().getName().equals("Thread 1")) {
                addElement(String.valueOf(i));
            }
            if(Thread.currentThread().getName().equals("Thread 2")) {
                removeElement(i);
            }

        }

        System.out.println(Thread.currentThread().getName() + "::Final size ::" + lst.size());
    }


}
