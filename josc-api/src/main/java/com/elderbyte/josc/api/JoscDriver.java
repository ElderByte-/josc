package com.elderbyte.josc.api;


/**
 * Represents a josc driver which is able to build and open an object store client
 * for a certain protocol / provider.
 */
public interface JoscDriver {

    /**
     * Attempts to open a new connection to the given object store.
     * @param host The object store host (driver specific value)
     * @param properties Additional connection properties
     * @return Returns an open object store client ready to use.
     * @throws ObjectStoreConnectionException Thrown when a connection could not be etablished.
     */
    ObjectStoreClient openConnection(String host, JoscConnectionProperties properties) throws ObjectStoreConnectionException;

    /**
     * Does this driver implementation support the given protocol / host?
     * True does not indicate that openConnection is guaranteed to succeed.
     *
     * @param protocol The josc protocol
     * @param host The host part
     * @param properties Additional properties used to init the connection
     * @return Returns true if this driver supports an ObjectStoreClient with the given settings.
     */
    boolean supports(String protocol, String host, JoscConnectionProperties properties);

}
