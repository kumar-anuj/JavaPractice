package com.practice.threads.deadlock;

public class MyObject {
    public int value;

    public MyObject(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }

    public void setValue(int value) {
        this.value = value;
    }
}
