package com.wix.restaurants.exceptions;

public class NoPermissionException extends RestaurantsException {
    public NoPermissionException(String message) {
        super(message);
    }

    public NoPermissionException(String message, Throwable cause) {
        super(message, cause);
    }
}
