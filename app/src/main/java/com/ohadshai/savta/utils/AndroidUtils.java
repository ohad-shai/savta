package com.ohadshai.savta.utils;

import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

/**
 * Represents utilities for android components.
 */
public class AndroidUtils {

    /**
     * Hides the android keyboard from the screen.
     *
     * @param activity The activity owner.
     */
    public static void hideKeyboard(Activity activity) {
        // Checks if no view has focus:
        View view = activity.getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

}
