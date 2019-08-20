package com.practice.datastructures.recursion;

public class Factorial {
    public static void main(String[] args) {
        int number = 5;
        long result = recursiveFactorial(number);
        System.out.println("Result ::" + result);
    }

    public static int recursiveFactorial(int num) {
        if(num == 0)
            return 1;
        else
            return (num * recursiveFactorial(num -1));
    }
}
