package com.spatialtranscriptomics.model;

/**
 * The IColor interface is used to represent the red, green, blue and alpha values of a color.
 * Valid values for each of the components are between 0 and 255.
 */
public interface IColor {

    int getR();

    void setR(int r);

    int getG();

    void setG(int g);

    int getB();

    void setB(int b);

    int getA();

    void setA(int a);
}
