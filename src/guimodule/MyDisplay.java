package guimodule;

import processing.core.PApplet;

public class MyDisplay extends PApplet {
    public void setup() {
        size(200, 200);
        background(200, 200, 200);
    }
    public void draw() {
        fill(255, 255, 0);
        ellipse(100, 100, 190, 190);
        fill(0,0,0);
        ellipse(60, 60, 15, 20);
        ellipse(140, 60, 15, 20);
        arc(100, 140, 50, 50, 0, PI);
    }
}
