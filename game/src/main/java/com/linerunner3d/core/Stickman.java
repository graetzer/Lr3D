package com.linerunner3d.core;

import android.content.Context;
import android.content.res.Resources;

import com.linerunner3d.R;
import com.threed.jpct.Animation;
import com.threed.jpct.Config;
import com.threed.jpct.Logger;
import com.threed.jpct.Mesh;
import com.threed.jpct.World;

import java.util.LinkedList;
import java.util.List;

import raft.jpct.bones.Animated3D;
import raft.jpct.bones.AnimatedGroup;
import raft.jpct.bones.BonesIO;
import raft.jpct.bones.SkeletonPose;
import raft.jpct.bones.SkinClip;

/**
 * Created by simon on 29.03.14.
 */
public class Stickman {
    private AnimatedGroup mAnimGroup;

    private static final boolean MESH_ANIM_ALLOWED = false;

    /** ninja placement locations. values are in angles */
    private static final float[] LOCATIONS = new float[] {0, 180, 90, 270, 45, 225, 315, 135};


    private Stickman() {}

    public static Stickman createStickman(Context ctx, World world) {
        Resources res = ctx.getResources();

        Stickman man = new Stickman();

        try {
            man.mAnimGroup = BonesIO.loadGroup(res.openRawResource(R.raw.stickman));
            if (MESH_ANIM_ALLOWED) man.createMeshKeyFrames();

            //man.addNinja();
        } catch (Exception e) {
            Logger.log(e);
        }

        float radius = 3, angle = 10;
        man.mAnimGroup.setSkeletonPose(new SkeletonPose(man.mAnimGroup.get(0).getSkeleton()));
        man.mAnimGroup.getRoot().translate((float)(Math.cos(angle) * radius), 0, (float)(Math.sin(angle) * radius));
        man.mAnimGroup.addToWorld(world);

        return man;
    }

    private void addNinja() {
      /*  if (ninjas.size() == LOCATIONS.length)
            return;

        AnimatedGroup ninja = mAnimGroup.clone(AnimatedGroup.MESH_DONT_REUSE);
        float[] bb = renderer.calcBoundingBox();
        float radius = (bb[3] - bb[2]) * 0.5f; // half of height
        double angle = Math.toRadians(LOCATIONS[ninjas.size()]);

        ninja.setSkeletonPose(new SkeletonPose(ninja.get(0).getSkeleton()));
        ninja.getRoot().translate((float)(Math.cos(angle) * radius), 0, (float)(Math.sin(angle) * radius));

        ninja.addToWorld(world);
        ninjas.add(ninja);
        Logger.log("added new ninja: " + ninjas.size());*/
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

    private static final int GRANULARITY = 25;
    private long frameTime = System.currentTimeMillis();
    private long aggregatedTime = 0;
    private float animateSeconds  = 0f;
    private float speed = 1f;

    public void updateAnimations() {

        long now = System.currentTimeMillis();
        aggregatedTime += (now - frameTime);
        frameTime = now;

        while (aggregatedTime > GRANULARITY) {
            aggregatedTime -= GRANULARITY;
            animateSeconds += GRANULARITY * 0.001f * speed;
            //cameraController.placeCamera();
        }

        int animation = 1;
        boolean useMeshAnim = false;

        if (animation > 0 && mAnimGroup.getSkinClipSequence().getSize() >= animation) {
            float clipTime = mAnimGroup.getSkinClipSequence().getClip(animation-1).getTime();
            if (animateSeconds > clipTime) {
                animateSeconds = 0;
            }
            float index = animateSeconds / clipTime;
            if (useMeshAnim) {
                for (Animated3D a : mAnimGroup)
                    a.animate(index, animation);
                /*for (AnimatedGroup group : ninjas) {
                    for (Animated3D a : group)
                        a.animate(index, animation);
                }*/
            } else {
                mAnimGroup.animateSkin(index, animation);
                if (!mAnimGroup.isAutoApplyAnimation())
                    mAnimGroup.applyAnimation();
                /*for (AnimatedGroup group : ninjas) {
                    group.animateSkin(index, animation);
//							if (!group.isAutoApplyAnimation())
//								group.applyAnimation();
                }*/
            }

        } else {
            animateSeconds = 0f;
        }
    }
}
