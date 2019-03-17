package com.practice.designpatterns.structural.compositepattern;

public class CompositePatternDemo {
    public static void main(String[] args) {
        CompositeEmployee CEO = new CompositeEmployee("Anuj", "CEO", 100000);

        CompositeEmployee headSales = new CompositeEmployee("Manoj", "Head Sales", 50000);
        CompositeEmployee headMarketing = new CompositeEmployee("Deepak", "Head Marketing", 50000);

        CEO.add(headSales);
        CEO.add(headMarketing);

        CompositeEmployee salesOfficer1 = new CompositeEmployee("Ankur", "Sales", 20000);
        CompositeEmployee salesOfficer2 = new CompositeEmployee("Pankaj", "Sales", 20000);

        CompositeEmployee clerk1 = new CompositeEmployee("Saurabh", "Marketing", 20000);
        CompositeEmployee clerk2 = new CompositeEmployee("Nitin", "Marketing", 20000);

        headSales.add(salesOfficer1);
        headSales.add(salesOfficer2);

        headMarketing.add(clerk1);
        headMarketing.add(clerk2);

        System.out.println(CEO);
        CEO.getSubordinates().forEach( t -> {
            System.out.println(t);
            t.getSubordinates().forEach(p -> {
                System.out.println(p);
            });
        });
    }
}
