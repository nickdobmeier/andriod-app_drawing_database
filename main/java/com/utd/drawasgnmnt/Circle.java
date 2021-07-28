// Written by Nicholas Dobmeier for CS 4301.001, for assignment 5, starting April 13, 2021.
//        NetID: njd170130

// This class is present to act as an information-expert to represent the attributes of each individual circle/point on-screen

package com.utd.drawasgnmnt;

import java.io.Serializable;

public class Circle implements Serializable             // information-expert class for representing the attributes of each circle/point that appears on-screen
{
    private float radius;
    //private Paint paintCircle;        // every circle does NOT need to save a Paint Object, as all we really care about is the color, so we can just save that as a smaller integer value to save memory
    private int color;
    private float Xcoord;
    private float Ycoord;

    public Circle(float radius, int color, float Xcoord, float Ycoord){
        this.radius = radius;
        //paintCircle = new Paint();
        //paintCircle.setColor(color);
        this.color = color;
        this.Xcoord = Xcoord;
        this.Ycoord = Ycoord;
    }

    public float getRadius() { return radius; }
    public int getColor() { return color; }
    public float getX() { return Xcoord; }
    public float getY() { return Ycoord; }
    //public Paint getPaint() { return paintCircle; }

    public void setRadius(float radius) { this.radius = radius; }
    public void setColor(int color) { this.color = color; }
    public void setX(float X) { this.Xcoord = X; }
    public void setY(float Y) { this.Ycoord = Y; }
}
