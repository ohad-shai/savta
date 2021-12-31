package com.ohadshai.savta.data.utils;

import com.ohadshai.savta.entities.User;

/**
 * Represents a listener for user login complete.
 */
public interface OnLoginCompleteListener {
    void onSuccess(User user);

    void onInvalidCredentials();

    void onFailure(Exception exception);
}
