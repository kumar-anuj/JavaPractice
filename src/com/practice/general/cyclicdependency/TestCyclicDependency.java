package com.practice.general.cyclicdependency;

public class TestCyclicDependency {
    public static void main(String[] args) {
        Building building = new Building();
        Lift lift = new Lift();

        System.out.println(building.getFloor());
        System.out.println(lift.getSpeed());
    }
}
