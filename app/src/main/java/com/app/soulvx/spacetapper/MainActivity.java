package com.app.soulvx.spacetapper;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * Created by Angelica on 05.12.2017.
 */

public class MainActivity extends Activity implements View.OnClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final Button buttonPlay = (Button) findViewById(R.id.buttonPlay);
        buttonPlay.setOnClickListener(this);
        final TextView highScore=(TextView) findViewById(R.id.textHighScore);
        Typeface speed = Typeface.createFromAsset(this.getApplicationContext().getAssets(), "myFont.ttf");
        Typeface speed2 = Typeface.create(speed,Typeface.BOLD);
        highScore.setTypeface(speed2);
        highScore.setTextColor(Color.argb(200,254,208,0));
        highScore.setTextSize(15);
        if(this.getSharedPreferences("Data",this.MODE_PRIVATE).getLong("fastestTime",0)!=0)
            highScore.setText(TDView.formatTimp(this.getSharedPreferences("Data",this.MODE_PRIVATE).getLong("fastestTime",0)));
        else
            highScore.setText("None, yet");

        final ImageView shipView = findViewById(R.id.shipView);
        final ObjectAnimator shipAnim1 = ObjectAnimator.ofFloat(shipView,"y",550,800).setDuration(3000);
        final ObjectAnimator shipAnim2 = ObjectAnimator.ofFloat(shipView,"y",800,550).setDuration(3000);
        shipAnim1.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animator) {

            }

            @Override
            public void onAnimationEnd(Animator animator) {
                shipAnim2.start();
            }

            @Override
            public void onAnimationCancel(Animator animator) {

            }

            @Override
            public void onAnimationRepeat(Animator animator) {

            }
        });
        shipAnim2.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animator) {

            }

            @Override
            public void onAnimationEnd(Animator animator) {
                shipAnim1.start();
            }

            @Override
            public void onAnimationCancel(Animator animator) {

            }

            @Override
            public void onAnimationRepeat(Animator animator) {

            }
        });
        shipAnim1.start();
    }

    @Override
    public void onClick(View view) {
        Intent i = new Intent(this,GameActivity.class);
        startActivity(i);
        finish();
    }
}
