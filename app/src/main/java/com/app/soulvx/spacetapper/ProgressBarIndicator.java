package com.app.soulvx.spacetapper;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

public class ProgressBarIndicator {
    private float x,y;
    private Bitmap bitmap;
    private float indice=0.048f; //la 10000

    public ProgressBarIndicator(int x,int y,Context context) {
        this.x=x;
        this.y=y;
        bitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.progressicon);
    }

    public void update(int playerSpeed) {
        x=x+indice/2*playerSpeed;
    }

    public float getX() {
        return x;
    }

    public float getY() {
        return y;
    }

    public Bitmap getBitmap() {
        return bitmap;
    }
}
