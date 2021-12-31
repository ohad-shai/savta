package com.ohadshai.savta.utils;

import android.view.View;

import androidx.core.view.ViewCompat;
import androidx.navigation.fragment.FragmentNavigator;

import java.util.HashMap;
import java.util.Map;

/**
 * Represents a util for shared elements.
 */
public class SharedElementsUtils {

    /**
     * Builds a shared elements map and assign it into a new FragmentNavigator.Extras object.
     *
     * @param views The list of views to set as shared elements.
     * @return Returns the new FragmentNavigator.Extras object created.
     */
    public static FragmentNavigator.Extras build(View... views) {
        Map<View, String> sharedElements = new HashMap<>();
        for (View view : views) {
            sharedElements.put(view, ViewCompat.getTransitionName(view));
        }
        return new FragmentNavigator.Extras.Builder().addSharedElements(sharedElements).build();
    }

}
