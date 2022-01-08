package com.ohadshai.savta.data.utils;

/**
 * Represents a listener for image upload complete.
 */
public interface OnImageUploadCompleteListener {
    void onSuccess(String imageUrl);

    void onFailure();
}
