package com.linerunner3d;

import android.app.Activity;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.v4.view.GestureDetectorCompat;
import android.view.MotionEvent;
import android.view.View;
import android.widget.RelativeLayout;


public class MainActivity extends Activity {


    private boolean isTouch = false;
    private RelativeLayout mRL;
    private GestureDetectorCompat gestureDetector = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        gestureDetector = new GestureDetectorCompat(this,new GestureInputDetector(this));


        setContentView(R.layout.activity_main);
        mRL = (RelativeLayout) findViewById(R.id.mRL);
        mRL.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                gestureDetector.onTouchEvent(event);
                return true;
            }
        });

    }
}