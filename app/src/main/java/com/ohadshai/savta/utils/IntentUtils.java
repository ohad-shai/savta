package com.ohadshai.savta.utils;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.provider.MediaStore;
import android.provider.Settings;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;

import com.ohadshai.savta.R;

/**
 * Represents utilities for intents.
 */
public class IntentUtils {

    /**
     * Gets the Gallery intent, to let the user choose an image.
     *
     * @return Returns the Gallery intent if found in the device, otherwise null.
     */
    public static Intent gallery() {
        Context context = ApplicationContext.getContext();
        Intent galleryIntent = new Intent(Intent.ACTION_PICK);
        // Ensures there's a Gallery activity:
        if (galleryIntent.resolveActivity(context.getPackageManager()) != null) {
            galleryIntent.setType("image/*");
            return galleryIntent;
        } else {
            return null;
        }
    }

    /**
     * Gets the Camera intent, to let the user capture an image.
     *
     * @return Returns the Camera intent if found in the device, otherwise null.
     */
    public static Intent camera() {
        Context context = ApplicationContext.getContext();
        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Ensures there's a Camera activity:
        if (cameraIntent.resolveActivity(context.getPackageManager()) != null) {
            return cameraIntent;
        } else {
            return null;
        }
    }

    /**
     * Starts the web browser activity on the specified URL.
     *
     * @param activity The activity owner.
     * @param url      The URL to open the web browser on.
     */
    public static void startWebBrowser(@NonNull FragmentActivity activity, String url) {
        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
        // Ensures there's a Web Browser activity:
        if (browserIntent.resolveActivity(activity.getPackageManager()) != null) {
            activity.startActivity(browserIntent);
        } else {
            Toast.makeText(activity, R.string.web_browser_intent_not_found, Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Opens the network settings activity.
     *
     * @param activity The activity owner.
     */
    public static void startNetworkSettings(@NonNull FragmentActivity activity) {
        Intent networkIntent = new Intent(Settings.ACTION_WIRELESS_SETTINGS);
        // Ensures there's a Network Settings activity:
        if (networkIntent.resolveActivity(activity.getPackageManager()) != null) {
            activity.startActivity(networkIntent);
        } else {
            Toast.makeText(activity, R.string.network_settings_intent_not_found, Toast.LENGTH_SHORT).show();
        }
    }

}
