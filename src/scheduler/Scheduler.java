package scheduler;

import javafx.collections.ObservableList;

import java.util.*;

class Scheduler {
    private String algorithm;
    private int TOTAL_TIME;
    private ArrayList<Process> process_list;
    private Queue<Process> ready_queue;
    private ArrayList<Process> ready_list;
    ArrayList<ChartSegment> chart;

    Scheduler(String algorithm, ObservableList<Process> process_list){
        this.algorithm = algorithm;
        this.process_list = new ArrayList<>();
        int i = 0;
        for(Process process : process_list){
            this.process_list.add(i,new Process(process));
            i++;
        }
        sort_by_arrival();
        schedule();
        modify_chart();
    }
    Scheduler(String algorithm, ObservableList<Process> process_list, int quantum){
        this.algorithm = algorithm;
        this.process_list = new ArrayList<>();
        int i = 0;
        for(Process process : process_list){
            this.process_list.add(i,new Process(process));
            i++;
        }
        Process.quantum = quantum;
        sort_by_arrival();
        schedule();
        modify_chart();
    }
    private void sort_by_arrival(){
        for(int i = 0 ; i < process_list.size() ; i++){
            boolean swapped = false;
            for(int j = 0; j < process_list.size()-1 ; j++){
                if(process_list.get(j).arrival > process_list.get(j+1).arrival){
                    Process temp = process_list.get(j);
                    process_list.set(j,process_list.get(j+1));
                    process_list.set(j+1,temp);
                    swapped = true;
                }
            }
            if(! swapped) break;
        }
    }
    private void sort_by_priority(){
        for(int i = 0 ; i < ready_list.size() ; i++){
            boolean swapped = false;
            for(int j = 0; j < ready_list.size()-1 ; j++){
                if(ready_list.get(j).priority > ready_list.get(j+1).priority){
                    Process temp = ready_list.get(j);
                    ready_list.set(j,ready_list.get(j+1));
                    ready_list.set(j+1,temp);
                    swapped = true;
                }
            }
            if(! swapped) break;
        }
    }
    private void sort_by_RT(){
        for(int i = 0 ; i < ready_list.size() ; i++){
            boolean swapped = false;
            for(int j = 0; j < ready_list.size()-1 ; j++){
                if(ready_list.get(j).remaining_burst > ready_list.get(j+1).remaining_burst){
                    Process temp = ready_list.get(j);
                    ready_list.set(j,ready_list.get(j+1));
                    ready_list.set(j+1,temp);
                    swapped = true;
                }
            }
            if(! swapped) break;
        }
    }
    private void schedule(){
        chart = new ArrayList<>();
        for(Process it: process_list) {
            switch (algorithm) {
                case "SJF":
                    it.priority = it.burst;
                    break;
                case "FCFS":
                    Process.quantum = Integer.MAX_VALUE;
                    break;
            }
        }
        switch (algorithm) {
            case "FCFS":
            case "RR":
                RR();
                break;
            case "priority":
            case "SJF":
                priority();
                break;
            case "priority_preemptive":
                priority_pre();
                break;
            case "SRTF":
                SRTF();
                break;
        }
    }

