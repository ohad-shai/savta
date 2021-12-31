package com.ohadshai.savta.data.utils;

import com.ohadshai.savta.entities.User;

/**
 * Represents a listener for user register complete.
 */
public interface OnRegisterCompleteListener {
    void onSuccess(User user);

    void onCollision();

    void onFailure(Exception exception);
}
