package com.wix.restaurants.exceptions;

public class InternalException extends RestaurantsException {
    public InternalException(String message) {
        super(message);
    }

    public InternalException(String message, Throwable cause) {
        super(message, cause);
    }
}