    private void RR(){
        ready_queue = new LinkedList<>();
        int timer = 0;
        double TotalWaiting = 0,TotalTurnaround=0;
        int sum = process_list.size();
        while( !ready_queue.isEmpty() || !process_list.isEmpty()){
            enqueue_arrived_processes(timer);
            if(ready_queue.isEmpty()){
                chart.add(new ChartSegment("idle",timer,1, "lightgray"));
                timer++;
            }
            else {
                int start = timer, chunk = Process.quantum;
                while (ready_queue.peek().remaining_burst != 0 && chunk != 0) {
                    chunk--;
                    ready_queue.peek().remaining_burst--;
                    timer++;
                }
                chart.add(new ChartSegment(ready_queue.peek().name, start, timer - start,ready_queue.peek().color));
                if (ready_queue.peek().remaining_burst != 0) {
                    enqueue_arrived_processes(timer);
                    ready_queue.add(ready_queue.poll());
                }
                else {
                    TotalWaiting += timer - ready_queue.peek().arrival - ready_queue.peek().burst;
                    TotalTurnaround += timer - ready_queue.peek().arrival;
                    ready_queue.poll();
                }
            }
        }
        TOTAL_TIME = timer;
        Process.average_waiting_time = TotalWaiting / sum;
        Process.average_turnaround_time = TotalTurnaround /sum;
    }
    private void priority(){
        ready_list = new ArrayList<>();
        int timer = 0;
        double TotalWaiting = 0,TotalTurnaround=0;
        int sum = process_list.size();
        while( !ready_list.isEmpty() || !process_list.isEmpty()){
            for(int i = 0 ; i < process_list.size() ; i++){
                if(timer >= process_list.get(i).arrival){
                    ready_list.add(process_list.get(i));
                    process_list.remove(i);
                    i--;
                }
            }
            if(ready_list.isEmpty()){
                chart.add(new ChartSegment("idle",timer,1,"lightgray"));
                timer++;
            }
            else {
                sort_by_priority();
                int start = timer;
                while (ready_list.get(0).remaining_burst != 0) {
                    ready_list.get(0).remaining_burst--;
                    timer++;
                }
                chart.add(new ChartSegment(ready_list.get(0).name, start, timer - start,ready_list.get(0).color));
                TotalWaiting += timer - ready_list.get(0).arrival - ready_list.get(0).burst;
                TotalTurnaround += timer - ready_list.get(0).arrival;
                ready_list.remove(0);
            }
        }
        TOTAL_TIME = timer;
        Process.average_waiting_time = TotalWaiting / sum;
        Process.average_turnaround_time = TotalTurnaround /sum;
    }
    private void priority_pre(){
        ready_list = new ArrayList<>();
        Process running;
        int timer = 0;
        double TotalWaiting = 0,TotalTurnaround=0;
        int sum = process_list.size();
        while( !ready_list.isEmpty() || !process_list.isEmpty()){
            for(int i = 0 ; i < process_list.size() ; i++){
                if(timer >= process_list.get(i).arrival){
                    ready_list.add(process_list.get(i));
                    process_list.remove(i);
                    i--;
                }
            }
            sort_by_priority();
            if(ready_list.isEmpty()){
                chart.add(new ChartSegment("idle",timer,1,"lightgray"));
                timer++;
            }
            else {
                running = new Process(ready_list.get(0));
                ready_list.remove(0);
                int start = timer;
                while (running.remaining_burst != 0) {
                    running.remaining_burst--;
                    timer++;
                    for(int i = 0 ; i < process_list.size() ; i++){
                        if(timer >= process_list.get(i).arrival){
                            ready_list.add(process_list.get(i));
                            process_list.remove(i);
                            i--;
                        }
                    }
                    if(! ready_list.isEmpty()){
                        sort_by_priority();
                        if(ready_list.get(0).priority < running.priority)break;
                    }
                }
                chart.add(new ChartSegment(running.name, start, timer - start,running.color));
                if (running.remaining_burst != 0) ready_list.add(running);
                else {
                    TotalWaiting += timer - running.arrival - running.burst;
                    TotalTurnaround += timer - running.arrival;
                }
            }
        }
        TOTAL_TIME = timer;
        Process.average_waiting_time = TotalWaiting / sum;
        Process.average_turnaround_time = TotalTurnaround /sum;
    }
    private void SRTF(){
        ready_list = new ArrayList<>();
        Process running;
        int timer = 0;
        double TotalWaiting = 0,TotalTurnaround=0;
        int sum = process_list.size();
        while( !ready_list.isEmpty() || !process_list.isEmpty()){
            for(int i = 0 ; i < process_list.size() ; i++){
                if(timer >= process_list.get(i).arrival){
                    ready_list.add(process_list.get(i));
                    process_list.remove(i);
                    i--;
                }
            }
            sort_by_RT();
            if(ready_list.isEmpty()){
                chart.add(new ChartSegment("idle",timer,1,"lightgray"));
                timer++;
            }
            else {
                running = new Process(ready_list.get(0));
                ready_list.remove(0);
                int start = timer;
                while (running.remaining_burst != 0) {
                    running.remaining_burst--;
                    timer++;
                    for(int i = 0 ; i < process_list.size() ; i++){
                        if(timer >= process_list.get(i).arrival){
                            ready_list.add(process_list.get(i));
                            process_list.remove(i);
                            i--;
                        }
                    }
                    if(! ready_list.isEmpty()){
                        sort_by_RT();
                        if(ready_list.get(0).remaining_burst < running.remaining_burst)break;
                    }
                }
                chart.add(new ChartSegment(running.name, start, timer - start,running.color));
                if (running.remaining_burst != 0) ready_list.add(running);
                else {
                    TotalWaiting += timer - running.arrival - running.burst;
                    TotalTurnaround += timer - running.arrival;
                }
            }
        }
        TOTAL_TIME = timer;
        Process.average_waiting_time = TotalWaiting / sum;
        Process.average_turnaround_time = TotalTurnaround /sum;
    }

    private void enqueue_arrived_processes(int time){
        for(int i = 0 ; i < process_list.size() ; i++){
            if(time >= process_list.get(i).arrival){
                ready_queue.add(process_list.get(i));
                process_list.remove(i);
                i--;
            }
        }
    }
    private void modify_chart(){
        for (int i = 0 ; i<chart.size()-1; i++){
            if(chart.get(i).name.equals(chart.get(i + 1).name)){
                ChartSegment temp = new ChartSegment(chart.get(i));
                temp.length = chart.get(i).length + chart.get(i+1).length;
                chart.remove(i);
                chart.remove(i);
                chart.add(i,temp);
                i--;
            }
        }
    }
    public int getTotalTime(){ return this.TOTAL_TIME; }
}