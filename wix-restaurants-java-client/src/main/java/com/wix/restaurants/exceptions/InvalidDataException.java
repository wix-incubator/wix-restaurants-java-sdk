package com.wix.restaurants.exceptions;

public class InvalidDataException extends RestaurantsException {
    public InvalidDataException(String message) {
        super(message);
    }

    public InvalidDataException(String message, Throwable cause) {
        super(message, cause);
    }
}
