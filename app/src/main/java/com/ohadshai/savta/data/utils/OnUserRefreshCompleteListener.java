package com.ohadshai.savta.data.utils;

import com.ohadshai.savta.entities.User;

/**
 * Represents a listener for user authentication refresh complete.
 */
public interface OnUserRefreshCompleteListener {
    void onSuccess(User user);

    void onUnauthorized();

    void onFailure(Exception exception);
}
