package com.wix.restaurants.exceptions;

public class CommunicationException extends RestaurantsException {
    public CommunicationException(String message) {
        super(message);
    }

    public CommunicationException(String message, Throwable cause) {
        super(message, cause);
    }
}
