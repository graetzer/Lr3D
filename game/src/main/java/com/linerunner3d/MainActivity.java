package com.linerunner3d;

import android.app.Activity;
import android.content.Intent;
import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.support.v4.view.GestureDetectorCompat;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.linerunner3d.core.Scene;
import com.threed.jpct.Logger;


public class MainActivity extends Activity {

    private GLSurfaceView mGLView;
    private Scene mScene = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Logger.log("onCreate");
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        Button startButton = (Button)findViewById(R.id.button_start);
        startButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, SceneActivity.class);
                startActivity(intent);
            }
        });
    }
}