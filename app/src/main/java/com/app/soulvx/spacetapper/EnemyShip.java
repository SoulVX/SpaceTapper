package com.app.soulvx.spacetapper;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Rect;

import java.util.Random;

public class EnemyShip {
    private Bitmap bitmap;
    private int x,y;
    private int speed=1;
    private int maxX,minX,maxY,minY;
    Rect hitbox;
    Context context;

    public EnemyShip(Context context,int screenX,int screenY) {
        Random generator = new Random();
        this.context=context;
        int wichBitmap = generator.nextInt(3);
        switch (wichBitmap) {
            case 0:
                bitmap= BitmapFactory.decodeResource(context.getResources(),R.drawable.enemy);
                break;
            case 1:
                bitmap= BitmapFactory.decodeResource(context.getResources(),R.drawable.enemy2);
                break;
            case 2:
                bitmap= BitmapFactory.decodeResource(context.getResources(),R.drawable.enemy3);
                break;
        }
        maxX=screenX+70;
        maxY=screenY;
        minX=0;
        minY=0;
        speed = generator.nextInt(6)+20;
        x=screenX;
        y=generator.nextInt(maxY - bitmap.getHeight());
        hitbox = new Rect(x,y,bitmap.getWidth(),bitmap.getHeight());
    }

    public Rect getHitbox() {
        return hitbox;
    }

    public Bitmap getBitmap() {
        return bitmap;
    }

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return y;
    }

    public void update(int playerSpeed) {
        x=x-playerSpeed-speed;
        if(x<minX-bitmap.getWidth()) {
            Random generator = new Random();
            speed = generator.nextInt(10) +20;
            x=maxX+70;
            y=generator.nextInt(maxY - bitmap.getHeight());
            int wichBitmap = generator.nextInt(3);
            switch (wichBitmap) {
                case 0:
                    bitmap= BitmapFactory.decodeResource(context.getResources(),R.drawable.enemy);
                    break;
                case 1:
                    bitmap= BitmapFactory.decodeResource(context.getResources(),R.drawable.enemy2);
                    break;
                case 2:
                    bitmap= BitmapFactory.decodeResource(context.getResources(),R.drawable.enemy3);
                    break;
            }
        }
        hitbox.left=x;
        hitbox.top=y;
        hitbox.right=x+bitmap.getWidth();
        hitbox.bottom=y+bitmap.getHeight();
    }
}
