package com.linerunner3d.core;

import android.content.Context;

import com.threed.jpct.Object3D;
import com.threed.jpct.Primitives;
import com.threed.jpct.SimpleVector;
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
    private float mDistance = 2;

    public static Obstacles create(Context ctx, World world) {
        Obstacles o = new Obstacles();

        for (int i = 0; i < MAX_ELEMENTS; i++) {

            Object3D box = Primitives.getBox(0.2f, 1.f);
            o.mBoxes.add(box);
            world.addObject(box);
            o.place(box);
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

    private float mSpeed = 0.1f;
    public void update(float currentTime, float deltaTime) {
        mDistance = (1.f - difficulty) * 5.f + 0.5f;

        for (int i = 0; i < mBoxes.size(); i++) {
            Object3D b = mBoxes.get(i);
            b.translate(-mSpeed*deltaTime, 0, 0);
            SimpleVector v = b.getOrigin();
            if (v.x < -2.f) {
                place(b);
            }
        }
    }


    private void place(Object3D ob) {

        int i = mBoxes.indexOf(ob);
        ob.setOrigin(SimpleVector.create(i*mDistance + 10, 1, 0 ));

    }
}
