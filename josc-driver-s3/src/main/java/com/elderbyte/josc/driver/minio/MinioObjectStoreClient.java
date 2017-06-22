package com.elderbyte.josc.driver.minio;

import com.elderbyte.josc.api.*;
import com.elderbyte.josc.core.Streams;
import io.minio.MinioClient;
import io.minio.ObjectStat;
import java.io.InputStream;
import java.nio.file.Path;
import java.util.stream.Stream;


public class MinioObjectStoreClient implements ObjectStoreClient {


    private final MinioClient minioClient;

    public MinioObjectStoreClient(MinioClient minioClient){
        this.minioClient = minioClient;
    }

    @Override
    public Stream<Bucket> listBuckets() {
        try {
            return minioClient.listBuckets().stream()
                .map(b -> MinioBlobObjectBuilder.build(b));
        }catch (Exception e){
            throw new ObjectStoreClientException("Failed to list buckets!", e);
        }
    }

    @Override
    public boolean bucketExists(String bucket) {
        try {
            return minioClient.bucketExists(bucket);
        }catch (Exception e){
            throw new ObjectStoreClientException("Failed to check if bucket "+bucket+" exists!", e);
        }
    }

    @Override
    public void removeBucket(String bucket) {
        try {
            minioClient.removeBucket(bucket);
        }catch (Exception e){
            throw new ObjectStoreClientException("Failed to remove bucket "+bucket+"!", e);
        }
    }

    @Override
    public void createBucket(String bucket) {
        try {
            minioClient.makeBucket(bucket);
        }catch (Exception e){
            throw new ObjectStoreClientException("Failed to create bucket "+bucket+"!", e);
        }
    }


    @Override
    public Stream<BlobObject> listBlobObjects(String bucket) {
        return listBlobObjects(bucket, null);
    }

    @Override
    public Stream<BlobObject> listBlobObjects(String bucket, String keyPrefix) {
       return listBlobObjects(bucket, keyPrefix, true);
    }

    @Override
    public Stream<BlobObject> listBlobObjects(String bucket, String keyPrefix, boolean recursive) {
        try {
            return Streams.stream(
                    minioClient.listObjects(
                            bucket,
                            keyPrefix,
                            recursive)
            )
                .map(res -> {
                    try {
                        return res.get();
                    }catch (Exception e){
                        throw new ObjectStoreClientException("Failed to get details of object: " + bucket +" / " + keyPrefix + " recourse: " + recursive, e);
                    }
                }).map(obj -> MinioBlobObjectBuilder.build(obj));

        }catch (Exception e){
            throw new ObjectStoreClientException("Failed to list bucket: "+bucket+" and  prefix: "+keyPrefix+ "!", e);
        }
    }

    @Override
    public InputStream getBlobObject(String bucket, String key) {
        try {
            return minioClient.getObject(bucket, key);
        }catch (Exception e){
            throw new ObjectStoreClientException("Failed to get InputStream of object: " + bucket +" / " + key, e);
        }
    }

    @Override
    public InputStream getBlobObject(String bucket, String key, long offset) {
        try {
            return minioClient.getObject(bucket, key, offset);
        }catch (Exception e){
            throw new ObjectStoreClientException("Failed to get InputStream of object: " + bucket +" / " + key, e);
        }
    }

    @Override
    public BlobObject getBlobObjectInfo(String bucket, String key) {
        try {
            ObjectStat stat = minioClient.statObject(bucket, key);
            return MinioBlobObjectBuilder.build(stat);
        }catch (Exception e){
            throw new ObjectStoreClientException("Failed to get Object-Info of object: " + bucket +" / " + key, e);
        }
    }

    @Override
    public void putBlobObject(String bucket, String key, Path file){
        try {
            minioClient.putObject(bucket, key, file.toString());
        }catch (Exception e){
            throw new ObjectStoreClientException("Failed to upload blob object: "  + bucket +" / " + key + " from file " + file, e);
        }
    }

    @Override
    public void putBlobObject(String bucket, String key, InputStream objectStream, long length, String mimeType){
        try {
            minioClient.putObject(bucket, key, objectStream, length, mimeType);
        }catch (Exception e){
            throw new ObjectStoreClientException("Failed to upload blob object.", e);
        }
    }

    @Override
    public void deleteBlobObject(String bucket, String key) {
        try {
            minioClient.removeObject(bucket, key);
        }catch (Exception e){
            throw new ObjectStoreClientException("Failed to delete object " + bucket + " : " + key, e);
        }
    }

    @Override
    public String getTempGETUrl(String bucket, String key) {
        try {
            return minioClient.presignedGetObject(bucket, key);
        }catch (Exception e){
            throw new ObjectStoreClientException("Failed to create presignedGetObject " + bucket + " : " + key, e);
        }
    }

    @Override
    public String getTempPUTUrl(String bucket, String key) {
        try {
            return minioClient.presignedPutObject(bucket, key);
        }catch (Exception e){
            throw new ObjectStoreClientException("Failed to create presignedGetObject " + bucket + " : " + key, e);
        }
    }

    public void copyBlobObject(String sourceBucket, String sourceKey, String destinationBucket, String destinationKey){
        try {
            minioClient.copyObject(sourceBucket, sourceKey, destinationBucket, destinationKey);
        }catch (Exception e){
            throw new ObjectStoreClientException("Failed to copy " + sourceBucket + " / " + sourceKey + " to " + destinationBucket + " / " + destinationKey, e);
        }
    }


    public String getPublicUrl(String bucket, String key){
        try {
            return minioClient.getObjectUrl(bucket, key);
        }catch (Exception e){
            throw new ObjectStoreClientException("Failed to get public object url " + bucket + " : " + key, e);
        }
    }
}
