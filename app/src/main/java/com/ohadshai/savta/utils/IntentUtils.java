package com.ohadshai.savta.utils;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.provider.MediaStore;

/**
 * Represents utilities for intents.
 */
public class IntentUtils {

    /**
     * Represents the request codes for capturing the result of the intent.
     */
    public class RequestCodes {
        public static final int GALLERY = 42622;
        public static final int CAMERA = 42623;
    }

    /**
     * Starts the Gallery activity, to let the user choose an image.
     *
     * @param activity The activity owner.
     */
    public static void startGallery(Activity activity) {
        Intent galleryIntent = new Intent(Intent.ACTION_PICK);
        galleryIntent.setType("image/*");
        activity.startActivityForResult(galleryIntent, RequestCodes.GALLERY);
    }

    /**
     * Starts the Camera activity, to let the user capture an image.
     *
     * @param activity The activity owner.
     */
    public static void startCamera(Activity activity) {
        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        activity.startActivityForResult(cameraIntent, RequestCodes.CAMERA);
    }

    /**
     * Starts the web browser activity on the specified URL.
     *
     * @param activity The activity owner.
     * @param url      The URL to open the web browser on.
     */
    public static void startWebBrowser(Activity activity, String url) {
        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
        activity.startActivity(browserIntent);
    }

}
