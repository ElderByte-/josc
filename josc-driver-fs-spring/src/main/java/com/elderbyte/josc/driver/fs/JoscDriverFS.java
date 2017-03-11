package com.elderbyte.josc.driver.fs;

import com.elderbyte.josc.api.ObjectStoreClient;
import com.elderbyte.josc.api.JoscConnectionProperties;
import com.elderbyte.josc.api.ObjectStoreConnectionException;
import com.elderbyte.josc.api.JoscDriver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.nio.file.Path;
import java.nio.file.Paths;


public class JoscDriverFS implements JoscDriver {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Override
    public ObjectStoreClient openConnection(String host, JoscConnectionProperties properties) throws ObjectStoreConnectionException {
        try {
            String basePathStr = host;
            Path basePath = Paths.get(basePathStr);
            return new FileSystemObjectStoreClient(basePath, properties.getProperty("fs.hostUrl").orElseGet(() -> getFallbackHostUrl()));
        }catch (Exception e){
            throw new ObjectStoreConnectionException("Failed to open connection to local FS object store!", e);
        }
    }

    @Override
    public boolean supports(String protocol, String host, JoscConnectionProperties properties) {
        return "fs".equalsIgnoreCase(protocol);
    }

    private String getFallbackHostUrl(){
        String host;
        try {
            host = "http://"+ InetAddress.getLocalHost().getHostAddress() + ":80" ;
            return host;
        } catch (UnknownHostException e) {
            logger.error("Failed to get fall back ip address", e);
            host = "http://localhost:80";
        }
        logger.warn("Missing property 'fs.hostUrl'. Using fallback host url for local blobstore emulation: " + host);
        return host;
    }
}