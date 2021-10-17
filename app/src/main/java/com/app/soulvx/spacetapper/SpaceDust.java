package com.app.soulvx.spacetapper;

import java.util.Random;

public class SpaceDust {
    private int x,y;
    private int speed;
    private int maxY,maxX,minY,minX;

    public SpaceDust(int screenX,int screenY) {
        maxX=screenX+70;
        maxY=screenY;
        minX=0;
        minY=0;
        Random generator = new Random();
        speed=generator.nextInt(20);
        x=generator.nextInt(maxX);
        y=generator.nextInt(maxY);
    }

    public void update(int playerSpeed) {
        x=x-playerSpeed-speed;
        if(x<0) {
            x=maxX+70;
            Random generator = new Random();
            speed=generator.nextInt(25);
            y=generator.nextInt(maxY);
        }
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }
}
