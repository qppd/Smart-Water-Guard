package com.qppd.smartwaterguard.Classes;

public class Device {

    private float cm3;
    private int rate;
    private int mL;


    public Device(float cm3, int rate, int mL) {
        this.cm3 = cm3;
        this.rate = rate;
        this.mL = mL;
    }

    public Device(float cm3, int rate) {
        this.cm3 = cm3;
        this.rate = rate;
    }

    public Device() {

    }

    public float getCm3() {
        return cm3;
    }

    public void setCm3(float cm3) {
        this.cm3 = cm3;
    }

    public int getRate() {
        return rate;
    }

    public void setRate(int rate) {
        this.rate = rate;
    }

    public int getmL() {
        return mL;
    }

    public void setmL(int mL) {
        this.mL = mL;
    }
}
