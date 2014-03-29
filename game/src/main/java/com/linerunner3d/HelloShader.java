package com.linerunner3d;

import android.app.Activity;
import android.opengl.GLSurfaceView;
import android.os.Bundle;
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
public class HelloShader extends Activity implements OnScaleGestureListener {

	private ScaleGestureDetector gestureDec = null;

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

		gestureDec = new ScaleGestureDetector(this.getApplicationContext(), this);
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

	public boolean onTouchEvent(MotionEvent me) {

		gestureDec.onTouchEvent(me);

		if (me.getAction() == MotionEvent.ACTION_DOWN) {
			xpos = me.getX();
			ypos = me.getY();
			return true;
		}

		if (me.getAction() == MotionEvent.ACTION_UP) {
			xpos = -1;
			ypos = -1;
			return true;
		}

		if (me.getAction() == MotionEvent.ACTION_MOVE) {
			float xd = me.getX() - xpos;
			float yd = me.getY() - ypos;

			xpos = me.getX();
			ypos = me.getY();

			return true;
		}

		try {
			Thread.sleep(15);
		} catch (Exception e) {
			// No need for this...
		}

		return super.onTouchEvent(me);
	}

	public boolean onScale(ScaleGestureDetector detector) {
		float div = detector.getCurrentSpan() - detector.getPreviousSpan();
		div /= 5000;

		scale += div;

		if (scale > 0.063f) {
			scale = 0.063f;
		}
		if (scale < 0) {
			scale = 0;
		}

		return true;
	}

	public boolean onScaleBegin(ScaleGestureDetector detector) {
		// TODO Auto-generated method stub
		return true;
	}

	public void onScaleEnd(ScaleGestureDetector detector) {
		// TODO Auto-generated method stub
	}
}
