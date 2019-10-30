package guimodule;

import processing.core.PApplet;
import processing.core.PImage;

public class SunChanging extends PApplet {
    public void setup() {
        size(500, 500);
        background(255);
        stroke(0);
        PImage img = loadImage("http://cseweb.ucsd.edu/~minnes/palmTrees.jpg");
        img.resize(0, height);
        image(img, 0, 0);
    }
    public void draw() {
        int[] color = getColor(second());
        fill (color[0], color[1], color[2]);
        ellipse(width / 4, height / 5, width / 4, height / 5);
    }

    private int[] getColor(float seconds) {
        int[] rgb = new int[3];
        float diffFrom30 = Math.abs(30 - seconds);

        float ratio = diffFrom30 / 30;
        rgb[0] = (int)(ratio * 255);
        rgb[1] = (int)(ratio * 255);

        //System.out.println("R" + rgb[0] + " G" + rgb[1] + " B" + rgb[2]);

        return rgb;
    }
}
