package com.linerunner3d;

import android.app.Activity;
import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.support.v4.view.GestureDetectorCompat;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.ScaleGestureDetector.OnScaleGestureListener;

import com.linerunner3d.core.Scene;
import com.threed.jpct.Logger;

import java.lang.reflect.Field;

/**
 * 
 * 
 * @author EgonOlsen
 * 
 */
public class HelloShader extends Activity {

	private GestureDetectorCompat gestureDetector = null;

	private GLSurfaceView mGLView;
	private Scene renderer = null;

	private float xpos = -1;
	private float ypos = -1;

	private float scale = 0.05f;

	protected void onCreate(Bundle savedInstanceState) {
		Logger.log("onCreate");
		Logger.setLogLevel(Logger.LL_DEBUG);

		super.onCreate(savedInstanceState);
		mGLView = new GLSurfaceView(getApplication());

		// Enable the OpenGL ES2.0 context
		mGLView.setEGLContextClientVersion(2);

		renderer = new Scene(this);
		mGLView.setRenderer(renderer);
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
	}

	public boolean onTouchEvent(MotionEvent event) {
        return gestureDetector.onTouchEvent(event);
    }
}
