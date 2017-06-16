package me.nereo.multi_image_selector.camera;

import android.content.Context;
import android.view.OrientationEventListener;

/**
 * Created by Administrator on 2015/7/31.
 */
public class MyOrientationDetector extends OrientationEventListener {
    int Orientation;

    public MyOrientationDetector(Context context) {
        super(context);
    }

    @Override
    public void onOrientationChanged(int orientation) {
        this.Orientation = orientation;
    }

    public int getOrientation() {
        return Orientation;
    }
}