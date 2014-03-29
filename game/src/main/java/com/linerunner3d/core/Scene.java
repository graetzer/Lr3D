package com.linerunner3d.core;

import android.content.Context;
import android.content.res.Resources;
import android.opengl.GLSurfaceView;

import com.linerunner3d.R;
import com.threed.jpct.Camera;
import com.threed.jpct.FrameBuffer;
import com.threed.jpct.GLSLShader;
import com.threed.jpct.ITextureEffect;
import com.threed.jpct.Light;
import com.threed.jpct.Loader;
import com.threed.jpct.Logger;
import com.threed.jpct.Object3D;
import com.threed.jpct.Primitives;
import com.threed.jpct.RGBColor;
import com.threed.jpct.SimpleVector;
import com.threed.jpct.Texture;
import com.threed.jpct.TextureInfo;
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

    private Object3D plane;
    private Light light;

    private GLSLShader shader = null;

    private long time = System.currentTimeMillis();

    private FrameBuffer fb = null;
    private SkyBox mSkybox = null;
    private World world = null;
    private RGBColor back = new RGBColor(50, 50, 100);

    public Scene(Context ctx) {
        mCtx = ctx.getApplicationContext();
        Texture.defaultToMipmapping(true);
        Texture.defaultTo4bpp(true);
    }

    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        Logger.log("onSurfaceCreated");
    }

    public void onSurfaceChanged(GL10 gl, int width, int height) {
        mWidth = width;
        mHeight = height;

        if (lastInstance != gl) {
            Logger.log("Setting buffer creation flag...");
            this.hasToCreateBuffer = true;
        }

        if (fb == null) {
            Logger.log("Initializing buffer...");
            fb = new FrameBuffer(mWidth, mHeight);

            initResources();
        }

        lastInstance = gl;
    }

    public void onDrawFrame(GL10 gl) {
        if (this.hasToCreateBuffer) {
            Logger.log("Recreating buffer...");
            hasToCreateBuffer = false;
            fb = new FrameBuffer(mWidth, mHeight);
        }

        float scale = 1.0f;
        shader.setUniform("heightScale", scale);

        fb.clear(back);
        mSkybox.render(world, fb);
        world.renderScene(fb);
        world.draw(fb);
        blitNumber(lfps, 5, 5);
        fb.display();

        if (System.currentTimeMillis() - time >= 1000) {
            lfps = fps;
            fps = 0;
            time = System.currentTimeMillis();
        }
        fps++;
    }

    private void initResources() {

        Resources res = mCtx.getResources();
        Logger.log("Initializing game...");
        world = new World();

        TextureManager tm = TextureManager.getInstance();
        tm.addTexture("white", new Texture(res.openRawResource(R.raw.white)));
        mSkybox = new SkyBox("white", "white","white","white","white","white", 50);

        Texture face = new Texture(res.openRawResource(R.raw.face));
        Texture normals = new Texture(res.openRawResource(R.raw.face_norm), true);
        Texture heighty = new Texture(res.openRawResource(R.raw.face_height2));

        plane = Primitives.getPlane(1, 100);

        TexelGrabber grabber = new TexelGrabber();
        heighty.setEffect(grabber);
        heighty.applyEffect();
        int[] heighties = grabber.getAlpha();

        AlphaMerger setter = new AlphaMerger(heighties);
        normals.setEffect(setter);
        normals.applyEffect();

        // ========== Font for the rendering ============
        font = new Texture(res.openRawResource(R.raw.numbers));
        font.setMipmap(false);

        // ========== Add the plane ============
        tm.addTexture("face", face);
        tm.addTexture("normals", normals);

        TextureInfo ti = new TextureInfo(tm.getTextureID("face"));
        ti.add(tm.getTextureID("normals"), TextureInfo.MODE_BLEND);

        plane.setTexture(ti);

        shader = new GLSLShader(Loader.loadTextFile(res.openRawResource(R.raw.vertexshader_offset)),
                Loader.loadTextFile(res.openRawResource(R.raw.fragmentshader_offset)));
        plane.setShader(shader);
        plane.setSpecularLighting(true);
        shader.setStaticUniform("invRadius", 0.0003f);

        plane.build();
        plane.strip();

        world.addObject(plane);

        // ========== Lighting ============

        light = new Light(world);
        light.enable();

        light.setIntensity(60, 120, 50);
        light.setPosition(SimpleVector.create(-10, -50, -100));

        world.setAmbientLight(10, 10, 10);

        // ========== Camera config ============

        Camera cam = world.getCamera();
        cam.moveCamera(Camera.CAMERA_MOVEOUT, 70);
        cam.lookAt(plane.getTransformedCenter());

        MemoryHelper.compact();

        world.compileAllObjects();
    }

    private void blitNumber(int number, int x, int y) {
        if (font != null) {
            String sNum = Integer.toString(number);

            for (int i = 0; i < sNum.length(); i++) {
                char cNum = sNum.charAt(i);
                int iNum = cNum - 48;
                fb.blit(font, iNum * 5, 0, x, y, 5, 9, 5, 9, 10, true, null);
                x += 5;
            }
        }
    }

    /**
     * Merges the height map into the alpha channel of the normal map.
     *
     * @author EgonOlsen
     *
     */
    private static class AlphaMerger implements ITextureEffect {

        private int[] alpha = null;

        public AlphaMerger(int[] alpha) {
            this.alpha = alpha;
        }

        public void apply(int[] arg0, int[] arg1) {
            int end = arg1.length;
            for (int i = 0; i < end; i++) {
                arg0[i] = arg1[i] & 0x00ffffff | alpha[i];
            }
        }

        public boolean containsAlpha() {
            return true;
        }

        public void init(Texture arg0) {
            // TODO Auto-generated method stub
        }
    }

    /**
     * Extracts the alpha channel from a texture.
     *
     * @author EgonOlsen
     *
     */
    private static class TexelGrabber implements ITextureEffect {

        private int[] alpha = null;

        public void apply(int[] arg0, int[] arg1) {
            alpha = new int[arg1.length];
            int end = arg1.length;
            for (int i = 0; i < end; i++) {
                alpha[i] = (arg1[i] << 24);
            }
        }

        public int[] getAlpha() {
            return alpha;
        }

        public boolean containsAlpha() {
            return true;
        }

        public void init(Texture arg0) {
            // TODO Auto-generated method stub
        }
    }
}
