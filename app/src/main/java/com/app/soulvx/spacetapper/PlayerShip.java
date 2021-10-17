package com.app.soulvx.spacetapper;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Rect;

public class PlayerShip  {
    private final int GRAVITY=-15;
    private int maxY;
    private int minY;
    private int MIN_SPEED=1;
    private int MAX_SPEED=30;
    private Bitmap bitmap;
    private int x,y;
    private int speed=0;
    private boolean boosting;
    private Rect hitbox;
    private int shieldStrenght=3;

    public PlayerShip(Context context, int screenX, int screenY) {
        x=50;
        y=50;
        speed=1;
        bitmap= BitmapFactory.decodeResource(context.getResources(),R.drawable.ship);
        boosting=false;
        maxY=screenY -bitmap.getHeight();
        minY=0;
        hitbox = new Rect(x,y,bitmap.getWidth(),bitmap.getHeight());
    }

    public void update() {
        if(boosting)
            speed=speed+1;
        else
            speed=speed-2;
        if(speed>MAX_SPEED)
            speed=MAX_SPEED;
        if(speed<MIN_SPEED)
            speed=MIN_SPEED;
        y=y-(speed+GRAVITY);
        if(y<minY)
            y=minY;
        if(y>maxY)
            y=maxY;
        hitbox.left=x;
        hitbox.top=y;
        hitbox.right=x+bitmap.getWidth();
        hitbox.bottom=y+bitmap.getHeight();
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

    public int getY() {
        return y;
    }

    public int getSpeed() {
        return speed;
    }

    public void setBoosting() {
        boosting=true;
    }

    public void stopBoosting() {
        boosting=false;
    }

    public int getShieldStrenght() {
        return shieldStrenght;
    }

    public void setShieldStrenght(int shieldStrenght) {
        this.shieldStrenght = shieldStrenght;
    }

    public void reduceShieldStrenght() {
        shieldStrenght--;
    }

    public void setSpeed(int speed) {
        this.speed = speed;
    }
}
