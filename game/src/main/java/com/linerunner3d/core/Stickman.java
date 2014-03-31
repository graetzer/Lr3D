package com.linerunner3d.core;

import android.content.Context;
import android.content.res.Resources;

import com.linerunner3d.R;
import com.threed.jpct.Animation;
import com.threed.jpct.Loader;
import com.threed.jpct.Logger;
import com.threed.jpct.Matrix;
import com.threed.jpct.Object3D;
import com.threed.jpct.SimpleVector;
import com.threed.jpct.World;

import java.io.InputStream;

/**
 * Created by simon on 29.03.14.
 */
public class Stickman {

    private Object3D mObject;

    public static Stickman createStickman(Context ctx, World world) {
        Stickman man = new Stickman();
        Resources res = ctx.getResources();

        man.mObject = load3DSModel(res.openRawResource(R.raw.stickman_pos1), 0.2f);

        Animation anim = new Animation(2);
        anim.createSubSequence("running");

        anim.addKeyFrame(man.mObject.getMesh());
        anim.addKeyFrame(load3DSModel(res.openRawResource(R.raw.stickman_pos2), 0.2f).getMesh());

        man.mObject.setAnimationSequence(anim);
        world.addObject(man.mObject);

        man.mObject.rotateY((float)-Math.PI/2);
        man.mObject.translate(0,-0.8f,0);

        return man;
    }

    private int mAnimationSeq = 1;

    public void run() {
        mAnimationSeq = 1;
    }

    public void roll() {// TODO actually
        mAnimationSeq = 2;
    }

    private float ind = 0;
    public void update(float currentTime, float deltaTime) {
        //float a = (float)(Math.sin(currentTime*2)+1)/2;
        //mObject.animate(a, mAnimationSeq);

        {
            ind += 0.018f;
            if (ind > 1f) {
                ind -= 1f;
            }
        }
        mObject.animate(ind, 1);
    }

    private static Object3D load3DSModel(InputStream in, float scale) {
        Loader.setVertexOptimization(false);
        Object3D[] model = Loader.load3DS(in, scale);

        Object3D o3d = new Object3D(0);

        Object3D temp = null;

        for (int i = 0; i < model.length; i++) {
            temp = model[i];
            temp.setCenter(SimpleVector.ORIGIN);
            temp.rotateX((float)( -.5*Math.PI));
            temp.rotateMesh();
            temp.setRotationMatrix(new Matrix());
            o3d = Object3D.mergeObjects(o3d, temp);
            o3d.build();
        }
        return o3d;
    }

    /*private AnimatedGroup mAnimGroup;

    private static final boolean MESH_ANIM_ALLOWED = false;

    private Stickman() {}

    public static Stickman createStickman(Context ctx, World world) {
        Resources res = ctx.getResources();

        Stickman man = new Stickman();

        try {
            man.mAnimGroup = BonesIO.loadGroup(res.openRawResource(R.raw.stickman));
            if (MESH_ANIM_ALLOWED) {
                man.createMeshKeyFrames();
            }

        } catch (Exception e) {
            Logger.log(e);
        }

        man.mAnimGroup.setSkeletonPose(new SkeletonPose(man.mAnimGroup.get(0).getSkeleton()));
        man.mAnimGroup.getRoot().scale(0.3f);

        man.mAnimGroup.getRoot().rotateY((float)-Math.PI/2);
        man.mAnimGroup.getRoot().rotateZ((float)Math.PI);
        man.mAnimGroup.getRoot().translate(-2, 2.5f, 0);
        man.mAnimGroup.addToWorld(world);

        return man;
    }

    private void createMeshKeyFrames() {
        Config.maxAnimationSubSequences = mAnimGroup.getSkinClipSequence().getSize() + 1; // +1 for whole sequence

        int keyframeCount = 0;
        final float deltaTime = 0.2f; // max time between frames

        for (SkinClip clip : mAnimGroup.getSkinClipSequence()) {
            float clipTime = clip.getTime();
            int frames = (int) Math.ceil(clipTime / deltaTime) + 1;
            keyframeCount += frames;
        }

        Animation[] animations = new Animation[mAnimGroup.getSize()];
        for (int i = 0; i < mAnimGroup.getSize(); i++) {
            animations[i] = new Animation(keyframeCount);
            animations[i].setClampingMode(Animation.USE_CLAMPING);
        }
        //System.out.println("------------ keyframeCount: " + keyframeCount + ", mesh size: " + mAnimGroup.getSize());
        int count = 0;

        int sequence = 0;
        for (SkinClip clip : mAnimGroup.getSkinClipSequence()) {
            float clipTime = clip.getTime();
            int frames = (int) Math.ceil(clipTime / deltaTime) + 1;
            float dIndex = 1f / (frames - 1);

            for (int i = 0; i < mAnimGroup.getSize(); i++) {
                animations[i].createSubSequence(clip.getName());
            }
            //System.out.println(sequence + ": " + clip.getName() + ", frames: " + frames);
            for (int i = 0; i < frames; i++) {
                mAnimGroup.animateSkin(dIndex * i, sequence + 1);

                for (int j = 0; j < mAnimGroup.getSize(); j++) {
                    Mesh keyframe = mAnimGroup.get(j).getMesh().cloneMesh(true);
                    keyframe.strip();
                    animations[j].addKeyFrame(keyframe);
                    count++;
                    //System.out.println("added " + (i + 1) + " of " + sequence + " to " + j + " total: " + count);
                }
            }
            sequence++;
        }
        for (int i = 0; i < mAnimGroup.getSize(); i++) {
            mAnimGroup.get(i).setAnimationSequence(animations[i]);
        }
        mAnimGroup.get(0).getSkeletonPose().setToBindPose();
        mAnimGroup.get(0).getSkeletonPose().updateTransforms();
        mAnimGroup.applySkeletonPose();
        mAnimGroup.applyAnimation();

        Logger.log("created mesh keyframes, " + keyframeCount + "x" + mAnimGroup.getSize());
    }

    public void update(float currentTime, float deltaTime) {
        int animation = 0;
        boolean useMeshAnim = false;

        if (mAnimGroup.getSkinClipSequence().getSize() >= animation) {
            currentTime += deltaTime;
            float clipTime = mAnimGroup.getSkinClipSequence().getClip(animation).getTime();
            if (currentTime > clipTime) {
                currentTime = 0;
            }
            float index = currentTime / clipTime;
            if (useMeshAnim) {
                for (Animated3D a : mAnimGroup)
                    a.animate(index, animation);
            } else {
                mAnimGroup.animateSkin(index, animation);
                if (!mAnimGroup.isAutoApplyAnimation())
                    mAnimGroup.applyAnimation();
            }

        }
    }*/
}
