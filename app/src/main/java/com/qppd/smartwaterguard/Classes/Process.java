package com.qppd.smartwaterguard.Classes;

public class Process {

    private String date_started;
    private int day;

    public Process(){

    }

    public Process(String date_started, int day) {
        this.date_started = date_started;
        this.day = day;
    }

    public String getDate_started() {
        return date_started;
    }

    public void setDate_started(String date_started) {
        this.date_started = date_started;
    }

    public int getDay() {
        return day;
    }

    public void setDay(int day) {
        this.day = day;
    }
}
