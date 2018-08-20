package com.kterry.ptassessor;

import android.app.Application;
import android.support.v7.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.List;

public class Globals extends AppCompatActivity{
    private static Globals instance;
    // Global variable
    private List<Double> xAcc = new ArrayList<Double>(10);
    private List<Double> yAcc = new ArrayList<Double>(10);
    private List<Double> zAcc = new ArrayList<Double>(10);
    private List<Double> xRot = new ArrayList<Double>(10);
    private List<Double> yRot = new ArrayList<Double>(10);
    private List<Double> zRot = new ArrayList<Double>(10);
    private List<Integer> deltaT = new ArrayList<Integer>(10);
    private List<Integer> deltaT3 = new ArrayList<Integer>(10);
    private int cntr;
    private int rotCntr;
    // Restrict the constructor from being instantiated
    private Globals(){}

    public List<Double> getXAcc() {
        return xAcc;
    }

    public void setXAcc(List<Double> xAcc) {
        this.xAcc = xAcc;
    }

    public List<Double> getYAcc() {
        return yAcc;
    }

    public void setYAcc(List<Double> yAcc) {
        this.yAcc = yAcc;
    }

    List<Double> getZAcc() {
        return zAcc;
    }

    void setZAcc(List<Double> zAcc) {
        this.zAcc = zAcc;
    }

    public List<Double> getXRot() {
        return xRot;
    }

    public void setXRot(List<Double> xRot) {
        this.xRot = xRot;
    }

    public List<Double> getYRot() {
        return yRot;
    }

    public void setYRot(List<Double> yRot) {
        this.yRot = yRot;
    }

    public List<Double> getZRot() {
        return zRot;
    }

    public void setZRot(List<Double> zRot) {
        this.zRot = zRot;
    }


    public List<Integer> getDeltaT() {
        return deltaT;
    }

    public void setDeltaT(List<Integer> deltaT) {
        this.deltaT = deltaT;
    }

    public List<Integer> getDeltaT3() {
        return deltaT3;
    }

    public void setDeltaT3(List<Integer> deltaT3) {
        this.deltaT3 = deltaT3;
    }


    public int getCntr() {
        return cntr;
    }

    public void setCntr(int cntr) {
        this.cntr = cntr;
    }

    public int getRotCntr() {
        return rotCntr;
    }

    public void setRotCntr(int rotCntr) {
        this.rotCntr = rotCntr;
    }

    public static synchronized Globals getInstance(){
        if(instance==null){
            instance=new Globals();
        }
        return instance;
    }
}