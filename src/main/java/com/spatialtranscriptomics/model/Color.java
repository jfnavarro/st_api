package com.spatialtranscriptomics.model;

/**
 * The Color class is used to store the red, green, blue and alpha values of a color.
 * Valid values for each of the components are between 0 and 255.
 */
public class Color implements IColor {

    public int getR() {
        return r;
    }

    public void setR(int r) {
        this.r = r;
    }

    public int getG() {
        return g;
    }

    public void setG(int g) {
        this.g = g;
    }

    public int getB() {
        return b;
    }

    public void setB(int b) {
        this.b = b;
    }

    public int getA() {
        return a;
    }

    public void setA(int a) {
        this.a = a;
    }

    private int r;
    private int g;
    private int b;
    private int a;
}