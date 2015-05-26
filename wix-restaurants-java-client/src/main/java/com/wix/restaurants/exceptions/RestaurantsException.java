package com.wix.restaurants.exceptions;

public class RestaurantsException extends RuntimeException {
    public RestaurantsException(String message) {
        super(message);
    }

    public RestaurantsException(String message, Throwable cause) {
        super(message, cause);
    }
}
