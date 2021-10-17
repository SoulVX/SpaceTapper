package com.app.soulvx.spacetapper;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.media.AudioAttributes;
import android.media.SoundPool;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import java.io.IOException;
import java.util.ArrayList;

public class TDView extends SurfaceView implements Runnable {
    volatile boolean playing;
    Thread gameThread=null;
    private PlayerShip player;
    public EnemyShip enemy1,enemy2,enemy3;
    public ArrayList<SpaceDust> dustList = new ArrayList<>();
    public ProgressBarIndicator progressBarIndicator;
    private Paint paint;
    private Canvas canvas;
    private SurfaceHolder ourHolder;
    private android.content.Context Context;
    private int screenX,screenY;
    private float distanceRemaining;
    private long timeTaken,timeStarted,fastestTime;
    private boolean gameEnded;
    private String endState;
    private boolean firstTimePlaying;
    private int initialDistance;
    private SharedPreferences prefs;
    private SharedPreferences.Editor editor;
    private boolean HUDappeared;
    private SoundPool soundPool;
    int start=-1;
    int bump=-1;
    int destroyed=-1;
    int win=-1;

    public TDView(Context context,Point resolution) {
        super(context);
        Context = context;
        soundPool = new SoundPool.Builder().setAudioAttributes(new AudioAttributes.Builder()
                .setUsage(AudioAttributes.USAGE_MEDIA).build()).setMaxStreams(10).build();

        try {
            start=soundPool.load(Context.getAssets().openFd("start.ogg"),0);
            bump=soundPool.load(Context.getAssets().openFd("bump.ogg"),0);
            destroyed=soundPool.load(Context.getAssets().openFd("destroyed.ogg"),0);
            win=soundPool.load(Context.getAssets().openFd("win.ogg"),0);
        } catch (IOException e) {
            e.printStackTrace();
        }

        screenX=resolution.x;
        screenY=resolution.y;
        ourHolder=getHolder();
        paint=new Paint();
        startGame();
        prefs=Context.getSharedPreferences("Data",Context.MODE_PRIVATE);
        editor=prefs.edit();
        fastestTime=prefs.getLong("fastestTime",0);
        firstTimePlaying=prefs.getBoolean("firstTimePlaying",true);
    }

    private void startGame() {
        player=new PlayerShip(Context,screenX,screenY);
        enemy1=new EnemyShip(Context,screenX,screenY);
        enemy2=new EnemyShip(Context,screenX,screenY);
        enemy3=new EnemyShip(Context,screenX,screenY);
        dustList.clear();
        int numDust=200;
        for(int i=1;i<=numDust;i++)
            dustList.add(new SpaceDust(screenX,screenY));
        progressBarIndicator=new ProgressBarIndicator(screenX-600,screenY - 60,Context);
        initialDistance=10000*2;
        distanceRemaining = initialDistance;
        timeStarted=System.currentTimeMillis();
        gameEnded=false;
        HUDappeared=false;
        soundPool.play(start,1,1,0,0,1);
    }

    @Override
    public void run() {
        while (playing) {
            update();
            draw();
            control();
        }
    }

    private void update() {
        if(!gameEnded) {
            boolean hitDetected=false;
            if(Rect.intersects(player.getHitbox(),enemy1.getHitbox())) {
                enemy1.setX(-500);
                hitDetected=true;
            }
            if(Rect.intersects(player.getHitbox(),enemy2.getHitbox())) {
                enemy2.setX(-500);
                hitDetected=true;
            }
            if(Rect.intersects(player.getHitbox(),enemy3.getHitbox())) {
                enemy3.setX(-500);
                hitDetected=true;
            }
            if(hitDetected) {
                player.reduceShieldStrenght();
                player.setSpeed(player.getSpeed()-100);
                if(player.getShieldStrenght()<0) {
                    gameEnded=true;
                    endState="lose";
                }
                else
                    soundPool.play(bump,1,1,0,0,1);
            }
            player.update();
            enemy1.update(player.getSpeed());
            enemy2.update(player.getSpeed());
            enemy3.update(player.getSpeed());
            progressBarIndicator.update(player.getSpeed());
            for(SpaceDust sd:dustList)
                sd.update(player.getSpeed());
            distanceRemaining=distanceRemaining-player.getSpeed();
            timeTaken=System.currentTimeMillis()-timeStarted;
        }
        if(distanceRemaining<0) {
            distanceRemaining=0;
            gameEnded=true;
            endState="win";
        }
    }

