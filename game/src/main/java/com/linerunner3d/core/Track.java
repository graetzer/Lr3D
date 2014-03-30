package com.linerunner3d.core;

import android.content.Context;

import com.threed.jpct.Object3D;
import com.threed.jpct.Primitives;
import com.threed.jpct.SimpleVector;
import com.threed.jpct.World;
import com.threed.jpct.util.ExtendedPrimitives;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by simon on 29.03.14.
 */
public class Track {

    List<Object3D> mTrackParts = new ArrayList<Object3D>();

    private Track() {}

    public static Track createTrack(Context ctx, World world) {
        Track t = new Track();

        for (int i = 0; i < 1; i++) {
            Object3D obj = ExtendedPrimitives.createBox(SimpleVector.create(30.f, 0.5f, 3.f));
            t.mTrackParts.add(obj);
            world.addObject(obj);
        }

        return t;
    }

    public void update(float currentTime, float deltaTime) {

        float x = -(currentTime % 10.f);

        for (int i = 0; i < mTrackParts.size(); i++) {
            Object3D obj = mTrackParts.get(i);

            obj.setOrigin(SimpleVector.create(x + i*5, 0, 0));
        }
    }
}
