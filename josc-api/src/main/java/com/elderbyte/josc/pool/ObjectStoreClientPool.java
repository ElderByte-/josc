package com.elderbyte.josc.pool;

import com.elderbyte.josc.api.JoscConnectionProperties;
import com.elderbyte.josc.api.ObjectStoreClient;
import com.elderbyte.josc.api.ObjectStoreClientFactory;
import com.elderbyte.josc.api.ObjectStoreConnectionException;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.github.benmanes.caffeine.cache.LoadingCache;

import java.util.concurrent.TimeUnit;

public class ObjectStoreClientPool implements ObjectStoreClientFactory {

    private final ObjectStoreClientFactory clientFactory;
    private final LoadingCache<ClientKey, ObjectStoreClient> clientCache;

    public ObjectStoreClientPool(ObjectStoreClientFactory clientFactory){
        this.clientFactory = clientFactory;
        clientCache = Caffeine.newBuilder()
                        .maximumSize(50)
                        .expireAfterWrite(10, TimeUnit.MINUTES)
                        .build(k -> buildClient(k));
    }

    @Override
    public ObjectStoreClient buildClient(String connectionString, JoscConnectionProperties properties) throws ObjectStoreConnectionException {
        return clientCache.get(new ClientKey(connectionString, properties));
    }

    private ObjectStoreClient buildClient(ClientKey key){
        return clientFactory.buildClient(key.getConnectionString(), key.getProperties());
    }

    private static class ClientKey {
        private final String connectionString;
        private final JoscConnectionProperties properties;

        public ClientKey(String connectionString, JoscConnectionProperties properties){
            this.connectionString = connectionString;
            this.properties = properties;
        }

        public String getConnectionString() {
            return connectionString;
        }

        public JoscConnectionProperties getProperties() {
            return properties;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            ClientKey clientKey = (ClientKey) o;

            if (connectionString != null ? !connectionString.equals(clientKey.connectionString) : clientKey.connectionString != null)
                return false;
            return properties != null ? properties.equals(clientKey.properties) : clientKey.properties == null;

        }

        @Override
        public int hashCode() {
            int result = connectionString != null ? connectionString.hashCode() : 0;
            result = 31 * result + (properties != null ? properties.hashCode() : 0);
            return result;
        }
    }
}