    private void draw() {
        if(ourHolder.getSurface().isValid() && playing) {
            if(!gameEnded) {
                canvas=ourHolder.lockCanvas();
                canvas.drawColor(Color.argb(255, 0, 0, 0));
                paint.setColor(Color.WHITE);
                for (SpaceDust sd : dustList)
                    canvas.drawPoint(sd.getX(), sd.getY(), paint);
                canvas.drawBitmap(player.getBitmap(), player.getX(), player.getY(), paint);
                canvas.drawBitmap(enemy1.getBitmap(), enemy1.getX(), enemy1.getY(), paint);
                canvas.drawBitmap(enemy2.getBitmap(), enemy2.getX(), enemy2.getY(), paint);
                canvas.drawBitmap(enemy3.getBitmap(), enemy3.getX(), enemy3.getY(), paint);
                canvas.drawBitmap(BitmapFactory.decodeResource(Context.getResources(), R.drawable.bar),screenX-600,screenY - 50,paint);
                canvas.drawBitmap(BitmapFactory.decodeResource(Context.getResources(), R.drawable.finish),screenX-100,screenY - 65,paint);
                canvas.drawBitmap(progressBarIndicator.getBitmap(),progressBarIndicator.getX(),progressBarIndicator.getY(),paint);
                paint.setTextAlign(Paint.Align.LEFT);
                paint.setTextSize(25);
                Typeface speed = Typeface.createFromAsset(Context.getApplicationContext().getAssets(), "myFont.ttf");
                Typeface speed2 = Typeface.create(speed,Typeface.BOLD);
                paint.setTypeface(speed2);
                if(player.getSpeed()<10)
                    canvas.drawBitmap(BitmapFactory.decodeResource(Context.getResources(), R.drawable.speed1),screenX-400,50,paint);
                else if(player.getSpeed()<20)
                    canvas.drawBitmap(BitmapFactory.decodeResource(Context.getResources(), R.drawable.speed2),screenX-400,50,paint);
                else
                    canvas.drawBitmap(BitmapFactory.decodeResource(Context.getResources(), R.drawable.speed3),screenX-400,50,paint);
                paint.setColor(Color.BLACK);
                paint.setTextSize(45);
                for(int i=0;i<player.getShieldStrenght();i++)
                    canvas.drawBitmap(BitmapFactory.decodeResource(Context.getResources(), R.drawable.shield), i*70, screenY - 150, paint);
                ourHolder.unlockCanvasAndPost(canvas);
            }
            else {
                switch (endState) {
                    case "win":
                        if(firstTimePlaying) {
                            soundPool.play(win,1,1,0,0,1);
                            canvas=ourHolder.lockCanvas();
                            canvas.drawColor(Color.argb(255, 0, 0, 0));
                            fastestTime=timeTaken;
                            firstTimePlaying=false;
                            editor.putLong("fastestTime",timeTaken);
                            editor.putBoolean("firstTimePlaying",false);
                            editor.commit();
                            paint.setColor(Color.WHITE);
                            for (SpaceDust sd : dustList)
                                canvas.drawPoint(sd.getX(), sd.getY(), paint);
                            paint.setColor(Color.BLACK);
                            canvas.drawBitmap(BitmapFactory.decodeResource(Context.getResources(),R.drawable.hudfirstwin),screenX/2-BitmapFactory.decodeResource(Context.getResources(),R.drawable.hudfirstwin).getWidth()/2,250,paint);
                            canvas.drawText(formatTimp(fastestTime),screenX/2 + 150,screenY/2 +31,paint);
                            HUDappeared=true;
                            ourHolder.unlockCanvasAndPost(canvas);
                        }
                        else if(timeTaken<fastestTime) {
                            soundPool.play(win,1,1,0,0,1);
                            canvas=ourHolder.lockCanvas();
                            canvas.drawColor(Color.argb(255, 0, 0, 0));
                            paint.setColor(Color.WHITE);
                            for (SpaceDust sd : dustList)
                                canvas.drawPoint(sd.getX(), sd.getY(), paint);
                            paint.setColor(Color.BLACK);
                            canvas.drawBitmap(BitmapFactory.decodeResource(Context.getResources(), R.drawable.hudrec), screenX / 2 - BitmapFactory.decodeResource(Context.getResources(), R.drawable.hudrec).getWidth() / 2, 250, paint);
                            canvas.drawText(formatTimp(fastestTime),screenX/2 + 175,screenY/2,paint);
                            canvas.drawText(formatTimp(timeTaken),screenX/2 + 175,screenY/2 + 103,paint);
                            HUDappeared=true;
                            fastestTime=timeTaken;
                            editor.putLong("fastestTime",timeTaken);
                            editor.commit();
                            ourHolder.unlockCanvasAndPost(canvas);
                        }
                        else if(!HUDappeared) {
                            soundPool.play(win,1,1,0,0,1);
                            canvas=ourHolder.lockCanvas();
                            canvas.drawColor(Color.argb(255, 0, 0, 0));
                            paint.setColor(Color.WHITE);
                            for (SpaceDust sd : dustList)
                                canvas.drawPoint(sd.getX(), sd.getY(), paint);
                            paint.setColor(Color.BLACK);
                            canvas.drawBitmap(BitmapFactory.decodeResource(Context.getResources(), R.drawable.hudwin), screenX / 2 - BitmapFactory.decodeResource(Context.getResources(), R.drawable.hudwin).getWidth() / 2, 250, paint);
                            canvas.drawText(formatTimp(timeTaken), screenX / 2 -90, screenY / 2 - 25, paint);
                            canvas.drawText(formatTimp(fastestTime), screenX / 2 + 60, screenY / 2 + 83, paint);
                            HUDappeared = true;
                            ourHolder.unlockCanvasAndPost(canvas);
                        }
                        break;
                    case "lose":
                        if(!HUDappeared) {
                            soundPool.play(destroyed,1,1,0,0,1);
                            canvas = ourHolder.lockCanvas();
                            canvas.drawColor(Color.argb(255, 0, 0, 0));
                            paint.setColor(Color.WHITE);
                            for (SpaceDust sd : dustList)
                                canvas.drawPoint(sd.getX(), sd.getY(), paint);
                            paint.setColor(Color.BLACK);
                            canvas.drawBitmap(BitmapFactory.decodeResource(Context.getResources(), R.drawable.hudlose), screenX / 2 - BitmapFactory.decodeResource(Context.getResources(), R.drawable.hudlose).getWidth() / 2, 250, paint);
                            canvas.drawText(formatTimp(timeTaken), screenX / 2 -90, screenY / 2 - 25, paint);
                            if(fastestTime!=0)
                                canvas.drawText(formatTimp(fastestTime), screenX / 2 + 60, screenY / 2 + 83, paint);
                            else
                                canvas.drawText("You have to win", screenX / 2 + 60, screenY / 2 + 83, paint);
                            HUDappeared = true;
                            ourHolder.unlockCanvasAndPost(canvas);
                            HUDappeared=true;
                        }
                        break;
                }
            }
        }
    }

