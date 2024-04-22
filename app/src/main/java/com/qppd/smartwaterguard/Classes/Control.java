package com.qppd.smartwaterguard.Classes;

public class Control {

    private boolean led;
    private boolean operate;

    public Control(boolean led, boolean operate) {
        this.led = led;
        this.operate = operate;
    }

    public Control(){

    }

    public boolean isLed() {
        return led;
    }

    public void setLed(boolean led) {
        this.led = led;
    }

    public boolean isOperate() {
        return operate;
    }

    public void setOperate(boolean operate) {
        this.operate = operate;
    }
}
