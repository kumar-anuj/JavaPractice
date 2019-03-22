package com.practice.threads.threadpool;

public class ThreadPoolMain {

    public static void main(String[] args) throws InterruptedException {
        Runnable task1 = () ->  System.out.println("Task 1");
        Runnable task2 = () ->  System.out.println("Task 2");
        Runnable task3 = () ->  System.out.println("Task 3");

        CustomTreadPool pool = new CustomTreadPool(1);
        pool.submitTask(task1);
        pool.submitTask(task2);
        pool.submitTask(task3);

        Thread.sleep(3000);
        pool.shutDown();
    }
}
