package scheduler;

import javafx.scene.paint.Color;

import java.util.ArrayList;

public class Process {
    public String name;
    public double arrival;
    public int burst, priority, remaining_burst;
    public static double average_waiting_time, average_turnaround_time;
    public static int quantum;
    public String color;
    private static ArrayList<Integer> taken_indeces = new ArrayList<>();
    private String[] ColorBasket = {
            "darkorange",
            "burlywood",
            "blue",
            "aqua",
            "cadetblue",
            "darkcyan",
            "deepskyblue",
            "lightblue",
            "lightgreen",
            "lime",
            "mediumspringgreen",
            "mediumpurple",
            "royalblue"
    };
    Process(Process process) {
        this.name = process.name;
        this.arrival = process.arrival;
        this.burst = process.burst;
        this.priority = process.priority;
        this.remaining_burst = process.remaining_burst;
        this.color = process.color;
    }
    Process(String name, double arrival, int burst) {
        this.name = name;
        this.arrival = arrival;
        this.burst = burst;
        this.remaining_burst = this.burst;
        this.priority = 0;

        int index = (int)(Math.random()*ColorBasket.length);
        while(!taken_indeces.isEmpty() && taken_indeces.contains(index)){
            index = (int)(Math.random()*ColorBasket.length);
        }
        this.color = ColorBasket[index];
        taken_indeces.add(index);
    }
    Process(String name, double arrival, int burst, int priority) {
        this.name = name;
        this.arrival = arrival;
        this.burst = burst;
        this.remaining_burst = this.burst;
        this.priority = priority;

        int index = (int)(Math.random()*ColorBasket.length);
        if(taken_indeces.size()==13) taken_indeces.clear();
        while(!taken_indeces.isEmpty() && taken_indeces.contains(index)){
            index = (int)(Math.random()*ColorBasket.length);
        }
        this.color = ColorBasket[index];
        taken_indeces.add(index);
    }
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public double getArrival() {
        return arrival;
    }
    public void setArrival(double arrival) {
        this.arrival = arrival;
    }
    public int getBurst() {
        return burst;
    }
    public void setBurst(int burst) {
        this.burst = burst;
    }
    public int getPriority() {
        return priority;
    }
    public void setPriority(int priority) {
        this.priority = priority;
    }
}