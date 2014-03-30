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
    }
}
