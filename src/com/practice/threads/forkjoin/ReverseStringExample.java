package com.practice.threads.forkjoin;

import java.sql.SQLOutput;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.RecursiveAction;

public class ReverseStringExample {
    public static void main(String[] args) {
        String str = "This is new India I like it very much";

        String[] newString = str.split(" ");
        List<String> newList = Arrays.asList(newString);
        List<String> workLoad = new ArrayList<>(newList);
        /*for (int i = newString.length-1; i >= 0; i--)
        {
            System.out.println(newString[i]);
        }*/

        ForkJoinPool forkJoinPool = new ForkJoinPool();
        NewAction action = new NewAction(workLoad);
        forkJoinPool.invoke(action);

    }
}

class NewAction extends RecursiveAction {
    private List<String> workLoad;
    private List<NewAction> subtasks = new ArrayList<NewAction>();

    public NewAction(List<String> workLoad) {
        this.workLoad = workLoad;
    }

    @Override
    protected void compute() {
        if(this.workLoad.size() > 1) {
            subtasks.addAll(createSubtasks(this.workLoad));
            //System.out.println("Compute : " + subtasks.size());
            for (NewAction action : subtasks) {
                action.fork();
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
        else {
            System.out.println(this.workLoad);
        }
    }

    private List<NewAction> createSubtasks(List<String> workLoad) {
        List<NewAction> subtasks = new ArrayList<NewAction>();
        NewAction subtask;

        while(workLoad.size() > 0) {

            subtask = new NewAction(Arrays.asList(workLoad.get(workLoad.size() - 1)));
            //System.out.println(subtask.workLoad);
            subtasks.add(subtask);
            workLoad.remove(workLoad.size() - 1);
        }
        //System.out.println("Create :" + subtasks.size());
        return subtasks;
    }

}