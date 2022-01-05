package com.ohadshai.savta.data.utils;

import com.ohadshai.savta.entities.User;

/**
 * Represents a listener for user's email update complete.
 */
public interface OnEmailUpdateCompleteListener {
    void onSuccess(User user);

    void onCollision();

    void onInvalidCredentials();

    void onFailure(Exception exception);
}