    private void control() {
        try {
            gameThread.sleep(17);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void pause() {
        playing=false;
        canvas=ourHolder.lockCanvas();
        canvas.drawColor(Color.argb(255, 0, 0, 0));
        paint.setColor(Color.WHITE);
        for (SpaceDust sd : dustList)
            canvas.drawPoint(sd.getX(), sd.getY(), paint);
        canvas.drawBitmap(player.getBitmap(), player.getX(), player.getY(), paint);
        canvas.drawBitmap(enemy1.getBitmap(), enemy1.getX(), enemy1.getY(), paint);
        canvas.drawBitmap(enemy2.getBitmap(), enemy2.getX(), enemy2.getY(), paint);
        canvas.drawBitmap(enemy3.getBitmap(), enemy3.getX(), enemy3.getY(), paint);
        canvas.drawBitmap(BitmapFactory.decodeResource(Context.getResources(), R.drawable.hudpaused),screenX/2-BitmapFactory.decodeResource(Context.getResources(),R.drawable.hudpaused).getWidth()/2,250,paint);
        ourHolder.unlockCanvasAndPost(canvas);
        try {
            gameThread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void resume(boolean firstTimeEnteringGame) {
        if(firstTimeEnteringGame) {
            playing = true;
            gameThread = new Thread(this);
            gameThread.start();
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction() & MotionEvent.ACTION_MASK) {
            case MotionEvent.ACTION_UP:
                performClick();
                if(!gameEnded)
                    player.stopBoosting();
                break;
            case MotionEvent.ACTION_DOWN:
                if(!playing) {
                    resume(true);
                }
                else if(!gameEnded)
                    player.setBoosting();
                else
                    startGame();
                performClick();
                break;
        }
        return true;
    }

    @Override
    public boolean performClick() {
        return super.performClick();
    }

    public static String formatTimp(long timp) {
        long seconds=(timp) / 1000;
        long milisec = (timp) - (seconds*1000);
        String strM = "" + milisec;
        if(milisec<100)
            strM="0"+milisec;
        if(milisec<10)
            strM="0"+strM;
        String strTimp = "" + seconds+ "." + strM;
        return strTimp;
    }

}

