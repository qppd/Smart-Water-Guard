package com.qppd.smartwaterguard.Classes;

public class History {

    private float cm3;
    private int mL;

    public History(float cm3, int mL) {
        this.cm3 = cm3;
        this.mL = mL;
    }

    public History(){

    }

    public float getCm3() {
        return cm3;
    }

    public void setCm3(float cm3) {
        this.cm3 = cm3;
    }

    public int getmL() {
        return mL;
    }

    public void setmL(int mL) {
        this.mL = mL;
    }
}
