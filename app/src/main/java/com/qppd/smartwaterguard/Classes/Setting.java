package com.qppd.smartwaterguard.Classes;

public class Setting {

    private float environmental_charge;
    private float maintenance_service_charge;
    private float government_tax;
    private float first10;
    private float next10;
    private float next20_1;
    private float next20_2;
    private float next20_3;
    private float next20_4;
    private float next50_1;
    private float next50_2;
    private float over200;

    public Setting(float environmental_charge, float maintenance_service_charge,
                   float government_tax, float first10, float next10, float next20_1,
                   float next20_2, float next20_3, float next20_4, float next50_1,
                   float next50_2, float over200) {
        this.environmental_charge = environmental_charge;
        this.maintenance_service_charge = maintenance_service_charge;
        this.government_tax = government_tax;
        this.first10 = first10;
        this.next10 = next10;
        this.next20_1 = next20_1;
        this.next20_2 = next20_2;
        this.next20_3 = next20_3;
        this.next20_4 = next20_4;
        this.next50_1 = next50_1;
        this.next50_2 = next50_2;
        this.over200 = over200;
    }

    private Setting(){

    }

    public float getEnvironmental_charge() {
        return environmental_charge;
    }

    public void setEnvironmental_charge(float environmental_charge) {
        this.environmental_charge = environmental_charge;
    }

    public float getMaintenance_service_charge() {
        return maintenance_service_charge;
    }

    public void setMaintenance_service_charge(float maintenance_service_charge) {
        this.maintenance_service_charge = maintenance_service_charge;
    }

    public float getGovernment_tax() {
        return government_tax;
    }

    public void setGovernment_tax(float government_tax) {
        this.government_tax = government_tax;
    }

    public float getFirst10() {
        return first10;
    }

    public void setFirst10(float first10) {
        this.first10 = first10;
    }

    public float getNext10() {
        return next10;
    }

    public void setNext10(float next10) {
        this.next10 = next10;
    }

    public float getNext20_1() {
        return next20_1;
    }

    public void setNext20_1(float next20_1) {
        this.next20_1 = next20_1;
    }

    public float getNext20_2() {
        return next20_2;
    }

    public void setNext20_2(float next20_2) {
        this.next20_2 = next20_2;
    }

    public float getNext20_3() {
        return next20_3;
    }

    public void setNext20_3(float next20_3) {
        this.next20_3 = next20_3;
    }

    public float getNext20_4() {
        return next20_4;
    }

    public void setNext20_4(float next20_4) {
        this.next20_4 = next20_4;
    }

    public float getNext50_1() {
        return next50_1;
    }

    public void setNext50_1(float next50_1) {
        this.next50_1 = next50_1;
    }

    public float getNext50_2() {
        return next50_2;
    }

    public void setNext50_2(float next50_2) {
        this.next50_2 = next50_2;
    }

    public float getOver200() {
        return over200;
    }

    public void setOver200(float over200) {
        this.over200 = over200;
    }
}
