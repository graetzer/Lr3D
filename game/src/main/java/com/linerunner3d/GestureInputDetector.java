package com.linerunner3d;

import android.app.Activity;
import android.content.Context;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;

/**
 * Die Klasse Input beherbergt alle nötigen Funktionen und Variablen und die Eingabe des Benutzers
 * zu erkennen und an die LogicEngine weiterzugeben
 * Created by wasc on 2014-03-29.
 */
public class GestureInputDetector extends GestureDetector.SimpleOnGestureListener {


    private float currentAxisY=0;
    private float currentAxisX=0;
    private float lastAxisY=0;
    private float lastAxisX=0;
    private Context _context;

    /*
     * Initialisiert den Input
     */
    public GestureInputDetector(Context ctx){
        _context = ctx.getApplicationContext();
    }

    /*
     * Verarbeitet Eingaben des Benutzers
     */
    @Override
    public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
        int threshold = 50;
        if(e1.getX()-e2.getX() > threshold){
            Toast.makeText(_context,"swipe left",Toast.LENGTH_SHORT).show();
        }else if(e2.getX()-e1.getX()>threshold){
            Toast.makeText(_context,"swipe right",Toast.LENGTH_SHORT).show();
        }else{
            Toast.makeText(_context,"horizontal nichts",Toast.LENGTH_SHORT).show();
        }

        if(e1.getY()-e2.getY()>threshold){
            Toast.makeText(_context,"swipe hoch",Toast.LENGTH_SHORT).show();
        }else if(e2.getY()-e1.getY()>threshold){
            Toast.makeText(_context,"swipe runter",Toast.LENGTH_SHORT).show();
        }else{
            Toast.makeText(_context,"vertikal nichts",Toast.LENGTH_SHORT).show();
        }
        return super.onFling(e1,e2,velocityX,velocityY);
    }
    /*
     * Muss true zurückliefern, damit onFling feuern kann
     */
    @Override
    public boolean onDown(MotionEvent e) {
        return true;
    }
}
