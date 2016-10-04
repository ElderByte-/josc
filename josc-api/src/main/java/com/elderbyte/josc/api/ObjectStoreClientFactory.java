package com.elderbyte.josc.api;

/**
 * Provides the ability to build and open a blob store client.
 */
public interface ObjectStoreClientFactory {

    /**
     * Builds a client and opens a conneciton to the specified blobstore.
     * @param connectionString The blobstore conneciton-string. Must start with josc:
     * @param properties Additional properties to use.
     * @return Returns a blob store client ready to use.
     * @throws ObjectStoreConnectionException If the client could not open a connection
     */
    ObjectStoreClient buildClient(String connectionString, JoscConnectionProperties properties) throws ObjectStoreConnectionException;

}
