package com.linerunner3d.core;

import android.content.Context;

import com.threed.jpct.Object3D;
import com.threed.jpct.Primitives;
import com.threed.jpct.World;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by simon on 30.03.14.
 */
public class Obstacles {
    private static final int MAX_ELEMENTS = 20;
    private List<Object3D> mBoxes = new ArrayList<Object3D>();
    private float difficulty = 0;

    public static Obstacles create(Context ctx, World world) {
        Obstacles o = new Obstacles();

        for (int i = 0; i < MAX_ELEMENTS; i++) {

            Object3D box = Primitives.getBox(1.5f, 1.f);
            o.mBoxes.add(box);
            world.addObject(box);

        }
        return o;
    }

    /**
     * Set a difficulty level
     * @param x Parameter between 0.f and 1.f
     */
    public void setDifficulty(float x) {
        difficulty = (float)Math.max(0, Math.min(1.0, x));
    }

    public void update(float currentTime, float deltaTime) {
        float distance = (1.f - difficulty) * 5.f + 1;


    }
}
