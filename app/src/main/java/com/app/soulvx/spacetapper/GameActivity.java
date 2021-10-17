package com.app.soulvx.spacetapper;

import android.app.Activity;
import android.graphics.Point;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.KeyEvent;

public class GameActivity extends Activity {
    private TDView gameView;
    private boolean firstTimeEnteringGame;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Point size=new Point();
        getWindowManager().getDefaultDisplay().getSize(size);
        gameView = new TDView(this,size);
        setContentView(gameView);
        firstTimeEnteringGame=true;
    }

    @Override
    protected void onPause() {
        super.onPause();
        gameView.pause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        gameView.resume(firstTimeEnteringGame);
        firstTimeEnteringGame=false;
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(keyCode == KeyEvent.KEYCODE_BACK) {
            gameView.pause();
            return true;
        }
        return false;
    }
}
