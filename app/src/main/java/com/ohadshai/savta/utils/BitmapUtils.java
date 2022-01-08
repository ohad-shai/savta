package com.ohadshai.savta.utils;

import android.graphics.Bitmap;

/**
 * Represents utilities for working with bitmap.
 */
public class BitmapUtils {

    /**
     * Resizes the specified bitmap to the maximum width, and keeps the ratio.
     *
     * @param bitmap   The bitmap to resize.
     * @param maxWidth The maximum width to resize to.
     * @return Returns the new resized bitmap.
     */
    public static Bitmap resizeAndKeepRatio(Bitmap bitmap, int maxWidth) {
        if (bitmap.getWidth() > maxWidth) {
            float ratio = ((float) bitmap.getHeight() / bitmap.getWidth());
            return Bitmap.createScaledBitmap(bitmap, maxWidth, (int) (maxWidth * ratio), false);
        } else {
            return bitmap;
        }
    }

}
