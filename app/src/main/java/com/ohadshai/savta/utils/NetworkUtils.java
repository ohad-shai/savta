package com.ohadshai.savta.utils;

import android.app.Activity;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.view.View;

import com.google.android.material.snackbar.Snackbar;
import com.ohadshai.savta.R;

/**
 * Represents utilities for network stuff.
 */
public class NetworkUtils {

    /**
     * Checks if a network connection is available in the device or not.
     *
     * @return Returns true if a network connection is available, otherwise false.
     */
    public static boolean isNetworkAvailable() {
        Context context = ApplicationContext.getContext();
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return (activeNetworkInfo != null && activeNetworkInfo.isConnected());
    }

    /**
     * Checks if a network connection is not available in the device, then shows a snackbar with the message.
     *
     * @return Returns true if a network connection is available, otherwise false.
     * @apiNote NOTE: In order to use - the view must contain a coordinator layout somewhere in the root, and the view must be created.
     */
    public static boolean checkIfNoNetworkToShowSnackBar(Activity activity, View view) {
        boolean hasNetwork = NetworkUtils.isNetworkAvailable();
        if (!hasNetwork) {
            Snackbar snackbar = Snackbar.make(view, R.string.no_network_message, Snackbar.LENGTH_LONG);
            snackbar.setAction(R.string.settings, new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    IntentUtils.startNetworkSettings(activity);
                }
            });
            snackbar.setActionTextColor(activity.getResources().getColor(R.color.warning));
            snackbar.show();
        }
        return !hasNetwork;
    }

}
