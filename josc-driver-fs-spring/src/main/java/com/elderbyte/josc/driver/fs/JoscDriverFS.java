package com.elderbyte.josc.driver.fs;

import com.elderbyte.josc.api.ObjectStoreClient;
import com.elderbyte.josc.api.JoscConnectionProperties;
import com.elderbyte.josc.api.ObjectStoreConnectionException;
import com.elderbyte.josc.api.JoscDriver;
import com.elderbyte.josc.spring.support.LocalHostUrlProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.nio.file.Path;
import java.nio.file.Paths;


public class JoscDriverFS implements JoscDriver {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private final LocalHostUrlProvider fallbackHostProvider;

    public JoscDriverFS(LocalHostUrlProvider fallbackHostProvider){
        this.fallbackHostProvider = fallbackHostProvider;
    }


    @Override
    public ObjectStoreClient openConnection(String path, JoscConnectionProperties properties) throws ObjectStoreConnectionException {
        try {
            Path basePath = Paths.get(path);

            String hosturl = properties.getProperty("fs.hostUrl")
                    .orElseGet(() -> getFallbackHostUrl());

            return new FileSystemObjectStoreClient(basePath, hosturl);
        }catch (Exception e){
            throw new ObjectStoreConnectionException("Failed to open connection to local FS object store!", e);
        }
    }

    @Override
    public boolean supports(String protocol, String host, JoscConnectionProperties properties) {
        return "fs".equalsIgnoreCase(protocol);
    }

    private String getFallbackHostUrl(){
        try {
            String fallback = fallbackHostProvider.getLocalHostUrl();
            logger.warn("Missing property 'fs.hostUrl'. Using fallback host url for local blobstore emulation: " + fallback);
            return fallback;
        }catch (Exception e){
            throw new ObjectStoreConnectionException("Failed to get fallback host for local object-store emulation.", e);
        }
    }
}