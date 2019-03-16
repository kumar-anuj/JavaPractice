package com.practice.designpatterns.creational.singleton;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

public class SingletonDemo {

   public static void main(String[] args) throws  InterruptedException  {
       /*EagerSingleton eagerSingleton = EagerSingleton.getInstance();
       EagerSingleton eagerSingleton1 = EagerSingleton.getInstance();

       eagerSingleton.showMessage();
       eagerSingleton1.showMessage();

       LazySingleton lazySingleton = LazySingleton.getInstance();
       LazySingleton lazySingleton1 = LazySingleton.getInstance();

       lazySingleton.showMessage();
       lazySingleton1.showMessage();*/

       ExecutorService executorService = Executors.newFixedThreadPool(5);

       List<MyThread> myThreadList = createTasks();
       List<Future<String>> futureList = executorService.invokeAll(myThreadList);
       //futureList.forEach(t -> System.out.println(t.get()));

       executorService.shutdown();

   }

    private static List<MyThread> createTasks() {
        List<MyThread> myThreadList = new ArrayList<>();
        myThreadList.add(new MyThread());
        myThreadList.add(new MyThread());
        myThreadList.add(new MyThread());
        myThreadList.add(new MyThread());
        myThreadList.add(new MyThread());

        return myThreadList;
    }


}

class MyThread implements Callable<String> {

    MyThread() { }

    @Override
    public String call() throws Exception {
        ThreadSafeSingleton tss = ThreadSafeSingleton.getInstance();
        //LazySingleton tss = LazySingleton.getInstance();
        System.out.println("ThreadSafeSingleton :" + tss);
        return "Success";
    }
}
