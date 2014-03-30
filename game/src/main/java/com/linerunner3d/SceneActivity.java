package com.linerunner3d;

import android.app.Activity;
import android.content.Context;
import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.os.PowerManager;
import android.support.v4.view.GestureDetectorCompat;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.linerunner3d.core.Scene;
import com.threed.jpct.Logger;
import com.threed.jpct.util.AAConfigChooser;

/**
 * Created by simon on 30.03.14.
 */
public class SceneActivity extends Activity {

    private GLSurfaceView mGLView;
    private Scene mScene = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Logger.log("onCreate");
        super.onCreate(savedInstanceState);

        mGLView = new GLSurfaceView(getApplication());
        // Enable the OpenGL ES2.0 context
        mGLView.setEGLContextClientVersion(2);
        mGLView.setEGLConfigChooser(new AAConfigChooser(mGLView, false));

        mScene = new Scene(this);
        mGLView.setRenderer(mScene);

        setContentView(R.layout.activity_scene);
        FrameLayout layout = (FrameLayout) findViewById(android.R.id.content);
        layout.addView(mGLView);


        final GestureDetectorCompat gestureDetector = new GestureDetectorCompat(this, new GestureDetector.SimpleOnGestureListener() {

            @Override
            public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
                int threshold = 50;
                if (e1.getX() - e2.getX() > threshold) {
                    Toast.makeText(SceneActivity.this, "swipe left", Toast.LENGTH_SHORT).show();
                    mScene.rotate(0,-1);
                } else if (e2.getX() - e1.getX() > threshold) {
                    Toast.makeText(SceneActivity.this, "swipe right", Toast.LENGTH_SHORT).show();
                    mScene.rotate(0, 1);
                } else {
                    Toast.makeText(SceneActivity.this, "horizontal nichts", Toast.LENGTH_SHORT).show();
                }

                if (e1.getY() - e2.getY() > threshold) {
                    Toast.makeText(SceneActivity.this, "swipe hoch", Toast.LENGTH_SHORT).show();
                } else if (e2.getY() - e1.getY() > threshold) {
                    Toast.makeText(SceneActivity.this, "swipe runter", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(SceneActivity.this, "vertikal nichts", Toast.LENGTH_SHORT).show();
                }
                return true;
            }
        }
        );

        layout.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                gestureDetector.onTouchEvent(motionEvent);
                return true;
            }
        });

    }

    @Override
    protected void onPause() {
        Logger.log("onPause");
        super.onPause();
        mGLView.onPause();
    }

    @Override
    protected void onResume() {
        Logger.log("onResume");
        super.onResume();
        mGLView.onResume();
    }

    @Override
    protected void onStop() {
        Logger.log("onStop");
        super.onStop();
    }
}
