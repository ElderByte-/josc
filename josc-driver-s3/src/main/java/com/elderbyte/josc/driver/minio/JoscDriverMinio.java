package com.elderbyte.josc.driver.minio;


import com.elderbyte.josc.api.JoscConnectionProperties;
import com.elderbyte.josc.api.JoscDriver;
import com.elderbyte.josc.api.ObjectStoreClient;
import com.elderbyte.josc.api.ObjectStoreConnectionException;
import io.minio.MinioClient;


public class JoscDriverMinio implements JoscDriver {

    @Override
    public ObjectStoreClient openConnection(String host, JoscConnectionProperties properties) throws ObjectStoreConnectionException {

        try {
            MinioClient minioClient = new MinioClient(
                host,
                properties.getRequiredProperty("user"),
                properties.getRequiredProperty("pass"));

            return new MinioObjectStoreClient(minioClient);
        }catch (Exception e){
            throw new ObjectStoreConnectionException("Failed to open connection to minio/S3 object store!", e);
        }
    }

    @Override
    public boolean supports(String protocol, String host, JoscConnectionProperties properties) {
        return "minio".equalsIgnoreCase(protocol);
    }
}
