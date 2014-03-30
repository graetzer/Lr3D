package com.linerunner3d.core;

import android.content.Context;
import android.content.res.Resources;

import com.linerunner3d.R;
import com.threed.jpct.Animation;
import com.threed.jpct.Config;
import com.threed.jpct.Logger;
import com.threed.jpct.Mesh;

import java.util.LinkedList;
import java.util.List;

import raft.jpct.bones.AnimatedGroup;
import raft.jpct.bones.BonesIO;
import raft.jpct.bones.SkeletonPose;
import raft.jpct.bones.SkinClip;

/**
 * Created by simon on 29.03.14.
 */
public class Stickman {
    private AnimatedGroup mAnimGroup;
    private final List<AnimatedGroup> ninjas = new LinkedList<AnimatedGroup>();

    private static final boolean MESH_ANIM_ALLOWED = false;

    /** ninja placement locations. values are in angles */
    private static final float[] LOCATIONS = new float[] {0, 180, 90, 270, 45, 225, 315, 135};


    private Stickman() {}

    public static Stickman createStickman(Context ctx) {
        Resources res = ctx.getResources();

        Stickman man = new Stickman();

        try {
            man.mAnimGroup = BonesIO.loadGroup(res.openRawResource(R.raw.stickman));
            if (MESH_ANIM_ALLOWED) man.createMeshKeyFrames();

            man.addNinja();
        } catch (Exception e) {
            Logger.log(e);
        }
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
}
