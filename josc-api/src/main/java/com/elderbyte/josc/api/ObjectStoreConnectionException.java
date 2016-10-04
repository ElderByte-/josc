package com.elderbyte.josc.api;

/**
 * Thrown when there was a problem with the connection to a object store.
 */
public class ObjectStoreConnectionException extends RuntimeException {

    public ObjectStoreConnectionException(String message){
        super(message);
    }

    public ObjectStoreConnectionException(String message, Throwable cause){
        super(message, cause);
    }

}
