package com.practice.general.basic;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.time.LocalTime;
import java.util.*;

import static java.awt.event.InputEvent.BUTTON1_DOWN_MASK;

public class Test2 {

    private static final int[] SAFE_KEYS = {KeyEvent.VK_UP, KeyEvent.VK_DOWN, KeyEvent.VK_LEFT,
            KeyEvent.VK_RIGHT, KeyEvent.VK_PAGE_DOWN, KeyEvent.VK_PAGE_UP};

    public static void main(String[] args) throws Exception {
        Robot ro = new Robot();
        Random ra = new Random();

        while(true) {

            ro.delay(3000 + ra.nextInt(20000));

            if(ra.nextInt(100) < 30)
                continue;

            doSomething(ro, ra);

            if(ra.nextInt(100) < 25) {
                ro.delay(200 + ra.nextInt(600));
                doSomething(ro, ra);
            }
        }
    }

    private static void doSomething(Robot ro, Random ra) {
        int value = ra.nextInt(5);
        //System.out.println("Value:" + value + " at time:" + LocalTime.now());
        ro.delay(3000 + ra.nextInt(20000));

        switch(value) {


            case 0 :
                if(!(ra.nextInt(100) < 20)) {

                    int rd = ra.nextInt();
                    int x = 2200 + (Math.max(rd, 100)) % 780;
                    rd = ra.nextInt();
                    int y = 300 + (Math.max(rd, 100)) % 510;
                    //System.out.println("X:" + x + " :: Y:" + y);
                    ro.mouseMove(x, y);

                    //Point p = MouseInfo.getPointerInfo().getLocation();
                    //System.out.println("Point X:" + p.getX() + " ::Point Y:" + p.getY());
                }
                break;

            case 1:
                if(!(ra.nextInt(100) < 20)) {
                    ro.mousePress(BUTTON1_DOWN_MASK);
                    ro.delay(30 + ra.nextInt(100));
                    ro.mouseRelease(BUTTON1_DOWN_MASK);
                }
                break;

            case 2 :
                if(!(ra.nextInt(100) < 20)) {
                    ro.mouseWheel(value + ra.nextInt(10));
                }
                break;

            case 3 :
                if(!(ra.nextInt(100) < 20)) {
                    ro.mouseWheel(-(value + ra.nextInt(9)));
                }
                break;

            case 4 :
            default:
                doNothing(ro, ra);

        }

    }

    private static void doNothing(Robot ro, Random ra) {
        int key = SAFE_KEYS[ra.nextInt(SAFE_KEYS.length)];
        ro.keyPress(key);
        ro.delay(30 + ra.nextInt(70));
        ro.keyRelease(key);
    }

}
