package com.elderbyte.josc.api;


/**
 * Thrown when there was a problem with the object store connection / communication.
 */
public class ObjectStoreClientException extends RuntimeException {

    public ObjectStoreClientException(String message){
        super(message);
    }

    public ObjectStoreClientException(String message, Throwable cause){
        super(message, cause);
    }

}
