package com.practice.general.cyclicdependency;

public class Building {
    private Lift lift;
    private int floor;

    public Building() {
        System.out.println("Building Constructor");
        lift = new Lift();
        setFloor(15);
    }

    public void setFloor(int floor) {
        this.floor = floor;
    }

    public int getFloor() {
        return floor;
    }


}
