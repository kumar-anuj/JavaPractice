package com.practice.general.basic;

import java.awt.*;
import java.util.*;

import static java.awt.event.InputEvent.BUTTON1_DOWN_MASK;

public class Test2 {
    public static void main(String[] args) throws Exception {
        Robot ro = new Robot();
        Random ra = new Random();
        int count = 0;

        while(true) {
            int value = count%4;
            count++;
            if(count >1000)
                count = 0;

            int delay = 5000 + 2000*value;
            ro.delay(delay);

            switch(value) {

                default:
                case 0 :

                    int rd = ra.nextInt();
                    int x = 1700 + (rd < 0 ? 0 : rd) % 780;
                    rd = ra.nextInt();
                    int y = 300 + (rd < 0 ? 0 : rd) % 510;
                    //System.out.println("X:" + x + " :: Y:" + y);
                    ro.mouseMove(x, y);

                    //Point p = MouseInfo.getPointerInfo().getLocation();
                    //System.out.println("Point X:" + p.getX() + " ::Point Y:" + p.getY());
                    break;

                case 1:
                    ro.mousePress(BUTTON1_DOWN_MASK);
                    ro.mouseRelease(BUTTON1_DOWN_MASK);
                    break;

                case 2 :
                    ro.mouseWheel(value);
                    break;

                case 3 :
                    ro.mouseWheel(-value);
                    break;



            }

        }
    }

}
