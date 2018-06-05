package com.wix.restaurants.exceptions;

public class TemporarilyUnavailableException extends RestaurantsException {
    public TemporarilyUnavailableException(String message) {
        super(message);
    }

    public TemporarilyUnavailableException(String message, Throwable cause) {
        super(message, cause);
    }
}
