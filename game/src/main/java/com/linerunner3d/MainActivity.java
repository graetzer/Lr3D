package com.linerunner3d;

import android.app.Activity;
<<<<<<< HEAD
=======
import android.content.Intent;
>>>>>>> FETCH_HEAD
import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.support.v4.view.GestureDetectorCompat;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
<<<<<<< HEAD
import android.widget.RelativeLayout;
import android.view.WindowManager;

import com.linerunner3d.core.Scene;
import com.threed.jpct.Logger;
=======
import android.view.WindowManager;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.Toast;
>>>>>>> FETCH_HEAD

import com.linerunner3d.core.Scene;
import com.threed.jpct.Logger;


<<<<<<< HEAD
    private GLSurfaceView mGLView;
    private Scene renderer = null;

    private GestureDetectorCompat gestureDetector = null;
=======
public class MainActivity extends Activity {

    private GLSurfaceView mGLView;
    private Scene mScene = null;
>>>>>>> FETCH_HEAD

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Logger.log("onCreate");
        super.onCreate(savedInstanceState);

<<<<<<< HEAD
        mGLView = new GLSurfaceView(getApplication());

        // Enable the OpenGL ES2.0 context
        mGLView.setEGLContextClientVersion(2);

        renderer = new Scene(this);
        mGLView.setRenderer(renderer);

        this.getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        setContentView(mGLView);
        gestureDetector = new GestureDetectorCompat(this.getApplicationContext(),
                                                    new GestureInputDetector(this.getApplication()));
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
=======
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        setContentView(R.layout.activity_main);

        Button startButton = (Button)findViewById(R.id.button_start);
        startButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, SceneActivity.class);
                startActivity(intent);
            }
        });
>>>>>>> FETCH_HEAD
    }
}