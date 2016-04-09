package com.uja.telematica.BO;

import android.content.Context;

/**
 * Created by Alfonso Troyano on 05/08/2015.
 */
public class Miscellaneous {

    public static int GetDipsFromPixel(float pixels, Context context)
    {
        // Get the screen's density scale
        final float scale = context.getResources().getDisplayMetrics().density;
        // Convert the dps to pixels, based on density scale
        return (int) (pixels * scale + 0.5f);
    }
}
