package com.practice.general.cyclicdependency;

public class Lift {
    private Building building;
    private int speed;

    public Lift(){
        System.out.println("Lift Constructor");
        building = new Building();
        setSpeed();
    }

    public void setSpeed() {
        if(building.getFloor() > 20)
            this.speed = 10;
        else
            this.speed = 5;
    }

    public int getSpeed() {
        return speed;
    }
}
