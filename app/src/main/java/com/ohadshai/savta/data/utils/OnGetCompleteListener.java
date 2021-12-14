package com.ohadshai.savta.data.utils;

/**
 * Represents a generic listener for database "get" interaction complete.
 *
 * @param <T> The type of the entity.
 */
public interface OnGetCompleteListener<T> {
    void onSuccess(T object);

    void onFailure();
}
