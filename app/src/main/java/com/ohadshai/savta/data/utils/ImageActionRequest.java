package com.ohadshai.savta.data.utils;

/**
 * Represents an enumeration of actions that can be requested to perform on an image.
 */
public enum ImageActionRequest {
    /**
     * Action does nothing on the image.
     */
    NONE,

    /**
     * Action creates a new image.
     */
    CREATE,

    /**
     * Action deletes current image and creates a new image.
     */
    REPLACE,

    /**
     * Action deletes current image.
     */
    DELETE
}
