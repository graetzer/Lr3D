package com.linerunner3d.core;

import android.content.Context;
import android.content.res.Resources;
import android.opengl.GLSurfaceView;

import com.linerunner3d.R;
import com.threed.jpct.Camera;
import com.threed.jpct.FrameBuffer;
import com.threed.jpct.Light;
import com.threed.jpct.Logger;
import com.threed.jpct.RGBColor;
import com.threed.jpct.SimpleVector;
import com.threed.jpct.Texture;
import com.threed.jpct.TextureManager;
import com.threed.jpct.World;
import com.threed.jpct.util.MemoryHelper;
import com.threed.jpct.util.SkyBox;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

/**
 * Created by simon on 29.03.14.
 */
public class Scene implements GLSurfaceView.Renderer {

    private Context mCtx;

    private boolean hasToCreateBuffer = false;
    private GL10 lastInstance = null;
    private int mWidth = 0;
    private int mHeight = 0;
    private int fps = 0;
    private int lfps = 0;

    private Texture font = null;

    private Light light;

    private FrameBuffer mFB = null;
    private SkyBox mSkybox = null;
    private World world = null;

    private Stickman mStickman;
    private Track mTrack;
    private Obstacles mObstacles;

    public Scene(Context ctx) {
        mCtx = ctx.getApplicationContext();
        Texture.defaultToMipmapping(true);
        Texture.defaultTo4bpp(true);
    }

    private void initResources() {

        Resources res = mCtx.getResources();
        Logger.log("Initializing game...");
        world = new World();

        TextureManager tm = TextureManager.getInstance();
        if(!tm.containsTexture("skybox")) {
            tm.addTexture("skybox", new Texture(res.openRawResource(R.drawable.skybox)));
            tm.addTexture("skybox2", new Texture(res.openRawResource(R.drawable.skybox_mirror)));
        }

        // ========== Skybox ============

        mSkybox = new SkyBox("skybox", "skybox2","skybox","skybox2","skybox","skybox", 30);

        // ========== Font for the rendering ============
        font = new Texture(res.openRawResource(R.raw.numbers));
        font.setMipmap(false);

        // ========== Add the Game Objects ============

        mStickman = Stickman.createStickman(mCtx, world);
        mTrack = Track.createTrack(mCtx, world);
        mObstacles = Obstacles.create(mCtx, world);

        /*
        //tm.addTexture("spaceship", new Texture(res.openRawResource(R.raw.ship)));
        Object3D[] list = Loader.load3DS(res.openRawResource(R.raw.mesh), 1.f);
        //  Loader.loadOBJ(res.openRawResource(R.raw.spaceship), res.openRawResource(R.raw.mtl_spaceship), 1f);
        mStickman = list[0];
        Matrix m = new Matrix();
        m.rotateX(-20);
        mStickman.setRotationMatrix(m);
        mStickman.build();

        world.addObject(mStickman);*/

        // ========== Lighting ============

        light = new Light(world);
        light.enable();

        light.setIntensity(80, 80, 80);
        light.setPosition(SimpleVector.create(-10, -50, -100));

        world.setAmbientLight(10, 10, 10);

        // ========== Camera config ============

        Camera cam = world.getCamera();
        cam.moveCamera(Camera.CAMERA_MOVEOUT, 15);
        cam.moveCamera(Camera.CAMERA_MOVEUP, 10);
        cam.lookAt(new SimpleVector(0,0,0));//mStickman.getTransformedCenter()

        MemoryHelper.compact();

        world.compileAllObjects();
    }

    public void onSurfaceCreated(GL10 gl, EGLConfig config) {

        Logger.log("onSurfaceCreated");
        initResources();
    }

    public void onSurfaceChanged(GL10 gl, int width, int height) {
        mWidth = width;
        mHeight = height;

        if (lastInstance != gl) {
            Logger.log("Setting buffer creation flag...");
            this.hasToCreateBuffer = true;
        }

        if (mFB == null) {
            Logger.log("Initializing buffer...");
            mFB = new FrameBuffer(mWidth, mHeight);
            //initResources();
        }

        lastInstance = gl;
    }

    public void onDrawFrame(GL10 gl) {
        if (this.hasToCreateBuffer) {
            Logger.log("Recreating buffer...");
            hasToCreateBuffer = false;
            mFB = new FrameBuffer(mWidth, mHeight);
        }

        updateWorld();

        mFB.clear();
        mSkybox.render(world, mFB);
        world.renderScene(mFB);
        world.draw(mFB);
        blitNumber(lfps, 5, 5);
        mFB.display();

    }

    private void blitNumber(int number, int x, int y) {
        if (font != null) {
            String sNum = Integer.toString(number);

            for (int i = 0; i < sNum.length(); i++) {
                char cNum = sNum.charAt(i);
                int iNum = cNum - 48;
                mFB.blit(font, iNum * 5, 0, x, y, 5, 9, 5, 9, 10, true, null);
                x += 5;
            }
        }
    }

    public void rotate(float x, float y) {
//        Matrix m = mStickman.getRotationMatrix();
//        m.rotateX(5*x);
//        m.rotateY(5*y);
        SimpleVector abc = new SimpleVector(Math.cos(frameTimeMilli/1000.f)*5, 0, Math.sin(frameTimeMilli/1000.f)*5);
        world.getCamera().setPosition(abc);
        world.getCamera().lookAt(SimpleVector.ORIGIN);
    }

    private static final int GRANULARITY_MILLI = 25;
    private long frameTimeMilli = System.currentTimeMillis();
    private long aggregatedTimeMilli = 0;
    private float speed = 1f;

    private void updateWorld() {
        long now = System.currentTimeMillis();
        long diff = (now - frameTimeMilli);
        aggregatedTimeMilli += diff;
        frameTimeMilli = now;

        float animateSeconds  = 0f;// Time to simulate
        while (aggregatedTimeMilli > GRANULARITY_MILLI) {
            aggregatedTimeMilli -= GRANULARITY_MILLI;
            animateSeconds += GRANULARITY_MILLI * 0.001f * speed;
            //cameraController.placeCamera();
        }

        if (diff >= 1000) {
            lfps = fps;
            fps = 0;
        }
        fps++;

        mStickman.update(now, animateSeconds);
        mTrack.update(now, animateSeconds);
        mObstacles.update(now, animateSeconds);


        double secs = now * 0.001;
        SimpleVector x = new SimpleVector(Math.cos(secs)*5, 0, Math.sin(secs)*5);
        world.getCamera().setPosition(x);
        world.getCamera().lookAt(SimpleVector.ORIGIN);
    }

}
