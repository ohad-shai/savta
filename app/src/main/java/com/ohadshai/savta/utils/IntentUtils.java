package com.ohadshai.savta.utils;

import android.content.Intent;
import android.net.Uri;
import android.provider.MediaStore;
import android.provider.Settings;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;

/**
 * Represents utilities for intents.
 */
public class IntentUtils {

    /**
     * Gets the Gallery intent, to let the user choose an image.
     */
    public static Intent gallery() {
        Intent galleryIntent = new Intent(Intent.ACTION_PICK);
        galleryIntent.setType("image/*");
        return galleryIntent;
    }

    /**
     * Gets the Camera intent, to let the user capture an image.
     */
    public static Intent camera() {
        return new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
    }

    /**
     * Starts the web browser activity on the specified URL.
     *
     * @param activity The activity owner.
     * @param url      The URL to open the web browser on.
     */
    public static void startWebBrowser(@NonNull FragmentActivity activity, String url) {
        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
        activity.startActivity(browserIntent);
    }

    /**
     * Opens the network settings activity.
     *
     * @param activity The activity owner.
     */
    public static void startNetworkSettings(@NonNull FragmentActivity activity) {
        Intent networkIntent = new Intent(Settings.ACTION_WIRELESS_SETTINGS);
        activity.startActivity(networkIntent);
    }

}
