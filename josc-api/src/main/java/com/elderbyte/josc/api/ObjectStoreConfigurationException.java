package com.elderbyte.josc.api;

/**
 * Thrown when there was an issue configuring josc
 */
public class ObjectStoreConfigurationException extends RuntimeException {

    public ObjectStoreConfigurationException(String message, Throwable cause){
        super(message, cause);
    }

    public ObjectStoreConfigurationException(String message){
        super(message);
    }

}
